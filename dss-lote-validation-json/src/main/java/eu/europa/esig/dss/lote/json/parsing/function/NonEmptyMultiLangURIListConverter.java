/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * <p>
 * This file is part of the "DSS - Digital Signature Services" project.
 * <p>
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * <p>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.lote.json.parsing.function;

import eu.europa.esig.dss.jades.DSSJsonUtils;
import eu.europa.esig.dss.lote.json.parsing.JsonLoTEHeaderParameterNames;
import eu.europa.esig.dss.utils.Utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * This class converts a Json list of extracted "NonEmptyMultiLangURI" objects to a Java map
 *
 */
public class NonEmptyMultiLangURIListConverter implements Function<List<?>, Map<String, List<String>>> {

    /** The predicate to be used */
    private final Predicate<String> predicate;

    /**
     * Default constructor (selects all)
     */
    public NonEmptyMultiLangURIListConverter() {
        // select all
        this(x -> true);
    }

    /**
     * Default constructor with a filter predicate
     *
     * @param predicate {@link Predicate}
     */
    public NonEmptyMultiLangURIListConverter(Predicate<String> predicate) {
        this.predicate = predicate;
    }

    @Override
    public Map<String, List<String>> apply(List<?> original) {
        Map<String, List<String>> result = new HashMap<>();
        if (Utils.isCollectionNotEmpty(original)) {
            for (Object multiLangStringObject : original) {
                Map<?, ?> multiLangString = DSSJsonUtils.toMap(multiLangStringObject);
                if (Utils.isMapNotEmpty(multiLangString)) {
                    final String lang = DSSJsonUtils.getAsString(multiLangString, JsonLoTEHeaderParameterNames.LANG);
                    final String uriValue = DSSJsonUtils.getAsString(multiLangString, JsonLoTEHeaderParameterNames.URI_VALUE);
                    if (predicate.test(uriValue)) {
                        result.computeIfAbsent(lang, k -> new ArrayList<>()).add(uriValue);
                    }
                }
            }
        }
        return result;
    }

}
