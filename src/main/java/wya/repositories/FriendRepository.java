package wya.repositories;

import wya.models.Person;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class FriendRepository {
    /**
     * Connection to db via JDBC.
     */
    private final Connection connection;

    /**
     * Constructor for Person Repository.
     *
     * @param connection DB connection.
     * @throws SQLException Statement failed to execute.
     */
    public FriendRepository(Connection connection) throws SQLException {
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS friends (identifier INTEGER PRIMARY KEY AUTOINCREMENT, user1 INTEGER, user2 INTEGER, status INTEGER DEFAULT 0)");
        statement.close();
    }

    /**
     * Get a list of friends from the current user.
     *
     * @return Arraylist of entries in person
     * @throws SQLException Statement failed to execute.
     */
    public List<Person> getAll(int user) throws SQLException {
        var people = new ArrayList<Person>();
        var statement = connection.prepareStatement("SELECT * FROM person INNER JOIN friends ON friends.user2=person.identifier WHERE user1=?");
        statement.setInt(1, user);
        var result = statement.executeQuery();
        while (result.next()) {
            people.add(createPersonFromDB(result));
        }

        statement.close();
        result.close();
        return people;
    }

    /**
     * Get the Person object of a specific friend specified by user2.
     *
     * @param user1 The person making the request.
     * @param user2 The target user friend.
     * @return The Person object of the friend.
     * @throws SQLException            The statement failed to execute.
     * @throws PersonNotFoundException Person is not found in the table.
     */
    public Person getOne(int user1, int user2) throws SQLException, PersonNotFoundException {
        var statement = connection.prepareStatement("SELECT * FROM person INNER JOIN friends ON friends.user2=person.identifier WHERE user1=? AND user2=? END AND");
        statement.setInt(1, user1);
        statement.setInt(2, user2);
        var result = statement.executeQuery();
        try {
            if (result.next()) {
                return createPersonFromDB(result);
            } else {
                throw new PersonNotFoundException();
            }
        } finally {
            statement.close();
            result.close();
        }
    }

    /**
     * When user1 requests to add user2 as a friend.
     *
     * @param user1 The ID of the user making the request.
     * @param user2 The ID of the user the request is being sent to.
     * @throws SQLException SQL statement failed to execut.
     */
    public void request(int user1, int user2) throws SQLException {
        var statement = connection.prepareStatement("INSERT INTO friends (user1, user2) VALUES (?,?)");
        statement.setInt(1, user1);
        statement.setInt(2, user2);
        statement.executeUpdate();
        statement.close();
    }

    /**
     * Update the status of two friends.
     *
     * @param user2  The ID of the user responding to the request.
     * @param user1  The ID of the user that created the original request.
     * @param accept A boolean that determines the status of the friend request; if true, friend request accepted
     *               if false, the pair are not friends.
     * @throws SQLException            SQL Error.
     * @throws FriendNotFoundException No such friend request found.
     */
    public void updateStatus(int user2, int user1, boolean accept) throws SQLException, FriendNotFoundException {
        var statement = connection.prepareStatement("UPDATE friends SET status=? WHERE user1=? AND user2=? END AND");
        int status;
        if (accept) status = 1;
        else status = 2;

        statement.setInt(1, status);
        statement.setInt(2, user1);
        statement.setInt(3, user2);
        try {
            if (statement.executeUpdate() == 0) throw new FriendNotFoundException();
            // Need to duplicate the same entry for user2.
            var statement2 = connection.prepareStatement("INSERT INTO friends (user1, user2, status) VALUES (?, ?, ?)");
            statement.setInt(1, status);
            statement.setInt(2, user1);
            statement.setInt(3, user2);
            statement2.executeUpdate();
        } finally {
            statement.close();
        }
    }

    /**
     * Check the status of the two users to see if they are friends.
     *
     * @param user1 The person making the request.
     * @param user2 The target person to see if they are friends.
     * @return The status of the relationship of the friends.
     * @throws SQLException            SQL failed to execute properly.
     * @throws FriendNotFoundException There's no relationship between the two people.
     */
    public int checkStatus(int user1, int user2) throws SQLException, FriendNotFoundException {
        var statement = connection.prepareStatement("SELECT status FROM friends WHERE user1=? AND user2=?");
        statement.setInt(1, user1);
        statement.setInt(2, user2);
        var result = statement.executeQuery();
        try {
            if (result.next()) {
                return result.getInt("status");
            } else {
                throw new FriendNotFoundException();
            }
        } finally {
            statement.close();
            result.close();
        }
    }

    /**
     * Helper function to create a a person object from a person database entry.
     *
     * @param result Entry from person.
     * @return Person object with the entries from person DB.
     * @throws SQLException Result failed to return field.
     */
    private Person createPersonFromDB(ResultSet result) throws SQLException {
        return new Person(
                result.getInt("identifier"),
                result.getString("fullName"),
                result.getString("lastSeen"),
                result.getBoolean("live"),
                result.getString("status"),
                result.getFloat("longitude"),
                result.getFloat("latitude"),
                result.getInt("availability"),
                result.getString("privacy"),
                result.getString("profilePicture")
        );
    }
}
