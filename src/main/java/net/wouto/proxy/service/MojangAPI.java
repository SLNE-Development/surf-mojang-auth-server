package net.wouto.proxy.service;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import com.mojang.authlib.Agent;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.HttpAuthenticationService;
import com.mojang.authlib.ProfileLookupCallback;
import com.mojang.authlib.exceptions.AuthenticationException;
import com.mojang.authlib.exceptions.AuthenticationUnavailableException;
import com.mojang.authlib.yggdrasil.YggdrasilEnvironment;
import com.mojang.authlib.yggdrasil.YggdrasilGameProfileRepository;
import com.mojang.authlib.yggdrasil.response.ProfileSearchResultsResponse;
import com.mojang.util.UUIDTypeAdapter;
import java.net.InetAddress;
import java.net.Proxy;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import net.wouto.proxy.request.JoinMinecraftServerRequestImpl;
import net.wouto.proxy.response.result.HasJoinedMinecraftServerResponseImpl;
import net.wouto.proxy.response.result.JoinMinecraftServerResponseImpl;
import net.wouto.proxy.response.result.MinecraftProfilePropertiesResponseImpl;
import net.wouto.proxy.response.result.ProfileSearchResultsResponseImpl;

public class MojangAPI {

    private static MojangAPI instance;

    private YggdrasilAuthenticationServiceProxy authenticationService;
    private YggdrasilMinecraftSessionServiceProxy sessionService;
    private YggdrasilGameProfileRepository gameProfileRepository;

    private String clientToken;

    private MojangAPI() {
        this.clientToken = UUID.randomUUID().toString();
        this.authenticationService = new YggdrasilAuthenticationServiceProxy(Proxy.NO_PROXY, clientToken);
        this.sessionService = new YggdrasilMinecraftSessionServiceProxy(this.authenticationService, YggdrasilEnvironment.PROD);
        this.gameProfileRepository = new YggdrasilGameProfileRepository(this.authenticationService, YggdrasilEnvironment.PROD);
    }

    public static MojangAPI getInstance() {
        if (instance == null) {
            instance = new MojangAPI();
        }
        return instance;
    }

    public HasJoinedMinecraftServerResponseImpl hasJoined(String name, String serverId, InetAddress address) throws AuthenticationUnavailableException {
        if (name == null) {
            return null;
        }
        GameProfile profile = this.sessionService.hasJoinedServer(new GameProfile(null, name), serverId, address);
        if (profile == null) {
            return null;
        }
        return new HasJoinedMinecraftServerResponseImpl(profile.getId(), profile.getName(), profile.getProperties());
    }

    public JoinMinecraftServerResponseImpl join(JoinMinecraftServerRequestImpl request) throws AuthenticationException {
        if (request == null) {
            return null;
        }
        this.sessionService.joinServer(request.getSelectedProfile(), request.getAccessToken(), request.getServerId());
        return new JoinMinecraftServerResponseImpl();
    }

    public GameProfile fillGameProfile(GameProfile profile, boolean unsigned) {
        if (profile == null) {
            return null;
        }
        GameProfile p = this.sessionService.fillGameProfile(new GameProfile(profile.getId(), null), unsigned);
        return p;
    }

    public void findProfilesByNames(List<String> names, ProfileLookupCallback callback) {
        if (names == null) {
            return;
        }
        this.gameProfileRepository.findProfilesByNames(names.toArray(new String[0]), Agent.MINECRAFT, callback);
    }

}
