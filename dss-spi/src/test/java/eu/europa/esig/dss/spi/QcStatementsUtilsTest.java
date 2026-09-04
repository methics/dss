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
package eu.europa.esig.dss.spi;

import eu.europa.esig.dss.enumerations.QCIdentMethodEnum;
import eu.europa.esig.dss.enumerations.QCType;
import eu.europa.esig.dss.enumerations.QCTypeEnum;
import eu.europa.esig.dss.enumerations.SemanticsIdentifier;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.extension.PSD2QcType;
import eu.europa.esig.dss.model.x509.extension.QCLimitValue;
import eu.europa.esig.dss.model.x509.extension.QCPSB;
import eu.europa.esig.dss.model.x509.extension.QcStatements;
import eu.europa.esig.dss.model.x509.extension.RoleOfPSP;
import eu.europa.esig.dss.utils.Utils;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.qualified.ETSIQCObjectIdentifiers;
import org.bouncycastle.asn1.x509.qualified.QCStatement;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class QcStatementsUtilsTest {

    @Test
    void cert1() {
        CertificateToken cert = DSSUtils.loadCertificateFromBase64EncodedString(
                "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");
        assertNotNull(cert);

        QcStatements qcStatements = QcStatementUtils.getQcStatements(cert);
        assertNotNull(qcStatements);
        assertTrue(qcStatements.isQcCompliance());
        assertFalse(qcStatements.isQcQSCD());
        assertNull(qcStatements.getQcSemanticsIdentifier());
        assertNull(qcStatements.getQcLimitValue());
        assertNull(qcStatements.getQcEuRetentionPeriod());

        assertEquals(2, qcStatements.getQcEuPDS().size());
        assertEquals("https://www.ica.cz/Zpravy-pro-uzivatele", qcStatements.getQcEuPDS().get(0).getUrl());
        assertEquals("cs", qcStatements.getQcEuPDS().get(0).getLanguage());
        assertEquals("https://www.ica.cz/PDS", qcStatements.getQcEuPDS().get(1).getUrl());
        assertEquals("en", qcStatements.getQcEuPDS().get(1).getLanguage());

        assertEquals(1, qcStatements.getQcTypes().size());
        assertEquals(QCTypeEnum.QCT_WEB, qcStatements.getQcTypes().get(0));
        assertEquals("0.4.0.1862.1.6.3", qcStatements.getQcTypes().get(0).getOid());
        assertEquals("qc-type-web", qcStatements.getQcTypes().get(0).getDescription());

        assertTrue(qcStatements.getQcTypes().contains(QCTypeEnum.QCT_WEB));
        PSD2QcType psd2QcType = qcStatements.getPsd2QcType();
        assertNotNull(psd2QcType);
        assertNotNull(psd2QcType.getNcaId());
        assertNotNull(psd2QcType.getNcaName());
        assertEquals(4, psd2QcType.getRolesOfPSP().size());
    }

    @Test
    void cert2() {
        CertificateToken caTokenA = DSSUtils.loadCertificateFromBase64EncodedString(
                "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");

        QcStatements qcStatements = QcStatementUtils.getQcStatements(caTokenA);
        assertNotNull(qcStatements);
        assertTrue(qcStatements.isQcCompliance());
        assertTrue(qcStatements.isQcQSCD());
        assertNull(qcStatements.getQcLimitValue());
        assertNull(qcStatements.getPsd2QcType());
        assertNull(qcStatements.getQcEuRetentionPeriod());
        assertEquals(1, qcStatements.getQcEuPDS().size());
        assertEquals(1, qcStatements.getQcTypes().size());
        assertTrue(qcStatements.getQcTypes().contains(QCTypeEnum.QCT_ESEAL));

        assertNotNull(qcStatements.getQcSemanticsIdentifier());
        assertEquals(SemanticsIdentifier.qcsSemanticsIdLegal, qcStatements.getQcSemanticsIdentifier());
    }

    @Test
    void certWithLegislation() {
        CertificateToken certificate = DSSUtils.loadCertificate(new File("src/test/resources/john_doe_tc.crt"));

        QcStatements qcStatements = QcStatementUtils.getQcStatements(certificate);
        assertNotNull(qcStatements);
        List<String> qcLegislationCountryCodes = qcStatements.getQcLegislationCountryCodes();
        assertNotNull(qcLegislationCountryCodes);
        assertEquals(1, qcLegislationCountryCodes.size());
        assertTrue(qcLegislationCountryCodes.contains("TC"));
    }

    @Test
    void certWithQCLimitValue() {
        CertificateToken certificateToken = DSSUtils.loadCertificateFromBase64EncodedString("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");
        QcStatements qcStatements = QcStatementUtils.getQcStatements(certificateToken);
        assertNotNull(qcStatements);
        QCLimitValue qcLimitValue = qcStatements.getQcLimitValue();
        assertNotNull(qcLimitValue);
        assertEquals("EUR", qcLimitValue.getCurrency());
        assertEquals(0, qcLimitValue.getAmount());
        assertEquals(0, qcLimitValue.getExponent());
    }

    @Test
    void certWithPSD2QcStatement() {
        CertificateToken cert = DSSUtils.loadCertificateFromBase64EncodedString(
                "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");
        assertNotNull(cert);

        QcStatements qcStatements = QcStatementUtils.getQcStatements(cert);
        assertNotNull(qcStatements);

        PSD2QcType psd2QcType = qcStatements.getPsd2QcType();
        assertNotNull(psd2QcType);

        List<RoleOfPSP> rolesOfPSP = psd2QcType.getRolesOfPSP();
        assertEquals(4, rolesOfPSP.size());
        for (RoleOfPSP roleOfPSP : rolesOfPSP) {
            assertNotNull(roleOfPSP);
            assertNotNull(roleOfPSP.getPspOid());

            assertNotNull(roleOfPSP.getPspOid().getOid());
            assertTrue(roleOfPSP.getPspOid().getOid().contains("0.4.0.19495.1."));

            assertNotNull(roleOfPSP.getPspOid().getDescription());
            assertTrue(roleOfPSP.getPspOid().getDescription().contains("psp-"));

            assertNotNull(roleOfPSP.getPspName());
            assertTrue(roleOfPSP.getPspName().contains("PSP_"));
        }
        assertEquals("Czech National Bank", psd2QcType.getNcaName());
        assertEquals("CZ-CNB", psd2QcType.getNcaId());
    }

    @Test
    void certWithQcQSCDlegislationQcStatement() {
        CertificateToken cert = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIFPzCCBCegAwIBAgIDAYcTMA0GCSqGSIb3DQEBCwUAMH8xIzAhBgNVBAMMGlRlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaMTgwNgYDVQQKDC9UZXN0IFF1YWxpZmllZCBUcnVzdCBTZXJ2aWNlIFByb3ZpZGVyIDEgZnJvbSBaWjERMA8GA1UECwwIUEtJLVRFU1QxCzAJBgNVBAYTAlpaMB4XDTI1MDExNjA4NTgzOVoXDTI2MTExNjA4NTgzOVowcDEUMBIGA1UEAwwLQ2hhcmxpZSBEb2UxODA2BgNVBAoML1Rlc3QgUXVhbGlmaWVkIFRydXN0IFNlcnZpY2UgUHJvdmlkZXIgMSBmcm9tIFpaMREwDwYDVQQLDAhQS0ktVEVTVDELMAkGA1UEBhMCWlowggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC0b3EyFB6ylmIbjBYEVNFp4ABeXKh8/soaT06iLvydYzgdaJOvXj4o0R05wTGbDxrztqhE/cpo9w7cnPeLe+ZcIixeDWv0sXlxK23n+EMrgQcVe8cgTgrCPYEGm5ylrWffVv/gDZey59f+bto3MCIJSkfdvDLXXAftEpftT4fjdIUnAsexgmPcIkM7lzR3ijFpg0Pt4CE1h2I4G2kZtCvm+TR0oeWBXxgZY8wrH/MUp+9cV/VC5bClGdvLTm105h0uNzrLGEeAf0mnSKaMT9es2BqvNZKKs9X7YKja1LEw0wYqSykD6FSQEAkCvUOJCQyM6X9UuhPXx7Dmhia64TALAgMBAAGjggHRMIIBzTAOBgNVHQ8BAf8EBAMCBkAwGgYDVR0gAQH/BBAwDjAMBgorBgEEAZOWLwECMFcGCCsGAQUFBwEDBEswSTAIBgYEAI5GAQEwCAYGBACORgEEMBMGBgQAjkYBBjAJBgcEAI5GAQYBMA4GBgQAjkYBBzAEEwJaWjAOBgYEAI5GAQkwBBMCWlowVAYDVR0fBE0wSzBJoEegRYZDaHR0cDovL2Rzcy5ub3dpbmEubHUvcGtpLWZhY3RvcnkvY3JsL1Rlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaLmNybDCBrwYIKwYBBQUHAQEEgaIwgZ8wTAYIKwYBBQUHMAGGQGh0dHA6Ly9kc3Mubm93aW5hLmx1L3BraS1mYWN0b3J5L29jc3AvVGVzdC1RdWFsaWZpZWQtQ0ExLWZyb20tWlowTwYIKwYBBQUHMAKGQ2h0dHA6Ly9kc3Mubm93aW5hLmx1L3BraS1mYWN0b3J5L2NydC9UZXN0LVF1YWxpZmllZC1DQTEtZnJvbS1aWi5jcnQwHwYDVR0jBBgwFoAUPV89E3wZnRG2b3OlwtjoXWMcbNkwHQYDVR0OBBYEFOIUK0edTcV3Xd+v+K5MTx1toKjFMA0GCSqGSIb3DQEBCwUAA4IBAQCFwN/GYAQs2PaOPhXMrKlR7XwiB0BqinVFElbxGNru2YLMfX/I/rUo6O5UFHVC9hM8Z6+iUbjO1egr851irwtmDPNTztMxH/kp+/k8nBBeipCBcjWQGtuWmtSgoN9hEuiRgsLmsgOONcbqNyUXys2deoZJqJvtxAWW93dgqp8euFmkqw6dy24GtALI1X52E8r5jqTobjCmsMyIrXQzICRvlq8L6t8tYzDuKtpZtkH2JMo+p7oT/Ew5Rr8sc0n7mLdwUHLN0JhvC7zIKW/WdZAVx3BJMBqeGeP52KOcg8DD8rFf7mzhdK+wek9t8KMXg0Eya3rpzypGJM5VD0hJ5VVK");
        assertNotNull(cert);

        QcStatements qcStatements = QcStatementUtils.getQcStatements(cert);
        assertNotNull(qcStatements);

        assertEquals(1, Utils.collectionSize(qcStatements.getQcQSCDLegislationCountryCodes()));
        assertEquals("ZZ", qcStatements.getQcQSCDLegislationCountryCodes().get(0));

        assertTrue(QcStatementUtils.isQcQSCDlegislationPresent(qcStatements, "ZZ"));
        assertFalse(QcStatementUtils.isQcQSCDlegislationPresent(qcStatements, "XX"));
    }

    @Test
    void certWithQcIdentMethodQcStatement() {
        CertificateToken cert = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIFVDCCBDygAwIBAgIDAYcTMA0GCSqGSIb3DQEBCwUAMH8xIzAhBgNVBAMMGlRlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaMTgwNgYDVQQKDC9UZXN0IFF1YWxpZmllZCBUcnVzdCBTZXJ2aWNlIFByb3ZpZGVyIDEgZnJvbSBaWjERMA8GA1UECwwIUEtJLVRFU1QxCzAJBgNVBAYTAlpaMB4XDTI1MDExNjA5MDExM1oXDTI2MTExNjA5MDExM1owcDEUMBIGA1UEAwwLQ2hhcmxpZSBEb2UxODA2BgNVBAoML1Rlc3QgUXVhbGlmaWVkIFRydXN0IFNlcnZpY2UgUHJvdmlkZXIgMSBmcm9tIFpaMREwDwYDVQQLDAhQS0ktVEVTVDELMAkGA1UEBhMCWlowggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQC+1BLF1RnMK4Z8WzFDeB3Y8tUmNroe2Xg3LkrP41HRmuzNt7rOH4wWGqm+MKhSDY+zuKxwO8or5H+SYn9rmTsPSaohO2SQ/n4jIvInCiuSB+pN4cG18pdLTWiVRrSE59V79bY57ILarfyfkkPgFg+HxshkXoYZfr2IlTnYmRrjdeYHkauEJBSBxkYXKnXPEsw62JrADX5TtS01nBcL9dST2CF2dd33waJHINp/i16exNv3NBy0+iPWZTEr2dhpI03TLw6JoFT4WZS6qFJz/H1i/x654Zhz6wEBzb9Vcb4s6KlsI+XgcR5QjP4ToZDkftDRusSl1ZIzrLmBPga8Vyu/AgMBAAGjggHmMIIB4jAOBgNVHQ8BAf8EBAMCBkAwGgYDVR0gAQH/BBAwDjAMBgorBgEEAZOWLwECMGwGCCsGAQUFBwEDBGAwXjAIBgYEAI5GAQEwCAYGBACORgEEMBMGBgQAjkYBBjAJBgcEAI5GAQYBMA4GBgQAjkYBBzAEEwJaWjAOBgYEAI5GAQkwBBMCWlowEwYGBACORgEIMAkGBwQAjkYBCAQwVAYDVR0fBE0wSzBJoEegRYZDaHR0cDovL2Rzcy5ub3dpbmEubHUvcGtpLWZhY3RvcnkvY3JsL1Rlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaLmNybDCBrwYIKwYBBQUHAQEEgaIwgZ8wTAYIKwYBBQUHMAGGQGh0dHA6Ly9kc3Mubm93aW5hLmx1L3BraS1mYWN0b3J5L29jc3AvVGVzdC1RdWFsaWZpZWQtQ0ExLWZyb20tWlowTwYIKwYBBQUHMAKGQ2h0dHA6Ly9kc3Mubm93aW5hLmx1L3BraS1mYWN0b3J5L2NydC9UZXN0LVF1YWxpZmllZC1DQTEtZnJvbS1aWi5jcnQwHwYDVR0jBBgwFoAUSm9V4sB8+kyT68EUetz0sILLfEkwHQYDVR0OBBYEFEeREyNWEjMaMiwKZV8I1mbThcLmMA0GCSqGSIb3DQEBCwUAA4IBAQAQi5e98xhT5o5/W1D7cyKgsEAhahxXo9TUDuiiEwyfory9jgtwhCvVlGZca53QVUoOAAtKFKOa6hPyhYdfxNEDzV6c9FiZbLSwVWgf7p8vzPxa8tMtzxnstp1ZXf56hhgKQBQMoM2gS1uOyH3TQ8vX2gQ8JYcreIjjNlWt/rn1M/sni5QT6IJDxcLYsSJtObBL/2hbqoQWgrv1TZ4pkWupObHTqBYDb9U/G1KMB6iLZbvipYzj+WTQYud5XRsH+L4m4etFoJ71g4gg09P8LmX2w0BB9so3US9Eo1F4arp5FHAW2wDatfb4/vCT0dE/GuoBSeAEHwlfz8G2uRG67bKo");
        assertNotNull(cert);

        QcStatements qcStatements = QcStatementUtils.getQcStatements(cert);
        assertNotNull(qcStatements);

        assertEquals(QCIdentMethodEnum.QCT_EIDAS2_B, qcStatements.getQcIdentMethod());
    }

    @Test
    void certWithCertForPIDQcStatement() {
        CertificateToken cert = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIFJzCCBA+gAwIBAgIDAYcUMA0GCSqGSIb3DQEBCwUAMH8xIzAhBgNVBAMMGlRlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaMTgwNgYDVQQKDC9UZXN0IFF1YWxpZmllZCBUcnVzdCBTZXJ2aWNlIFByb3ZpZGVyIDEgZnJvbSBaWjERMA8GA1UECwwIUEtJLVRFU1QxCzAJBgNVBAYTAlpaMB4XDTI1MDMwOTA4MTEyMFoXDTI3MDEwOTA4MTEyMFowcTEVMBMGA1UEAwwMQ2VydCBmb3IgUElEMTgwNgYDVQQKDC9UZXN0IFF1YWxpZmllZCBUcnVzdCBTZXJ2aWNlIFByb3ZpZGVyIDEgZnJvbSBaWjERMA8GA1UECwwIUEtJLVRFU1QxCzAJBgNVBAYTAlpaMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnQV7+LVn+N2OepfPv7I4liKBdEQNG3bR9WEU//Ju1bpWAAsMvjVo+JYH0uikPa1b5HuasnXjA2+/PWTfKDngcfEsotMuLDZaGVEdFIkrudjGgGwKJWoVbdPadiLcZ6K5n2KHssB1ILxjf6Cgd/sWyBH1X7Z4DckUWOK7Phrm0WYvUjeAEcZi5nZyzeEIiqCWi78j7pAtqKpEVjVb6RT40VYSfcO2wLN7KmOxEn+ZVrE7EWDMRXT+HrNNXO5KbWt1c9rio+hjxlJaRH25aPQTQM7/dqevUjfn+4ZkVhxH9PqecKZD9T9w5bFIGGddqwdKG5GYHX8yWNwhchqu2bWJQwIDAQABo4IBuDCCAbQwDgYDVR0PAQH/BAQDAgbAMCEGA1UdEQQaMBiCCW5vd2luYS5sdYILKi5ub3dpbmEubHUwNwYIKwYBBQUHAQMEKzApMAgGBgQAjkYBATAIBgYEAI5GAQQwEwYGBACORgEGMAkGBwQAi+xOAQEwVAYDVR0fBE0wSzBJoEegRYZDaHR0cDovL2Rzcy5ub3dpbmEubHUvcGtpLWZhY3RvcnkvY3JsL1Rlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaLmNybDCBrwYIKwYBBQUHAQEEgaIwgZ8wTAYIKwYBBQUHMAGGQGh0dHA6Ly9kc3Mubm93aW5hLmx1L3BraS1mYWN0b3J5L29jc3AvVGVzdC1RdWFsaWZpZWQtQ0ExLWZyb20tWlowTwYIKwYBBQUHMAKGQ2h0dHA6Ly9kc3Mubm93aW5hLmx1L3BraS1mYWN0b3J5L2NydC9UZXN0LVF1YWxpZmllZC1DQTEtZnJvbS1aWi5jcnQwHwYDVR0jBBgwFoAU/RVYd982PMkwzrISxYWBINpCUNIwHQYDVR0OBBYEFMaLEMU1w/RJ/V4floj8arXG415pMA0GCSqGSIb3DQEBCwUAA4IBAQBLnEKbNoQp4GUgCh9ioNMNEr1sShUbKTooCtzbS48KM8WpbqOzlxe9agWHhyb/skqN0/h4aFFFZdgbfHD10/91R001MOV0InFK0QLyxkr4XkBpOVrBQH3tQSpR4cZxJZd9wsysNZW8BGeoPez9LBwPeBwsYa+2Gt3Ej0Fn2Frc/XOIcGPjJViD2CBFNNril6iuKf9NDj13wJSXIs0znSDJpDO0N2fhlFyzQUUmKS9w8SJC2/X4IxHzGzBzKcAcXyPMow20AuQ2VdrCWTcC9t+2oaBpzjtLQmv7r0tJXtAMD5YV/nZvb06F937QC1QL7DHfWt2A0tKTlM/DDl+zkXfb");
        assertNotNull(cert);

        QcStatements qcStatements = QcStatementUtils.getQcStatements(cert);
        assertNotNull(qcStatements);

        List<QCType> qcTypes = qcStatements.getQcTypes();
        assertEquals(1, qcTypes.size());
        assertEquals(QCTypeEnum.QCT_PID, qcTypes.get(0));
    }

    @Test
    void certWithCertForWalletQcStatement() {
        CertificateToken cert = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIFKjCCBBKgAwIBAgIDAYcVMA0GCSqGSIb3DQEBCwUAMH8xIzAhBgNVBAMMGlRlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaMTgwNgYDVQQKDC9UZXN0IFF1YWxpZmllZCBUcnVzdCBTZXJ2aWNlIFByb3ZpZGVyIDEgZnJvbSBaWjERMA8GA1UECwwIUEtJLVRFU1QxCzAJBgNVBAYTAlpaMB4XDTI1MDMwOTA4MTAzNVoXDTI3MDEwOTA4MTAzNVowdDEYMBYGA1UEAwwPQ2VydCBmb3IgV2FsbGV0MTgwNgYDVQQKDC9UZXN0IFF1YWxpZmllZCBUcnVzdCBTZXJ2aWNlIFByb3ZpZGVyIDEgZnJvbSBaWjERMA8GA1UECwwIUEtJLVRFU1QxCzAJBgNVBAYTAlpaMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyWEqqjh51SWSUP+J+Y17AuM6R1rkz2nsy5dh3lhntVLRSN+qPwRed3DXVNdmIgJuL9HjoI3T5hSwrt3hzcTrDhVgGaWwnjJ+OyNYlMbrm/8x+WwTQkLzz7QN07CPaXhQNB0hHnbHfqB80nGaUfGyNuOzPO7ol35vvfK8eeZ5KeJwqXvThjH0+NS9NrBpOB5yVHQ1AkZ0pV+wM3wMqCN1Cks6/GRArJDbN/9h2DFtwaKSQiENlZ9KOYhRUMsjUtDHSWSJRxbPBq9H/TU9TItWH+rwnMolFHpOaopT6AqnzIKJXHtYhv7RIYxtPw07txKLNQK5fof0Mm0+I7EbYPfR7wIDAQABo4IBuDCCAbQwDgYDVR0PAQH/BAQDAgbAMCEGA1UdEQQaMBiCCW5vd2luYS5sdYILKi5ub3dpbmEubHUwNwYIKwYBBQUHAQMEKzApMAgGBgQAjkYBATAIBgYEAI5GAQQwEwYGBACORgEGMAkGBwQAi+xOAQIwVAYDVR0fBE0wSzBJoEegRYZDaHR0cDovL2Rzcy5ub3dpbmEubHUvcGtpLWZhY3RvcnkvY3JsL1Rlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaLmNybDCBrwYIKwYBBQUHAQEEgaIwgZ8wTAYIKwYBBQUHMAGGQGh0dHA6Ly9kc3Mubm93aW5hLmx1L3BraS1mYWN0b3J5L29jc3AvVGVzdC1RdWFsaWZpZWQtQ0ExLWZyb20tWlowTwYIKwYBBQUHMAKGQ2h0dHA6Ly9kc3Mubm93aW5hLmx1L3BraS1mYWN0b3J5L2NydC9UZXN0LVF1YWxpZmllZC1DQTEtZnJvbS1aWi5jcnQwHwYDVR0jBBgwFoAU1rCuDUzUt8vcQvdwHbau3TPpaKkwHQYDVR0OBBYEFNnDoa7lyndZV4iNK1M3NpYo05bnMA0GCSqGSIb3DQEBCwUAA4IBAQBPAdivSEsKotwAY9lqvPUsxh3g06eWjNDkiCegA6JlKEcIPkrg0hG3uDI5mwr6i9UR6HS7dYcppnB8cN2enttngZrj5+r6Q0wjBvBKaKV6koLThh5TOKvFaHHoaaDDNiFZjcak2xH781n56Q6FPKOkVKR830LP/nEDUlAII4q9c5z06Am5HZk8aWmlw0u2N7yRks3MW069Net1lMgvfQtioar6w30Jjrz+UrGl5bdUfl/vl4rSK4GPvwRS+s5lgyQXK5eMGX40GhagmkD2Ss5AvsVHc9DZH18IyI6r4BsfakoCo5ap9Wf/iN860zsPB0iYevmNar5jiDga8ShIcQOD");
        assertNotNull(cert);

        QcStatements qcStatements = QcStatementUtils.getQcStatements(cert);
        assertNotNull(qcStatements);

        List<QCType> qcTypes = qcStatements.getQcTypes();
        assertEquals(1, qcTypes.size());
        assertEquals(QCTypeEnum.QCT_WAL, qcTypes.get(0));
    }

    @Test
    void certWithCertForPSBQcStatement() {
        CertificateToken cert = DSSUtils.loadCertificateFromBase64EncodedString(
                "MIIFTDCCBDSgAwIBAgIDAYcWMA0GCSqGSIb3DQEBCwUAMH8xIzAhBgNVBAMMGlRlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaMTgwNgYDVQQKDC9UZXN0IFF1YWxpZmllZCBUcnVzdCBTZXJ2aWNlIFByb3ZpZGVyIDEgZnJvbSBaWjERMA8GA1UECwwIUEtJLVRFU1QxCzAJBgNVBAYTAlpaMB4XDTI1MDMwNjEyNDIwNVoXDTI3MDEwNjEyNDIwNVowdDEYMBYGA1UEAwwPQ2VydCBmb3IgUFVCRUFBMTgwNgYDVQQKDC9UZXN0IFF1YWxpZmllZCBUcnVzdCBTZXJ2aWNlIFByb3ZpZGVyIDEgZnJvbSBaWjERMA8GA1UECwwIUEtJLVRFU1QxCzAJBgNVBAYTAlpaMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAqO4hLJN2GKqfD34Dtp7RH0DFB6iuOgewBVAMVXtZW+AkLJBOVvRL3V9w+5dqOYGzOfkoyASQVyeB7Sh0Xf8si+Cg6wh1PWkAAJGaLZdwIj09HjQfeMA7rBhydOnAh0IHfnM3UmBJFr4vJhLwmynsxWciGY591qZFgRFA5EYuoEvGqpmfWI5qZHFrf5XjTrsfv7uMd+vTX9zk9yv/9B1FmoYlcFt3y1DF+cQbqu9CoxRO+2+dmdaQztuM6KaBOe/6+81X2XxfGT4K4D4+KuwYOPX1z/rIeZjfwFuC/aJWLXSekZhb4++IKTWycWZqMMBVrjgLMTQKDqcxMCUHVR62IwIDAQABo4IB2jCCAdYwDgYDVR0PAQH/BAQDAgbAMCEGA1UdEQQaMBiCCW5vd2luYS5sdYILKi5ub3dpbmEubHUwWQYIKwYBBQUHAQMETTBLMAgGBgQAjkYBATAIBgYEAI5GAQQwEwYGBACORgEGMAkGBwQAjkYBBgEwIAYHBACL7E4BAzAVEwJFVQwISUQtMTIzNDUMBVRFLUVVMFQGA1UdHwRNMEswSaBHoEWGQ2h0dHA6Ly9kc3Mubm93aW5hLmx1L3BraS1mYWN0b3J5L2NybC9UZXN0LVF1YWxpZmllZC1DQTEtZnJvbS1aWi5jcmwwga8GCCsGAQUFBwEBBIGiMIGfMEwGCCsGAQUFBzABhkBodHRwOi8vZHNzLm5vd2luYS5sdS9wa2ktZmFjdG9yeS9vY3NwL1Rlc3QtUXVhbGlmaWVkLUNBMS1mcm9tLVpaME8GCCsGAQUFBzAChkNodHRwOi8vZHNzLm5vd2luYS5sdS9wa2ktZmFjdG9yeS9jcnQvVGVzdC1RdWFsaWZpZWQtQ0ExLWZyb20tWlouY3J0MB8GA1UdIwQYMBaAFH/zjBn9WK6TDqo2q0OeyJ8g+GHrMB0GA1UdDgQWBBSVTVcvEiZRceBt0fsBjimrnzgxEjANBgkqhkiG9w0BAQsFAAOCAQEAT8ruIWB+3iI2S0JvRkptyPEIkJzfwMMDAJz9SiYeUOSjKbTCjJp7Sf+h/un191KOaTxhCrnFKGx0uVtSuLREd1zk7Og+gti2SLXEeWOM+jfCp2/+Sos/Dplht6GDzUKtB4Z8SNz1FtUcyJAq0E88h9HKYqzYo6qmQvPjDm8tTQxFcXq5PBHaCp1p16sDi8hQU8M8GpvzAli6PjJx4utGKMwZO3HrxxYcPi40WjPXnI/B9+cWBu4y4CIlNl9ugNIzX6X0X7iPLoK0jjFWW3WBgMl7rDvr9Wp/KLl24kfKO2F8pntfh4hmvMRfSGnudGLwgo2pv2sBXKdqzXf5TfjdgQ==");
        assertNotNull(cert);

        QcStatements qcStatements = QcStatementUtils.getQcStatements(cert);
        assertNotNull(qcStatements);

        QCPSB qcPSB = qcStatements.getQcPSB();
        assertNotNull(qcPSB);
        assertEquals("EU", qcPSB.getCountryOfLegislation());
        assertEquals("ID-12345", qcPSB.getAuthSourceIdentification());
        assertEquals("TE-EU", qcPSB.getLegislationIdentification());
    }

    @Test
    void certWithoutQCStatements() {
        CertificateToken certificate = DSSUtils.loadCertificate(new File("src/test/resources/TSP_Certificate_2014.crt"));
        QcStatements qcStatements = QcStatementUtils.getQcStatements(certificate);
        assertNull(qcStatements);
    }

    @Test
    void qcStatementNullSequence() {
        assertNull(QcStatementUtils.getQcStatements((ASN1Sequence) null));
    }

    @Test
    void getQcStatementsTest() {
        ASN1Encodable[] asn1Encodables = new ASN1Encodable[1];
        asn1Encodables[0] = new QCStatement(ETSIQCObjectIdentifiers.id_etsi_qcs_QcCompliance);
        DERSequence asn1Sequence = new DERSequence(asn1Encodables);

        QcStatements qcStatements = QcStatementUtils.getQcStatements(asn1Sequence);
        assertNotNull(qcStatements);
        assertTrue(qcStatements.isQcCompliance());

        asn1Encodables[0] = ETSIQCObjectIdentifiers.id_etsi_qcs_QcCompliance;
        asn1Sequence = new DERSequence(asn1Encodables);

        qcStatements = QcStatementUtils.getQcStatements(asn1Sequence);
        assertNotNull(qcStatements);
        assertFalse(qcStatements.isQcCompliance());
    }

}
