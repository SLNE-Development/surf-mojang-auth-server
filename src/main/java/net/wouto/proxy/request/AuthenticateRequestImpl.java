package net.wouto.proxy.request;

public class AuthenticateRequestImpl {

    private String username, password, clientToken;

    public AuthenticateRequestImpl(String username, String password, String clientToken) {
        this.username = username;
        this.password = password;
        this.clientToken = clientToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
