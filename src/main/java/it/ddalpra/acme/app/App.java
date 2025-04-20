package it.ddalpra.acme.app;

import java.io.Serial;
import java.io.Serializable;
import java.util.Locale;

import org.eclipse.microprofile.jwt.Claims;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.primefaces.context.PrimeApplicationContext;

import io.quarkus.oidc.IdToken;
import io.quarkus.oidc.RefreshToken;
import io.quarkus.runtime.annotations.RegisterForReflection;
import it.ddalpra.acme.domain.Country;
import jakarta.enterprise.context.SessionScoped;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@SessionScoped
@RegisterForReflection
public class App implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Inject
    @IdToken
    JsonWebToken idToken;
    @Inject    
    JsonWebToken accessToken;
    @Inject
    RefreshToken refreshToken;

    private String fullName;

    private String theme = "saga-blue";
    private boolean darkMode = false;
    private String inputStyle = "outlined";
    private Country locale = new Country(0, Locale.ITALIAN);

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public boolean isDarkMode() {
        return darkMode;
    }

    public void setDarkMode(boolean darkMode) {
        this.darkMode = darkMode;
    }

    public String getInputStyle() {
        return inputStyle;
    }

    public void setInputStyle(String inputStyle) {
        this.inputStyle = inputStyle;
    }

    public String getInputStyleClass() {
        return this.inputStyle.equals("filled") ? "ui-input-filled" : "";
    }

    public Country getLocale() {
        return locale;
    }

    public void setLocale(Country locale) {
        this.locale = locale;
    }

    public String getFullName() {
        fullName = this.idToken.getClaim(Claims.given_name).toString() + " " 
       + this.idToken.getClaim(Claims.family_name).toString();
        return fullName;
    }

    public void changeTheme(String theme, boolean darkMode) {
        this.theme = theme;
        this.darkMode = darkMode;
    }

    public String getThemeImage() {
        String result = getTheme();
        switch (result) {
            case "nova-light":
                result = "nova.png";
                break;
            case "nova-colored":
                result = "nova-accent.png";
                break;
            case "nova-dark":
                result = "nova-alt.png";
                break;
            case "bootstrap4-blue-light":
                result = "bootstrap4-light-blue.svg";
                break;
            case "bootstrap4-blue-dark":
                result = "bootstrap4-dark-blue.svg";
                break;
            case "bootstrap4-purple-light":
                result = "bootstrap4-light-purple.svg";
                break;
            case "bootstrap4-purple-dark":
                result = "bootstrap4-dark-purple.svg";
                break;
            default:
                result += ".png";
                break;
        }
        return result;
    }

    public String getPrimeFacesVersion() {
        return PrimeApplicationContext.getCurrentInstance(FacesContext.getCurrentInstance()).getEnvironment().getBuildVersion();
    }
}