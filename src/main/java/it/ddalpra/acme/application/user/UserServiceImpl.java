package it.ddalpra.acme.application.user;

import org.eclipse.microprofile.jwt.JsonWebToken;

import it.ddalpra.acme.domain.user.User;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

@ApplicationScoped
public class UserServiceImpl implements UserService {

    @Inject
    JsonWebToken jwt;

    @Override
    public User getCurrentUser() {
        User user = new User();
        user.setUsername(jwt.getName());
        user.setFirstName(jwt.getClaim("given_name"));
        user.setLastName(jwt.getClaim("family_name"));
        user.setEmail(jwt.getClaim("email"));
        user.setFullName(user.getFirstName() + " " + user.getLastName());
        // Estrai altri dati dal token JWT se necessario
        return user;
    }
}