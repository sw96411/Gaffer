/*
 * Copyright 2017-2020 Crown Copyright
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package uk.gov.gchq.gaffer.data.graph.function.walk;

import uk.gov.gchq.gaffer.data.element.Edge;
import uk.gov.gchq.gaffer.data.graph.Walk;
import uk.gov.gchq.koryphe.Since;
import uk.gov.gchq.koryphe.Summary;
import uk.gov.gchq.koryphe.function.KorypheFunction;

import java.util.Set;

/**
 * An {@code ExtractWalkEdges} is a utility {@link KorypheFunction} for extracting the {@link java.util.List} of
 * {@link Set}s of Gaffer {@link Edge}s, from a provided {@link Walk} object.
 */
@Since("1.2.0")
@Summary("Extracts the sets of edges from a Walk")
public class ExtractWalkEdges extends KorypheFunction<Walk, Iterable<Set<Edge>>> {
    @Override
    public Iterable<Set<Edge>> apply(final Walk walk) {
        if (null == walk) {
            throw new IllegalArgumentException("Walk cannot be null");
        }
        return walk.getEdges();
    }
}
