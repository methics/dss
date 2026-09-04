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

import org.bouncycastle.operator.ContentVerifierProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.operator.jcajce.JcaContentVerifierProviderBuilder;

import java.security.Provider;
import java.security.PublicKey;

/**
 * This factory is used to create a {@code org.bouncycastle.operator.ContentVerifierProvider} instance based on the PublicKey algorithm's name.
 *
 */
public class DSSContentVerifierProviderSecurityFactory extends DSSSecurityFactory<PublicKey, ContentVerifierProvider> {

    /**
     * Instance of the factory to initialize a ContentVerifierProvider object
     */
    public static final DSSContentVerifierProviderSecurityFactory INSTANCE = new DSSContentVerifierProviderSecurityFactory();

    /**
     * Default constructor
     */
    private DSSContentVerifierProviderSecurityFactory() {
        // empty
    }

    @Override
    protected String getFactoryClassName() {
        return ContentVerifierProvider.class.getSimpleName();
    }

    @Override
    protected String toString(PublicKey input) {
        return String.format("PublicKey with algorithm '%s'",  input != null ? input.getAlgorithm() : null);
    }

    @Override
    protected ContentVerifierProvider buildWithProvider(PublicKey input, Provider securityProvider) throws OperatorCreationException {
        JcaContentVerifierProviderBuilder jcaContentVerifierProviderBuilder = new JcaContentVerifierProviderBuilder();
        jcaContentVerifierProviderBuilder.setProvider(securityProvider);
        return jcaContentVerifierProviderBuilder.build(input);
    }

}
