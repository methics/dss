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
package eu.europa.esig.dss.spi.security;

import eu.europa.esig.dss.model.DSSException;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PublicKey;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DSSPublicKeySecurityFactoryTest {

    @Test
    void loadRsaPublicKeyFromBinary() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);

        KeyPair keyPair = kpg.generateKeyPair();

        byte[] encoded = keyPair.getPublic().getEncoded();

        PublicKey loadedKey =
                DSSPublicKeySecurityFactory.BINARY_INSTANCE.build(encoded);

        assertNotNull(loadedKey);
        assertEquals(keyPair.getPublic(), loadedKey);
        assertArrayEquals(encoded, loadedKey.getEncoded());
    }

    @Test
    void loadRsaPublicKeyFromInputStream() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);

        KeyPair keyPair = kpg.generateKeyPair();

        byte[] encoded = keyPair.getPublic().getEncoded();

        PublicKey loadedKey =
                DSSPublicKeySecurityFactory.INPUT_STREAM_INSTANCE.build(
                        new ByteArrayInputStream(encoded));

        assertNotNull(loadedKey);
        assertEquals(keyPair.getPublic(), loadedKey);
    }

    @Test
    void loadEcPublicKeyFromBinary() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256);

        KeyPair keyPair = kpg.generateKeyPair();

        PublicKey loadedKey =
                DSSPublicKeySecurityFactory.BINARY_INSTANCE.build(
                        keyPair.getPublic().getEncoded());

        assertNotNull(loadedKey);
        assertEquals(keyPair.getPublic(), loadedKey);
    }

    @Test
    void loadEcdsaPublicKeyFromBinary() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256); // secp256r1

        KeyPair keyPair = kpg.generateKeyPair();

        byte[] encoded = keyPair.getPublic().getEncoded();

        PublicKey loadedKey =
                DSSPublicKeySecurityFactory.BINARY_INSTANCE.build(encoded);

        assertNotNull(loadedKey);
        assertEquals("ECDSA", loadedKey.getAlgorithm());
        assertEquals(keyPair.getPublic(), loadedKey);
        assertArrayEquals(encoded, loadedKey.getEncoded());
    }

    @Test
    void loadEcdsaPublicKeyFromInputStream() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("EC");
        kpg.initialize(256); // secp256r1

        KeyPair keyPair = kpg.generateKeyPair();

        byte[] encoded = keyPair.getPublic().getEncoded();

        PublicKey loadedKey =
                DSSPublicKeySecurityFactory.INPUT_STREAM_INSTANCE.build(
                        new ByteArrayInputStream(encoded));

        assertNotNull(loadedKey);
        assertEquals("ECDSA", loadedKey.getAlgorithm());
        assertEquals(keyPair.getPublic(), loadedKey);
        assertArrayEquals(encoded, loadedKey.getEncoded());
    }

    @Test
    void binaryAndInputStreamReturnEquivalentKeys() throws Exception {
        KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
        kpg.initialize(2048);

        PublicKey original = kpg.generateKeyPair().getPublic();

        byte[] encoded = original.getEncoded();

        PublicKey binaryLoaded =
                DSSPublicKeySecurityFactory.BINARY_INSTANCE.build(encoded);

        PublicKey streamLoaded =
                DSSPublicKeySecurityFactory.INPUT_STREAM_INSTANCE.build(
                        new ByteArrayInputStream(encoded));

        assertEquals(binaryLoaded, streamLoaded);
        assertArrayEquals(binaryLoaded.getEncoded(), streamLoaded.getEncoded());
    }

    @Test
    void nullBinaryInputThrowsException() {
        Exception exception = assertThrows(NullPointerException.class,
                () -> DSSPublicKeySecurityFactory.BINARY_INSTANCE.build(null));
        assertEquals("Input cannot be null", exception.getMessage());
    }

    @Test
    void nullInputStreamThrowsException() {
        Exception exception = assertThrows(NullPointerException.class,
                () -> DSSPublicKeySecurityFactory.INPUT_STREAM_INSTANCE.build(null));
        assertEquals("Input cannot be null", exception.getMessage());
    }

    @Test
    void invalidBinaryInputThrowsException() {
        byte[] invalid = new byte[] {1, 2, 3, 4, 5};

        Exception exception = assertThrows(DSSException.class,
                () -> DSSPublicKeySecurityFactory.BINARY_INSTANCE.build(invalid));
        assertEquals("Unable to load KeyFactory for the given byte[]. All security providers have failed. " +
                "More detail in debug mode.", exception.getMessage());
    }

    @Test
    void invalidInputStreamThrowsException() {
        byte[] invalid = new byte[] {1, 2, 3, 4, 5};

        Exception exception = assertThrows(DSSException.class,
                () -> DSSPublicKeySecurityFactory.INPUT_STREAM_INSTANCE.build(
                        new ByteArrayInputStream(invalid)));
        assertEquals("Unable to load KeyFactory for the given ByteArrayInputStream. All security providers have failed. " +
                "More detail in debug mode.", exception.getMessage());
    }

}
