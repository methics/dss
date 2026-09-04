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
package eu.europa.esig.dss.cades.validation.revocation;

import eu.europa.esig.dss.alert.SilentOnStatusAlert;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESService;
import eu.europa.esig.dss.cades.validation.AbstractCAdESTestValidation;
import eu.europa.esig.dss.cades.validation.CAdESSignature;
import eu.europa.esig.dss.cms.CMS;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.RevocationWrapper;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureDigestReference;
import eu.europa.esig.dss.enumerations.RevocationOrigin;
import eu.europa.esig.dss.enumerations.RevocationType;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.spi.DSSASN1Utils;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.signature.AdvancedSignature;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.validationreport.enums.ObjectType;
import eu.europa.esig.validationreport.jaxb.POEProvisioningType;
import eu.europa.esig.validationreport.jaxb.SignatureReferenceType;
import eu.europa.esig.validationreport.jaxb.ValidationObjectType;
import eu.europa.esig.validationreport.jaxb.ValidationReportType;
import org.bouncycastle.asn1.cms.SignerInfo;
import org.bouncycastle.cms.SignerInformation;
import org.bouncycastle.cms.SignerInformationStore;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CAdESRevocationSourceHUTest extends AbstractCAdESTestValidation {

	@Override
	protected DSSDocument getSignedDocument() {
		return new InMemoryDocument(CAdESRevocationSourceHUTest.class.getResourceAsStream("/validation/Signature-C-HU_POL-3.p7m"));
	}

	@Override
	protected void checkBLevelValid(DiagnosticData diagnosticData) {
		super.checkBLevelValid(diagnosticData);

		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		assertNotNull(signature);
		assertNotNull(signature.getDigestMatchers());
		assertEquals(1, signature.getDigestMatchers().size());
		assertNotNull(signature.getSignatureValue());
	}

	@Override
	protected void checkSignatureLevel(DiagnosticData diagnosticData) {
		assertTrue(diagnosticData.isTLevelTechnicallyValid(diagnosticData.getFirstSignatureId()));
		assertTrue(diagnosticData.isALevelTechnicallyValid(diagnosticData.getFirstSignatureId()));
	}
	
	@Override
	protected void verifySourcesAndDiagnosticData(List<AdvancedSignature> advancedSignatures,
			DiagnosticData diagnosticData) {
		super.verifySourcesAndDiagnosticData(advancedSignatures, diagnosticData);
		
		int revocationSignatureOriginCounter = 0;
		Set<RevocationWrapper> revocationData = diagnosticData.getAllRevocationData();
		for (RevocationWrapper revocation : revocationData) {
			assertNotNull(revocation.getRevocationType());
			assertNotNull(revocation.getOrigin());
			if (RevocationOrigin.INPUT_DOCUMENT.equals(revocation.getOrigin())) {
				revocationSignatureOriginCounter++;
			}
		}
		
		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		
		assertEquals(2, revocationSignatureOriginCounter);
		assertEquals(0, signature.foundRevocations().getRelatedRevocationsByType(RevocationType.CRL).size());
		assertEquals(2, signature.foundRevocations().getRelatedRevocationsByType(RevocationType.OCSP).size());
		assertEquals(0, signature.foundRevocations().getRelatedRevocationsByTypeAndOrigin(RevocationType.OCSP, RevocationOrigin.REVOCATION_VALUES).size());
		assertEquals(2, signature.foundRevocations().getRelatedRevocationsByTypeAndOrigin(RevocationType.OCSP, RevocationOrigin.CMS_SIGNED_DATA).size());
		assertEquals(0, signature.foundRevocations().getRelatedRevocationsByTypeAndOrigin(RevocationType.OCSP, RevocationOrigin.TIMESTAMP_VALIDATION_DATA).size());
		assertEquals(0, signature.foundRevocations().getRelatedRevocationsByTypeAndOrigin(RevocationType.OCSP, RevocationOrigin.DSS_DICTIONARY).size());
	}
	
	@Override
	protected void verifyOriginalDocuments(SignedDocumentValidator validator, DiagnosticData diagnosticData) {
		super.verifyOriginalDocuments(validator, diagnosticData);

		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		XmlSignatureDigestReference signatureDigestReference = signature.getSignatureDigestReference();
		assertNotNull(signatureDigestReference);

		List<AdvancedSignature> signatures = validator.getSignatures();
		assertEquals(1, signatures.size());
		CAdESSignature cadesSignature = (CAdESSignature) signatures.get(0);
		CMS cms = cadesSignature.getCMS();
		SignerInformationStore signerInfos = cms.getSignerInfos();
		SignerInformation signerInformation = signerInfos.iterator().next();
		SignerInfo signerInfo = signerInformation.toASN1Structure();
		byte[] derEncoded = DSSASN1Utils.getDEREncoded(signerInfo);
		byte[] digest = DSSUtils.digest(signatureDigestReference.getDigestMethod(), derEncoded);

		String signatureReferenceDigestValue = Utils.toBase64(signatureDigestReference.getDigestValue());
		String signatureElementDigestValue = Utils.toBase64(digest);
		assertEquals(signatureReferenceDigestValue, signatureElementDigestValue);
	}

	@Override
	protected void verifyReportsData(Reports reports) {
		super.verifyReportsData(reports);

		DiagnosticData diagnosticData = reports.getDiagnosticData();
		SignatureWrapper signature = diagnosticData.getSignatureById(diagnosticData.getFirstSignatureId());
		XmlSignatureDigestReference signatureDigestReference = signature.getSignatureDigestReference();
		String signatureReferenceDigestValue = Utils.toBase64(signatureDigestReference.getDigestValue());

		ValidationReportType etsiValidationReport = reports.getEtsiValidationReportJaxb();
		List<ValidationObjectType> validationObjects = etsiValidationReport.getSignatureValidationObjects().getValidationObject();
		int timestampCounter = 0;
		for (ValidationObjectType validationObject : validationObjects) {
			if (ObjectType.TIMESTAMP.equals(validationObject.getObjectType())) {
				POEProvisioningType poeProvisioning = validationObject.getPOEProvisioning();
				List<SignatureReferenceType> signatureReferences = poeProvisioning.getSignatureReference();
				assertEquals(1, signatureReferences.size());
				SignatureReferenceType signatureReferenceType = signatureReferences.get(0);
				assertNotNull(signatureReferenceType.getDigestMethod());
				assertNotNull(signatureReferenceType.getDigestValue());
				assertNull(signatureReferenceType.getCanonicalizationMethod());
				assertNull(signatureReferenceType.getXAdESSignaturePtr());
				assertNull(signatureReferenceType.getPAdESFieldName());
				assertEquals(signatureReferenceDigestValue, Utils.toBase64(signatureReferenceType.getDigestValue()));
				timestampCounter++;
			}
		}
		assertEquals(2, timestampCounter);
	}

	// See DSS-3650
	@Test
	void extendSignatureTest() {
		CertificateVerifier certificateVerifier = getOfflineCertificateVerifier();
		certificateVerifier.setAlertOnMissingRevocationData(new SilentOnStatusAlert());
		certificateVerifier.setAlertOnExpiredCertificate(new SilentOnStatusAlert());

		CAdESService cadesService = new CAdESService(certificateVerifier);
		cadesService.setTspSource(getGoodTsa());

		CAdESSignatureParameters extensionParameters = new CAdESSignatureParameters();
		extensionParameters.setSignatureLevel(SignatureLevel.CAdES_BASELINE_LTA);

		Exception exception = assertThrows(DSSException.class, () -> cadesService.extendDocument(getSignedDocument(), extensionParameters));
		assertTrue(exception.getMessage().contains("Unable to replace validation content of CMS SignedData"));
	}

}
