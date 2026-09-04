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
package eu.europa.esig.dss.validation.process.bbb.fc;

import eu.europa.esig.dss.detailedreport.jaxb.XmlFC;
import eu.europa.esig.dss.diagnostic.AbstractTokenProxy;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.enumerations.Context;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.ValidationPolicy;
import eu.europa.esig.dss.validation.process.Chain;

/**
 * This class contains a common code to be processed as a part of a "5.2.2 Format Checking" building block
 * for validation of signatures and timestamps.
 *
 * @param <S> token wrapper
 */
public abstract class AbstractFormatChecking<S extends AbstractTokenProxy> extends Chain<XmlFC> {

    /** Diagnostic data */
    protected final DiagnosticData diagnosticData;

    /** The token to validate */
    protected final S token;

    /** The validation context */
    protected final Context context;

    /** The validation policy */
    protected final ValidationPolicy policy;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param diagnosticData {@link DiagnosticData}
     * @param token {@link AbstractTokenProxy}
     * @param context {@link Context}
     * @param policy {@link ValidationPolicy}
     */
    protected AbstractFormatChecking(I18nProvider i18nProvider, DiagnosticData diagnosticData,
                                  S token, Context context, ValidationPolicy policy) {
        super(i18nProvider, new XmlFC());
        this.diagnosticData = diagnosticData;
        this.token = token;
        this.context = context;
        this.policy = policy;
    }

    @Override
    protected MessageTag getTitle() {
        return MessageTag.FORMAT_CHECKING;
    }


}
