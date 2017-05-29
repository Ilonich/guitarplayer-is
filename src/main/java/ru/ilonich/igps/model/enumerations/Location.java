package ru.ilonich.igps.model.enumerations;

public enum  Location {
    UNKNOWN("Не указан"),
    MOSCOW("Москва");


    private String translation;
    Location(String translation){
        this.translation = translation;
    }

    @Override
    public String toString() {
        return translation;
    }
}
