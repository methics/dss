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

import eu.europa.esig.dss.xml.common.definition.xmldsig.XMLDSigAttribute;

/**
 * Builds an XPath expression part for an element retrieval by any identifier type
 * (suitable types are: 'Id', 'ID', 'id').
 */
public class XPathQueryIdentifierParameter extends XPathQueryAttributeParameter {

    /**
     * Default constructor
     *
     * @param idValue {@link String}
     */
    public XPathQueryIdentifierParameter(final String idValue) {
        super(XMLDSigAttribute.ID, idValue, true);
    }

    /**
     * Gets the ID String value
     *
     * @return {@link String} ID
     */
    public String getId() {
        return getAttributeValue();
    }

}
