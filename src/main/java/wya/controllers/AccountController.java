package wya.controllers;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.javalin.http.Context;
import io.javalin.http.ForbiddenResponse;
import io.javalin.http.NotFoundResponse;
import wya.models.Account;
import wya.repositories.AccountNotFoundException;
import wya.repositories.AccountRepository;

import java.sql.SQLException;
import java.util.Objects;

public class AccountController {

    private final AccountRepository accountRepository;

    public AccountController(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    /**
     * Returns the entire users table.
     * ctx.status = 200 upon successful completion.
     * Creates a JSON object with all the entries in users.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException SQL statement failed to execute.
     */
    public void getAll(Context ctx) throws SQLException {
        ctx.json(accountRepository.getAll());
        ctx.status(200);
    }

    /**
     * Returns the user specified by the username field from ctx.formParam.
     * Creates a JSON object with the specified entry in users with the ctx.formParam("username").
     *
     * @param ctx The Javalin Context Object.
     * @throws SQLException             SQL statement failed to execute.
     * @throws AccountNotFoundException Account with the specificed username not found in users table.
     */
    public void getOne(Context ctx) throws SQLException, AccountNotFoundException {
        ctx.json(accountRepository.getOne(ctx.formParam("username")));
        ctx.status(200);
    }

    /**
     * Gets the current user from the user table according to the username stored as ctx.sessionAttribute.
     * Creates a JSON object with the specified entry in useres with the username of the current user.
     *
     * @param ctx Javalin Context Object.
     * @throws SQLException             SQL statement failed to execute.
     * @throws AccountNotFoundException Account with the username not found in users table.
     */
    public void getCurrentUser(Context ctx) throws SQLException, AccountNotFoundException {
        var curr = currentUser(ctx);
        ctx.json(accountRepository.getOne(curr.getUsername()));
        ctx.status(200);
    }

    /**
     * Create a new user entry in user using the form params from ctx and the person_id.
     *
     * @param ctx       Javalin Context object with the form params to insert into new user entry.
     * @param person_id PK from person to use as FK in person.
     * @throws SQLException SQL statement failed to execute.
     */
    void register(Context ctx, int person_id) throws SQLException {
        Account account = new Account();
        account.setPerson_id(person_id);
        createToDB(ctx, account);
        accountRepository.create(account);
        ctx.status(201);
    }

    /**
     * Login handler.
     * Set the session attribute to the user account and the person_id of the current user logged in using cookies.
     *
     * @param ctx Javalin Context object with the form params to insert into new user entry.
     */
    void login(Context ctx) {
        Account user;
        String username = ctx.formParam("username");
        try {
            user = accountRepository.getOne(username);
        } catch (AccountNotFoundException e) {
            System.out.println("\"" + username + "\": Username not registered.");
            throw new ForbiddenResponse();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new NotFoundResponse();
        }
        BCrypt.Result result = BCrypt.verifyer().verify(Objects.requireNonNull(ctx.formParam("password", "")).toCharArray(), user.getPassword());
        if (!result.verified) {
            System.out.println("Invalid password");
            throw new ForbiddenResponse();
        }
        ctx.sessionAttribute("user", user);
        ctx.sessionAttribute("person_id", user.getPerson_id());
        ctx.status(200);
    }

    /**
     * Change password for account specified by ctx.
     *
     * @param ctx Javalin Context object with the form params to insert into user entry.
     */
    void changePass(Context ctx) throws AccountNotFoundException, SQLException {
        var curr = currentUser(ctx);
        BCrypt.Result result = BCrypt.verifyer().verify(Objects.requireNonNull(ctx.formParam("oldpassword")).toCharArray(), curr.getPassword());
        if (!result.verified) {
            System.out.println("Old passwords don't match.");
            throw new ForbiddenResponse();
        }

        if (Objects.requireNonNull(ctx.formParam("newpassword")).length() < 1) {
            System.out.println("New password is empty.");
            throw new ForbiddenResponse();
        }
        curr.setPassword(BCrypt.withDefaults().hashToString(12, Objects.requireNonNull(ctx.formParam("newpassword")).toCharArray()));
        accountRepository.updateDetails(curr);
        ctx.status(200);
    }

    /**
     * Check if the username is a duplicate.
     *
     * @param ctx Javalin Context Object.
     * @return If true, a duplicate username already in users table. If false, the username is unique.
     * @throws SQLException SQL statement failed to execute.
     */
    public boolean duplicateUsername(Context ctx) throws SQLException {
        return accountRepository.duplicateUsername(ctx);
    }

    /**
     * Private helper function to the current user of the session.
     *
     * @param ctx Javalin Context object
     * @return The Account of the current user.
     */
    private Account currentUser(Context ctx) {
        var user = (Account) ctx.sessionAttribute("user");
        if (user == null) throw new ForbiddenResponse();
        return user;
    }

    /**
     * Update user entry in user using the form params from ctx.
     *
     * @param ctx Javalin Context object with the form params to insert into user entry.
     * @throws SQLException             Statement failed to execute.
     * @throws AccountNotFoundException Account with PK identifier not found in user.
     */
    void updateDetails(Context ctx) throws SQLException, AccountNotFoundException {
        var curr = currentUser(ctx);
        editToDB(ctx, curr);
        accountRepository.updateDetails(curr);
        ctx.status(204);
    }

    /**
     * Helper function to create the Account object to be inserted into user.
     *
     * @param ctx     Javalin Context object with the form params to insert into user entry.
     * @param account Account object to be converted into user entry.
     */
    private void createToDB(Context ctx, Account account) {
        account.setUsername(ctx.formParam("username", ""));
        account.setPassword(BCrypt.withDefaults().hashToString(12, Objects.requireNonNull(ctx.formParam("password", "")).toCharArray()));
        account.setEmail(ctx.formParam("email", ""));
        account.setPerson_id(account.getPerson_id());
        account.setProfilePicture(ctx.formParam("profilePicture", ""));
    }

    /**
     * Helper function to create the Account object for updateDetails to be inserted into users table.
     *
     * @param ctx     Javalin Context Object.
     * @param account Account object containing metadata of user account.
     */
    private void editToDB(Context ctx, Account account) {
        account.setEmail(ctx.formParam("email", ""));
        account.setProfilePicture(ctx.formParam("profilePicture", ""));
    }
}

