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

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteEvaluation;

import java.util.Set;
import java.util.Date;
import java.util.Map;

/**
 * Contains policy for validation of cryptographic suites used on the signature or certificates
 *
 */
public interface CryptographicSuite extends LevelRule {

    /**
     * Gets a cryptographic suite name
     *
     * @return {@link String}
     */
    String getPolicyName();

    /**
     * Gets a map of DigestAlgorithm's extracted from a cryptographic suite and
     * their corresponding {@code CryptographicSuiteEvaluation} rules
     *
     * @return a link between {@code DigestAlgorithm}s and corresponding {@link CryptographicSuiteEvaluation}s
     */
    Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> getAcceptableDigestAlgorithms();

    /**
     * Gets a map of SignatureAlgorithm's extracted from a cryptographic suite and
     * their corresponding {@code CryptographicSuiteEvaluation} rules
     *
     * @return a link between {@code SignatureAlgorithm}s and corresponding {@link CryptographicSuiteEvaluation}s
     */
    Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> getAcceptableSignatureAlgorithms();

    /**
     * Sets the global execution level for the cryptographic suite constraints
     *
     * @param level {@link Level}
     */
    void setLevel(Level level);

    /**
     * Returns a level constraint for AcceptableDigestAlgo constraint if present,
     * the global {@code getLevel} otherwise.
     *
     * @return {@link Level}
     */
    Level getAcceptableDigestAlgorithmsLevel();

    /**
     * Sets the execution level for the acceptable digest algorithms check
     *
     * @param acceptableDigestAlgorithmsLevel {@link Level}
     */
    void setAcceptableDigestAlgorithmsLevel(Level acceptableDigestAlgorithmsLevel);

    /**
     * Returns a level constraint for AcceptableEncryptionAlgo constraint if present,
     * the global {@code getLevel} otherwise.
     *
     * @return {@link Level}
     */
    Level getAcceptableSignatureAlgorithmsLevel();

    /**
     * Sets the execution level for the acceptable signature algorithms check
     *
     * @param acceptableSignatureAlgorithmsLevel {@link Level}
     */
    void setAcceptableSignatureAlgorithmsLevel(Level acceptableSignatureAlgorithmsLevel);

    /**
     * Returns a level constraint for MiniPublicKeySize constraint if present,
     * the global {@code getLevel} otherwise.
     *
     * @return {@link Level}
     */
    Level getAcceptableSignatureAlgorithmsMiniKeySizeLevel();

    /**
     * Sets the execution level for the acceptable minimum key sizes of signature algorithms check
     *
     * @param acceptableSignatureAlgorithmsMiniKeySizeLevel {@link Level}
     */
    void setAcceptableSignatureAlgorithmsMiniKeySizeLevel(Level acceptableSignatureAlgorithmsMiniKeySizeLevel);

    /**
     * Returns a level constraint for AlgoExpirationDate constraint if present,
     * the global {@code getLevel} otherwise.
     *
     * @return {@link Level}
     */
    Level getAlgorithmsExpirationDateLevel();

    /**
     * Sets the execution level for checking algorithms expiration
     *
     * @param algorithmsExpirationDateLevel {@link Level}
     */
    void setAlgorithmsExpirationDateLevel(Level algorithmsExpirationDateLevel);

    /**
     * Returns a level constraint for AlgoExpirationDate constraint if present,
     * the global {@code getLevel} otherwise.
     *
     * @return {@link Level}
     */
    Level getAlgorithmsExpirationDateAfterUpdateLevel();

    /**
     * Sets the execution level for checking algorithms expiration after the validation policy update
     * Default : Level.WARN (warning message is returned in case of expiration of the used cryptographic constraints
     *                       after the policy update date)
     *
     * @param algorithmsExpirationTimeAfterPolicyUpdateLevel {@link Level}
     */
    void setAlgorithmsExpirationTimeAfterPolicyUpdateLevel(Level algorithmsExpirationTimeAfterPolicyUpdateLevel);

    /**
     * Returns a date of the update of the cryptographic suites within the validation policy
     *
     * @return {@link Date}
     */
    Date getCryptographicSuiteUpdateDate();

}
