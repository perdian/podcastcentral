package de.perdian.apps.podcastcentral.ui;

import java.io.File;

import de.perdian.apps.podcastcentral.preferences.PreferencesFactory;
import de.perdian.apps.podcastcentral.ui.CentralApplicationLauncher;

public class CentralApplicationLauncherDevelopment {

    public static void main(String[] args) throws Exception {
        System.setProperty(PreferencesFactory.DOWNLOAD_DIRECTORY_KEY, new File("tmp/download").getCanonicalPath());
        CentralApplicationLauncher.main(args);
    }

}
