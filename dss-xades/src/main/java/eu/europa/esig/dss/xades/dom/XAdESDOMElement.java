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
package eu.europa.esig.dss.xades.dom;

import eu.europa.esig.dss.xades.definition.XAdESPath;
import org.w3c.dom.Element;

import java.util.List;

/**
 * This class represents a wrapper for a {@code org.w3c.dom.Element} object for a XAdES signature
 *
 */
public class XAdESDOMElement {

    /** Owner document */
    private final XAdESDOMDocument ownerDocument;

    /** XML DOM element */
    private final Element element;

    /**
     * Default constructor
     *
     * @param element {@link Element}
     * @param ownerDocument {@link XAdESDOMDocument}
     */
    public XAdESDOMElement(final Element element, final XAdESDOMDocument ownerDocument) {
        this.ownerDocument = ownerDocument;
        this.element = element;
    }

    /**
     * Gets the XML DOM Element
     *
     * @return {@link Element}
     */
    public Element getElement() {
        return element;
    }

    /**
     * Gets the owner document
     *
     * @return {@link XAdESDOMDocument}
     */
    public XAdESDOMDocument getOwnerDocument() {
        return ownerDocument;
    }

    /**
     * Gets a list of registered XAdES Path holders
     *
     * @return a list of {@link XAdESPath}s
     */
    public List<XAdESPath> getXAdESPathHolders() {
        return ownerDocument.getXAdESPathHolders();
    }

}
