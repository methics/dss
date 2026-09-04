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

import eu.europa.esig.dss.detailedreport.jaxb.XmlConclusion;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationQWACProcess;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.QWACProfile;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.validation.process.Chain;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;
import eu.europa.esig.dss.validation.process.qualification.certificate.AcceptableBuildingBlockConclusionCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.QWACDomainNameCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.TLSCertificateBindingPresentInSignatureCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.TLSCertificateBindingQWACCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.TLSCertificateBindingSignatureExpProtectedHeaderPresentCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.TLSCertificateBindingSignatureExpiryDateCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.TLSCertificateBindingSignatureFormatCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.TLSCertificateBindingSignatureFoundCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.TLSCertificateBindingSignatureSerializationTypeCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.TLSCertificateBindingSignatureValidationResultCheck;
import eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks.TLSCertificateBindingUrlPresentCheck;

/**
 * This class performs validation process of a TLS certificate on whether it is supported by a 2-QWAC certificate
 * (through the TLS Certificate Binding mechanism).
 *
 */
public class TLSCertificateSupportedByQWAC2ValidationProcessBlock extends Chain<XmlValidationQWACProcess> {

    /** Diagnostic data */
    private final DiagnosticData diagnosticData;

    /** The certificate to determine qualification for */
    private final CertificateWrapper tlsCertificate;

    /** Certificate's BasicBuildingBlock's conclusion */
    private final XmlConclusion tlsCertificateBasicValidationConclusion;

    /** Basic Validation conclusion of the TLS Certificate Binding signature */
    private final XmlConclusion bindingSignatureBasicValidationConclusion;

    /** QWAC profile of the binding certificate */
    private final QWACProfile bindingCertificateProfile;

    /** URL of the website to validate the QWAC certificate against */
    private final String websiteUrl;

    /**
     * Common constructor
     *
     * @param i18nProvider the access to translations
     * @param diagnosticData {@link DiagnosticData} containing the validation information data
     * @param tlsCertificate {@link CertificateWrapper} representing a TLS certificate to be validated
     * @param tlsCertificateBasicValidationConclusion {@link XmlConclusion} of the TLS/SSL certificate validation process
     * @param bindingSignatureBasicValidationConclusion {@link XmlConclusion} of the TLS/SSL certificate binding signature validation process
     * @param bindingCertificateProfile {@link QWACProfile}
     * @param websiteUrl {@link String}
     */
    public TLSCertificateSupportedByQWAC2ValidationProcessBlock(
            final I18nProvider i18nProvider, final DiagnosticData diagnosticData, final CertificateWrapper tlsCertificate,
            final XmlConclusion tlsCertificateBasicValidationConclusion, final XmlConclusion bindingSignatureBasicValidationConclusion,
            final QWACProfile bindingCertificateProfile, final String websiteUrl) {
        super(i18nProvider, new XmlValidationQWACProcess());

        result.setId(tlsCertificate.getId());

        this.tlsCertificate = tlsCertificate;
        this.diagnosticData = diagnosticData;
        this.tlsCertificateBasicValidationConclusion = tlsCertificateBasicValidationConclusion;
        this.bindingSignatureBasicValidationConclusion = bindingSignatureBasicValidationConclusion;
        this.bindingCertificateProfile = bindingCertificateProfile;
        this.websiteUrl = websiteUrl;
    }

    @Override
    protected String buildChainTitle() {
        MessageTag message = MessageTag.QWAC_VALIDATION_PROFILE;
        MessageTag param = ValidationProcessUtils.getQWACValidationMessageTag(getQWACProfile());
        return i18nProvider.getMessage(message, param);
    }

    /**
     * Gets the current QWAC profile
     *
     * @return {@link QWACProfile}
     */
    public QWACProfile getQWACProfile() {
        return QWACProfile.TLS_BY_QWAC_2;
    }

    @Override
    protected void initChain() {

        /*
         * 6.2.2 Usage of 2-QWACs with TLS Certificate Binding (i.e. "Approach #2")
         *
         * When using 2-QWACs with secure TLS connections to websites, web browsers shall:
         *
         * 1) Establish a secure TLS connection with the site using the web browsers' procedures and configuration, and
         * evaluate the presented TLS Certificate with the security requirements of the web browser vendor and their
         * policies for web security, domain authentication and the encryption of web traffic as outlined in Recital 65 of
         * the Regulation (EU) 2024/1183 [i.3].
         * - If this step fails, the procedure finishes negatively.
         */

        ChainItem<XmlValidationQWACProcess> item = firstItem = isAcceptableBuildingBlockConclusion();

        item = item.setNextItem(qwacDomainName());

        /*
         * 2) Examine the HTTP headers included in any main frame navigation response from the server (relating to
         * navigation by the web browser to the address as displayed in the address bar) for a HTTP 'Link' response
         * header (as defined in IETF RFC 8288 [6]) with a rel value of tls-certificate-binding.
         * - If this step is absent, the procedure finishes negatively.
         */
        item = item.setNextItem(tlsCertificateBindingUrlPresent());

        /*
         * 3) Fetch the resource located at this link and evaluate it for conformance with the profile laid out in Annex B.
         * - If this step fails or the resource is non-conformant, the procedure finishes negatively.
         */
        item = item.setNextItem(tlsCertificateBindingSignatureFound());

        if (diagnosticData.getTLSCertificateBindingSignature() != null) {

            item = item.setNextItem(tlsCertificateBindingSignatureFormat());

            item = item.setNextItem(tlsCertificateBindingSignatureSerializationType());

            // TODO : no verification of present headers -> ETSI TS 119 411-5 v1.2.1 has a sigD/crit dictionaries conflict

            // NOTE: header requirements are to be indirectly checked through the validation policy on signature validation

            item = item.setNextItem(tlsCertificateBindingSignatureExpProtectedHeaderPresent());

            item = item.setNextItem(tlsCertificateBindingSignatureExpiryDate());

            /*
             * 4) Examine the QWAC presented in the binding with the validation criteria laid out in clause 6.1.2 of the present document.
             * - If this step fails or the certificate is not considered a '2-QWAC' under clause 6.1.2 of the present
             * document, the procedure finishes negatively.
             */
            item = item.setNextItem(tlsCertificateBindingIsQWAC());

            /*
             * 5) Validate the JAdES signature on the TLS Certificate binding according to ETSI EN 319 102-1 [2].
             * - If this step fails or the TLS Certificate binding is not considered valid, the procedure finishes negatively
             */
            item = item.setNextItem(tlsCertificateBindingSignatureValid());

            /*
             * 6) Validate that the TLS Certificate used to establish this connection in Step 1 appears in the list contained
             * in the validated binding.
             * - If this step fails or the list does not contain the certificate, the procedure finishes negatively.
             */
            item = item.setNextItem(tlsCertificateBindingCertificateAppearInSignature());

        }

    }

    private ChainItem<XmlValidationQWACProcess> isAcceptableBuildingBlockConclusion() {
        return new AcceptableBuildingBlockConclusionCheck<>(i18nProvider, result, tlsCertificateBasicValidationConclusion, getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> qwacDomainName() {
        return new QWACDomainNameCheck(i18nProvider, result, tlsCertificate, websiteUrl, getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> tlsCertificateBindingUrlPresent() {
        return new TLSCertificateBindingUrlPresentCheck(i18nProvider, result,
                diagnosticData.getTLSCertificateBindingUrl(), getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> tlsCertificateBindingSignatureFound() {
        return new TLSCertificateBindingSignatureFoundCheck(i18nProvider, result,
                diagnosticData.getTLSCertificateBindingSignature(), getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> tlsCertificateBindingSignatureFormat() {
        return new TLSCertificateBindingSignatureFormatCheck(i18nProvider, result,
                diagnosticData.getTLSCertificateBindingSignature(), getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> tlsCertificateBindingSignatureSerializationType() {
        return new TLSCertificateBindingSignatureSerializationTypeCheck(i18nProvider, result,
                diagnosticData.getTLSCertificateBindingSignature(), getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> tlsCertificateBindingSignatureExpProtectedHeaderPresent() {
        return new TLSCertificateBindingSignatureExpProtectedHeaderPresentCheck(i18nProvider, result,
                diagnosticData.getTLSCertificateBindingSignature(), getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> tlsCertificateBindingSignatureExpiryDate() {
        return new TLSCertificateBindingSignatureExpiryDateCheck(i18nProvider, result, diagnosticData.getValidationDate(),
                diagnosticData.getTLSCertificateBindingSignature(), diagnosticData.getUsedCertificates(), getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> tlsCertificateBindingIsQWAC() {
        return new TLSCertificateBindingQWACCheck(i18nProvider, result, bindingCertificateProfile, getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> tlsCertificateBindingSignatureValid() {
        return new TLSCertificateBindingSignatureValidationResultCheck(i18nProvider, result,
                bindingSignatureBasicValidationConclusion, getFailLevelRule());
    }

    private ChainItem<XmlValidationQWACProcess> tlsCertificateBindingCertificateAppearInSignature() {
        return new TLSCertificateBindingPresentInSignatureCheck(i18nProvider, result,
                tlsCertificate, diagnosticData.getTLSCertificateBindingSignature(), getFailLevelRule());
    }

}
