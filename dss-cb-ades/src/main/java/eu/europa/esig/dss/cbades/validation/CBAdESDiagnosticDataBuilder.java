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

import eu.europa.esig.dss.diagnostic.jaxb.XmlCOSESignatureType;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.spi.signature.AdvancedSignature;
import eu.europa.esig.dss.validation.reports.diagnostic.SignedDocumentDiagnosticDataBuilder;

/**
 * DiagnosticDataBuilder for a COSE signature
 *
 */
public class CBAdESDiagnosticDataBuilder extends SignedDocumentDiagnosticDataBuilder {

    /**
     * Default constructor
     */
    public CBAdESDiagnosticDataBuilder() {
        // empty
    }

    @Override
    public XmlSignature buildDetachedXmlSignature(AdvancedSignature signature) {
        XmlSignature xmlSignature = super.buildDetachedXmlSignature(signature);
        xmlSignature.setCOSESignatureType(getXmlCOSESignatureType((CBAdESSignature) signature));
        return xmlSignature;
    }

    private XmlCOSESignatureType getXmlCOSESignatureType(CBAdESSignature signature) {
        XmlCOSESignatureType xmlCOSESignatureType = new XmlCOSESignatureType();
        xmlCOSESignatureType.setValue(signature.getCOSESignatureType());
        xmlCOSESignatureType.setTagged(signature.isTagged());
        return xmlCOSESignatureType;
    }

}
