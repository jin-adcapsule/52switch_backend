package com.adacpsule.server52switch.shared.inputs;

import java.util.Objects;

public class LocationInput {
    private final String workplace;
    private final String workhourOn;
    private final String workhourOff;
    private final String workhourHalf;

    // Constructor for initialization
    public LocationInput(String workplace, String workhourOn, String workhourOff, String workhourHalf) {
        this.workplace = workplace;
        this.workhourOn = workhourOn;
        this.workhourOff = workhourOff;
        this.workhourHalf = workhourHalf;
    }

    // Getters
    public String getWorkplace() { return workplace; }
    public String getWorkhourOn() { return workhourOn; }
    public String getWorkhourOff() { return workhourOff; }
    public String getWorkhourHalf() { return workhourHalf; }

    // Overridden toString, equals, and hashCode methods
    @Override
    public String toString() {
        return "LocationInput{" +
                "workplace='" + workplace + '\'' +
                ", workhourOn='" + workhourOn + '\'' +
                ", workhourOff='" + workhourOff + '\'' +
                ", workhourHalf='" + workhourHalf + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LocationInput that = (LocationInput) o;
        return Objects.equals(workplace, that.workplace) &&
               Objects.equals(workhourOn, that.workhourOn) &&
               Objects.equals(workhourOff, that.workhourOff) &&
               Objects.equals(workhourHalf, that.workhourHalf);
    }

    @Override
    public int hashCode() {
        return Objects.hash(workplace, workhourOn, workhourOff, workhourHalf);
    }
}

