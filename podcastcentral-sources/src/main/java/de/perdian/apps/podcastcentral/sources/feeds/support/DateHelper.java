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
package de.perdian.apps.podcastcentral.sources.feeds.support;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public class DateHelper {

    private static final String[] DATE_PATTERNS = {
        "EEE, dd MMM yyyy HH:mm:ss xx",
        "EEE, d MMM yyyy HH:mm:ss xx",
        "EEE, dd MMM yyyy HH:mm:ss z",
        "EEE, d MMM yyyy HH:mm:ss z",
    };

    public static Instant parseInstant(String inputValue) {
        for (String pattern : DATE_PATTERNS) {
            try {
                return ZonedDateTime.parse(inputValue, DateTimeFormatter.ofPattern(pattern).withLocale(Locale.ENGLISH)).toInstant();
            } catch (DateTimeParseException e) {
                // Ignore
            }
        }
        throw new IllegalArgumentException("Cannot parse date input value: " + inputValue);
    }

}
