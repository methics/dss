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
package eu.europa.esig.dss.cbades.validation;

import eu.europa.esig.dss.cbades.COSESignStructure;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.signature.AdvancedSignature;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.diagnostic.SignedDocumentDiagnosticDataBuilder;

import java.util.List;

/**
 * This class is used to validate COSE (RFC 8152) and CB-AdES (ETSI TS 119 152) signatures.
 *
 */
public class COSEDocumentValidator extends SignedDocumentValidator {

    /**
     * Empty constructor
     */
    public COSEDocumentValidator() {
        super(new COSEDocumentAnalyzer());
    }

    /**
     * Default constructor
     *
     * @param document {@link DSSDocument} to validate
     */
    public COSEDocumentValidator(DSSDocument document) {
        super(new COSEDocumentAnalyzer(document));
    }

    @Override
    public SignedDocumentDiagnosticDataBuilder initializeDiagnosticDataBuilder() {
        return new CBAdESDiagnosticDataBuilder();
    }

    /**
     * Sets externally supplied data as per RFC 9052 "4.3. Externally Supplied Data".
     * <p>
     * WARN: Provide the data only when the signature have used the externally supplied data on its creation.
     *       Otherwise, it will invalidate the signature.
     *
     * @param externallySuppliedData {@link DSSDocument}
     */
    public void setExternallySuppliedData(DSSDocument externallySuppliedData) {
        getDocumentAnalyzer().setExternallySuppliedData(externallySuppliedData);
    }

    @Override
    public COSEDocumentAnalyzer getDocumentAnalyzer() {
        return (COSEDocumentAnalyzer) super.getDocumentAnalyzer();
    }

    @Override
    public boolean isSupported(DSSDocument dssDocument) {
        return getDocumentAnalyzer().isSupported(dssDocument);
    }

    /**
     * Gets a {@code COSESignStructure} to be validated
     *
     * @return {@link COSESignStructure}
     */
    public COSESignStructure getCoseSignStructure() {
        return getDocumentAnalyzer().getCoseSignStructure();
    }

    @Override
    public List<DSSDocument> getOriginalDocuments(AdvancedSignature advancedSignature) {
        return getDocumentAnalyzer().getOriginalDocuments(advancedSignature);
    }

}
