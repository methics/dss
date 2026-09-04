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

import eu.europa.esig.dss.xml.common.definition.DSSAttribute;
import org.w3c.dom.Node;

import java.util.Objects;

/**
 * Gets XPath expression item allowing to access an attribute value of the current element.
 * This class is normally used for attribute value extraction, as an alternative to {@code XPathQueryByAttribute}
 *
 */
public class XPathQueryAttributeItem extends AbstractXPathQueryItem {

    /** Defines an attribute value */
    private static final String ATTRIBUTE_PATH = "@";

    /** Attribute which value is to be accessed */
    private final DSSAttribute attribute;

    /**
     * Default constructor to extract an element containing the given attribute (any value is accepted)
     *
     * @param attribute {@link DSSAttribute}
     */
    public XPathQueryAttributeItem(final DSSAttribute attribute) {
        Objects.requireNonNull(attribute, "Attribute cannot be null!");
        this.attribute = attribute;
    }

    /**
     * Gets the corresponding DSSAttribute
     *
     * @return {@link DSSAttribute}
     */
    public DSSAttribute getAttribute() {
        return attribute;
    }

    @Override
    protected boolean process(Node node) {
        if (Node.ATTRIBUTE_NODE == node.getNodeType()) {
            return attribute.getAttributeName().equals(getLocalName(node));
        }
        return false;
    }

    @Override
    public boolean isElementRelated() {
        return false;
    }

    @Override
    public boolean isAttributeRelated() {
        return true;
    }

    @Override
    public String getQueryString() {
        return ATTRIBUTE_PATH + attribute.getAttributeName();
    }

}
