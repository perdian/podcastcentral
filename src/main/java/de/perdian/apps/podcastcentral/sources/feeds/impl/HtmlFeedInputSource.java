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
package de.perdian.apps.podcastcentral.sources.feeds.impl;

import java.io.IOException;
import java.net.URI;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.sources.feeds.FeedInputSource;

public class HtmlFeedInputSource implements FeedInputSource {

    @Override
    public FeedInput loadFeedInput(String data, String contentType, URL sourceUrl) throws IOException {
        if ("text/html".equalsIgnoreCase(contentType)) {
            Document htmlDocument = Jsoup.parse(data);
            Element linkElement = htmlDocument.selectFirst("link[rel=\"alternate\"][type=\"application/rss+xml\"]");
            String feedUrl = linkElement == null ? null : linkElement.attr("href");
            if (StringUtils.isNotEmpty(feedUrl)) {
                return new RssFeedInputSource().loadFeedInput(IOUtils.toString(URI.create(feedUrl).toURL(), "UTF-8"), "application/rss+xml", URI.create(feedUrl).toURL());
            }
        }
        return null;
    }

}
