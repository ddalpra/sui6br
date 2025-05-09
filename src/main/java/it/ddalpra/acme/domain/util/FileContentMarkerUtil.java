package it.ddalpra.acme.domain.util;

import jakarta.el.ELException;
import jakarta.faces.context.FacesContext;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import io.quarkus.runtime.annotations.RegisterForReflection;
import lombok.experimental.UtilityClass;
import lombok.extern.jbosslog.JBossLog;
import org.primefaces.util.LangUtils;

@RegisterForReflection
@UtilityClass
@JBossLog
public class FileContentMarkerUtil {

    private static final FileContentSettings javaFileSettings = new FileContentSettings()
            .setType("java")
            .setStartMarkers(
                    Marker.of("package "),
                    Marker.of("EXCLUDE-SOURCE-END").excluded())
            .setEndMarkers(Marker.of("EXCLUDE-SOURCE-START").excluded());

    private static final FileContentSettings xhtmlFileSettings = new FileContentSettings()
            .setType("xml")
            .setStartMarkers(
                    Marker.of("EXAMPLE-SOURCE-START").excluded(),
                    Marker.of("<ui:define name=\"implementation\">").excluded(),
                    Marker.of("<ui:define name=\"head\">").excluded())
            .setEndMarkers(
                    Marker.of("EXAMPLE-SOURCE-END").excluded(),
                    Marker.of("</ui:define>").excluded());

    private static final Pattern SC_BEAN_PATTERN = Pattern.compile("#\\{([^.]*)\\.?.*?}", Pattern.MULTILINE);

    private static final String SC_PREFIX = "org.primefaces.showcase";

    public static FileContent readFileContent(String fullPathToFile, InputStream is, boolean readBeans) {
        try {
            String fileName = fullPathToFile.substring(fullPathToFile.lastIndexOf("/") + 1);
            if (fullPathToFile.endsWith(".java")) {
                return readFileContent(fileName, is, javaFileSettings, readBeans);
            }

            if (fullPathToFile.endsWith(".xhtml")) {
                return readFileContent(fileName, is, xhtmlFileSettings, readBeans);
            }

            throw new UnsupportedOperationException();
        } catch (Exception e) {
            throw new IllegalStateException("Internal error: file " + fullPathToFile + " could not be read", e);
        }
    }

    private static FileContent readFileContent(String fileName, InputStream inputStream, FileContentSettings settings,
                                               boolean readBeans) throws Exception {
        StringBuilder content = new StringBuilder();
        Set<FileContent> javaFiles = new LinkedHashSet<>();
        FacesContext facesContext = FacesContext.getCurrentInstance();

        try (InputStreamReader ir = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
             BufferedReader br = new BufferedReader(ir)) {
            String line;
            boolean started = false;

            while ((line = br.readLine()) != null) {
                if (!started) {
                    Marker marker = getMatchingMarker(line, settings.getStartMarkers());
                    started = marker != null;
                    if (!started || marker.isExcluded()) {
                        continue;
                    }
                }

                // if is before first end marker
                if (getMatchingMarker(line, settings.getEndMarkers()) != null) {
                    started = false;
                    content.append("\n");
                    continue;
                }

                content.append("\n");
                content.append(line);

                if (readBeans && line.contains("#{")) {
                    Matcher m = SC_BEAN_PATTERN.matcher(line.trim());
                    while (m.find()) {
                        String group = m.group(1);
                        addBean(facesContext, javaFiles, group);
                    }
                }
            }
        }

        String value = content.toString().trim();
        if ("xml".equalsIgnoreCase(settings.getType())) {
            value = prettyFormat(value);
        }
        return new FileContent(fileName, value, settings.getType(), javaFiles);
    }

    private static void addBean(FacesContext facesContext, Set<FileContent> javaFiles, String group) throws Exception {
        Object bean;
        try {
            bean = facesContext.getApplication()
                    .evaluateExpressionGet(facesContext, "#{" + group + "}", Object.class);
        } catch (ELException e) {
            return;
        }
        if (bean == null) {
            return;
        }

        Class<?> beanClass = LangUtils.getUnproxiedClass(bean.getClass());
        log.debugf("BeanClass: %s", beanClass);
        if (isEligibleFile(beanClass.getName())) {
            // special handling for member classes (like ColumnsView and ColumnsView$ColumnModel)
            String className = beanClass.getName();
            log.debugf("BeanClass Name: %s", className);
            if (beanClass.isMemberClass()) {
                className = className.substring(0, className.indexOf("$"));
            }

            String javaFileName = packageToPathAccess(className);
            if (isFileNotContainedIn(javaFileName, javaFiles)) {
                FileContent content = createFileContent(className);
                javaFiles.add(content);
            }

            for (Field field : beanClass.getDeclaredFields()) {
                addDeclaredField(javaFiles, field);
            }
        }
    }

    private static void addDeclaredField(Set<FileContent> javaFiles, Field field) throws Exception {
        String typeName = getType(field);
        log.debugf("DeclaredField: %s Type: %s", field, typeName);
        String javaFileName = packageToPathAccess(typeName);
        if (isEligibleFile(typeName) && isFileNotContainedIn(javaFileName, javaFiles)) {
            FileContent content = createFileContent(typeName);
            javaFiles.add(content);

            // add any other PF related POJO's recursively
            if (isEligibleFile(typeName)) {
                try {
                    Class<?> subType = Class.forName(typeName);
                    for (Field subField : subType.getDeclaredFields()) {
                        addDeclaredField(javaFiles, subField);
                    }
                } catch (Exception e) {
                    // class could not be instantiated so move on
                }
            }
        }
    }

    private static String getType(Field field) {
        String typeName = field.getType().getTypeName();

        try {
            // get Product from `List<Product>` because of type erasure
            Type genericFieldType = field.getGenericType();
            if (genericFieldType instanceof ParameterizedType parameterizedType) {
                Type[] fieldArgTypes = parameterizedType.getActualTypeArguments();
                for (Type fieldArgType : fieldArgTypes) {
                    Class<?> fieldArgClass = (Class<?>) fieldArgType;
                    typeName = fieldArgClass.getName();
                }
            }
        } catch (Exception e) {
            // just use the original type name if any error
        }
        return typeName;
    }

    private static FileContent createFileContent(String fileName) throws Exception {
        String path = createFullPath(fileName);
        InputStream is = FacesContext.getCurrentInstance().getExternalContext().getResourceAsStream(path);
        String name = fileName.substring(fileName.lastIndexOf(".") + 1) + ".java";

        if (is == null) {
            is = new ByteArrayInputStream(name.getBytes(StandardCharsets.UTF_8));
        }

        return readFileContent(name, is, javaFileSettings, false);
    }

    private static Marker getMatchingMarker(String line, Marker[] markers) {
        for (Marker marker : markers) {
            if (line.contains(marker.getName())) {
                return marker;
            }
        }

        return null;
    }

    private static String prettyFormat(String value) {
        String[] chunks = value.split("(?<=\\n)");
        StringBuilder pretty = new StringBuilder();
        for (String chunk : chunks) {
            pretty.append(chunk.replaceFirst("\\s{8}", ""));
        }
        return pretty.toString();
    }

    /**
     * Determines if a given file is eligible for processing.
     *
     * @param file The file name or path to check
     * @return true if the file is eligible, false otherwise
     */
    private static boolean isEligibleFile(String file) {
        // Check the file is a Showcase file, is not an array "[]", and is not an inner class "$"
        return file != null && file.startsWith(SC_PREFIX) && !file.endsWith("[]") && !file.contains("$");
    }

    private static String packageToPathAccess(String pkg) {
        return pkg.substring(pkg.lastIndexOf(".") + 1) + ".java";
    }

    private static boolean isFileNotContainedIn(String filename, Set<FileContent> javaFiles) {
        return !javaFiles.contains(new FileContent(filename, null, null, null));
    }

    private static String createFullPath(String filename) {
        return "/java/" + filename.replaceAll("\\.", "/") + ".java";
    }
}