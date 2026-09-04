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
package eu.europa.esig.dss.xml.common.xpath.item;

import org.w3c.dom.Node;

import java.util.List;

/**
 * Represents a single XPath expression item.
 *
 */
public interface XPathQueryItem {

    /**
     * Gets the next XPath chain item, if any
     *
     * @return {@link XPathQueryItem} if next chain item is present, FALSE otherwise
     */
    XPathQueryItem nextItem();

    /**
     * Sets the next {@code eu.europa.esig.dss.xml.common.definition.xpath.item.XPathChainItem}
     * as part of the XPath expression
     *
     * @param nextItem {@link XPathQueryItem} to set
     * @return {@link XPathQueryItem} that has been set
     */
    XPathQueryItem setNextItem(XPathQueryItem nextItem);

    /**
     * This method verifies whether the given {@code node} matches the XPathQueryItem
     *
     * @param node {@link Node} to be evaluated against the XPath Query Item
     * @return TRUE if the Node matches the value, FALSE otherwise
     */
    boolean matchNode(Node node);

    /**
     * Adds a parameter to the given XPath query item
     *
     * @param parameter {@link XPathQueryParameter}
     */
    void addParameter(XPathQueryParameter parameter);

    /**
     * Gets parameters related to the given XPathQueryItem, if any
     *
     * @return a list of {@link XPathQueryParameter}s
     */
    List<XPathQueryParameter> getParameters();

    /**
     * Gets whether XPath query item is related to an Element node processing
     *
     * @return TRUE if XPath query item is related to an Element node processing, FALSE otherwise
     */
    boolean isElementRelated();

    /**
     * Gets whether XPath query item is related to an Attribute node processing
     *
     * @return TRUE if XPath query item is related to an Attribute node processing, FALSE otherwise
     */
    boolean isAttributeRelated();

    /**
     * Gets whether the XPath query item is empty
     *
     * @return TRUE if the XPath query item is empty, FALSE otherwise
     */
    boolean isEmpty();

    /**
     * Gets a string representation of the XPath expression chain item
     *
     * @return {@link String}
     */
    String getQueryString();

}
