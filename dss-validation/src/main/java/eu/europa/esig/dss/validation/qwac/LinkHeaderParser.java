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
package eu.europa.esig.dss.validation.qwac;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to parse the "Link" HTTP response header value, according to RFC 8288 requirements.
 *
 */
public class LinkHeaderParser {

    /**
     * Default constructor
     */
    public LinkHeaderParser() {
        // empty
    }

    /**
     * Parses a full Link header into 1..n LinkHeader objects.
     *
     * @param headerValueStr Link header value string
     * @return list of parsed LinkHeader entries (never null)
     */
    public List<LinkHeader> parse(String headerValueStr) {
        if (headerValueStr == null || headerValueStr.trim().isEmpty()) {
            throw new IllegalArgumentException("Link header cannot be null or empty");
        }

        List<LinkHeader> result = new ArrayList<>();

        // One header can have multiple links, separated by a comma
        // BUT commas inside quotes must not split entries
        List<String> linkValues = splitHeaderValues(headerValueStr);

        for (String linkValue : linkValues) {
            LinkHeader header = parseSingleLink(linkValue.trim());
            result.add(header);
        }

        return result;
    }

    /**
     * Splits a header by commas while respecting quoted values.
     */
    private List<String> splitHeaderValues(String headerValueStr) {
        List<String> values = new ArrayList<>();

        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < headerValueStr.length(); i++) {
            char c = headerValueStr.charAt(i);

            if (c == '"') {
                inQuotes = !inQuotes;
            }

            if (c == ',' && !inQuotes) {
                values.add(current.toString());
                current.setLength(0);
            } else {
                current.append(c);
            }
        }

        if (current.length() > 0) {
            values.add(current.toString());
        }

        return values;
    }

    /**
     * Parses a single <url>; param1=...; param2=...
     */
    private LinkHeader parseSingleLink(String headerValueStr) {
        if (!headerValueStr.startsWith("<")) {
            throw new IllegalArgumentException("Link entry must start with '<': " + headerValueStr);
        }

        int urlEnd = headerValueStr.indexOf('>');
        if (urlEnd == -1) {
            throw new IllegalArgumentException("Missing '>' in Link entry: " + headerValueStr);
        }

        String url = headerValueStr.substring(1, urlEnd).trim();
        Map<String, String> attributes = new HashMap<>();

        // Remaining attributes after >
        String attributesPart = headerValueStr.substring(urlEnd + 1).trim();

        if (!attributesPart.isEmpty()) {
            // Split by semicolons
            String[] parts = attributesPart.split(";");
            for (String part : parts) {
                part = part.trim();
                if (part.isEmpty()) continue;

                String[] keyValue = part.split("=", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim();
                    String value = keyValue[1].trim();
                    value = stripQuotes(value);
                    attributes.put(key, value);
                } else {
                    // attribute without "=", allowed by RFC 8288 (flags)
                    attributes.put(part, null);
                }
            }
        }

        LinkHeader header = new LinkHeader();
        header.setUrl(url);
        header.setAttributes(attributes);
        return header;
    }

    /**
     * Removes surrounding quotes if present
     */
    private String stripQuotes(String value) {
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
            return value.substring(1, value.length() - 1);
        }
        return value;
    }

    /**
     * Represents a parsed value of the "Link" HTTP response header
     *
     */
    public static class LinkHeader implements Serializable {

        private static final long serialVersionUID = 5652555158066131132L;

        /** The "Link" URL */
        private String url;

        /** Map of the "Link" header attributes */
        private Map<String, String> attributes;

        /**
         * Default constructor
         */
        protected LinkHeader() {
            // empty
        }

        /**
         * Gets the "Link" header value URL
         *
         * @return {@link String}
         */
        public String getUrl() {
            return url;
        }

        /**
         * Sets the "Link" header value URL
         *
         * @param url {@link String}
         */
        public void setUrl(String url) {
            this.url = url;
        }

        /**
         * Gets a map of "Link" header attributes
         *
         * @return a map of attributes
         */
        public Map<String, String> getAttributes() {
            return attributes;
        }

        /**
         * Sets a map of "Link" header attributes
         *
         * @param attributes a map of attributes
         */
        public void setAttributes(Map<String, String> attributes) {
            this.attributes = attributes;
        }

    }

}
