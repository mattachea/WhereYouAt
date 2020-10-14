package wya.models;

public class Event {
    private int identifier;
    private String name;
    private String description;
    private String place;
    private float longitude;
    private float latitude;
    private String image;       //images will be links
    private String startTime;   //according to ISO8601 YYYY-MM-DDTHH:MM:SS.SSSXXX
    private String endTime;     //according to ISO8601 YYYY-MM-DDTHH:MM:SS.SSSXXX
    //    private Person host;
    private Person[] peopleAvailable;
    private Person[] peopleInvited;
    private Person[] peopleAccepted;
    private Person[] peopleSeen;

    public Event() {
    }

    /**
     * Event constructor in MVC.
     *
     * @param identifier      PK in events table.
     * @param name            Title of the event.
     * @param description     Description of the event.
     * @param place           String representation of the location of the event.
     * @param longitude       Longitude of the location of the event.
     * @param latitude        Latitude of the location of the event.
     * @param image           Image of the event.
     * @param startTime       Start time of the event.
     * @param endTime         End time of the event.
     * @param peopleAvailable Friends available to attend.
     * @param peopleInvited   Friends invited to attend.
     * @param peopleAccepted  Friends who have accepted the event.
     * @param peopleSeen      Friend who have seen the event.
     */
    public Event(int identifier, String name, String description, String place, float longitude, float latitude, String image, String startTime, String endTime, Person[] peopleAvailable, Person[] peopleInvited, Person[] peopleAccepted, Person[] peopleSeen) {
        this.identifier = identifier;
        this.name = name;
        this.description = description;
        this.place = place;
        this.longitude = longitude;
        this.latitude = latitude;
        this.image = image;
        this.startTime = startTime;
        this.endTime = endTime;
        this.peopleAvailable = peopleAvailable;
        this.peopleInvited = peopleInvited;
        this.peopleAccepted = peopleAccepted;
        this.peopleSeen = peopleSeen;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Person[] getPeopleAvailable() {
        return peopleAvailable;
    }

    public void setPeopleAvailable(Person[] peopleAvailable) {
        this.peopleAvailable = peopleAvailable;
    }

    public Person[] getPeopleInvited() {
        return peopleInvited;
    }

    public void setPeopleInvited(Person[] peopleInvited) {
        this.peopleInvited = peopleInvited;
    }

    public Person[] getPeopleAccepted() {
        return peopleAccepted;
    }

    public void setPeopleAccepted(Person[] peopleAccepted) {
        this.peopleAccepted = peopleAccepted;
    }

    public Person[] getPeopleSeen() {
        return peopleSeen;
    }

    public void setPeopleSeen(Person[] peopleSeen) {
        this.peopleSeen = peopleSeen;
    }

//    public Person getHost() {
//        return host;
//    }
//
//    public void setHost(Person creator) {
//        this.host = creator;
//    }
}