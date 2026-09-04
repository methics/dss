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
package eu.europa.esig.dss.validation.process.qualification.signature.qwac;

import eu.europa.esig.dss.detailedreport.jaxb.XmlBasicBuildingBlocks;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCertificateQualificationProcess;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConclusion;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraintsConclusionWithProofOfExistence;
import eu.europa.esig.dss.detailedreport.jaxb.XmlQWACProcess;
import eu.europa.esig.dss.detailedreport.jaxb.XmlTLAnalysis;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationCertificateQualification;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.TrustServiceWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.ValidationTime;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.qualification.certificate.CertQualificationAtTimeBlock;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.CertQualificationAtTimeForQWACBlock;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.QWACForTLSBindingCertificateValidationBlock;
import eu.europa.esig.dss.validation.process.qualification.signature.SignatureQualificationBlock;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * This class executes qualification determination process for a TLS/SSL Certificate Binding signature
 *
 */
public class TLSBindingSignatureQualificationBlock extends SignatureQualificationBlock {

    /** Map of Basic Building Blocks */
    private final Map<String, XmlBasicBuildingBlocks> bbbs;

    /** TLS Certificate Binding signature */
    private final SignatureWrapper bindingSignature;

    /** URL of the website to validate the QWAC certificate against */
    private final String websiteUrl;

    /**
     * Default constructor
     *
     * @param i18nProvider         {@link I18nProvider}
     * @param bbbs                 a map of {@link XmlBasicBuildingBlocks}
     * @param etsi319102validation {@link XmlConstraintsConclusionWithProofOfExistence}
     *                             result of signature validation process as in EN 319 102-1
     * @param bindingSignature     {@link SignatureWrapper} TLS Certificate Binding signature
     * @param tlAnalysis           a list of performed {@link XmlTLAnalysis}
     * @param websiteUrl           {@link String}
     */
    public TLSBindingSignatureQualificationBlock(final I18nProvider i18nProvider, final Map<String, XmlBasicBuildingBlocks> bbbs,
             final XmlConstraintsConclusionWithProofOfExistence etsi319102validation, final SignatureWrapper bindingSignature,
             final List<XmlTLAnalysis> tlAnalysis, final String websiteUrl) {
        super(i18nProvider, etsi319102validation, bindingSignature.getSigningCertificate(), tlAnalysis);

        this.bbbs = bbbs;
        this.bindingSignature = bindingSignature;
        this.websiteUrl = websiteUrl;
    }

    @Override
    protected void initChain() {
        super.initChain();

        QWACForTLSBindingCertificateValidationBlock qwacForTLSBindingValidationBlock = new QWACForTLSBindingCertificateValidationBlock(
                i18nProvider, bestSignatureTime, bindingSignature, signingCertificate, bbbs, getCertificateQualification(), websiteUrl);
        XmlQWACProcess qwacProcess = qwacForTLSBindingValidationBlock.execute();
        result.setQWACProcess(qwacProcess);
    }

    private XmlCertificateQualificationProcess getCertificateQualification() {
        XmlCertificateQualificationProcess xmlCertificateQualificationProcess = new XmlCertificateQualificationProcess();
        xmlCertificateQualificationProcess.getValidationCertificateQualification().addAll(result.getValidationCertificateQualification());
        if (Utils.isCollectionEmpty(result.getValidationCertificateQualification())) {
            xmlCertificateQualificationProcess.setConclusion(getConclusion(Indication.FAILED));
            return xmlCertificateQualificationProcess;
        }

        for (XmlValidationCertificateQualification certificateQualificationAtTime : result.getValidationCertificateQualification()) {
            if (!isValid(certificateQualificationAtTime)) {
                result.setConclusion(getConclusion(certificateQualificationAtTime.getConclusion()));
                return xmlCertificateQualificationProcess;
            }
        }
        xmlCertificateQualificationProcess.setConclusion(getConclusion(Indication.PASSED));
        return xmlCertificateQualificationProcess;
    }

    private XmlConclusion getConclusion(Indication indication) {
        XmlConclusion xmlConclusion = new XmlConclusion();
        xmlConclusion.setIndication(indication);
        return xmlConclusion;
    }

    private XmlConclusion getConclusion(XmlConclusion conclusion) {
        XmlConclusion xmlConclusion = new XmlConclusion();
        xmlConclusion.setIndication(conclusion.getIndication());
        xmlConclusion.setSubIndication(conclusion.getSubIndication());
        xmlConclusion.getErrors().addAll(new ArrayList<>(conclusion.getErrors()));
        xmlConclusion.getWarnings().addAll(new ArrayList<>(conclusion.getWarnings()));
        xmlConclusion.getInfos().addAll(new ArrayList<>(conclusion.getInfos()));
        return xmlConclusion;
    }

    @Override
    protected CertQualificationAtTimeBlock getCertQualificationAtIssuanceTimeBlock(List<TrustServiceWrapper> acceptableServices) {
        return new CertQualificationAtTimeForQWACBlock(i18nProvider, ValidationTime.CERTIFICATE_ISSUANCE_TIME, signingCertificate, acceptableServices);
    }

    @Override
    protected CertQualificationAtTimeBlock getCertQualificationAtSigningTimeBlock(List<TrustServiceWrapper> acceptableServices, Date signingTime) {
        return new CertQualificationAtTimeForQWACBlock(i18nProvider, ValidationTime.VALIDATION_TIME, signingTime, signingCertificate, acceptableServices);
    }

}
