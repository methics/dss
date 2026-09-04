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
package eu.europa.esig.dss.spi.random;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import org.junit.jupiter.api.Test;

import java.security.SecureRandom;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DSSSecureRandomProviderTest {

    @Test
    void deterministicTest() {
        DSSSecureRandomProvider provider =
                new DSSSecureRandomProvider();

        byte[] seed = "my-seed".getBytes();

        SecureRandom random1 = provider.getSecureRandom(seed);
        SecureRandom random2 = provider.getSecureRandom(seed);

        byte[] bytes1 = new byte[2048];
        byte[] bytes2 = new byte[2048];

        random1.nextBytes(bytes1);
        random2.nextBytes(bytes2);

        assertArrayEquals(bytes1, bytes2,
                "Random streams generated from same seed must match");
    }

    @Test
    void differentSeed() {
        DSSSecureRandomProvider provider =
                new DSSSecureRandomProvider();

        SecureRandom random1 =
                provider.getSecureRandom("seed-1".getBytes());

        SecureRandom random2 =
                provider.getSecureRandom("seed-2".getBytes());

        byte[] bytes1 = new byte[512];
        byte[] bytes2 = new byte[512];

        random1.nextBytes(bytes1);
        random2.nextBytes(bytes2);

        assertFalse(Arrays.equals(bytes1, bytes2),
                "Random streams generated from different seeds must differ");
    }

    @Test
    void largeBytesTest() {
        DSSSecureRandomProvider provider =
                new DSSSecureRandomProvider();

        SecureRandom random =
                provider.getSecureRandom("large-request-seed".getBytes());

        byte[] bytes = new byte[10_000];

        assertDoesNotThrow(() -> random.nextBytes(bytes));

        /*
         * Ensure output is not empty / all zeros.
         */
        boolean allZero = true;

        for (byte b : bytes) {
            if (b != 0) {
                allZero = false;
                break;
            }
        }

        assertFalse(allZero,
                "Generated random bytes should not be all zeros");
    }

    @Test
    void consistentTest() {
        DSSSecureRandomProvider provider =
                new DSSSecureRandomProvider();

        byte[] seed = "chunk-seed".getBytes();

        SecureRandom random1 = provider.getSecureRandom(seed);
        SecureRandom random2 = provider.getSecureRandom(seed);

        byte[] full = new byte[4096];
        random1.nextBytes(full);

        byte[] chunked = new byte[4096];

        byte[] chunk1 = new byte[1000];
        byte[] chunk2 = new byte[2000];
        byte[] chunk3 = new byte[1096];

        random2.nextBytes(chunk1);
        random2.nextBytes(chunk2);
        random2.nextBytes(chunk3);

        System.arraycopy(chunk1, 0, chunked, 0, chunk1.length);
        System.arraycopy(chunk2, 0, chunked, chunk1.length, chunk2.length);
        System.arraycopy(chunk3, 0, chunked,
                chunk1.length + chunk2.length, chunk3.length);

        assertArrayEquals(full, chunked,
                "Chunked generation must match continuous generation");
    }

    @Test
    void generateSeedTest() {
        DSSSecureRandomProvider provider =
                new DSSSecureRandomProvider();

        byte[] seed = "seed-generation".getBytes();

        SecureRandom random1 = provider.getSecureRandom(seed);
        SecureRandom random2 = provider.getSecureRandom(seed);

        byte[] generated1 = random1.generateSeed(1024);
        byte[] generated2 = random2.generateSeed(1024);

        assertArrayEquals(generated1, generated2,
                "Generated seeds must be deterministic");
    }

    @Test
    void nullTest() {
        DSSSecureRandomProvider provider =
                new DSSSecureRandomProvider();

        Exception exception = assertThrows(NullPointerException.class,
                () -> provider.getSecureRandom(null));
        assertEquals("Seed cannot be null", exception.getMessage());
    }

    @Test
    void emptyTest() {
        DSSSecureRandomProvider provider =
                new DSSSecureRandomProvider();

        SecureRandom random =
                provider.getSecureRandom("empty".getBytes());

        byte[] bytes = new byte[0];

        assertDoesNotThrow(() -> random.nextBytes(bytes));

        assertEquals(0, bytes.length);
    }

    @Test
    void manyRequestsTest() {
        DSSSecureRandomProvider provider =
                new DSSSecureRandomProvider();

        byte[] seed = "multi-block-seed".getBytes();

        SecureRandom random1 = provider.getSecureRandom(seed);
        SecureRandom random2 = provider.getSecureRandom(seed);

        for (int i = 0; i < 100; i++) {

            byte[] a = new byte[333];
            byte[] b = new byte[333];

            random1.nextBytes(a);
            random2.nextBytes(b);

            assertArrayEquals(a, b,
                    "Streams diverged at iteration " + i);
        }
    }

    @Test
    void diffDigestAlgoTest() {
        DSSSecureRandomProvider provider =
                new DSSSecureRandomProvider(DigestAlgorithm.SHA1);

        byte[] seed = "my-seed".getBytes();

        SecureRandom random1 = provider.getSecureRandom(seed);
        SecureRandom random2 = provider.getSecureRandom(seed);

        byte[] bytes1 = new byte[2048];
        byte[] bytes2 = new byte[2048];

        random1.nextBytes(bytes1);
        random2.nextBytes(bytes2);

        assertArrayEquals(bytes1, bytes2,
                "Random streams generated from same seed must match");
    }

}
