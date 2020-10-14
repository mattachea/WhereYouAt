class LoginPage extends React.Component {
    constructor(props) {
        super(props);
        this.state = {
            username: '',
            password: '',
            login_time:'',
            failed_login: false,
            logged_in: false,
            username_not_found: false,
            empty_entries: false
        }
    }

    handleLogin = (e) => {
        e.preventDefault();
        e.stopPropagation();
        console.log(this.state.username);
        console.log(this.state.password);
        const formData = new FormData();
        formData.append("username", this.state.username);
        formData.append("password", this.state.password);
        if (this.state.username && this.state.password) {
            fetch("/login", {method: "POST", body: formData}).then(function (response) {
                if (response.status === 403 || response.status == 404) {
                    this.setState({failed_login: true});
                    this.props.logIn(false);
                } else {
                    console.log("correct password");
                    this.setState({logged_in: true});
                    this.setState({failed_login: false});
                    this.props.logIn(true);
                }
            }.bind(this));
        }
    }

    handleClick = () => {
        this.props.signUp(true);
    }

    render(){
        return (
            <div className="login-wrapper">
                <div className="login-div">
                    <div className="login-side">
                        <form className="login-form" onSubmit={this.handleLogin}>
                            <input required type="text" placeholder="username" onChange={(event)=>this.setState({username: event.target.value})}/>
                            <input required type="password" placeholder="password" onChange={(event)=>this.setState({password: event.target.value})}/>
                            <div className="login-buttons">
                                <button className="wya-button" type="submit">Log In</button>
                                <button className="wya-button" onClick={this.handleClick}>Sign Up</button>
                            </div>
                        </form>
                        {this.state.failed_login ? <div className="error-message">Wrong username or password, please try again</div>  : null}
                    </div>
                    <div className="logo">
                        <h1 className="logo-text">Where<br/>You<br/>At</h1>
                    </div>
                </div>
            </div>
        );
    }

}


