package net.wouto.proxy.request;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.mojang.authlib.GameProfile;
import java.util.Objects;
import net.wouto.proxy.Util;

public class JoinMinecraftServerRequestImpl {

    public JoinMinecraftServerRequestImpl() {}

    private String accessToken;
    private GameProfile selectedProfile;
    private String serverId;

    @JsonIgnore
    public GameProfile getSelectedProfile() {
        return selectedProfile;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public String getServerId() {
        return serverId;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    @JsonGetter("selectedProfile")
    public String getSelectedProfileFromId() {
        return getSelectedProfile().getId().toString().replace("-", "");
    }

    @JsonSetter("selectedProfile")
    public void setSelectedProfileFromId(String selectedProfile){
        this.setSelectedProfile(new GameProfile(Util.deserialize(selectedProfile), null));
    }

    @JsonIgnore
    public void setSelectedProfile(GameProfile selectedProfile) {
        this.selectedProfile = selectedProfile;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    @Override
    public String toString() {
        return "JoinMinecraftServerRequestImpl{" +
                "accessToken='" + accessToken + '\'' +
                ", selectedProfile=" + selectedProfile +
                ", serverId='" + serverId + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JoinMinecraftServerRequestImpl that = (JoinMinecraftServerRequestImpl) o;
        return Objects.equals(accessToken, that.accessToken) &&
                Objects.equals(selectedProfile, that.selectedProfile) &&
                Objects.equals(serverId, that.serverId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, selectedProfile, serverId);
    }
}
