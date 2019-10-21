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
package de.perdian.apps.podcentral.database.entities;

import java.io.Serializable;

public class FeedEntity implements Serializable {

    static final long serialVersionUID = 1L;

    private Long id = null;
    private String feedUrl = null;
    private String websiteUrl = null;
    private String title = null;
    private String subtitle = null;
    private String description = null;
    private String owner = null;
    private String ownerUrl = null;
    private String languageCode = null;
    private String imageUrl = null;
    private String category = null;

}
