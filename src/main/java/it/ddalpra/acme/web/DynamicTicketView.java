package it.ddalpra.acme.web;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

import it.ddalpra.acme.application.ticket.DynamicTicketService;
import it.ddalpra.acme.domain.esternalservice.ExternalService;
import it.ddalpra.acme.domain.ticket.Ticket;
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Inject;
import jakarta.inject.Named;

@Named
@ViewScoped
public class DynamicTicketView implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    DynamicTicketService dynamicTicketService;

    private List<Ticket> tickets;
    private List<ExternalService> availableServices;
    private String selectedServiceName;
    private ExternalService newService = new ExternalService();
    private ExternalService selectedServiceToEdit;

    public void loadAvailableServices() {
        this.availableServices = dynamicTicketService.getAllExternalServices();
    }

    public void loadTickets() {
        if (selectedServiceName != null && !selectedServiceName.isEmpty()) {
            try {
                this.tickets = dynamicTicketService.fetchAllTickets(selectedServiceName);
            } catch (IOException | InterruptedException e) {
                // Gestisci l'eccezione (mostra un messaggio all'utente, log)
                e.printStackTrace();
                this.tickets = List.of();
            }
        } else {
            this.tickets = List.of();
        }
    }

    public void saveNewService() {
        dynamicTicketService.saveExternalService(newService);
        newService = new ExternalService();
        loadAvailableServices();
    }

    public void deleteService(Long serviceId) {
        dynamicTicketService.deleteExternalService(serviceId);
        loadAvailableServices();
    }

    public void loadServiceToEdit(Long serviceId) {
        Optional<ExternalService> service = dynamicTicketService.getExternalServiceById(serviceId);
        service.ifPresent(s -> this.selectedServiceToEdit = s);
    }

    public void updateService() {
        if (selectedServiceToEdit != null) {
            dynamicTicketService.saveExternalService(selectedServiceToEdit);
            this.selectedServiceToEdit = null; // Resetta
            loadAvailableServices();
        }
    }

    // Getter e setter
    public List<Ticket> getTickets() {
        return tickets;
    }

    public List<ExternalService> getAvailableServices() {
        return availableServices;
    }

    public String getSelectedServiceName() {
        return selectedServiceName;
    }

    public void setSelectedServiceName(String selectedServiceName) {
        this.selectedServiceName = selectedServiceName;
    }

    public ExternalService getNewService() {
        return newService;
    }

    public void setNewService(ExternalService newService) {
        this.newService = newService;
    }

    public ExternalService getSelectedServiceToEdit() {
        return selectedServiceToEdit;
    }

    public void setSelectedServiceToEdit(ExternalService selectedServiceToEdit) {
        this.selectedServiceToEdit = selectedServiceToEdit;
    }
}