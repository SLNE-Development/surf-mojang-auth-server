package net.wouto.proxy.response.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import java.util.UUID;
import net.wouto.proxy.Util;

public class BasicGameProfile {

    private UUID id;
    private String name;

    public BasicGameProfile() {
    }

    public BasicGameProfile(UUID id, String name) {
        this.id = id;
        this.name = name;
    }

    @JsonIgnore
    public void setId(UUID id) {
        this.id = id;
    }

    @JsonIgnore
    public UUID getId() {
        return this.id;
    }

    @JsonGetter("id")
    public String getStrippedId() {
        return getId().toString().replace("-", "");
    }

    @JsonSetter("id")
    public void setStrippedId(String strippedId) {
        this.setId(Util.deserialize(strippedId));
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
