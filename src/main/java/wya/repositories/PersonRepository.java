package wya.repositories;

import wya.models.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class PersonRepository {
    /**
     * Connection to db via JDBC.
     */
    private final Connection connection;

    /**
     * Constructor for PersonRepository.
     * Creates the person table if it does not exist in connection and prepares the headers.
     * Headers:
     * 1) identifier: INTEGER | PRIMARY KEY | AUTOINCREMENT
     * 2) fullName: TEXT
     * 3) lastSeen: TEXT
     * 4) live: BOOLEAN | DEFAULT true
     * 5) stats: TEXT
     * 6) longitude: DECIMAL(9,6)
     * 7) latitude: DECIMAL(9,6)|
     * 8) availability: INTEGER | DEFAULT 0
     * 9) privacy: TEXT
     *
     * @param connection DB connection.
     * @throws SQLException Statement failed to execute.
     */
    public PersonRepository(Connection connection) throws SQLException {
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS person (identifier INTEGER PRIMARY KEY AUTOINCREMENT, fullName TEXT, lastSeen TEXT, live BOOLEAN DEFAULT(TRUE), status TEXT, longitude DECIMAL(9,6), latitude DECIMAL(9,6), availability INTEGER DEFAULT(0), privacy TEXT, profilePicture TEXT)");
        statement.close();
    }

    /**
     * Get all entries in person and puts them into an ArrayList of Person objects.
     *
     * @return Arraylist of entries in person as Person Objects.
     * @throws SQLException Statement failed to execute.
     */
    public List<Person> getAll() throws SQLException {
        var people = new ArrayList<Person>();
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT * FROM person");
        while (result.next()) {
            people.add(createPersonFromDB(result));
        }
        statement.close();
        result.close();
        return people;
    }

    /**
     * Get all entries in person table except the user specified by the identifier, and only if they are marked as live=true.
     * Then put them into an ArrayList of Person Objects to be returned.
     *
     * @return The Arraylist of all person entries in persons that are marked as live excluding the person with the identifier.
     */
    public Object getAll(int identifier) throws SQLException {
        var people = new ArrayList<Person>();
        var statement = connection.prepareStatement("SELECT * FROM person WHERE NOT identifier=? AND live=TRUE");
        statement.setInt(1, identifier);
        var result = statement.executeQuery();
        while (result.next()) {
            people.add(createPersonFromDB(result));
        }
        statement.close();
        result.close();
        return people;
    }

    /**
     * Get entry with identifier from person table and into a Person Object.
     *
     * @param identifier The PK to find in person
     * @return Person object containing the fields from the entry with identifier.
     * @throws SQLException            Statement failed to execute.
     * @throws PersonNotFoundException PK not found in person.
     */
    public Person getOne(int identifier) throws SQLException, PersonNotFoundException {
        var statement = connection.prepareStatement("SELECT * FROM person WHERE identifier = ?");
        statement.setInt(1, identifier);
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
     * Create a new person entry for the current user.
     *
     * @param person Person object that has fullName, lastSeen, live, status, longitude, latitude, availability fields.
     * @return PK identifier of new person entry.
     * @throws SQLException Statement failed to execute.
     */
    public int create(Person person) throws SQLException {
        var statement = connection.prepareStatement("INSERT INTO person (fullName, lastSeen, live, status, longitude, latitude, privacy, profilePicture) VALUES (?,?,?,?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        prepareStatement(person, statement);
        statement.executeUpdate();
        int id = statement.getGeneratedKeys().getInt(1);
        statement.close();
        return id;
    }

    /**
     * Update the user profile of the session's user with the fullName, lastSeen, live, status, longitude, latitude,
     * availability, privacy fields stored in person.
     *
     * @param person Person object that has fullName, lastSeen, live, status, longitude, latitude, availability, privacy fields.
     * @throws SQLException            Statement failed to execute.
     * @throws PersonNotFoundException Person with person_id not found.
     */
    public void updateDetails(Person person) throws SQLException, PersonNotFoundException {
        var statement = connection.prepareStatement("UPDATE person SET fullName=?, lastSeen=?, live=?, status=?, longitude=?, latitude=?, privacy=?, availability=?, profilePicture=? WHERE identifier = ? ");
        prepareStatement(person, statement);
        statement.setInt(8, person.getAvailability());
        statement.setString(9, person.getProfilePicture());
        statement.setInt(10, person.getIdentifier());
        try {
            if (statement.executeUpdate() == 0) throw new PersonNotFoundException();
        } finally {
            statement.close();
        }
    }

    /**
     * Updates the lastSeen field of the current person to time.
     *
     * @param time       The time string to be stored in the table.
     * @param identifier The identifier of the person to be updated.
     * @throws SQLException            SQL execution problem.
     * @throws PersonNotFoundException Person does not exist.
     */
    public void updateTime(String time, int identifier) throws SQLException, PersonNotFoundException {
        var statement = connection.prepareStatement("UPDATE person SET lastSeen = ? WHERE identifier = ?");
        statement.setString(1, time);
        statement.setInt(2, identifier);
        try {
            if (statement.executeUpdate() == 0) throw new PersonNotFoundException();
        } finally {
            statement.close();
        }
    }

    /**
     * Updates the location of the Current User's profile to their device's current location.
     *
     * @param longitude  Longitude of their current location.
     * @param latitude   Latitiude of their current location.
     * @param identifier The identifier of the person to be changed.
     * @throws SQLException            Result failed to find the row.
     * @throws PersonNotFoundException Person does not exist.
     */
    public void updateLocation(float longitude, float latitude, int identifier) throws SQLException, PersonNotFoundException {
        var statement = connection.prepareStatement("UPDATE person SET longitude=?, latitude=? WHERE identifier = ?");
        statement.setFloat(1, longitude);
        statement.setFloat(2, latitude);
        statement.setInt(3, identifier);
        try {
            if (statement.executeUpdate() == 0) throw new PersonNotFoundException();
        } finally {
            statement.close();
        }
    }

    /**
     * Updates the location of the Current User's profile to their device's current location.
     *
     * @param avail      The availability of the user
     *                   0 = available
     *                   1 = busy
     *                   2 = do not disturb
     * @param identifier The identifier of the person to be changed.
     * @throws SQLException            SQL statement failed to execute.
     * @throws PersonNotFoundException Person not found in table.
     */
    public void updateAvailability(int avail, int identifier) throws SQLException, PersonNotFoundException {
        var statement = connection.prepareStatement("UPDATE person SET availability=? WHERE identifier=?");
        statement.setInt(1, avail);
        statement.setInt(2, identifier);
        try {
            if (statement.executeUpdate() == 0) throw new PersonNotFoundException();
        } finally {
            statement.close();
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

    private void prepareStatement(Person person, PreparedStatement statement) throws SQLException {
        statement.setString(1, person.getFullName());
        statement.setString(2, person.getLastSeen());
        statement.setBoolean(3, person.isLive());
        statement.setString(4, person.getStatus());
        statement.setFloat(5, person.getLongitude());
        statement.setFloat(6, person.getLatitude());
        statement.setString(7, person.getPrivacy());
        statement.setString(8, person.getProfilePicture());
    }
}
