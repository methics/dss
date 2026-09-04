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
package eu.europa.esig.dss.xml.common.xpath;

import eu.europa.esig.dss.xml.common.xpath.item.XPathQueryItem;
import eu.europa.esig.dss.xml.common.xpath.item.XPathQueryParameter;

import java.util.List;

/**
 * Abstract representation of the XPathChain expression. Contains common code and implementation.
 *
 */
public abstract class AbstractXPathQuery implements XPathQuery {

    /** Defines the next element */
    private static final String SLASH_PATH = "/";

    /** Defines the enclosed definition start */
    private static final String OPEN_SQUARE_BRACKET = "[";

    /** Defines the enclosed definition end */
    private static final String CLOSE_SQUARE_BRACKET = "]";

    /** The first part of the XPath expression chain */
    private XPathQueryItem firstItem;

    /** The last cached copy of the XPath expression (used internally) */
    private XPathQueryItem currentItem;

    /**
     * Default constructor
     *
     */
    protected AbstractXPathQuery() {
        // empty
    }

    @Override
    public XPathQueryItem getFirstXPathQueryItem() {
        return firstItem;
    }

    @Override
    public XPathQuery setNextItem(XPathQueryItem nextItem) {
        if (firstItem == null) {
            firstItem = currentItem = nextItem;
        } else {
            currentItem = currentItem.setNextItem(nextItem);
        }
        return this;
    }

    @Override
    public boolean isEmpty() {
        return firstItem == null;
    }

    @Override
    public String getQueryString() {
        StringBuilder sb = new StringBuilder();

        String xPathPreamble = getXPathPreamble();
        if (xPathPreamble != null) {
            sb.append(xPathPreamble);
        }

        XPathQueryItem item = firstItem;
        while (item != null) {
            if (item.isEmpty()) {
                item = item.nextItem();
                continue;
            }

            if (item != firstItem) {
                sb.append(SLASH_PATH);
            }
            sb.append(item.getQueryString());

            List<XPathQueryParameter> parameters = item.getParameters();
            if (parameters != null && !parameters.isEmpty()) {
                for (XPathQueryParameter parameter : parameters) {
                    sb.append(OPEN_SQUARE_BRACKET);
                    sb.append(parameter.getQueryString());
                    sb.append(CLOSE_SQUARE_BRACKET);
                }
            }
            item = item.nextItem();
        }

        return sb.toString();
    }

    /**
     * Gets the beginning string of the XPath expression
     *
     * @return {@link String}
     */
    protected abstract String getXPathPreamble();

    @Override
    public String toString() {
        return String.format("%s : %s", getClass().getSimpleName(), getQueryString());
    }

}
