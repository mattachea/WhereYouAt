class Sidebar extends React.Component {
    constructor(props) {
        super(props);
    }

    render() {
        const placeholder_pfp = "https://i.imgur.com/u31EyPE.jpg";
        const cardsRender = this.props.friends.map((friend) => {
            console.log(friend);
            if (!friend.profilePicture || friend.profilePicture === "") {
                friend.profilePicture = placeholder_pfp;
            }

            if (!friend.status || friend.status === "") {
                friend.status = "User has not updated their status.";
            }

            if (!friend.lastSeen || friend.lastSeen === "") {
                friend.lastSeenPrintout = "Could not find last seen time.";
            } else {
                let lastSeenDate = new Date(friend.lastSeen);
                let difference = Date.now() - lastSeenDate.getTime();

                let calc = "";

                if (difference < 1000 && difference >= 0) {
                    calc = difference.toString() + " ms";
                } else if (difference / 1000 < 60) {
                    calc = Math.floor(difference / 1000).toString() + " seconds";
                } else if (difference / 1000 / 60 < 60) {
                    calc = Math.floor(difference / 1000 / 60).toString() + " minutes";
                } else if (difference / 1000 / 60 / 60 < 24) {
                    calc = Math.floor(difference / 1000 / 60 / 60).toString() + " hours";
                } else if (difference / 1000 / 60 / 60 < 365) {
                    calc = Math.floor(difference / 1000 / 60 / 60 / 24).toString() + " days";
                } else if (difference / 1000 / 60 / 60 / 365 < 100) {
                    calc = Math.floor(difference / 1000 / 60 / 60 / 24 / 365).toString() + " years";
                } else {
                    calc = "NaN ms"
                }


                friend.lastSeenPrintout = "last seen " + calc + " ago";
            }

            return([
                <li className="wyacard">
                    <div className="pfp-container"><img src= {friend.profilePicture} alt={friend.fullName}/></div>
                    <div className="info">
                        <div className="name"><h2 className="name-text">{friend.fullName}</h2></div>
                        <div className="last-seen"><p className="last-seen-text"><i>{friend.lastSeenPrintout}</i></p></div>
                        <div className="status"><p className="status-text">{friend.status}</p>
                        </div>
                    </div>
                </li>]);
        });


        return (
            <div className="side">
                <div className="side-header">
                    <h1 className="side-header-text">{this.props.location}</h1>
                </div>
                <ul className="card-list">
                    {cardsRender}
                </ul>
            </div>
        );
    }
}