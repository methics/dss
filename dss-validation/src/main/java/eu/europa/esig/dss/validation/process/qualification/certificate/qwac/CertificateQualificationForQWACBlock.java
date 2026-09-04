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
package eu.europa.esig.dss.validation.process.qualification.certificate.qwac;

import eu.europa.esig.dss.detailedreport.jaxb.XmlConclusion;
import eu.europa.esig.dss.detailedreport.jaxb.XmlTLAnalysis;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.TrustServiceWrapper;
import eu.europa.esig.dss.enumerations.ValidationTime;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.validation.process.qualification.certificate.CertQualificationAtTimeBlock;
import eu.europa.esig.dss.validation.process.qualification.certificate.CertificateQualificationBlock;

import java.util.Date;
import java.util.List;

/**
 * Performs qualification determination for a QWAC certificate according to the TS 119 615 process.
 *
 */
public class CertificateQualificationForQWACBlock extends CertificateQualificationBlock {

    /**
     * Default constructor
     *
     * @param i18nProvider             {@link I18nProvider}
     * @param buildingBlocksConclusion {@link XmlConclusion} of BBB for the validating certificate
     * @param validationTime           {@link Date} validation time
     * @param signingCertificate       {@link CertificateWrapper} to be validated
     * @param tlAnalysis               a list of {@link XmlTLAnalysis}
     */
    public CertificateQualificationForQWACBlock(I18nProvider i18nProvider, XmlConclusion buildingBlocksConclusion,
                                                Date validationTime, CertificateWrapper signingCertificate, List<XmlTLAnalysis> tlAnalysis) {
        super(i18nProvider, buildingBlocksConclusion, validationTime, signingCertificate, tlAnalysis);
    }

    @Override
    protected CertQualificationAtTimeBlock getCertQualificationAtIssuanceTimeBlock(List<TrustServiceWrapper> acceptableServices) {
        return new CertQualificationAtTimeForQWACBlock(i18nProvider, ValidationTime.CERTIFICATE_ISSUANCE_TIME, signingCertificate, acceptableServices);
    }

    @Override
    protected CertQualificationAtTimeBlock getCertQualificationAtValidationTimeBlock(List<TrustServiceWrapper> acceptableServices) {
        return new CertQualificationAtTimeForQWACBlock(i18nProvider, ValidationTime.VALIDATION_TIME, validationTime, signingCertificate, acceptableServices);
    }

}
