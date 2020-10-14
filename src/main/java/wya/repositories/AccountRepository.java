package wya.repositories;

import io.javalin.http.Context;
import wya.models.Account;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AccountRepository {
    private final Connection connection;

    /**
     * Prepares and creates the users table and headers if it does not already exist in connection.
     * Headers:
     * 1) username: TEXT | PRIMARY KEY | UNIQUE | NOT NULL
     * 2) password: TEXT | NOT NULL
     * 3) email: TEXT
     * 4) person_id: INTEGER | FOREIGN KEY (Referencing person(identifier)
     * 5) profilePictre: TEXT
     *
     * @param connection The connection to the database
     * @throws SQLException SQL statement failed to execute.
     */
    public AccountRepository(Connection connection) throws SQLException {
        this.connection = connection;
        var statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS users (username TEXT PRIMARY KEY UNIQUE NOT NULL, password TEXT NOT NULL, email TEXT," +
                "person_id INTEGER NOT NULL, profilePicture TEXT, FOREIGN KEY (person_id) REFERENCES person(identifier))");
        statement.close();
    }

    /**
     * Get all the entries in the users table and returns every column using the Account object representation.
     *
     * @return A list of Account objects.
     * @throws SQLException SQL statement failed to execute.
     */
    public List<Account> getAll() throws SQLException {
        var account = new ArrayList<Account>();
        var statement = connection.createStatement();
        var result = statement.executeQuery("SELECT * FROM users");
        while (result.next()) {
            account.add(createAccountFromDB(result));
        }
        result.close();
        statement.close();
        return account;
    }

    /**
     * Get the entry in the users table with the specific username.
     *
     * @param username The username that will be used to select from the users table.
     * @return The Account object representation of the columns for the users entry.
     * @throws SQLException             SQL statement failed to execute.
     * @throws AccountNotFoundException Entry with the username not found in users.
     */
    public Account getOne(String username) throws SQLException, AccountNotFoundException {
        var statement = connection.prepareStatement("SELECT * FROM users WHERE username = ?");
        statement.setString(1, username);
        var result = statement.executeQuery();
        try {
            if (result.next()) {
                return createAccountFromDB(result);
            } else {
                throw new AccountNotFoundException();
            }
        } finally {
            statement.close();
            result.close();
        }
    }

    /**
     * Insert a new entry into users with the same data from account member variables.
     *
     * @param account The Account object to be inserted into the users table.
     * @throws SQLException SQL statement failed to execute.
     */
    public void create(Account account) throws SQLException {
        var statement = connection.prepareStatement("INSERT INTO users (username, password, email, person_id, profilePicture) VALUES(?,?,?,?,?)");
        statement.setString(1, account.getUsername());
        statement.setString(2, account.getPassword());
        statement.setString(3, account.getEmail());
        statement.setInt(4, account.getPerson_id());
        statement.setString(5, account.getProfilePicture());
        statement.execute();
        statement.close();
    }

    /**
     * Find an entry with the same username specified in ctx.formParam("username").
     *
     * @param ctx Javalin Context Object.
     * @return If true, the username is not unique. If false, the username is unique.
     * @throws SQLException SQL statement failed to execute.
     */
    public boolean duplicateUsername(Context ctx) throws SQLException {
        var statement = connection.prepareStatement("SELECT CASE WHEN EXISTS (SELECT 1 FROM users WHERE username=?) THEN CAST(1 AS BIT) ELSE CAST(0 AS BIT) END");
        statement.setString(1, ctx.formParam("username"));
        var result = statement.executeQuery();
        return result.getBoolean(1);
    }

    /**
     * Update the details of the user with details stored in the Account object representation of the user into the users table.
     *
     * @param account The account that needs to be uploaded.
     * @throws SQLException             SQL statement failed to execute.
     * @throws AccountNotFoundException Account with the username not found in the users table.
     */
    public void updateDetails(Account account) throws SQLException, AccountNotFoundException {
        var statement = connection.prepareStatement("UPDATE users SET password = ?,  email = ?, person_id = ?,  profilePicture = ? WHERE username = ?");
        statement.setString(1, account.getPassword());
        statement.setString(2, account.getEmail());
        statement.setInt(3, account.getPerson_id());
        statement.setString(4, account.getProfilePicture());
        statement.setString(5, account.getUsername());

        var statement2 = connection.prepareStatement("UPDATE person SET profilePicture =? WHERE identifier = ?");
        statement2.setString(1, account.getProfilePicture());
        statement2.setInt(2, account.getPerson_id());

        try {
            if (statement.executeUpdate() == 0) throw new AccountNotFoundException();
            if (statement2.executeUpdate() == 0) try {
                throw new PersonNotFoundException();
            } catch (PersonNotFoundException e) {
                e.printStackTrace();
            }
        } finally {
            statement.close();
        }
    }

    private Account createAccountFromDB(ResultSet result) throws SQLException {
        return new Account(
                result.getString("username"),
                result.getString("password"),
                result.getString("email"),
                result.getInt("person_id"),
                result.getString("profilePicture")
        );
    }
}
