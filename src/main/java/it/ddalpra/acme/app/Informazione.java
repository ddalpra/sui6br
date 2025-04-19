package it.ddalpra.acme.app;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;

import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.RefreshToken;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@RequestScoped  
public class Informazione {
    @Inject
    @IdToken
    JsonWebToken idToken;
    @Inject
    
    JsonWebToken accessToken;
    @Inject
    RefreshToken refreshToken;


    private String username;


    public String getUsername() {
        if (idToken == null) {
            return "N/D";
        }
        Object userName = this.idToken.getClaim(Claims.preferred_username);
        if (userName == null) {
            return "N/D";
        }
        return userName.toString();
    }

}