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
package eu.europa.esig.dss.spi.lote;

import eu.europa.esig.dss.enumerations.CertificateSourceType;
import eu.europa.esig.dss.model.identifier.EntityIdentifier;
import eu.europa.esig.dss.model.lote.LoTEValidationJobSummary;
import eu.europa.esig.dss.model.lote.ServiceStatusAndInformationExtensions;
import eu.europa.esig.dss.model.lote.TrustedProperties;
import eu.europa.esig.dss.model.lote.TrustedPropertiesCertificateSource;
import eu.europa.esig.dss.model.tsl.CertificateTrustTime;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.x509.CommonTrustedCertificateSource;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Certificate source built based on trusted entities
 *
 */
public class TrustedEntitiesCertificateSource extends CommonTrustedCertificateSource implements TrustedPropertiesCertificateSource {

    private static final long serialVersionUID = 1928774883535280047L;

    private static final Logger LOG = LoggerFactory.getLogger(TrustedEntitiesCertificateSource.class);

    /** The TL Validation job summary */
    private LoTEValidationJobSummary summary;

    /** The map of trust properties by EntityIdentifier (public keys) */
    private Map<EntityIdentifier, List<TrustedProperties>> trustPropertiesByEntity = new HashMap<>();

    /** The map of trust time periods by EntityIdentifier */
    private Map<EntityIdentifier, List<CertificateTrustTime>> trustTimeByEntity = new HashMap<>();

    /**
     * The default constructor.
     */
    public TrustedEntitiesCertificateSource() {
        super();
    }

    /**
     * Gets LoTE Validation job summary
     *
     * @return {@link LoTEValidationJobSummary}
     */
    public LoTEValidationJobSummary getSummary() {
        return summary;
    }

    @Override
    public void setSummary(LoTEValidationJobSummary summary) {
        this.summary = summary;
    }

    @Override
    public CertificateSourceType getCertificateSourceType() {
        return CertificateSourceType.TRUSTED_ENTITIES;
    }

    /**
     * This method is not applicable for this kind of certificate source. You should
     * use {@link #setTrustedPropertiesByCertificates}
     *
     * @param certificate
     *                    the certificate you have to trust
     * @return the corresponding certificate token
     */
    @Override
    public CertificateToken addCertificate(CertificateToken certificate) {
        throw new UnsupportedOperationException("Cannot directly add certificate to a TrustedListsCertificateSource");
    }

    @Override
    public synchronized void setTrustedPropertiesByCertificates(final Map<CertificateToken, List<TrustedProperties>> trustPropertiesByCerts) {
        Objects.requireNonNull(trustPropertiesByCerts, "TrustedPropertiesByCerts cannot be null!");
        this.trustPropertiesByEntity = new HashMap<>(); // reinit the map
        super.reset();
        trustPropertiesByCerts.forEach(this::addCertificate);
    }

    private void addCertificate(CertificateToken certificateToken, List<TrustedProperties> trustPropertiesList) {
        super.addCertificate(certificateToken);
        Objects.requireNonNull(trustPropertiesList, "TrustedPropertiesList must be filled");

        EntityIdentifier entityKey = certificateToken.getEntityKey();
        List<TrustedProperties> list = trustPropertiesByEntity.computeIfAbsent(entityKey, k -> new ArrayList<>());
        for (TrustedProperties trustProperties : trustPropertiesList) {
            if (!list.contains(trustProperties)) {
                list.add(trustProperties);
            }
        }
    }

    @Override
    public List<TrustedProperties> getTrustedProperties(CertificateToken token) {
        List<TrustedProperties> currentTrustedProperties = trustPropertiesByEntity.get(token.getEntityKey());
        if (currentTrustedProperties != null) {
            return currentTrustedProperties;
        } else {
            return Collections.emptyList();
        }
    }

    @Override
    public synchronized void setTrustedTimeByCertificates(Map<CertificateToken, List<CertificateTrustTime>> trustTimeByCertificate) {
        Objects.requireNonNull(trustTimeByCertificate, "trustTimeByCertificate cannot be null!");
        this.trustTimeByEntity = new HashMap<>(); // reinit the map
        trustTimeByCertificate.forEach(this::addCertificateTrustTimes);
    }

    private void addCertificateTrustTimes(CertificateToken certificateToken, List<CertificateTrustTime> certificateTrustTimes) {
        super.addCertificate(certificateToken);
        Objects.requireNonNull(certificateTrustTimes, "CertificateTrustTimes must be filled");

        EntityIdentifier entityKey = certificateToken.getEntityKey();
        List<CertificateTrustTime> list = trustTimeByEntity.computeIfAbsent(entityKey, k -> new ArrayList<>());
        for (CertificateTrustTime trustTime : certificateTrustTimes) {
            if (!list.contains(trustTime)) {
                list.add(trustTime);
            }
        }
    }

    @Override
    public synchronized CertificateTrustTime getTrustTime(CertificateToken token) {
        if (!super.isTrusted(token)) {
            return new CertificateTrustTime(false);
        }
        List<CertificateTrustTime> trustTimes = trustTimeByEntity.get(token.getEntityKey());
        if (Utils.isCollectionNotEmpty(trustTimes)) {
            CertificateTrustTime certificateTrustTime = null;
            for (CertificateTrustTime trustTime : trustTimes) {
                if (certificateTrustTime == null || !certificateTrustTime.isTrusted()) {
                    certificateTrustTime = trustTime;
                } else if (trustTime != null && trustTime.isTrusted()) {
                    certificateTrustTime = certificateTrustTime.getJointTrustTime(trustTime.getStartDate(), trustTime.getEndDate());
                }
            }
            return certificateTrustTime;
        } else {
            return new CertificateTrustTime(true); // no trust anchor expiration time defined
        }
    }

    @Override
    public boolean isTrustedAtTime(CertificateToken certificateToken, Date controlTime) {
        CertificateTrustTime trustTime = getTrustTime(certificateToken);
        return trustTime.isTrustedAtTime(controlTime);
    }

    @Override
    public List<String> getAlternativeOCSPUrls(CertificateToken trustAnchor) {
        return getServiceSupplyPoints(trustAnchor, "ocsp");
    }

    @Override
    public List<String> getAlternativeCRLUrls(CertificateToken trustAnchor) {
        return getServiceSupplyPoints(trustAnchor, "crl", "certificateRevocationList");
    }

    private List<String> getServiceSupplyPoints(CertificateToken trustAnchor, String... keywords) {
        List<String> urls = new ArrayList<>();
        List<TrustedProperties> trustPropertiesList = getTrustedProperties(trustAnchor);
        for (TrustedProperties trustProperties : trustPropertiesList) {
            for (ServiceStatusAndInformationExtensions statusAndInfo : trustProperties.getTrustedServices()) {
                List<String> serviceSupplyPoints = statusAndInfo.getServiceSupplyPoints();
                if (Utils.isCollectionNotEmpty(serviceSupplyPoints)) {
                    for (String serviceSupplyPoint : serviceSupplyPoints) {
                        for (String keyword : keywords) {
                            if (serviceSupplyPoint.contains(keyword)) {
                                LOG.debug("ServiceSupplyPoints (TL) found for keyword '{}'", keyword);
                                urls.add(serviceSupplyPoint);
                            }
                        }
                    }
                }
            }
        }
        return urls;
    }

    @Override
    public boolean isTrusted(CertificateToken certificateToken) {
        if (super.isTrusted(certificateToken)) {
            CertificateTrustTime trustTime = getTrustTime(certificateToken);
            return trustTime == null || trustTime.isTrusted();
        }
        return false;
    }

    /**
     * Gets the number of trusted entity keys (public key + subject name)
     *
     * @return the number of trusted entity keys (public key + subject name)
     */
    public int getNumberOfTrustedEntityKeys() {
        return trustPropertiesByEntity.size();
    }

}
