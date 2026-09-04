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
package eu.europa.esig.dss.extension;

import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.spi.extension.DocumentExtender;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;

import java.util.Objects;
import java.util.ServiceLoader;

/**
 * This class contains common code for signature augmentation utilities.
 *
 */
public abstract class SignedDocumentExtender implements DocumentExtender {

    /**
     * The reference to the certificate verifier. The current DSS implementation
     * proposes {@link eu.europa.esig.dss.spi.validation.CommonCertificateVerifier}.
     * This verifier encapsulates the references to different sources used in the
     * signature validation process.
     */
    protected CertificateVerifier certificateVerifier;

    /**
     * The source to be used for a timestamp token request, when applicable.
     */
    protected TSPSource tspSource;

    /**
     * (Optional) Document signature services.
     * When defined, the applicable instance of a corresponding service will be used.
     * If no suitable service found, a new service instance will be created.
     */
    protected DocumentSignatureService<?, ?>[] services;

    /**
     * Empty constructor
     */
    protected SignedDocumentExtender() {
        // empty
    }

    /**
     * This method guesses the document format and returns an appropriate
     * document reader.
     *
     * @param dssDocument
     *            The instance of {@code DSSDocument} to validate
     * @return returns the specific instance of {@code DocumentReader} in terms
     *         of the document type
     */
    public static SignedDocumentExtender fromDocument(final DSSDocument dssDocument) {
        Objects.requireNonNull(dssDocument, "DSSDocument is null");
        ServiceLoader<SignedDocumentExtenderFactory> serviceLoaders = ServiceLoader.load(SignedDocumentExtenderFactory.class);
        for (SignedDocumentExtenderFactory factory : serviceLoaders) {
            if (factory.isSupported(dssDocument)) {
                return factory.create(dssDocument);
            }
        }
        throw new UnsupportedOperationException("Document format not recognized/handled");
    }

    @Override
    public void setCertificateVerifier(CertificateVerifier certificateVerifier) {
        this.certificateVerifier = certificateVerifier;
    }

    @Override
    public void setTspSource(TSPSource tspSource) {
        this.tspSource = tspSource;
    }

    /**
     * (Optional) Sets document signature services.
     * When defined, the applicable instance of a corresponding service will be used.
     * If no suitable service found, a new service instance will be created.
     *
     * @param services an array of {@link DocumentSignatureService}s
     */
    public void setServices(DocumentSignatureService<?, ?>... services) {
        this.services = services;
    }

    /**
     * Gets the signature form for the current implementation
     *
     * @return {@link SignatureForm}
     */
    public abstract SignatureForm getSignatureForm();

    /**
     * Gets whether the document to be extended represents an ASiC container
     *
     * @return TRUE if the document is an ASiC container, FALSE otherwise
     */
    public boolean isASiC() {
        return false;
    }

}
