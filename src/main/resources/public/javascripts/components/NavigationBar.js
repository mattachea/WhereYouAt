var Nav = ReactBootstrap.Nav;

const SIDEBAR_STATE = {
    closed: 0,
    createEvent: 1,
    yourEvents: 2,
    cardList: 3,
    settings: 4,
};

class NavigationBar extends React.Component {
    render(){
        return (
            <Nav
                activeKey="/home"
                onSelect={selectedKey => {switch(selectedKey) {
                    case "HOME":
                    default:
                        this.props.updateSidebar(SIDEBAR_STATE.closed);
                        break;
                    case "CREATE EVENT":
                        this.props.updateSidebar(SIDEBAR_STATE.createEvent);
                        break;
                    case "YOUR EVENTS":
                        this.props.updateSidebar(SIDEBAR_STATE.yourEvents);
                        break;
                    case "SETTINGS":
                        this.props.updateSidebar(SIDEBAR_STATE.settings);
                        break;
                }}}
            >
                <Nav.Item>
                    <Nav.Link eventKey="HOME">Home</Nav.Link>
                </Nav.Item>
                <Nav.Item>
                    <Nav.Link eventKey="CREATE EVENT">Create an Event</Nav.Link>
                </Nav.Item>
                <Nav.Item>
                    <Nav.Link eventKey="YOUR EVENTS"> Your Events</Nav.Link>
                </Nav.Item>
                <Nav.Item>
                    <Nav.Link eventKey="SETTINGS">Settings</Nav.Link>
                </Nav.Item>
            </Nav>

        );
    }

}


