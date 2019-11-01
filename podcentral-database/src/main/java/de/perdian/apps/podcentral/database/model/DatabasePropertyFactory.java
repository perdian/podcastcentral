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

import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DatabasePropertyFactory<T> {

    private T bean = null;
    private SessionFactory sessionFactory = null;

    DatabasePropertyFactory(T bean, SessionFactory sessionFactory) {
        this.setBean(bean);
        this.setSessionFactory(sessionFactory);
    }

    StringProperty createProperty(Function<T, String> entityGetterFunction, BiConsumer<T, String> entitySetterFunction) {
        return this.createProperty(entityGetterFunction, entitySetterFunction, SimpleStringProperty::new);
    }

    <U, P extends Property<U>> P createProperty(Function<T, U> entityGetterFunction, BiConsumer<T, U> entitySetterFunction, Supplier<P> propertySupplier) {
        P property = propertySupplier.get();
        property.setValue(entityGetterFunction.apply(this.getBean()));
        property.addListener((o, oldValue, newValue) -> {
            if (!Objects.equals(oldValue, newValue)) {
                try (Session session = this.getSessionFactory().openSession()) {
                    Transaction transaction = session.beginTransaction();
                    entitySetterFunction.accept(this.getBean(), newValue);
                    session.update(this.getBean());
                    transaction.commit();
                }
            }
        });
        return property;
    }

    private T getBean() {
        return this.bean;
    }
    private void setBean(T bean) {
        this.bean = bean;
    }

    private SessionFactory getSessionFactory() {
        return this.sessionFactory;
    }
    private void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

}
