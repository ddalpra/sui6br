package it.ddalpra.acme.domain.esternalservice;

import jakarta.persistence.*;

@Entity
@Table(name = "external_services")
public class ExternalService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private String baseUrl;

    private String authenticationType; // Ad esempio, "Basic", "Bearer", null
    private String username;
    private String password;
    private String token;

    // Altri dettagli di configurazione se necessari

    // Costruttori (con e senza argomenti)
    public ExternalService() {
    }

    public ExternalService(String name, String baseUrl) {
        this.name = name;
        this.baseUrl = baseUrl;
    }

    // Getter e setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getAuthenticationType() {
        return authenticationType;
    }

    public void setAuthenticationType(String authenticationType) {
        this.authenticationType = authenticationType;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}