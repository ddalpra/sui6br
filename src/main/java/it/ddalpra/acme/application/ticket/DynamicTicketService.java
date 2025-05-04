package it.ddalpra.acme.application.ticket;


import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Optional;
import com.fasterxml.jackson.databind.ObjectMapper;

import it.ddalpra.acme.domain.esternalservice.ExternalService;
import it.ddalpra.acme.domain.ticket.Ticket;
import it.ddalpra.acme.interfaces.persistence.ExternalServiceRepositoryPort;

@ApplicationScoped
public class DynamicTicketService {

    @Inject
    ExternalServiceRepositoryPort externalServiceRepositoryPort;

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public List<Ticket> fetchAllTickets(String serviceName) throws IOException, InterruptedException {
        Optional<ExternalService> serviceInfo = externalServiceRepositoryPort.findByName(serviceName);
        if (serviceInfo.isPresent()) {
            String baseUrl = serviceInfo.get().getBaseUrl();
            String ticketsUrl = baseUrl + "/tickets"; // Assumiamo un endpoint /api/tickets

            HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                    .uri(URI.create(ticketsUrl))
                    .GET()
                    .header("Content-Type", "application/json");

            // Aggiungi header di autenticazione se presenti
            if ("Basic".equalsIgnoreCase(serviceInfo.get().getAuthenticationType()) &&
                serviceInfo.get().getUsername() != null && serviceInfo.get().getPassword() != null) {
                String auth = java.util.Base64.getEncoder().encodeToString(
                    (serviceInfo.get().getUsername() + ":" + serviceInfo.get().getPassword()).getBytes());
                requestBuilder.header("Authorization", "Basic " + auth);
            } else if ("Bearer".equalsIgnoreCase(serviceInfo.get().getAuthenticationType()) &&
                       serviceInfo.get().getToken() != null) {
                requestBuilder.header("Authorization", "Bearer " + serviceInfo.get().getToken());
            }

            HttpRequest request = requestBuilder.build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                return objectMapper.readValue(response.body(), objectMapper.getTypeFactory().constructCollectionType(List.class, Ticket.class));
            } else {
                // Gestisci l'errore in modo appropriato
                System.err.println("Errore nella chiamata al webservice: " + response.statusCode() + " - " + response.body());
                return List.of(); // O lancia un'eccezione
            }
        } else {
            System.err.println("Servizio esterno non trovato: " + serviceName);
            return List.of(); // O lancia un'eccezione
        }
    }

    // Metodi simili per fetchTicketById, createNewTicket, ecc.,
    // che prenderanno anche il nome del servizio come parametro.

    public List<ExternalService> getAllExternalServices() {
        return externalServiceRepositoryPort.findAll();
    }

    public Optional<ExternalService> getExternalServiceById(Long id) {
        return externalServiceRepositoryPort.findById(id);
    }

    public void saveExternalService(ExternalService service) {
        externalServiceRepositoryPort.save(service);
    }

    public void deleteExternalService(Long id) {
        externalServiceRepositoryPort.delete(id);
    }
}