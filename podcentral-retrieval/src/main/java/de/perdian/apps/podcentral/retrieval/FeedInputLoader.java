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
package de.perdian.apps.podcentral.retrieval;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.ServiceLoader;
import java.util.ServiceLoader.Provider;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.model.FeedInput;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FeedInputLoader {

    private static final Logger log = LoggerFactory.getLogger(FeedInputLoader.class);

    private static final OkHttpClient HTTP_CLIENT = new OkHttpClient.Builder().build();

    public static FeedInput loadFeedInputFromUrl(String feedUrl) throws Exception {
        try {
            Request httpRequest = new Request.Builder().get().url(feedUrl).build();
            Instant startTime = Instant.now();
            try (Response httpResponse = HTTP_CLIENT.newCall(httpRequest).execute()) {
                log.debug("Loaded content (took {}) from feed URL: {}", Duration.between(startTime, Instant.now()), feedUrl);
                List<Provider<FeedInputSource>> feedInputSources = ServiceLoader.load(FeedInputSource.class).stream().collect(Collectors.toList());
                log.debug("Processing {} feed input sources for feed from URL: {}", feedInputSources.size(), feedUrl);
                for (Provider<FeedInputSource> feedInputSource : feedInputSources) {
                    FeedInput feedInput = feedInputSource.get().loadFeedInput(httpResponse);
                    if (feedInput == null) {
                        log.debug("Cannot load feed input using source {} from feed URL: {}", feedInputSource.get(), feedUrl);
                    } else {
                        log.info("Loaded feed input using source {} from feed URL: {}", feedInputSource.get(), feedUrl);
                        return feedInput;
                    }
                }
                throw new IllegalArgumentException("Cannot analyze response for content type '" + httpResponse.body().contentType() + "' from feed URL: " + feedUrl);
            }
        } catch (IOException e) {
            throw new IOException("Cannot load feed from feed URL: " + feedUrl, e);
        }
    }

}
