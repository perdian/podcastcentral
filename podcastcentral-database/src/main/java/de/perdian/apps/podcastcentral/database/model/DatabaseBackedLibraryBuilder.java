/*
 * Copyright 2019 Christian Seifert
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.perdian.apps.podcastcentral.database.model;

import java.io.File;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcastcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcastcentral.database.entities.FeedEntity;
import de.perdian.apps.podcastcentral.model.LibraryBuilder;
import de.perdian.apps.podcastcentral.preferences.Preferences;
import de.perdian.apps.podcastcentral.storage.Storage;

public class DatabaseBackedLibraryBuilder implements LibraryBuilder {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackedLibraryBuilder.class);

    @Override
    public DatabaseBackedLibrary buildLibrary(Storage storage, Preferences preferences) {
        SessionFactory sessionFactory = this.buildHibernateSessionFactory(preferences);
        return new DatabaseBackedLibrary(sessionFactory, storage);
    }

    private SessionFactory buildHibernateSessionFactory(Preferences preferences) {

        Configuration hibernateConfiguration = new Configuration();
        hibernateConfiguration.setProperty(Environment.DRIVER, "org.h2.Driver");
        hibernateConfiguration.setProperty(Environment.URL, this.buildHibernateDatabaseUrl(preferences));
        hibernateConfiguration.setProperty(Environment.USER, "sa");
        hibernateConfiguration.setProperty(Environment.PASS, "");
        hibernateConfiguration.setProperty(Environment.DIALECT, "org.hibernate.dialect.H2Dialect");
        hibernateConfiguration.setProperty(Environment.HBM2DDL_AUTO, "update");
//        hibernateConfiguration.setProperty(Environment.SHOW_SQL, "true");
//        hibernateConfiguration.setProperty(Environment.FORMAT_SQL, "true");
        hibernateConfiguration.addAnnotatedClass(FeedEntity.class);
        hibernateConfiguration.addAnnotatedClass(EpisodeEntity.class);

        log.info("Creating database connection using URL: {}", hibernateConfiguration.getProperty(Environment.URL));
        ServiceRegistry hibernateServiceRegistry = new StandardServiceRegistryBuilder().applySettings(hibernateConfiguration.getProperties()).build();
        return hibernateConfiguration.buildSessionFactory(hibernateServiceRegistry);

    }

    private String buildHibernateDatabaseUrl(Preferences preferences) {
        File databaseDirectory = new File(preferences.getMetadataDirectory(), "database/");
        if (!databaseDirectory.exists()) {
            try {
                log.info("Creating database directory at: {}", databaseDirectory.getAbsolutePath());
                databaseDirectory.mkdirs();
            } catch (Exception e) {
                throw new RuntimeException("Cannot create database directory at: " + databaseDirectory, e);
            }
        }
        File databaseFile = new File(databaseDirectory, "library");
        log.debug("Using H2 database at: {}", databaseFile.getAbsolutePath());
        StringBuilder databaseUrl = new StringBuilder();
        databaseUrl.append("jdbc:h2:file:").append(databaseFile.getAbsolutePath());
        return databaseUrl.toString();
    }

}
