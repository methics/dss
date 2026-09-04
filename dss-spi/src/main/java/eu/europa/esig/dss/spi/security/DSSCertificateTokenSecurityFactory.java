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
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.Provider;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.List;

/**
 * This factory is used to create a {@code eu.europa.esig.dss.model.x509.CertificateToken} from various input types,
 * such as InputStream, byte array or a File.
 *
 * @param <I> input type
 */
public abstract class DSSCertificateTokenSecurityFactory<I> extends DSSSecurityFactory<I, CertificateToken> {

    private static final Logger LOG = LoggerFactory.getLogger(DSSCertificateTokenSecurityFactory.class);

    /**
     * Default constructor
     */
    private DSSCertificateTokenSecurityFactory() {
        // empty
    }

    /**
     * This class creates a CertificateToken using the InputStream.
     * NOTE: Unlike other implementations, this class uses only one security provider because of inability
     *       to process InputStream multiple times.
     */
    public static final DSSCertificateTokenSecurityFactory<InputStream> INPUT_STREAM_INSTANCE = new DSSCertificateTokenSecurityFactory<InputStream>(){

        @Override
        protected String toString(InputStream input) {
            return "InputStream";
        }

        @Override
        public CertificateToken build(InputStream input) {
            // NOTE: possible to process InputStream only once
            CertificateToken certificateToken = buildWithPrimarySecurityProvider(input);
            if (certificateToken != null) {
                return certificateToken;
            }
            throw new DSSException(String.format("Unable to load %s for the given certificate %s. " +
                    "All security providers have failed. More detail in debug mode.", getFactoryClassName(), input.getClass().getSimpleName()));
        }

        @Override
        protected CertificateToken buildWithProvider(InputStream input, Provider securityProvider) throws Exception {
            List<CertificateToken> certificateTokens = DSSP7CCertificatesSecurityFactory.INPUT_STREAM_INSTANCE.buildWithProvider(input, securityProvider);
            if (certificateTokens == null || certificateTokens.size() == 0) {
                throw new DSSException("Could not parse certificate.");
            } else if (certificateTokens.size() == 1) {
                return certificateTokens.get(0);
            } else {
                throw new DSSException(String.format("'%s' certificate obtained instead of one. " +
                        "Please provide a single certificate to load or use a different method to read multiple certificates.",
                        certificateTokens.size()));
            }
        }

    };

    /**
     * This class builds a CertificateToken based on the provided byte array
     */
    public static final DSSCertificateTokenSecurityFactory<byte[]> BINARY_INSTANCE = new DSSCertificateTokenSecurityFactory<byte[]>(){

        @Override
        protected String toString(byte[] input) {
            return input != null ? Utils.toBase64(input) : null;
        }

        @Override
        protected CertificateToken buildWithProvider(byte[] input, Provider securityProvider) throws Exception {
            return INPUT_STREAM_INSTANCE.buildWithProvider(new ByteArrayInputStream(input), securityProvider);
        }

    };

    /**
     * This class builds a CertificateToken based on the provided File
     */
    public static final DSSCertificateTokenSecurityFactory<File> FILE_INSTANCE = new DSSCertificateTokenSecurityFactory<File>(){

        @Override
        protected String toString(File input) {
            return input != null ? input.getPath() : null;
        }

        @Override
        protected CertificateToken buildWithProvider(File input, Provider securityProvider) throws Exception{
            try {
                return INPUT_STREAM_INSTANCE.buildWithProvider(Files.newInputStream(input.toPath()), securityProvider);
            } catch (IOException e) {
                throw new DSSException(String.format("Unable to find a file '%s' : %s", input.getPath(), e.getMessage()), e);
            }
        }

    };

    /**
     * This class builds a CertificateToken based on the provided X509CertificateHolder
     */
    public static final DSSCertificateTokenSecurityFactory<X509CertificateHolder> X509_CERTIFICATE_HOLDER_INSTANCE =
            new DSSCertificateTokenSecurityFactory<X509CertificateHolder>(){

        @Override
        protected String toString(X509CertificateHolder input) {
            try {
                return input != null ? Utils.toBase64(input.getEncoded()) : null;
            } catch (IOException e) {
                LOG.warn("Unable to read encoded binaries of X509CertificateHolder : {}", e.getMessage(), e);
                return null;
            }
        }

        @Override
        protected CertificateToken buildWithProvider(X509CertificateHolder input, Provider securityProvider)  throws Exception{
            JcaX509CertificateConverter jcaX509CertificateConverter = new JcaX509CertificateConverter();
            jcaX509CertificateConverter.setProvider(securityProvider);
            X509Certificate certificate = jcaX509CertificateConverter.getCertificate(input);
            return new CertificateToken(certificate);
        }

    };

    @Override
    protected String getFactoryClassName() {
        return CertificateFactory.class.getSimpleName();
    }

}
