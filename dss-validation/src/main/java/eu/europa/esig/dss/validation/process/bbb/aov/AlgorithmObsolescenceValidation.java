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
package eu.europa.esig.dss.validation.process.bbb.aov;

import eu.europa.esig.dss.detailedreport.jaxb.XmlAOV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCertificateChainCryptographicValidation;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCryptographicValidation;
import eu.europa.esig.dss.diagnostic.AbstractTokenProxy;
import eu.europa.esig.dss.enumerations.Context;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.CryptographicSuite;
import eu.europa.esig.dss.model.policy.ValidationPolicy;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.Chain;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;

import java.util.Date;
import java.util.List;

/**
 * This class performs validation of cryptographic algorithms against the provided cryptographic suite constraints.
 * The result of this block is used within XCV and SAV building blocks.
 *
 * @param <T> validation token wrapper
 */
public abstract class AlgorithmObsolescenceValidation<T> extends Chain<XmlAOV> {

    /** The token to be validated */
    protected final T token;

    /** The validation context */
    protected final Context context;

    /** The validation time */
    protected final Date validationDate;

    /** Validation policy containing the corresponding cryptographic constraints */
    protected final ValidationPolicy validationPolicy;

    /** Position of the token */
    protected MessageTag position;

    /** Cryptographic suite for the token's validation */
    protected CryptographicSuite cryptographicSuite;

    /** The cryptographic information for the SignatureValue algorithms validation */
    protected XmlCryptographicValidation signatureCryptographicValidation;

    /** The cryptographic information for the signed attributes algorithms validation */
    protected XmlCryptographicValidation signedAttributesCryptographicValidation;

    /** The cryptographic information for the digest matchers (signed references) algorithms validation */
    protected XmlCryptographicValidation digestMatchersCryptographicValidation;

    /** The cryptographic information for the certificate chain algorithms validation */
    protected List<XmlCryptographicValidation> certificateChainCryptographicValidation;

    /**
     * Common constructor
     *
     * @param i18nProvider the access to translations
     * @param token instance of {@link AbstractTokenProxy} to be processed
     * @param context {@link Context} validation context
     * @param validationDate {@link Date} validation time
     * @param validationPolicy {@link ValidationPolicy} to be used during the validation
     */
    protected AlgorithmObsolescenceValidation(I18nProvider i18nProvider, T token, Context context,
                                              Date validationDate, ValidationPolicy validationPolicy) {
        super(i18nProvider, new XmlAOV());

        this.token = token;
        this.validationDate = validationDate;
        this.context = context;
        this.validationPolicy = validationPolicy;
    }

    @Override
    protected MessageTag getTitle() {
        return MessageTag.AOV;
    }

    @Override
    protected void initChain() {
        this.position = getPosition();
        this.cryptographicSuite = getCryptographicSuite();

        buildChain();
    }

    /**
     * Builds a chain of checks to be executed during the process
     *
     * @return {@link ChainItem}
     */
    protected abstract ChainItem<XmlAOV> buildChain();

    /**
     * Gets position of the currently verifying token, based on the {@code context}
     *
     * @return {@link MessageTag}
     */
    protected MessageTag getPosition() {
        return ValidationProcessUtils.getCryptoPosition(context);
    }

    /**
     * Gets the cryptographic suite based on the currently validating {@code context}
     *
     * @return {@link CryptographicSuite}
     */
    protected CryptographicSuite getCryptographicSuite() {
        return validationPolicy.getSignatureCryptographicConstraint(context);
    }

    /**
     * Checks whether CryptographicValidation returned a successful validation result
     *
     * @param cryptographicValidation {@link XmlCryptographicValidation}
     * @return TRUE if the cryptographic validation was successful, FALSE otherwise
     */
    protected boolean isValid(XmlCryptographicValidation cryptographicValidation) {
        return cryptographicValidation != null && cryptographicValidation.getConclusion() != null &&
                Indication.PASSED == cryptographicValidation.getConclusion().getIndication();
    }

    @Override
    protected void addAdditionalInfo() {
        super.addAdditionalInfo();

        result.setValidationTime(validationDate);
        result.setSignatureCryptographicValidation(signatureCryptographicValidation);
        result.setSignedAttributesValidation(signedAttributesCryptographicValidation);
        result.setDigestMatchersValidation(digestMatchersCryptographicValidation);
        if (Utils.isCollectionNotEmpty(certificateChainCryptographicValidation)) {
            XmlCertificateChainCryptographicValidation xmlCertificateChainCryptographicValidation = new XmlCertificateChainCryptographicValidation();
            xmlCertificateChainCryptographicValidation.getCertificateCryptographicValidation().addAll(certificateChainCryptographicValidation);
            result.setCertificateChainCryptographicValidation(xmlCertificateChainCryptographicValidation);
        }
    }

}
