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
package eu.europa.esig.dss.jades.extension;

import eu.europa.esig.dss.enumerations.JWSSerializationType;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.extension.AbstractDocumentExtender;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.jades.JAdESTimestampParameters;
import eu.europa.esig.dss.jades.signature.JAdESService;
import eu.europa.esig.dss.jades.validation.JWSDocumentAnalyzerFactory;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.SerializableSignatureParameters;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Objects;

/**
 * JAdES specific implementation of a {@code eu.europa.esig.dss.spi.augmentation.DocumentExtender}.
 *
 */
public class JAdESDocumentExtender extends AbstractDocumentExtender<JAdESSignatureParameters, JAdESTimestampParameters> {

    private static final Logger LOG = LoggerFactory.getLogger(JAdESDocumentExtender.class);

    /**
     * Empty constructor
     */
    JAdESDocumentExtender() {
        // empty
    }

    /**
     * Default constructor
     *
     * @param document {@link DSSDocument} to be extended
     */
    public JAdESDocumentExtender(final DSSDocument document) {
        Objects.requireNonNull(document, "Document to be extended cannot be null!");
        this.document = document;
    }

    @Override
    protected DocumentSignatureService<JAdESSignatureParameters, JAdESTimestampParameters> createSignatureService() {
        Objects.requireNonNull(certificateVerifier, "Please provide CertificateVerifier or corresponding JAdESService!");
        final JAdESService service = new JAdESService(certificateVerifier);
        service.setTspSource(tspSource);
        return service;
    }

    @Override
    public boolean isSupported(DSSDocument dssDocument) {
        return new JWSDocumentAnalyzerFactory().isSupported(dssDocument);
    }

    @Override
    protected JAdESSignatureParameters emptySignatureParameters() {
        JAdESSignatureParameters emptyParameters = new JAdESSignatureParameters();
        LOG.info("No JAdES related signature parameters found within the configuration. " +
                "Fallback to '{}' JwsSerializationType", JWSSerializationType.JSON_SERIALIZATION);
        emptyParameters.setJwsSerializationType(JWSSerializationType.JSON_SERIALIZATION);
        return emptyParameters;
    }

    @Override
    protected boolean isSupportedParameters(SerializableSignatureParameters parameters) {
        return parameters instanceof JAdESSignatureParameters;
    }

    @Override
    protected boolean isSupportedService(DocumentSignatureService<?, ?> service) {
        return service instanceof JAdESService;
    }

    @Override
    public SignatureForm getSignatureForm() {
        return SignatureForm.JAdES;
    }

}
