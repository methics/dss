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
package eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlMessage;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationCertificateQualification;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationQWACProcess;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;

/**
 * This class verifies whether the certificate is a Qualified Certificate for WebSiteAuthentication at the given time
 *
 */
public class QualifiedCertificateForWSAAtTimeCheck extends ChainItem<XmlValidationQWACProcess> {

    /** Certificate qualification validation result at time */
    private final XmlValidationCertificateQualification certificateQualification;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlValidationQWACProcess}
     * @param certificateQualification {@link XmlValidationCertificateQualification}
     * @param constraint {@link LevelRule}
     */
    public QualifiedCertificateForWSAAtTimeCheck(final I18nProvider i18nProvider, final XmlValidationQWACProcess result,
                                                 final XmlValidationCertificateQualification certificateQualification, final LevelRule constraint) {
        super(i18nProvider, result, constraint);
        this.certificateQualification = certificateQualification;
    }

    @Override
    protected boolean process() {
        return CertificateQualification.QCERT_FOR_WSA == certificateQualification.getCertificateQualification();
    }

    @Override
    protected XmlMessage buildConstraintMessage() {
        return buildXmlMessage(MessageTag.QWAC_IS_WSA_AT_TIME,
                ValidationProcessUtils.getValidationTimeMessageTag(certificateQualification.getValidationTime()));
    }

    @Override
    protected XmlMessage buildErrorMessage() {
        return buildXmlMessage(MessageTag.QWAC_IS_WSA_AT_TIME_ANS,
                ValidationProcessUtils.getValidationTimeMessageTag(certificateQualification.getValidationTime()));
    }

    @Override
    protected String buildAdditionalInfo() {
        return i18nProvider.getMessage(MessageTag.VALIDATION_TIME,
                ValidationProcessUtils.getFormattedDate(certificateQualification.getDateTime()));
    }

    @Override
    protected Indication getFailedIndicationForConclusion() {
        return Indication.FAILED;
    }

    @Override
    protected SubIndication getFailedSubIndicationForConclusion() {
        return null;
    }

}
