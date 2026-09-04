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
package eu.europa.esig.dss.validation.executor.process;

import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.detailedreport.jaxb.XmlAOV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlBasicBuildingBlocks;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraint;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCryptographicValidation;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSAV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlStatus;
import eu.europa.esig.dss.diagnostic.DiagnosticDataFacade;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignatureDigestReference;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;
import eu.europa.esig.dss.validation.reports.Reports;
import eu.europa.esig.validationreport.enums.ObjectType;
import eu.europa.esig.validationreport.enums.TypeOfProof;
import eu.europa.esig.validationreport.jaxb.POEProvisioningType;
import eu.europa.esig.validationreport.jaxb.POEType;
import eu.europa.esig.validationreport.jaxb.SignatureReferenceType;
import eu.europa.esig.validationreport.jaxb.SignatureValidationReportType;
import eu.europa.esig.validationreport.jaxb.VOReferenceType;
import eu.europa.esig.validationreport.jaxb.ValidationObjectListType;
import eu.europa.esig.validationreport.jaxb.ValidationObjectType;
import eu.europa.esig.validationreport.jaxb.ValidationReportType;
import eu.europa.esig.validationreport.jaxb.ValidationTimeInfoType;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DSS1988ExecutorTest extends AbstractProcessExecutorTest {

    @Test
    void dss1988Test() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(new File("src/test/resources/diag-data/dss-1988.xml"));
        assertNotNull(diagnosticData);

        DefaultSignatureProcessExecutor executor = new DefaultSignatureProcessExecutor();
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(loadDefaultPolicy());
        executor.setCurrentTime(diagnosticData.getValidationDate());

        Reports reports = executor.execute();

        SimpleReport simpleReport = reports.getSimpleReport();
        assertEquals(Indication.INDETERMINATE, simpleReport.getIndication(simpleReport.getFirstSignatureId()));
        assertEquals(SubIndication.CRYPTO_CONSTRAINTS_FAILURE_NO_POE, simpleReport.getSubIndication(simpleReport.getFirstSignatureId()));

        ValidationReportType etsiValidationReport = reports.getEtsiValidationReportJaxb();
        assertNotNull(etsiValidationReport);
        List<SignatureValidationReportType> signatureValidationReports = etsiValidationReport.getSignatureValidationReport();
        assertEquals(1, signatureValidationReports.size());

        SignatureValidationReportType signatureValidationReport = signatureValidationReports.get(0);
        ValidationTimeInfoType validationTimeInfo = signatureValidationReport.getValidationTimeInfo();
        assertNotNull(validationTimeInfo);
        assertEquals(diagnosticData.getValidationDate(), validationTimeInfo.getValidationTime());

        POEType bestSignatureTime = validationTimeInfo.getBestSignatureTime();
        assertNotNull(bestSignatureTime);

        assertEquals(TypeOfProof.VALIDATION, bestSignatureTime.getTypeOfProof());
        VOReferenceType poeObject = bestSignatureTime.getPOEObject();
        assertNotNull(poeObject);

        List<Object> voReference = poeObject.getVOReference();
        assertNotNull(voReference);
        assertEquals(1, voReference.size());

        Object timestampObject = voReference.get(0);
        assertInstanceOf(ValidationObjectType.class, timestampObject);
        ValidationObjectType timestampValidationObject = (ValidationObjectType) timestampObject;
        String timestampId = timestampValidationObject.getId();
        assertNotNull(timestampId);

        ValidationObjectListType signatureValidationObjects = etsiValidationReport.getSignatureValidationObjects();
        assertNotNull(signatureValidationObjects);
        assertTrue(Utils.isCollectionNotEmpty(signatureValidationObjects.getValidationObject()));
        for (ValidationObjectType validationObject : signatureValidationObjects.getValidationObject()) {
            if (timestampId.equals(validationObject.getId())) {
                timestampValidationObject = validationObject;
                break;
            }
        }

        assertEquals(ObjectType.TIMESTAMP, timestampValidationObject.getObjectType());
        POEProvisioningType poeProvisioning = timestampValidationObject.getPOEProvisioning();
        assertNotNull(poeProvisioning);

        List<VOReferenceType> timestampedObjects = poeProvisioning.getValidationObject();
        assertTrue(Utils.isCollectionNotEmpty(timestampedObjects));

        List<SignatureReferenceType> signatureReferences = poeProvisioning.getSignatureReference();
        assertEquals(1, signatureReferences.size());

        XmlSignatureDigestReference signatureDigestReference = diagnosticData.getSignatures().get(0).getSignatureDigestReference();

        SignatureReferenceType signatureReferenceType = signatureReferences.get(0);
        assertEquals(signatureDigestReference.getCanonicalizationMethod(), signatureReferenceType.getCanonicalizationMethod());
        assertEquals(signatureDigestReference.getDigestMethod(), DigestAlgorithm.forXML(signatureReferenceType.getDigestMethod()));
        assertArrayEquals(signatureDigestReference.getDigestValue(), signatureReferenceType.getDigestValue());

        DetailedReport detailedReport = reports.getDetailedReport();

        XmlBasicBuildingBlocks signatureBBB = detailedReport.getBasicBuildingBlockById(detailedReport.getFirstSignatureId());
        XmlSAV sav = signatureBBB.getSAV();
        assertEquals(Indication.INDETERMINATE, sav.getConclusion().getIndication());
        assertEquals(SubIndication.CRYPTO_CONSTRAINTS_FAILURE_NO_POE, sav.getConclusion().getSubIndication());
        assertEquals(3, sav.getConclusion().getErrors().size());

        int cryptoCheckCounter = 0;
        for (XmlConstraint constraint : sav.getConstraint()) {
            if (MessageTag.ACCM.getId().equals(constraint.getName().getKey())) {
                assertEquals(XmlStatus.NOT_OK, constraint.getStatus());
                assertEquals(i18nProvider.getMessage(MessageTag.ACCM_ANS, MessageTag.ACCM_POS_SIG_SIG), constraint.getError().getValue());
                assertEquals(i18nProvider.getMessage(MessageTag.CRYPTOGRAPHIC_CHECK_FAILURE,
                        i18nProvider.getMessage(MessageTag.ASCCM_AR_ANS_AKSNR, SignatureAlgorithm.RSA_SHA1.getName(), "2048", MessageTag.ACCM_POS_SIG_SIG),
                        ValidationProcessUtils.getFormattedDate(diagnosticData.getValidationDate())), constraint.getAdditionalInfo());
                ++cryptoCheckCounter;
            }
        }
        assertEquals(1, cryptoCheckCounter);

        XmlAOV aov = signatureBBB.getAOV();
        assertEquals(Indication.INDETERMINATE, aov.getConclusion().getIndication());
        assertEquals(SubIndication.CRYPTO_CONSTRAINTS_FAILURE_NO_POE, aov.getConclusion().getSubIndication());
        assertEquals(3, aov.getConclusion().getErrors().size());

        int sigValueCheckCounter = 0;
        int signCertAttrCheckCounter = 0;
        int referenceCheckCounter = 0;
        int signedPropertiesCheckCounter = 0;
        int signCertCheckCounter = 0;
        for (XmlConstraint constraint : aov.getConstraint()) {
            if (MessageTag.ACCM.getId().equals(constraint.getName().getKey())) {
                if (constraint.getName().getValue().contains(i18nProvider.getMessage(MessageTag.ACCM_POS_SIG_SIG))) {
                    assertEquals(XmlStatus.NOT_OK, constraint.getStatus());
                    assertEquals(i18nProvider.getMessage(MessageTag.ASCCM_AR_ANS_AKSNR, SignatureAlgorithm.RSA_SHA1.getName(), "2048", MessageTag.ACCM_POS_SIG_SIG),
                            constraint.getError().getValue());
                    assertTrue(constraint.getAdditionalInfo().contains(i18nProvider.getMessage(MessageTag.ASCCM_AR_ANS_AKSNR,
                            SignatureAlgorithm.RSA_SHA1.getName(), "2048", MessageTag.ACCM_POS_SIG_SIG)));
                    ++sigValueCheckCounter;

                } else if (constraint.getName().getValue().contains(i18nProvider.getMessage(MessageTag.ACCM_POS_SIG_CERT_REF))) {
                    assertEquals(XmlStatus.OK, constraint.getStatus());
                    assertTrue(constraint.getAdditionalInfo().contains(i18nProvider.getMessage(MessageTag.CRYPTOGRAPHIC_CHECK_SUCCESS_DM_WITH_ID,
                            DigestAlgorithm.SHA512.getName(), ValidationProcessUtils.getFormattedDate(diagnosticData.getValidationDate()), MessageTag.ACCM_POS_SIG_CERT_REF, "")));
                    ++signCertAttrCheckCounter;

                } else if (constraint.getName().getValue().contains(i18nProvider.getMessage(MessageTag.ACCM_POS_REF))) {
                    assertEquals(XmlStatus.NOT_OK, constraint.getStatus());
                    assertEquals(i18nProvider.getMessage(MessageTag.ASCCM_AR_ANS_ANR, DigestAlgorithm.SHA1.getName(), MessageTag.ACCM_POS_REF),
                            constraint.getError().getValue());
                    assertTrue(constraint.getAdditionalInfo().contains(i18nProvider.getMessage(MessageTag.ASCCM_AR_ANS_ANR,
                            DigestAlgorithm.SHA1.getName(), MessageTag.ACCM_POS_REF)));
                    ++referenceCheckCounter;

                } else if (constraint.getName().getValue().contains(i18nProvider.getMessage(MessageTag.ACCM_POS_SIGND_PRT))) {
                    assertEquals(XmlStatus.NOT_OK, constraint.getStatus());
                    assertEquals(i18nProvider.getMessage(MessageTag.ASCCM_AR_ANS_ANR, DigestAlgorithm.SHA1.getName(), MessageTag.ACCM_POS_SIGND_PRT),
                            constraint.getError().getValue());
                    assertTrue(constraint.getAdditionalInfo().contains(i18nProvider.getMessage(MessageTag.ASCCM_AR_ANS_ANR,
                            DigestAlgorithm.SHA1.getName(), MessageTag.ACCM_POS_SIGND_PRT)));
                    ++signedPropertiesCheckCounter;

                } else if (constraint.getName().getValue().contains(i18nProvider.getMessage(MessageTag.SIGNING_CERTIFICATE))) {
                    assertEquals(XmlStatus.OK, constraint.getStatus());
                    assertTrue(constraint.getAdditionalInfo().contains(i18nProvider.getMessage(MessageTag.CRYPTOGRAPHIC_CHECK_SUCCESS_KEY_SIZE,
                            SignatureAlgorithm.RSA_SHA256.getName(), "2048", ValidationProcessUtils.getFormattedDate(diagnosticData.getValidationDate()))));
                    ++signCertCheckCounter;
                }
            }
        }
        assertEquals(1, sigValueCheckCounter);
        assertEquals(1, signCertAttrCheckCounter);
        assertEquals(1, referenceCheckCounter);
        assertEquals(1, signedPropertiesCheckCounter);
        assertEquals(1, signCertCheckCounter);

        XmlCryptographicValidation cryptographicValidation = aov.getSignatureCryptographicValidation();
        assertEquals(SignatureAlgorithm.RSA_SHA1, SignatureAlgorithm.forXML(cryptographicValidation.getAlgorithm().getUri()));
        assertEquals("2048", cryptographicValidation.getAlgorithm().getKeyLength());

        checkReports(reports);
    }

}
