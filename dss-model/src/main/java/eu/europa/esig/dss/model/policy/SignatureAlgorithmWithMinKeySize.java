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
package eu.europa.esig.dss.model.policy;

import eu.europa.esig.dss.enumerations.SignatureAlgorithm;

import java.io.Serializable;
import java.util.Objects;

/**
 * Defines a {@code eu.europa.esig.dss.enumerations.SignatureAlgorithm} with a minimum key size
 *
 */
public class SignatureAlgorithmWithMinKeySize implements Serializable {

    private static final long serialVersionUID = 613854187099274464L;

    /** The Signature algorithm */
    private final SignatureAlgorithm signatureAlgorithm;

    /** The minimal accepted key size */
    private final int minKeySize;

    /**
     * Default constructor
     *
     * @param signatureAlgorithm {@link SignatureAlgorithm}
     * @param minKeySize integer key size. 0 when not defined
     */
    public SignatureAlgorithmWithMinKeySize(final SignatureAlgorithm signatureAlgorithm, int minKeySize) {
        this.signatureAlgorithm = signatureAlgorithm;
        this.minKeySize = minKeySize;
    }

    /**
     * Gets Signature algorithm
     *
     * @return {@link SignatureAlgorithm}
     */
    public SignatureAlgorithm getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    /**
     * Gets the minimum key size value
     *
     * @return key size
     */
    public int getMinKeySize() {
        return minKeySize;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        SignatureAlgorithmWithMinKeySize that = (SignatureAlgorithmWithMinKeySize) object;
        return minKeySize == that.minKeySize
                && signatureAlgorithm == that.signatureAlgorithm;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(signatureAlgorithm);
        result = 31 * result + minKeySize;
        return result;
    }

    @Override
    public String toString() {
        return "SignatureAlgorithmWithMinKeySize [" +
                "signatureAlgorithm=" + signatureAlgorithm +
                ", minKeySize=" + minKeySize +
                ']';
    }

}