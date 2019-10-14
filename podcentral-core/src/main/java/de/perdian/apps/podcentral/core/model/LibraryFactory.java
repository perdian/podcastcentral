package de.perdian.apps.podcentral.core.model;

public interface LibraryFactory {

    public static LibraryFactory getFactory() {
        return null;
    }

    Library createLibrary();

}
