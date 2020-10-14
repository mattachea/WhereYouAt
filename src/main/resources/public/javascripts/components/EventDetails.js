class EventDetails extends React.Component {

    constructor(props) {
        super(props);
        this.state = {
            myFriends: ["Matt", "Vincent", "Gary"],
        };
    }



    componentDidMount() {
        fetch("/friends")
            .then(res => res.json())
            .then(json => this.setState({ myFriends: json }));
    }

    handleSelect = async(eventID, personID) => {
        const formData = new FormData();
        formData.append("personID", personID);
        formData.append("eventID", eventID);
        await fetch("eventAttendance", {method: "POST", body: formData});
        alert("Added to event");

    };

    render() {

        const eventInfo = this.props.eventInfo;
        console.log(eventInfo);
        console.log(eventInfo.name);
        return (
            <div className="side">
                <div className="side-header">
                    <h1 className="side-header-text">Event Details</h1>
                </div>

                <div className="event-list">
                    <div className="event-card">
                        <div className="event-name">
                            <h2 className="event-name-text">{eventInfo.name}</h2>
                        </div>

                        <div className="event-details">
                            <p>Description: <b>{eventInfo.description}</b></p>
                            <p>Address: <b>{eventInfo.place}</b></p>
                            <p>Start Time: <b>{eventInfo.startTime}</b></p>

                            <div>
                                {/*<DropdownButton title = "Add Friends" onSelect={(e) => {alert(e + " added to Event")}}>*/}
                                <DropdownButton title = "Add Friends" onSelect={(e) => {this.handleSelect(eventInfo.identifier, e)}}>

                                    {this.state.myFriends.map(person => (
                                        <Dropdown.Item as="button" eventKey={person.identifier}> {person.fullName} </Dropdown.Item>
                                    ))}
                                </DropdownButton>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}