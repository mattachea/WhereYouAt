package wya.models;

public class Person {
    private int identifier;
    private String fullName;
    private String lastSeen;
    private boolean live = true;
    private String status;
    private float longitude;
    private float latitude;
    private int availability;
    private String privacy;
    private String profilePicture;


    public Person() {
    }

    /**
     * Public constructor to create a Person model in the MVC.
     *
     * @param identifier   The identifier of the person.
     * @param fullName     The full name of the person.
     * @param lastSeen     The time stamp of the last time the person updated his/her time.
     * @param live         The boolean whether or not the person is active or inactive on the app.
     *                     true = online.
     *                     false = offline.
     * @param status       The status that the user wants to tell other users about what they are doing.
     * @param longitude    The longitude of their last logged location.
     * @param latitude     The latitude of their last logged location.
     * @param availability The availability of the user at the current time using the enumeration:
     *                     0 = available
     *                     1 = busy
     *                     2 = do not disturb
     * @param privacy      The privacy setting chosen by the user from the following set:
     *                     {"neighborhood", "postal_code", "locality" (city), "administrative_area_level_1" (state)}.
     */
    public Person(int identifier, String fullName, String lastSeen, boolean live, String status, float longitude, float latitude, int availability, String privacy, String profilePicture) {
        this.identifier = identifier;
        this.fullName = fullName;
        this.lastSeen = lastSeen;
        this.live = live;
        this.status = status;
        this.availability = availability;
        this.longitude = longitude;
        this.latitude = latitude;
        this.privacy = privacy;
        this.profilePicture = profilePicture;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public int getIdentifier() {
        return identifier;
    }

    public void setIdentifier(int identifier) {
        this.identifier = identifier;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public boolean isLive() {
        return live;
    }

    public void setLive(boolean live) {
        this.live = live;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public int getAvailability() {
        return availability;
    }

    public void setAvailability(int availability) {
        this.availability = availability;
    }

    public String getPrivacy() {
        return privacy;
    }

    public void setPrivacy(String privacy) {
        this.privacy = privacy;
    }

}
