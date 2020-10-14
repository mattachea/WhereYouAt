package wya.controllers;

import io.javalin.http.Context;
import wya.models.Event;
import wya.repositories.EventNotFoundException;
import wya.repositories.EventRepository;

import java.sql.SQLException;

public class EventController {
    private final EventRepository eventsRepository;

    /**
     * Event Controller Constructor.
     *
     * @param eventsRepository The events repository this controller is connected.
     */
    public EventController(EventRepository eventsRepository) {
        this.eventsRepository = eventsRepository;
    }

    /**
     * Get all the events from the events table into a JSON object.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    public void getAll(Context ctx) throws SQLException {
        ctx.json(eventsRepository.getAll());
    }

    /**
     * Create an event in the event table using the Context.formParams.
     * Example:
     * name:event
     * description:some event
     * place:Johns Hopkins University
     * longitude:39.3289957
     * latitude:-76.6205235
     * image:someimageurl.com
     * startTime:2019-11-06T01:29:06+00:00
     * endTime:2019-11-07T01:29:06+00:00
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    public void create(Context ctx) throws SQLException {
        var event = new Event();
        createToDB(ctx, event);
        eventsRepository.create(event);
        ctx.status(201);
    }

    /**
     * Edit an event in the event table using the Context.formParams specified by the Context.pathParam.
     * Example:
     * name:edit event
     * description:edited event
     * place:Johns Hopkins University
     * longitude:39.3289957
     * latitude:-76.6205235
     * image:someimageurl.com
     * startTime:2019-11-06T01:29:06+00:00
     * endTime:2019-11-07T01:29:06+00:00
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException           SQL statement failed to execute successfully.
     * @throws EventNotFoundException Event specified by the pathParam not found in events table.
     */
    public void updateDetails(Context ctx) throws SQLException, EventNotFoundException {
        var event = eventsRepository.getOne(ctx.pathParam("identifier", Integer.class).get());
        createToDB(ctx, event);
        eventsRepository.updateDetails(event);
        ctx.status(204);
    }

    public void updatePeople(Context ctx) {
        //TODO FOR PERSONS ARRAYS
    }

    private void createToDB(Context ctx, Event event) {
        event.setName(ctx.formParam("name", ""));
        event.setDescription(ctx.formParam("description", ""));
        event.setPlace(ctx.formParam("place", ""));
        event.setLongitude(ctx.formParam("longitude", float.class, "").get());
        event.setLatitude(ctx.formParam("latitude", float.class, "").get());
        event.setImage(ctx.formParam("image", ""));
        event.setStartTime(ctx.formParam("startTime", ""));
        event.setEndTime(ctx.formParam("endTime", ""));
    }

}
