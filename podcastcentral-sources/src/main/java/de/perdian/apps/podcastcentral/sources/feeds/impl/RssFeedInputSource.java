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
import java.io.StringReader;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.perdian.apps.podcastcentral.model.EpisodeData;
import de.perdian.apps.podcastcentral.model.FeedData;
import de.perdian.apps.podcastcentral.model.FeedInput;
import de.perdian.apps.podcastcentral.sources.feeds.FeedInputSource;
import de.perdian.apps.podcastcentral.sources.feeds.support.DateHelper;
import de.perdian.apps.podcastcentral.sources.feeds.support.RegexParsingHelper;
import de.perdian.apps.podcastcentral.sources.feeds.support.XmlHelper;
import okhttp3.Response;

/**
 * Implementation of the {@code FeedInputLoader} interface that evaluates the incoming input using the RSS
 * specification as template.
 *
 * @author Christian Seifert
 */

public class RssFeedInputSource implements FeedInputSource {

    private static final Logger log = LoggerFactory.getLogger(RssFeedInputSource.class);
    private static final RegexParsingHelper<Duration> DURATION_PARSING_HELPER = new RegexParsingHelper<Duration>()
        .add("([0-9]+)", matcher -> Duration.ofSeconds(Integer.parseInt(matcher.group(1))))
        .add("(\\d+)\\:(\\d+)", matcher -> Duration.ofSeconds(Long.parseLong(matcher.group(1), 10) * 60 + Long.parseLong(matcher.group(2), 10)))
        .add("(\\d+)\\:(\\d+)\\:(\\d+)", matcher -> Duration.ofSeconds(Long.parseLong(matcher.group(1), 10) * (60 * 60) + Long.parseLong(matcher.group(2), 10) * 60 + Long.parseLong(matcher.group(3), 10)));

    private List<String> validContentTypes = List.of("application/rss+xml", "application/xml");

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public FeedInput loadFeedInput(Response feedResponse) throws IOException {
        if (this.getValidContentTypes().contains(feedResponse.body().contentType().type() + "/" + feedResponse.body().contentType().subtype())) {
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new StringReader(feedResponse.body().string()));
                return this.parseFeedInputFromDocument(document, feedResponse.request().url().toString());
            } catch (DocumentException e) {
                log.debug("Cannot parser XML document", e);
                throw new IllegalArgumentException("Invalid XML content found", e);
            }
        } else {
            log.debug("Non-RSS content type found: '{}'. Parser will not try to load the document", feedResponse.body().contentType());
            return null;
        }
    }

    private FeedInput parseFeedInputFromDocument(Document document, String sourceUrl) {
        FeedInput feedInput = new FeedInput();
        feedInput.setData(this.parseFeedDataFromDocument(document, sourceUrl));
        feedInput.setEpisodes(this.parseEpisodeDataListFromDocument(document, sourceUrl));
        return feedInput;
    }

    private FeedData parseFeedDataFromDocument(Document document, String sourceUrl) {
        log.debug("Parsing feed response for feed: {}", sourceUrl);
        FeedData feedData = new FeedData();
        feedData.setUrl(XmlHelper.getFirstMatchingValue(document, List.of("//channel/itunes:new-feed-url")).orElse(sourceUrl));
        feedData.setWebsiteUrl(XmlHelper.getFirstMatchingValue(document, List.of("//channel/link")).orElse(null));
        feedData.setTitle(XmlHelper.getFirstMatchingValue(document, List.of("//channel/title")).orElse(null));
        feedData.setSubtitle(XmlHelper.getFirstMatchingValue(document, List.of("//channel/subtitle", "//channel/itunes:subtitle")).orElse(null));
        feedData.setDescription(XmlHelper.getFirstMatchingValue(document, List.of("//channel/description", "//channel/itunes:summary")).orElse(null));
        feedData.setOwner(XmlHelper.getFirstMatchingValue(document, List.of("//channel/itunes:owner/itunes:name")).orElse(null));
        feedData.setLanguageCode(XmlHelper.getFirstMatchingValue(document, List.of("//channel/language")).orElse(null));
        feedData.setImageUrl(XmlHelper.getFirstMatchingValue(document, List.of("//channel/image/url", "//channel/itunes:image/@href")).orElse(null));
        feedData.setCategory(XmlHelper.getFirstMatchingValue(document, List.of("//channel/itunes:category")).orElse(null));
        return feedData;
    }

    private List<EpisodeData> parseEpisodeDataListFromDocument(Document document, String sourceUrl) {
        List<Node> itemNodes = Optional.ofNullable(document.selectNodes("//channel/item")).orElseGet(Collections::emptyList);
        log.debug("Parsing {} episodes retrieved for feed: {}", itemNodes.size(), sourceUrl);
        List<EpisodeData> episodes = new ArrayList<>();
        for (Node itemNode : itemNodes) {
            episodes.add(this.parseEpisodeInputFromItemNode(itemNode));
        }
        return episodes;
    }

    private EpisodeData parseEpisodeInputFromItemNode(Node itemNode) {
        EpisodeData episodeData = new EpisodeData();
        episodeData.setGuid(XmlHelper.getFirstMatchingValue(itemNode, List.of("guid", "enclosure/@url", "media:content/@url")).orElse(null));
        episodeData.setContentType(XmlHelper.getFirstMatchingValue(itemNode, List.of("enclosure/@type", "media:content/@type")).orElse(null));
        episodeData.setContentUrl(XmlHelper.getFirstMatchingValue(itemNode, List.of("enclosure/@url", "media:content/@url")).orElse(null));
        episodeData.setDescription(XmlHelper.getFirstMatchingValue(itemNode, List.of("description", "itunes:summary")).orElse(null));
        episodeData.setDuration(XmlHelper.getFirstMatchingValue(itemNode, List.of("itunes:duration")).map(stringValue -> DURATION_PARSING_HELPER.parse(stringValue).orElse(null)).orElse(null));
        episodeData.setImageUrl(XmlHelper.getFirstMatchingValue(itemNode, List.of("image/url", "itunes:image/@href")).orElse(null));
        episodeData.setPublicationDate(XmlHelper.getFirstMatchingValue(itemNode, List.of("pubDate")).map(DateHelper::parseInstant).orElse(null));
        episodeData.setSize(XmlHelper.getFirstMatchingValue(itemNode, List.of("enclosure/@length", "media:content/@fileSize")).map(Long::valueOf).orElse(null));
        episodeData.setSubtitle(XmlHelper.getFirstMatchingValue(itemNode, List.of("itunes:subtitle")).orElse(null));
        episodeData.setTitle(XmlHelper.getFirstMatchingValue(itemNode, List.of("title")).orElse(null));
        return episodeData;
    }

    public List<String> getValidContentTypes() {
        return this.validContentTypes;
    }
    public void setValidContentTypes(List<String> validContentTypes) {
        this.validContentTypes = validContentTypes;
    }

}
