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
package de.perdian.apps.podcentral.core.web;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.core.model.Channel;
import de.perdian.apps.podcentral.core.web.impl.RssChannelSourceParser;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

enum ChannelSourceFormat {

    RSS(new RssChannelSourceParser());

    private static final Logger log = LoggerFactory.getLogger(ChannelSourceFormat.class);

    private ChannelSourceParser parser = null;

    private ChannelSourceFormat(ChannelSourceParser parser) {
        this.setParser(parser);
    }

    public Channel loadChannelFromUrl(String url) throws IOException {
        try {
            OkHttpClient httpClient = new OkHttpClient.Builder().build();
            Request httpRequest = new Request.Builder().get().url(url).build();
            Instant startTime = Instant.now();
            try (Response httpResponse = httpClient.newCall(httpRequest).execute()) {
                String responseContent = httpResponse.body().string();
                log.debug("Loaded content ({} characters) in {} for format {} for channel: {}", responseContent.length(), Duration.between(startTime, Instant.now()), this.name(), url);
                String responseContentType = httpResponse.header("Content-Type");
                log.debug("Trying to parse response for format {} using parser '{}' for channel: {}", this.name(), this.getParser(), url);
                Channel parsedChannel = this.getParser().parseChannel(responseContent, responseContentType, url);
                return parsedChannel;
            }
        } catch (IOException e) {
            throw new IOException("Cannot load channel from URL: " + url, e);
        }
    }

    private ChannelSourceParser getParser() {
        return this.parser;
    }
    private void setParser(ChannelSourceParser parser) {
        this.parser = parser;
    }

}
