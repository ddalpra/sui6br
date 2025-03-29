package it.ddalpra.acme.util;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;
import java.util.Set;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class FileContent implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private final String title;

    private final String value;

    private final String type;

    private final Set<FileContent> attached;

    public FileContent(String title, String value, String type, Set<FileContent> attached) {
        this.title = title;
        this.value = value;
        this.type = type;
        this.attached = attached;
    }

    public String getTitle() {
        return title;
    }

    public String getValue() {
        return value;
    }

    public String getType() {
        return type;
    }

    public Set<FileContent> getAttached() {
        return attached;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FileContent that = (FileContent) o;
        return Objects.equals(getTitle(), that.getTitle());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getTitle());
    }

    @Override
    public String toString() {
        return value;
    }

}