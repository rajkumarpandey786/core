package com.rkp.topcore.canonical.document;

public final class CanonicalValue {

    private final Object value;

    public CanonicalValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}