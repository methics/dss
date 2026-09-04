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

import eu.europa.esig.dss.xml.common.definition.DSSElement;
import org.w3c.dom.Node;

import java.util.Objects;

/**
 * Represents an item within an XPath expression filtering out elements with a particular parent element.
 *
 */
public class XPathQueryNotChildOfParameter extends AbstractXPathQueryParameter {

    /** The beginning string of the "not child of" condition */
    private static final String NOT_PARENT_CONDITION_START = "not(parent::";

    /** The end string of the "not child of" condition */
    private static final String NOT_PARENT_CONDITION_END = ")";

    /**
     * Element item representing the parent element, the current element shall not belong to
     */
    private final XPathQueryElementItem elementItem;

    /**
     * Default constructor
     *
     * @param parentElement {@link DSSElement} to be avoided
     */
    public XPathQueryNotChildOfParameter(final DSSElement parentElement) {
        Objects.requireNonNull(parentElement, "Parent element cannot be null!");
        this.elementItem = new XPathQueryElementItem(parentElement);
    }

    /**
     * Gets the parent element to be avoided
     *
     * @return {@link DSSElement}
     */
    public DSSElement getParentElement() {
        return elementItem.getElement();
    }

    @Override
    protected boolean process(Node node) {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            Node parentNode = node.getParentNode();
            return !elementItem.matchNode(parentNode);
        }
        return false;
    }

    @Override
    public boolean isElementRelated() {
        return true;
    }

    @Override
    public boolean isAttributeRelated() {
        return false;
    }

    @Override
    public String getQueryString() {
        return NOT_PARENT_CONDITION_START + elementItem.getQueryString() + NOT_PARENT_CONDITION_END;
    }

}
