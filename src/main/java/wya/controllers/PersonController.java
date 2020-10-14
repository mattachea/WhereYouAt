package wya.controllers;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import wya.models.Person;
import wya.repositories.PersonNotFoundException;
import wya.repositories.PersonRepository;

import java.sql.SQLException;

public class PersonController {

    private final PersonRepository personRepository;

    // If we ever implement the friends repository.
//        private FriendRepository friendRepository;
//
//    PersonController(PersonRepository PersonRepository, FriendRepository friendRepository) {
//        this.personRepository = PersonRepository;
//        this.friendRepository = friendRepository;
//    }
    PersonController(PersonRepository PersonRepository) {
        this.personRepository = PersonRepository;
    }

    /**
     * Get all entries in the person table as a JSON object passed through ctx.
     *
     * @param ctx Javalin Context Object
     * @throws SQLException SQL statement failed to execute successfully.
     */
    void getAll(Context ctx) throws SQLException {
        ctx.json(personRepository.getAll());
    }

    /**
     * get a specific identifier entry in the person table as a JSON object passed through ctx.
     *
     * @param ctx Javalin Context Object
     * @throws SQLException            SQL statement failed to execute successfully.
     * @throws PersonNotFoundException Person does not exist in the person table.
     */
    void getOne(Context ctx) throws SQLException, PersonNotFoundException {
        var currperson = currentPerson(ctx);
        ctx.json(personRepository.getOne(currperson.getIdentifier()));
    }

    /**
     * Get all entries in the person table except that of the current user as a JSON object passed through ctx.
     *
     * @param ctx Javalin Context Object
     * @throws SQLException            SQL statement failed to execute successfully.
     * @throws PersonNotFoundException Person does not exist in the person table.
     */
    void getOthers(Context ctx) throws SQLException, PersonNotFoundException {
        ctx.json(personRepository.getAll(currentPerson(ctx).getIdentifier()));
    }
// For use if we implement adding and removing friends
//    void getAllFriends(Context ctx) throws SQLException, PersonNotFoundException {
//        var currperson = currentPerson(ctx);
//        ctx.json(friendRepository.getAll(currperson.getIdentifier()));

//    }

    /**
     * Get the entry of the current person from the person table as a JSON object passed through the ctx object.
     *
     * @param ctx Javalin Context Object
     * @throws SQLException            SQL statement failed to execute successfully.
     * @throws PersonNotFoundException Person does not exist in the person table.
     */
    void getCurrentPerson(Context ctx) throws SQLException, PersonNotFoundException {
        var currperson = currentPerson(ctx);
        ctx.json(personRepository.getOne(currperson.getIdentifier()));
    }

    /**
     * Create a person entry in the person table using the Context object.
     * Example:
     * fullName:Vincent Lee
     * lastSeen:3 hrs ago
     * live:false
     * status:failing the final
     * availability:2
     * longitude:39.328995
     * latitude:-76.620523
     * privacy:locality
     *
     * @param ctx Javalin Context Object
     * @return The identifier of the person entry created.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    int create(Context ctx) throws SQLException {
        Person person = new Person();
        createToDB(ctx, person);
        int person_id = personRepository.create(person);
        ctx.status(201);
        return person_id;
    }

    /**
     * Update the details of the current user's person entry in the person table using the ctx.formParams.
     * Example:
     * fullName:Vincent Lee
     * lastSeen:3 hrs ago
     * live:false
     * status:failing the final
     * availability:2
     * longitude:39.328995
     * latitude:-76.620523
     * privacy:locality
     *
     * @param ctx Javalin Context Object
     * @throws SQLException            SQL statement failed to execute successfully.
     * @throws PersonNotFoundException Person does not exist in the person table.
     */
    void updateDetails(Context ctx) throws SQLException, PersonNotFoundException {
        var person = currentPerson(ctx);
        createToDB(ctx, person);
        person.setLive(ctx.formParam("live", Boolean.class).get());
        person.setAvailability(ctx.formParam("availability", Integer.class).get());
        personRepository.updateDetails(person);
        ctx.status(204);
    }

    /**
     * Update the location column of the current user's entry in the person table.
     * Example:
     * longitude:39.328995
     * latitude:-76.620523
     *
     * @param ctx Javalin Context Object
     * @throws SQLException            SQL statement failed to execute successfully.
     * @throws PersonNotFoundException Person does not exist in the person table.
     */
    void updateLocation(Context ctx) throws SQLException, PersonNotFoundException {
        float longitude = ctx.formParam("longitude", float.class).get();
        float latitude = ctx.formParam("latitude", float.class).get();
        personRepository.updateLocation(longitude, latitude, currentPerson(ctx).getIdentifier());
        ctx.status(204);
    }

    /**
     * Update the timestamp in the lastSeen column of the current user's entry in the person table.
     *
     * @param ctx Javalin Context Object
     * @throws SQLException            SQL statement failed to execute successfully.
     * @throws PersonNotFoundException Person does not exist in the person table.
     */
    void updateTime(Context ctx) throws SQLException, PersonNotFoundException {
        String time = ctx.formParam("time");
        personRepository.updateTime(time, currentPerson(ctx).getIdentifier());
    }

    /**
     * Update the availability in the availability column of the current user's entry in the person table.
     *
     * @param ctx Javalin Context Object
     * @throws SQLException            SQL statement failed to execute successfully.
     * @throws PersonNotFoundException Person does not exist in the person table.
     */
    void updateAvailability(Context ctx) throws SQLException, PersonNotFoundException {
        int avail = ctx.formParam("availability", Integer.class).get();
        personRepository.updateAvailability(avail, currentPerson(ctx).getIdentifier());
    }

    /**
     * Get the current person of the session.
     *
     * @param ctx Javalin Context Object
     * @return The Person of the current user.
     */
    private Person currentPerson(Context ctx) throws SQLException, PersonNotFoundException {
        var personID = ctx.sessionAttribute("person_id");
        var person = personRepository.getOne((int) personID);
        if (person == null) throw new ForbiddenResponse();
        return person;
    }

    private void createToDB(Context ctx, Person person) {
        person.setFullName(ctx.formParam("fullName", ""));
        person.setLastSeen(ctx.formParam("lastSeen", ""));
        person.setStatus(ctx.formParam("status", ""));
        person.setLongitude(ctx.formParam("longitude", float.class, "").get());
        person.setLatitude(ctx.formParam("latitude", float.class, "").get());
        person.setPrivacy(ctx.formParam("privacy"));
    }

    /**
     * Gets the privacy of the current user in a JSON object.
     *
     * @param ctx Javalin Context Object
     * @throws SQLException            SQL statement failed to execute successfully.
     * @throws PersonNotFoundException Person does not exist in the person table.
     */
    public void getPrivacy(Context ctx) throws SQLException, PersonNotFoundException {
        var person = currentPerson(ctx);
        ctx.json(person.getPrivacy());
        ctx.status(200);
    }
}
