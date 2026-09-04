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
package eu.europa.esig.dss.pki.jaxb.security;

import eu.europa.esig.dss.spi.security.DSSSecurityFactory;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.KeyPairGenerator;

/**
 * This factory is used to create a {@code java.security.KeyPairGenerator} instance based on the algorithm's name.
 *
 */
public class DSSKeyPairGeneratorSecurityFactory extends DSSSecurityFactory<String, KeyPairGenerator> {

    /**
     * Instance of the factory to initialize a KeyPairGenerator object
     */
    public static final DSSKeyPairGeneratorSecurityFactory INSTANCE = new DSSKeyPairGeneratorSecurityFactory();

    /**
     * Default constructor
     */
    private DSSKeyPairGeneratorSecurityFactory() {
        // empty
    }

    @Override
    protected String getFactoryClassName() {
        return KeyPairGenerator.class.getSimpleName();
    }

    @Override
    protected String toString(String input) {
        return input;
    }

    @Override
    protected KeyPairGenerator buildWithProvider(String input, Provider securityProvider) throws NoSuchAlgorithmException {
        return KeyPairGenerator.getInstance(input, securityProvider);
    }

}
