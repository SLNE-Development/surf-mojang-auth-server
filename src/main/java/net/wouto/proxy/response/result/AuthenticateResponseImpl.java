package net.wouto.proxy.response.result;

import com.mojang.authlib.GameProfile;

public class AuthenticateResponseImpl {

    private String accessToken;
    private String clientToken;
    private BasicGameProfile selectedProfile;
    private BasicGameProfile[] activeProfiles;

    public AuthenticateResponseImpl(String accessToken, BasicGameProfile selectedProfile, BasicGameProfile[] activeProfiles, String clientToken) {
        this.accessToken = accessToken;
        this.selectedProfile = selectedProfile;
        this.activeProfiles = activeProfiles;
        this.clientToken = clientToken;
    }

    public String getClientToken() {
        return clientToken;
    }

    public void setClientToken(String clientToken) {
        this.clientToken = clientToken;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public BasicGameProfile getSelectedProfile() {
        return selectedProfile;
    }

    public void setSelectedProfile(BasicGameProfile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    public BasicGameProfile[] getActiveProfiles() {
        return activeProfiles;
    }

    public void setActiveProfiles(BasicGameProfile[] activeProfiles) {
        this.activeProfiles = activeProfiles;
    }
}
