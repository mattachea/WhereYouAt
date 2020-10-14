package wya;

import io.javalin.Javalin;
import wya.controllers.AppController;
import wya.repositories.AccountNotFoundException;
import wya.repositories.EventNotFoundException;
import wya.repositories.EventRelationsNotFoundException;
import wya.repositories.PersonNotFoundException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static io.javalin.apibuilder.ApiBuilder.*;

public class Server {
    public static void main(String[] args) throws SQLException {
        AppController appController = new AppController();
        Connection connection = DriverManager.getConnection("jdbc:sqlite:wya.db");
        Javalin.create(config -> config.addStaticFiles("/public"))
                .events(event -> event.serverStopped(connection::close))
                .routes(() -> {
                    path("register", () -> post(appController::register));
                    path("changePassword", () -> put(appController::changePassword));
                    /* This gets a USER. */
                    path("login", () -> post(appController::login));
                    path("updateAccount", () -> {
                        get(appController::getAccount);
                        /* THis updates a USER.*/
                        put(appController::updateAccount);
                    });
                    path("profile", () -> {
                        get(appController::getProfile);
                        /* This is a person identifier. */
                        path(":identifier", () -> {
                            get(appController::getEventIDsForPerson);
//                            path(":eventIdentifier", () -> {
//                                /** Say that a user is NOT going to a specific event. */
//                                delete(appController::removeRelation);
//                                /** Say that a user IS going to a specific event. */
//                                put(appController::createRelation);
//                            });
                        });
                    });
                    path("updateProfile", () -> {
                        put(appController::updateProfile);
                        get(appController::getProfile);
                    });
                    path("event", () -> {
                        get(appController::viewEvents);
                        path(":identifier", () -> {
                            put(appController::editEvent);
                            get(appController::getPersonIDForEvent);
                            delete(appController::removeRelation);
                        });
                        path("create", () -> post(appController::createEvent));
                    });
                    path("eventAttendance", () -> {
                        post(appController::createForList);
                        /* this path will also get person IDs for an event. getting event IDs for a person is in  profile path. */
                        path(":identifier", () -> {
                            put(appController::createRelation);
                            get(appController::getPersonIDForEvent);
                            delete(appController::removeRelation);
                        });
                    });
                    path("friends", () -> {
                        get(appController::viewFriends);
//                        path("editFriends", () -> {
//                            put(appController::editFriends);
//                        });
                    });
                    path("time", () -> put(appController::updateTime));
                    path("location", () -> put(appController::updateLocation));
                    path("availability", () -> put(appController::updateAvailability));
                    path("privacy", () -> get(appController::getPrivacy));
                    path("account", () -> get(appController::getAccount));
                })
                .exception(AccountNotFoundException.class, (e, ctx) -> ctx.status(403))
                .exception(PersonNotFoundException.class, (e, ctx) -> ctx.status(403))
                .exception(EventNotFoundException.class, (e, ctx) -> ctx.status(404))
                .exception(EventRelationsNotFoundException.class, (e, ctx) -> ctx.status(404))
                .start(System.getenv("PORT") == null ? 7000 : Integer.parseInt(System.getenv("PORT")));
    }
}