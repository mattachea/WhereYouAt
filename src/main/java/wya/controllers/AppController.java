package wya.controllers;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import wya.repositories.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Objects;

public class AppController {
    final Connection connection = DriverManager.getConnection("jdbc:sqlite:wya.db");
    private final EventRepository eventRepository = new EventRepository(connection);
    //    private final FriendRepository friendRepository = new FriendRepository(connection);
    private final PersonRepository personRepository = new PersonRepository(connection);
    private final AccountRepository accountRepository = new AccountRepository(connection);
    private final EventRelationsRepository eventRelationsRepository = new EventRelationsRepository(connection);
    private final EventController eventController = new EventController(eventRepository);
    private final PersonController personController = new PersonController(personRepository);
    private final AccountController accountController = new AccountController(accountRepository);
    private final EventRelationsController eventRelationsController = new EventRelationsController(eventRelationsRepository);

    /**
     * Constructor for App Controller
     * @throws SQLException SQL statement failed to execute.
     */
    public AppController() throws SQLException {
    }

    /**
     * Creates an entry in the user and person tables. This uses Context.formParams to fill in the columns for each entry.
     * Example:
     * fullName:admin
     * lastSeen:2019-12-14T02:09:12+00:00↵
     * status:admin stuff
     * longitude:39.3289957
     * latitude:-76.6205235
     * username:admin
     * password:securepassword
     * email:admin@test.com
     * profilePicture:drive.com/profilepicture
     * privacy:neighborhood
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException Failed to execute the SQL Statement successfully.
     */
    public void register(Context ctx) throws SQLException {
        if (Objects.requireNonNull(ctx.formParam("username")).length() < 1) {
            System.out.println("No username entered");
            throw new ForbiddenResponse();
        }
        if (Objects.requireNonNull(ctx.formParam("password")).length() < 1) {
            System.out.println("No password entered");
        }
        if (accountController.duplicateUsername(ctx)) {
            // ctx.status set to 409 if duplicate username
            System.out.println("\"" + ctx.formParam("username") + "\": Username already taken.");
            ctx.status(409);
            return;
        }
        int person_id = personController.create(ctx);
        try {
            accountController.register(ctx, person_id);
        } catch (SQLException e) {
            // ctx.status set to 409 if duplicate username
            // repeated just in case
            ctx.status(409);
        }
    }

    /**
     * Update the account profile picture and email for the current user. This uses Context.formParams.
     * Example.
     * email:newemailadmin@test.com
     * profilePicture:drive.com/profilepicture
     * Context.status = 204 when successfully changed.
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException             Failed to execute the SQL Statement successfully.
     * @throws AccountNotFoundException The current user account entry cannot be found in user table.
     */
    public void updateAccount(Context ctx) throws SQLException, AccountNotFoundException {
        accountController.updateDetails(ctx);
        ctx.status(204);
    }

    /**
     * Update the account password for the current user. This uses Context.formParams.
     * Example:
     * oldpassword:securedpassword
     * newpassword:newpassword
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException             Failed to execute the SQL Statement successfully.
     * @throws AccountNotFoundException The current user account entry cannot be found in user table.
     */
    public void changePassword(Context ctx) throws SQLException, AccountNotFoundException {
        accountController.changePass(ctx);
        ctx.status(200);
    }

    /**
     * Update the profile of the current user. This uses Context.formParams:
     * Example:
     * 1) fullName:name
     * 2) lastSeen:timestamp
     * 3) live:true
     * 4) stats:some status
     * 5) longitude:24.513445
     * 6) latitude:24.511351
     * 7) availability:0
     * 8) privacy:neighborhood
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException            Failed to execute the SQL Statement successfully.
     * @throws PersonNotFoundException Person of the current user cannot be found in the person table.
     */
    public void updateProfile(Context ctx) throws SQLException, PersonNotFoundException {
        personController.updateDetails(ctx);
        ctx.status(204);
    }

    /**
     * Login handler that takes checks the username and password from Context.formParams:
     * Example:
     * 1) username:username
     * 2) password:password
     * This will set the current user to the Account object and current user's person_id upon successful completion and sets ctx.status = 200.
     * If the username is not found in the database and/or the password associated with the username is wrong, then throws 403 - Forbidden.
     *
     * @param ctx The Javalin Context Object.
     */
    public void login(Context ctx) {
        accountController.login(ctx);
        ctx.status(200);
    }

    /**
     * Gets all the events the current user is associated with.
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException Failed to execute the SQL Statement successfully.
     */
    public void viewEvents(Context ctx) throws SQLException {
        eventController.getAll(ctx);
        ctx.status(200);
    }

    /**
     * Creates an event and associates it with the user. This uses Context.formParams.
     * Example:
     * 1) name:event name
     * 2) description:some description
     * 3) place:some place name
     * 4) longitude:24.132511
     * 5) latitude:24.513243
     * 6) image:imageurl.com/image.png
     * 7) startTime:timestamp
     * 8) endTime:timestamp
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException Failed to execute the SQL Statement successfully.
     */
    public void createEvent(Context ctx) throws SQLException {
        eventController.create(ctx);
        ctx.status(201);
    }

    /**
     * Edits the details of a specific event according to a Context.pathParam with the fields from the Context.formParams.
     * 1) name:event name
     * 2) description:some description
     * 3) place:some place name
     * 4) longitude:24.132511
     * 5) latitude:24.513243
     * 6) image:imageurl.com/image.png
     * 7) startTime:timestamp
     * 8) endTime:timestamp
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException           Failed to execute the SQL Statement successfully.
     * @throws EventNotFoundException Event with the identifier not found in events table.
     */
    public void editEvent(Context ctx) throws SQLException, EventNotFoundException {
        eventController.updateDetails(ctx);
        ctx.status(204);
    }

    /**
     * Gets all the friends associated with the current user.
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException Failed to execute the SQL Statement successfully.
     */
    public void viewFriends(Context ctx) throws SQLException, PersonNotFoundException {
//        personController.getAllFriends(ctx);  // If we ever implement adding and removing friends
        personController.getOthers(ctx);           // For now everyone is your friend
        ctx.status(200);
    }

    /**
     * Handles adding and removing friends from the current user.
     *
     * @param ctx The Javalin Context Object.
     */
    public void editFriends(Context ctx) {    // If we ever implement adding and removing friends
//        //TODO
    }

    /**
     * Gets the account information of the current user in the user table.
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException             Failed to execute the SQL Statement successfully.
     * @throws AccountNotFoundException The current user account entry cannot be found in user table.
     */
    public void getAccount(Context ctx) throws SQLException, AccountNotFoundException {
        accountController.getCurrentUser(ctx);
        ctx.status(200);
    }

    /**
     * Gets the person information of the current user in the person table.
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException            Failed to execute the SQL Statement successfully.
     * @throws PersonNotFoundException Person of the current user cannot be found in the person table.
     */
    public void getProfile(Context ctx) throws SQLException, PersonNotFoundException {
        personController.getCurrentPerson(ctx);
        ctx.status(200);
    }

    /**
     * @param ctx The Javalin Context Object.
     * @throws SQLException                    Failed to execute the SQL Statement successfully.
     * @throws EventRelationsNotFoundException EventRelations identifier not found in table.
     */
    public void getPersonIDForEvent(Context ctx) throws SQLException, EventRelationsNotFoundException {
        eventRelationsController.getPersonIDForEvent(ctx);
        ctx.status(200);
    }

    /**
     * @param ctx The Javalin Context Object.
     * @throws SQLException                    Failed to execute the SQL Statement successfully.
     * @throws EventRelationsNotFoundException EventRelations identifier not found in table.
     */
    public void getEventIDsForPerson(Context ctx) throws SQLException, EventRelationsNotFoundException {
        eventRelationsController.getEventIDsForPerson(ctx);
        ctx.status(200);
    }

    /**
     * @param ctx The Javalin Context Object.
     * @throws SQLException Failed to execute the SQL Statement successfully.
     */
    public void removeRelation(Context ctx) throws SQLException {
        eventRelationsController.removeRelation(ctx);
        ctx.status(204);
    }

    /**
     * @param ctx The Javalin Context Object.
     * @throws SQLException Failed to execute the SQL Statement successfully.
     */
    public void createRelation(Context ctx) throws SQLException {
        eventRelationsController.create(ctx);
        ctx.status(201);
    }

    /**
     * Deletes the specified event from the event table.
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException Failed to execute the SQL Statement successfully.
     */
    //TODO: Route through event...WHEN DELETE EVENT IS IMPLEMENTED
    public void deleteEvent(Context ctx) throws SQLException {
        eventRelationsController.deleteEvent(ctx);
    }

    /**
     * @param ctx The Javalin Context Object.
     * @throws SQLException Failed to execute the SQL Statement successfully.
     */
    //TODO: Route through person WHEN DELETE ACCOUNT IS IMPLEMENTED
    public void deletePerson(Context ctx) throws SQLException {
        eventRelationsController.deletePerson(ctx);
    }

    /**
     * Update the lastSeen timestamp for the current user to the time specified in the Context.formParams.
     * Example:
     * 1) lastseen:2019-12-15T18:04:39+00:00
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException            Failed to execute the SQL Statement successfully.
     * @throws PersonNotFoundException Person of the current user cannot be found in the person table.
     */
    public void updateTime(Context ctx) throws SQLException, PersonNotFoundException {
        personController.updateTime(ctx);
        ctx.status(204);
    }

    /**
     * Update the longitude and latitudes for the current user to the current location as specified in the Context.formparams.
     * Example:
     * 1) longitude:24.132451
     * 2) latitude:24.512341
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException            Failed to execute the SQL Statement successfully.
     * @throws PersonNotFoundException Person of the current user cannot be found in the person table.
     */
    public void updateLocation(Context ctx) throws SQLException, PersonNotFoundException {
        personController.updateLocation(ctx);
        ctx.status(204);
    }

    /**
     * Update the availability for the current user to a new availability code as specified in the Context.formParams.
     * Example:
     * 1) availability:2
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException            Failed to execute the SQL Statement successfully.
     * @throws PersonNotFoundException Person of the current user cannot be found in the person table.
     */
    public void updateAvailability(Context ctx) throws SQLException, PersonNotFoundException {
        personController.updateAvailability(ctx);
        ctx.status(204);
    }

    /**
     * Gets the privacy of the current user in a JSON object from the person table.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException            SQL statement failed to execute successfully.
     * @throws PersonNotFoundException Person does not exist.
     */
    public void getPrivacy(Context ctx) throws SQLException, PersonNotFoundException {
        personController.getPrivacy(ctx);
        ctx.status(200);
    }

    /**
     * Throw this a "personID" and "eventID".
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException SQL statement failed to execut successfully.
     */
    public void createForList(Context ctx) throws SQLException {
        eventRelationsController.createForList(ctx);
        ctx.status(200);
    }
}
