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
import eu.europa.esig.dss.xml.common.definition.DSSNamespace;
import org.w3c.dom.Node;

import java.util.Objects;

/**
 * Represents a single element to look for within an XPath expression.
 *
 */
public class XPathQueryElementItem extends AbstractXPathQueryItem {

    /** The namespace separator */
    private static final String COLON_PATH = ":";

    /**
     * Element representing the XPath expression part
     */
    private final DSSElement element;

    /**
     * Default constructor
     *
     * @param element {@link DSSElement}
     */
    public XPathQueryElementItem(final DSSElement element) {
        Objects.requireNonNull(element, "Element cannot be null!");
        this.element = element;
    }

    /**
     * Gets the DSSElement
     *
     * @return {@link DSSElement}
     */
    public DSSElement getElement() {
        return element;
    }

    @Override
    protected boolean process(Node node) {
        if (Node.ELEMENT_NODE == node.getNodeType()) {
            return element.isSameTagName(node.getLocalName()) && (element.getURI() == null || element.getURI().equals(node.getNamespaceURI()));
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
        StringBuilder sb = new StringBuilder();
        DSSNamespace namespace = element.getNamespace();
        if (namespace != null) {
            sb.append(namespace.getPrefix());
            sb.append(COLON_PATH);
        }
        sb.append(element.getTagName());
        return sb.toString();
    }

}
