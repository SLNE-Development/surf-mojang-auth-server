package net.wouto.proxy.response.result;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSetter;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import com.mojang.authlib.properties.PropertyMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import net.wouto.proxy.Util;
import net.wouto.proxy.response.BaseResponse;

public class MinecraftProfilePropertiesResponseImpl implements BaseResponse {

    private UUID id;
    private String name;
    private BasicProperty[] properties;

    public MinecraftProfilePropertiesResponseImpl() {
    }

    public MinecraftProfilePropertiesResponseImpl(GameProfile profile) {
        this(profile.getId(), profile.getName(), profile.getProperties());
    }

    public MinecraftProfilePropertiesResponseImpl(UUID id, String name, PropertyMap properties) {
        this.id = id;
        this.name = name;
        List<BasicProperty> propertyRes = new ArrayList<>();
        for (String s : properties.keySet()) {
            Set<Property> data = (Set<Property>) properties.get(s);
            for (Property p : data) {
                String nameVal = p.getName();
                String valueVal = p.getValue();
                String signatureVal = p.getSignature();
                BasicProperty pr;
                if (signatureVal != null) {
                    pr = new BasicProperty(nameVal, valueVal, signatureVal);
                } else {
                    pr = new BasicProperty(nameVal, valueVal);
                }
                propertyRes.add(pr);
            }
        }
        this.properties = propertyRes.toArray(new BasicProperty[propertyRes.size()]);
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
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
        return this.name;
    }

    public BasicProperty[] getProperties() {
        return properties;
    }

    @JsonIgnore
    public PropertyMap getPropertyMap() {
        PropertyMap map = new PropertyMap();
        for (BasicProperty property : this.properties) {
            Property p = new Property(property.getName(), property.getValue(), property.getSignature());
            map.put(p.getName(), p);
        }
        return map;
    }

    public void setProperties(BasicProperty[] properties) {
        this.properties = properties;
    }

    @JsonIgnore
    public GameProfile getGameProfile() {
        GameProfile profile = new GameProfile(this.id, this.name);
        profile.getProperties().putAll(this.getPropertyMap());
        return profile;
    }
}
