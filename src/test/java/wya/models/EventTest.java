package wya.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EventTest {
    Event event;
    int identifier = 9;
    String name = "event";
    String description = "description";
    String place = "place";
    float longitude = 70.205134f;
    float latitude = 83.151234f;
    String image = "image";
    String startTime = "startTime";
    String endTime = "endTime";

    Person friend = new Person(0, "name", "lastSeen", true, "studying", 12.1235f, 1234123f, 1, "postal code", "pfp");
    Person[] people = {friend};

    @BeforeEach
    void setUp() {
        event = new Event(identifier, name, description, place, longitude, latitude, image, startTime, endTime, people, people, people, people);
    }

    @Test
    void getIdentifier() {
        assertEquals(identifier, event.getIdentifier());
    }

    @Test
    void setIdentifier() {
        int expected = 19;
        event.setIdentifier(expected);
        assertEquals(expected, event.getIdentifier());
    }

    @Test
    void getName() {
        assertEquals(name, event.getName());
    }

    @Test
    void setName() {
        String expected = "Expected";
        event.setName(expected);
        assertEquals(expected, event.getName());
    }

    @Test
    void getDescription() {
        assertEquals(description, event.getDescription());
    }

    @Test
    void setDescription() {
        String expected = "Expected";
        event.setDescription(expected);
        assertEquals(expected, event.getDescription());
    }

    @Test
    void getPlace() {
        assertEquals(place, event.getPlace());
    }

    @Test
    void setPlace() {
        String expected = "place";
        event.setPlace(expected);
        assertEquals(expected, event.getPlace());
    }

    @Test
    void getLongitude() {
        assertEquals(longitude, event.getLongitude());
    }

    @Test
    void setLongitude() {
        float expected = 12.345678f;
        event.setLongitude(expected);
        assertEquals(expected, event.getLongitude());
    }

    @Test
    void getLatitude() {
        assertEquals(latitude, event.getLatitude());
    }

    @Test
    void setLatitude() {
        float expected = 12.345678f;
        event.setLatitude(expected);
        assertEquals(expected, event.getLatitude());
    }

    @Test
    void getImage() {
        assertEquals(image, event.getImage());
    }

    @Test
    void setImage() {
        String expected = "Pretty picture.";
        event.setImage(expected);
        assertEquals(expected, event.getImage());
    }

    @Test
    void getStartTime() {
        assertEquals(startTime, event.getStartTime());
    }

    @Test
    void setStartTime() {
        String expected = "starting now";
        event.setStartTime(expected);
        assertEquals(expected, event.getStartTime());
    }

    @Test
    void getEndTime() {
        assertEquals(endTime, event.getEndTime());
    }

    @Test
    void setEndTime() {
        String expected = "ending now";
        event.setEndTime(expected);
        assertEquals(expected, event.getEndTime());
    }

    @Test
    void getPeopleAvailable() {
        assertArrayEquals(people, event.getPeopleAvailable());
    }

    @Test
    void setPeopleAvailable() {
        Person friend2 = new Person(2, "John Smith", "lastSeen", true, "studying", 12.1235f, 1234123f, 1, "postal code","pfp");
        Person[] friends = new Person[]{friend, friend2};
        event.setPeopleAvailable(friends);
        assertEquals(friends, event.getPeopleAvailable());
    }

    @Test
    void getPeopleInvited() {
        assertArrayEquals(people, event.getPeopleInvited());
    }

    @Test
    void setPeopleInvited() {
        Person friend2 = new Person(2, "John Smith", "lastSeen", true, "studying", 12.1235f, 1234123f, 1, "postal code","pfp");
        Person[] friends = new Person[]{friend, friend2};
        event.setPeopleInvited(friends);
        assertArrayEquals(friends, event.getPeopleInvited());
    }

    @Test
    void getPeopleAccepted() {
        assertArrayEquals(people, event.getPeopleAccepted());
    }

    @Test
    void setPeopleAccepted() {
        Person friend2 = new Person(2, "John Smith", "lastSeen", true, "studying", 12.1235f, 1234123f, 1, "postal code","pfp");
        Person[] friends = new Person[]{friend, friend2};
        event.setPeopleAccepted(friends);
        assertArrayEquals(friends, event.getPeopleAccepted());
    }

    @Test
    void getPeopleSeen() {
        assertArrayEquals(people, event.getPeopleSeen());
    }

    @Test
    void setPeopleSeen() {
        Person friend2 = new Person(2, "John Smith", "lastSeen", true, "studying", 12.1235f, 1234123f, 1, "postal code","pfp");
        Person[] friends = new Person[]{friend, friend2};
        event.setPeopleSeen(friends);
        assertArrayEquals(friends, event.getPeopleSeen());
    }
}