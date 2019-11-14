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

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javafx.beans.property.Property;

class DatabaseHelper {

    static <E, P extends Property<T>, T> P createProperty(E entity, Function<E, T> entityGetterFunction, BiConsumer<E, T> entitySetterFunction, Supplier<P> propertySupplier, SessionFactory sessionFactory) {
        P property = propertySupplier.get();
        property.setValue(entityGetterFunction.apply(entity));
        property.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                try (Session session = sessionFactory.openSession()) {
                    Transaction transaction = session.beginTransaction();
                    entitySetterFunction.accept(entity, newValue);
                    session.update(entity);
                    transaction.commit();
                }
            }
        });
        return property;
    }

}
