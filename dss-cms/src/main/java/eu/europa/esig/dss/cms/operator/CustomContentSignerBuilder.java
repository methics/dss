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
package eu.europa.esig.dss.cms.operator;

import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.SignatureValue;
import org.bouncycastle.cms.SignerInfoGenerator;

import java.util.Objects;

/**
 * This class is used to create an instance of {@code eu.europa.esig.dss.cades.signature.CustomContentSigner}
 *
 */
public class CustomContentSignerBuilder {

    /**
     * Default constructor
     */
    public CustomContentSignerBuilder() {
        // empty
    }

    /**
     * Builds a {@code CustomContentSigner} for the CMS signature creation.
     * This method creates a CustomContentSigner with an absent SignatureValue.
     * Method is normally used for message-digest computation.
     *
     * @param signatureAlgorithm {@link SignatureAlgorithm} to be used on signature creation
     * @return {@link SignerInfoGenerator}
     */
    public CustomContentSigner build(final SignatureAlgorithm signatureAlgorithm) {
        Objects.requireNonNull(signatureAlgorithm, "SignatureAlgorithm cannot be null!");
        return new CustomContentSigner(signatureAlgorithm.getJCEId());
    }

    /**
     * Builds a {@code CustomContentSigner} for the CMS signature creation using the given SignatureValue.
     *
     * @param signatureAlgorithm {@link SignatureAlgorithm} to be used on signature creation
     * @param signatureValue {@link SignatureValue} to be embedded within the CMS
     * @return {@link SignerInfoGenerator}
     */
    public CustomContentSigner build(final SignatureAlgorithm signatureAlgorithm, final SignatureValue signatureValue) {
        Objects.requireNonNull(signatureAlgorithm, "SignatureAlgorithm cannot be null!");
        Objects.requireNonNull(signatureValue, "signatureValue cannot be null!");
        if (signatureAlgorithm != signatureValue.getAlgorithm()) {
            throw new IllegalArgumentException(String.format("The defined SignatureAlgorithm '%s' " +
                            "does not match the SignatureAlgorithm '%s' used on SignatureValue computation!",
                    signatureAlgorithm, signatureValue.getAlgorithm()));
        }
        return new CustomContentSigner(signatureAlgorithm.getJCEId(), signatureValue.getValue());
    }

}
