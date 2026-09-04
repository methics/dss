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

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.policy.SignatureAlgorithmWithMinKeySize;
import eu.europa.esig.dss.policy.CryptographicConstraintWrapper;
import eu.europa.esig.dss.policy.jaxb.Algo;
import eu.europa.esig.dss.policy.jaxb.AlgoExpirationDate;
import eu.europa.esig.dss.policy.jaxb.CryptographicConstraint;
import eu.europa.esig.dss.policy.jaxb.ListAlgo;
import eu.europa.esig.dss.validation.policy.CryptographicSuiteUtils;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.TimeZone;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class CryptographicSuiteUtilsTest {

    @Test
    void isSignatureAlgorithmReliableTest() {
        CryptographicConstraint cryptographicConstraint = new CryptographicConstraint();

        ListAlgo encryptionListAlgo = new ListAlgo();
        encryptionListAlgo.getAlgos().add(createAlgo(EncryptionAlgorithm.RSA));
        cryptographicConstraint.setAcceptableEncryptionAlgo(encryptionListAlgo);

        CryptographicConstraintWrapper wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmReliable(wrapper, SignatureAlgorithm.RSA_SHA256));
        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmReliable(wrapper, SignatureAlgorithm.DSA_SHA256));

        ListAlgo digestListAlgo = new ListAlgo();
        digestListAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA256));
        cryptographicConstraint.setAcceptableDigestAlgo(digestListAlgo);

        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        assertTrue(CryptographicSuiteUtils.isSignatureAlgorithmReliable(wrapper, SignatureAlgorithm.RSA_SHA256));
        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmReliable(wrapper, SignatureAlgorithm.DSA_SHA256));

        // wrong definition
        cryptographicConstraint.setAcceptableEncryptionAlgo(digestListAlgo);
        cryptographicConstraint.setAcceptableDigestAlgo(encryptionListAlgo);

        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmReliable(wrapper, SignatureAlgorithm.RSA_SHA256));
        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmReliable(wrapper, SignatureAlgorithm.DSA_SHA256));
    }

    @Test
    void isDigestAlgorithmReliableTest() {
        CryptographicConstraint cryptographicConstraint = new CryptographicConstraint();

        ListAlgo listAlgo = new ListAlgo();
        listAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA256));
        cryptographicConstraint.setAcceptableDigestAlgo(listAlgo);

        CryptographicConstraintWrapper wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        assertTrue(CryptographicSuiteUtils.isDigestAlgorithmReliable(wrapper, DigestAlgorithm.SHA256));
        assertFalse(CryptographicSuiteUtils.isDigestAlgorithmReliable(wrapper, DigestAlgorithm.SHA1));

        cryptographicConstraint.setAcceptableDigestAlgo(null);
        cryptographicConstraint.setAcceptableEncryptionAlgo(listAlgo);

        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        assertFalse(CryptographicSuiteUtils.isDigestAlgorithmReliable(wrapper, DigestAlgorithm.SHA256));
        assertFalse(CryptographicSuiteUtils.isDigestAlgorithmReliable(wrapper, DigestAlgorithm.SHA1));
    }

    @Test
    void isSignatureAlgorithmWithKeySizeReliableTest() {
        CryptographicConstraint cryptographicConstraint = new CryptographicConstraint();

        ListAlgo listAlgo = new ListAlgo();
        listAlgo.getAlgos().add(createAlgo(EncryptionAlgorithm.RSA, 3000));
        cryptographicConstraint.setAcceptableEncryptionAlgo(listAlgo);
        cryptographicConstraint.setMiniPublicKeySize(listAlgo);

        ListAlgo digestListAlgo = new ListAlgo();
        digestListAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA256));
        cryptographicConstraint.setAcceptableDigestAlgo(digestListAlgo);

        CryptographicConstraintWrapper wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        assertTrue(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.RSA_SHA256, 3072));
        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.RSA_SHA256, 2048));

        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.DSA_SHA256, 3072));
        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.DSA_SHA256, 2048));

        listAlgo = new ListAlgo();
        listAlgo.getAlgos().add(createAlgo(EncryptionAlgorithm.DSA));
        cryptographicConstraint.setAcceptableEncryptionAlgo(listAlgo);
        cryptographicConstraint.setMiniPublicKeySize(listAlgo);

        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        // no key size -> reliable
        assertTrue(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.DSA_SHA256, 3072));
        assertTrue(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.DSA_SHA256, 2048));

        listAlgo = new ListAlgo();
        listAlgo.getAlgos().add(createAlgo(EncryptionAlgorithm.DSA, 2000));
        cryptographicConstraint.setAcceptableEncryptionAlgo(listAlgo);
        cryptographicConstraint.setMiniPublicKeySize(listAlgo);

        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        assertTrue(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.DSA_SHA256, 3072));
        assertTrue(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.DSA_SHA256, 2048));

        listAlgo = new ListAlgo();
        listAlgo.getAlgos().add(createAlgo(EncryptionAlgorithm.DSA, 4000));
        cryptographicConstraint.setAcceptableEncryptionAlgo(listAlgo);
        cryptographicConstraint.setMiniPublicKeySize(listAlgo);

        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.DSA_SHA256, 3072));
        assertFalse(CryptographicSuiteUtils.isSignatureAlgorithmWithKeySizeReliable(wrapper, SignatureAlgorithm.DSA_SHA256, 2048));
    }

    @Test
    void getReliableDigestAlgorithmsAtTimeTest() {
        CryptographicConstraint cryptographicConstraint = new CryptographicConstraint();

        ListAlgo listAlgo = new ListAlgo();
        listAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA256));
        cryptographicConstraint.setAcceptableDigestAlgo(listAlgo);

        CryptographicConstraintWrapper wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        Calendar oldDateCalendar = Calendar.getInstance();
        oldDateCalendar.set(2010, Calendar.JANUARY, 1);

        Calendar newDateCalendar = Calendar.getInstance();
        newDateCalendar.set(2025, Calendar.JANUARY, 1);

        assertEquals(Collections.singleton(DigestAlgorithm.SHA256), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(Collections.singleton(DigestAlgorithm.SHA256), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, newDateCalendar.getTime()));

        listAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA512));
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        assertEquals(new HashSet<>(Arrays.asList(DigestAlgorithm.SHA256, DigestAlgorithm.SHA512)), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(new HashSet<>(Arrays.asList(DigestAlgorithm.SHA256, DigestAlgorithm.SHA512)), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, newDateCalendar.getTime()));

        AlgoExpirationDate algoExpirationDate = new AlgoExpirationDate();
        algoExpirationDate.setLevel(Level.FAIL);
        algoExpirationDate.setFormat("yyyy");
        Algo algo = new Algo();
        algo.setValue("SHA256");
        algoExpirationDate.getAlgos().add(algo);
        cryptographicConstraint.setAlgoExpirationDate(algoExpirationDate);
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        // no expiration date
        assertEquals(new HashSet<>(Arrays.asList(DigestAlgorithm.SHA256, DigestAlgorithm.SHA512)), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(new HashSet<>(Arrays.asList(DigestAlgorithm.SHA256, DigestAlgorithm.SHA512)), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, newDateCalendar.getTime()));

        algo.setDate("2029");
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        // expiration in the future
        assertEquals(new HashSet<>(Arrays.asList(DigestAlgorithm.SHA256, DigestAlgorithm.SHA512)), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(new HashSet<>(Arrays.asList(DigestAlgorithm.SHA256, DigestAlgorithm.SHA512)), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, newDateCalendar.getTime()));

        algo.setDate("2020");
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        // expiration happened
        assertEquals(new HashSet<>(Arrays.asList(DigestAlgorithm.SHA256, DigestAlgorithm.SHA512)), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(Collections.singleton(DigestAlgorithm.SHA512), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, newDateCalendar.getTime()));

        algo.setDate("2005");
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        // old expiration
        assertEquals(Collections.singleton(DigestAlgorithm.SHA512), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(Collections.singleton(DigestAlgorithm.SHA512), CryptographicSuiteUtils.getReliableDigestAlgorithmsAtTime(wrapper, newDateCalendar.getTime()));
    }

    @Test
    void getReliableEncryptionAlgorithmsWithMinimalKeyLengthAtTimeTest() {
        CryptographicConstraint cryptographicConstraint = new CryptographicConstraint();

        ListAlgo listAlgo = new ListAlgo();
        listAlgo.getAlgos().add(createAlgo(EncryptionAlgorithm.RSA));
        cryptographicConstraint.setAcceptableEncryptionAlgo(listAlgo);

        ListAlgo digestListAlgo = new ListAlgo();
        digestListAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA256));
        cryptographicConstraint.setAcceptableDigestAlgo(digestListAlgo);

        CryptographicConstraintWrapper wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        Calendar oldDateCalendar = Calendar.getInstance();
        oldDateCalendar.set(2010, Calendar.JANUARY, 1);

        Calendar newDateCalendar = Calendar.getInstance();
        newDateCalendar.set(2025, Calendar.JANUARY, 1);

        Set<SignatureAlgorithmWithMinKeySize> expected = new HashSet<>();
        expected.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.RSA_SHA256, 0));

        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));

        listAlgo.getAlgos().add(createAlgo(EncryptionAlgorithm.ECDSA));
        expected.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.ECDSA_SHA256, 0));
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));

        AlgoExpirationDate algoExpirationDate = new AlgoExpirationDate();
        algoExpirationDate.setLevel(Level.FAIL);
        algoExpirationDate.setFormat("yyyy");
        Algo algo = new Algo();
        algo.setValue("RSA");
        algo.setSize(1024);
        algoExpirationDate.getAlgos().add(algo);
        cryptographicConstraint.setAlgoExpirationDate(algoExpirationDate);

        expected.clear();
        expected.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.RSA_SHA256, 1024));
        expected.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.ECDSA_SHA256, 0));
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));

        ListAlgo minKeySize = new ListAlgo();
        minKeySize.setLevel(Level.FAIL);
        minKeySize.getAlgos().add(createAlgo(EncryptionAlgorithm.RSA, 1024));
        cryptographicConstraint.setMiniPublicKeySize(minKeySize);
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));

        algo.setDate("2029");
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));

        Set<SignatureAlgorithmWithMinKeySize> ecdsaOnlyList = new HashSet<>();
        ecdsaOnlyList.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.ECDSA_SHA256, 0));

        algo.setDate("2020");
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        assertEquals(expected, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(ecdsaOnlyList, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));

        algo.setDate("2005");
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        assertEquals(ecdsaOnlyList, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(ecdsaOnlyList, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));

        Algo biggerAlgo = new Algo();
        biggerAlgo.setValue("RSA");
        biggerAlgo.setSize(1900);
        biggerAlgo.setDate("2020");
        algoExpirationDate.getAlgos().add(biggerAlgo);

        Set<SignatureAlgorithmWithMinKeySize> rsa1900List = new HashSet<>();
        rsa1900List.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.RSA_SHA256, 1900));
        rsa1900List.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.ECDSA_SHA256, 0));
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        assertEquals(rsa1900List, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(ecdsaOnlyList, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));

        biggerAlgo = new Algo();
        biggerAlgo.setValue("RSA");
        biggerAlgo.setSize(3000);
        biggerAlgo.setDate("2029");
        algoExpirationDate.getAlgos().add(biggerAlgo);

        Set<SignatureAlgorithmWithMinKeySize> rsa3000List = new HashSet<>();
        rsa3000List.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.RSA_SHA256, 3000));
        rsa3000List.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.ECDSA_SHA256, 0));
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        assertEquals(rsa1900List, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(rsa3000List, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));

        minKeySize.getAlgos().clear();
        minKeySize.getAlgos().add(createAlgo(EncryptionAlgorithm.RSA, 4000));

        Set<SignatureAlgorithmWithMinKeySize> rsa4000List = new HashSet<>();
        rsa4000List.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.RSA_SHA256, 4000));
        rsa4000List.add(new SignatureAlgorithmWithMinKeySize(SignatureAlgorithm.ECDSA_SHA256, 0));
        wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);

        assertEquals(rsa4000List, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, oldDateCalendar.getTime()));
        assertEquals(rsa4000List, CryptographicSuiteUtils.getReliableSignatureAlgorithmsWithMinimalKeyLengthAtTime(wrapper, newDateCalendar.getTime()));
    }

    @Test
    void getExpirationDateEncryptionAlgoTest() {
        CryptographicConstraint cryptographicConstraint = new CryptographicConstraint();

        AlgoExpirationDate listAlgo = new AlgoExpirationDate();
        listAlgo.setFormat("yyyy");
        listAlgo.getAlgos().add(createAlgo(EncryptionAlgorithm.RSA, 1900, "2022"));
        listAlgo.getAlgos().add(createAlgo(EncryptionAlgorithm.RSA, 3000, "2025"));
        cryptographicConstraint.setAcceptableEncryptionAlgo(listAlgo);
        cryptographicConstraint.setAlgoExpirationDate(listAlgo);

        ListAlgo digestListAlgo = new ListAlgo();
        digestListAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA256));
        cryptographicConstraint.setAcceptableDigestAlgo(digestListAlgo);

        CryptographicConstraintWrapper wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        assertNull(CryptographicSuiteUtils.getExpirationDate(wrapper, SignatureAlgorithm.RSA_SHA256, 1024));
        assertEquals(getDate("2022", simpleDateFormat), CryptographicSuiteUtils.getExpirationDate(wrapper, SignatureAlgorithm.RSA_SHA256, 2048));
        assertEquals(getDate("2025", simpleDateFormat), CryptographicSuiteUtils.getExpirationDate(wrapper, SignatureAlgorithm.RSA_SHA256, 3072));
        assertEquals(getDate("2025", simpleDateFormat), CryptographicSuiteUtils.getExpirationDate(wrapper, SignatureAlgorithm.RSA_SHA256, 4096));
        assertNull(CryptographicSuiteUtils.getExpirationDate(wrapper, SignatureAlgorithm.DSA_SHA256, 1024));
        assertNull(CryptographicSuiteUtils.getExpirationDate(wrapper, SignatureAlgorithm.DSA_SHA256, 2048));
        assertNull(CryptographicSuiteUtils.getExpirationDate(wrapper, SignatureAlgorithm.DSA_SHA256, 3072));
        assertNull(CryptographicSuiteUtils.getExpirationDate(wrapper, SignatureAlgorithm.DSA_SHA256, 4096));
    }

    @Test
    void getExpirationDateDigestAlgoTest() {
        CryptographicConstraint cryptographicConstraint = new CryptographicConstraint();

        AlgoExpirationDate listAlgo = new AlgoExpirationDate();
        listAlgo.setFormat("yyyy");
        listAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA1, "2022"));
        listAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA256, "2025"));
        listAlgo.getAlgos().add(createAlgo(DigestAlgorithm.SHA512, "2028"));
        cryptographicConstraint.setAcceptableDigestAlgo(listAlgo);
        cryptographicConstraint.setAlgoExpirationDate(listAlgo);

        CryptographicConstraintWrapper wrapper = new CryptographicConstraintWrapper(cryptographicConstraint);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        simpleDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));

        assertNull(CryptographicSuiteUtils.getExpirationDate(wrapper, DigestAlgorithm.MD5));
        assertEquals(getDate("2022", simpleDateFormat), CryptographicSuiteUtils.getExpirationDate(wrapper, DigestAlgorithm.SHA1));
        assertEquals(getDate("2025", simpleDateFormat), CryptographicSuiteUtils.getExpirationDate(wrapper, DigestAlgorithm.SHA256));
        assertEquals(getDate("2028", simpleDateFormat), CryptographicSuiteUtils.getExpirationDate(wrapper, DigestAlgorithm.SHA512));
        assertNull(CryptographicSuiteUtils.getExpirationDate(wrapper, DigestAlgorithm.SHA224));
    }

    private Algo createAlgo(EncryptionAlgorithm encryptionAlgorithm) {
        return createAlgo(encryptionAlgorithm, null);
    }

    private Algo createAlgo(EncryptionAlgorithm encryptionAlgorithm, Integer length) {
        return createAlgo(encryptionAlgorithm, length, null);
    }

    private Algo createAlgo(EncryptionAlgorithm encryptionAlgorithm, Integer length, String expirationDate) {
        Algo algo = new Algo();
        algo.setValue(encryptionAlgorithm.getName());
        algo.setSize(length);
        algo.setDate(expirationDate);
        return algo;
    }

    private Algo createAlgo(DigestAlgorithm digestAlgorithm) {
        return createAlgo(digestAlgorithm, null);
    }

    private Algo createAlgo(DigestAlgorithm digestAlgorithm, String expirationDate) {
        Algo algo = new Algo();
        algo.setValue(digestAlgorithm.getName());
        algo.setDate(expirationDate);
        return algo;
    }

    private Date getDate(String dateString, SimpleDateFormat format) {
        if (dateString != null) {
            try {
                return format.parse(dateString);
            } catch (ParseException e) {
                fail(e);
            }
        }
        return null;
    }

}
