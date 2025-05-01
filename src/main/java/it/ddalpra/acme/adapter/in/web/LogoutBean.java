package it.ddalpra.acme.adapter.in.web;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Named;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

@Named("logoutBean")
@RequestScoped
public class LogoutBean {

    public String logout() throws ServletException {
        HttpServletRequest request = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
        request.logout();
        return "/index.html"; // Reindirizza alla pagina iniziale dopo il logout
    }
}