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
package de.perdian.apps.podcentral.model;

import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

public interface FeedCollection {

    List<? extends FeedCollectionItem> getItems();

    public default String toXmlString() {

        Element xmlRootElement = DocumentHelper.createElement("opml");
        xmlRootElement.addAttribute("version", "1.0");
        Element bodyElement = xmlRootElement.addElement("body");
        for (FeedCollectionItem item : this.getItems()) {
            Element outlineElement = bodyElement.addElement("outline");
            outlineElement.addAttribute("title", item.getTitle().getValue());
            outlineElement.addAttribute("htmlUrl", item.getWebsiteUrl().getValue());
            outlineElement.addAttribute("xmlUrl", item.getFeedUrl().getValue());
            outlineElement.addAttribute("type", "rss");
        }

        try {
            StringWriter stringWriter = new StringWriter();
            XMLWriter xmlWriter = new XMLWriter(stringWriter, OutputFormat.createPrettyPrint());
            xmlWriter.write(DocumentHelper.createDocument(xmlRootElement));
            return stringWriter.toString();
        } catch (IOException e) {
            throw new IllegalAccessError("Cannot export feed collection to XML");
        }

    }

}
