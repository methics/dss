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
package eu.europa.esig.dss.model.policy.crypto;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.policy.CryptographicSuite;

import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * This class contains common methods for processing XML and JSON TS 119 322 schemas.
 *
 */
public class CryptographicSuite19322 implements CryptographicSuite {

    /** Metadata of the cryptographic suite */
    private final CryptographicSuiteMetadata metadata;

    /** The list of applicable algorithm constraints */
    private final List<CryptographicSuiteAlgorithm> algorithmList;

    /** Defines global execution level of the cryptographic rules */
    private Level globalLevel = Level.FAIL;

    /** Defines execution level of the acceptability of signature algorithms check */
    private Level acceptableSignatureAlgorithmsLevel;

    /** Defines execution level of the acceptability of the signature algorithms' key length check */
    private Level acceptableSignatureAlgorithmsMinKeySizeLevel;

    /** Defines execution level of the acceptability of digest algorithms check */
    private Level acceptableDigestAlgorithmsLevel;

    /** Defines execution level of the algorithms expiration check */
    private Level algorithmsExpirationDateLevel;

    /** Defines execution level of the algorithms expiration check with expiration occurred after the update of the cryptographic suite */
    private Level algorithmsExpirationTimeAfterPolicyUpdateLevel = Level.WARN;

    /** Cached map of acceptable digest algorithms and their corresponding validation requirements */
    private Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> acceptableDigestAlgorithms;

    /** Cached map of acceptable signature algorithms and their corresponding validation requirements */
    private Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> acceptableSignatureAlgorithms;

    /**
     * Default constructor
     *
     * @param metadata {@link CryptographicSuiteMetadata}
     * @param algorithmList a collection of {@link CryptographicSuiteAlgorithm}s
     */
    public CryptographicSuite19322(final CryptographicSuiteMetadata metadata, final List<CryptographicSuiteAlgorithm> algorithmList) {
        Objects.requireNonNull(metadata, "metadata cannot be null!");
        Objects.requireNonNull(algorithmList, "algorithmList cannot be null!");
        this.metadata = metadata;
        this.algorithmList = algorithmList;
    }

    @Override
    public String getPolicyName() {
        return metadata.getPolicyName();
    }

    @Override
    public Date getCryptographicSuiteUpdateDate() {
        return metadata.getPolicyIssueDate();
    }

    @Override
    public Level getLevel() {
        return globalLevel;
    }

    @Override
    public void setLevel(Level level) {
        this.globalLevel = level;
    }

    @Override
    public Level getAcceptableDigestAlgorithmsLevel() {
        return getLevel(acceptableDigestAlgorithmsLevel);
    }

    @Override
    public void setAcceptableDigestAlgorithmsLevel(Level acceptableDigestAlgorithmsLevel) {
        this.acceptableDigestAlgorithmsLevel = acceptableDigestAlgorithmsLevel;
    }

    @Override
    public Level getAcceptableSignatureAlgorithmsLevel() {
        return getLevel(acceptableSignatureAlgorithmsLevel);
    }

    @Override
    public void setAcceptableSignatureAlgorithmsLevel(Level acceptableSignatureAlgorithmsLevel) {
        this.acceptableSignatureAlgorithmsLevel = acceptableSignatureAlgorithmsLevel;
    }

    @Override
    public Level getAcceptableSignatureAlgorithmsMiniKeySizeLevel() {
        return getLevel(acceptableSignatureAlgorithmsMinKeySizeLevel);
    }

    @Override
    public void setAcceptableSignatureAlgorithmsMiniKeySizeLevel(Level acceptableSignatureAlgorithmsMiniKeySizeLevel) {
        this.acceptableSignatureAlgorithmsMinKeySizeLevel = acceptableSignatureAlgorithmsMiniKeySizeLevel;
    }

    @Override
    public Level getAlgorithmsExpirationDateLevel() {
        return getLevel(algorithmsExpirationDateLevel);
    }

    @Override
    public void setAlgorithmsExpirationDateLevel(Level algorithmsExpirationDateLevel) {
        this.algorithmsExpirationDateLevel = algorithmsExpirationDateLevel;
    }

    @Override
    public Level getAlgorithmsExpirationDateAfterUpdateLevel() {
        return algorithmsExpirationTimeAfterPolicyUpdateLevel;
    }

    @Override
    public void setAlgorithmsExpirationTimeAfterPolicyUpdateLevel(Level algorithmsExpirationTimeAfterPolicyUpdateLevel) {
        this.algorithmsExpirationTimeAfterPolicyUpdateLevel = algorithmsExpirationTimeAfterPolicyUpdateLevel;
    }

    private Level getLevel(Level level) {
        // returns global level in case of failure
        return level != null ? level : globalLevel;
    }

    public Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> getAcceptableDigestAlgorithms() {
        if (acceptableDigestAlgorithms == null) {
            acceptableDigestAlgorithms = new EnumMap<>(DigestAlgorithm.class);
            for (CryptographicSuiteAlgorithm algorithm : algorithmList) {
                DigestAlgorithm digestAlgorithm = getDigestAlgorithm(algorithm);
                if (digestAlgorithm == null) {
                    continue;
                }
                acceptableDigestAlgorithms.computeIfAbsent(digestAlgorithm, v -> new HashSet<>())
                        .addAll(algorithm.getEvaluationList());
            }
        }
        return acceptableDigestAlgorithms;
    }

    private DigestAlgorithm getDigestAlgorithm(CryptographicSuiteAlgorithm algorithm) {
        if (algorithm == null) {
            return null;
        }

        // NOTE: Name is not evaluated, it is not supposed to be machine-processable
        List<String> objectIdentifiers = algorithm.getAlgorithmIdentifierOIDs();
        if (objectIdentifiers != null && !objectIdentifiers.isEmpty()) {
            for (String oid : objectIdentifiers) {
                try {
                    // first come, first served policy
                    return DigestAlgorithm.forOID(oid);
                } catch (IllegalArgumentException e) {
                    // continue silently
                }
            }
        }
        // optional
        List<String> uris = algorithm.getAlgorithmIdentifierURIs();
        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                try {
                    return DigestAlgorithm.forXML(uri);
                } catch (IllegalArgumentException e) {
                    // continue silently
                }
            }
        }
        return null;
    }

    @Override
    public Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> getAcceptableSignatureAlgorithms() {
        if (acceptableSignatureAlgorithms == null) {
            acceptableSignatureAlgorithms = new EnumMap<>(SignatureAlgorithm.class);

            // Step 1. Find all entries matching the SignatureAlgorithm definition
            for (CryptographicSuiteAlgorithm algorithm : algorithmList) {
                SignatureAlgorithm signatureAlgorithm = getSignatureAlgorithm(algorithm);
                if (signatureAlgorithm == null) {
                    continue;
                }
                acceptableSignatureAlgorithms.computeIfAbsent(signatureAlgorithm, v -> new HashSet<>())
                        .addAll(algorithm.getEvaluationList());
            }

            // Step 2a. Extract supported digest algorithms for mapping
            Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> digestAlgorithmsMap = getAcceptableDigestAlgorithms();

            // Step 2b. Extract supported encryption algorithms for mapping
            // NOTE: we build a temp map to avoid conflict with acceptableSignatureAlgorithms during map building
            Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> tempMap = new EnumMap<>(SignatureAlgorithm.class);
            for (CryptographicSuiteAlgorithm algorithm : algorithmList) {
                EncryptionAlgorithm encryptionAlgorithm = getEncryptionAlgorithm(algorithm);
                if (encryptionAlgorithm == null) {
                    continue;
                }
                for (Map.Entry<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> entry : digestAlgorithmsMap.entrySet()) {
                    SignatureAlgorithm signatureAlgorithm = findSignatureAlgorithm(encryptionAlgorithm, entry.getKey());
                    if (signatureAlgorithm == null) {
                        continue;
                    }
                    if (acceptableSignatureAlgorithms.containsKey(signatureAlgorithm)) {
                        // if the SignatureAlgorithm is already present, prefer the explicit definition
                        continue;
                    }

                    // Apply stricter requirements, if applicable
                    final Set<CryptographicSuiteEvaluation> finalEvaluationList = new HashSet<>();
                    for (CryptographicSuiteEvaluation evaluation : algorithm.getEvaluationList()) {
                        for (CryptographicSuiteEvaluation digestAlgoEvaluation : entry.getValue()) {
                            evaluation = CryptographicSuiteEvaluation.copy(evaluation);
                            Date digestAlgoValidityStart = digestAlgoEvaluation.getValidityStart();
                            if (digestAlgoValidityStart != null && (evaluation.getValidityStart() == null || digestAlgoValidityStart.after(evaluation.getValidityStart()))) {
                                evaluation.setValidityStart(digestAlgoValidityStart);
                            }
                            Date digestAlgoValidityEnd = digestAlgoEvaluation.getValidityEnd();
                            if (digestAlgoValidityEnd != null && (evaluation.getValidityEnd() == null || digestAlgoValidityEnd.before(evaluation.getValidityEnd()))) {
                                evaluation.setValidityEnd(digestAlgoValidityEnd);
                            }

                            // avoid dates misconfiguration
                            if (evaluation.getValidityStart() == null || evaluation.getValidityEnd() == null
                                    || evaluation.getValidityStart().before(evaluation.getValidityEnd())) {
                                finalEvaluationList.add(evaluation);
                            }
                        }
                    }

                    tempMap.computeIfAbsent(signatureAlgorithm, v -> new HashSet<>())
                            .addAll(finalEvaluationList);
                }
            }

            // Step 2c. Populate the main map
            for (Map.Entry<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> entry : tempMap.entrySet()) {
                acceptableSignatureAlgorithms.computeIfAbsent(entry.getKey(), v -> new HashSet<>())
                        .addAll(entry.getValue());
            }

        }
        return acceptableSignatureAlgorithms;
    }

    private SignatureAlgorithm getSignatureAlgorithm(CryptographicSuiteAlgorithm algorithm) {
        if (algorithm == null) {
            return null;
        }
        List<String> objectIdentifiers = algorithm.getAlgorithmIdentifierOIDs();
        if (objectIdentifiers != null && !objectIdentifiers.isEmpty()) {
            for (String oid : objectIdentifiers) {
                try {
                    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forOID(oid);
                    if (signatureAlgorithm != null) {
                        /*
                         * Here we check for a potential conflict with the EncryptionAlgorithm definition.
                         * If matching EncryptionAlgorithm is found as well, then we continue with the URIs check.
                         * Example: RSASSA-PSS using the same OID ("1.2.840.113549.1.1.10") as the RSA_SSA_PSS_SHA1_MGF1 Signature Algorithm
                         */
                        EncryptionAlgorithm encryptionAlgorithm = getEncryptionAlgorithm(algorithm);
                        if (encryptionAlgorithm == null) {
                            return signatureAlgorithm;
                        }
                    }
                } catch (IllegalArgumentException e) {
                    // continue silently
                }
            }
        }
        // optional
        List<String> uris = algorithm.getAlgorithmIdentifierURIs();
        if (uris != null && !uris.isEmpty()) {
            for (String uri : uris) {
                try {
                    SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forXML(uri);
                    if (signatureAlgorithm != null) {
                        return signatureAlgorithm;
                    }
                } catch (IllegalArgumentException e) {
                    // continue silently
                }
            }
        }
        return null;
    }

    private EncryptionAlgorithm getEncryptionAlgorithm(CryptographicSuiteAlgorithm algorithm) {
        if (algorithm == null) {
            return null;
        }
        List<String> objectIdentifiers = algorithm.getAlgorithmIdentifierOIDs();
        if (objectIdentifiers != null && !objectIdentifiers.isEmpty()) {
            for (String oid : objectIdentifiers) {
                try {
                    // first come, first served policy
                    return EncryptionAlgorithm.forOID(oid);
                } catch (IllegalArgumentException e) {
                    // continue silently
                }
            }
        }
        return null;
    }

    private SignatureAlgorithm findSignatureAlgorithm(EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm) {
        return SignatureAlgorithm.getAlgorithm(encryptionAlgorithm, digestAlgorithm);
    }

}
