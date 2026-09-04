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

import eu.europa.esig.dss.utils.Utils;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.security.Provider;
import java.security.Security;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DSSSecurityProviderTest {

    @AfterEach
    void resetToDefault() {
        DSSSecurityProvider.setSecurityProvider(new BouncyCastleProvider());
        DSSSecurityProvider.setAlternativeSecurityProviders(new Provider[] {});
    }

    @Test
    void setProviderTest() {
        DSSSecurityProvider.setSecurityProvider(new BouncyCastleProvider());

        Provider securityProvider = DSSSecurityProvider.getSecurityProvider();
        assertNotNull(securityProvider);
        assertEquals("BC", securityProvider.getName());
        assertEquals("BC", DSSSecurityProvider.getSecurityProviderName());

        DSSSecurityProvider.setSecurityProvider("SUN");

        securityProvider = DSSSecurityProvider.getSecurityProvider();
        assertNotNull(securityProvider);
        assertEquals("SUN", securityProvider.getName());
        assertEquals("SUN", DSSSecurityProvider.getSecurityProviderName());
    }

    @Test
    void setProviderNullTest() {
        Exception exception = assertThrows(NullPointerException.class, () -> DSSSecurityProvider.setSecurityProvider((Provider) null));
        assertEquals("Provider cannot be null!", exception.getMessage());

        exception = assertThrows(NullPointerException.class, () -> DSSSecurityProvider.setSecurityProvider((String) null));
        assertEquals("Provider name cannot be null!", exception.getMessage());
    }

    @Test
    void setProviderInvalidTest() {
        Exception exception = assertThrows(SecurityException.class, () -> DSSSecurityProvider.setSecurityProvider("INVALID"));
        assertEquals("An error occurred on security Provider initialization : " +
                "Unable to instantiate security Provider for name 'INVALID'! The implementation is not found.", exception.getMessage());
    }

    @Test
    void setAlternativeSecurityProvidersTest() {
        assertFalse(Utils.isArrayNotEmpty(DSSSecurityProvider.getAlternativeSecurityProviders()));
        assertFalse(Utils.isArrayNotEmpty(DSSSecurityProvider.getAlternativeSecurityProviderNames()));

        DSSSecurityProvider.setAlternativeSecurityProviders("SUN");

        assertTrue(Utils.isArrayNotEmpty(DSSSecurityProvider.getAlternativeSecurityProviders()));
        assertTrue(Utils.isArrayNotEmpty(DSSSecurityProvider.getAlternativeSecurityProviderNames()));

        Provider[] alternativeSecurityProviders = DSSSecurityProvider.getAlternativeSecurityProviders();
        assertEquals(1, alternativeSecurityProviders.length);

        Provider provider = alternativeSecurityProviders[0];
        assertEquals("SUN", provider.getName());

        String[] alternativeSecurityProviderNames = DSSSecurityProvider.getAlternativeSecurityProviderNames();
        assertEquals(1, alternativeSecurityProviderNames.length);

        String providerName = alternativeSecurityProviderNames[0];
        assertEquals("SUN", providerName);

        DSSSecurityProvider.setAlternativeSecurityProviders(Security.getProvider("SUN"), new BouncyCastleProvider());

        assertEquals(2, Utils.arraySize(DSSSecurityProvider.getAlternativeSecurityProviders()));
        assertEquals(2, Utils.arraySize(DSSSecurityProvider.getAlternativeSecurityProviderNames()));

        alternativeSecurityProviders = DSSSecurityProvider.getAlternativeSecurityProviders();
        Provider firstProvider = alternativeSecurityProviders[0];
        assertEquals("SUN", firstProvider.getName());
        Provider secondProvider = alternativeSecurityProviders[1];
        assertEquals("BC", secondProvider.getName());

        alternativeSecurityProviderNames = DSSSecurityProvider.getAlternativeSecurityProviderNames();
        String firstProviderName = alternativeSecurityProviderNames[0];
        assertEquals("SUN", firstProviderName);
        String secondProviderName = alternativeSecurityProviderNames[1];
        assertEquals("BC", secondProviderName);
    }

    @Test
    void setAlternativeSecurityProvidersNullTest() {
        Exception exception = assertThrows(NullPointerException.class, () -> DSSSecurityProvider.setAlternativeSecurityProviders((Provider) null));
        assertEquals("Provider cannot be null!", exception.getMessage());

        exception = assertThrows(NullPointerException.class, () -> DSSSecurityProvider.setAlternativeSecurityProviders((String) null));
        assertEquals("Provider name cannot be null!", exception.getMessage());

        exception = assertThrows(NullPointerException.class, () -> DSSSecurityProvider.setAlternativeSecurityProviders((Provider[]) null));
        assertEquals("Array of providers cannot be null!", exception.getMessage());

        exception = assertThrows(NullPointerException.class, () -> DSSSecurityProvider.setAlternativeSecurityProviders((String[]) null));
        assertEquals("Array of provider names cannot be null!", exception.getMessage());
    }

    @Test
    void setAlternativeSecurityProvidersInvalidTest() {
        Exception exception = assertThrows(SecurityException.class, () -> DSSSecurityProvider.setAlternativeSecurityProviders("INVALID"));
        assertEquals("An error occurred on security Provider initialization : " +
                "Unable to instantiate security Provider for name 'INVALID'! The implementation is not found.", exception.getMessage());

        exception = assertThrows(SecurityException.class, () -> DSSSecurityProvider.setAlternativeSecurityProviders("SUN", "INVALID"));
        assertEquals("An error occurred on security Provider initialization : " +
                "Unable to instantiate security Provider for name 'INVALID'! The implementation is not found.", exception.getMessage());
    }

}
