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
import co.nstant.in.cbor.model.DataItem;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * A wrapper of a CBOR array object
 *
 */
public class CBORArray extends AbstractCBORObject<Array> {

    private static final Logger LOG = LoggerFactory.getLogger(CBORArray.class);

    /**
     * Constructor to create an empty array
     */
    public CBORArray() {
        this(new Array());
    }

    /**
     * Constructor to create an empty array with an initial size
     *
     * @param arraySize initial size of the array
     */
    public CBORArray(int arraySize) {
        this(new Array(arraySize));
    }

    /**
     * Constructor to create a CBORArray from an Array implementation
     *
     * @param array {@link co.nstant.in.cbor.model.Array}
     */
    public CBORArray(final Array array) {
        super(array);
    }

    /**
     * Constructor to create a CBORArray from a Collection of objects
     *
     * @param collection {@link Collection}
     */
    public CBORArray(final Collection<?> collection) {
        this(toArray(collection));
    }

    private static Array toArray(final Collection<?> list) {
        Array array = new Array(list.size());
        list.forEach(l -> array.add(CBORObjectFactory.toDataItem(l)));
        return array;
    }

    /**
     * Constructor to create a CBORArray from an array of objects
     *
     * @param array {@link List}
     * @param <T> generic type
     */
    public <T> CBORArray(final T[] array) {
        this(toArray(array));
    }

    private static <T> Array toArray(final T[] inputArray) {
        Array array = new Array(inputArray.length);
        for (Object object : inputArray) {
            array.add(CBORObjectFactory.toDataItem(object));
        }
        return array;
    }

    /**
     * This method adds a new object to the array in the latest position, by extending the array.
     * The method transforms the given Object to a supported DataItem type.
     *
     * @param object {@link Object} to add
     */
    public void add(Object object) {
        toDataItem().add(CBORObjectFactory.toDataItem(object));
    }

    /**
     * Sets {@code object} within the current CBOR Array at the {@code index} position, replacing the existing value
     *
     * @param index position to set the object at
     * @param object to be placed
     */
    public void set(int index, Object object) {
        Array array = toDataItem();
        ListIterator<DataItem> it = array.getDataItems().listIterator();
        int i = 0;
        while (it.hasNext()) {
            it.next();
            if (index == i) {
                it.set(CBORObjectFactory.toDataItem(object));
                break;
            }
            ++i;
        }
    }

    /**
     * Checks if the given array is empty
     *
     * @return TRUE if the array is empty, FALSE otherwise
     */
    public boolean isEmpty() {
        return Utils.isCollectionEmpty(toDataItem().getDataItems());
    }

    @Override
    public List<CBORObject> getValueAsList() {
        return toDataItem().getDataItems().stream().map(CBORObjectFactory::toCBORObject).collect(Collectors.toList());
    }

    /**
     * Gets an item from the position {@code index} in the array and returns its Long value
     *
     * @param index position of the item in the array to return
     * @return {@link Long} if identified, NULL otherwise
     */
    public Long getAsLong(int index) {
        CBORObject item = getItem(index);
        if (item == null) {
            return null;
        }
        if (item.isNegativeInteger() || item.isUnsignedInteger()) {
            return item.getValueAsLong();
        }
        return null;
    }

    /**
     * Gets an item from the position {@code index} in the array and returns its Long value,
     * whether the item is encoded as an integer or a String, when transformation is possible.
     * The parsing from String is encoded safely.
     *
     * @param index position of the item in the array to return
     * @return {@link Long} if identified, NULL otherwise
     */
    public Long getAsLongOrString(int index) {
        CBORObject item = getItem(index);
        if (item == null) {
            return null;
        }
        if (item.isNegativeInteger() || item.isUnsignedInteger()) {
            return item.getValueAsLong();
        }
        if (item.isUnicodeString()) {
            String itemAsString = item.getValueAsString();
            try {
                return Long.parseLong(itemAsString);
            } catch (NumberFormatException e) {
                LOG.debug("Unable to decode CBOR String '{}' to Long : {}", itemAsString, e.getMessage());
            }
        }
        return null;
    }

    /**
     * Gets an item from the position {@code index} in the array and returns its String value
     *
     * @param index position of the item in the array to return
     * @return {@link String} if identified, NULL otherwise
     */
    public String getAsString(int index) {
        CBORObject item = getItem(index);
        if (item == null) {
            return null;
        }
        if (item.isUnicodeString()) {
            return item.getValueAsString();
        }
        return null;
    }

    /**
     * Gets an item from the position {@code index} in the array and returns its byte array value
     *
     * @param index position of the item in the array to return
     * @return byte array if identified, NULL otherwise
     */
    public byte[] getAsBinaries(int index) {
        CBORObject item = getItem(index);
        if (item == null) {
            return null;
        }
        if (item.isByteString()) {
            return item.getValueAsBytes();
        }
        return null;
    }

    /**
     * Gets an item from the position {@code index} in the array and returns its CBOR array value
     *
     * @param index position of the item in the array to return
     * @return {@link CBORArray} if identified, NULL otherwise
     */
    public CBORArray getAsArray(int index) {
        CBORObject item = getItem(index);
        if (item == null) {
            return null;
        }
        if (item.isArray()) {
            return ((CBORArray) item);
        }
        return null;
    }

    /**
     * Gets an item from the position {@code index} in the array and returns its CBOR map value
     *
     * @param index position of the item in the array to return
     * @return {@link CBORMap} if identified, NULL otherwise
     */
    public CBORMap getAsMap(int index) {
        CBORObject item = getItem(index);
        if (item == null) {
            return null;
        }
        if (item.isMap()) {
            return ((CBORMap) item);
        }
        return null;
    }

    /**
     * Gets an array item from the given index
     *
     * @param index position of the item in the array to return
     * @return {@link CBORArray}
     */
    public CBORObject getItem(int index) {
        return CBORObjectFactory.toCBORObject(toDataItem().getDataItems().get(index));
    }

    /**
     * Converts the current CBOR Array values to a List of {@code String}s
     *
     * @return a list of {@link String}s
     */
    public List<String> toListOfStrings() {
        final List<String> result = new ArrayList<>();
        for (CBORObject cborObject : getValueAsList()) {
            if (cborObject.isUnicodeString()) {
                result.add(cborObject.getValueAsString());
            } else {
                LOG.debug("The entry '{}' is not of UnicodeString type. The entry is skipped.", cborObject);
            }
        }
        return result;
    }

    /**
     * Converts the current CBOR Array values to a List of {@code Long}s
     *
     * @return a list of {@link Long}s
     */
    public List<Long> toListOfLongs() {
        final List<Long> result = new ArrayList<>();
        for (CBORObject cborObject : getValueAsList()) {
            if (cborObject.isNegativeInteger() || cborObject.isUnsignedInteger()) {
                result.add(cborObject.getValueAsLong());
            } else {
                LOG.debug("The entry '{}' is not of NegativeInteger nor UnsignedInteger type. The entry is skipped.", cborObject);
            }
        }
        return result;
    }

    /**
     * Converts the current CBOR Array values to a List of {@code String}s
     *
     * @return a list of {@link String}s
     */
    public List<byte[]> toListOfBinaries() {
        final List<byte[]> result = new ArrayList<>();
        for (CBORObject cborObject : getValueAsList()) {
            if (cborObject.isByteString()) {
                result.add(cborObject.getValueAsBytes());
            } else {
                LOG.debug("The entry '{}' is not of RByteString type. The entry is skipped.", cborObject);
            }
        }
        return result;
    }

    /**
     * Removes {@code cborObject} from the current CBORArray, when present
     *
     * @param cborObject {@link CBORObject} to remove
     */
    public void remove(CBORObject cborObject) {
        Objects.requireNonNull(cborObject, "CBORObject cannot be null!");

        DataItem toBeRemoved = cborObject.toDataItem();
        Array array = toDataItem();
        ListIterator<DataItem> it = array.getDataItems().listIterator();
        while (it.hasNext()) {
            DataItem dataItem = it.next();
            if (toBeRemoved == dataItem) {
                it.remove();
                break;
            }
        }
    }

    /**
     * Returns size of the array
     *
     * @return size of the array
     */
    public int getSize() {
        return toDataItem().getDataItems().size();
    }

}
