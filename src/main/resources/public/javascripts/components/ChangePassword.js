class ChangePassword extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            oldPass: '',
            newPass: '',
            newPassRepeat: '',

            /* 0 is default, 1 is success, 2 is DB error. */
            formSubmitted: 0,
            passMatch: true,
            emptySubmit: false,
            wrongPass: false
        }
    }

    /* Function for actual for submission. */
    async submitForm() {
        const formData = new FormData();
        formData.append("oldpassword", this.state.oldPass);
        formData.append("newpassword", this.state.newPass);
        await fetch("changePassword", {method: "PUT", body: formData})
            .then(function (response) {
                if (response.status === 403) {
                    this.setState({wrongPass: true});
                }
                if (response.status === 400) {
                    this.setState({formSubmitted: 2});
                }
            }.bind(this));
        // console.trace("Submitted updated form data.");
    }

    async handleSubmit() {
        document.getElementById('input').value = '';
        document.getElementById('input1').value = '';
        document.getElementById('input2').value = '';
        await this.resetErrStates();
        let err = await this.basicErrCheck();
        if (err) {
            return;
        }

        await this.submitForm();
        if (this.state.wrongPass) {
            return;
        }
        if (this.state.formSubmitted === 2) {
            return;
        }
        this.resetInputStates();

        this.setState({formSubmitted: 1});
    }

    resetErrStates() {
        this.setState({emptySubmit: false});
        this.setState({passMatch: true});
        this.setState({formSubmitted: 0});
        this.setState({wrongPass: false});
    }

    resetInputStates() {
        this.setState({oldPass: ''});
        this.setState({newPass: ''});
        this.setState({newPassRepeat: ''})
    }

    basicErrCheck() {
        if (this.state.oldPass === '' || this.state.newPass === '' || this.state.newPassRepeat === '') {
            this.setState({emptySubmit: true});
            return true;
        }
        if (this.state.newPass !== this.state.newPassRepeat) {
            this.setState({passMatch: false});
            this.resetInputStates();
            return true;
        }
        return false;
    }

    render() {

        const emptySubmitMessage = <h6>Please fill ALL fields!</h6>;
        const successfulSubmitMessage = <h6>Password Changed Successfully!</h6>;
        const dbErrMessage = <h6>Something went wrong in the backend...</h6>;
        const noMatchMessage = <h6>Passwords do not match</h6>;
        const wrongPassMessage = <h6>Wrong password!</h6>;

        return(
            <div className="side" style={{padding: '25px'}}>
                <header>
                    <h5>Please enter BOTH your old password AND a new password!</h5>
                </header>

                <div className={"Input Old Password"}>
                    <Form.Label>Input old Password</Form.Label>
                    <InputGroup className="mb-3">
                        <Form.Control
                            placeholder="Please enter your old password"
                            aria-label="Enter old pass"
                            id="input"
                            type="password"
                            onChange={(e) => {
                                this.setState({oldPass: e.target.value})
                            }}
                        />
                    </InputGroup>
                </div>

                <div className={"Input New Password"}>
                    <Form.Label>Input new Password</Form.Label>
                    <InputGroup className="mb-3">
                        <Form.Control
                            placeholder="Please enter a new password"
                            aria-label="Enter new pass"
                            id="input1"
                            type="password"
                            onChange={(e) => {
                                this.setState({newPass: e.target.value})
                            }}
                        />
                    </InputGroup>
                </div>

                <div className={"Input New Password Again"}>
                    <Form.Label>Retype your new password</Form.Label>
                    <InputGroup className="mb-3">
                        <Form.Control
                            placeholder="Please enter new password again"
                            aria-label="Enter new pass again"
                            id="input2"
                            type="password"
                            onChange={(e) => {
                                this.setState({newPassRepeat: e.target.value})
                            }}
                        />
                    </InputGroup>
                </div>

                <div>
                    <Button onClick={this.handleSubmit.bind(this)}>Submit</Button>
                    {this.state.emptySubmit === true ? emptySubmitMessage : null}
                    {this.state.formSubmitted === 1 ? successfulSubmitMessage : null}
                    {this.state.formSubmitted === 2 ? dbErrMessage : null}
                    {this.state.passMatch === false ? noMatchMessage: null}
                    {this.state.wrongPass === true ? wrongPassMessage: null}
                </div>

            </div>
        )
    }
}
