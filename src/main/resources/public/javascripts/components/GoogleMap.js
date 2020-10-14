class GoogleMap extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            userLocation: {
                lat: 0,
                lng: 0
            },
            myFriends: [],
            allEvents: [],
            privacy: "neighborhood",
            map: null
            //privacy options are "neighborhood", "postal_code", "locality" (city), "administrative_area_level_1" (state)
        };

        this.populateSidebar = this.populateSidebar.bind(this);
    }

    async getFriendsFromServer() {
        this.setState({ myFriends: await (await fetch("/friends")).json() });


        console.table(this.state.myFriends);
        //add friends markers to the map

        let markers = this.resolveMarkerLabels(this.bucketize(this.state.myFriends));

        for (const marker of markers) {
            this.getNameArea(marker, this.populateSidebar, this.map);
        }
    }

    resolveMarkerLabels(markers) {
        for (const marker of markers) {
            if (marker.numPeople > 0) {
                marker.label = marker.numPeople.toString();
            }
        }
        return markers;
    }

    bucketize(friends) {
        let markers = [];
        for (const friend of friends) {
            let duplicate = false;
            for (const marker of markers) {
                if (friend.latitude === marker.latitude && friend.longitude === marker.longitude) {
                    duplicate = true;
                    marker.numPeople++;
                    break;
                }
            }

            if (!duplicate) {
                let marker = {
                    label: friend.fullName,
                    numPeople: 1,
                    latitude: friend.latitude,
                    longitude: friend.longitude,
                    privacy: friend.privacy
                };
                markers.push(marker);
            }
        }
        return markers;
    }

    //Rounding due to weird floating point errors from GoogleMaps
    roundCoordinate(n) {
        return Math.floor(n * (10 ** 5));
    }

    sameCoordinate(latA, lngA, latB, lngB) {
        return Math.abs(latA - latB) < 5 && Math.abs(lngA - lngB) < 5;
    }

    populateSidebar(lat, lng, location) {
        const roundedLat = this.roundCoordinate(lat);
        const roundedLng = this.roundCoordinate(lng);

        let clickedFriends = [];
        for (const friend of this.state.myFriends) {
            const roundedFriendLat = this.roundCoordinate(friend.latitude);
            const roundedFriendLng = this.roundCoordinate(friend.longitude);
            if (this.sameCoordinate(roundedLat, roundedLng, roundedFriendLat, roundedFriendLng)) {
                clickedFriends.push(friend);
            }
        }
        this.props.updateSidebar(SIDEBAR_STATE.cardList, clickedFriends, location);
    }

    getLocation() {
        //updating current location
        if (navigator && navigator.geolocation) {
            navigator.geolocation.getCurrentPosition((pos) => {
                this.setState({userLocation: {lat: pos.coords.latitude, lng: pos.coords.longitude}});

                var myLocation = {lat: pos.coords.latitude, lng: pos.coords.longitude};

                //create map centered at myLocation
                this.state.map = new window.google.maps.Map(document.getElementById('map'), {
                    center: myLocation,
                    zoom: 11,
                    disableDefaultUI: true
                });

                this.map = this.state.map;

                //add user's marker to map at myLocation
                new window.google.maps.Marker({
                    position: myLocation,
                    map: this.map,
                    label: "You",
                    icon: {
                        scaledSize: new google.maps.Size(54/2, 86/2),
                        size: new google.maps.Size(54/2, 86/2),
                        labelOrigin: new google.maps.Point(54/4, 15),
                        url: "https://imgur.com/zTokiQB.png"
                    }
                });

                // console.log("me: " +  myLocation.lat);
                // console.log("me: " +  myLocation.lng);

                this.getFriendsFromServer();
                this.reverseGeocode(myLocation);
            })
        }
    }

    async componentDidMount() {
        await this.getLocation();
        console.log(this.props.range);
        await this.getEventFromServer();
    };

    getNameArea(marker, popSidebar, map) {
        let geocoder = new window.google.maps.Geocoder;

        let uplift = function (address) {
            const m = new window.google.maps.Marker({
                position: {lat: marker.latitude, lng: marker.longitude},
                map: map,
                label: marker.label,
                title: address
            });

            window.google.maps.event.addDomListener(m, 'click', () => {
                popSidebar(m.getPosition().lat(), m.getPosition().lng(), m.getTitle());
            });
        };

        geocoder.geocode({'location': {lat: marker.latitude, lng: marker.longitude}}, (results, status) => {
            if (status === 'OK') {
                if (results[0]) {
                    let flag = false;
                    for (var i in results[0].address_components) {
                        if (marker.privacy === results[0].address_components[i].types[0]) {
                            var address = (results[0].address_components[i].long_name).toString();
                            uplift(address);
                            flag = true;
                            break;
                        }
                    }
                    if (!flag) {
                        uplift("Location");
                    }
                } else {
                    console.trace('reverseGeocoder: No results found');
                }
            } else {
                console.trace('reverseGeocoder failed due to: ' + status);
            }
        });
    }


    //reverse geocode: latlng to approx place, then get latlng of approx place
    async reverseGeocode(myLocation) {
        this.setState({privacy: await (await fetch("/privacy")).json()});
        let geocoder = new window.google.maps.Geocoder;

        geocoder.geocode({'location': myLocation}, (results, status) => {
            if (status === 'OK') {
                if (results[0]) {
                    for (var i in results[0].address_components) {
                        if (this.state.privacy === results[0].address_components[i].types[0]) {
                            var address = (results[0].address_components[i].long_name).toString();

                            //get lat and lng from place
                            //console.log("reverseGeocode: " + address);
                            this.geocode(address)
                        }
                    }
                } else {
                    console.trace('reverseGeocoder: No results found');
                }
            } else {
                console.trace('reverseGeocoder failed due to: ' + status);
            }
        });


    }

    //gets lat and long given the address
    geocode(address) {
        let geocoder = new window.google.maps.Geocoder;
        console.log("Geocode: " + address);
        geocoder.geocode( { 'address': address}, (results, status) => {
            if (status == 'OK') {

                // //puts marker at approx location (for debugging)
                // var marker = new window.google.maps.Marker({
                //                     map: this.map,
                //                     position: results[0].geometry.location,
                //                     label: "approx"
                //                 });

                const location = results[0].geometry.location;
                console.log("geocode: " +  location.lat());
                console.log("geocode: " +  location.lng());

                //send approx latlng to database
                const formData = new FormData();
                formData.append("latitude", location.lat());
                formData.append("longitude", location.lng());
                fetch("location", {method: "PUT", body: formData})

                //send time to database
                this.updateTime();

            } else {
                console.trace('Geocode was not successful for the following reason: ' + status);
            }
        });

    }

    async updateTime() {
        var tempDate = new Date();
        var date = tempDate.getFullYear() + '-' + (tempDate.getMonth()+1) + '-' + tempDate.getDate() +' '+ tempDate.getHours()+':'+ tempDate.getMinutes()+':'+ tempDate.getSeconds();
        //console.log(date);
        const formData = new FormData();
        formData.append("time", date);
        await fetch("time", {method: "PUT", body: formData})
    }

    async getEventFromServer() {
        this.setState({ allEvents: await (await fetch("/event")).json() });

        this.state.allEvents.map((event)=>{this.displayEvent(event)});
    }

    displayEvent=(event)=> {
        let lat = event.latitude;
        let lng = event.longitude;
        const event_pos = {lat, lng};

        let m = new window.google.maps.Marker({
            position: event_pos,
            map: this.state.map,
            label: event.name,
            customInfo: event,
            icon: {
                scaledSize: new google.maps.Size(54/2, 86/2),
                size: new google.maps.Size(54/2, 86/2),
                labelOrigin: new google.maps.Point(54/4, 15),
                url: 'https://imgur.com/QlTYn3K.png'
            }
        });

        window.google.maps.event.addDomListener(m, 'click', () => {
            this.populateEvent(m.customInfo);
        });

    };

    populateEvent =(eventInfo)=> {
        this.props.updateSidebar(SIDEBAR_STATE.eventDetails, null, null, eventInfo);
    };


    render() {
        return (
            <div style={{ width: "100%", height: "calc(100% - 32px)" }} id="map" />
        );
    }
}