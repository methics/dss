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
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;

/**
 * This class verifies whether the QWAC validation has succeeded for 2-QWAC profile
 *
 */
public class QWAC2ValidationResultCheck extends QWACValidationResultCheck {

    /**
     * Default constructor
     *
     * @param i18nProvider            {@link I18nProvider}
     * @param result                  {@link XmlQWACProcess}
     * @param qwacValidationProcesses an array of {@link XmlValidationQWACProcess}es
     * @param constraint              {@link LevelRule}
     */
    public QWAC2ValidationResultCheck(final I18nProvider i18nProvider, final XmlQWACProcess result,
                                      final XmlValidationQWACProcess[] qwacValidationProcesses, final LevelRule constraint) {
        super(i18nProvider, result, qwacValidationProcesses, constraint);
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.QWAC_VALID_ANS_2;
    }

}
