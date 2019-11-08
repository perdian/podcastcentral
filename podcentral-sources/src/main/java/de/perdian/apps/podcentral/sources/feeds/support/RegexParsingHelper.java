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
package de.perdian.apps.podcentral.sources.feeds.support;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexParsingHelper<T> {

    private List<RegexParsingHelperItem<T>> items = null;

    public RegexParsingHelper() {
        this.setItems(new ArrayList<>());
    }

    public RegexParsingHelper<T> add(String pattern, Function<Matcher, T> matcherToResultFunction) {
        this.getItems().add(new RegexParsingHelperItem<>(Pattern.compile(pattern), matcherToResultFunction));
        return this;
    }

    public Optional<T> parse(String input) {
        for (RegexParsingHelperItem<T> item : this.getItems()) {
            Matcher itemMatcher = item.getPattern().matcher(input);
            if (itemMatcher.matches()) {
                return Optional.ofNullable(item.getMatcherToResultFunction().apply(itemMatcher));
            }
        }
        return Optional.empty();
    }

    static class RegexParsingHelperItem<T> {

        private Pattern pattern = null;
        private Function<Matcher, T> matcherToResultFunction = null;

        RegexParsingHelperItem(Pattern pattern, Function<Matcher, T> matcherToResultFunction) {
            this.setPattern(pattern);
            this.setMatcherToResultFunction(matcherToResultFunction);
        }

        Pattern getPattern() {
            return this.pattern;
        }
        void setPattern(Pattern pattern) {
            this.pattern = pattern;
        }

        Function<Matcher, T> getMatcherToResultFunction() {
            return this.matcherToResultFunction;
        }
        void setMatcherToResultFunction(Function<Matcher, T> matcherToResultFunction) {
            this.matcherToResultFunction = matcherToResultFunction;
        }

    }

    private List<RegexParsingHelperItem<T>> getItems() {
        return this.items;
    }
    private void setItems(List<RegexParsingHelperItem<T>> items) {
        this.items = items;
    }

}
