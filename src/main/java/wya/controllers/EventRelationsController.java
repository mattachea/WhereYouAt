package wya.controllers;

import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import wya.models.Account;
import wya.models.EventRelations;
import wya.repositories.EventRelationsNotFoundException;
import wya.repositories.EventRelationsRepository;

import java.sql.SQLException;

public class EventRelationsController {
    private final EventRelationsRepository eventRelationsRepository;

    public EventRelationsController(EventRelationsRepository eventRelationsRepository) {
        this.eventRelationsRepository = eventRelationsRepository;
    }

    /**
     * Get JSON object containing the list of people associated with the event.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException                    SQL statement failed to execute successfully.
     * @throws EventRelationsNotFoundException Events relationship with the identifier not found.
     */
    public void getPersonIDForEvent(Context ctx) throws SQLException, EventRelationsNotFoundException {
        ctx.json(eventRelationsRepository.getPersonIDsForEvent(ctx.pathParam("identifier", Integer.class).get()));
//        return eventRelationsRepository.getPersonIDsForEvent(id);
    }

    /**
     * Get JSON object containing the list of events associated with the person.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException                    SQL statement failed to execute successfully.
     * @throws EventRelationsNotFoundException Events relationship with the identifier not found.
     */
    public void getEventIDsForPerson(Context ctx) throws SQLException, EventRelationsNotFoundException {
        ctx.json(eventRelationsRepository.getEventIDsForPerson(ctx.pathParam("identifier", Integer.class).get()));
//        return eventRelationsRepository.getEventIDsForPerson(id);
    }

    /**
     * Deletes an entry in events table if an event invitation is refused.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    public void removeRelation(Context ctx) throws SQLException {
        var eventRelation = new EventRelations();
        //route through person id then find way to get event id?
        createToDB(this.currentUserID(ctx), ctx.pathParam("identifier", Integer.class).get(), eventRelation);
        eventRelationsRepository.removeAPerson(eventRelation);
    }

    /**
     * Create a new entry in the events table using the identifier of the current person and the event they are to be associated with.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    public void create(Context ctx) throws SQLException {
        var eventRelation = new EventRelations();
        createToDB(this.currentUserID(ctx), ctx.pathParam("identifier", Integer.class).get(), eventRelation);
        eventRelationsRepository.create(eventRelation);
    }

    /**
     * Create a new entry in the events table using the identifier of the person and identifier to be associated with each other.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    public void createForList(Context ctx) throws SQLException {
        var eventRelation = new EventRelations();
        createToDB(ctx.formParam("personID", Integer.class).get(), ctx.formParam("eventID", Integer.class).get(), eventRelation);
        eventRelationsRepository.create(eventRelation);
    }

    /**
     * Deleting an event and its relationships from the events and EventsRelationshions tables respectively.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    //TODO: ADD THIS TO THE DELETE EVENT ROUTE!
    public void deleteEvent(Context ctx) throws SQLException {
        eventRelationsRepository.removeEvent(ctx.pathParam("identifier", Integer.class).get());
    }


    /**
     * Removing a relation in the eventsrelation table such that the person is no longer associated with the event.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    //TODO: ADD THIS TO THE DELETE PERSON ROUTE!
    public void deletePerson(Context ctx) throws SQLException {
        eventRelationsRepository.removePerson(ctx.pathParam("identifier", Integer.class).get());
    }

    /**
     * Get the current user of the session.
     *
     * @param ctx Javalin Context Object.
     * @return The Account of the current user.
     */
    private int currentUserID(Context ctx) {
        var user = (Account) ctx.sessionAttribute("user");
        if (user == null) throw new ForbiddenResponse();
        return user.getPerson_id();
    }

    private void createToDB(int personID, int eventID, EventRelations eventRelations) {
        eventRelations.setPersonID(personID);
        eventRelations.setEventID(eventID);
    }

}
