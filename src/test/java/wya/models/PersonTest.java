package wya.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PersonTest {
    Person person;
    int identifier = 7;
    String fullName = "John";
    String lastSeen = "yesterday";
    boolean live = true;
    String status = "coding";
    int availability = 1;
    float longitude = 12.345678f;
    float latitude = 87.654321f;
    String privacy = "state";
    String profilePicture = "profilePicture";

    @BeforeEach
    void setUp() {
        person = new Person(identifier, fullName, lastSeen, live, status, longitude, latitude, availability, privacy, profilePicture);
    }

    @Test
    void getIdentifier() {
        assertEquals(identifier, person.getIdentifier());
    }

    @Test
    void setIdentifier() {
        int expected = 9;
        person.setIdentifier(expected);
        assertEquals(expected, person.getIdentifier());
    }

    @Test
    void getFullName() {
        assertEquals(fullName, person.getFullName());
    }

    @Test
    void setFullName() {
        String expected = "name name";
        person.setFullName(expected);
        assertEquals(expected, person.getFullName());
    }

    @Test
    void getLastSeen() {
        assertEquals(lastSeen, person.getLastSeen());
    }

    @Test
    void setLastSeen() {
        String expected = "last night";
        person.setLastSeen(expected);
        assertEquals(expected, person.getLastSeen());
    }

    @Test
    void isLive() {
        assertTrue(person.isLive());
    }

    @Test
    void setLive() {
        person.setLive(false);
        assertFalse(person.isLive());
    }

    @Test
    void getStatus() {
        assertEquals(status, person.getStatus());
    }

    @Test
    void setStatus() {
        String expected = "expected status";
        person.setStatus(expected);
        assertEquals(expected, person.getStatus());
    }

    @Test
    void getLongitude() {
        assertEquals(longitude, person.getLongitude());
    }

    @Test
    void setLongitude() {
        float expected = 13216132.4123f;
        person.setLongitude(expected);
        assertEquals(expected, person.getLongitude());

    }

    @Test
    void getLatitude() {
        assertEquals(latitude, person.getLatitude());
    }

    @Test
    void setLatitude() {
        float expected = 13216132.4123f;
        person.setLatitude(expected);
        assertEquals(expected, person.getLatitude());

    }

    @Test
    void getAvailability() {
        assertEquals(availability, person.getAvailability());
    }

    @Test
    void setAvailability() {
        int expected = 0;
        person.setAvailability(expected);
        assertEquals(expected, person.getAvailability());
    }

    @Test
    void setPrivacy() {
        assertEquals(privacy, person.getPrivacy());
    }

    @Test
    void getPrivacy() {

        String expected = "home";
        person.setPrivacy(expected);
        assertEquals(expected, person.getPrivacy());
    }
//
//    @Test
//    void setProfilePicture() {
//        String expected = "profile";
//
//    }
}