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
package eu.europa.esig.dss.validation;

import eu.europa.esig.dss.enumerations.Context;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.enumerations.SubContext;
import eu.europa.esig.dss.model.policy.CertificateApplicabilityRule;
import eu.europa.esig.dss.model.policy.CryptographicSuite;
import eu.europa.esig.dss.model.policy.DurationRule;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.model.policy.MultiValuesRule;
import eu.europa.esig.dss.model.policy.SignatureAlgorithmWithMinKeySize;
import eu.europa.esig.dss.model.policy.ValidationPolicy;
import eu.europa.esig.dss.spi.validation.RevocationDataVerifier;
import eu.europa.esig.dss.validation.policy.CryptographicSuiteUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * This class loads {@code RevocationDataVerifier} from a provided {@code eu.europa.esig.dss.model.policy.ValidationPolicy}
 *
 */
public class RevocationDataVerifierFactory {

    private static final Logger LOG = LoggerFactory.getLogger(RevocationDataVerifierFactory.class);

    /** Validation policy to load RevocationDataVerifier from */
    private final ValidationPolicy validationPolicy;

    /** The used validation time */
    private Date validationTime;

    /**
     * Default constructor
     *
     * @param validationPolicy {@link ValidationPolicy}
     */
    public RevocationDataVerifierFactory(final ValidationPolicy validationPolicy) {
        this.validationPolicy = validationPolicy;
    }

    /**
     * Gets validation time. Instantiates value to the current time, if not provided explicitly.
     *
     * @return {@link Date}
     */
    protected Date getValidationTime() {
        if (validationTime == null) {
            validationTime = new Date();
        }
        return validationTime;
    }

    /**
     * Sets the used validation time
     *
     * @param validationTime {@link Date}
     * @return this {@code RevocationDataVerifierFactory}
     */
    public RevocationDataVerifierFactory setValidationTime(Date validationTime) {
        this.validationTime = validationTime;
        return this;
    }

    /**
     * Creates the {@code RevocationDataVerifier}
     *
     * @return {@link RevocationDataVerifier}
     */
    public RevocationDataVerifier create() {
        final RevocationDataVerifier revocationDataVerifier = RevocationDataVerifier.createEmptyRevocationDataVerifier();
        instantiateCryptographicSuite(revocationDataVerifier);
        instantiateRevocationSkipConstraints(revocationDataVerifier);
        instantiateRevocationFreshnessConstraints(revocationDataVerifier);
        instantiateAcceptRevocationIssuersWithoutRevocationConstraint(revocationDataVerifier);
        return revocationDataVerifier;
    }

    private void instantiateCryptographicSuite(final RevocationDataVerifier revocationDataVerifier) {
        Set<DigestAlgorithm> acceptableDigestAlgorithms;
        Set<SignatureAlgorithmWithMinKeySize> acceptableSignatureAlgorithms;

        final CryptographicSuite cryptographicSuite = getRevocationCryptographicSuite(validationPolicy);
        if (cryptographicSuite != null && Level.FAIL.equals(cryptographicSuite.getLevel())) {
            Date currentTime = getValidationTime();
            acceptableDigestAlgorithms = CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(cryptographicSuite, currentTime);
            acceptableSignatureAlgorithms = CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(cryptographicSuite, currentTime);
        } else {
            LOG.info("No enforced cryptographic constraints have been found in the provided validation policy. Accept all cryptographic algorithms.");
            acceptableDigestAlgorithms = new HashSet<>();
            Collections.addAll(acceptableDigestAlgorithms, DigestAlgorithm.values());
            acceptableSignatureAlgorithms = new HashSet<>();
            for (SignatureAlgorithm signatureAlgorithm : SignatureAlgorithm.values()) {
                acceptableSignatureAlgorithms.add(new SignatureAlgorithmWithMinKeySize(signatureAlgorithm, 0));
            }
        }
        revocationDataVerifier.setAcceptableDigestAlgorithms(acceptableDigestAlgorithms);
        revocationDataVerifier.setAcceptableSignatureAlgorithmKeyLength(toSignatureAlgorithmWithKeySizesMap(acceptableSignatureAlgorithms));
    }

    private CryptographicSuite getRevocationCryptographicSuite(ValidationPolicy validationPolicy) {
        return validationPolicy.getSignatureCryptographicConstraint(Context.REVOCATION);
    }

    private Map<SignatureAlgorithm, Integer> toSignatureAlgorithmWithKeySizesMap(Collection<SignatureAlgorithmWithMinKeySize> signatureAlgorithms) {
        final Map<SignatureAlgorithm, Integer> signatureAlgorithmsMap = new EnumMap<>(SignatureAlgorithm.class);
        for (SignatureAlgorithmWithMinKeySize encryptionAlgorithmWithMinKeySize : signatureAlgorithms) {
            SignatureAlgorithm signatureAlgorithm = encryptionAlgorithmWithMinKeySize.getSignatureAlgorithm();
            Integer minKeySize = signatureAlgorithmsMap.get(signatureAlgorithm);
            int keySize = encryptionAlgorithmWithMinKeySize.getMinKeySize();
            if (minKeySize == null || minKeySize > keySize) {
                signatureAlgorithmsMap.put(signatureAlgorithm, keySize);
            }
        }
        return signatureAlgorithmsMap;
    }

    private void instantiateRevocationSkipConstraints(final RevocationDataVerifier revocationDataVerifier) {
        final Set<String> certificateExtensions = new HashSet<>();
        final Set<String> certificatePolicies = new HashSet<>();

        populateRevocationSkipFromBasicSignatureConstraints(certificateExtensions, certificatePolicies, Context.SIGNATURE);
        populateRevocationSkipFromBasicSignatureConstraints(certificateExtensions, certificatePolicies, Context.COUNTER_SIGNATURE);
        populateRevocationSkipFromBasicSignatureConstraints(certificateExtensions, certificatePolicies, Context.KEY_BINDING_SIGNATURE);
        populateRevocationSkipFromBasicSignatureConstraints(certificateExtensions, certificatePolicies, Context.REVOCATION);
        populateRevocationSkipFromBasicSignatureConstraints(certificateExtensions, certificatePolicies, Context.TIMESTAMP);

        revocationDataVerifier.setRevocationSkipCertificateExtensions(certificateExtensions);
        revocationDataVerifier.setRevocationSkipCertificatePolicies(certificatePolicies);
    }

    private void populateRevocationSkipFromBasicSignatureConstraints(
            final Set<String> certificateExtensions, final Set<String> certificatePolicies, Context context) {
        populateRevocationSkipFromCertificateConstraints(certificateExtensions, certificatePolicies,
                context, SubContext.SIGNING_CERT);
        populateRevocationSkipFromCertificateConstraints(certificateExtensions, certificatePolicies,
                context, SubContext.CA_CERTIFICATE);
    }

    private void populateRevocationSkipFromCertificateConstraints(
            final Set<String> certificateExtensions, final Set<String> certificatePolicies,
            Context context, SubContext subContext) {
        CertificateApplicabilityRule revocationDataSkipConstraint = validationPolicy.getRevocationDataSkipConstraint(context, subContext);
        if (revocationDataSkipConstraint == null) {
            return;
        }

        MultiValuesRule certificateExtensionsConstraint = revocationDataSkipConstraint.getCertificateExtensions();
        if (certificateExtensionsConstraint != null) {
            certificateExtensions.addAll(certificateExtensionsConstraint.getValues());
        }

        MultiValuesRule certificatePoliciesConstraint = revocationDataSkipConstraint.getCertificatePolicies();
        if (certificatePoliciesConstraint != null) {
            certificatePolicies.addAll(certificatePoliciesConstraint.getValues());
        }
    }

    private void instantiateRevocationFreshnessConstraints(final RevocationDataVerifier revocationDataVerifier) {
        revocationDataVerifier.setSignatureMaximumRevocationFreshness(getSignatureRevocationFreshnessConstraint());
        revocationDataVerifier.setTimestampMaximumRevocationFreshness(getRevocationFreshnessConstraint(Context.TIMESTAMP));
        revocationDataVerifier.setRevocationMaximumRevocationFreshness(getRevocationFreshnessConstraint(Context.REVOCATION));

        boolean revocationFreshnessNextUpdateConstraint = getRevocationFreshnessNextUpdateConstraintPresent(Context.SIGNATURE);
        revocationFreshnessNextUpdateConstraint = revocationFreshnessNextUpdateConstraint || getRevocationFreshnessNextUpdateConstraintPresent(Context.COUNTER_SIGNATURE);
        revocationFreshnessNextUpdateConstraint = revocationFreshnessNextUpdateConstraint || getRevocationFreshnessNextUpdateConstraintPresent(Context.KEY_BINDING_SIGNATURE);
        revocationFreshnessNextUpdateConstraint = revocationFreshnessNextUpdateConstraint || getRevocationFreshnessNextUpdateConstraintPresent(Context.TIMESTAMP);
        revocationFreshnessNextUpdateConstraint = revocationFreshnessNextUpdateConstraint || getRevocationFreshnessNextUpdateConstraintPresent(Context.REVOCATION);

        revocationDataVerifier.setCheckRevocationFreshnessNextUpdate(revocationFreshnessNextUpdateConstraint);
    }

    private Long getSignatureRevocationFreshnessConstraint() {
        Long maximumRevocationFreshness = getRevocationFreshnessConstraint(Context.SIGNATURE);

        Long counterSignatureRevocationFreshnessConstraint = getRevocationFreshnessConstraint(Context.COUNTER_SIGNATURE);
        if (maximumRevocationFreshness == null || (counterSignatureRevocationFreshnessConstraint != null
                && counterSignatureRevocationFreshnessConstraint < maximumRevocationFreshness)) {
            maximumRevocationFreshness = counterSignatureRevocationFreshnessConstraint;
        }
        Long keyBindingSignatureRevocationFreshnessConstraint = getRevocationFreshnessConstraint(Context.KEY_BINDING_SIGNATURE);
        if (maximumRevocationFreshness == null || (keyBindingSignatureRevocationFreshnessConstraint != null
                && keyBindingSignatureRevocationFreshnessConstraint < maximumRevocationFreshness)) {
            maximumRevocationFreshness = keyBindingSignatureRevocationFreshnessConstraint;
        }

        return maximumRevocationFreshness;
    }

    private Long getRevocationFreshnessConstraint(Context context) {
        Long maximumRevocationFreshness = getRevocationFreshnessConstraintValue(context, SubContext.SIGNING_CERT);
        Long caCertRevocationFreshness = getRevocationFreshnessConstraintValue(context, SubContext.CA_CERTIFICATE);
        if (maximumRevocationFreshness == null || (caCertRevocationFreshness != null && caCertRevocationFreshness < maximumRevocationFreshness)) {
            maximumRevocationFreshness = caCertRevocationFreshness;
        }
        return maximumRevocationFreshness;
    }

    private Long getRevocationFreshnessConstraintValue(Context context, SubContext subContext) {
        DurationRule revocationFreshness = validationPolicy.getRevocationFreshnessConstraint(context, subContext);
        if (revocationFreshness != null) {
            return revocationFreshness.getDuration();
        }
        return null;
    }

    private boolean getRevocationFreshnessNextUpdateConstraintPresent(Context context) {
        LevelRule revocationFreshnessNextUpdateConstraint = validationPolicy.getRevocationFreshnessNextUpdateConstraint(context, SubContext.SIGNING_CERT);
        if (revocationFreshnessNextUpdateConstraint != null) {
            return true;
        }
        revocationFreshnessNextUpdateConstraint = validationPolicy.getRevocationFreshnessNextUpdateConstraint(context, SubContext.CA_CERTIFICATE);
        if (revocationFreshnessNextUpdateConstraint != null) {
            return true;
        }
        return false;
    }

    private void instantiateAcceptRevocationIssuersWithoutRevocationConstraint(final RevocationDataVerifier revocationDataVerifier) {
        boolean revocationDataAvailableConstraint = getRevocationDataAvailablePresent(Context.REVOCATION);
        revocationDataVerifier.setAcceptRevocationCertificatesWithoutRevocation(!revocationDataAvailableConstraint);

        revocationDataAvailableConstraint = getRevocationDataAvailablePresent(Context.TIMESTAMP);
        revocationDataVerifier.setAcceptTimestampCertificatesWithoutRevocation(!revocationDataAvailableConstraint);
    }

    private boolean getRevocationDataAvailablePresent(Context context) {
        LevelRule signingCertificateRule = validationPolicy.getRevocationDataAvailableConstraint(context, SubContext.SIGNING_CERT);
        if (signingCertificateRule != null && Level.FAIL.equals(signingCertificateRule.getLevel())) {
            return true;
        }
        LevelRule caCertificateRule = validationPolicy.getRevocationDataAvailableConstraint(context, SubContext.CA_CERTIFICATE);
        if (caCertificateRule != null && Level.FAIL.equals(caCertificateRule.getLevel())) {
            return true;
        }
        return false;
    }

}
