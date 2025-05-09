package it.ddalpra.acme.domain.util;

import jakarta.enterprise.inject.spi.CDI;
import jakarta.faces.application.ProjectStage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.component.UIPanel;
import jakarta.faces.context.FacesContext;
import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.quarkus.runtime.annotations.RegisterForReflection;
import org.primefaces.cache.CacheProvider;
import org.primefaces.component.tabview.Tab;

@RegisterForReflection
public class ShowcaseUtil {

    public static List<FileContent> getFilesContent(String fullPath, Boolean readBeans) {
        CacheProvider provider = CDI.current().select(ShowcaseCacheProvider.class).get().getCacheProvider();
        List<FileContent> files = (List<FileContent>) provider.get("contents", fullPath);

        if (files == null) {
            FacesContext facesContext = FacesContext.getCurrentInstance();
            FileContent srcContent = getFileContent(fullPath, readBeans);
            UIComponent tabs = UIComponent.getCurrentComponent(facesContext).getFacet("static-tabs");
            if (tabs != null) {
                attach(tabs, srcContent);
            }
            files = new ArrayList<>();
            assert srcContent != null;
            flatFileContent(srcContent, files);

            if (facesContext.isProjectStage(ProjectStage.Production)) {
                provider.put("contents", fullPath, files);
            }
        }
        return files;
    }

    public static Object getPropertyValueViaReflection(Object o, String field)
            throws ReflectiveOperationException, IllegalArgumentException, IntrospectionException {
        return new PropertyDescriptor(field, o.getClass()).getReadMethod().invoke(o);
    }

    // EXCLUDE-SOURCE-START
    private static FileContent getFileContent(String fullPath, Boolean readBeans) {
        try {
            // Finding in WEB ...
            FacesContext fc = FacesContext.getCurrentInstance();
            InputStream is = fc.getExternalContext().getResourceAsStream(fullPath);
            if (is != null) {
                return FileContentMarkerUtil.readFileContent(fullPath, is, readBeans);
            }

            // Finding in ClassPath ...
            is = ShowcaseUtil.class.getResourceAsStream(fullPath);
            if (is != null) {
                return FileContentMarkerUtil.readFileContent(fullPath, is, readBeans);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Internal error: file " + fullPath + " could not be read", e);
        }

        return null;
    }

    private static void attach(UIComponent component, FileContent file) {
        if (component.isRendered()) {
            if (component instanceof Tab) {
                String flatten = (String) component.getAttributes().get("flatten");
                String title = ((Tab) component).getTitle();
                if (title != null && title.endsWith(".java")) {
                    title = "/java" + title;
                }
                FileContent content = getFileContent(
                        title,
                        Boolean.parseBoolean(flatten));
                file.getAttached().add(content);
            } else if (component instanceof UIPanel) {
                for (UIComponent child : component.getChildren()) {
                    attach(child, file);
                }
            }
        }
    }

    private static void flatFileContent(FileContent source, List<FileContent> dest) {
        if (source == null) {
            return;
        }
        dest.add(new FileContent(source.getTitle(), source.getValue(), source.getType(),
                Collections.emptySet()));

        for (FileContent file : source.getAttached()) {
            flatFileContent(file, dest);
        }
    }
    // EXCLUDE-SOURCE-END
}