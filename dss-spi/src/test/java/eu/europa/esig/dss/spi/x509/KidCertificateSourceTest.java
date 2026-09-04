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
package eu.europa.esig.dss.spi.x509;

import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class KidCertificateSourceTest {

    private static CertificateToken token;
    private static CertificateToken token2;

    private static byte[] issuerSerialToken;
    private static byte[] issuerSerialToken2;

    @BeforeAll
    static void init() {
        token = DSSUtils.loadCertificate(new File("src/test/resources/citizen_ca.cer"));
        token2 = DSSUtils.loadCertificate(new File("src/test/resources/ecdsa.cer"));

        issuerSerialToken = DSSUtils.generateKid(token);
        issuerSerialToken2 = DSSUtils.generateKid(token2);
    }

    @Test
    void addCertificatesTest() {
        KidCertificateSource kidCertificateSource = new KidCertificateSource();
        assertEquals(0, kidCertificateSource.getNumberOfCertificates());

        kidCertificateSource.addCertificate(token);
        assertEquals(1, kidCertificateSource.getNumberOfCertificates());
        assertEquals(token, kidCertificateSource.getCertificateByKid(Utils.toBase64(issuerSerialToken)));

        kidCertificateSource.addCertificate(token2);
        assertEquals(2, kidCertificateSource.getNumberOfCertificates());
        assertEquals(token, kidCertificateSource.getCertificateByKid(Utils.toBase64(issuerSerialToken)));
        assertEquals(token2, kidCertificateSource.getCertificateByKid(Utils.toBase64(issuerSerialToken2)));

        kidCertificateSource = new KidCertificateSource();
        kidCertificateSource.addCertificate("KID-ID", token);

        assertEquals(1, kidCertificateSource.getNumberOfCertificates());
        assertNull(kidCertificateSource.getCertificateByKid(Utils.toBase64(issuerSerialToken)));
        assertEquals(token, kidCertificateSource.getCertificateByKid("KID-ID"));

        kidCertificateSource = new KidCertificateSource();
        kidCertificateSource.addCertificate("KID-ID".getBytes(), token);

        assertEquals(1, kidCertificateSource.getNumberOfCertificates());
        assertNull(kidCertificateSource.getCertificateByKid(Utils.toBase64(issuerSerialToken)));
        assertEquals(token, kidCertificateSource.getCertificateByKid("KID-ID".getBytes()));
        assertEquals(token, kidCertificateSource.getCertificateByKid(Utils.toBase64("KID-ID".getBytes())));
    }

    @Test
    void nullTest() {
        KidCertificateSource kidCertificateSource = new KidCertificateSource();

        Exception exception = assertThrows(NullPointerException.class, () -> kidCertificateSource.addCertificate(null));
        assertEquals("The certificate must be filled", exception.getMessage());

        exception = assertThrows(NullPointerException.class, () -> kidCertificateSource.addCertificate((String) null, token));
        assertEquals("kid cannot be null!", exception.getMessage());

        exception = assertThrows(NullPointerException.class, () -> kidCertificateSource.addCertificate((byte[]) null, token));
        assertEquals("kid cannot be null!", exception.getMessage());

        exception = assertThrows(NullPointerException.class, () -> kidCertificateSource.addCertificate("KID-ID", null));
        assertEquals("The certificate must be filled", exception.getMessage());

        exception = assertThrows(NullPointerException.class, () -> kidCertificateSource.addCertificate("KID-ID".getBytes(), null));
        assertEquals("The certificate must be filled", exception.getMessage());
    }

}
