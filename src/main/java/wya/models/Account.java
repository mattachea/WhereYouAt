package wya.models;

public class Account {
    private String username;
    private String password;
    private String email;
    private String profilePicture;
    private int person_id;

    /**
     * The Account Constructor in the MVC
     *
     * @param username       The username to be associated with the account.
     * @param password       The password to be associated with the account, should NOT be in plaintext
     * @param email          The email to be associated with the account.
     * @param person_id      The person ID associated with the account, acts like a foreign key in a relational database.
     * @param profilePicture The profile picture associated with the account.
     */
    public Account(String username, String password, String email, int person_id, String profilePicture) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.profilePicture = profilePicture;
        this.person_id = person_id;
    }

    public Account() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getPerson_id() {
        return person_id;
    }

    public void setPerson_id(int person) {
        this.person_id = person;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
