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
package eu.europa.esig.dss.validation.executor.certificate.qwac;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.validation.executor.certificate.DefaultCertificateProcessExecutor;
import eu.europa.esig.dss.validation.executor.certificate.DetailedReportForCertificateBuilder;

/**
 * Executes a QWAC certificate validation
 *
 */
public class QWACCertificateProcessExecutor extends DefaultCertificateProcessExecutor {

    /**
     * Default constructor
     */
    public QWACCertificateProcessExecutor() {
        // empty
    }

    @Override
    protected DetailedReportForCertificateBuilder getDetailedReportBuilder(DiagnosticData diagnosticData) {
        return new DetailedReportForQWACBuilder(getI18nProvider(), diagnosticData, policy, currentTime, certificateId);
    }

}
