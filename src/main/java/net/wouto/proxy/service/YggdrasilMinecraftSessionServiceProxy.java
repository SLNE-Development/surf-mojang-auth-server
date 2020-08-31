package net.wouto.proxy.service;

import com.mojang.authlib.Environment;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.yggdrasil.YggdrasilAuthenticationService;
import com.mojang.authlib.yggdrasil.YggdrasilMinecraftSessionService;

/**
 * Exposes the constructor of the {@link YggdrasilMinecraftSessionService}
 */
public class YggdrasilMinecraftSessionServiceProxy extends YggdrasilMinecraftSessionService {

    public YggdrasilMinecraftSessionServiceProxy(YggdrasilAuthenticationService yggdrasilAuthenticationService, Environment environment) {
        super(yggdrasilAuthenticationService, environment);
    }

    @Override
    public GameProfile fillGameProfile(GameProfile gameProfile, boolean b) {
        return super.fillGameProfile(gameProfile, b);
    }
}
