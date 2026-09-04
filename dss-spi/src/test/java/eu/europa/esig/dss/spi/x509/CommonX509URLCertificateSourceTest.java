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
import eu.europa.esig.dss.spi.client.http.MemoryDataLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CommonX509URLCertificateSourceTest {

    private static final String URL = "https://nowina.lu/pki-factory/good-pki/cert-chain";

    private static CertificateToken token;
    private static CertificateToken token2;

    @BeforeAll
    static void init() {
        token = DSSUtils.loadCertificate(new File("src/test/resources/citizen_ca.cer"));
        token2 = DSSUtils.loadCertificate(new File("src/test/resources/ecdsa.cer"));
    }

    @Test
    void addCertificatesTest() {
        CommonX509URLCertificateSource x509URLCertificateSource = new CommonX509URLCertificateSource();
        assertEquals(0, x509URLCertificateSource.getNumberOfCertificates());

        Exception exception = assertThrows(UnsupportedOperationException.class, () -> x509URLCertificateSource.addCertificate(token));
        assertEquals("#addCertificate(certificateToAdd) method is not supported in CommonX509URLCertificateSource! " +
                "Please use #addCertificate(uri, certificateToAdd) or #addCertificates(uri, certificatesToAdd) methods.", exception.getMessage());

        x509URLCertificateSource.addCertificate(URL, token);
        assertEquals(1, x509URLCertificateSource.getNumberOfCertificates());
        assertEquals(Collections.singletonList(token), x509URLCertificateSource.getCertificatesByUrl(URL));

        x509URLCertificateSource.addCertificate(URL, token2);
        assertEquals(2, x509URLCertificateSource.getNumberOfCertificates());
        assertEquals(Arrays.asList(token, token2), x509URLCertificateSource.getCertificatesByUrl(URL));

        x509URLCertificateSource.reset();
        assertEquals(0, x509URLCertificateSource.getNumberOfCertificates());

        x509URLCertificateSource.addCertificates(URL, Arrays.asList(token, token2));
        assertEquals(2, x509URLCertificateSource.getNumberOfCertificates());
        assertEquals(Arrays.asList(token, token2), x509URLCertificateSource.getCertificatesByUrl(URL));

        assertEquals(Collections.emptyList(), x509URLCertificateSource.getCertificatesByUrl("https://wrong.url"));
    }

    @Test
    void dataLoaderTest() {
        HashMap<String, byte[]> map = new HashMap<>();
        map.put(URL, token.getEncoded());

        MemoryDataLoader dataLoader = new MemoryDataLoader(map);

        CommonX509URLCertificateSource x509URLCertificateSource = new CommonX509URLCertificateSource();
        assertEquals(Collections.emptyList(), x509URLCertificateSource.getCertificatesByUrl(URL));

        x509URLCertificateSource.setDataLoader(dataLoader);
        assertEquals(Collections.singletonList(token), x509URLCertificateSource.getCertificatesByUrl(URL));

        x509URLCertificateSource = new CommonX509URLCertificateSource(dataLoader);
        assertEquals(Collections.singletonList(token), x509URLCertificateSource.getCertificatesByUrl(URL));
    }

}
