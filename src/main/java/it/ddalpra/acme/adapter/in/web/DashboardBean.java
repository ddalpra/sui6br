package it.ddalpra.acme.adapter.in.web;

import it.ddalpra.acme.application.user.UserService;
import it.ddalpra.acme.domain.user.User;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import lombok.Getter;

@Named("dashboardBean")
@RequestScoped
@Getter
public class DashboardBean {

    @Inject
    UserService userService;

    private User currentUser;
    
    public User getCurrentUser() {
        if (currentUser == null) {
            currentUser = userService.getCurrentUser();
        }
        return currentUser;
    }
}