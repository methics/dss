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
package eu.europa.esig.dss.service;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SecureRandomNonceSourceTest {

    @Test
    void test() {
        assertEquals(32, new SecureRandomNonceSource().getNonceValue().length);
        assertFalse(Arrays.equals(new SecureRandomNonceSource().getNonceValue(), new SecureRandomNonceSource().getNonceValue()));

        SecureRandomNonceSource secureRandomNonceSource = new SecureRandomNonceSource();
        assertFalse(Arrays.equals(secureRandomNonceSource.getNonceValue(), secureRandomNonceSource.getNonceValue()));

        assertEquals(1, new SecureRandomNonceSource(1).getNonceValue().length);
        assertEquals(16, new SecureRandomNonceSource(16).getNonceValue().length);
        assertEquals(30, new SecureRandomNonceSource(30).getNonceValue().length);
        assertEquals(32, new SecureRandomNonceSource(32).getNonceValue().length);
        assertEquals(64, new SecureRandomNonceSource(64).getNonceValue().length);
        assertEquals(128, new SecureRandomNonceSource(128).getNonceValue().length);
    }

    @Test
    void illegalValueTest() {
        Exception exception = assertThrows(IllegalArgumentException.class, () -> new SecureRandomNonceSource(-1));
        assertEquals("The nonce size cannot be 0 or smaller!", exception.getMessage());

        exception = assertThrows(IllegalArgumentException.class, () -> new SecureRandomNonceSource(0));
        assertEquals("The nonce size cannot be 0 or smaller!", exception.getMessage());
    }

}
