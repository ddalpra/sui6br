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

    private String fullName;

    public String getUsername() {
        if (idToken == null) {
            return "N/D";
        }

       fullName = this.idToken.getClaim(Claims.given_name).toString() + " " 
       + this.idToken.getClaim(Claims.family_name).toString();
        return fullName;
    }
}