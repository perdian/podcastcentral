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
package de.perdian.apps.podcentral.database.model;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.cfg.Environment;
import org.hibernate.service.ServiceRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.core.impl.AbstractLibraryBuilder;
import de.perdian.apps.podcentral.database.entities.EpisodeEntity;
import de.perdian.apps.podcentral.database.entities.FeedEntity;

public class DatabaseBackedLibraryBuilder extends AbstractLibraryBuilder {

    private static final Logger log = LoggerFactory.getLogger(DatabaseBackedLibraryBuilder.class);

    @Override
    public DatabaseBackedLibrary buildLibrary(Properties properties) {
        Path storageDirectory = this.resolveStorageDirectory(properties);
        SessionFactory sessionFactory = this.resolveHibernateSessionFactory(properties);
        DatabaseBackedLibrary library = new DatabaseBackedLibrary(sessionFactory, storageDirectory);
        library.loadInitialFeeds();
        return library;
    }

    private SessionFactory resolveHibernateSessionFactory(Properties properties) {

        Configuration hibernateConfiguration = new Configuration();
        hibernateConfiguration.setProperty(Environment.DRIVER, "org.h2.Driver");
        hibernateConfiguration.setProperty(Environment.URL, this.resolveHibernateDatabaseUrl(properties));
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

    private String resolveHibernateDatabaseUrl(Properties properties) {
        Path databaseDirectory = this.resolveApplicationDirectory(properties).resolve(properties.getProperty("database.directory", "database/"));
        if (!Files.exists(databaseDirectory)) {
            try {
                log.info("Creating database directory at: {}", databaseDirectory);
                Files.createDirectories(databaseDirectory);
            } catch (Exception e) {
                throw new RuntimeException("Cannot create database directory at: " + databaseDirectory, e);
            }
        }
        StringBuilder databaseUrl = new StringBuilder();
        databaseUrl.append("jdbc:h2:file:").append(databaseDirectory.resolve("feeds"));
        return databaseUrl.toString();
    }

    private Path resolveStorageDirectory(Properties properties) {
        return this.resolveApplicationDirectory(properties).resolve(properties.getProperty("downloads.directory", "downloads/"));
    }

    private Path resolveApplicationDirectory(Properties properties) {
        return Paths.get(System.getProperty("user.home"), ".podcastcentral");
    }

}
