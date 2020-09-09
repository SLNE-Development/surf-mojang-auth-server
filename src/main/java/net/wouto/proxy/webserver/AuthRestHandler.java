package net.wouto.proxy.webserver;

import com.mojang.authlib.GameProfile;
import java.util.Collections;
import java.util.UUID;
import net.wouto.proxy.MojangProxyServer;
import net.wouto.proxy.cache.GameProfileCache;
import net.wouto.proxy.request.AuthenticateRequestImpl;
import net.wouto.proxy.response.result.AuthenticateResponseImpl;
import net.wouto.proxy.response.result.BasicGameProfile;
import net.wouto.proxy.service.JwtUserDetailsService;
import net.wouto.proxy.util.JwtTokenUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin
public class AuthRestHandler {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtTokenUtil jwtTokenUtil;
    @Autowired
    private JwtUserDetailsService userDetailsService;

    private GameProfileCache gameProfileCache;

    public AuthRestHandler() {
        gameProfileCache = MojangProxyServer.get().getGameProfileCache();
    }

    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseEntity<?> createAuthenticationToken(@RequestBody AuthenticateRequestImpl authenticationRequest) throws Exception {
        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());
        final UserDetails userDetails = userDetailsService.loadUserByUsername(authenticationRequest.getUsername());
        final String token = jwtTokenUtil.generateToken(userDetails);
        // fetch the profile for the given username
        String userName = userDetails.getUsername();
        GameProfile[] profiles = gameProfileCache.findProfilesByNames(Collections.singleton(userName));
        GameProfile profile = profiles[0];
        return ResponseEntity.ok(new AuthenticateResponseImpl(token, new BasicGameProfile(profile.getId(), profile.getName()), null, authenticationRequest.getClientToken()));
    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException e) {
            throw new Exception("USER_DISABLED", e);
        } catch (BadCredentialsException e) {
            throw new Exception("INVALID_CREDENTIALS", e);
        }
    }
}
