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
package eu.europa.esig.dss.validation.process.bbb.aov.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlAOV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraintsConclusion;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;

import java.util.Date;

/**
 * Verifies result of the {@code eu.europa.esig.dss.validation.process.bbb.aov.AlgorithmObsolescenceValidation} process.
 * This class also allows providing an identifier of a token to be validated.
 *
 * @param <T> {@link XmlConstraintsConclusion}
 */
public class AlgorithmObsolescenceValidationCheckWithId<T extends XmlConstraintsConclusion> extends AlgorithmObsolescenceValidationCheck<T> {

    /** Identifier of a token to be validated */
    protected final String tokenId;

    /**
     * Default constructor
     *
     * @param i18nProvider   {@link I18nProvider}
     * @param result         {@link XmlConstraintsConclusion}
     * @param aovResult      {@link XmlAOV}
     * @param validationDate {@link Date}
     * @param position       {@link MessageTag}
     * @param tokenId        {@link String}
     */
    public AlgorithmObsolescenceValidationCheckWithId(I18nProvider i18nProvider, T result,
                                                      XmlAOV aovResult, Date validationDate,
                                                      MessageTag position, String tokenId) {
        super(i18nProvider, result, aovResult, validationDate, position, tokenId);
        this.tokenId = tokenId;
    }

    @Override
    protected String buildAdditionalInfo() {
        return i18nProvider.getMessage(MessageTag.ACCM_DESC_WITH_ID_RESULT, super.buildAdditionalInfo(), tokenId);
    }

}
