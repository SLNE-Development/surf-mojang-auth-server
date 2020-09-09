package net.wouto.proxy.cache;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.ProfileLookupCallback;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.wouto.proxy.Config;
import net.wouto.proxy.request.JoinMinecraftServerRequestImpl;
import net.wouto.proxy.response.result.HasJoinedMinecraftServerResponseImpl;
import net.wouto.proxy.response.result.JoinMinecraftServerResponseImpl;
import net.wouto.proxy.service.MojangAPI;

public class GameProfileCache {

    private Cache<String, GameProfile> nameProfileCache;
    private Cache<UUID, GameProfile> uuidProfileCache;
    private Cache<String, GameProfile> hasJoinedCache;

    public GameProfileCache(Config config) {
        this.nameProfileCache = CacheBuilder.newBuilder()
                .maximumSize(Long.parseLong(config.getProperty("cacheCount", "10000")))
                .expireAfterWrite(Long.parseLong(config.getProperty("cacheDuration", "3600")), TimeUnit.SECONDS)
                .build();

        this.uuidProfileCache = CacheBuilder.newBuilder()
                .maximumSize(Long.parseLong(config.getProperty("cacheCount", "10000")))
                .expireAfterWrite(Long.parseLong(config.getProperty("cacheDuration", "3600")), TimeUnit.SECONDS)
                .build();
        this.hasJoinedCache = CacheBuilder.newBuilder()
                .expireAfterWrite(10, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Checks in the cache for the given GameProfile and fills it
     *
     * @param profile  the unfilled gameProfile
     * @param unsigned if unsigned
     * @return a new GameProfile or null
     */
    public GameProfile fillGameProfile(GameProfile profile, boolean unsigned) {
        if (profile == null) {
            return null;
        }
        // local cache
        GameProfile fillGameProfile = this.uuidProfileCache.getIfPresent(profile.getId());
        if (fillGameProfile != null) {
            return fillGameProfile;
        }

        // call mojang api
        fillGameProfile = MojangAPI.getInstance().fillGameProfile(profile, true);
        if (fillGameProfile == null || fillGameProfile.getName() == null) {
            return null;
        }
        this.nameProfileCache.put(fillGameProfile.getName(), fillGameProfile);
        this.uuidProfileCache.put(fillGameProfile.getId(), fillGameProfile);
        return fillGameProfile;
    }

    /**
     * Finds the profiles for the given names
     *
     * @param namesInput the names
     * @return an array containing the profiles
     */
    public GameProfile[] findProfilesByNames(Collection<String> namesInput) {
        List<String> names = new ArrayList<>(namesInput);
        List<GameProfile> profiles = new ArrayList<>();
        names.removeIf(s -> {
            GameProfile profile = this.nameProfileCache.getIfPresent(s);
            if (profile != null) {
                profiles.add(profile);
                return true;
            }
            return false;
        });
        if (!names.isEmpty()) {
            MojangAPI.getInstance().findProfilesByNames(names, new ProfileLookupCallback() {
                @Override
                public void onProfileLookupSucceeded(GameProfile profile) {
                    profiles.add(profile);
                }

                @Override
                public void onProfileLookupFailed(GameProfile profile, Exception exception) {
                    // ignore
                }
            });
        }
        return profiles.toArray(new GameProfile[0]);
    }

    /**
     * Adds the selected profile to a short cache
     *
     * @param request the request
     * @return true if successful
     * @throws Exception
     */
    public boolean join(JoinMinecraftServerRequestImpl request) throws Exception {
        GameProfile profile = this.fillGameProfile(request.getSelectedProfile(), true);
        if (profile == null) {
            return false;
        }
        hasJoinedCache.put(request.getServerId(), profile);
        return true;
    }

    /**
     * Checks if player has joined
     *
     * @param username the username to check against
     * @param serverId the serverId
     * @param address the source ip address
     * @return the gameProfile, if the player hasJoined, null if not
     * @throws Exception
     */
    public GameProfile hasJoined(String username, String serverId, InetAddress address) throws Exception {
        HasJoinedMinecraftServerResponseImpl response = null;

        GameProfile joinedProfile = this.hasJoinedCache.getIfPresent(serverId);
        if (joinedProfile != null) {
            // return the cached profile, since the /join endpoint needs authentication
            return joinedProfile;
        }

        try {
            // make mojang api call
            response = MojangAPI.getInstance().hasJoined(username, serverId, address);
            GameProfile gameProfile = new GameProfile(response.getId(), response.getName());
            gameProfile.getProperties().putAll(response.getPropertyMap());
            this.nameProfileCache.put(gameProfile.getName(), gameProfile);
            this.uuidProfileCache.put(gameProfile.getId(), gameProfile);
            return gameProfile;
        } catch (Exception e) {
            // ignored
        }
        return null;
    }

}
