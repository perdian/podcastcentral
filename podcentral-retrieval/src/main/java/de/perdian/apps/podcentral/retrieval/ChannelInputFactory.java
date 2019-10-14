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

import de.perdian.apps.podcentral.core.model.ChannelInput;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ChannelInputFactory {

    private static final Logger log = LoggerFactory.getLogger(ChannelInputLoader.class);

    public static ChannelInput getChannelInput(String feedUrl) throws Exception {
        try {
            OkHttpClient httpClient = new OkHttpClient.Builder().build();
            Request httpRequest = new Request.Builder().get().url(feedUrl).build();
            Instant startTime = Instant.now();
            try (Response httpResponse = httpClient.newCall(httpRequest).execute()) {

                log.debug("Loaded content (took {}) from feed URL: {}", Duration.between(startTime, Instant.now()), feedUrl);
                List<Provider<ChannelInputLoader>> channelInputLoaders = ServiceLoader.load(ChannelInputLoader.class).stream().collect(Collectors.toList());
                log.debug("Processing {} channel input loaders for feed from URL: {}", channelInputLoaders.size(), feedUrl);
                for (Provider<ChannelInputLoader> channelInputLoader : channelInputLoaders) {
                    ChannelInput channelInput = channelInputLoader.get().loadChannelInput(httpResponse);
                    if (channelInput == null) {
                        log.debug("Cannot load channel input using {} from feed URL: {}", channelInputLoader.get(), feedUrl);
                    } else {
                        log.info("Loaded channel input using {} from feed URL: {}", channelInputLoader.get(), feedUrl);
                        return channelInput;
                    }
                }
                throw new IllegalArgumentException("Cannot analyze response for content type '" + httpResponse.body().contentType() + "' from feed URL: " + feedUrl);

            }
        } catch (IOException e) {
            throw new IOException("Cannot load channel from feed URL: " + feedUrl, e);
        }
    }



}
