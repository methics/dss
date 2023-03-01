/**
 * DSS - Digital Signature Services
 * Copyright (C) 2015 European Commission, provided under the CEF programme
 * 
 * This file is part of the "DSS - Digital Signature Services" project.
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package eu.europa.esig.dss.validation.process.vpfbs.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraintsConclusion;
import eu.europa.esig.dss.detailedreport.jaxb.XmlXCV;
import eu.europa.esig.dss.diagnostic.TokenProxy;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.validation.process.ChainItem;

/**
 * Verifies if the X.509 Certificate Validation as per clause 5.2.6 did not return
 * INDETERMINATE/REVOKED_NO_POE indication
 *
 * @param <T> implementation of the block's conclusion
 */
public class SigningCertificateNotRevokedCheck<T extends XmlConstraintsConclusion> extends ChainItem<T> {

    /** X509 Certificate Validation building block suffix */
    private static final String XCV_BLOCK_SUFFIX = "-XCV";

    /** Token's X509CertificateValidation result */
    private final XmlXCV xmlXCV;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlConstraintsConclusion}
     * @param xmlXCV {@link XmlXCV}
     * @param token {@link TokenProxy}
     * @param constraint {@link LevelConstraint}
     */
    public SigningCertificateNotRevokedCheck(I18nProvider i18nProvider, T result,
                                             XmlXCV xmlXCV, TokenProxy token, LevelConstraint constraint) {
        super(i18nProvider, result, constraint, token.getId() + XCV_BLOCK_SUFFIX);
        this.xmlXCV = xmlXCV;
    }

    @Override
    protected boolean process() {
        return xmlXCV != null && !(Indication.INDETERMINATE.equals(xmlXCV.getConclusion().getIndication()) &&
                SubIndication.REVOKED_NO_POE.equals(xmlXCV.getConclusion().getSubIndication()));
    }

    @Override
    protected Indication getFailedIndicationForConclusion() {
        return Indication.INDETERMINATE;
    }

    @Override
    protected SubIndication getFailedSubIndicationForConclusion() {
        return SubIndication.REVOKED_NO_POE;
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.BSV_ISCRAVTC;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.BSV_ISCRAVTC_ANS;
    }

}
