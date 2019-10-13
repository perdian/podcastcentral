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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcentral.core.model.Channel;

/**
 * Abstract representation of the content that has been retrieved via the network from the channel URL
 *
 * @author Christian Seifert
 */

public class ChannelLoader {

    private static final Logger log = LoggerFactory.getLogger(ChannelLoader.class);

    public Channel loadChannel(String url) throws Exception {
        for (ChannelSourceFormat format : ChannelSourceFormat.values()) {
            log.debug("Trying to load content using format {} for channel: {}", format.name(), url);
            Channel channelFromFormat = format.loadChannelFromUrl(url);
            if (channelFromFormat != null) {
                log.info("Loaded information using format {} from channel: {}", format.name(), url);
                return channelFromFormat;
            } else {
                log.debug("Cannot load information using format {} from fom channel: {}", format.name(), url);
            }
        }
        throw new IllegalArgumentException("Cannot determine format of input retrieved from URL: " + url);
    }

}
