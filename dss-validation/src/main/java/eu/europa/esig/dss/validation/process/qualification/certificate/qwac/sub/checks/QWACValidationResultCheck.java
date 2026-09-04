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

import eu.europa.esig.dss.detailedreport.jaxb.XmlQWACProcess;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationQWACProcess;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.ChainItem;

/**
 * This class verifies whether the QWAC validation has succeeded at least for one QWAC profile
 *
 */
public class QWACValidationResultCheck extends ChainItem<XmlQWACProcess> {

    /** TLS certificate */
    private final XmlValidationQWACProcess[] qwacValidationProcesses;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlQWACProcess}
     * @param qwacValidationProcesses an array of {@link XmlValidationQWACProcess}es
     * @param constraint {@link LevelRule}
     */
    public QWACValidationResultCheck(
            final I18nProvider i18nProvider, final XmlQWACProcess result, final XmlValidationQWACProcess[] qwacValidationProcesses,
            final LevelRule constraint) {
        super(i18nProvider, result, constraint);
        this.qwacValidationProcesses = qwacValidationProcesses;
    }

    @Override
    protected boolean process() {
        if (Utils.isArrayNotEmpty(qwacValidationProcesses)) {
            for (XmlValidationQWACProcess qwacProcess : qwacValidationProcesses) {
                if (isValid(qwacProcess)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.QWAC_VALID;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.QWAC_VALID_ANS;
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
