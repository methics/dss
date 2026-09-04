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
package eu.europa.esig.dss.validation.qwac;

import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains utility methods for performing QWAC validation process
 *
 */
public class QWACUtils {

    private static final Logger LOG = LoggerFactory.getLogger(QWACUtils.class);

    /** Represents a "Link" response header */
    private static final String HEADER_LINK = "Link";

    /** Represents a "rel" (relation type) attribute of the "Link" response header */
    private static final String RELATION_TYPE = "rel";

    /** Represents "tls-certificate-binding" value of "rel" attribute identifying a Link to a TLS/SSL binding signature file */
    private static final String TLS_CERTIFICATE_BINDING = "tls-certificate-binding";

    /**
     * Default constructor
     */
    private QWACUtils() {
        // empty
    }

    /**
     * Loops over the {@code headers} and returns the tls-certificate-binding URL value from a "Link" header if found.
     * If no matching value found, the method returns NULL.
     *
     * @param headers a map of header name and values
     * @return {@link String} TSL Certificate Binding URL
     */
    public static String getTLSCertificateBindingUrl(Map<String, List<String>> headers) {
        if (headers == null) {
            return null;
        }

        List<String> headerValues = headers.get(HEADER_LINK);
        if (headerValues == null) {
            LOG.debug("No Link header found in the obtained map of headers.");
            return null;
        }

        for (String linkHeaderValue : headerValues) {
            try {
                List<LinkHeaderParser.LinkHeader> linkHeader = new LinkHeaderParser().parse(linkHeaderValue);
                if (Utils.isCollectionNotEmpty(linkHeader)) {
                    for (LinkHeaderParser.LinkHeader singleHeaderValue : linkHeader) {
                        if (isTLSCertificateBindingRel(singleHeaderValue)) {
                            LOG.debug("'Link' header value was obtained from with value '{}'", singleHeaderValue.getUrl());
                            return singleHeaderValue.getUrl();
                        }
                    }
                }

            } catch (Exception e) {
                LOG.debug("An error occurred on processing a 'Link' header value : {}", e.getMessage(), e);
            }

        }
        LOG.debug("No Link header contains a rel value of tls-certificate-binding.");
        return null;
    }

    /**
     * Checks whether the "Link" header attributes contain a rel value of tls-certificate-binding
     *
     * @param linkHeader {@link LinkHeaderParser.LinkHeader} to check
     * @return TRUE if the "Link" attributes is for TLS Certificate Binding, FALSE otherwise
     */
    private static boolean isTLSCertificateBindingRel(LinkHeaderParser.LinkHeader linkHeader) {
        return TLS_CERTIFICATE_BINDING.equals(linkHeader.getAttributes().get(RELATION_TYPE));
    }

    /**
     * Gets TLS Binding Certificates identified from the binding signature
     *
     * @param signature {@link SignatureWrapper}
     * @param certificates a list of {@link CertificateWrapper} candidates
     * @return a list of {@link CertificateWrapper} identified TLS/SSL certificates
     */
    public static List<CertificateWrapper> getIdentifiedTLSCertificates(SignatureWrapper signature,
                                                                        List<CertificateWrapper> certificates) {
        List<CertificateWrapper> result = new ArrayList<>();
        for (XmlDigestMatcher digestMatcher : signature.getDigestMatchers()) {
            if (DigestMatcherType.SIG_D_ENTRY == digestMatcher.getType()
                    && digestMatcher.isDataFound() && digestMatcher.isDataIntact()
                    && digestMatcher.getDocumentName() != null) {
                for (CertificateWrapper certificate : certificates) {
                    if (digestMatcher.getDocumentName().equals(certificate.getId())) {
                        result.add(certificate);
                    }
                }
            }
        }
        return result;
    }

}
