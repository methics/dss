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

import eu.europa.esig.dss.enumerations.CryptographicSuiteAlgorithmUsage;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class CryptographicSuite19322Test {

    private static final String DATE_FORMAT = "yyyy-MM-dd";

    private static final String MODULES_LENGTH = "moduluslength";
    private static final String PLENGTH = "plength";
    private static final String QLENGTH = "glength";

    @Test
    void getAcceptableDigestAlgorithmsTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), Collections.emptyList());
        assertEquals(Collections.emptyMap(), cryptographicSuite.getAcceptableDigestAlgorithms());

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2029-01-01"))));

        Set<DigestAlgorithm> expectedSet = new HashSet<>(Collections.singletonList(DigestAlgorithm.SHA224));
        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expectedSet, cryptographicSuite.getAcceptableDigestAlgorithms().keySet());

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, null));

        expectedSet = new HashSet<>(Arrays.asList(DigestAlgorithm.SHA224, DigestAlgorithm.SHA256));
        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expectedSet, cryptographicSuite.getAcceptableDigestAlgorithms().keySet());

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA384, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA512, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA3_256, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA3_384, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA3_512, null));
        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        expectedSet = new HashSet<>(Arrays.asList(
                DigestAlgorithm.SHA224, DigestAlgorithm.SHA256, DigestAlgorithm.SHA384, DigestAlgorithm.SHA512,
                DigestAlgorithm.SHA3_256, DigestAlgorithm.SHA3_384, DigestAlgorithm.SHA3_512));

        assertEquals(expectedSet, cryptographicSuite.getAcceptableDigestAlgorithms().keySet());
    }

    @Test
    void getAcceptableDigestAlgorithmsWithEmptyListTest() {
        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), Collections.emptyList());

        Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> result = cryptographicSuite.getAcceptableDigestAlgorithms();
        assertTrue(result.isEmpty());
    }

    @Test
    void getAcceptableDigestAlgorithmsWithDuplicateAlgorithmsTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2021-01-01"))));

        Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> expected = new EnumMap<>(DigestAlgorithm.class);

        expected.put(DigestAlgorithm.SHA224, createEvaluations(
                Collections.singletonList(new EvaluationDTO("2021-01-01"))));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableDigestAlgorithms());

        // duplicate entries test

        algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2021-01-01"))));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2029-01-01"))));

        expected = new EnumMap<>(DigestAlgorithm.class);

        expected.put(DigestAlgorithm.SHA224, createEvaluations(Arrays.asList(
                new EvaluationDTO("2021-01-01"), new EvaluationDTO("2029-01-01")
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableDigestAlgorithms());

        // opposite test

        algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2029-01-01"))));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2021-01-01"))));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableDigestAlgorithms());

        // null test

        algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2021-01-01"))));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2029-01-01"))));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO(null))));

        expected = new EnumMap<>(DigestAlgorithm.class);

        expected.put(DigestAlgorithm.SHA224, createEvaluations(Arrays.asList(
                new EvaluationDTO("2021-01-01"), new EvaluationDTO("2029-01-01"), new EvaluationDTO(null)
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableDigestAlgorithms());

        // opposite null test

        algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO(null))));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2021-01-01"))));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2029-01-01"))));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableDigestAlgorithms());
    }

    @Test
    void getAcceptableDigestAlgorithmsWithUnknownAlgorithmTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        // Assuming this creates an unknown algorithm
        CryptographicSuiteAlgorithm algorithm = new CryptographicSuiteAlgorithm();
        algorithm.setAlgorithmIdentifierName("SHA999");
        algorithm.setAlgorithmIdentifierOIDs(Collections.singletonList("1.2.3.1.50"));

        CryptographicSuiteEvaluation evaluation = new CryptographicSuiteEvaluation();
        evaluation.setValidityEnd(toDate("2030-12-31"));

        algorithm.setEvaluationList(Collections.singletonList(evaluation));

        algorithmList.add(algorithm);

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertTrue(cryptographicSuite.getAcceptableDigestAlgorithms().isEmpty());
    }

    @Test
    void getAcceptableDigestAlgorithmsWithExpirationDatesTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2029-01-01"))));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA384, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA512, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA3_256, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA3_384, null));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA3_512, null));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2029, Calendar.JANUARY, 1);

        Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> expected = new EnumMap<>(DigestAlgorithm.class);
        expected.put(DigestAlgorithm.SHA224, createEvaluations(Collections.singletonList(new EvaluationDTO("2029-01-01"))));
        expected.put(DigestAlgorithm.SHA256, Collections.singleton(new CryptographicSuiteEvaluation()));
        expected.put(DigestAlgorithm.SHA384, Collections.singleton(new CryptographicSuiteEvaluation()));
        expected.put(DigestAlgorithm.SHA512, Collections.singleton(new CryptographicSuiteEvaluation()));
        expected.put(DigestAlgorithm.SHA3_256, Collections.singleton(new CryptographicSuiteEvaluation()));
        expected.put(DigestAlgorithm.SHA3_384, Collections.singleton(new CryptographicSuiteEvaluation()));
        expected.put(DigestAlgorithm.SHA3_512, Collections.singleton(new CryptographicSuiteEvaluation()));

        assertEquals(expected, cryptographicSuite.getAcceptableDigestAlgorithms());
    }

    @Test
    void getAcceptableDigestAlgorithmsWithStartDatesTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224,
                Collections.singletonList(new EvaluationDTO("2000-01-01", "2029-01-01", null, null))));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256,
                Collections.singletonList(new EvaluationDTO("2000-01-01", null, null, null))));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
        calendar.clear();
        calendar.set(2029, Calendar.JANUARY, 1);

        Map<DigestAlgorithm, Set<CryptographicSuiteEvaluation>> expected = new EnumMap<>(DigestAlgorithm.class);
        expected.put(DigestAlgorithm.SHA224, createEvaluations(Collections.singletonList(
                new EvaluationDTO("2000-01-01", "2029-01-01", null, null))));
        expected.put(DigestAlgorithm.SHA256, createEvaluations(Collections.singletonList(
                new EvaluationDTO("2000-01-01", null, null, null))));

        assertEquals(expected, cryptographicSuite.getAcceptableDigestAlgorithms());
    }

    @Test
    void getAcceptableSignatureAlgorithmsEmptyList() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        assertTrue(cryptographicSuite.getAcceptableSignatureAlgorithms().isEmpty(), "Expected no signature algorithms for empty list.");
    }

    @Test
    void getAcceptableSignatureAlgorithmsUnknownAlgorithmIgnored() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        // Assuming this creates an unknown algorithm
        CryptographicSuiteAlgorithm algorithm = new CryptographicSuiteAlgorithm();
        algorithm.setAlgorithmIdentifierName("UNKNOWN_ALGO");
        algorithm.setAlgorithmIdentifierOIDs(Collections.singletonList("1.2.3.1.50"));

        CryptographicSuiteEvaluation evaluation = new CryptographicSuiteEvaluation();
        evaluation.setValidityEnd(toDate("2030-12-31"));

        algorithm.setEvaluationList(Collections.singletonList(evaluation));

        algorithmList.add(algorithm);

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertTrue(cryptographicSuite.getAcceptableSignatureAlgorithms().isEmpty(), "Unknown algorithm should be ignored or not parsed.");
    }

    @Test
    void getAcceptableSignatureAlgorithmsTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.DSA, Arrays.asList(
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH)
                )),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)
                ))
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA224, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA256, Collections.emptyList()));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSASSA_PSS, Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        Set<SignatureAlgorithm> expected = new HashSet<>(Arrays.asList(SignatureAlgorithm.ECDSA_SHA224,
                SignatureAlgorithm.ECDSA_SHA256, SignatureAlgorithm.RSA_SHA224));
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms().keySet());

        // Add DigestAlgorithm definition
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA512, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));
        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        expected = new HashSet<>(Arrays.asList(SignatureAlgorithm.ECDSA_SHA224, SignatureAlgorithm.ECDSA_SHA256,
                SignatureAlgorithm.RSA_SHA224, SignatureAlgorithm.DSA_SHA512, SignatureAlgorithm.RSA_SSA_PSS_SHA512_MGF1));
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms().keySet());
    }

    @Test
    void getAcceptableSignatureAlgorithmsWithMinKeySizesDuplicatesHandledCorrectly() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.DSA, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1024, PLENGTH)))
        )));
        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.DSA, Collections.singletonList(
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(2048, PLENGTH)))
        )));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        
        Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> expected = new EnumMap<>(SignatureAlgorithm.class);
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // Add DigestAlgorithm definition
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA512, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        expected.put(SignatureAlgorithm.DSA_SHA256, createEvaluations(Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1024, PLENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(2048, PLENGTH)))
        )));
        expected.put(SignatureAlgorithm.DSA_SHA512, createEvaluations(Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1024, PLENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(2048, PLENGTH)))
        )));
        
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());
    }

    @Test
    void getAcceptableSignatureAlgorithmsWithMinKeySizesMissingParameter() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        CryptographicSuiteAlgorithm dsa = new CryptographicSuiteAlgorithm();
        dsa.setAlgorithmIdentifierName("DSA");

        CryptographicSuiteEvaluation evaluation = new CryptographicSuiteEvaluation();
        evaluation.setValidityEnd(toDate("2029-01-01"));

        CryptographicSuiteParameter parameter = new CryptographicSuiteParameter();
        parameter.setName(PLENGTH);
        evaluation.setParameterList(Collections.singletonList(parameter));

        dsa.setEvaluationList(Collections.singletonList(evaluation));

        algorithmList.add(dsa);

        // Add DigestAlgorithm definition
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertTrue(cryptographicSuite.getAcceptableSignatureAlgorithms().isEmpty(),
                "Malformed parameter should result in exclusion.");
    }

    @Test
    void getAcceptableSignatureAlgorithmsWithMinKeySizesTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.DSA, Arrays.asList(
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH))),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)))
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA224, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA256, Collections.emptyList()));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSASSA_PSS, Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> expected = new EnumMap<>(SignatureAlgorithm.class);

        expected.put(SignatureAlgorithm.ECDSA_SHA224, createEvaluations(Collections.singletonList(
                new EvaluationDTO("2029-01-01"))
        ));
        expected.put(SignatureAlgorithm.ECDSA_SHA256, createEvaluations(Collections.singletonList(
                new EvaluationDTO(null))
        ));
        expected.put(SignatureAlgorithm.RSA_SHA224, createEvaluations(Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));
        
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // Add DigestAlgorithm definition
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA512, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        expected.put(SignatureAlgorithm.DSA_SHA512, createEvaluations(Arrays.asList(
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH))
                ),
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH))
        ))));
        expected.put(SignatureAlgorithm.RSA_SSA_PSS_SHA512_MGF1, createEvaluations(Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));
        
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());
    }

    @Test
    void getAcceptableSignatureAlgorithmsWithExpirationDatesTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.DSA, Arrays.asList(
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH))),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)))
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA224, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));
        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA256, Collections.emptyList()));
        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));
        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSASSA_PSS, Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> expected = new EnumMap<>(SignatureAlgorithm.class);

        expected.put(SignatureAlgorithm.RSA_SHA224, createEvaluations(Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));
        expected.put(SignatureAlgorithm.ECDSA_SHA224, createEvaluations(Collections.singletonList(
                new EvaluationDTO("2029-01-01")
        )));
        expected.put(SignatureAlgorithm.ECDSA_SHA256, createEvaluations(Collections.singletonList(
                new EvaluationDTO(null)
        )));

        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // Add DigestAlgorithm definition
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA512, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        
        expected.put(SignatureAlgorithm.DSA_SHA512, createEvaluations(Arrays.asList(
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH))),
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)))
        )));
        expected.put(SignatureAlgorithm.RSA_SSA_PSS_SHA512_MGF1, createEvaluations(Arrays.asList(
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());
    }

    @Test
    void getAcceptableSignatureAlgorithmsWithStartDatesTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.ECDSA, Arrays.asList(
                new EvaluationDTO("2010-01-01", "2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH)), null),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)))
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.DSA_SHA224, Collections.singletonList(
                new EvaluationDTO("2000-01-01", "2029-01-01", Collections.emptyList(), null)
        )));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA1, Collections.singletonList(
                new EvaluationDTO("2000-01-01", "2012-08-01", Collections.emptyList(), null)
        )));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224, Collections.singletonList(
                new EvaluationDTO("2000-01-01", "2029-01-01", Collections.emptyList(), null)
        )));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO(null, null, Collections.emptyList(), null)
        )));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA512, Collections.singletonList(
                new EvaluationDTO("2000-01-01", null, Collections.emptyList(), null)
        )));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA3_256, Collections.singletonList(
                new EvaluationDTO("2020-01-01", null, Collections.emptyList(), null)
        )));
        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.RIPEMD160, Collections.singletonList(
                new EvaluationDTO("2005-01-01", "2008-01-01", Collections.emptyList(), null)
        )));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> expected = new EnumMap<>(SignatureAlgorithm.class);

        expected.put(SignatureAlgorithm.DSA_SHA224, createEvaluations(Collections.singletonList(
                new EvaluationDTO("2000-01-01", "2029-01-01", Collections.emptyList(), null)
        )));
        expected.put(SignatureAlgorithm.ECDSA_SHA1, createEvaluations(Arrays.asList(
                new EvaluationDTO("2010-01-01", "2012-08-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH)), null),
                new EvaluationDTO("2000-01-01", "2012-08-01", Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)), null)
        )));
        expected.put(SignatureAlgorithm.ECDSA_SHA224, createEvaluations(Arrays.asList(
                new EvaluationDTO("2010-01-01", "2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH)), null),
                new EvaluationDTO("2000-01-01", "2029-01-01", Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)), null)
        )));
        expected.put(SignatureAlgorithm.ECDSA_SHA256, createEvaluations(Arrays.asList(
                new EvaluationDTO("2010-01-01", "2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH)), null),
                new EvaluationDTO(null, null, Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)), null)
        )));
        expected.put(SignatureAlgorithm.ECDSA_SHA512, createEvaluations(Arrays.asList(
                new EvaluationDTO("2010-01-01", "2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH)), null),
                new EvaluationDTO("2000-01-01", null, Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)), null)
        )));
        expected.put(SignatureAlgorithm.ECDSA_SHA3_256, createEvaluations(Arrays.asList(
                new EvaluationDTO("2020-01-01", "2029-01-01", Arrays.asList(
                        new ParameterDTO(1900, PLENGTH),
                        new ParameterDTO(200, QLENGTH)), null),
                new EvaluationDTO("2020-01-01", null, Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)), null)
        )));
        expected.put(SignatureAlgorithm.ECDSA_RIPEMD160, createEvaluations(Collections.singletonList(
                new EvaluationDTO("2005-01-01", "2008-01-01", Arrays.asList(
                        new ParameterDTO(3000, PLENGTH),
                        new ParameterDTO(250, QLENGTH)), null)
        )));

        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());
    }

    @Test
    void dss3655RsaTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSA, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> expected = new EnumMap<>(SignatureAlgorithm.class);

        expected.put(SignatureAlgorithm.RSA_SHA224, createEvaluations(Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // Opposite order test

        algorithmList = new ArrayList<>();

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSA, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // add Digest Algo test

        algorithmList = new ArrayList<>();

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSA, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO(null, Collections.emptyList())
        )));

        expected.put(SignatureAlgorithm.RSA_SHA256, createEvaluations(Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // opposite test

        algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO(null, Collections.emptyList())
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSA, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // DigestAlgo expiration after test

        algorithmList = new ArrayList<>();

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSA, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));

        expected.put(SignatureAlgorithm.RSA_SHA256, createEvaluations(Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // opposite test

        algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO("2029-01-01", Collections.emptyList())
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSA, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // DigestAlgo expiration before

        algorithmList = new ArrayList<>();

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSA, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO("2019-01-01", Collections.emptyList())
        )));

        expected.put(SignatureAlgorithm.RSA_SHA256, createEvaluations(Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-01-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-01-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2019-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO("2019-01-01", Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // opposite test

        algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO("2019-01-01", Collections.emptyList())
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.RSA_SHA224, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(1900, MODULES_LENGTH))),
                new EvaluationDTO("2029-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.RSA, Arrays.asList(
                new EvaluationDTO("2010-08-01", Collections.singletonList(new ParameterDTO(786, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1024, MODULES_LENGTH))),
                new EvaluationDTO("2019-10-01", Collections.singletonList(new ParameterDTO(1536, MODULES_LENGTH))),
                new EvaluationDTO("2025-01-01", Collections.singletonList(new ParameterDTO(3000, MODULES_LENGTH))),
                new EvaluationDTO(null, Collections.singletonList(new ParameterDTO(4096, MODULES_LENGTH)))
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());
    }

    @Test
    void dss3655EcdsaTest() {
        List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA224, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(224, PLENGTH),
                        new ParameterDTO(224, QLENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.ECDSA, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2021-10-01", Arrays.asList(
                        new ParameterDTO(256, PLENGTH),
                        new ParameterDTO(256, QLENGTH))),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(384, PLENGTH),
                        new ParameterDTO(384, QLENGTH)))
        )));

        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        Map<SignatureAlgorithm, Set<CryptographicSuiteEvaluation>> expected = new EnumMap<>(SignatureAlgorithm.class);

        expected.put(SignatureAlgorithm.ECDSA_SHA224, createEvaluations(Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(224, PLENGTH),
                        new ParameterDTO(224, QLENGTH)))
        )));

        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // Opposite order test

        algorithmList = new ArrayList<>();

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.ECDSA, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2021-10-01", Arrays.asList(
                        new ParameterDTO(256, PLENGTH),
                        new ParameterDTO(256, QLENGTH))),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(384, PLENGTH),
                        new ParameterDTO(384, QLENGTH)))
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA224, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(224, PLENGTH),
                        new ParameterDTO(224, QLENGTH)))
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // add DigestAlgo

        algorithmList = new ArrayList<>();

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.ECDSA, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2021-10-01", Arrays.asList(
                        new ParameterDTO(256, PLENGTH),
                        new ParameterDTO(256, QLENGTH))),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(384, PLENGTH),
                        new ParameterDTO(384, QLENGTH)))
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA224, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(224, PLENGTH),
                        new ParameterDTO(224, QLENGTH)))
        )));

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO(null, Collections.emptyList())
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        expected.put(SignatureAlgorithm.ECDSA_SHA256, createEvaluations(Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2021-10-01", Arrays.asList(
                        new ParameterDTO(256, PLENGTH),
                        new ParameterDTO(256, QLENGTH))),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(384, PLENGTH),
                        new ParameterDTO(384, QLENGTH)))
        )));

        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // opposite test

        algorithmList = new ArrayList<>();

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA256, Collections.singletonList(
                new EvaluationDTO(null, Collections.emptyList())
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.ECDSA, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2021-10-01", Arrays.asList(
                        new ParameterDTO(256, PLENGTH),
                        new ParameterDTO(256, QLENGTH))),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(384, PLENGTH),
                        new ParameterDTO(384, QLENGTH)))
        )));

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA224, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(224, PLENGTH),
                        new ParameterDTO(224, QLENGTH)))
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());

        // same DigestAlgo

        algorithmList = new ArrayList<>();

        algorithmList.add(createSignatureAlgorithmDefinition(SignatureAlgorithm.ECDSA_SHA224, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(224, PLENGTH),
                        new ParameterDTO(224, QLENGTH)))
        )));

        algorithmList.add(createEncryptionAlgorithmDefinition(EncryptionAlgorithm.ECDSA, Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2021-10-01", Arrays.asList(
                        new ParameterDTO(256, PLENGTH),
                        new ParameterDTO(256, QLENGTH))),
                new EvaluationDTO(null, Arrays.asList(
                        new ParameterDTO(384, PLENGTH),
                        new ParameterDTO(384, QLENGTH)))
        )));

        algorithmList.add(createDigestAlgorithmDefinition(DigestAlgorithm.SHA224, Collections.singletonList(
                new EvaluationDTO(null, Collections.emptyList())
        )));

        cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), algorithmList);

        expected = new EnumMap<>(SignatureAlgorithm.class);

        expected.put(SignatureAlgorithm.ECDSA_SHA224, createEvaluations(Arrays.asList(
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(160, PLENGTH),
                        new ParameterDTO(160, QLENGTH))),
                new EvaluationDTO("2012-08-01", Arrays.asList(
                        new ParameterDTO(163, PLENGTH),
                        new ParameterDTO(163, QLENGTH))),
                new EvaluationDTO("2029-01-01", Arrays.asList(
                        new ParameterDTO(224, PLENGTH),
                        new ParameterDTO(224, QLENGTH)))
        )));

        // SignatureAlgorithm definition takes priority
        assertEquals(expected, cryptographicSuite.getAcceptableSignatureAlgorithms());
    }

    private CryptographicSuiteAlgorithm createDigestAlgorithmDefinition(DigestAlgorithm digestAlgorithm, List<EvaluationDTO> evaluationList) {
        CryptographicSuiteAlgorithm algorithm = new CryptographicSuiteAlgorithm();

        algorithm.setAlgorithmIdentifierName(digestAlgorithm.getName());
        if (digestAlgorithm.getOid() != null) {
            algorithm.setAlgorithmIdentifierOIDs(Collections.singletonList(digestAlgorithm.getOid()));
        }
        if (digestAlgorithm.getUri() != null) {
            algorithm.setAlgorithmIdentifierURIs(Collections.singletonList(digestAlgorithm.getUri()));
        }

        algorithm.setEvaluationList(new ArrayList<>(createEvaluations(evaluationList)));

        return algorithm;
    }

    private CryptographicSuiteAlgorithm createEncryptionAlgorithmDefinition(EncryptionAlgorithm encryptionAlgorithm, List<EvaluationDTO> evaluationList) {
        CryptographicSuiteAlgorithm algorithm = new CryptographicSuiteAlgorithm();

        algorithm.setAlgorithmIdentifierName(encryptionAlgorithm.getName());
        if (encryptionAlgorithm.getOid() != null) {
            algorithm.setAlgorithmIdentifierOIDs(Collections.singletonList(encryptionAlgorithm.getOid()));
        }

        algorithm.setEvaluationList(new ArrayList<>(createEvaluations(evaluationList)));

        return algorithm;
    }

    private CryptographicSuiteAlgorithm createSignatureAlgorithmDefinition(SignatureAlgorithm signatureAlgorithm, List<EvaluationDTO> evaluationList) {
        CryptographicSuiteAlgorithm algorithm = new CryptographicSuiteAlgorithm();

        algorithm.setAlgorithmIdentifierName(signatureAlgorithm.getName());
        if (signatureAlgorithm.getOid() != null) {
            algorithm.setAlgorithmIdentifierOIDs(Collections.singletonList(signatureAlgorithm.getOid()));
        }
        if (signatureAlgorithm.getUri() != null) {
            algorithm.setAlgorithmIdentifierURIs(Collections.singletonList(signatureAlgorithm.getUri()));
        }

        algorithm.setEvaluationList(new ArrayList<>(createEvaluations(evaluationList)));

        return algorithm;
    }

    private Set<CryptographicSuiteEvaluation> createEvaluations(List<EvaluationDTO> evaluationList) {
        Set<CryptographicSuiteEvaluation> result = new HashSet<>();
        if (evaluationList != null && !evaluationList.isEmpty()) {
            for (EvaluationDTO evaluationDTO : evaluationList) {
                CryptographicSuiteEvaluation evaluation = new CryptographicSuiteEvaluation();
                evaluation.setValidityStart(toDate(evaluationDTO.validityStart));
                evaluation.setValidityEnd(toDate(evaluationDTO.validityEnd));

                if (evaluationDTO.parameterList != null && !evaluationDTO.parameterList.isEmpty()) {
                    List<CryptographicSuiteParameter> parameters = new ArrayList<>();
                    for (ParameterDTO parameterDTO : evaluationDTO.parameterList) {
                        CryptographicSuiteParameter parameter = new CryptographicSuiteParameter();
                        parameter.setMin(parameterDTO.minKeyLength);
                        parameter.setName(parameterDTO.parameterName);
                        parameters.add(parameter);
                    }
                    evaluation.setParameterList(parameters);
                }
                if (evaluationDTO.usages != null && !evaluationDTO.usages.isEmpty()) {
                    evaluation.setAlgorithmUsage(evaluationDTO.usages);
                }

                result.add(evaluation);
            }

        } else {
            CryptographicSuiteEvaluation evaluationType = new CryptographicSuiteEvaluation();
            result.add(evaluationType);
        }
        return result;
    }

    private Date toDate(final String str) {
        if (str == null) {
            return null;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            return sdf.parse(str);
        } catch (Exception e) {
            fail(e);
            return null;
        }
    }

    @Test
    void levelsTest() {
        CryptographicSuite19322 cryptographicSuite = new CryptographicSuite19322(new CryptographicSuiteMetadata(), Collections.emptyList());

        assertEquals(Level.FAIL, cryptographicSuite.getLevel()); // default
        // inherited from default
        assertEquals(Level.FAIL, cryptographicSuite.getAcceptableDigestAlgorithmsLevel());
        assertEquals(Level.FAIL, cryptographicSuite.getAcceptableSignatureAlgorithmsLevel());
        assertEquals(Level.FAIL, cryptographicSuite.getAcceptableSignatureAlgorithmsMiniKeySizeLevel());
        assertEquals(Level.FAIL, cryptographicSuite.getAlgorithmsExpirationDateLevel());
        assertEquals(Level.WARN, cryptographicSuite.getAlgorithmsExpirationDateAfterUpdateLevel()); // default

        cryptographicSuite.setLevel(Level.IGNORE);
        assertEquals(Level.IGNORE, cryptographicSuite.getLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAcceptableDigestAlgorithmsLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAcceptableSignatureAlgorithmsLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAcceptableSignatureAlgorithmsMiniKeySizeLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAlgorithmsExpirationDateLevel());
        assertEquals(Level.WARN, cryptographicSuite.getAlgorithmsExpirationDateAfterUpdateLevel());

        cryptographicSuite.setAlgorithmsExpirationTimeAfterPolicyUpdateLevel(Level.INFORM);
        assertEquals(Level.IGNORE, cryptographicSuite.getLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAcceptableDigestAlgorithmsLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAcceptableSignatureAlgorithmsLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAcceptableSignatureAlgorithmsMiniKeySizeLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAlgorithmsExpirationDateLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAlgorithmsExpirationDateAfterUpdateLevel());

        cryptographicSuite.setAcceptableDigestAlgorithmsLevel(Level.INFORM);
        assertEquals(Level.IGNORE, cryptographicSuite.getLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAcceptableDigestAlgorithmsLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAcceptableSignatureAlgorithmsLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAcceptableSignatureAlgorithmsMiniKeySizeLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAlgorithmsExpirationDateLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAlgorithmsExpirationDateAfterUpdateLevel());

        cryptographicSuite.setAcceptableSignatureAlgorithmsLevel(Level.INFORM);
        assertEquals(Level.IGNORE, cryptographicSuite.getLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAcceptableDigestAlgorithmsLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAcceptableSignatureAlgorithmsLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAcceptableSignatureAlgorithmsMiniKeySizeLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAlgorithmsExpirationDateLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAlgorithmsExpirationDateAfterUpdateLevel());

        cryptographicSuite.setAcceptableSignatureAlgorithmsMiniKeySizeLevel(Level.INFORM);
        assertEquals(Level.IGNORE, cryptographicSuite.getLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAcceptableDigestAlgorithmsLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAcceptableSignatureAlgorithmsLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAcceptableSignatureAlgorithmsMiniKeySizeLevel());
        assertEquals(Level.IGNORE, cryptographicSuite.getAlgorithmsExpirationDateLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAlgorithmsExpirationDateAfterUpdateLevel());

        cryptographicSuite.setAlgorithmsExpirationDateLevel(Level.INFORM);
        assertEquals(Level.IGNORE, cryptographicSuite.getLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAcceptableDigestAlgorithmsLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAcceptableSignatureAlgorithmsLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAcceptableSignatureAlgorithmsMiniKeySizeLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAlgorithmsExpirationDateLevel());
        assertEquals(Level.INFORM, cryptographicSuite.getAlgorithmsExpirationDateAfterUpdateLevel());
    }

    private static class EvaluationDTO {

        private final String validityStart;
        private final String validityEnd;
        private final List<ParameterDTO> parameterList;
        private final List<CryptographicSuiteAlgorithmUsage> usages;

        public EvaluationDTO(final String validityEnd) {
            this(validityEnd, null, null);
        }

        public EvaluationDTO(final String validityEnd, final List<ParameterDTO> parameterList) {
            this(validityEnd, parameterList, null);
        }

        public EvaluationDTO(final String validityEnd, final List<ParameterDTO> parameterList, final List<CryptographicSuiteAlgorithmUsage> usages) {
            this(null, validityEnd, parameterList, usages);
        }

        public EvaluationDTO(final String validityStart, final String validityEnd, final List<ParameterDTO> parameterList,
                             final List<CryptographicSuiteAlgorithmUsage> usages) {
            this.validityStart = validityStart;
            this.validityEnd = validityEnd;
            this.parameterList = parameterList;
            this.usages = usages;
        }

    }

    private static class ParameterDTO {

        private final Integer minKeyLength;
        private final String parameterName;

        public ParameterDTO(final Integer minKeyLength, final String parameterName) {
            this.minKeyLength = minKeyLength;
            this.parameterName = parameterName;
        }

    }

}
