package net.wouto.proxy.webserver;

import com.mojang.authlib.GameProfile;
import java.util.UUID;
import net.wouto.proxy.MojangProxyServer;
import net.wouto.proxy.Util;
import net.wouto.proxy.cache.GameProfileCache;
import net.wouto.proxy.request.JoinMinecraftServerRequestImpl;
import net.wouto.proxy.response.result.HasJoinedMinecraftServerResponseImpl;
import net.wouto.proxy.response.result.JoinMinecraftServerResponseImpl;
import net.wouto.proxy.response.result.MinecraftProfilePropertiesResponseImpl;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SessionRestHandler {

    private GameProfileCache cache;

    public SessionRestHandler() {
        this.cache = MojangProxyServer.get().getGameProfileCache();
    }

    @RequestMapping(value = "/session/minecraft/hasJoined", method = RequestMethod.GET)
    @ResponseBody
    public HasJoinedMinecraftServerResponseImpl hasJoined(
            @RequestParam(value = "serverId") String serverId,
            @RequestParam(value = "username") String username,
            @RequestParam(value = "proxyKey", required = false) String key) throws Exception {
        MojangProxyServer.authorize(key);
        if (MojangProxyServer.LOG_KNOWN_REQUESTS) {
            System.out.println("forwarding hasJoined(username:\"" + username + "\", serverId:\"" + serverId + "\")");
        }

        GameProfile profile =this.cache.hasJoined(username, serverId, null);
        if(profile == null) {
            profile = new GameProfile(UUID.randomUUID(), username);
        }

        return new HasJoinedMinecraftServerResponseImpl(
            profile
        );
    }


    @RequestMapping(value = "/session/minecraft/join", method = RequestMethod.POST, consumes = "application/json")
    @ResponseBody
    public JoinMinecraftServerResponseImpl join(@RequestBody JoinMinecraftServerRequestImpl request) throws Exception {
        if (MojangProxyServer.LOG_KNOWN_REQUESTS) {
            System.out.println("forwarding join(" + request + ")");
        }

        if(this.cache.join(request)) {
            return new JoinMinecraftServerResponseImpl();
        }
        return null;
    }

    @RequestMapping(value = "/session/minecraft/profile/{uuid}", method = RequestMethod.GET)
    @ResponseBody
    public MinecraftProfilePropertiesResponseImpl fillGameProfile(
            @PathVariable(value = "uuid") String uuid,
            @RequestParam(value = "unsigned", required = false, defaultValue = "true") boolean unsigned,
            @RequestParam(value = "proxyKey", required = false) String key) throws Exception {
        MojangProxyServer.authorize(key);
        if (MojangProxyServer.LOG_KNOWN_REQUESTS) {
            System.out.println("forwarding fillGameProfile(uuid:\"" + uuid + "\", unsigned:" + unsigned + ")");
        }


        UUID uuidObj = Util.deserialize(uuid);
        GameProfile profile = this.cache.fillGameProfile(new GameProfile(uuidObj, null), unsigned);

        if(profile == null) {
            profile = new GameProfile(uuidObj, "Unknown");
        }

        return new MinecraftProfilePropertiesResponseImpl(profile);
    }

}
