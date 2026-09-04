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
package eu.europa.esig.dss.cookbook.example.sign;

import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cookbook.example.CookbookTools;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignaturePackaging;
import eu.europa.esig.dss.enumerations.SignatureProfile;
import eu.europa.esig.dss.enumerations.ValidationDataEncapsulationStrategy;
import eu.europa.esig.dss.extension.SignedDocumentExtender;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.ToBeSigned;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.signature.PAdESService;
import eu.europa.esig.dss.pdf.pdfbox.PdfBoxNativeObjectFactory;
import eu.europa.esig.dss.service.crl.OnlineCRLSource;
import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
import eu.europa.esig.dss.token.DSSPrivateKeyEntry;
import eu.europa.esig.dss.token.SignatureTokenConnection;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.signature.XAdESService;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

/**
 * How to extend with XAdES-BASELINE signature
 *
 */
class ExtendXAdESTest extends CookbookTools {

	@Test
	void test() throws Exception {
		prepareXmlDoc();

		DSSDocument signedDocument = null;
		try (SignatureTokenConnection signingToken = getUserPkcs12Token()) {

			DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);
			XAdESSignatureParameters parameters = new XAdESSignatureParameters();
			parameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_B);
			parameters.setSignaturePackaging(SignaturePackaging.ENVELOPED);
			parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
			parameters.setSigningCertificate(privateKey.getCertificate());
			parameters.setCertificateChain(privateKey.getCertificateChain());

			XAdESService service = new XAdESService(new CommonCertificateVerifier());
			ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);
			SignatureValue signatureValue = signingToken.sign(dataToSign, parameters.getDigestAlgorithm(), privateKey);
			signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);
		}
		
		// tag::demoTExtendPrep[]
		// import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
		// import eu.europa.esig.dss.spi.x509.tsp.TSPSource;

		// Create a CertificateVerifier (empty configuration is possible for T-level extension)
		CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();
		// configure if needed

		// init TSP source for timestamp requesting
		TSPSource tspSource = getOnlineTSPSource();

		// end::demoTExtendPrep[]

		// tag::demoTExtendWithExtender[]
		// import eu.europa.esig.dss.enumerations.SignatureProfile;
		// import eu.europa.esig.dss.extension.SignedDocumentExtender;
		// import eu.europa.esig.dss.model.DSSDocument;

		// Initialize a SignedDocumentExtender, which will load the relevant
		// implementation of a DocumentExtender based on document's format
		SignedDocumentExtender documentExtender = SignedDocumentExtender.fromDocument(signedDocument);

		// Set the CertificateVerifier instantiated earlier
		documentExtender.setCertificateVerifier(certificateVerifier);

		// Set the TSPSource for a timestamp extraction
		documentExtender.setTspSource(tspSource);

		// Extend the document, by specifying the target augmentation profile
		DSSDocument tLevelSignature = documentExtender.extendDocument(SignatureProfile.BASELINE_T);

		// end::demoTExtendWithExtender[]

		// tag::demoTExtend[]
		// import eu.europa.esig.dss.enumerations.SignatureLevel;
		// import eu.europa.esig.dss.xades.XAdESSignatureParameters;
		// import eu.europa.esig.dss.xades.signature.XAdESService;

		// Create signature parameters with target extension level
		XAdESSignatureParameters parameters = new XAdESSignatureParameters();
		parameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_T);

		// Init service for signature augmentation using the defined earlier CertificateVerifier
		XAdESService xadesService = new XAdESService(certificateVerifier);

		// Init TSP source for timestamp requesting
		xadesService.setTspSource(tspSource);

		// Extend the document by providing the configured parameters
		tLevelSignature = xadesService.extendDocument(signedDocument, parameters);

		// end::demoTExtend[]

		// tag::demoLTExtendPrep[]
		// import eu.europa.esig.dss.service.crl.OnlineCRLSource;
		// import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
		// import eu.europa.esig.dss.spi.x509.CertificateSource;

		// init revocation sources for CRL/OCSP requesting
		certificateVerifier.setCrlSource(new OnlineCRLSource());
		certificateVerifier.setOcspSource(new OnlineOCSPSource());

		// Trust anchors should be defined for revocation data requesting
		CertificateSource trustedCertificateSource = getTrustedCertificateSource();
		certificateVerifier.setTrustedCertSources(trustedCertificateSource);

		// end::demoLTExtendPrep[]

		// tag::demoLTExtendWithExtender[]
		// import eu.europa.esig.dss.enumerations.SignatureProfile;
		// import eu.europa.esig.dss.extension.SignedDocumentExtender;
		// import eu.europa.esig.dss.model.DSSDocument;

		// Initialize a SignedDocumentExtender, which will load the relevant
		// implementation of a DocumentExtender based on document's format
		documentExtender = SignedDocumentExtender.fromDocument(signedDocument);

		// Set the CertificateVerifier and TSP Source
		documentExtender.setCertificateVerifier(certificateVerifier);
		documentExtender.setTspSource(tspSource);

		// Extend the document
		DSSDocument ltLevelDocument = documentExtender.extendDocument(SignatureProfile.BASELINE_LT);

		// end::demoLTExtendWithExtender[]

		// tag::demoLTExtend[]
		// import eu.europa.esig.dss.enumerations.SignatureLevel;
		// import eu.europa.esig.dss.model.DSSDocument;
		// import eu.europa.esig.dss.service.crl.OnlineCRLSource;
		// import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
		// import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
		// import eu.europa.esig.dss.xades.XAdESSignatureParameters;
		// import eu.europa.esig.dss.xades.signature.XAdESService;

		// Create signature parameters with target extension level
		parameters = new XAdESSignatureParameters();
		parameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_LT);

		// Init service for signature augmentation, by providing the CertificateVerifier
		// and TSPSource
		xadesService = new XAdESService(certificateVerifier);
		xadesService.setTspSource(getOnlineTSPSource());

		// Extend signature
		ltLevelDocument = xadesService.extendDocument(tLevelSignature, parameters);

		// end::demoLTExtend[]

		// tag::demoLTAExtendPrep[]
		// import eu.europa.esig.dss.service.crl.OnlineCRLSource;
		// import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
		// import eu.europa.esig.dss.service.tsp.OnlineTSPSource;

		// init revocation sources for CRL/OCSP requesting
		certificateVerifier.setCrlSource(new OnlineCRLSource());
		certificateVerifier.setOcspSource(new OnlineOCSPSource());

		// Trust anchors should be defined for revocation data requesting
		trustedCertificateSource = getTrustedCertificateSource();
		certificateVerifier.setTrustedCertSources(trustedCertificateSource);

		// Initialize the TSPSource for timestamp retrieving
		tspSource = getOnlineTSPSource();

		// end::demoLTAExtendPrep[]

		// tag::demoLTAExtendWithExtender[]
		// import eu.europa.esig.dss.enumerations.SignatureProfile;
		// import eu.europa.esig.dss.extension.SignedDocumentExtender;
		// import eu.europa.esig.dss.model.DSSDocument;

		// Initialize a SignedDocumentExtender, which will load the relevant
		// implementation of a DocumentExtender based on document's format
		documentExtender = SignedDocumentExtender.fromDocument(signedDocument);

		// Set the CertificateVerifier and TSP Source
		documentExtender.setCertificateVerifier(certificateVerifier);
		documentExtender.setTspSource(tspSource);

		// Extend the document
		DSSDocument ltaLevelDocument = documentExtender.extendDocument(SignatureProfile.BASELINE_LTA);

		// end::demoLTAExtendWithExtender[]

		// tag::demoLTAExtend[]
		// import eu.europa.esig.dss.enumerations.SignatureLevel;
		// import eu.europa.esig.dss.xades.XAdESSignatureParameters;
		// import eu.europa.esig.dss.xades.signature.XAdESService;

		// Create signature parameters with target extension level
		parameters = new XAdESSignatureParameters();
		parameters.setSignatureLevel(SignatureLevel.XAdES_BASELINE_LTA);

		// Initialize signature service with TSP Source for time-stamp requesting
		xadesService = new XAdESService(certificateVerifier);
		xadesService.setTspSource(getOnlineTSPSource());

		// Extend signature
		ltaLevelDocument = xadesService.extendDocument(ltLevelDocument, parameters);

		// end::demoLTAExtend[]

		testFinalDocument(ltaLevelDocument);
	}

	@Test
	void signedDocumentExtenderTest() {
		preparePdfDoc();

		DSSDocument signedDocument = null;
		try (SignatureTokenConnection signingToken = getUserPkcs12Token()) {

			DSSPrivateKeyEntry privateKey = signingToken.getKeys().get(0);
			PAdESSignatureParameters parameters = new PAdESSignatureParameters();
			parameters.setSignatureLevel(SignatureLevel.PAdES_BASELINE_B);
			parameters.setSignaturePackaging(SignaturePackaging.ENVELOPING);
			parameters.setDigestAlgorithm(DigestAlgorithm.SHA256);
			parameters.setSigningCertificate(privateKey.getCertificate());
			parameters.setCertificateChain(privateKey.getCertificateChain());

			PAdESService service = new PAdESService(new CommonCertificateVerifier());
			ToBeSigned dataToSign = service.getDataToSign(toSignDocument, parameters);
			SignatureValue signatureValue = signingToken.sign(dataToSign, parameters.getDigestAlgorithm(), privateKey);
			signedDocument = service.signDocument(toSignDocument, parameters, signatureValue);
		}

		// tag::demoDocExtenderInit[]
		// import eu.europa.esig.dss.enumerations.SignatureProfile;
		// import eu.europa.esig.dss.extension.SignedDocumentExtender;

		SignedDocumentExtender documentExtender = SignedDocumentExtender.fromDocument(signedDocument);
		// end::demoDocExtenderInit[]

		// tag::demoDocExtenderConf[]
		// import eu.europa.esig.dss.service.crl.OnlineCRLSource;
		// import eu.europa.esig.dss.service.ocsp.OnlineOCSPSource;
		// import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
		// import eu.europa.esig.dss.spi.x509.tsp.TSPSource;
		// import eu.europa.esig.dss.spi.x509.CertificateSource;

		// Initialize CertificateVerifier and configure according to the requirements
		CommonCertificateVerifier certificateVerifier = new CommonCertificateVerifier();

		// Provide revocation sources
		certificateVerifier.setCrlSource(new OnlineCRLSource());
		certificateVerifier.setOcspSource(new OnlineOCSPSource());

		// Provide trust anchors definition
		CertificateSource trustedCertificateSource = getTrustedCertificateSource();
		certificateVerifier.setTrustedCertSources(trustedCertificateSource);

		// Initialize a TSPSource for timestamp retrieval
		TSPSource tspSource = getOnlineTSPSource();

		// Set the configuration within the SignedDocumentExtender
		documentExtender.setCertificateVerifier(certificateVerifier);
		documentExtender.setTspSource(tspSource);
		// end::demoDocExtenderConf[]

		// tag::demoDocExtenderServ[]
		// import eu.europa.esig.dss.pades.signature.PAdESService;
		// import eu.europa.esig.dss.pdf.pdfbox.PdfBoxNativeObjectFactory;

		// Initialize a service to be used on PAdES signature augmentation
		PAdESService padesService = new PAdESService(certificateVerifier);
		padesService.setTspSource(tspSource);

		// Set custom PdfObjFactory
		padesService.setPdfObjFactory(new PdfBoxNativeObjectFactory());

		// Provide the service or services within the DocumentExtender
		documentExtender.setServices(padesService);
		// end::demoDocExtenderServ[]

		// tag::demoDocExtenderExtend[]
		// import eu.europa.esig.dss.enumerations.SignatureProfile;
		// import eu.europa.esig.dss.model.DSSDocument;

		DSSDocument extendedDocument = documentExtender.extendDocument(SignatureProfile.BASELINE_T);
		// end::demoDocExtenderExtend[]

		List<DSSDocument> detachedDocuments = Collections.emptyList();

		// tag::demoDocExtenderExtendDet[]
		// import eu.europa.esig.dss.enumerations.SignatureProfile;
		// import eu.europa.esig.dss.model.DSSDocument;

		DSSDocument extendedDetachedDocument = documentExtender.extendDocument(SignatureProfile.BASELINE_T, detachedDocuments);
		// end::demoDocExtenderExtendDet[]

		// tag::demoDocExtenderExtendParams[]
		// import eu.europa.esig.dss.enumerations.SignatureProfile;
		// import eu.europa.esig.dss.model.DSSDocument;
		// import eu.europa.esig.dss.pades.PAdESSignatureParameters;

		PAdESSignatureParameters signatureParameters = new PAdESSignatureParameters();
		signatureParameters.setFilter("Custom TST Filter");

		DSSDocument extendedDocumentWithParams = documentExtender.extendDocument(SignatureProfile.BASELINE_T, detachedDocuments, signatureParameters);

		// end::demoDocExtenderExtendParams[]

		JAdESSignatureParameters otherSignatureParameters = new JAdESSignatureParameters();

		// tag::demoDocExtenderExtendManyParams[]
		// import eu.europa.esig.dss.cades.CAdESSignatureParameters;
		// import eu.europa.esig.dss.enumerations.SignatureProfile;
		// import eu.europa.esig.dss.xades.XAdESSignatureParameters;

		extendedDocumentWithParams = documentExtender.extendDocument(SignatureProfile.BASELINE_T, detachedDocuments,
				new XAdESSignatureParameters(), new CAdESSignatureParameters(), otherSignatureParameters);
		// end::demoDocExtenderExtendManyParams[]

		testFinalDocument(extendedDocumentWithParams);

	}

	void validationDataEncapsulationStrategy() {
		XAdESSignatureParameters signatureParameters = new XAdESSignatureParameters();
		// tag::valDataStrategy[]
		// import eu.europa.esig.dss.enumerations.ValidationDataEncapsulationStrategy;

		// This constraint ensures the following behavior, used by Default:
		// LT-level:  the validation data for the signature is added within CertificateValues and
		//            RevocationValues elements, while validation data for embedded timestamps is
		//            added within TimeStampValidationData element.
		// LTA-level: the validation data for archival timestamp(s) and missed validation data
		//            for other timestamps is added within TimeStampValidationData element.
		//            Missed validation data for signature is added within AnyValidationData element.
		signatureParameters.setValidationDataEncapsulationStrategy(ValidationDataEncapsulationStrategy.CERTIFICATE_REVOCATION_VALUES_AND_TIMESTAMP_VALIDATION_DATA_AND_ANY_VALIDATION_DATA);

		// This constraint ensures the following behavior:
		// LT-level:  the validation data for the signature and available timestamps is added
		//            within CertificateValues and RevocationValues elements.
		// LTA-level: the validation data for archival timestamp(s) and missed validation data
		//            for signature is added within TimeStampValidationData element.
		// NOTE: This is a legacy behavior, used in DSS up to 6.1 version.
		signatureParameters.setValidationDataEncapsulationStrategy(ValidationDataEncapsulationStrategy.CERTIFICATE_REVOCATION_VALUES_AND_TIMESTAMP_VALIDATION_DATA);

		// This constraint ensures the following behavior:
		// LT-level:  the validation data for the signature is added within CertificateValues and
		//            RevocationValues elements, while validation data for embedded timestamps is
		//            added within TimeStampValidationData element.
		// LTA-level: the validation data for archival timestamp(s) and missed validation data
		//            for signature is added within TimeStampValidationData element.
		signatureParameters.setValidationDataEncapsulationStrategy(ValidationDataEncapsulationStrategy.CERTIFICATE_REVOCATION_VALUES_AND_TIMESTAMP_VALIDATION_DATA_LT_SEPARATED);

		// This constraint ensures the following behavior:
		// LT-level:  the validation data for the signature is added within CertificateValues and
		//            RevocationValues elements, while validation data for embedded timestamps is
		//            added within AnyValidationData element.
		// LTA-level: the validation data for archival timestamp(s) and missed validation data
		//            for signature is added within AnyValidationData element.
		signatureParameters.setValidationDataEncapsulationStrategy(ValidationDataEncapsulationStrategy.CERTIFICATE_REVOCATION_VALUES_AND_ANY_VALIDATION_DATA);

		// This constraint ensures the following behavior:
		// LT-level:  the validation data for the signature and available timestamps is added
		//            within AnyValidationData element.
		// LTA-level: the validation data for archival timestamp(s) and missed validation data
		//            for signature is added within AnyValidationData element.
		signatureParameters.setValidationDataEncapsulationStrategy(ValidationDataEncapsulationStrategy.ANY_VALIDATION_DATA_ONLY);
		// end::valDataStrategy[]
	}

}
