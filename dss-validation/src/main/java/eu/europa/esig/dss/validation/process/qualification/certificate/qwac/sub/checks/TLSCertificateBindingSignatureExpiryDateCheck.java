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

import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationQWACProcess;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;
import eu.europa.esig.dss.validation.qwac.QWACUtils;

import java.util.Date;
import java.util.List;

/**
 * Verifies the validity of the TLS Certificate Binding signature
 *
 */
public class TLSCertificateBindingSignatureExpiryDateCheck extends ChainItem<XmlValidationQWACProcess> {

    /** Current validation time */
    private final Date currentTime;

    /** The TLS Certificate Binding signature */
    private final SignatureWrapper signature;

    /** List of certificates derived from the validation process */
    private final List<CertificateWrapper> certificates;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlValidationQWACProcess}
     * @param currentTime {@link Date}
     * @param signature {@link SignatureWrapper}
     * @param certificates a list of  {@link CertificateWrapper}s
     * @param constraint {@link LevelRule}
     */
    public TLSCertificateBindingSignatureExpiryDateCheck(final I18nProvider i18nProvider, final XmlValidationQWACProcess result,
            final Date currentTime, final SignatureWrapper signature, final List<CertificateWrapper> certificates,
            final LevelRule constraint) {
        super(i18nProvider, result, constraint);
        this.currentTime = currentTime;
        this.signature = signature;
        this.certificates = certificates;
    }

    @Override
    protected boolean process() {
        /*
         * The maximum effective expiry time is whichever is soonest of this field,
         * the longest-lived TLS certificate identified in the sigD member payload (below),
         * or the notAfter time of the signing certificate.
         */
        if (signature.getExpirationTime() != null && !currentTime.before(signature.getExpirationTime())) {
            return false;
        }
        for (CertificateWrapper certificate : getIdentifiedTLSCertificates()) {
            if (certificate.getNotAfter() != null && !currentTime.before(certificate.getNotAfter())) {
                return false;
            }
        }
        if (signature.getSigningCertificate() != null && signature.getSigningCertificate().getNotAfter() != null
                && !currentTime.before(signature.getSigningCertificate().getNotAfter())) {
            return false;
        }
        return true;
    }

    private List<CertificateWrapper> getIdentifiedTLSCertificates() {
        return QWACUtils.getIdentifiedTLSCertificates(signature, certificates);
    }

    @Override
    protected String buildAdditionalInfo() {
        if (signature.getExpirationTime() != null && !currentTime.before(signature.getExpirationTime())) {
            return i18nProvider.getMessage(MessageTag.QWAC_EXPIRY_EXP, ValidationProcessUtils.getFormattedDate(currentTime),
                    ValidationProcessUtils.getFormattedDate(signature.getExpirationTime()));
        }
        for (CertificateWrapper certificate : getIdentifiedTLSCertificates()) {
            if (certificate.getNotAfter() != null && !currentTime.before(certificate.getNotAfter())) {
                return i18nProvider.getMessage(MessageTag.QWAC_EXPIRY_EXP, ValidationProcessUtils.getFormattedDate(currentTime),
                        ValidationProcessUtils.getFormattedDate(certificate.getNotAfter()), certificate.getId());
            }
        }
        if (signature.getSigningCertificate() != null && signature.getSigningCertificate().getNotAfter() != null
                && !currentTime.before(signature.getSigningCertificate().getNotAfter())) {
            return i18nProvider.getMessage(MessageTag.QWAC_EXPIRY_EXP, ValidationProcessUtils.getFormattedDate(currentTime),
                    ValidationProcessUtils.getFormattedDate(signature.getSigningCertificate().getNotAfter()),
                    signature.getSigningCertificate().getId());
        }
        return null;
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.TLS_CERT_BINDING_SIG_EXPIRY_DATE;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.TLS_CERT_BINDING_SIG_EXPIRY_DATE_ANS;
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
