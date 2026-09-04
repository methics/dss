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

import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.utils.Utils;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Objects;

/**
 * This factory is used to create a {@code java.security.PublicKey} from various input types,
 * such as InputStream or byte array.
 *
 * @param <I> input type
 */
public abstract class DSSPublicKeySecurityFactory<I> extends DSSSecurityFactory<I, PublicKey> {

    private static final Logger LOG = LoggerFactory.getLogger(DSSPublicKeySecurityFactory.class);

    /**
     * Default constructor
     */
    private DSSPublicKeySecurityFactory() {
        // empty
    }

    /**
     * Builds a PublicKey from an InputStream.
     * <p>
     * NOTE: Unlike other implementations, this class uses only one security provider because
     * of inability to process InputStream multiple times.
     */
    public static final DSSPublicKeySecurityFactory<InputStream> INPUT_STREAM_INSTANCE =
            new DSSPublicKeySecurityFactory<InputStream>() {

                @Override
                protected String toString(InputStream input) {
                    return "InputStream";
                }

                @Override
                public PublicKey build(InputStream input) {
                    Objects.requireNonNull(input, "Input cannot be null");
                    PublicKey publicKey = buildWithPrimarySecurityProvider(input);
                    if (publicKey != null) {
                        return publicKey;
                    }
                    throw new DSSException(String.format(
                            "Unable to load %s for the given %s. " +
                                    "All security providers have failed. More detail in debug mode.",
                            getFactoryClassName(), input.getClass().getSimpleName()));
                }

                @Override
                protected PublicKey buildWithProvider(InputStream input, Provider securityProvider) throws Exception {
                    SubjectPublicKeyInfo spki = SubjectPublicKeyInfo.getInstance(Utils.toByteArray(input));
                    return SUBJECT_PUBLIC_KEY_INFO_INSTANCE.buildWithProvider(spki, securityProvider);
                }

            };

    /**
     * Builds a PublicKey from a byte array.
     */
    public static final DSSPublicKeySecurityFactory<byte[]> BINARY_INSTANCE =
            new DSSPublicKeySecurityFactory<byte[]>() {

                @Override
                protected String toString(byte[] input) {
                    return input != null ? Utils.toBase64(input) : null;
                }

                @Override
                protected PublicKey buildWithProvider(byte[] input, Provider securityProvider) throws Exception {
                    return INPUT_STREAM_INSTANCE.buildWithProvider(
                            new ByteArrayInputStream(input), securityProvider);
                }

            };

    /**
     * Builds a PublicKey from SubjectPublicKeyInfo.
     */
    public static final DSSPublicKeySecurityFactory<SubjectPublicKeyInfo> SUBJECT_PUBLIC_KEY_INFO_INSTANCE =
            new DSSPublicKeySecurityFactory<SubjectPublicKeyInfo>() {

                @Override
                protected String toString(SubjectPublicKeyInfo input) {
                    try {
                        return input != null ? Utils.toBase64(input.getEncoded()) : null;
                    } catch (Exception e) {
                        LOG.warn("Unable to read encoded binaries of SubjectPublicKeyInfo : {}", e.getMessage(), e);
                        return null;
                    }
                }

                @Override
                protected PublicKey buildWithProvider(SubjectPublicKeyInfo input, Provider securityProvider)
                        throws Exception {

                    EncryptionAlgorithm encryptionAlgorithm = getEncryptionAlgorithm(input.getAlgorithm().getAlgorithm());
                    String algorithm = encryptionAlgorithm.getName();

                    KeyFactory keyFactory = KeyFactory.getInstance(algorithm, securityProvider);

                    X509EncodedKeySpec keySpec = new X509EncodedKeySpec(input.getEncoded());

                    return keyFactory.generatePublic(keySpec);
                }

                private EncryptionAlgorithm getEncryptionAlgorithm(ASN1ObjectIdentifier asn1ObjectIdentifier) {
                    String oid = asn1ObjectIdentifier.getId();
                    try {
                        return EncryptionAlgorithm.forOID(oid);
                    } catch (IllegalArgumentException e) {
                        // purposely empty
                    }
                    // fallback to identify via signature algorithm
                    final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.forOID(oid);
                    return signatureAlgorithm.getEncryptionAlgorithm();
                }

            };

    @Override
    protected String getFactoryClassName() {
        return KeyFactory.class.getSimpleName();
    }

}