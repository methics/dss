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

import co.nstant.in.cbor.model.Array;
import co.nstant.in.cbor.model.ByteString;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.DoublePrecisionFloat;
import co.nstant.in.cbor.model.NegativeInteger;
import co.nstant.in.cbor.model.SimpleValue;
import co.nstant.in.cbor.model.SimpleValueType;
import co.nstant.in.cbor.model.SinglePrecisionFloat;
import co.nstant.in.cbor.model.UnicodeString;
import co.nstant.in.cbor.model.UnsignedInteger;
import eu.europa.esig.dss.spi.DSSUtils;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

/**
 * Factory to create a {@code eu.europa.esig.dss.cbades.cbor.CBORObject} from a value
 *
 */
public class CBORObjectFactory {

    /**
     * Default constructor
     */
    private CBORObjectFactory() {
        // empty
    }

    /**
     * Instantiates a new CBOR object from the given {@code DataItem}
     *
     * @param object {@link DataItem}
     * @return {@link CBORObject}
     */
    public static CBORObject toCBORObject(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof CBORObject) {
            return (CBORObject) object;
        }
        DataItem dataItem;
        if (object instanceof DataItem) {
            dataItem = (DataItem) object;
        } else {
            dataItem = toDataItem(object);
        }
        switch (dataItem.getMajorType()) {
            case MAP:
                return new CBORMap((co.nstant.in.cbor.model.Map) dataItem);
            case ARRAY:
                return new CBORArray((co.nstant.in.cbor.model.Array) dataItem);
            case BYTE_STRING:
                return new CBORByteString((co.nstant.in.cbor.model.ByteString) dataItem);
            case TAG:
                return new CBORTag((co.nstant.in.cbor.model.Tag) dataItem);
            case SPECIAL:
                if (dataItem instanceof SimpleValue) {
                    SimpleValue simpleValue = (SimpleValue) dataItem;
                    if (SimpleValueType.NULL == simpleValue.getSimpleValueType()) {
                        return new CBORNull(simpleValue);
                    }
                }
                return new CBORSimpleObject(dataItem);
            default:
                return new CBORSimpleObject(dataItem);
        }
    }

    /**
     * This method coverts the given object to a DataItem instance, corresponding to the object's format
     *
     * @param object to be converted
     * @return {@link DataItem}
     */
    public static DataItem toDataItem(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof DataItem) {
            return (DataItem) object;

        } else if (object instanceof CBORObject) {
            CBORObject cborObject = (CBORObject) object;
            return cborObject.toDataItem();

        } else if (object instanceof Integer) {
            int intNumber = (Integer) object;
            return intNumber >= 0 ? new UnsignedInteger(intNumber) : new NegativeInteger(intNumber);

        } else if (object instanceof Long) {
            long longNumber = (Long) object;
            return longNumber >= 0 ? new UnsignedInteger(longNumber) : new NegativeInteger(longNumber);

        } else if (object instanceof Double) {
            double doubleNumber = (Double) object;
            return new DoublePrecisionFloat(doubleNumber);

        } else if (object instanceof Float) {
            float floatNumber = (Float) object;
            return new SinglePrecisionFloat(floatNumber);

        } else if (object instanceof String) {
            String str = (String) object;
            return new UnicodeString(str);

        } else if (object instanceof byte[]) {
            byte[] byteArray = (byte[]) object;
            return new ByteString(byteArray);

        } else if (object instanceof Boolean) {
            boolean value = (boolean) object;
            SimpleValueType simpleValueType = value ? SimpleValueType.TRUE : SimpleValueType.FALSE;
            return new SimpleValue(simpleValueType);

        } else if (object instanceof Date) {
            Date date = (Date) object;
            UnicodeString unicodeString = new UnicodeString(DSSUtils.formatDateToRFC(date));
            unicodeString.setTag(0);
            return unicodeString;

        } else if (object instanceof Collection<?>) {
            Collection<?> collection = (Collection<?>) object;
            Array cborArray = new Array(collection.size());
            collection.forEach(l -> cborArray.add(CBORObjectFactory.toDataItem(l)));
            return cborArray;

        } else if (object instanceof Map<?, ?>) {
            Map<?, ?> map = (Map<?, ?>) object;
            co.nstant.in.cbor.model.Map cborMap = new co.nstant.in.cbor.model.Map(map.size());
            map.forEach((k, v) -> cborMap.put(toDataItem(k), toDataItem(v)));
            return cborMap;

        } else {
            throw new UnsupportedOperationException(
                    String.format("The object of class '%s' is not yet supported!", object.getClass().getName()));
        }
    }

}
