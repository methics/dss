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
import eu.europa.esig.dss.detailedreport.jaxb.XmlCC;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCryptographicValidation;
import eu.europa.esig.dss.diagnostic.AbstractTokenProxy;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.TokenProxy;
import eu.europa.esig.dss.enumerations.Context;
import eu.europa.esig.dss.enumerations.SubContext;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.CryptographicSuite;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.model.policy.ValidationPolicy;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;
import eu.europa.esig.dss.validation.process.bbb.aov.cc.SignatureAlgorithmCryptographicChecker;
import eu.europa.esig.dss.validation.process.bbb.aov.checks.SignatureAlgorithmCryptographicCheckerResultCheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Performs cryptographic validation for a given {@code TokenProxy}, including the signature value,
 * signed properties and certificate chain validation, when applicable
 *
 * @param <T> {@link TokenProxy}
 */
public abstract class TokenAlgorithmObsolescenceValidation<T extends TokenProxy> extends DigestAlgorithmObsolescenceValidation<T> {

    /**
     * Common constructor
     *
     * @param i18nProvider     the access to translations
     * @param token            instance of {@link AbstractTokenProxy} to be processed
     * @param context          {@link Context} validation context
     * @param validationDate   {@link Date} validation time
     * @param validationPolicy {@link ValidationPolicy} to be used during the validation
     */
    protected TokenAlgorithmObsolescenceValidation(I18nProvider i18nProvider, T token, Context context,
                                                   Date validationDate, ValidationPolicy validationPolicy) {
        super(i18nProvider, token, context, validationDate, validationPolicy);
    }

    @Override
    protected ChainItem<XmlAOV> buildChain() {

        return buildSignatureValidationChain(firstItem);

    }

    /**
     * Builds a chain of crypto checks to be executed on a signature
     *
     * @param item {@link ChainItem} to chain new checks to
     * @return {@link ChainItem}
     */
    protected ChainItem<XmlAOV> buildSignatureValidationChain(ChainItem<XmlAOV> item) {
        final SignatureAlgorithmCryptographicChecker cc = new SignatureAlgorithmCryptographicChecker(i18nProvider,
                token.getSignatureAlgorithm(), token.getKeyLengthUsedToSignThisToken(), validationDate, position, cryptographicSuite);
        XmlCC ccResult = cc.execute();

        if (item == null) {
            item = firstItem = signatureAlgorithmCryptographicCheckResult(ccResult, position, cryptographicSuite);
        } else {
            item = item.setNextItem(signatureAlgorithmCryptographicCheckResult(ccResult, position, cryptographicSuite));
        }

        signatureCryptographicValidation = ccResult.getCryptographicValidation();
        if (signatureCryptographicValidation != null) {
            signatureCryptographicValidation.setTokenId(token.getId());
        }

        return item;
    }

    /**
     * Builds a chain of crypto checks to be executed on a signature's certificate chain
     *
     * @param item {@link ChainItem} to chain new checks to
     * @param signingCertificate {@link CertificateWrapper} end-entity certificate of the certificate chain
     * @param certificateChain a list of {@link CertificateWrapper} representing the certificate chain
     * @return {@link ChainItem}
     */
    protected ChainItem<XmlAOV> buildCertificateChainValidationChain(
            ChainItem<XmlAOV> item, CertificateWrapper signingCertificate, List<CertificateWrapper> certificateChain) {
        if (signingCertificate == null) {
            return item;
        }

        certificateChainCryptographicValidation = new ArrayList<>();

        for (CertificateWrapper certificate : certificateChain) {

            SubContext subContext;
            if (signingCertificate.equals(certificate)) {
                subContext = SubContext.SIGNING_CERT;
            } else {
                subContext = SubContext.CA_CERTIFICATE;
            }

            if (isTrustAnchor(certificate, subContext)) {
                break;
            }

            CryptographicSuite certificateCryptographicSuite = validationPolicy.getCertificateCryptographicConstraint(context, subContext);
            MessageTag certificatePosition = ValidationProcessUtils.getSubContextPosition(context, subContext);

            SignatureAlgorithmCryptographicChecker cc = new SignatureAlgorithmCryptographicChecker(i18nProvider,
                    certificate.getSignatureAlgorithm(), certificate.getKeyLengthUsedToSignThisToken(), validationDate, certificatePosition, certificateCryptographicSuite);
            XmlCC ccResult = cc.execute();

            if (item == null) {
                item = firstItem = signatureAlgorithmCryptographicCheckResult(ccResult, certificatePosition, certificateCryptographicSuite, certificate.getId());
            } else {
                item = item.setNextItem(signatureAlgorithmCryptographicCheckResult(ccResult, certificatePosition, certificateCryptographicSuite, certificate.getId()));
            }

            XmlCryptographicValidation certificateCryptographicValidation = ccResult.getCryptographicValidation();
            certificateCryptographicValidation.setTokenId(certificate.getId());

            certificateChainCryptographicValidation.add(certificateCryptographicValidation);

        }

        return item;
    }

    private ChainItem<XmlAOV> signatureAlgorithmCryptographicCheckResult(XmlCC ccResult, MessageTag position, CryptographicSuite cryptographicSuite) {
        return new SignatureAlgorithmCryptographicCheckerResultCheck<>(i18nProvider, result, validationDate, position, ccResult, cryptographicSuite);
    }

    private ChainItem<XmlAOV> signatureAlgorithmCryptographicCheckResult(XmlCC ccResult, MessageTag position, CryptographicSuite cryptographicSuite, String tokenId) {
        return new SignatureAlgorithmCryptographicCheckerResultCheck<>(i18nProvider, result, validationDate, Context.CERTIFICATE, position, ccResult, cryptographicSuite, tokenId);
    }

    private boolean isTrustAnchor(CertificateWrapper certificateWrapper, SubContext subContext) {
        LevelRule constraint = validationPolicy.getCertificateSunsetDateConstraint(context, subContext);
        return ValidationProcessUtils.isTrustAnchor(certificateWrapper, validationDate, constraint);
    }

}
