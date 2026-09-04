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

import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationCertificateApprovalStatus;
import eu.europa.esig.dss.diagnostic.TrustServiceWrapper;
import eu.europa.esig.dss.diagnostic.TrustedEntityServiceWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.ChainItem;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Verifies whether the Trusted Entity Service statuses are consistent
 *
 */
public class TrustedEntityServiceStatusConsistencyCheck extends ChainItem<XmlValidationCertificateApprovalStatus> {

    /** List of {@code TrustedEntityServiceWrapper}s at control time */
    private final List<TrustedEntityServiceWrapper> trustedServicesWithSti;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlValidationCertificateApprovalStatus}
     * @param trustedServicesWithSti list of {@link TrustServiceWrapper}s
     * @param constraint {@link LevelRule}
     */
    public TrustedEntityServiceStatusConsistencyCheck(I18nProvider i18nProvider, XmlValidationCertificateApprovalStatus result,
                                                    List<TrustedEntityServiceWrapper> trustedServicesWithSti, LevelRule constraint) {
        super(i18nProvider, result, constraint);
        this.trustedServicesWithSti = trustedServicesWithSti;
    }

    @Override
    protected boolean process() {
        Set<String> statusesSet = getApplicableStatusesSet();
        return Utils.collectionSize(statusesSet) <= 1;
    }

    private Set<String> getApplicableStatusesSet() {
        return trustedServicesWithSti.stream().map(TrustedEntityServiceWrapper::getStatus).collect(Collectors.toSet());
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.CERT_USAGE_STATUS_CONS;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.CERT_USAGE_STATUS_CONS_ANS;
    }

    @Override
    protected String buildAdditionalInfo() {
        Set<String> applicableStatusesSet = getApplicableStatusesSet();
        if (Utils.collectionSize(applicableStatusesSet) == 1) {
            return i18nProvider.getMessage(MessageTag.CERTIFICATE_USAGE_STATUS, applicableStatusesSet.iterator().next());
        }
        return i18nProvider.getMessage(MessageTag.CERTIFICATE_USAGE_STATUSES, applicableStatusesSet);
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
