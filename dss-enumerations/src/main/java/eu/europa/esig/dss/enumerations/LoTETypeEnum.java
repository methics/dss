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
package eu.europa.esig.dss.enumerations;

/**
 * Contains a list of registered LoTE types within ETSI TS 119 602
 *
 */
public enum LoTETypeEnum implements ListType {

    /** EU PID Providers List */
    EUPIDProvidersList("http://uri.etsi.org/19602/LoTEType/EUPIDProvidersList", "EU List of providers of person identity data"),

    /** EU Wallet Providers List */
    EUWalletProvidersList("http://uri.etsi.org/19602/LoTEType/EUWalletProvidersList", "EU List of wallet providers"),

    /** EU WRPAC Providers List */
    EUWRPACProvidersList("https://uri.etsi.org/19602/LoTEType/EUWRPACProvidersList", "EU List of providers of wallet relying party access certificates"),

    /** EU WRPRC Providers List */
    EUWRPRCProvidersList("http://uri.etsi.org/19602/LoTEType/EUWRPRCProvidersList", "EU List of providers of wallet relying party registration certificates"),

    /** EU Pub-EAA Providers List */
    EUPubEAAProvidersList("http://uri.etsi.org/19602/LoTEType/EUPubEAAProvidersList", "EU List of public sector bodies issuing electronic attestation of attributes"),

    /** EU Registrars and Registers List */
    EURegistrarsAndRegistersList("http://uri.etsi.org/19602/LoTEType/EURegistrarsAndRegistersList", "EU List of registrars and registers");

    /** URI associated with the TSPType */
    private final String uri;

    /** Name of the TSLType */
    private final String label;

    /**
     * Default constructor
     *
     * @param uri {@link String}
     * @param label {@link String}
     */
    LoTETypeEnum(final String uri, final String label) {
        this.uri = uri;
        this.label = label;
    }

    @Override
    public String getUri() {
        return uri;
    }

    @Override
    public String getLabel() {
        return label;
    }

}
