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

import eu.europa.esig.dss.cbades.COSEParser;
import eu.europa.esig.dss.cbades.COSESignStructure;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.spi.policy.DefaultSignaturePolicyValidatorLoader;
import eu.europa.esig.dss.spi.policy.NonASN1SignaturePolicyValidator;
import eu.europa.esig.dss.spi.policy.SignaturePolicyValidatorLoader;
import eu.europa.esig.dss.spi.signature.AdvancedSignature;
import eu.europa.esig.dss.spi.validation.analyzer.DefaultDocumentAnalyzer;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class performs signature extraction and Java validation of COSE (RFC 8152) and CB-AdES (ETSI TS 119 152) signatures
 *
 */
public class COSEDocumentAnalyzer extends DefaultDocumentAnalyzer {

    private static final Logger LOG = LoggerFactory.getLogger(COSEDocumentAnalyzer.class);

    /** The COSE signature structure to be validated */
    protected COSESignStructure coseSignStructure;

    /** Represents content of RFC 9052 "4.3. Externally Supplied Data" */
    protected DSSDocument externallySuppliedData;

    /**
     * The empty constructor
     */
    public COSEDocumentAnalyzer() {
        // empty
    }

    /**
     * The default constructor for validation of a {@code DSSDocument} containing a COSE signature structure
     *
     * @param document
     *            {@link DSSDocument} containing COSE signature(s)
     */
    public COSEDocumentAnalyzer(final DSSDocument document) {
        super();
        Objects.requireNonNull(document, "Document to be validated cannot be null!");

        this.document = document;
        this.coseSignStructure = buildCoseSignStructure(document);
    }

    /**
     * The constructor for {@code COSEDocumentAnalyzer} to validate a provided {@code COSESignStructure}
     *
     * @param coseSignStructure
     *            {@link COSESignStructure} containing COSE signature(s)
     */
    public COSEDocumentAnalyzer(final COSESignStructure coseSignStructure) {
        this.coseSignStructure = coseSignStructure;
    }

    private COSESignStructure buildCoseSignStructure(final DSSDocument document) {
        COSEParser coseParser = COSEParser.fromDocument(document);
        return coseParser.parse();
    }

    /**
     * Sets externally supplied data as per RFC 9052 "4.3. Externally Supplied Data"
     *
     * @param externallySuppliedData {@link DSSDocument}
     */
    public void setExternallySuppliedData(DSSDocument externallySuppliedData) {
        this.externallySuppliedData = externallySuppliedData;
    }

    @Override
    public boolean isSupported(DSSDocument dssDocument) {
        return COSEParser.isSupported(dssDocument);
    }

    /**
     * Gets a {@code COSESignStructure} to be validated
     *
     * @return {@link COSESignStructure}
     */
    public COSESignStructure getCoseSignStructure() {
        return coseSignStructure;
    }

    @Override
    protected List<AdvancedSignature> buildSignatures() {
        final List<AdvancedSignature> signatures = new ArrayList<>();
        COSESignStructure coseSignStructureObject = getCoseSignStructure();
        List<CBORSignature> cborSignatures = CBORSignature.fromCOSESignStructure(coseSignStructureObject);
        LOG.info("{} signature(s) found", Utils.collectionSize(cborSignatures));
        for (CBORSignature cose : cborSignatures) {
            CBAdESSignature cbadesSignature = new CBAdESSignature(cose);
            cbadesSignature.setFilename(document.getName());
            cbadesSignature.setSigningCertificateSource(signingCertificateSource);
            cbadesSignature.setDetachedContents(detachedContents);
            if (externallySuppliedData != null) {
                cbadesSignature.setExternallySuppliedData(externallySuppliedData);
            }
            cbadesSignature.initBaselineRequirementsChecker(certificateVerifier);
            validateSignaturePolicy(cbadesSignature);
            signatures.add(cbadesSignature);
        }
        return signatures;
    }

    @Override
    public List<DSSDocument> getOriginalDocuments(AdvancedSignature advancedSignature) {
        final CBAdESSignature cbadesSignature = (CBAdESSignature) advancedSignature;
        try {
            return cbadesSignature.getOriginalDocuments();
        } catch (DSSException e) {
            LOG.error("Cannot retrieve a list of original documents");
            return Collections.emptyList();
        }
    }

    @Override
    public SignaturePolicyValidatorLoader getSignaturePolicyValidatorLoader() {
        DefaultSignaturePolicyValidatorLoader signaturePolicyValidatorLoader = new DefaultSignaturePolicyValidatorLoader();
        signaturePolicyValidatorLoader.setDefaultSignaturePolicyValidator(new NonASN1SignaturePolicyValidator());
        return signaturePolicyValidatorLoader;
    }

}
