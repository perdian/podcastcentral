package de.perdian.apps.podcentral.core.model;

import java.util.Properties;

public interface LibraryFactory {

    public static LibraryFactory getFactory() {
        return null;
    }

    Library createLibrary(Properties preferences);

}
