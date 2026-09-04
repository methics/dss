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
package eu.europa.esig.dss.xades.signature;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.xades.SignatureBuilder;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.XAdESSignatureProfile;
import eu.europa.esig.dss.xml.utils.SantuarioInitializer;

import java.util.Collections;
import java.util.List;

/**
 * Contains B level baseline profile for XAdES signature.
 *
 *
 */
public class XAdESLevelBaselineB implements XAdESSignatureProfile {

	static {
		SantuarioInitializer.init();
	}

	/**
	 * The reference to the {@code CertificateVerifier} which provides information on the sources to be used in the
	 * validation process in the context of a signature.
	 */
	private CertificateVerifier certificateVerifier;

	/**
	 * The default constructor for XAdESLevelBaselineB.
	 *
	 * @param certificateVerifier
	 *            the certificate verifier
	 */
	public XAdESLevelBaselineB(final CertificateVerifier certificateVerifier) {
		this.certificateVerifier = certificateVerifier;
	}

	/**
	 * Returns the canonicalized ds:SignedInfo XML segment under the form of InputStream.
	 * This method is used for signing a document.
	 *
	 * @param dssDocument
	 *            The original dssDocument to sign.
	 * @param parameters
	 *            set of the driving signing parameters
	 * @return bytes the digest to signed
	 * @throws DSSException
	 *             if an error occurred
	 */
	public byte[] getDataToSign(final DSSDocument dssDocument, final XAdESSignatureParameters parameters) throws DSSException {
		return getDataToSign(Collections.singletonList(dssDocument), parameters);
	}

	/**
	 * Returns the canonicalized ds:SignedInfo XML segment under the form of InputStream.
	 * This method is used for signing multiple documents.
	 *
	 * @param documents
	 *            The list of original documents to sign.
	 * @param parameters
	 *            set of the driving signing parameters
	 * @return bytes the digest to signed
	 * @throws DSSException
	 *             if an error occurred
	 */
	public byte[] getDataToSign(final List<DSSDocument> documents, final XAdESSignatureParameters parameters) throws DSSException {
		final XAdESSignatureBuilder signatureBuilder = XAdESSignatureBuilder.getSignatureBuilder(parameters, documents, certificateVerifier);
		parameters.getContext().setBuilder(signatureBuilder);
		return signatureBuilder.build();
	}

	/**
	 * Adds the signature value to the signature.
	 *
	 * @param document
	 *            the original document to sign.
	 * @param parameters
	 *            set of the driving signing parameters
	 * @param signatureValue
	 *            array of bytes representing the signature value.
	 * @return the document with the signature
	 * @throws DSSException
	 *             if an error occurred
	 */
	@Override
	public DSSDocument signDocument(final DSSDocument document, final XAdESSignatureParameters parameters, final byte[] signatureValue) throws DSSException {
		return signDocument(Collections.singletonList(document), parameters, signatureValue);
	}

	@Override
	public DSSDocument signDocument(List<DSSDocument> toSignDocuments, XAdESSignatureParameters parameters, byte[] signatureValue) {
		SignatureBuilder builder = parameters.getContext().getBuilder();
		if (builder != null) {
			builder = parameters.getContext().getBuilder();
		} else {
			builder = XAdESSignatureBuilder.getSignatureBuilder(parameters, toSignDocuments, certificateVerifier);
		}
		final DSSDocument dssDocument = builder.signDocument(signatureValue);
		parameters.getContext().setBuilder(builder);
		return dssDocument;
	}

}
