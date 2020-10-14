package wya.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AccountTest {
    Account account;
    String username = "username";
    String password = "password";
    String email = "email";
    String profilePicture = "profilePicture";
    int person_id = 1;


    @BeforeEach
    void setUp() {
        account = new Account(username, password, email, person_id, profilePicture);
    }

    @Test
    void getUsername() {
        assertEquals(username, account.getUsername());
    }

    @Test
    void setUsername() {
        String expected = "expected";
        account.setUsername(expected);
        assertEquals(expected, account.getUsername());
    }

    @Test
    void getEmail() {
        assertEquals(email, account.getEmail());

    }

    @Test
    void setEmail() {
        String expected = "expected";
        account.setEmail(expected);
        assertEquals(expected, account.getEmail());
    }

    @Test
    void getProfilePicture() {
        assertEquals(profilePicture, account.getProfilePicture());
    }

    @Test
    void setProfilePicture() {
        String expected = "expected";
        account.setProfilePicture(expected);
        assertEquals(expected, account.getProfilePicture());
    }

    @Test
    void getPerson_id() {
        assertEquals(person_id, account.getPerson_id());
    }

    @Test
    void setPerson_id() {
        int expected = 7;
        account.setPerson_id(expected);
        assertEquals(expected, account.getPerson_id());
    }

    @Test
    void getPassword() {
        assertEquals(password, account.getPassword());
    }

    @Test
    void setPassword() {
        String expected = "expected";
        account.setPassword(expected);
        assertEquals(expected, account.getPassword());
    }
}