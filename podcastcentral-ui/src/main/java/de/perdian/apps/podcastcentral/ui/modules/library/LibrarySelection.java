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
package de.perdian.apps.podcastcentral.ui.modules.library;

import java.util.List;

import de.perdian.apps.podcastcentral.model.Episode;
import de.perdian.apps.podcastcentral.model.Feed;

/**
 * The {@code TreeTableSelectionModel} isn't enough to determine which feeds and episodes are *selected* for our
 * different usecases. For the FX part, "selected" simply means that a row (with either a feed or an episode) is
 * selected, but for our usecases we need other information as well. Even if an episode is selected, it doesn't mean
 * that it should be processed. For example the download operation should process all the feeds that are *selected*
 * (that part we get from the {@code TreeTableSelectionModel}) and that are not downloaded already (which we don't get
 * from the {@code TreeTableSelectionModel} but only from looking into the episodes themselves.
 *
 * Furthermore, if a feed is selected, we want to consider all of the episodes below that feed to be processed as if
 * they had been selected as well (which is also something that we don't get from the {@code TreeTableSelectionModel}).
 *
 * So the {@code LibrarySelection} not only captures the actually selected items but also precomputes new lists
 * of items for specific usecases.
 */

public interface LibrarySelection {

    List<Feed> getSelectedFeeds();
    List<Episode> getSelectedEpisodesDirectly();
    List<Episode> getSelectedEpisodesConsolidated();

}
