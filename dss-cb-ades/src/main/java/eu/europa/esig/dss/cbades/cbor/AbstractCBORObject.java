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
import co.nstant.in.cbor.model.MajorType;
import co.nstant.in.cbor.model.SimpleValue;
import co.nstant.in.cbor.model.SimpleValueType;
import co.nstant.in.cbor.model.Tag;

import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * An abstract implementation of a CBOR object wrapper
 *
 * @param <D> the implementation of {@link DataItem}
 */
public abstract class AbstractCBORObject<D extends DataItem> implements CBORObject {

    /** A CBOR object implementation */
    private final D dataItem;

    /**
     * Creates a new CBOR object from the given {@code dataItem}
     *
     * @param dataItem {@link DataItem}
     */
    protected AbstractCBORObject(final D dataItem) {
        this.dataItem = dataItem;
    }

    @Override
    public boolean isTagged() {
        return dataItem.hasTag();
    }

    @Override
    public CBORTag getTag() {
        return new CBORTag(dataItem.getTag());
    }

    @Override
    public void setTag(long value) {
        dataItem.setTag(new Tag(value));
    }

    @Override
    public D toDataItem() {
        return dataItem;
    }

    @Override
    public boolean isUnsignedInteger() {
        return MajorType.UNSIGNED_INTEGER == dataItem.getMajorType();
    }

    @Override
    public boolean isNegativeInteger() {
        return MajorType.NEGATIVE_INTEGER == dataItem.getMajorType();
    }

    @Override
    public boolean isFloatingPointNumber() {
        return isDoublePrecisionFloat() || isFloatPrecisionFloat();
    }

    /**
     * Returns whether the current CBOR object is of Special type categorized by an additional type
     * as a double precision point float number (additional information 27).
     *
     * @return TRUE if the current CBOR object is of Special double precision float number type, FALSE otherwise
     */
    protected boolean isDoublePrecisionFloat() {
        return MajorType.SPECIAL == dataItem.getMajorType() && dataItem instanceof DoublePrecisionFloat;
    }

    /**
     * Returns whether the current CBOR object is of Special type categorized by an additional type
     * as a floating precision number (additional information 25 or 26).
     *
     * @return TRUE if the current CBOR object is of Special floating precision number type, FALSE otherwise
     */
    protected boolean isFloatPrecisionFloat() {
        return MajorType.SPECIAL == dataItem.getMajorType() && dataItem instanceof AbstractFloat;
    }

    @Override
    public boolean isByteString() {
        return MajorType.BYTE_STRING == dataItem.getMajorType();
    }

    @Override
    public boolean isUnicodeString() {
        return MajorType.UNICODE_STRING == dataItem.getMajorType();
    }

    @Override
    public boolean isBoolean() {
        if (isSimpleValue()) {
            SimpleValue simpleValue = (SimpleValue) dataItem;
            return SimpleValueType.TRUE == simpleValue.getSimpleValueType() || SimpleValueType.FALSE == simpleValue.getSimpleValueType();
        }
        return false;
    }

    @Override
    public boolean isNull() {
        if (isSimpleValue()) {
            SimpleValue simpleValue = (SimpleValue) dataItem;
            return SimpleValueType.NULL == simpleValue.getSimpleValueType();
        }
        return false;
    }

    private boolean isSimpleValue() {
        return MajorType.SPECIAL == dataItem.getMajorType() && dataItem instanceof SimpleValue;
    }

    @Override
    public boolean isMap() {
        return MajorType.MAP == dataItem.getMajorType();
    }

    @Override
    public boolean isArray() {
        return MajorType.ARRAY == dataItem.getMajorType();
    }


    @Override
    public Long getValueAsLong() {
        return null;
    }

    @Override
    public Double getValueAsDouble() {
        return null;
    }

    @Override
    public String getValueAsString() {
        return null;
    }

    @Override
    public Boolean getValueAsBoolean() {
        return null;
    }

    @Override
    public byte[] getValueAsBytes() {
        return null;
    }

    @Override
    public Map<CBORObject, CBORObject> getValueAsMap() {
        return null;
    }

    @Override
    public List<CBORObject> getValueAsList() {
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AbstractCBORObject<?> that = (AbstractCBORObject<?>) o;
        return Objects.equals(dataItem, that.dataItem);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(dataItem);
    }

    @Override
    public String toString() {
        return dataItem.toString();
    }

}
