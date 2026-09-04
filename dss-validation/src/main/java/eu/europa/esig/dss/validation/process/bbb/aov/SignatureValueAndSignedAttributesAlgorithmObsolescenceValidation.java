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
import eu.europa.esig.dss.diagnostic.AbstractSignatureWrapper;
import eu.europa.esig.dss.diagnostic.CertificateRefWrapper;
import eu.europa.esig.dss.diagnostic.TokenProxy;
import eu.europa.esig.dss.enumerations.Context;
import eu.europa.esig.dss.enumerations.SubContext;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.model.policy.ValidationPolicy;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.bbb.aov.checks.SigningCertificateRefDigestAlgorithmCheck;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Performs cryptographic validation of a token's signature value algorithm and
 * algorithms used within the signed properties, when applicable
 *
 * @param <T> {@link TokenProxy}
 */
public class SignatureValueAndSignedAttributesAlgorithmObsolescenceValidation<T extends TokenProxy> extends TokenAlgorithmObsolescenceValidation<T> {

    /**
     * Common constructor
     *
     * @param i18nProvider     the access to translations
     * @param token            instance of {@link AbstractSignatureWrapper} to be processed
     * @param context          {@link Context} validation context
     * @param validationDate   {@link Date} validation time
     * @param validationPolicy {@link ValidationPolicy} to be used during the validation
     */
    public SignatureValueAndSignedAttributesAlgorithmObsolescenceValidation(
            I18nProvider i18nProvider, T token, Context context, Date validationDate,
            ValidationPolicy validationPolicy) {
        super(i18nProvider, token, context, validationDate, validationPolicy);
    }

    @Override
    protected ChainItem<XmlAOV> buildChain() {

        ChainItem<XmlAOV> item = super.buildChain();

        item = buildSignedAttributesValidationChain(item);

        return item;

    }

    /**
     * Builds a chain of crypto checks to be executed on a signature's signed attributes
     *
     * @param item {@link ChainItem} to chain new checks to
     * @return {@link ChainItem}
     */
    protected ChainItem<XmlAOV> buildSignedAttributesValidationChain(ChainItem<XmlAOV> item) {
        if (!token.isSigningCertificateReferencePresent()) {
            return item;
        }

        List<CertificateRefWrapper> signingCertificateReferences = token.getSigningCertificateReferences();
        CertificateRefWrapper signingCertificateReference = token.getSigningCertificateReference();

        XmlCryptographicValidation cryptographicValidation = null;

        // This code ensures that at least one good digest algorithm is found for every defined signing certificate reference
        final Map<String, List<CertificateRefWrapper>> signCertRefsMap = new HashMap<>();
        signingCertificateReferences.forEach(r -> signCertRefsMap.computeIfAbsent(r.getCertificateId(), s -> new ArrayList<>()).add(r));
        for (Map.Entry<String, List<CertificateRefWrapper>> entry : signCertRefsMap.entrySet()) {
            String certificateId = entry.getKey();
            List<CertificateRefWrapper> certificateRefWrappers = entry.getValue();

            SubContext subContext;
            if (signingCertificateReference != null && signingCertificateReference.getCertificateId().equals(certificateId)) {
                subContext = SubContext.SIGNING_CERT;
            } else {
                subContext = SubContext.CA_CERTIFICATE;
            }

            SigningCertificateRefDigestAlgorithmCheck<XmlAOV> signCertCheck =
                    signingCertificateRefDigestAlgoCheckResult(certificateRefWrappers, certificateId, subContext);

            if (item == null) {
                item = firstItem = signCertCheck;
            } else {
                item = item.setNextItem(signCertCheck);
            }

            XmlCC cryptoValidationResult = signCertCheck.getCryptographicValidationResult();
            if (cryptographicValidation == null || (isValid(cryptographicValidation) && !isValid(cryptoValidationResult))) {
                cryptographicValidation = cryptoValidationResult.getCryptographicValidation();
                cryptographicValidation.setTokenId(certificateId);
            }
        }

        signedAttributesCryptographicValidation = cryptographicValidation;
        if (signedAttributesCryptographicValidation != null) {
            signedAttributesCryptographicValidation.setTokenId(token.getId());
        }

        return item;
    }

    private SigningCertificateRefDigestAlgorithmCheck<XmlAOV> signingCertificateRefDigestAlgoCheckResult(
            List<CertificateRefWrapper> signCertRefs, String certificateId, SubContext subContext) {
        LevelRule constraint = validationPolicy.getSigningCertificateDigestAlgorithmConstraint(context);
        return new SigningCertificateRefDigestAlgorithmCheck<>(i18nProvider, result, validationDate,
                signCertRefs, certificateId, context, subContext, validationPolicy, constraint);
    }

}
