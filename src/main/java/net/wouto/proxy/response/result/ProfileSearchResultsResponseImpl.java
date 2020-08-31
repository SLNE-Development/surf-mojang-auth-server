package net.wouto.proxy.response.result;

import com.mojang.authlib.GameProfile;
import net.wouto.proxy.response.BaseResponse;

public class ProfileSearchResultsResponseImpl implements BaseResponse {

    private GameProfile[] profiles;

    public ProfileSearchResultsResponseImpl() {
    }

    public ProfileSearchResultsResponseImpl(GameProfile[] profiles) {
        this.profiles = profiles;
    }

    public void setProfiles(GameProfile[] profiles) {
        this.profiles = profiles;
    }

    public GameProfile[] getProfiles() {
        return this.profiles;
    }

}
