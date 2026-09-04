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
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.utils.Utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.Provider;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * This factory is used to create a list of {@code eu.europa.esig.dss.model.x509.CertificateToken}s
 * from various input types, representing a p7c certificate chain, with options such as InputStream, byte array or a File.
 *
 * @param <I> input type
 */
public abstract class DSSP7CCertificatesSecurityFactory<I> extends DSSSecurityFactory<I, List<CertificateToken>> {

    /**
     * Default constructor
     */
    private DSSP7CCertificatesSecurityFactory() {
        // empty
    }

    /**
     * This class creates a list of CertificateToken's using the p7c certificate chain provided in the form of InputStream.
     * NOTE: Unlike other implementations, this class uses only one security provider because of inability
     *       to process InputStream multiple times.
     */
    public static final DSSP7CCertificatesSecurityFactory<InputStream> INPUT_STREAM_INSTANCE = new DSSP7CCertificatesSecurityFactory<InputStream>(){

        @Override
        protected String toString(InputStream input) {
            return "InputStream";
        }

        @Override
        public List<CertificateToken> build(InputStream input) {
            // NOTE: possible to process InputStream only once
            List<CertificateToken> certificateTokens = buildWithPrimarySecurityProvider(input);
            if (certificateTokens != null && certificateTokens.size() != 0) {
                return certificateTokens;
            }
            throw new DSSException(String.format("Unable to load %s for the given certificate chain provided as a %s. " +
                    "All security providers have failed. More detail in debug mode.", getFactoryClassName(), input.getClass().getSimpleName()));
        }

        @Override
        protected List<CertificateToken> buildWithProvider(InputStream input, Provider securityProvider) {
            final List<CertificateToken> certificates = new ArrayList<>();
            try (InputStream is = input) {
                @SuppressWarnings("unchecked")
                final Collection<X509Certificate> certificatesCollection = (Collection<X509Certificate>) CertificateFactory
                        .getInstance("X.509", securityProvider).generateCertificates(is);
                if (certificatesCollection != null) {
                    for (X509Certificate cert : certificatesCollection) {
                        certificates.add(new CertificateToken(cert));
                    }
                }
                if (certificates.isEmpty()) {
                    throw new DSSException("No certificate found in the InputStream");
                }
                return certificates;
            } catch (DSSException e) {
                throw e;
            } catch (Exception e) {
                throw new DSSException(String.format("Failed to load certificate(s) : %s", e.getMessage()), e);
            }
        }

    };

    /**
     * This class builds a list of CertificateToken's using the p7c certificate chain provided in the form of byte array
     */
    public static final DSSP7CCertificatesSecurityFactory<byte[]> BINARY_INSTANCE = new DSSP7CCertificatesSecurityFactory<byte[]>(){

        @Override
        protected String toString(byte[] input) {
            return input != null ? Utils.toBase64(input) : null;
        }

        @Override
        protected List<CertificateToken> buildWithProvider(byte[] input, Provider securityProvider) throws Exception {
            return INPUT_STREAM_INSTANCE.buildWithProvider(new ByteArrayInputStream(input), securityProvider);
        }

    };

    /**
     * This class builds a list of CertificateToken's using the p7c certificate chain provided in the form of File
     */
    public static final DSSP7CCertificatesSecurityFactory<File> FILE_INSTANCE = new DSSP7CCertificatesSecurityFactory<File>(){

        @Override
        protected String toString(File input) {
            return input != null ? input.getPath() : null;
        }

        @Override
        protected List<CertificateToken> buildWithProvider(File input, Provider securityProvider) throws Exception {
            try {
                return INPUT_STREAM_INSTANCE.buildWithProvider(Files.newInputStream(input.toPath()), securityProvider);
            } catch (IOException e) {
                throw new DSSException(String.format("Unable to find a file '%s' : %s", input.getPath(), e.getMessage()), e);
            }
        }

    };

    @Override
    protected String getFactoryClassName() {
        return CertificateFactory.class.getSimpleName();
    }

}
