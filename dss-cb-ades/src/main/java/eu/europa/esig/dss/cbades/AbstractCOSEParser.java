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
import co.nstant.in.cbor.decoder.ArrayDecoder;
import eu.europa.esig.dss.cbades.cbor.CBORObject;
import eu.europa.esig.dss.cbades.cbor.CBORUtils;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;

import java.io.InputStream;
import java.util.Objects;

/**
 * Abstract class containing common methods for parsing COSE structures
 *
 */
public abstract class AbstractCOSEParser {

    /** The object to be parsed */
    protected final CBORObject cborObject;

    /**
     * The constructor to parse a CBORObject
     *
     * @param cborObject {@link CBORObject} to parse
     */
    protected AbstractCOSEParser(CBORObject cborObject) {
        Objects.requireNonNull(cborObject, "CBORObject cannot be null!");
        this.cborObject = cborObject;
    }

    /**
     * Parses CBOR {@code DSSDocument}
     *
     * @param document {@link DSSDocument}
     * @return {@link CBORObject}
     */
    protected static CBORObject parseCbor(DSSDocument document) {
        try {
            return CBORUtils.parseCbor(document);
        } catch (CborException e) {
            throw new DSSException(String.format("A parsing error of CBOR content occurred : %s", e.getMessage()), e);
        }
    }

    /**
     * Extended implementation of {@code ArrayDecoder}
     */
    protected static class DSSArrayDecoder extends ArrayDecoder {

        /**
         * Default constructor
         *
         * @param inputStream {@link InputStream}
         */
        public DSSArrayDecoder(InputStream inputStream) {
            super(null, inputStream);
        }

        @Override
        protected long getLength(int initialByte) throws CborException {
            return super.getLength(initialByte);
        }

    }

}
