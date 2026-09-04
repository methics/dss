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
package eu.europa.esig.dss.signature.security;

import eu.europa.esig.dss.spi.security.DSSSecurityFactory;

import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Signature;

/**
 * This factory is used to create a {@code java.security.Signature} instance based on the signature algorithm's name.
 *
 */
public class DSSSignatureSecurityFactory extends DSSSecurityFactory<String, Signature> {

    /**
     * Instance of the factory to initialize a Signature object
     */
    public static final DSSSignatureSecurityFactory INSTANCE = new DSSSignatureSecurityFactory();

    /**
     * Default constructor
     */
    private DSSSignatureSecurityFactory() {
        // empty
    }

    @Override
    protected String getFactoryClassName() {
        return Signature.class.getSimpleName();
    }

    @Override
    protected String toString(String input) {
        return input;
    }

    @Override
    protected Signature buildWithProvider(String input, Provider securityProvider) throws NoSuchAlgorithmException {
        return Signature.getInstance(input, securityProvider);
    }

}
