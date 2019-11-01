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
package de.perdian.apps.podcentral.core.model;

import java.time.Duration;
import java.time.Instant;
import java.util.Comparator;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.StringProperty;

public interface Episode {

    StringProperty getTitle();
    StringProperty getSubtitle();
    StringProperty getDescription();
    ObjectProperty<Duration> getDuration();
    ObjectProperty<Long> getSize();
    ObjectProperty<Instant> getCreationDate();
    ObjectProperty<Instant> getPublicationDate();
    StringProperty getContentUrl();
    StringProperty getContentType();
    StringProperty getWebsiteUrl();
    StringProperty getImageUrl();
    EpisodeDownload getDownload();

    public static class PublishedDateComparator implements Comparator<Episode> {

        @Override
        public int compare(Episode e1, Episode e2) {
            Instant i1 = e1.getPublicationDate().getValue();
            Instant i2 = e2.getPublicationDate().getValue();
            if (i1 == null && i2 == null) {
                return 0;
            } else if (i1 == null) {
                return -1;
            } else if (i2 == null) {
                return 1;
            } else {
                return i1.compareTo(i2);
            }
        }

    }

}
