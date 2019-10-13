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
package de.perdian.apps.podcentral.core.web.impl;

import java.io.StringReader;
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

import de.perdian.apps.podcentral.core.model.Channel;
import de.perdian.apps.podcentral.core.model.Episode;
import de.perdian.apps.podcentral.core.web.ChannelSourceParser;
import de.perdian.apps.podcentral.core.web.support.XmlHelper;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Implementation of the {@code ChannelSourceParser} interface that evaluates the incoming input using the RSS
 * specification as template.
 *
 * @author Christian Seifert
 */

public class RssChannelSourceParser implements ChannelSourceParser {

    private static final Logger log = LoggerFactory.getLogger(RssChannelSourceParser.class);

    private List<String> validContentTypes = List.of("application/rss+xml", "application/xml");

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public Channel parseChannel(String content, String contentType, String sourceUrl) {
        int indexOfSemicolon = contentType.indexOf(";");
        String checkContentType = indexOfSemicolon > -1 ? contentType.substring(0, indexOfSemicolon) : contentType;
        if (this.getValidContentTypes().contains(checkContentType)) {
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new StringReader(content));
                return this.parseChannelFromDocument(document, sourceUrl);
            } catch (DocumentException e) {
                log.debug("Cannot parser XML document", e);
                throw new IllegalArgumentException("Invalid XML content found", e);
            }
        } else {
            log.debug("Non-RSS content type found: '{}'. Parser will not try to load the document", contentType);
            return null;
        }
    }

    private Channel parseChannelFromDocument(Document document, String sourceUrl) {
        log.debug("Parsing channel response for channel: {}", sourceUrl);
        Channel channel = new Channel();
        channel.setFeedUrl(new SimpleStringProperty(XmlHelper.getFirstMatchingValue(document, List.of("//channel/itunes:new-feed-url")).orElse(sourceUrl)));
        channel.setWebsiteUrl(new SimpleStringProperty(XmlHelper.getFirstMatchingValue(document, List.of("//channel/link")).orElse(null)));
        channel.setTitle(new SimpleStringProperty(XmlHelper.getFirstMatchingValue(document, List.of("//channel/title")).orElse(null)));
        channel.setSubtitle(new SimpleStringProperty(XmlHelper.getFirstMatchingValue(document, List.of("//channel/subtitle", "//channel/itunes:subtitle")).orElse(null)));
        channel.setDescription(new SimpleStringProperty(XmlHelper.getFirstMatchingValue(document, List.of("//channel/description", "//channel/itunes:summary")).orElse(null)));
        channel.setOwner(new SimpleStringProperty(XmlHelper.getFirstMatchingValue(document, List.of("//channel/itunes:owner/itunes:name")).orElse(null)));
        channel.setLanguageCode(new SimpleStringProperty(XmlHelper.getFirstMatchingValue(document, List.of("//channel/language")).orElse(null)));
        channel.setImageUrl(new SimpleStringProperty(XmlHelper.getFirstMatchingValue(document, List.of("//channel/image/url23", "//channel/itunes:image/@href")).orElse(null)));
        channel.setCategory(new SimpleStringProperty(XmlHelper.getFirstMatchingValue(document, List.of("//channel/itunes:category")).orElse(null)));
        channel.setEpisodes(this.parseChannelEpisodesFromDocument(document, sourceUrl));
        return channel;
    }

    private ObservableList<Episode> parseChannelEpisodesFromDocument(Document document, String sourceUrl) {
        List<Node> itemNodes = Optional.ofNullable(document.selectNodes("//channel/item")).orElseGet(Collections::emptyList);
        log.debug("Parsing {} episodes retrieved for channel: {}", itemNodes.size(), sourceUrl);
        ObservableList<Episode> episodes = FXCollections.observableArrayList();
        for (Node itemNode : itemNodes) {
            episodes.add(this.parseChannelEpisodeFromItemNode(itemNode));
        }
        return episodes;
    }

    private Episode parseChannelEpisodeFromItemNode(Node itemNode) {
        Episode episode = new Episode();
        return episode;
    }

    public List<String> getValidContentTypes() {
        return this.validContentTypes;
    }
    public void setValidContentTypes(List<String> validContentTypes) {
        this.validContentTypes = validContentTypes;
    }

}
