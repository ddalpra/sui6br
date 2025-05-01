package it.ddalpra.acme.domain.user;

import lombok.Data;

@Data
public class User {
    private String username;
    private String firstName;
    private String lastName;
    private String email;
    private String fullName;
    // Altri dati dell'utente
}