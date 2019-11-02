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
package de.perdian.apps.podcentral.model;

import javafx.beans.property.StringProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.ObservableList;

public interface Feed {

    StringProperty getUrl();
    StringProperty getWebsiteUrl();
    StringProperty getTitle();
    StringProperty getSubtitle();
    StringProperty getDescription();
    StringProperty getOwner();
    StringProperty getOwnerUrl();
    StringProperty getLanguageCode();
    StringProperty getImageUrl();
    StringProperty getCategory();
    ObservableList<Episode> getEpisodes();
    ObservableList<Object> getProcessors();
    ObservableBooleanValue getBusy();

}
