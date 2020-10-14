package wya.repositories;

import wya.models.Event;
import wya.models.Person;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventRepository {
    private final Connection connection;

    /**
     * Prepares and creates the events table and headers.
     * Headers:
     * 1) identifier: INTEGER | PRIMARY KEY | AUTOINCREMENT
     * 2) name: TEXT
     * 3) description: TEXT
     * 4) place: TEXT
     * 5) longitude: DECIMAL(9,6)
     * 6) latitude: DECIMAL(9,6)
     * 7) image: TEXT
     * 8) startTime: TEXT
     * 9) endTime: TEXT
     *
     * @param connection The connection to the database.
     * @throws SQLException The SQL statement failed to execute.
     */
    public EventRepository(Connection connection) throws SQLException {
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS events (identifier INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, description TEXT, " +
                "place TEXT, longitude DECIMAL(9,6), latitude DECIMAL(9,6), image TEXT, startTime TEXT, endTime TEXT)");
        statement.close();
    }

    /**
     * Get all the events from the events table into a JSON object.
     *
     * @throws SQLException SQL statement failed to execute successfully.
     */
    public List<Event> getAll() throws SQLException {
        var events = new ArrayList<Event>();
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT * FROM events");
        while (result.next()) {
            events.add(createEventFromDB(result));
        }
        result.close();
        statement.close();
        return events;
    }

    /**
     * Get an entry from the events table specified by the id.
     *
     * @param id The identifier of the events to get from the events table.
     * @return The Event object representation of the entry in the events table.
     * @throws SQLException SQL statement failed to execute successfully
     * @throws EventNotFoundException Event with id not found in table.
     */
    public Event getOne(int id) throws SQLException, wya.repositories.EventNotFoundException {
        var statement = connection.prepareStatement("SELECT * FROM events WHERE identifier = ?");
        statement.setInt(1, id);
        var result = statement.executeQuery();
        try {
            if (result.next()) {
                return createEventFromDB(result);
            } else {
                throw new EventNotFoundException();
            }
        } finally {
            statement.close();
            result.close();
        }
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
     * @throws SQLException SQL statement failed to execute successfully.
     */
    public void create(Event event) throws SQLException {
        var statement = connection.prepareStatement("INSERT INTO events (name, description, place, longitude, latitude, image, startTime, endTime) VALUES(?,?,?,?,?,?,?,?)");
        prepareStatement(event, statement);
        statement.execute();
        statement.close();
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
     * @throws SQLException           SQL statement failed to execute successfully.
     * @throws EventNotFoundException Event specified by the pathParam not found in events table.
     */
    public void updateDetails(Event event) throws SQLException, EventNotFoundException {
        var statement = connection.prepareStatement("UPDATE events SET name = ?, description = ?, place = ?, longitude = ?, latitude = ?, image = ?, startTime = ?, endTime = ? WHERE identifier = ?");
        prepareStatement(event, statement);
        statement.setInt(9, event.getIdentifier());
        try {
            if (statement.executeUpdate() == 0) throw new EventNotFoundException();
        } finally {
            statement.close();
        }
    }

    /*
     * Helper to prepare SQL query
     */
    private void prepareStatement(Event event, PreparedStatement statement) throws SQLException {
        statement.setString(1, event.getName());
        statement.setString(2, event.getDescription());
        statement.setString(3, event.getPlace());
        statement.setFloat(4, event.getLongitude());
        statement.setFloat(5, event.getLatitude());
        statement.setString(6, event.getImage());
        statement.setString(7, event.getStartTime());
        statement.setString(8, event.getEndTime());
    }

    /*
     * Helper to prepare INSERT SQL query
     */
    private Event createEventFromDB(ResultSet result) throws SQLException {
        return new Event(
                result.getInt("identifier"),
                result.getString("name"),
                result.getString("description"),
                result.getString("place"),
                result.getFloat("longitude"),
                result.getFloat("latitude"),
                result.getString("image"),
                result.getString("startTime"),
                result.getString("endTime"),
                new Person[]{}, //TODO How do you interact with foreign keys
                new Person[]{}, //TODO How do you interact with foreign keys
                new Person[]{}, //TODO How do you interact with foreign keys
                new Person[]{}
        ); //TODO How do you interact with foreign keys
    }
}
