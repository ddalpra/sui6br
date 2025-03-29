package it.ddalpra.acme.util;

import java.util.Objects;

import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection
public class Marker {

    private final String name;

    private boolean excluded;

    private Marker(String name) {
        this.name = name;
    }

    public static Marker of(String name) {
        return new Marker(name);
    }

    public Marker excluded() {
        excluded = true;
        return this;
    }

    public boolean isExcluded() {
        return excluded;
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Marker marker = (Marker) o;
        return Objects.equals(name, marker.name);
    }
}