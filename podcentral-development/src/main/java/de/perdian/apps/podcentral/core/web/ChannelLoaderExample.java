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

import de.perdian.apps.podcentral.core.model.ChannelInput;
import de.perdian.apps.podcentral.retrieval.ChannelInputFactory;

public class ChannelLoaderExample {

    private static final Logger log = LoggerFactory.getLogger(ChannelLoaderExample.class);

    public static void main(String[] args) throws Exception {

        ChannelInput channelInput = ChannelInputFactory.getChannelInput("https://podcasts.files.bbci.co.uk/w13xttx2.rss");
        log.info("Loaded channel: {}", channelInput);

    }

}