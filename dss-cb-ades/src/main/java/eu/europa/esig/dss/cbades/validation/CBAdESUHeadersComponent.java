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
package eu.europa.esig.dss.cbades.validation;

import eu.europa.esig.dss.cbades.cbor.CBORMap;
import eu.europa.esig.dss.cbades.cbor.CBORObject;
import eu.europa.esig.dss.cbades.cbor.CBORUtils;

/**
 * Represents an item of the 'uHeaders' header array
 *
 */
public class CBAdESUHeadersComponent extends CBAdESAttribute {

    private static final long serialVersionUID = 468968604999660231L;

    /** The component in its original representation */
    private final CBORObject component;

    /**
     * Default constructor
     *
     * @param component  {@link CBORObject} original representation of the component
     * @param headerId   {@link CBORObject} header id
     * @param value      {@link CBORObject} value
     * @param identifier {@link CBAdESAttributeIdentifier}
     */
    CBAdESUHeadersComponent(CBORObject component, CBORObject headerId, CBORObject value, CBAdESAttributeIdentifier identifier) {
        super(headerId, value);
        this.component = component;
        this.identifier = identifier;
    }

    /**
     * Builds {@code CBAdESUHeadersComponent} from the 'uHeaders' array entry
     *
     * @param component represents the component of the 'uHeaders' array
     * @param order defines the position number of the component in the 'uHeaders' array
     * @return {@link CBAdESUHeadersComponent}
     */
    public static CBAdESUHeadersComponent build(CBORObject component, int order) {
        CBORMap cborMap = CBORUtils.parseUHeadersEntry(component);
        if (cborMap != null && !cborMap.isEmpty()) {
            // one entry is expected
            CBORObject key = cborMap.getKeys().iterator().next();
            CBORObject value = cborMap.getHeader(key);
            CBAdESAttributeIdentifier identifier = CBAdESAttributeIdentifier.build(key, value, order);
            return new CBAdESUHeadersComponent(component, key, value, identifier);
        }
        return null;
    }

    /**
     * Builds the {@code EtsiUComponent} from the given parameters
     *
     * @param headerKey {@link CBORObject} name of the 'uHeaders' array component
     * @param value {@link CBORObject} represents the value of the component
     * @param identifier {@link CBAdESAttributeIdentifier}
     * @return {@link CBAdESUHeadersComponent}
     */
    public static CBAdESUHeadersComponent build(CBORObject headerKey, CBORObject value, CBAdESAttributeIdentifier identifier) {
        CBORObject component = createUHeadersComponent(headerKey, value);
        return new CBAdESUHeadersComponent(component, headerKey, value, identifier);
    }

    /**
     * Returns an 'uHeaders' component in the defined representation
     *
     * @param key              {@link CBORObject} header name
     * @param value            {@link CBORObject} object
     * @return {@link CBORObject} 'uHeaders' component
     */
    private static CBORObject createUHeadersComponent(CBORObject key, CBORObject value) {
        CBORMap cborMap = new CBORMap();
        cborMap.put(key, value);
        return cborMap.getByteString();
    }

    /**
     * Gets the current component in {@code CBORObject} representation
     *
     * @return {@link CBORObject}
     */
    public CBORObject getComponent() {
        return component;
    }

}
