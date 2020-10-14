//var Image = ReactBootstrap.Image;
// import React from "react";

var DropdownButton = ReactBootstrap.DropdownButton;
var SplitButton = ReactBootstrap.SplitButton;
var Dropdown = ReactBootstrap.Dropdown;
var Form = ReactBootstrap.Form;
var Button = ReactBootstrap.Button;
var InputGroup = ReactBootstrap.InputGroup;

class Settings extends React.Component {
    constructor(props) {
        super(props);
        this.goToChangePass = this.goToChangePass.bind(this);
        this.state = {
            /** Person props. */
            identifier: 0,
            fullName: "",
            lastSeen: "",
            live: true,
            status: "",
            longitude: 0,
            latitude: 0,
            availability: 0,
            privacy: "",
            email: "",
            profilePicture: "",

            editName: "",
            editStatus: "",
            editEmail: "",
            editPic: "",

            selectedAvailBut: null,

            /* 0 means nothing, 1 means success, 2 means random DB fail, 3 means empty submit. */
            nameSuccess: 0,
            statusSuccess: 0,
            emailSuccess: 0,
            picSuccess: 0,

            failedSubmit: false,
            emptySubmit: false
        };
        this.getDBPersonState.bind(this);
    }

    /** Fetches privacy data before the page renders! */
    componentDidMount() {
        const that = this;
        fetch("/profile")
            .then(function (response) {
                return response.json();
            })
            .then(function (jsonData) {
                that.setState({privacy: jsonData['privacy']});
                that.setState({live: jsonData['live']});
            });
    }

    /** Fetch and set default person props.
     *  Call this everytime you're about to update state of a person
     * */
    async getDBPersonState() {
        await this.resetSuccessHandlers();
        let profile = await (await fetch("/profile")).json();

        /** Set state based on information from "profile". */
        this.setState({"identifier": profile['identifier']});
        this.setState({"fullName": profile['fullName']});
        this.setState({"lastSeen": profile['lastSeen']});
        this.setState({"live": profile['live']});
        this.setState({"status": profile['status']});
        this.setState({"longitude": profile['longitude']});
        this.setState({"latitude": profile['latitude']});
        this.setState({"availability": profile['availability']});
        this.setState({"privacy": profile['privacy']});

        // console.trace("Updated person state data from server");
    }

    /** Fetch and set default user props.
     *  call everytime before update state of user.
     * */
    async getDBUserState() {
        await this.resetSuccessHandlers();
        let user = await (await fetch("/updateAccount", {method: "GET"})).json();
        /** Set state based on information from "profile". */
        this.setState({"email": user['email']});
        this.setState({"profilePicture": user['profilePicture']});
    }

    /* Function for actual for submission. */
    async submitProfileForm() {
        const formData = new FormData();
        formData.append("identifier", this.state.identifier);
        formData.append("fullName", this.state.fullName);
        formData.append("lastSeen", this.state.lastSeen);
        formData.append("live", this.state.live);
        formData.append("status", this.state.status);
        formData.append("longitude", this.state.longitude);
        formData.append("latitude", this.state.latitude);
        formData.append("availability", this.state.availability);
        formData.append("privacy", this.state.privacy);
        await fetch("updateProfile", {method: "PUT", body: formData})
            .then(function (response) {
                if (response.status !== 204) {
                    this.setState({failedSubmit: true})
                }
            }.bind(this));
        // console.trace("Submitted updated form data.");
    }

    async submitUserForm() {
        const formData = new FormData();
        formData.append("email", this.state.email);
        formData.append("profilePicture", this.state.profilePicture);
        await fetch("updateAccount", {method: "PUT", body: formData})
            .then(function (response) {
                if (response.status !== 204) {
                    this.setState({failedSubmit: true})
                }
            }.bind(this));
        // console.trace("Submitted updated user form data.");
    }

    /** Dropdown handlers! */
    async handleAvailabilityUpdate(num) {
        await this.getDBPersonState();
        /* Check if there was no submission data. */
        this.setState({availability: num});
        await this.submitProfileForm();
    }

    async handlePrivacyUpdate(str) {
        await this.getDBPersonState();
        /* Check if there was no submission data. */
        this.setState({privacy: str});
        await this.submitProfileForm();
    }

    /* Form input Handlers! */
    async handleNameUpdate() {
        await this.getDBPersonState();
        /* Check if there was no submission data. */
        if (this.state.editName === "") {
            this.setState({nameSuccess: 3});
            return;
        }
        this.setState({fullName: this.state.editName});
        this.setState({editName: ""});
        this.submitProfileForm();
        if (this.state.failedSubmit) {
            this.setState({nameSuccess: 2});
            return;
        }
        document.getElementById('Edit_Name').value = '';
        this.setState({nameSuccess: 1});
    }

    async handleStatusUpdate() {
        await this.getDBPersonState();
        /* Check if there was no submission data. */
        if (this.state.editStatus === "") {
            this.setState({statusSuccess: 3});
            return;
        }
        this.setState({status: this.state.editStatus});
        this.setState({editStatus: ""});
        this.submitProfileForm();
        if (this.state.failedSubmit) {
            this.setState({statusSuccess: 2});
            return;
        }
        document.getElementById('Edit_Status').value = '';
        this.setState({statusSuccess: 1});
    }

    async handleEmailUpdate() {
        await this.getDBUserState();
        /* Check if there was no submission data. */
        if (this.state.editEmail === "") {
            this.setState({emailSuccess: 3});
            return;
        }
        this.setState({email: this.state.editEmail});
        this.setState({editEmail: ""});
        this.submitUserForm();
        if (this.state.failedSubmit) {
            this.setState({emailSuccess: 2});
            return;
        }
        document.getElementById('Edit_Email').value = '';
        this.setState({emailSuccess: 1});
    }

    async handleProfPicUpdate() {
        await this.getDBUserState();
        /* Check if there was no submission data. */
        if (this.state.editPic === "") {
            this.setState({picSuccess: 3});
            return;
        }
        this.setState({profilePicture: this.state.editPic});
        this.setState({editPic: ""});
        this.submitUserForm();
        if (this.state.failedSubmit) {
            this.setState({picSuccess: 2});
            return;
        }
        document.getElementById('Edit_Picture').value = '';
        this.setState({picSuccess: 1});
    }

    async changeLive() {
        await this.getDBPersonState();
        this.setState({live: !this.state.live});
        await this.submitProfileForm();
    }

    /* Helper functs. */

    resetSuccessHandlers() {
        this.setState({nameSuccess: 0});
        this.setState({statusSuccess: 0});
        this.setState({emailSuccess: 0});
        this.setState({picSuccess: 0});
        this.setState({failedSubmit: false});
    }

    goToChangePass() {
        this.props.updateSidebar(SIDEBAR_STATE.changePass);
    }

    render() {

        const successMessage = <h6>Information Submitted!</h6>;

        const emptySubmitMessage = <h6>Please do not submit an empty field!</h6>;

        const failedSubmitMessage = <h6>Something went wrong with submission...</h6>;

        /** Render different selected AVAILABILITY dropdowns depending on availability state. */
        const selectedAvailAvailable = <SplitButton id="availability" title="Availability">
            <Dropdown.Item active as="button"
                           onClick={this.handleAvailabilityUpdate.bind(this, 0)}> Available </Dropdown.Item>
            <Dropdown.Item as="button" onClick={this.handleAvailabilityUpdate.bind(this, 1)}> Busy </Dropdown.Item>
            <Dropdown.Item as="button" onClick={this.handleAvailabilityUpdate.bind(this, 2)}> Do not
                disturb </Dropdown.Item>
        </SplitButton>;
        const selectedAvailBusy = <SplitButton id="availability" title="Availability">
            <Dropdown.Item as="button" onClick={this.handleAvailabilityUpdate.bind(this, 0)}> Available </Dropdown.Item>
            <Dropdown.Item active as="button"
                           onClick={this.handleAvailabilityUpdate.bind(this, 1)}> Busy </Dropdown.Item>
            <Dropdown.Item as="button" onClick={this.handleAvailabilityUpdate.bind(this, 2)}> Do not
                disturb </Dropdown.Item>
        </SplitButton>;
        const selectedAvailDnD = <SplitButton id="availability" title="Availability">
            <Dropdown.Item as="button" onClick={this.handleAvailabilityUpdate.bind(this, 0)}> Available </Dropdown.Item>
            <Dropdown.Item as="button" onClick={this.handleAvailabilityUpdate.bind(this, 1)}> Busy </Dropdown.Item>
            <Dropdown.Item active as="button" onClick={this.handleAvailabilityUpdate.bind(this, 2)}> Do not
                disturb </Dropdown.Item>
        </SplitButton>;


        /** Render different selected PRIVACY dropdowns depending on privacy state. */
        const selectedPrivNeighborhood = <SplitButton id="privacy" title="Privacy Range">
            <Dropdown.Item active as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "neighborhood")}> Neighborhood </Dropdown.Item>
            <Dropdown.Item as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "postal_code")}> Town </Dropdown.Item>
            <Dropdown.Item as="button" onClick={this.handlePrivacyUpdate.bind(this, "locality")}> City </Dropdown.Item>
            <Dropdown.Item as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "administrative_area_level_1")}> State </Dropdown.Item>
        </SplitButton>;
        const selectedPrivTown = <SplitButton id="privacy" title="Privacy Range">
            <Dropdown.Item as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "neighborhood")}> Neighborhood </Dropdown.Item>
            <Dropdown.Item active as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "postal_code")}> Town </Dropdown.Item>
            <Dropdown.Item as="button" onClick={this.handlePrivacyUpdate.bind(this, "locality")}> City </Dropdown.Item>
            <Dropdown.Item as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "administrative_area_level_1")}> State </Dropdown.Item>
        </SplitButton>;
        const selectedPrivCity = <SplitButton id="privacy" title="Privacy Range">
            <Dropdown.Item as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "neighborhood")}> Neighborhood </Dropdown.Item>
            <Dropdown.Item as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "postal_code")}> Town </Dropdown.Item>
            <Dropdown.Item active as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "locality")}> City </Dropdown.Item>
            <Dropdown.Item as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "administrative_area_level_1")}> State </Dropdown.Item>
        </SplitButton>;
        const selectedPrivState = <SplitButton id="privacy" title="Privacy Range">
            <Dropdown.Item as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "neighborhood")}> Neighborhood </Dropdown.Item>
            <Dropdown.Item as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "postal_code")}> Town </Dropdown.Item>
            <Dropdown.Item as="button" onClick={this.handlePrivacyUpdate.bind(this, "locality")}> City </Dropdown.Item>
            <Dropdown.Item active as="button"
                           onClick={this.handlePrivacyUpdate.bind(this, "administrative_area_level_1")}> State </Dropdown.Item>
        </SplitButton>;


        return (
            <div className="side" style={{padding: '25px'}}>
                <header>
                    <h1> Settings </h1>
                </header>

                {this.state.availability === 0 ? selectedAvailAvailable : null}
                {this.state.availability === 1 ? selectedAvailBusy : null}
                {this.state.availability === 2 ? selectedAvailDnD : null}


                {this.state.privacy === "neighborhood" ? selectedPrivNeighborhood : null}
                {this.state.privacy === "postal_code" ? selectedPrivTown : null}
                {this.state.privacy === "locality" ? selectedPrivCity : null}
                {this.state.privacy === "administrative_area_level_1" ? selectedPrivState : null}

                <div className={"Live"}>
                    <Form>
                        <Form.Check
                            type="switch"
                            id="custom-switch"
                            label="Live Status"
                            checked={this.state.live}
                            onChange={this.changeLive.bind(this)}
                        />
                    </Form>
                </div>

                <div className={"Edit Name"}>
                    <Form.Label>Name</Form.Label>
                    <InputGroup className="mb-3">
                        <Form.Control
                            placeholder="Edit your name"
                            aria-label="Edit your name"
                            id="Edit_Name"
                            // aria-describedby="basic-addon2"
                            onChange={(e) => {
                                this.setState({editName: e.target.value})
                            }}
                        />
                        <InputGroup.Append>
                            <Button variant="outline-secondary"
                                    type="submit"
                                    onClick={this.handleNameUpdate.bind(this)}
                            >Submit</Button>
                        </InputGroup.Append>
                    </InputGroup>
                    {this.state.nameSuccess === 1 ? successMessage : null}
                    {this.state.nameSuccess === 2 ? failedSubmitMessage : null}
                    {this.state.nameSuccess === 3 ? emptySubmitMessage : null}
                </div>

                <div className={"Edit Status"}>
                    <Form.Label>Status</Form.Label>
                    <InputGroup className="mb-3">
                        <Form.Control
                            placeholder="Edit your status"
                            aria-label="Edit your status"
                            id="Edit_Status"
                            // aria-describedby="basic-addon2"
                            onChange={(e) => {
                                this.setState({editStatus: e.target.value})
                            }}
                        />
                        <InputGroup.Append>
                            <Button variant="outline-secondary"
                                    type="submit"
                                    onClick={this.handleStatusUpdate.bind(this)}
                            >Submit</Button>
                        </InputGroup.Append>
                    </InputGroup>
                    {this.state.statusSuccess === 1 ? successMessage : null}
                    {this.state.statusSuccess === 2 ? failedSubmitMessage : null}
                    {this.state.statusSuccess === 3 ? emptySubmitMessage : null}
                </div>

                <div className={"Edit Email"}>
                    <Form.Label>Email</Form.Label>
                    <InputGroup className="mb-3">
                        <Form.Control
                            placeholder="Edit your Email"
                            aria-label="Edit your Email"
                            id="Edit_Email"
                            // aria-describedby="basic-addon2"
                            onChange={(e) => {
                                this.setState({editEmail: e.target.value})
                            }}
                        />
                        <InputGroup.Append>
                            <Button variant="outline-secondary"
                                    type="submit"
                                    onClick={this.handleEmailUpdate.bind(this)}
                            >Submit</Button>
                        </InputGroup.Append>
                    </InputGroup>
                    {this.state.emailSuccess === 1 ? successMessage : null}
                    {this.state.emailSuccess === 2 ? failedSubmitMessage : null}
                    {this.state.emailSuccess === 3 ? emptySubmitMessage : null}
                </div>

                <div className={"Edit ProfPic link"}>
                    <Form.Label>Profile Picture</Form.Label>
                    <InputGroup className="mb-3">
                        <Form.Control
                            placeholder="Edit your Picture"
                            aria-label="Edit your Picture"
                            id="Edit_Picture"
                            // aria-describedby="basic-addon2"
                            onChange={(e) => {
                                this.setState({editPic: e.target.value})
                            }}
                        />
                        <InputGroup.Append>
                            <Button variant="outline-secondary"
                                    type="submit"
                                    onClick={this.handleProfPicUpdate.bind(this)}
                            >Submit</Button>
                        </InputGroup.Append>
                    </InputGroup>
                    {this.state.picSuccess === 1 ? successMessage : null}
                    {this.state.picSuccess === 2 ? failedSubmitMessage : null}
                    {this.state.picSuccess === 3 ? emptySubmitMessage : null}
                </div>

                <Button onClick={this.goToChangePass}>Change Password</Button>

            </div>

        );
    }
}
