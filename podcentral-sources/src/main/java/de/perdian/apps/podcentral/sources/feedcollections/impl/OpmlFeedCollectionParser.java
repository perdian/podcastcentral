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
package de.perdian.apps.podcentral.sources.feedcollections.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.Node;
import org.dom4j.io.SAXReader;

import de.perdian.apps.podcentral.model.FeedCollection;
import de.perdian.apps.podcentral.sources.feedcollections.FeedCollectionParser;

public class OpmlFeedCollectionParser implements FeedCollectionParser {

    @Override
    public FeedCollection parseFeedCollectionData(byte[] data, String mimeType) throws IOException {
        if ("text/x-opml".equalsIgnoreCase(mimeType)) {
            return this.parseFeedCollectionDataFromXml(data, true);
        } else if ("application/xml".equalsIgnoreCase(mimeType) || "text/xml".equalsIgnoreCase(mimeType)) {
            return this.parseFeedCollectionDataFromXml(data, false);
        } else {
            return null;
        }
    }

    private FeedCollection parseFeedCollectionDataFromXml(byte[] data, boolean throwExceptionIfInvalid) throws IOException {
        try {
            SAXReader saxReader = new SAXReader();
            Document xmlDocument = saxReader.read(new ByteArrayInputStream(data));
            if (!"opml".equalsIgnoreCase(xmlDocument.getRootElement().getName())) {
                if (throwExceptionIfInvalid) {
                    throw new IllegalArgumentException("Expected 'opml' root element, but found: " + xmlDocument.getRootElement().getName());
                } else {
                    return null;
                }
            } else {
                List<OpmlFeedCollectionItem> feedCollectionItems = new ArrayList<>();
                for (Node outlineNode : xmlDocument.getRootElement().selectNodes("//body/outline")) {
                    Element outlineElement = (Element)outlineNode;
                    OpmlFeedCollectionItem feedCollectionItem = new OpmlFeedCollectionItem();
                    feedCollectionItem.getFeedUrl().setValue(outlineElement.attributeValue("xmlUrl"));
                    feedCollectionItem.getTitle().setValue(outlineElement.attributeValue("title"));
                    feedCollectionItem.getWebsiteUrl().setValue(outlineElement.attributeValue("htmlUrl"));
                    feedCollectionItems.add(feedCollectionItem);
                }
                return new OpmlFeedCollection(feedCollectionItems);
            }
        } catch (DocumentException e) {
            throw new IOException("Invalid XML content", e);
        }
    }

}
