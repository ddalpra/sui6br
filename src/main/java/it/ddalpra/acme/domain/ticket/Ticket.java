package it.ddalpra.acme.domain.ticket;

import java.time.LocalDateTime;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;


public class Ticket {
    private Long id;

    @NotBlank(message = "Il titolo è obbligatorio")
    @Size(min = 3, max = 100, message = "Il titolo deve essere tra 3 e 100 caratteri")
    private String title;

    @NotBlank(message = "La descrizione è obbligatoria")
    @Size(max = 500, message = "La descrizione non può superare i 500 caratteri")
    private String description;

    private TicketStatus status;
    private LocalDateTime creationDate;
    private LocalDateTime updateDate;

    public enum TicketStatus {
        OPEN,
        IN_PROGRESS,
        RESOLVED,
        CLOSED
    }

    public Ticket() {
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        this.status = TicketStatus.OPEN;
    }

    public Ticket(String title, String description) {
        this();
        this.title = title;
        this.description = description;
    }

    // Getters and Setters (aggiornati per l'enum Status)
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TicketStatus getStatus() {
        return status;
    }

    public void setStatus(TicketStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime creationDate) {
        this.creationDate = creationDate;
    }

    public LocalDateTime getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(LocalDateTime updateDate) {
        this.updateDate = updateDate;
    }

    @Override
    public String toString() {
        return "Ticket{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                ", creationDate=" + creationDate +
                ", updateDate=" + updateDate +
                '}';
    }
}
