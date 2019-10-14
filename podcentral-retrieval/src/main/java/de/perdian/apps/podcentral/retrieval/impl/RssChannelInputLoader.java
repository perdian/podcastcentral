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
package de.perdian.apps.podcentral.retrieval.impl;

import java.io.StringReader;
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

import de.perdian.apps.podcentral.core.model.ChannelInput;
import de.perdian.apps.podcentral.core.model.EpisodeInput;
import de.perdian.apps.podcentral.retrieval.ChannelInputLoader;
import de.perdian.apps.podcentral.retrieval.support.XmlHelper;
import okhttp3.Response;

/**
 * Implementation of the {@code ChannelInputLoader} interface that evaluates the incoming input using the RSS
 * specification as template.
 *
 * @author Christian Seifert
 */

public class RssChannelInputLoader implements ChannelInputLoader {

    private static final Logger log = LoggerFactory.getLogger(RssChannelInputLoader.class);

    private List<String> validContentTypes = List.of("application/rss+xml", "application/xml");

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.SHORT_PREFIX_STYLE);
    }

    @Override
    public ChannelInput loadChannelInput(Response feedResponse) throws Exception {
        if (this.getValidContentTypes().contains(feedResponse.body().contentType().type() + "/" + feedResponse.body().contentType().subtype())) {
            try {
                SAXReader saxReader = new SAXReader();
                Document document = saxReader.read(new StringReader(feedResponse.body().string()));
                return this.parseChannelInputFromDocument(document, feedResponse.request().url().toString());
            } catch (DocumentException e) {
                log.debug("Cannot parser XML document", e);
                throw new IllegalArgumentException("Invalid XML content found", e);
            }
        } else {
            log.debug("Non-RSS content type found: '{}'. Parser will not try to load the document", feedResponse.body().contentType());
            return null;
        }
    }

    private ChannelInput parseChannelInputFromDocument(Document document, String sourceUrl) {
        log.debug("Parsing channel response for channel: {}", sourceUrl);
        ChannelInput channelInput = new ChannelInput();
        channelInput.setFeedUrl(XmlHelper.getFirstMatchingValue(document, List.of("//channel/itunes:new-feed-url")).orElse(sourceUrl));
        channelInput.setWebsiteUrl(XmlHelper.getFirstMatchingValue(document, List.of("//channel/link")).orElse(null));
        channelInput.setTitle(XmlHelper.getFirstMatchingValue(document, List.of("//channel/title")).orElse(null));
        channelInput.setSubtitle(XmlHelper.getFirstMatchingValue(document, List.of("//channel/subtitle", "//channel/itunes:subtitle")).orElse(null));
        channelInput.setDescription(XmlHelper.getFirstMatchingValue(document, List.of("//channel/description", "//channel/itunes:summary")).orElse(null));
        channelInput.setOwner(XmlHelper.getFirstMatchingValue(document, List.of("//channel/itunes:owner/itunes:name")).orElse(null));
        channelInput.setLanguageCode(XmlHelper.getFirstMatchingValue(document, List.of("//channel/language")).orElse(null));
        channelInput.setImageUrl(XmlHelper.getFirstMatchingValue(document, List.of("//channel/image/url23", "//channel/itunes:image/@href")).orElse(null));
        channelInput.setCategory(XmlHelper.getFirstMatchingValue(document, List.of("//channel/itunes:category")).orElse(null));
        channelInput.setEpisodes(this.parseEpisodeInputListFromDocument(document, sourceUrl));
        return channelInput;
    }

    private List<EpisodeInput> parseEpisodeInputListFromDocument(Document document, String sourceUrl) {
        List<Node> itemNodes = Optional.ofNullable(document.selectNodes("//channel/item")).orElseGet(Collections::emptyList);
        log.debug("Parsing {} episodes retrieved for channel: {}", itemNodes.size(), sourceUrl);
        List<EpisodeInput> episodes = new ArrayList<>();
        for (Node itemNode : itemNodes) {
            episodes.add(this.parseEpisodeInputFromItemNode(itemNode));
        }
        return episodes;
    }

    private EpisodeInput parseEpisodeInputFromItemNode(Node itemNode) {
        EpisodeInput episodeInput = new EpisodeInput();
        return episodeInput;
    }

    public List<String> getValidContentTypes() {
        return this.validContentTypes;
    }
    public void setValidContentTypes(List<String> validContentTypes) {
        this.validContentTypes = validContentTypes;
    }

}
