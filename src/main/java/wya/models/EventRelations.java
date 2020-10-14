package wya.models;

public class EventRelations {
    private int eventID;
    private int personID;
//    private int interest;

    public EventRelations() {
    }

    public EventRelations(int eventID, int personID
//            , int interest
    ) {
        this.eventID = eventID;
        this.personID = personID;
        /* We should make this 0 1 or 2.
           0 for not going
           1 for interested
           2 for going
         */
//        this.interest = interest;
    }

    public int getEventID() {
        return eventID;
    }

    public void setEventID(int eventID) {
        this.eventID = eventID;
    }

    public int getPersonID() {
        return personID;
    }

    public void setPersonID(int personID) {
        this.personID = personID;
    }

//    public int getInterest() {
//        return interest;
//    }
//
//    public void setInterest(int interest) {
//        this.interest = interest;
//    }
}