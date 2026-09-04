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
package eu.europa.esig.dss.cbades.cbor;

import co.nstant.in.cbor.model.AbstractFloat;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.DoublePrecisionFloat;
import co.nstant.in.cbor.model.NegativeInteger;
import co.nstant.in.cbor.model.SimpleValue;
import co.nstant.in.cbor.model.SimpleValueType;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;

/**
 * A wrapper for a one-dimensional CBOR object
 *
 */
public class CBORSimpleObject extends AbstractCBORObject<DataItem> {

    /**
     * Creates a new CBOR object from the given {@code dataItem}
     *
     * @param dataItem {@link DataItem}
     */
    public CBORSimpleObject(DataItem dataItem) {
        super(dataItem);
    }

    @Override
    public Long getValueAsLong() {
        if (isUnsignedInteger()) {
            return ((UnsignedInteger) toDataItem()).getValue().longValue();
        } else if (isNegativeInteger()) {
            return ((NegativeInteger) toDataItem()).getValue().longValue();
        }
        return null;
    }

    @Override
    public Double getValueAsDouble() {
        if (isDoublePrecisionFloat()) {
            return ((DoublePrecisionFloat) toDataItem()).getValue();
        } else if (isFloatPrecisionFloat()) {
            return (double) ((AbstractFloat) toDataItem()).getValue();
        }
        return null;
    }

    @Override
    public String getValueAsString() {
        if (isUnicodeString()) {
            return ((UnicodeString) toDataItem()).getString();
        }
        return null;
    }

    @Override
    public Boolean getValueAsBoolean() {
        if (isBoolean()) {
            SimpleValueType simpleValueType = ((SimpleValue) toDataItem()).getSimpleValueType();
            switch (simpleValueType) {
                case TRUE:
                    return true;
                case FALSE:
                    return false;
                default:
                    return null;
            }
        }
        return null;
    }

}
