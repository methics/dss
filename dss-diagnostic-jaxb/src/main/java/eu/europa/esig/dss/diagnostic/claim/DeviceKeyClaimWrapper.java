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
package eu.europa.esig.dss.diagnostic.claim;

import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlAuthorizedDataElements;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDeviceKeyClaim;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestAlgoAndValue;
import eu.europa.esig.dss.diagnostic.jaxb.XmlX509Certificate;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Provides user-friendly access to the information present within a claim representing a wallet holder's key
 *
 */
public class DeviceKeyClaimWrapper extends ClaimWrapper {

    /**
     * Default constructor
     *
     * @param wrapped {@link XmlDeviceKeyClaim}
     */
    public DeviceKeyClaimWrapper(final XmlDeviceKeyClaim wrapped) {
        super(wrapped);
    }

    /**
     * Constructor with a parent provided
     *
     * @param wrapped {@link XmlDeviceKeyClaim}
     * @param parent {@link ClaimWrapper}
     */
    public DeviceKeyClaimWrapper(final XmlDeviceKeyClaim wrapped, final ClaimWrapper parent) {
        super(wrapped, parent);
    }

    /**
     * Gets the public key provided within the claim
     *
     * @return byte array representing the public key
     */
    public byte[] getPublicKey() {
        return getWrapped().getPublicKey();
    }

    /**
     * Gets a list of certificate tokens
     *
     * @return a list of {@link CertificateWrapper}s
     */
    public List<CertificateWrapper> getCertificates() {
        List<XmlX509Certificate> x509Certificates = getWrapped().getX509Certificate();
        if (x509Certificates != null && !x509Certificates.isEmpty()) {
            return x509Certificates.stream().map(x -> new CertificateWrapper(x.getCertificate())).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    /**
     * Gets a list of certificate digests
     *
     * @return a list of {@link XmlDigestAlgoAndValue}s
     */
    public List<XmlDigestAlgoAndValue> getCertificateDigests() {
        return getWrapped().getDigestAlgoAndValue();
    }

    /**
     * Gets a list of certificate key identifiers
     *
     * @return a list of {@link String}s
     */
    public List<String> getKIDs() {
        return getWrapped().getKID();
    }

    /**
     * Gets a list of certificate access URLs
     *
     * @return a list of {@link String}s
     */
    public List<String> getX509URLs() {
        return getWrapped().getX509Url();
    }

    /**
     * Gets a list namespaces the key is authorized to sign or MAC
     *
     * @return a list of {@link String}s
     */
    public List<String> getAuthorizedNamespaces() {
        if (getWrapped().getKeyAuthorizations() != null && getWrapped().getKeyAuthorizations().getAuthorizedNamespace() != null) {
            return getWrapped().getKeyAuthorizations().getAuthorizedNamespace();
        }
        return Collections.emptyList();
    }

    /**
     * Gets a map of namespaces and corresponding data elements the key is authorized to sign or MAC
     *
     * @return a map of {@link String} namespaces of lists of {@link String} data elements
     */
    public Map<String, List<String>> getAuthorizedDataElements() {
        if (getWrapped().getKeyAuthorizations() != null && getWrapped().getKeyAuthorizations().getAuthorizedDataElements() != null) {
            final Map<String, List<String>> result = new LinkedHashMap<>();
            for (XmlAuthorizedDataElements xmlAuthorizedDataElements : getWrapped().getKeyAuthorizations().getAuthorizedDataElements()) {
                result.put(xmlAuthorizedDataElements.getNamespace(), xmlAuthorizedDataElements.getDataElement());
            }
            return result;
        }
        return Collections.emptyMap();
    }

    @Override
    public XmlDeviceKeyClaim getWrapped() {
        return (XmlDeviceKeyClaim) super.getWrapped();
    }

}
