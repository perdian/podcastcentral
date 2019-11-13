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
package de.perdian.apps.podcentral.sources.feedcollections;

import java.io.IOException;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import de.perdian.apps.podcentral.model.FeedCollection;

public class FeedCollectionLoader {

    public static FeedCollection loadFeedCollection(byte[] data, String mimeType) throws IOException {
        List<Provider<FeedCollectionParser>> feedCollectionParsers = ServiceLoader.load(FeedCollectionParser.class).stream().collect(Collectors.toList());
        for (Provider<FeedCollectionParser> feedCollectionParser : feedCollectionParsers) {
            FeedCollection feedCollection = feedCollectionParser.get().parseFeedCollectionData(data, mimeType);
            if (feedCollection != null) {
                return feedCollection;
            }
        }
        throw new IllegalArgumentException("Cannot find parser to analyze feed collection source for type '" + mimeType + "'");
    }

}
