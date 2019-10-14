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
package de.perdian.apps.podcentral.retrieval.support;

import java.util.List;
import java.util.Optional;

import org.dom4j.Node;

public class XmlHelper {

    public static Optional<String> getFirstMatchingValue(Node parentNode, List<String> xpathExpressions) {
        for (String xpathExpression : xpathExpressions) {
            Node selectedNode = parentNode.selectSingleNode(xpathExpression);
            String selectedText = selectedNode == null ? null : selectedNode.getText();
            if (selectedText != null && !selectedText.strip().isEmpty()) {
                return Optional.of(selectedText.strip());
            }
        }
        return Optional.empty();
    }

}
