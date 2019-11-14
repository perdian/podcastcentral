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
package de.perdian.apps.podcastcentral.sources.feeds.support;

import java.util.List;
import java.util.Optional;

import org.dom4j.Node;
import org.dom4j.XPathException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XmlHelper {

    private static final Logger log = LoggerFactory.getLogger(XmlHelper.class);

    public static Optional<String> getFirstMatchingValue(Node parentNode, List<String> xpathExpressions) {
        for (String xpathExpression : xpathExpressions) {
            try {
                Node selectedNode = parentNode.selectSingleNode(xpathExpression);
                String selectedText = selectedNode == null ? null : selectedNode.getText();
                if (selectedText != null && !selectedText.strip().isEmpty()) {
                    return Optional.of(selectedText.strip());
                }
            } catch (XPathException e) {
                log.warn("Cannot evaluate XPath expression: {}", xpathExpression);
            }
        }
        return Optional.empty();
    }

}
