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
package eu.europa.esig.dss.validation.process.qualification.certificate.usage.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlMessage;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationCertificateApprovalStatus;
import eu.europa.esig.dss.diagnostic.TrustServiceWrapper;
import eu.europa.esig.dss.diagnostic.TrustedEntityServiceWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.LoTEServiceTypeIdentifier;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.ChainItem;

import java.util.List;

/**
 * Verifies whether the trusted enbtity services with the given STI exist
 *
 */
public class TrustedEntityServiceWithStiCheck extends ChainItem<XmlValidationCertificateApprovalStatus> {

    /** List of {@code TrustedEntityServiceWrapper}s at control time */
    private final List<TrustedEntityServiceWrapper> trustedServicesWithSti;

    /** Service Type Identifier URI */
    private final String stiUri;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlValidationCertificateApprovalStatus}
     * @param trustedServicesWithSti list of {@link TrustServiceWrapper}s
     * @param stiUri {@link String}
     * @param constraint {@link LevelRule}
     */
    public TrustedEntityServiceWithStiCheck(I18nProvider i18nProvider, XmlValidationCertificateApprovalStatus result,
                                            List<TrustedEntityServiceWrapper> trustedServicesWithSti, String stiUri, LevelRule constraint) {
        super(i18nProvider, result, constraint);
        this.trustedServicesWithSti = trustedServicesWithSti;
        this.stiUri = stiUri;
    }

    @Override
    protected boolean process() {
        return Utils.isCollectionNotEmpty(trustedServicesWithSti);
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.CERT_USAGE_STI;
    }

    @Override
    protected XmlMessage buildConstraintMessage() {
        return buildXmlMessage(getMessageTag(), getStiUserFriendlyLabel());
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.CERT_USAGE_STI_ANS;
    }

    @Override
    protected XmlMessage buildErrorMessage() {
        return buildXmlMessage(getErrorMessageTag(), getStiUserFriendlyLabel());
    }

    private String getStiUserFriendlyLabel() {
        LoTEServiceTypeIdentifier sti = LoTEServiceTypeIdentifier.fromUri(stiUri);
        if (sti != null && sti.getLabel() != null) {
            return sti.getLabel();
        }
        return stiUri;
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
