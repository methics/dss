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

import co.nstant.in.cbor.CborDecoder;
import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.model.DataItem;
import co.nstant.in.cbor.model.MajorType;
import eu.europa.esig.dss.spi.exception.IllegalInputException;
import eu.europa.esig.dss.utils.Utils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Internal wrapper of a CBOR Map object
 *
 */
public class CBORMap extends AbstractCBORObject<co.nstant.in.cbor.model.Map> {

    /** Serialized map */
    private CBORByteString serializedMap;

    /**
     * Constructor to create an empty map
     */
    public CBORMap() {
        this(new co.nstant.in.cbor.model.Map());
    }

    /**
     * Constructor to create a CBORMap from a Map implementation
     *
     * @param map {@link co.nstant.in.cbor.model.Map}
     */
    public CBORMap(final co.nstant.in.cbor.model.Map map) {
        super(map);
    }

    /**
     * Creates a CBORMap from a serialized map
     *
     * @param serializedMap {@link CBORByteString} containing an empty or serialized map
     */
    public CBORMap(final CBORByteString serializedMap) {
        this(parseByteStringHeader(serializedMap));
        this.serializedMap = serializedMap;
    }

    private static co.nstant.in.cbor.model.Map parseByteStringHeader(CBORByteString cborByteString) {
        try {
            List<DataItem> dataItems = CborDecoder.decode(cborByteString.getValueAsBytes());
            if (Utils.collectionSize(dataItems) == 0) {
                return new co.nstant.in.cbor.model.Map();
            } else if (Utils.collectionSize(dataItems) > 1) {
                throw new IllegalInputException("CBOR byte string root shall consist of one data object!");
            }
            DataItem dataItem = dataItems.iterator().next();
            if (MajorType.MAP != dataItem.getMajorType()) {
                throw new IllegalInputException("CBOR byte string's content shall be of Map type!");
            }
            return (co.nstant.in.cbor.model.Map) dataItem;

        } catch (CborException e) {
            throw new IllegalInputException(String.format("Unable to parse byte string a Map: %s", e.getMessage()), e);
        }
    }

    /**
     * Returns a serialized value of the protected header map
     *
     * @return {@link CBORByteString}
     */
    public CBORByteString getByteString() {
        if (serializedMap == null) {
            serializedMap = CBORUtils.toCborBtsrWrapped(this);
        }
        return serializedMap;
    }

    /**
     * Checks whether the map contains a header value for the given {@code key}
     *
     * @param key {@link CBORObject} value of the header key
     * @return TRUE if the map contains the header for the given key, FALSE otherwise
     */
    public boolean containsKey(CBORObject key) {
        return toDataItem().getKeys().contains(CBORObjectFactory.toDataItem(key));
    }

    /**
     * Returns header value for the given key
     *
     * @param key {@link Object} value of a key to get a map value for
     * @return {@link CBORObject}
     */
    public CBORObject getHeader(Object key) {
        DataItem value = toDataItem().get(CBORObjectFactory.toDataItem(key));
        if (value != null) {
            return CBORObjectFactory.toCBORObject(value);
        }
        return null;
    }

    @Override
    public Map<CBORObject, CBORObject> getValueAsMap() {
        final Map<CBORObject, CBORObject> result = new HashMap<>();
        for (CBORObject key : getKeys()) {
            CBORObject value = getHeader(key);
            result.put(key, value);
        }
        return result;
    }

    /**
     * Returns a list of all keys from the current CBOR Map
     *
     * @return a set of {@code CBORObject} keys
     */
    public Set<CBORObject> getKeys() {
        final Set<CBORObject> keys = new HashSet<>();
        for (DataItem keyDataItem : toDataItem().getKeys()) {
            CBORObject keyObject = CBORObjectFactory.toCBORObject(keyDataItem);
            keys.add(keyObject);
        }
        return keys;
    }

    /**
     * Gets a header value as {@code Boolean}. Returns NULL if the header is not found for the given key or
     * its value is not of Boolean type
     *
     * @param key {@link Object} value of the key
     * @return {@link Boolean} value of the header
     */
    public Boolean getAsBoolean(Object key) {
        CBORObject cborObject = getHeader(CBORObjectFactory.toCBORObject(key));
        if (cborObject != null && (cborObject.isBoolean())) {
            return cborObject.getValueAsBoolean();
        }
        return null;
    }

    /**
     * Gets a header value as {@code Long}. Returns NULL if the header is not found for the given key or
     * its value is not of UnsignedInteger or NegativeInteger type
     *
     * @param key {@link Object} value of the key
     * @return {@link Long} value of the header
     */
    public Long getAsLong(Object key) {
        CBORObject cborObject = getHeader(CBORObjectFactory.toCBORObject(key));
        if (cborObject != null && (cborObject.isUnsignedInteger() || cborObject.isNegativeInteger())) {
            return cborObject.getValueAsLong();
        }
        return null;
    }

    /**
     * Gets a header value as {@code Double}. Returns NULL if the header is not found for the given key or
     * its value is not of Special type with additional information 25, 26, or 27.
     *
     * @param key {@link Object} value of the key
     * @return {@link Double} value of the header
     */
    public Double getAsDouble(Object key) {
        CBORObject cborObject = getHeader(CBORObjectFactory.toCBORObject(key));
        if (cborObject != null && (cborObject.isFloatingPointNumber())) {
            return cborObject.getValueAsDouble();
        }
        return null;
    }

    /**
     * Gets a header value as {@code String}. Returns NULL if the header is not found for the given key or
     * its value is not of UnicodeString type
     *
     * @param key {@link Object} value of the key
     * @return {@link String} value of the header
     */
    public String getAsString(Object key) {
        CBORObject cborObject = getHeader(CBORObjectFactory.toCBORObject(key));
        if (cborObject != null && (cborObject.isUnicodeString())) {
            return cborObject.getValueAsString();
        }
        return null;
    }

    /**
     * Gets a header value as a byte array. Returns NULL if the header is not found for the given key or
     * its value is not of ByteString type
     *
     * @param key {@link Object} value of the key
     * @return a byte array value of the header
     */
    public byte[] getAsBinaries(Object key) {
        CBORObject cborObject = getHeader(CBORObjectFactory.toCBORObject(key));
        if (cborObject != null && cborObject.isByteString()) {
            return cborObject.getValueAsBytes();
        }
        return null;
    }

    /**
     * Gets a header value as {@code CBORArray}. Returns NULL if the header is not found for the given key or
     * its value is not of Array type
     *
     * @param key {@link Object} value of the key
     * @return {@link CBORArray} value of the header
     */
    public CBORArray getAsArray(Object key) {
        CBORObject cborObject = getHeader(CBORObjectFactory.toCBORObject(key));
        if (cborObject != null && cborObject.isArray()) {
            return ((CBORArray) cborObject);
        }
        return null;
    }

    /**
     * Gets a header value as {@code CBORMap}. Returns NULL if the header is not found for the given key or
     * its value is not of Map type
     *
     * @param key {@link Object} value of the key
     * @return {@link CBORMap} value of the header
     */
    public CBORMap getAsMap(Object key) {
        CBORObject cborObject = getHeader(CBORObjectFactory.toCBORObject(key));
        if (cborObject != null && cborObject.isMap()) {
            return ((CBORMap) cborObject);
        }
        return null;
    }

    /**
     * Adds a new entry to the map with a given {@code key} and {@code value}.
     * Transforms the {@code value} to the required type (if supported)
     *
     * @param key {@link Object} value of the key
     * @param value {@link Object} representing the header value
     */
    public void put(Object key, Object value) {
        toDataItem().put(CBORObjectFactory.toDataItem(key), CBORObjectFactory.toDataItem(value));
        clearSerializedBytes();
    }

    private void clearSerializedBytes() {
        serializedMap = null;
    }

    /**
     * Adds all entries from the {@code map}
     *
     * @param map {@link Map} to add values from
     */
    public void putAll(Map<?, ?> map) {
        if (Utils.isMapNotEmpty(map)) {
            map.forEach(this::put);
        }
    }

    /**
     * Checks if the current map object is empty
     *
     * @return TRUE if the map is empty, FALSE otherwise
     */
    public boolean isEmpty() {
        return Utils.isCollectionEmpty(toDataItem().getKeys());
    }

    /**
     * Returns size of the map
     *
     * @return size of the map
     */
    public int getSize() {
        return toDataItem().getKeys().size();
    }

}
