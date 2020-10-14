package wya.repositories;

import wya.models.EventRelations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventRelationsRepository {
    private final Connection connection;

    /**
     * EventRelations Constructor
     *
     * @param connection The connection to the datbaase client.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    public EventRelationsRepository(Connection connection) throws SQLException {
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS eventRelations (eventID INTEGER, personID INTEGER)");
        statement.close();
    }

    /**
     * Return a list of event identifiers associated with the person identifier id.
     *
     * @param id The person identifier.
     * @return The List of event identifiers associated with the person id.
     * @throws SQLException SQL statement failed to execute successfully.
     * @throws EventRelationsNotFoundException The eventrelations identifier not found in the table.
     */
    public List<Integer> getEventIDsForPerson(int id) throws SQLException, EventRelationsNotFoundException {
        var eventIDs = new ArrayList<Integer>();
        var statement = connection.prepareStatement("SELECT eventID FROM eventRelations WHERE personID = ?");
        statement.setInt(1, id);
        var result = statement.executeQuery();
        while (result.next()) {
            eventIDs.add(result.getInt("eventID"));
        }
        if (eventIDs.isEmpty()) {
            throw new EventRelationsNotFoundException();
        }
        result.close();
        statement.close();
        return eventIDs;
    }

    /**
     * Return a list of person identifiers associated with the event identifier id.
     *
     * @param id The event identifier.
     * @return The list of person identifiers associated with the event id.
     * @throws SQLException SQL statement failed to execute successfully.
     * @throws EventRelationsNotFoundException The eventrelations identifier not found in the table.
     */
    public List<Integer> getPersonIDsForEvent(int id) throws SQLException, EventRelationsNotFoundException {
        var personIDs = new ArrayList<Integer>();
        var statement = connection.prepareStatement("SELECT personID FROM eventRelations WHERE eventID = ?");
        statement.setInt(1, id);
        var result = statement.executeQuery();
        while (result.next()) {
            personIDs.add(result.getInt("personID"));
        }
        if (personIDs.isEmpty()) {
            throw new EventRelationsNotFoundException();
        }
        result.close();
        statement.close();
        return personIDs;
    }

    /**
     * Create a new entry in the eventrelations table with the eventID and personID.
     *
     * @param eventRelations The eventRelations object to insert into the table.
     * @throws SQLException SQL statement failed to execute successfully.
     */
    public void create(EventRelations eventRelations) throws SQLException {
        var statement = connection.prepareStatement("INSERT INTO eventRelations (eventID, personID) VALUES(?,?)");
        prepareStatement(eventRelations, statement);
        statement.execute();
        statement.close();
    }


    /**
     * For this to work we need to give an eventID and person ID to remove 1 person from 1 event.
     */
    public void removeAPerson(EventRelations eventRelations) throws SQLException {
        var statement = connection.prepareStatement("DELETE FROM eventRelations WHERE eventID = ? AND personID = ?");
        prepareStatement(eventRelations, statement);
        statement.execute();
        statement.close();
    }

    /**
     * Remove every personID and eventID for a single event ID.
     */
    public void removeEvent(int eventID) throws SQLException {
        var statement = connection.prepareStatement("DELETE * FROM eventRelations WHERE eventID = ?");
        statement.setInt(1, eventID);
        statement.execute();
        statement.close();
    }

    /**
     * Remove every eventID and personID for a single person ID.
     */
    public void removePerson(int personID) throws SQLException {
        var statement = connection.prepareStatement("DELETE * FROM eventRelations WHERE personID = ?");
        statement.setInt(1, personID);
        statement.execute();
        statement.close();
    }

    private void prepareStatement(EventRelations eventRelations, PreparedStatement statement) throws SQLException {
        statement.setInt(1, eventRelations.getEventID());
        statement.setInt(2, eventRelations.getPersonID());
    }


}
