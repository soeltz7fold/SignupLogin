package x7a.droid.signuplogin;

/**
 * Created by DroiD on 20/04/2016.
 */
public class User {
    private String id;
    private String email;
    private String password;
    private String token_authentication;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken_authentication() {
        return token_authentication;
    }

    public void setToken_authentication(String token_authentication) {
        this.token_authentication = token_authentication;
    }
}
