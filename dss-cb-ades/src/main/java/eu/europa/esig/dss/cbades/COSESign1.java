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
package eu.europa.esig.dss.cbades;

import eu.europa.esig.dss.cbades.cbor.CBORArray;
import eu.europa.esig.dss.cbades.cbor.CBORNull;
import eu.europa.esig.dss.cbades.cbor.CBORObject;
import eu.europa.esig.dss.cbades.cbor.CBORUtils;
import eu.europa.esig.dss.enumerations.COSESignatureType;

/**
 * This class represents a COSE_Sign1 structure (Tag '18')
 *
 */
public class COSESign1 extends COSESignature implements COSESignStructure {

    /** Defines the encoding of the structure */
    private boolean tagged;

    /** The signed content */
    private CBORObject payload;

    /**
     * Instantiates an empty COSE_Sign1 structure object
     */
    public COSESign1() {
        // empty
    }

    @Override
    public boolean isTagged() {
        return tagged;
    }

    /**
     * Sets whether the signature structure is encoded as tagged or untagged
     *
     * @param tagged whether the signature structure is encoded as tagged or untagged
     */
    public void setTagged(boolean tagged) {
        this.tagged = tagged;
    }

    @Override
    public CBORObject getPayload() {
        if (payload == null) {
            payload = new CBORNull();
        }
        return payload;
    }

    /**
     * Sets the content to be signed
     *
     * @param payload {@link CBORObject}
     */
    public void setPayload(CBORObject payload) {
        this.payload = payload;
    }

    @Override
    public byte[] serialize() {
        final CBORArray coseSign1 = new CBORArray(4);
        if (tagged) {
            coseSign1.setTag(getContext().getTag());
        }
        coseSign1.add(getProtectedHeader().getByteString());
        coseSign1.add(getUnprotectedHeader());
        coseSign1.add(getPayload());
        coseSign1.add(getSignature());
        return CBORUtils.serializeCborObject(coseSign1);
    }

    @Override
    public COSESignatureType getContext() {
        return COSESignatureType.COSE_SIGN1;
    }

}
