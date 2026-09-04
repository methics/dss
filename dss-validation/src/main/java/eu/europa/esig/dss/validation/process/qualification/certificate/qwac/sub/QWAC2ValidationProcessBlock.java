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
package eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub;

import eu.europa.esig.dss.detailedreport.jaxb.XmlCertificateQualificationProcess;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConclusion;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.enumerations.QWACProfile;
import eu.europa.esig.dss.i18n.I18nProvider;

import java.util.Date;

/**
 * Performs validation of the 2-QWAC profile as per ETSI TS 119 411-5 part "4.2 2-QWAC Approach"
 *
 */
public class QWAC2ValidationProcessBlock extends AbstractQWACValidationProcessBlock {

    /**
     * Common constructor
     *
     * @param i18nProvider the access to translations
     * @param validationTime {@link Date} used to perform the validation
     * @param certificate {@link CertificateWrapper} representing a TLS certificate to be validated
     * @param buildingBlocksConclusion {@link XmlConclusion}
     * @param certificateQualification {@link XmlCertificateQualificationProcess}
     * @param websiteUrl {@link String}
     */
    public QWAC2ValidationProcessBlock(final I18nProvider i18nProvider, final Date validationTime,
                                       final CertificateWrapper certificate, final XmlConclusion buildingBlocksConclusion,
                                       final XmlCertificateQualificationProcess certificateQualification, final String websiteUrl) {
        super(i18nProvider, validationTime, certificate, buildingBlocksConclusion, certificateQualification, websiteUrl);
    }

    @Override
    public QWACProfile getQWACProfile() {
        return QWACProfile.QWAC_2;
    }

}
