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
package eu.europa.esig.dss.policy;

import eu.europa.esig.dss.enumerations.CryptographicSuiteAlgorithmUsage;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.policy.CryptographicSuite;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteEvaluation;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteParameter;
import eu.europa.esig.dss.policy.jaxb.Algo;
import eu.europa.esig.dss.policy.jaxb.AlgoExpirationDate;
import eu.europa.esig.dss.policy.jaxb.CryptographicConstraint;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.policy.jaxb.ListAlgo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

/**
 * Wraps a {@code CryptographicConstraint} of the DSS JAXB validation policy implementation
 * into a {@code CryptographicConstraintWrapper}
 *
 */
public class CryptographicConstraintWrapper extends LevelConstraintWrapper implements CryptographicSuite {

    private static final Logger LOG = LoggerFactory.getLogger(CryptographicConstraintWrapper.class);

    /** The default date format */
    private static final String DEFAULT_DATE_FORMAT = "yyyy-MM-dd";

    /** The default timezone (UTC) */
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    /** Key size parameter used by RSA algorithms */
    private static final String MODULES_LENGTH_PARAMETER = "moduluslength";

    /** P Length key size parameter used by DSA algorithms */
    private static final String PLENGTH_PARAMETER = "plength";

    private Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> acceptableDigestAlgorithms;

    private Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> acceptableSignatureAlgorithms;

    /**
     * Constructor to create an empty instance of Cryptographic constraints
     */
    public CryptographicConstraintWrapper() {
        super(null);
    }

    /**
     * Default constructor
     *
     * @param constraint {@link CryptographicConstraint}
     */
    public CryptographicConstraintWrapper(CryptographicConstraint constraint) {
        super(constraint);
    }

    @Override
    public String getPolicyName() {
        return "DSS Cryptographic Constraint";
    }

    @Override
    public Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> getAcceptableDigestAlgorithms() {
        if (acceptableDigestAlgorithms == null) {
            acceptableDigestAlgorithms = new EnumMap<>(DigestAlgorithm.class);

            // Step 1. Build evaluations based on acceptable algo list
            ListAlgo digestAlgo = ((CryptographicConstraint) constraint).getAcceptableDigestAlgo();
            if (digestAlgo != null) {
                List<Algo> algos = digestAlgo.getAlgos();
                if (algos != null && !algos.isEmpty()) {
                    for (Algo algo : algos) {
                        DigestAlgorithm digestAlgorithm = toDigestAlgorithm(algo.getValue());
                        if (digestAlgorithm != null) {
                            acceptableDigestAlgorithms.computeIfAbsent(digestAlgorithm, v -> new HashSet<>());
                        }
                    }
                }
            }
            // Step 2. Build evaluations based on expiration dates (for acceptable digest algos only)
            AlgoExpirationDate algoExpirationDate = ((CryptographicConstraint) constraint).getAlgoExpirationDate();
            if (algoExpirationDate != null) {
                SimpleDateFormat dateFormat = getUsedDateFormat(algoExpirationDate);
                List<Algo> algos = algoExpirationDate.getAlgos();
                if (algos != null && !algos.isEmpty()) {
                    for (Algo algo : algos) {
                        DigestAlgorithm digestAlgorithm = toDigestAlgorithm(algo.getValue());
                        if (digestAlgorithm != null && acceptableDigestAlgorithms.containsKey(digestAlgorithm)) {
                            CryptographicSuiteEvaluation evaluation = buildEvaluation(algo, dateFormat);
                            acceptableDigestAlgorithms.computeIfAbsent(digestAlgorithm, v -> new HashSet<>())
                                    .add(evaluation);
                        }
                    }
                }
            }
            // Step 3. For acceptable digest algos without expiration date, add an empty evaluation (does not expire)
            for (Set<CryptographicSuiteEvaluation> evaluationList : acceptableDigestAlgorithms.values()) {
                if (evaluationList.isEmpty()) {
                    evaluationList.add(new CryptographicSuiteEvaluation());
                }
            }
        }
        return acceptableDigestAlgorithms;
    }

    @Override
    public Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> getAcceptableSignatureAlgorithms() {
        if (acceptableSignatureAlgorithms == null) {
            acceptableSignatureAlgorithms = new EnumMap<>(SignatureAlgorithm.class);

            Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> acceptableDigestAlgorithmsMap = getAcceptableDigestAlgorithms();

            // Step 1. Build evaluations based on acceptable algo list
            ListAlgo encryptionAlgo = ((CryptographicConstraint) constraint).getAcceptableEncryptionAlgo();
            if (encryptionAlgo != null) {
                List<Algo> algos = encryptionAlgo.getAlgos();
                if (algos != null && !algos.isEmpty()) {
                    for (Algo algo : algos) {
                        EncryptionAlgorithm encryptionAlgorithm = toEncryptionAlgorithm(algo.getValue());
                        if (encryptionAlgorithm != null) {
                            for (DigestAlgorithm digestAlgorithm : acceptableDigestAlgorithmsMap.keySet()) {
                                SignatureAlgorithm signatureAlgorithm = findSignatureAlgorithm(encryptionAlgorithm, digestAlgorithm);
                                if (signatureAlgorithm != null) {
                                    acceptableSignatureAlgorithms.computeIfAbsent(signatureAlgorithm, v -> new HashSet<>());
                                }
                            }
                        }
                    }
                }
            }
            // Step 2a. Build evaluations based on expiration dates (for acceptable signature algos only)
            AlgoExpirationDate algoExpirationDate = ((CryptographicConstraint) constraint).getAlgoExpirationDate();
            if (algoExpirationDate != null) {
                SimpleDateFormat dateFormat = getUsedDateFormat(algoExpirationDate);
                List<Algo> algos = algoExpirationDate.getAlgos();
                if (algos != null && !algos.isEmpty()) {
                    for (Algo algo : algos) {
                        EncryptionAlgorithm encryptionAlgorithm = toEncryptionAlgorithm(algo.getValue());
                        if (encryptionAlgorithm != null) {
                            for (Map.Entry<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> entry : acceptableDigestAlgorithmsMap.entrySet()) {
                                SignatureAlgorithm signatureAlgorithm = findSignatureAlgorithm(encryptionAlgorithm, entry.getKey());
                                if (signatureAlgorithm != null && acceptableSignatureAlgorithms.containsKey(signatureAlgorithm)) {
                                    Date digestAlgoValidityEnd = getAlgorithmExpirationDate(entry.getValue());
                                    CryptographicSuiteEvaluation evaluation = buildEvaluation(encryptionAlgorithm, algo, dateFormat, digestAlgoValidityEnd);
                                    acceptableSignatureAlgorithms.computeIfAbsent(signatureAlgorithm, v -> new HashSet<>())
                                            .add(evaluation);
                                }
                            }
                        }
                    }
                }
            }
            // Step 2b. Build evaluations based on min key sizes (for acceptable signature algos only)
            ListAlgo miniPublicKeySize = ((CryptographicConstraint) constraint).getMiniPublicKeySize();
            if (miniPublicKeySize != null) {
                List<Algo> algos = miniPublicKeySize.getAlgos();
                if (algos != null && !algos.isEmpty()) {
                    for (Algo algo : algos) {
                        EncryptionAlgorithm encryptionAlgorithm = toEncryptionAlgorithm(algo.getValue());
                        if (encryptionAlgorithm != null) {
                            for (Map.Entry<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> entry : acceptableDigestAlgorithmsMap.entrySet()) {
                                SignatureAlgorithm signatureAlgorithm = findSignatureAlgorithm(encryptionAlgorithm, entry.getKey());
                                if (signatureAlgorithm != null && acceptableSignatureAlgorithms.containsKey(signatureAlgorithm)) {
                                    Set<CryptographicSuiteEvaluation> evaluations = acceptableSignatureAlgorithms.get(signatureAlgorithm);
                                    Date digestAlgoValidityEnd = getAlgorithmExpirationDate(entry.getValue());
                                    evaluations = getFloorEvaluations(evaluations, encryptionAlgorithm, algo, digestAlgoValidityEnd);
                                    acceptableSignatureAlgorithms.put(signatureAlgorithm, evaluations);
                                }
                            }
                        }
                    }
                }
            }
            // Step 3. For acceptable signature algos without expiration date, add an empty evaluation (does not expire)
            for (Map.Entry<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> entry : acceptableSignatureAlgorithms.entrySet()) {
                Set<CryptographicSuiteEvaluation> evaluationList = entry.getValue();
                if (evaluationList.isEmpty()) {
                    CryptographicSuiteEvaluation evaluation = new CryptographicSuiteEvaluation();
                    Set<CryptographicSuiteEvaluation> digestAlgoEvaluations = acceptableDigestAlgorithmsMap.get(entry.getKey().getDigestAlgorithm());
                    if (digestAlgoEvaluations != null && !digestAlgoEvaluations.isEmpty()) {
                        evaluation.setValidityEnd(getAlgorithmExpirationDate(digestAlgoEvaluations));
                    }
                    evaluationList.add(evaluation);
                }
            }

        }
        return acceptableSignatureAlgorithms;
    }

    private Date getAlgorithmExpirationDate(Set<CryptographicSuiteEvaluation> evaluations) {
        if (evaluations == null || evaluations.isEmpty()) {
            return null;
        }
        Date expirationDate = null;
        for (CryptographicSuiteEvaluation evaluation : evaluations) {
            Date validityEnd = evaluation.getValidityEnd();
            if (validityEnd == null) {
                return null;
            }
            if (expirationDate == null || expirationDate.before(validityEnd)) {
                expirationDate = validityEnd;
            }
        }
        return expirationDate;
    }

    private Set<CryptographicSuiteEvaluation> getFloorEvaluations(Set<CryptographicSuiteEvaluation> existingEvaluations,
                                                                  EncryptionAlgorithm encryptionAlgorithm, Algo algo, Date forcedValidityEnd) {
        if (!existingEvaluations.isEmpty()) {
            Integer minSize = algo.getSize();
            for (CryptographicSuiteEvaluation evaluation : existingEvaluations) {
                for (CryptographicSuiteParameter parameter : evaluation.getParameterList()) {
                    if (minSize != null && (parameter.getMin() == null || minSize > parameter.getMin())) {
                        parameter.setMin(minSize);
                    }
                }
            }
        } else {
            CryptographicSuiteEvaluation evaluation = buildEvaluation(encryptionAlgorithm, algo, null, forcedValidityEnd);
            existingEvaluations.add(evaluation);
        }
        return existingEvaluations;
    }

    private CryptographicSuiteEvaluation buildEvaluation(Algo algo, SimpleDateFormat simpleDateFormat) {
        return buildEvaluation(null, algo, simpleDateFormat, null);
    }

    private CryptographicSuiteEvaluation buildEvaluation(EncryptionAlgorithm encryptionAlgorithm, Algo algo, SimpleDateFormat simpleDateFormat, Date forcedValidityEnd) {
        CryptographicSuiteEvaluation evaluation = new CryptographicSuiteEvaluation();
        evaluation.setParameterList(buildParameters(encryptionAlgorithm, algo));
        if (simpleDateFormat != null) {
            Date validityEnd = getDate(algo, simpleDateFormat);
            if (validityEnd == null || (forcedValidityEnd != null && validityEnd.after(forcedValidityEnd))) {
                validityEnd = forcedValidityEnd;
            }
            evaluation.setValidityEnd(validityEnd);
        }
        evaluation.setAlgorithmUsage(buildUsages());
        return evaluation;
    }


    private List<CryptographicSuiteParameter> buildParameters(EncryptionAlgorithm encryptionAlgorithm, Algo algo) {
        List<CryptographicSuiteParameter> parameters = new ArrayList<>();
        if (algo.getSize() != null) {
            CryptographicSuiteParameter parameter = new CryptographicSuiteParameter();
            parameter.setName(getParameterName(encryptionAlgorithm));
            parameter.setMin(algo.getSize());
            parameters.add(parameter);
        }
        return parameters;
    }

    private String getParameterName(EncryptionAlgorithm encryptionAlgorithm) {
        if (encryptionAlgorithm == null) {
            return null;
        } else if (EncryptionAlgorithm.RSA.isEquivalent(encryptionAlgorithm)) {
            return MODULES_LENGTH_PARAMETER;
        } else if (EncryptionAlgorithm.DSA.isEquivalent(encryptionAlgorithm) ||
                EncryptionAlgorithm.ECDSA.isEquivalent(encryptionAlgorithm) ||
                EncryptionAlgorithm.EDDSA.isEquivalent(encryptionAlgorithm)) {
            return PLENGTH_PARAMETER;
        }
        return null;
    }

    private List<CryptographicSuiteAlgorithmUsage> buildUsages() {
        // only global is supported by far
        return Collections.emptyList();
    }

    private DigestAlgorithm toDigestAlgorithm(String algorithmName) {
        try {
            return DigestAlgorithm.forName(algorithmName);
        } catch (IllegalArgumentException e) {
            // continue silently
            return null;
        }
    }

    private SimpleDateFormat getUsedDateFormat(AlgoExpirationDate expirations) {
        SimpleDateFormat sdf = new SimpleDateFormat(expirations.getFormat() != null ? expirations.getFormat() : DEFAULT_DATE_FORMAT);
        sdf.setTimeZone(UTC);
        return sdf;
    }

    private Date getDate(Algo algo, SimpleDateFormat format) {
        if (algo != null) {
            return getDate(algo.getDate(), format);
        }
        return null;
    }

    private Date getDate(String dateString, SimpleDateFormat format) {
        if (dateString != null) {
            try {
                return format.parse(dateString);
            } catch (ParseException e) {
                LOG.warn("Unable to parse '{}' with format '{}'", dateString, format);
            }
        }
        return null;
    }

    private SignatureAlgorithm findSignatureAlgorithm(EncryptionAlgorithm encryptionAlgorithm, DigestAlgorithm digestAlgorithm) {
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.getAlgorithm(encryptionAlgorithm, digestAlgorithm);
        if (signatureAlgorithm == null) {
            LOG.trace("Cannot find a SignatureAlgorithm for combination of {} with {}.", encryptionAlgorithm.getName(), digestAlgorithm.getName());
        }
        return signatureAlgorithm;
    }

    private EncryptionAlgorithm toEncryptionAlgorithm(String algorithmName) {
        try {
            return EncryptionAlgorithm.forName(algorithmName);
        } catch (IllegalArgumentException e) {
            // continue silently
            return null;
        }
    }

    @Override
    public void setLevel(Level level) {
        if (constraint != null) {
            constraint.setLevel(level);
        }
    }

    @Override
    public Level getAcceptableSignatureAlgorithmsLevel() {
        if (constraint != null) {
            return getCryptographicLevel(((CryptographicConstraint) constraint).getAcceptableEncryptionAlgo());
        }
        return null;
    }

    @Override
    public void setAcceptableSignatureAlgorithmsLevel(Level acceptableEncryptionAlgorithmsLevel) {
        if (constraint != null) {
            ListAlgo acceptableEncryptionAlgo = ((CryptographicConstraint) constraint).getAcceptableEncryptionAlgo();
            if (acceptableEncryptionAlgo != null) {
                acceptableEncryptionAlgo.setLevel(acceptableEncryptionAlgorithmsLevel);
            }
        }
    }

    @Override
    public Level getAcceptableSignatureAlgorithmsMiniKeySizeLevel() {
        if (constraint != null) {
            return getCryptographicLevel(((CryptographicConstraint) constraint).getMiniPublicKeySize());
        }
        return null;
    }

    @Override
    public void setAcceptableSignatureAlgorithmsMiniKeySizeLevel(Level acceptableEncryptionAlgorithmsMiniKeySizeLevel) {
        if (constraint != null) {
            ListAlgo miniPublicKeySize = ((CryptographicConstraint) constraint).getMiniPublicKeySize();
            if (miniPublicKeySize != null) {
                miniPublicKeySize.setLevel(acceptableEncryptionAlgorithmsMiniKeySizeLevel);
            }
        }
    }

    @Override
    public Level getAcceptableDigestAlgorithmsLevel() {
        if (constraint != null) {
            return getCryptographicLevel(((CryptographicConstraint) constraint).getAcceptableDigestAlgo());
        }
        return null;
    }

    @Override
    public void setAcceptableDigestAlgorithmsLevel(Level acceptableDigestAlgorithmsLevel) {
        if (constraint != null) {
            ListAlgo acceptableDigestAlgo = ((CryptographicConstraint) constraint).getAcceptableDigestAlgo();
            if (acceptableDigestAlgo != null) {
                acceptableDigestAlgo.setLevel(acceptableDigestAlgorithmsLevel);
            }
        }
    }

    @Override
    public Level getAlgorithmsExpirationDateLevel() {
        if (constraint != null) {
            return getCryptographicLevel(((CryptographicConstraint) constraint).getAlgoExpirationDate());
        }
        return null;
    }

    @Override
    public void setAlgorithmsExpirationDateLevel(Level algorithmsExpirationDateLevel) {
        if (constraint != null) {
            AlgoExpirationDate algoExpirationDate = ((CryptographicConstraint) constraint).getAlgoExpirationDate();
            if (algoExpirationDate != null) {
                algoExpirationDate.setLevel(algorithmsExpirationDateLevel);
            }
        }
    }

    @Override
    public Level getAlgorithmsExpirationDateAfterUpdateLevel() {
        if (constraint != null) {
            AlgoExpirationDate algoExpirationDate = ((CryptographicConstraint) constraint).getAlgoExpirationDate();
            if (algoExpirationDate != null && algoExpirationDate.getLevelAfterUpdate() != null) {
                return algoExpirationDate.getLevelAfterUpdate();
            }
            return getCryptographicLevel(algoExpirationDate);
        }
        return null;
    }

    @Override
    public void setAlgorithmsExpirationTimeAfterPolicyUpdateLevel(Level algorithmsExpirationTimeAfterPolicyUpdateLevel) {
        if (constraint != null) {
            AlgoExpirationDate algoExpirationDate = ((CryptographicConstraint) constraint).getAlgoExpirationDate();
            if (algoExpirationDate != null) {
                algoExpirationDate.setLevelAfterUpdate(algorithmsExpirationTimeAfterPolicyUpdateLevel);
            }
        }
    }

    @Override
    public Date getCryptographicSuiteUpdateDate() {
        if (constraint != null) {
            AlgoExpirationDate algoExpirationDates = ((CryptographicConstraint) constraint).getAlgoExpirationDate();
            if (algoExpirationDates != null) {
                final SimpleDateFormat dateFormat = getUsedDateFormat(algoExpirationDates);
                return getDate(algoExpirationDates.getUpdateDate(), dateFormat);
            }
        }
        return null;
    }

    private Level getCryptographicLevel(LevelConstraint cryptoConstraint) {
        if (cryptoConstraint != null && cryptoConstraint.getLevel() != null) {
            return cryptoConstraint.getLevel();
        }
        // return global Level if target level is not present
        return getLevel();
    }

}
