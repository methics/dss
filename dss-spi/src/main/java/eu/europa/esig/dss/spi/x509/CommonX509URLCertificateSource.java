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
package eu.europa.esig.dss.spi.x509;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.DataLoader;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * The common implementation of {@code X509URLCertificateSource} retrieving X.509 certificates by the given URI.
 * This class is used for validation of JAdES and CB-AdES signatures.
 * <p>
 * This class provides the following workflows:
 * - Provide a mapping between URL and certificate pairs using {@code #addCertificate} and/or {@code #addCertificates} methods; or
 * - Instantiate the class either using a constructor with a DataLoader to access the certificates in the runtime or
 *   by setting the DataLoader using the {@code #setDataLoader} method; or
 * - By using combination of both.
 * 
 */
public class CommonX509URLCertificateSource extends CommonCertificateSource implements X509URLCertificateSource {

    private static final long serialVersionUID = 5423873125786850353L;

    private static final Logger LOG = LoggerFactory.getLogger(CommonX509URLCertificateSource.class);

    /** DataLoader used to access 'x5u' certificates */
    private DataLoader dataLoader;

    /** Map of uris and related certificate tokens */
    private Map<String, Collection<CertificateToken>> mapByUri = new HashMap<>();

    /**
     * Constructor to instantiate a pre-configured certificate source
     */
    public CommonX509URLCertificateSource() {
        // empty
    }

    /**
     * Constructor to create an instance of the class with a {@code dataLoader} to access
     * the certificates from the corresponding 'x5u' location in the runtime
     *
     * @param dataLoader {@link DataLoader}
     */
    public CommonX509URLCertificateSource(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    /**
     * Sets the DataLoader to access the certificates from the corresponding 'x5u' location in the runtime
     *
     * @param dataLoader {@link DataLoader}
     */
    public void setDataLoader(DataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    @Override
    public CertificateToken addCertificate(CertificateToken certificateToAdd) {
        throw new UnsupportedOperationException("#addCertificate(certificateToAdd) method is not supported in CommonX509URLCertificateSource! " +
                "Please use #addCertificate(uri, certificateToAdd) or #addCertificates(uri, certificatesToAdd) methods.");
    }

    /**
     * Adds a certificate for a given 'x5u' URL (JWS/JAdES)
     *
     * @param uri         the used URI in the JWS/JAdES
     * @param certificate the related certificate token
     * @return the certificate
     */
    public CertificateToken addCertificate(String uri, CertificateToken certificate) {
        CertificateToken addedCertificate = super.addCertificate(certificate);
        Collection<CertificateToken> certificateTokens = mapByUri.get(uri);
        if (Utils.isCollectionEmpty(certificateTokens)) {
            certificateTokens = new ArrayList<>();
            mapByUri.put(uri, certificateTokens);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("URI {} is already known, the certificate will be added to the existing collection.", uri);
        }
        certificateTokens.add(certificate);
        return addedCertificate;
    }

    /**
     * Adds a collection of certificates for a given 'x5u' URL (JWS/JAdES)
     *
     * @param uri          the used URI in the JWS/JAdES
     * @param certificates a collection of {@link CertificateToken}s
     * @return the certificate
     */
    public Collection<CertificateToken> addCertificates(String uri, Collection<CertificateToken> certificates) {
        Collection<CertificateToken> certificateTokens = mapByUri.get(uri);
        if (Utils.isCollectionEmpty(certificateTokens)) {
            certificateTokens = new ArrayList<>();
            mapByUri.put(uri, certificateTokens);
        } else if (LOG.isDebugEnabled()) {
            LOG.debug("URI {} is already known, the certificates will be added to the existing collection.", uri);
        }
        for (CertificateToken certificate : certificates) {
            CertificateToken addedCertificate = super.addCertificate(certificate);
            certificateTokens.add(addedCertificate);
        }
        return certificateTokens;
    }

    @Override
    public Collection<CertificateToken> getCertificatesByUrl(String uri) {
        Collection<CertificateToken> certificates = mapByUri.get(uri);
        if (Utils.isCollectionNotEmpty(certificates)) {
            LOG.debug("Certificates are known for 'x5u' value '{}'. Return existing values.", uri);
            return certificates;
        }
        certificates = loadCertificates(uri);
        if (Utils.isCollectionNotEmpty(certificates)) {
            return certificates;
        }
        return Collections.emptyList();
    }

    /**
     * Loads the certificates using a {@code DataLoader} from the given {@code url}
     *
     * @param url {@link String} to load certificates from
     * @return collection of {@link CertificateToken}s
     */
    protected Collection<CertificateToken> loadCertificates(String url) {
        if (dataLoader != null) {
            try {
                LOG.trace("--> X509URLCertificateSource queried for {}", url);
                byte[] content = dataLoader.get(url);
                if (content != null) {
                    LOG.debug("Content obtained from the 'x5u' protected header with value '{}'", url);
                    return loadCertificates(content);
                } else {
                    LOG.warn("No content has been extracted from the 'x5u' protected header with value '{}'", url);
                }
            } catch (Exception e) {
                String errorMessage = "Unable to load certificates from 'x5u' protected header with value '{}' : {}";
                if (LOG.isDebugEnabled()) {
                    LOG.warn(errorMessage, url, e.getMessage(), e);
                } else {
                    LOG.warn(errorMessage, url, e.getMessage());
                }
            }

        } else {
            LOG.debug("No DataLoader is configured within the CommonX509URLCertificateSource.");
        }
        return Collections.emptyList();
    }

    /**
     * Loads certificates from the obtained {@code content}
     *
     * @param content representing the certificates (implementation specific)
     * @return collection of {@link CertificateToken}s
     */
    protected Collection<CertificateToken> loadCertificates(byte[] content) {
        try {
            return DSSUtils.loadCertificateFromP7c(content);
        } catch (Exception e) {
            throw new DSSException(String.format("Unable to load certificates : %s", e.getMessage()), e);
        }
    }

    @Override
    public Set<CertificateToken> findTokensFromCertRef(CertificateRef certificateRef) {
        final Set<CertificateToken> certificates = super.findTokensFromCertRef(certificateRef);
        if (Utils.isStringNotEmpty(certificateRef.getX509Url())) {
            Collection<CertificateToken> x509UrlCertificates = mapByUri.get(certificateRef.getX509Url());
            if (Utils.isCollectionNotEmpty(x509UrlCertificates)) {
                certificates.addAll(x509UrlCertificates);
            }
        }
        return certificates;
    }

    @Override
    protected void reset() {
        super.reset();
        mapByUri = new HashMap<>();
    }
    
}
