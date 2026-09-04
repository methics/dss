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

import eu.europa.esig.dss.detailedreport.jaxb.XmlCertificateApprovalStatusProcess;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.ListType;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.validation.process.ChainItem;

/**
 * Verifies whether the list type is known
 *
 */
public class ListTypeKnownCheck extends ChainItem<XmlCertificateApprovalStatusProcess> {

    /** List Type URI */
    private final String listTypeUri;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlCertificateApprovalStatusProcess}
     * @param listTypeUri {@link String}
     * @param constraint {@link LevelRule}
     */
    public ListTypeKnownCheck(I18nProvider i18nProvider, XmlCertificateApprovalStatusProcess result,
                              String listTypeUri, LevelRule constraint) {
        super(i18nProvider, result, constraint);
        this.listTypeUri = listTypeUri;
    }

    @Override
    protected boolean process() {
        ListType listType = ListType.fromUri(listTypeUri);
        return listType != null && listType.getLabel() != null; // Label is present -> defined
    }

    @Override
    protected String buildAdditionalInfo() {
        return i18nProvider.getMessage(MessageTag.CERTIFICATE_USAGE_LIST_TYPE, listTypeUri);
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.CERT_USAGE_LIST_TYPE_KNOWN;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.CERT_USAGE_LIST_TYPE_KNOWN_ANS;
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