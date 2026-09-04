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

import java.util.Collections;
import java.util.List;

/**
 * This XPath Query item is used to indicate the end of an XPath expression.
 * The use of the item is optional, and it does not impact the XPath Query processing,
 * but does not allow extension of the XPath Query with other rules or items.
 *
 */
public class XPathQueryEndItem implements XPathQueryItem {

    /**
     * Default constructor
     */
    public XPathQueryEndItem() {
        // empty
    }

    @Override
    public XPathQueryItem nextItem() {
        return null;
    }

    @Override
    public XPathQueryItem setNextItem(XPathQueryItem nextItem) {
        throw new UnsupportedOperationException("Unable to continue XPath query after the XPathQueryEnd item.");
    }

    @Override
    public boolean matchNode(Node node) {
        return true;
    }

    @Override
    public void addParameter(XPathQueryParameter parameter) {
        throw new UnsupportedOperationException("Unable to add parameters to the XPathQueryEnd item.");
    }

    @Override
    public List<XPathQueryParameter> getParameters() {
        return Collections.emptyList();
    }

    @Override
    public boolean isElementRelated() {
        return true;
    }

    @Override
    public boolean isAttributeRelated() {
        return true;
    }

    @Override
    public boolean isEmpty() {
        return true;
    }

    @Override
    public String getQueryString() {
        return "";
    }

}
