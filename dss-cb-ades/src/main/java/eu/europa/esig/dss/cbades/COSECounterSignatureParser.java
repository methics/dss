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

import co.nstant.in.cbor.CborException;
import co.nstant.in.cbor.decoder.TagDecoder;
import co.nstant.in.cbor.model.MajorType;
import co.nstant.in.cbor.model.Tag;
import eu.europa.esig.dss.cbades.cbor.CBORArray;
import eu.europa.esig.dss.cbades.cbor.CBORByteString;
import eu.europa.esig.dss.cbades.cbor.CBORMap;
import eu.europa.esig.dss.cbades.cbor.CBORObject;
import eu.europa.esig.dss.enumerations.COSESignatureType;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.spi.exception.IllegalInputException;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class parses a counter signature
 *
 */
public class COSECounterSignatureParser extends AbstractCOSEParser {

    private static final Logger LOG = LoggerFactory.getLogger(COSECounterSignatureParser.class);

    /** The original context of the counter signature */
    private COSESignatureType context;

    /** The target signature containing the extracted counter signature */
    private COSEStructure masterSignature;

    /**
     * The constructor to parse a CBORObject
     *
     * @param cborObject {@link CBORObject} to parse
     */
    protected COSECounterSignatureParser(CBORObject cborObject) {
        super(cborObject);
    }

    /**
     * Instantiates a COSECounterSignatureParser from a {@code DSSDocument}
     *
     * @param document {@link DSSDocument} to parse
     * @return {@link COSECounterSignatureParser}
     */
    public static COSECounterSignatureParser fromDocument(DSSDocument document) {
        Objects.requireNonNull(document, "Document cannot be null!");
        if (!isSupported(document)) {
            throw new IllegalInputException("Document is not of a COSE signature type!");
        }
        final CBORObject cborObject = parseCbor(document);
        return new COSECounterSignatureParser(cborObject);
    }

    /**
     * Instantiates a COSECounterSignatureParser from a {@code CBORObject}
     *
     * @param cborObject {@link CBORObject} to parse
     * @return {@link COSECounterSignatureParser}
     */
    public static COSECounterSignatureParser fromCBORObject(CBORObject cborObject) {
        Objects.requireNonNull(cborObject, "CBORObject cannot be null!");
        return new COSECounterSignatureParser(cborObject);
    }

    /**
     * Sets the original counter signature context it has been extracted from
     *
     * @param context {@link COSESignatureType}
     * @return this {@link COSECounterSignatureParser}
     */
    public COSECounterSignatureParser setContext(COSESignatureType context) {
        this.context = context;
        return this;
    }

    /**
     * Sets the target signature
     *
     * @param masterSignature {@link COSEStructure}
     * @return {@link COSECounterSignatureParser}
     */
    public COSECounterSignatureParser setMasterSignature(COSEStructure masterSignature) {
        this.masterSignature = masterSignature;
        return this;
    }

    /**
     * This method verifies whether the document represents a COSE counter signature structure
     *
     * @param document {@link DSSDocument} to be validated
     * @return TRUE if the document is supported, FALSE otherwise
     */
    public static boolean isSupported(DSSDocument document) {
        try (InputStream is = document.openStream()) {
            return isCoseCounterSignatureStart(is);
        } catch (IOException e) {
            throw new DSSException(String.format("Unable to read the document with name '%s' : %s",
                    document.getName(), e.getMessage()));
        }
    }

    /**
     * This method verifies whether the beginning of the InputStream is a COSE counter signature structure
     *
     * @param inputStream {@link InputStream} to check
     * @return TRUE if the beginning of the InoutStream represents a COSE counter signature structure, FALSE otherwise
     * @throws IOException if an error occurs on InputStream reading
     */
    private static boolean isCoseCounterSignatureStart(InputStream inputStream) throws IOException {
        try (InputStream is = inputStream) {
            int symbol = is.read();
            if (isCoseCounterSignatureTag(symbol, is)) {
                symbol = is.read();
                return isCoseCounterSignatureArray(symbol, is);
            } else if (isCoseCounterSignatureArray(symbol, is)) {
                return true;
            }
        } catch (CborException e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Error on CBOR decoding : {}. Not a valid CBOR file.", e.getMessage());
            }
        }
        return false;
    }

    private static boolean isCoseCounterSignatureTag(int symbol, InputStream inputStream) throws CborException {
        if (MajorType.TAG == MajorType.ofByte(symbol)) {
            TagDecoder tagDecoder = new TagDecoder(null, inputStream);
            Tag tag = tagDecoder.decode(symbol);
            return COSEConstants.COSE_COUNTERSIGNATURE_TAG == tag.getValue();
        }
        return false;
    }

    private static boolean isCoseCounterSignatureArray(int symbol, InputStream inputStream) throws CborException, IOException {
        if (MajorType.ARRAY == MajorType.ofByte(symbol)) {
            DSSArrayDecoder arrayDecoder = new DSSArrayDecoder(inputStream);
            long length = arrayDecoder.getLength(symbol);
            int arrayFirstSymbol = inputStream.read();
            if (MajorType.BYTE_STRING == MajorType.ofByte(arrayFirstSymbol)) {
                // COSE_Countersignature0
                return true;
            } else if (MajorType.ARRAY == MajorType.ofByte(arrayFirstSymbol)) {
                // COSE_Countersignature / [+ COSE_Countersignature]
                if (isCoseSignature(arrayFirstSymbol, length)) {
                    return true;
                }
                length = arrayDecoder.getLength(symbol);
                arrayFirstSymbol = inputStream.read();
                if (MajorType.ARRAY == MajorType.ofByte(arrayFirstSymbol) && isCoseSignature(arrayFirstSymbol, length)) {
                    return true;
                }

            }
            return MajorType.BYTE_STRING == MajorType.ofByte(arrayFirstSymbol);
        }
        return false;
    }

    private static boolean isCoseSignature(int arrayFirstSymbol, long arrayLength) {
        if (arrayLength == -1 || arrayLength == 3) { // -1 for not defined length, 3 for COSESignature structure
            return MajorType.BYTE_STRING == MajorType.ofByte(arrayFirstSymbol);
        }
        return false;
    }

    /**
     * This method parses COSE signature present within a provided document
     *
     * @return {@link COSECounterSignStructure}
     */
    public COSECounterSignStructure parse() {
        if (cborObject.isArray()) {
            CBORArray cborArray = (CBORArray) cborObject;
            if (cborArray.isTagged()) {
                // COSE_Countersignature
                if (COSESignatureType.COSE_COUNTER_SIGNATURE.getTag() == cborArray.getTag().getValue()) {
                    return parseCOSECounterSignature(cborArray);
                } else {
                    throw new UnsupportedOperationException(String.format(
                            "The tag '%s' is not supported for COSE counter signature structure!", cborArray.getTag().getValue()));
                }
            }
            if (entriesCBORArrays(cborArray)) {
                // [+ COSE_Countersignature]
                return parseCOSECounterSignatureArray(cborArray);
            } else {
                // COSE_Countersignature
                return parseCOSECounterSignature(cborObject);
            }

        } else if (cborObject.isByteString()) {
            // COSE_Countersignature0
            CBORByteString cborByteString = (CBORByteString) cborObject;
            return parseCOSECounterSignature0(cborByteString);

        } else {
            throw new IllegalInputException("A COSE counter signature shall be represented by a CBOR Array or ByteString type!");
        }
    }

    private boolean entriesCBORArrays(CBORArray cborArray) {
        return cborArray.getValueAsList().stream().allMatch(CBORObject::isArray);
    }

    private COSECounterSignatureArray parseCOSECounterSignatureArray(CBORArray cborArray) {
        final COSECounterSignatureArray coseCounterSignatureArray = new COSECounterSignatureArray();
        coseCounterSignatureArray.setTagged(cborArray.isTagged());
        coseCounterSignatureArray.setContext(context);
        coseCounterSignatureArray.setMasterSignature(masterSignature);
        List<COSECounterSignature> signatures = new ArrayList<>();
        for (CBORObject arrayEntry : cborArray.getValueAsList()) {
            COSECounterSignature coseCounterSignature = parseCOSECounterSignature(arrayEntry);
            signatures.add(coseCounterSignature);
        }
        coseCounterSignatureArray.setCoseCounterSignatureList(signatures);
        return coseCounterSignatureArray;
    }

    private COSECounterSignature parseCOSECounterSignature(CBORObject cborObject) {
        if (!cborObject.isArray()) {
            throw new IllegalInputException("COSE_Countersignature must be of CBORArray type!");
        }
        CBORArray cborArray = (CBORArray) cborObject;
        final List<CBORObject> dataItems = cborArray.getValueAsList();
        if (Utils.collectionSize(dataItems) != 3) {
            throw new IllegalInputException("COSE_Countersignature array must have 3 entries!");
        }
        CBORObject protectedHeaderItem = dataItems.get(0);
        if (!protectedHeaderItem.isByteString()) {
            throw new IllegalInputException("COSE_Countersignature.protected header must be a bstr!");
        }
        CBORObject unprotectedHeaderItem = dataItems.get(1);
        if (!unprotectedHeaderItem.isMap()) {
            throw new IllegalInputException("COSE_Countersignature.unprotected header must be a header map!");
        }
        CBORObject signatureItem = dataItems.get(2);
        if (!signatureItem.isByteString()) {
            throw new IllegalInputException("COSE_Signature.signature must be a bstr!");
        }

        final COSECounterSignature coseCounterSignature = new COSECounterSignature();
        coseCounterSignature.setContext(context);
        coseCounterSignature.setMasterSignature(masterSignature);
        coseCounterSignature.setTagged(cborArray.isTagged());
        coseCounterSignature.setProtectedHeader(new COSEProtectedHeader((CBORByteString) protectedHeaderItem));
        coseCounterSignature.setUnprotectedHeader(new COSEUnprotectedHeader((CBORMap) unprotectedHeaderItem));
        coseCounterSignature.setSignature((CBORByteString) signatureItem);
        return coseCounterSignature;
    }

    private COSECounterSignature0 parseCOSECounterSignature0(CBORByteString cborByteString) {
        final COSECounterSignature0 coseCounterSignature0 = new COSECounterSignature0();
        coseCounterSignature0.setContext(context);
        coseCounterSignature0.setMasterSignature(masterSignature);
        coseCounterSignature0.setTagged(cborByteString.isTagged());
        coseCounterSignature0.setSignature(cborByteString);
        return coseCounterSignature0;
    }

}
