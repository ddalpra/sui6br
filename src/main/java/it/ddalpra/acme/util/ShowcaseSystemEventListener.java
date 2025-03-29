package it.ddalpra.acme.util;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.component.export.DataExporters;
import org.primefaces.component.export.TableExporter;

import jakarta.faces.application.Application;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;

public class ShowcaseSystemEventListener implements SystemEventListener {

    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        DataExporters.register(DataTable.class, TableExporter.class, "txt");
    }

    @Override
    public boolean isListenerForSource(Object source) {
        return source instanceof Application;
    }
}