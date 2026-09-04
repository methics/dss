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
package eu.europa.esig.dss.validation.executor;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.TypeFactory;
import com.fasterxml.jackson.module.jakarta.xmlbind.JakartaXmlBindAnnotationIntrospector;
import eu.europa.esig.dss.detailedreport.DetailedReport;
import eu.europa.esig.dss.detailedreport.DetailedReportFacade;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCertificate;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraint;
import eu.europa.esig.dss.detailedreport.jaxb.XmlDetailedReport;
import eu.europa.esig.dss.detailedreport.jaxb.XmlQWACProcess;
import eu.europa.esig.dss.detailedreport.jaxb.XmlStatus;
import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationQWACProcess;
import eu.europa.esig.dss.diagnostic.DiagnosticDataFacade;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificateExtension;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificatePolicies;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificatePolicy;
import eu.europa.esig.dss.diagnostic.jaxb.XmlCertificateRevocation;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlOID;
import eu.europa.esig.dss.diagnostic.jaxb.XmlQcStatements;
import eu.europa.esig.dss.enumerations.CertificateExtensionEnum;
import eu.europa.esig.dss.enumerations.CertificatePolicy;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.CertificateStatus;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.QCTypeEnum;
import eu.europa.esig.dss.enumerations.QWACProfile;
import eu.europa.esig.dss.enumerations.RevocationReason;
import eu.europa.esig.dss.enumerations.ValidationTime;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.simplecertificatereport.SimpleCertificateReport;
import eu.europa.esig.dss.simplecertificatereport.SimpleCertificateReportFacade;
import eu.europa.esig.dss.simplecertificatereport.jaxb.XmlSimpleCertificateReport;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.executor.certificate.qwac.QWACCertificateProcessExecutor;
import eu.europa.esig.dss.validation.policy.ValidationPolicyLoader;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;
import eu.europa.esig.dss.validation.reports.CertificateReports;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class QWAC1CertificateProcessExecutorTest extends AbstractTestValidationExecutor {

    private static final String QWAC_VALIDATION_POLICY_LOCATION = "/diag-data/policy/qwac-constraint.xml";

    private static I18nProvider i18nProvider;

    @BeforeAll
    static void init() {
        i18nProvider = new I18nProvider(Locale.getDefault());
    }

    @Test
    void validTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(
                new File("src/test/resources/diag-data/qwac-validation/1-qwac-valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        String certificateId = "C-8ECE05699B2196B46CBB3078FB2F2213C9C04EAF717FD1EBD2F853FA40656009";

        QWACCertificateProcessExecutor executor = new QWACCertificateProcessExecutor();
        executor.setCertificateId(certificateId);
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(loadDefaultPolicy());
        executor.setCurrentTime(diagnosticData.getValidationDate());

        CertificateReports reports = executor.execute();

        SimpleCertificateReport simpleReport = reports.getSimpleReport();
        assertNotNull(simpleReport);

        assertEquals(CertificateQualification.QCERT_FOR_WSA, simpleReport.getQualificationAtCertificateIssuance());
        assertTrue(Utils.isCollectionEmpty(simpleReport.getQualificationInfoAtIssuanceTime(certificateId)));
        assertTrue(Utils.isCollectionEmpty(simpleReport.getQualificationWarningsAtIssuanceTime(certificateId)));
        assertTrue(Utils.isCollectionEmpty(simpleReport.getQualificationErrorsAtIssuanceTime(certificateId)));

        assertEquals(CertificateQualification.QCERT_FOR_WSA, simpleReport.getQualificationAtValidationTime());
        assertTrue(Utils.isCollectionEmpty(simpleReport.getQualificationInfoAtValidationTime(certificateId)));
        assertTrue(Utils.isCollectionEmpty(simpleReport.getQualificationWarningsAtValidationTime(certificateId)));
        assertTrue(Utils.isCollectionEmpty(simpleReport.getQualificationErrorsAtValidationTime(certificateId)));

        assertEquals(QWACProfile.QWAC_1, simpleReport.getQWACProfile());

        DetailedReport detailedReport = reports.getDetailedReport();
        assertEquals(QWACProfile.QWAC_1, detailedReport.getCertificateQWACProfile(certificateId));

        XmlCertificate xmlCertificate = detailedReport.getXmlCertificateById(certificateId);
        assertNotNull(xmlCertificate);

        XmlQWACProcess qwacProcess = xmlCertificate.getQWACProcess();
        assertNotNull(qwacProcess);
        assertEquals(certificateId, qwacProcess.getId());
        assertEquals(QWACProfile.QWAC_1, qwacProcess.getQWACType());
        assertEquals(Indication.PASSED, qwacProcess.getConclusion().getIndication());

        boolean isQWACValidCheckFound = false;
        for (XmlConstraint xmlConstraint : qwacProcess.getConstraint()) {
            if (MessageTag.QWAC_VALID.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                isQWACValidCheckFound = true;
            }
        }
        assertTrue(isQWACValidCheckFound);

        // 1-qwac checks
        boolean certPolicyCheckPresent = false;
        boolean certQualConclusiveCheckPresent = false;
        boolean wsaCheckAtIssuanceTimePresent = false;
        boolean wsaCheckAtValidationTimePresent = false;
        boolean certValidityPeriodCheckPresent = false;
        boolean domainNamePresent = false;
        boolean bbbCheckConclusive = false;

        // 2-qwac checks
        boolean is2QWACProcessPresent = false;
        for (XmlValidationQWACProcess xmlValidationQWACProcess : qwacProcess.getValidationQWACProcess()) {
            if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.QWAC_1)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.PASSED, xmlValidationQWACProcess.getConclusion().getIndication());
                for (XmlConstraint xmlConstraint : xmlValidationQWACProcess.getConstraint()) {
                    assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                    if (MessageTag.QWAC_CERT_POLICY.getId().equals(xmlConstraint.getName().getKey())) {
                        certPolicyCheckPresent = true;
                    } else if (MessageTag.QWAC_CERT_QUAL_CONCLUSIVE.getId().equals(xmlConstraint.getName().getKey())) {
                        certQualConclusiveCheckPresent = true;
                    } else if (MessageTag.QWAC_IS_WSA_AT_TIME.getId().equals(xmlConstraint.getName().getKey())) {
                        if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.CERTIFICATE_ISSUANCE_TIME)).equals(xmlConstraint.getName().getValue())) {
                            wsaCheckAtIssuanceTimePresent = true;
                        } else if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.VALIDATION_TIME)).equals(xmlConstraint.getName().getValue())) {
                            wsaCheckAtValidationTimePresent = true;
                        }
                    } else if (MessageTag.QWAC_VAL_PERIOD.getId().equals(xmlConstraint.getName().getKey())) {
                        certValidityPeriodCheckPresent = true;
                    } else if (MessageTag.QWAC_DOMAIN_NAME.getId().equals(xmlConstraint.getName().getKey())) {
                        domainNamePresent = true;
                    } else if (MessageTag.BBB_ACCEPT.getId().equals(xmlConstraint.getName().getKey())) {
                        bbbCheckConclusive = true;
                    }
                }

            } else if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.TLS_BY_QWAC_2)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                is2QWACProcessPresent = true;
            }
        }
        assertTrue(certPolicyCheckPresent);
        assertTrue(certQualConclusiveCheckPresent);
        assertTrue(wsaCheckAtIssuanceTimePresent);
        assertTrue(wsaCheckAtValidationTimePresent);
        assertTrue(certValidityPeriodCheckPresent);
        assertTrue(domainNamePresent);
        assertTrue(bbbCheckConclusive);
        assertTrue(is2QWACProcessPresent);

        checkReports(reports);
    }

    @Test
    void noTLTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(
                new File("src/test/resources/diag-data/qwac-validation/1-qwac-no-tl-diag-data.xml"));
        assertNotNull(diagnosticData);

        String certificateId = "C-8ECE05699B2196B46CBB3078FB2F2213C9C04EAF717FD1EBD2F853FA40656009";

        QWACCertificateProcessExecutor executor = new QWACCertificateProcessExecutor();
        executor.setCertificateId(certificateId);
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(loadDefaultPolicy());
        executor.setCurrentTime(diagnosticData.getValidationDate());

        CertificateReports reports = executor.execute();

        SimpleCertificateReport simpleReport = reports.getSimpleReport();
        assertNotNull(simpleReport);

        assertEquals(QWACProfile.NOT_QWAC, simpleReport.getQWACProfile());
        assertTrue(checkMessageValuePresence(simpleReport.getQWACValidationErrors(certificateId),
                i18nProvider.getMessage(MessageTag.QWAC_VALID_ANS)));

        DetailedReport detailedReport = reports.getDetailedReport();
        assertEquals(QWACProfile.NOT_QWAC, detailedReport.getCertificateQWACProfile(certificateId));

        XmlCertificate xmlCertificate = detailedReport.getXmlCertificateById(certificateId);
        assertNotNull(xmlCertificate);

        XmlQWACProcess qwacProcess = xmlCertificate.getQWACProcess();
        assertNotNull(qwacProcess);
        assertEquals(certificateId, qwacProcess.getId());
        assertEquals(QWACProfile.NOT_QWAC, qwacProcess.getQWACType());
        assertEquals(Indication.FAILED, qwacProcess.getConclusion().getIndication());

        boolean isQWACValidCheckFound = false;
        for (XmlConstraint xmlConstraint : qwacProcess.getConstraint()) {
            if (MessageTag.QWAC_VALID.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                assertEquals(MessageTag.QWAC_VALID_ANS.getId(), xmlConstraint.getError().getKey());
                isQWACValidCheckFound = true;
            }
        }
        assertTrue(isQWACValidCheckFound);

        // 1-qwac checks
        boolean certPolicyCheckPresent = false;
        boolean certQualConclusiveCheckPresent = false;
        boolean wsaCheckAtIssuanceTimePresent = false;
        boolean wsaCheckAtValidationTimePresent = false;
        boolean certValidityPeriodCheckPresent = false;
        boolean domainNamePresent = false;
        boolean bbbCheckConclusive = false;

        // 2-qwac checks
        boolean is2QWACProcessPresent = false;
        for (XmlValidationQWACProcess xmlValidationQWACProcess : qwacProcess.getValidationQWACProcess()) {
            if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.QWAC_1)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                for (XmlConstraint xmlConstraint : xmlValidationQWACProcess.getConstraint()) {
                    if (MessageTag.QWAC_CERT_POLICY.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                        certPolicyCheckPresent = true;
                    } else if (MessageTag.QWAC_CERT_QUAL_CONCLUSIVE.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                        assertEquals(MessageTag.QWAC_CERT_QUAL_CONCLUSIVE_ANS.getId(), xmlConstraint.getError().getKey());
                        certQualConclusiveCheckPresent = true;
                    } else if (MessageTag.QWAC_IS_WSA_AT_TIME.getId().equals(xmlConstraint.getName().getKey())) {
                        if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.CERTIFICATE_ISSUANCE_TIME)).equals(xmlConstraint.getName().getValue())) {
                            wsaCheckAtIssuanceTimePresent = true;
                        } else if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.VALIDATION_TIME)).equals(xmlConstraint.getName().getValue())) {
                            wsaCheckAtValidationTimePresent = true;
                        }
                    } else if (MessageTag.QWAC_VAL_PERIOD.getId().equals(xmlConstraint.getName().getKey())) {
                        certValidityPeriodCheckPresent = true;
                    } else if (MessageTag.QWAC_DOMAIN_NAME.getId().equals(xmlConstraint.getName().getKey())) {
                        domainNamePresent = true;
                    } else if (MessageTag.BBB_ACCEPT.getId().equals(xmlConstraint.getName().getKey())) {
                        bbbCheckConclusive = true;
                    }
                }

            } else if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.TLS_BY_QWAC_2)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                is2QWACProcessPresent = true;
            }
        }
        assertTrue(certPolicyCheckPresent);
        assertTrue(certQualConclusiveCheckPresent);
        assertFalse(wsaCheckAtIssuanceTimePresent);
        assertFalse(wsaCheckAtValidationTimePresent);
        assertFalse(certValidityPeriodCheckPresent);
        assertFalse(domainNamePresent);
        assertFalse(bbbCheckConclusive);
        assertTrue(is2QWACProcessPresent);

        checkReports(reports);
    }

    @Test
    void qwac2CertPolicyTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(
                new File("src/test/resources/diag-data/qwac-validation/1-qwac-valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        for (eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate xmlCertificate : diagnosticData.getUsedCertificates()) {
            for (XmlCertificateExtension certificateExtension : xmlCertificate.getCertificateExtensions()) {
                if (CertificateExtensionEnum.CERTIFICATE_POLICIES.getOid().equals(certificateExtension.getOID())) {
                    XmlCertificatePolicies xmlCertificatePolicies = (XmlCertificatePolicies) certificateExtension;
                    XmlCertificatePolicy xmlCertificatePolicy = new XmlCertificatePolicy();
                    xmlCertificatePolicy.setValue(CertificatePolicy.QNCP_WEB_GEN.getOid());
                    xmlCertificatePolicies.getCertificatePolicy().clear();
                    xmlCertificatePolicies.getCertificatePolicy().add(xmlCertificatePolicy);
                }
            }
        }

        String certificateId = "C-8ECE05699B2196B46CBB3078FB2F2213C9C04EAF717FD1EBD2F853FA40656009";

        QWACCertificateProcessExecutor executor = new QWACCertificateProcessExecutor();
        executor.setCertificateId(certificateId);
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(loadDefaultPolicy());
        executor.setCurrentTime(diagnosticData.getValidationDate());

        CertificateReports reports = executor.execute();

        SimpleCertificateReport simpleReport = reports.getSimpleReport();
        assertNotNull(simpleReport);

        assertEquals(QWACProfile.NOT_QWAC, simpleReport.getQWACProfile());
        assertTrue(checkMessageValuePresence(simpleReport.getQWACValidationErrors(certificateId),
                i18nProvider.getMessage(MessageTag.QWAC_VALID_ANS)));

        DetailedReport detailedReport = reports.getDetailedReport();
        assertEquals(QWACProfile.NOT_QWAC, detailedReport.getCertificateQWACProfile(certificateId));

        XmlCertificate xmlCertificate = detailedReport.getXmlCertificateById(certificateId);
        assertNotNull(xmlCertificate);

        XmlQWACProcess qwacProcess = xmlCertificate.getQWACProcess();
        assertNotNull(qwacProcess);
        assertEquals(certificateId, qwacProcess.getId());
        assertEquals(QWACProfile.NOT_QWAC, qwacProcess.getQWACType());
        assertEquals(Indication.FAILED, qwacProcess.getConclusion().getIndication());

        boolean isQWACValidCheckFound = false;
        for (XmlConstraint xmlConstraint : qwacProcess.getConstraint()) {
            if (MessageTag.QWAC_VALID.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                assertEquals(MessageTag.QWAC_VALID_ANS.getId(), xmlConstraint.getError().getKey());
                isQWACValidCheckFound = true;
            }
        }
        assertTrue(isQWACValidCheckFound);

        // 1-qwac checks
        boolean certPolicyCheckPresent = false;
        boolean certQualConclusiveCheckPresent = false;
        boolean wsaCheckAtIssuanceTimePresent = false;
        boolean wsaCheckAtValidationTimePresent = false;
        boolean certValidityPeriodCheckPresent = false;
        boolean domainNamePresent = false;
        boolean bbbCheckConclusive = false;

        // 2-qwac checks
        boolean is2QWACProcessPresent = false;
        for (XmlValidationQWACProcess xmlValidationQWACProcess : qwacProcess.getValidationQWACProcess()) {
            if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.QWAC_1)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                for (XmlConstraint xmlConstraint : xmlValidationQWACProcess.getConstraint()) {
                    if (MessageTag.QWAC_CERT_POLICY.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                        assertEquals(MessageTag.QWAC_CERT_POLICY_ANS.getId(), xmlConstraint.getError().getKey());
                        certPolicyCheckPresent = true;
                    } else if (MessageTag.QWAC_CERT_QUAL_CONCLUSIVE.getId().equals(xmlConstraint.getName().getKey())) {
                        certQualConclusiveCheckPresent = true;
                    } else if (MessageTag.QWAC_IS_WSA_AT_TIME.getId().equals(xmlConstraint.getName().getKey())) {
                        if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.CERTIFICATE_ISSUANCE_TIME)).equals(xmlConstraint.getName().getValue())) {
                            wsaCheckAtIssuanceTimePresent = true;
                        } else if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.VALIDATION_TIME)).equals(xmlConstraint.getName().getValue())) {
                            wsaCheckAtValidationTimePresent = true;
                        }
                    } else if (MessageTag.QWAC_VAL_PERIOD.getId().equals(xmlConstraint.getName().getKey())) {
                        certValidityPeriodCheckPresent = true;
                    } else if (MessageTag.QWAC_DOMAIN_NAME.getId().equals(xmlConstraint.getName().getKey())) {
                        domainNamePresent = true;
                    } else if (MessageTag.BBB_ACCEPT.getId().equals(xmlConstraint.getName().getKey())) {
                        bbbCheckConclusive = true;
                    }
                }

            } else if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.TLS_BY_QWAC_2)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                is2QWACProcessPresent = true;
            }
        }
        assertTrue(certPolicyCheckPresent);
        assertFalse(certQualConclusiveCheckPresent);
        assertFalse(wsaCheckAtIssuanceTimePresent);
        assertFalse(wsaCheckAtValidationTimePresent);
        assertFalse(certValidityPeriodCheckPresent);
        assertFalse(domainNamePresent);
        assertFalse(bbbCheckConclusive);
        assertTrue(is2QWACProcessPresent);

        checkReports(reports);
    }

    @Test
    void certForESigTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(
                new File("src/test/resources/diag-data/qwac-validation/1-qwac-valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        for (eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate xmlCertificate : diagnosticData.getUsedCertificates()) {
            for (XmlCertificateExtension certificateExtension : xmlCertificate.getCertificateExtensions()) {
                if (CertificateExtensionEnum.QC_STATEMENTS.getOid().equals(certificateExtension.getOID())) {
                    XmlQcStatements xmlQcStatements = (XmlQcStatements) certificateExtension;
                    XmlOID xmlOID = new XmlOID();
                    xmlOID.setValue(QCTypeEnum.QCT_ESIGN.getOid());
                    xmlQcStatements.getQcTypes().clear();
                    xmlQcStatements.getQcTypes().add(xmlOID);
                }
            }
        }

        String certificateId = "C-8ECE05699B2196B46CBB3078FB2F2213C9C04EAF717FD1EBD2F853FA40656009";

        QWACCertificateProcessExecutor executor = new QWACCertificateProcessExecutor();
        executor.setCertificateId(certificateId);
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(loadDefaultPolicy());
        executor.setCurrentTime(diagnosticData.getValidationDate());

        CertificateReports reports = executor.execute();

        SimpleCertificateReport simpleReport = reports.getSimpleReport();
        assertNotNull(simpleReport);

        assertEquals(QWACProfile.NOT_QWAC, simpleReport.getQWACProfile());
        assertTrue(checkMessageValuePresence(simpleReport.getQWACValidationErrors(certificateId),
                i18nProvider.getMessage(MessageTag.QWAC_VALID_ANS)));

        DetailedReport detailedReport = reports.getDetailedReport();
        assertEquals(QWACProfile.NOT_QWAC, detailedReport.getCertificateQWACProfile(certificateId));

        XmlCertificate xmlCertificate = detailedReport.getXmlCertificateById(certificateId);
        assertNotNull(xmlCertificate);

        XmlQWACProcess qwacProcess = xmlCertificate.getQWACProcess();
        assertNotNull(qwacProcess);
        assertEquals(certificateId, qwacProcess.getId());
        assertEquals(QWACProfile.NOT_QWAC, qwacProcess.getQWACType());
        assertEquals(Indication.FAILED, qwacProcess.getConclusion().getIndication());

        boolean isQWACValidCheckFound = false;
        for (XmlConstraint xmlConstraint : qwacProcess.getConstraint()) {
            if (MessageTag.QWAC_VALID.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                assertEquals(MessageTag.QWAC_VALID_ANS.getId(), xmlConstraint.getError().getKey());
                isQWACValidCheckFound = true;
            }
        }
        assertTrue(isQWACValidCheckFound);

        // 1-qwac checks
        boolean certPolicyCheckPresent = false;
        boolean certQualConclusiveCheckPresent = false;
        boolean wsaCheckAtIssuanceTimePresent = false;
        boolean wsaCheckAtValidationTimePresent = false;
        boolean certValidityPeriodCheckPresent = false;
        boolean domainNamePresent = false;
        boolean bbbCheckConclusive = false;

        // 2-qwac checks
        boolean is2QWACProcessPresent = false;
        for (XmlValidationQWACProcess xmlValidationQWACProcess : qwacProcess.getValidationQWACProcess()) {
            if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.QWAC_1)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                for (XmlConstraint xmlConstraint : xmlValidationQWACProcess.getConstraint()) {
                    if (MessageTag.QWAC_CERT_POLICY.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                        certPolicyCheckPresent = true;
                    } else if (MessageTag.QWAC_CERT_QUAL_CONCLUSIVE.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                        certQualConclusiveCheckPresent = true;
                    } else if (MessageTag.QWAC_IS_WSA_AT_TIME.getId().equals(xmlConstraint.getName().getKey())) {
                        if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.CERTIFICATE_ISSUANCE_TIME)).equals(xmlConstraint.getName().getValue())) {
                            assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                            assertEquals(MessageTag.QWAC_IS_WSA_AT_TIME_ANS.getId(), xmlConstraint.getError().getKey());
                            wsaCheckAtIssuanceTimePresent = true;
                        } else if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.VALIDATION_TIME)).equals(xmlConstraint.getName().getValue())) {
                            wsaCheckAtValidationTimePresent = true;
                        }
                    } else if (MessageTag.QWAC_VAL_PERIOD.getId().equals(xmlConstraint.getName().getKey())) {
                        certValidityPeriodCheckPresent = true;
                    } else if (MessageTag.QWAC_DOMAIN_NAME.getId().equals(xmlConstraint.getName().getKey())) {
                        domainNamePresent = true;
                    } else if (MessageTag.BBB_ACCEPT.getId().equals(xmlConstraint.getName().getKey())) {
                        bbbCheckConclusive = true;
                    }
                }

            } else if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.TLS_BY_QWAC_2)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                is2QWACProcessPresent = true;
            }
        }
        assertTrue(certPolicyCheckPresent);
        assertTrue(certQualConclusiveCheckPresent);
        assertTrue(wsaCheckAtIssuanceTimePresent);
        assertFalse(wsaCheckAtValidationTimePresent);
        assertFalse(certValidityPeriodCheckPresent);
        assertFalse(domainNamePresent);
        assertFalse(bbbCheckConclusive);
        assertTrue(is2QWACProcessPresent);

        checkReports(reports);
    }

    @Test
    void certExpiredTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(
                new File("src/test/resources/diag-data/qwac-validation/1-qwac-valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        String certificateId = "C-8ECE05699B2196B46CBB3078FB2F2213C9C04EAF717FD1EBD2F853FA40656009";

        for (eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate xmlCertificate : diagnosticData.getUsedCertificates()) {
            if (certificateId.equals(xmlCertificate.getId())) {
                xmlCertificate.setNotAfter(DSSUtils.getUtcDate(2025, Calendar.NOVEMBER, 1));
            }
        }

        EtsiValidationPolicy validationPolicy = loadDefaultPolicy();
        LevelConstraint levelConstraint = new LevelConstraint();
        levelConstraint.setLevel(Level.WARN);
        validationPolicy.getSignatureConstraints().getBasicSignatureConstraints().getSigningCertificate().setNotExpired(levelConstraint);
        validationPolicy.getSignatureConstraints().getBasicSignatureConstraints().getSigningCertificate().setAcceptableRevocationDataFound(levelConstraint);

        QWACCertificateProcessExecutor executor = new QWACCertificateProcessExecutor();
        executor.setCertificateId(certificateId);
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(validationPolicy);
        executor.setCurrentTime(diagnosticData.getValidationDate());

        CertificateReports reports = executor.execute();

        SimpleCertificateReport simpleReport = reports.getSimpleReport();
        assertNotNull(simpleReport);

        assertEquals(QWACProfile.NOT_QWAC, simpleReport.getQWACProfile());
        assertTrue(checkMessageValuePresence(simpleReport.getQWACValidationErrors(certificateId),
                i18nProvider.getMessage(MessageTag.QWAC_VALID_ANS)));

        DetailedReport detailedReport = reports.getDetailedReport();
        assertEquals(QWACProfile.NOT_QWAC, detailedReport.getCertificateQWACProfile(certificateId));

        XmlCertificate xmlCertificate = detailedReport.getXmlCertificateById(certificateId);
        assertNotNull(xmlCertificate);

        XmlQWACProcess qwacProcess = xmlCertificate.getQWACProcess();
        assertNotNull(qwacProcess);
        assertEquals(certificateId, qwacProcess.getId());
        assertEquals(QWACProfile.NOT_QWAC, qwacProcess.getQWACType());
        assertEquals(Indication.FAILED, qwacProcess.getConclusion().getIndication());

        boolean isQWACValidCheckFound = false;
        for (XmlConstraint xmlConstraint : qwacProcess.getConstraint()) {
            if (MessageTag.QWAC_VALID.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                assertEquals(MessageTag.QWAC_VALID_ANS.getId(), xmlConstraint.getError().getKey());
                isQWACValidCheckFound = true;
            }
        }
        assertTrue(isQWACValidCheckFound);

        // 1-qwac checks
        boolean certPolicyCheckPresent = false;
        boolean certQualConclusiveCheckPresent = false;
        boolean wsaCheckAtIssuanceTimePresent = false;
        boolean wsaCheckAtValidationTimePresent = false;
        boolean certValidityPeriodCheckPresent = false;
        boolean domainNamePresent = false;
        boolean bbbCheckConclusive = false;

        // 2-qwac checks
        boolean is2QWACProcessPresent = false;
        for (XmlValidationQWACProcess xmlValidationQWACProcess : qwacProcess.getValidationQWACProcess()) {
            if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.QWAC_1)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                for (XmlConstraint xmlConstraint : xmlValidationQWACProcess.getConstraint()) {
                    if (MessageTag.QWAC_CERT_POLICY.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                        certPolicyCheckPresent = true;
                    } else if (MessageTag.QWAC_CERT_QUAL_CONCLUSIVE.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                        certQualConclusiveCheckPresent = true;
                    } else if (MessageTag.QWAC_IS_WSA_AT_TIME.getId().equals(xmlConstraint.getName().getKey())) {
                        if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.CERTIFICATE_ISSUANCE_TIME)).equals(xmlConstraint.getName().getValue())) {
                            assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                            wsaCheckAtIssuanceTimePresent = true;
                        } else if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.VALIDATION_TIME)).equals(xmlConstraint.getName().getValue())) {
                            assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                            wsaCheckAtValidationTimePresent = true;
                        }
                    } else if (MessageTag.QWAC_VAL_PERIOD.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                        assertEquals(MessageTag.QWAC_VAL_PERIOD_ANS.getId(), xmlConstraint.getError().getKey());
                        certValidityPeriodCheckPresent = true;
                    } else if (MessageTag.QWAC_DOMAIN_NAME.getId().equals(xmlConstraint.getName().getKey())) {
                        domainNamePresent = true;
                    } else if (MessageTag.BBB_ACCEPT.getId().equals(xmlConstraint.getName().getKey())) {
                        bbbCheckConclusive = true;
                    }
                }

            } else if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.TLS_BY_QWAC_2)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                is2QWACProcessPresent = true;
            }
        }
        assertTrue(certPolicyCheckPresent);
        assertTrue(certQualConclusiveCheckPresent);
        assertTrue(wsaCheckAtIssuanceTimePresent);
        assertTrue(wsaCheckAtValidationTimePresent);
        assertTrue(certValidityPeriodCheckPresent);
        assertFalse(domainNamePresent);
        assertFalse(bbbCheckConclusive);
        assertTrue(is2QWACProcessPresent);

        checkReports(reports);
    }

    @Test
    void diffDomainNameTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(
                new File("src/test/resources/diag-data/qwac-validation/1-qwac-valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        diagnosticData.getConnectionInfo().setUrl("https://nowina.lu");

        String certificateId = "C-8ECE05699B2196B46CBB3078FB2F2213C9C04EAF717FD1EBD2F853FA40656009";

        QWACCertificateProcessExecutor executor = new QWACCertificateProcessExecutor();
        executor.setCertificateId(certificateId);
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(loadDefaultPolicy());
        executor.setCurrentTime(diagnosticData.getValidationDate());

        CertificateReports reports = executor.execute();

        SimpleCertificateReport simpleReport = reports.getSimpleReport();
        assertNotNull(simpleReport);

        assertEquals(QWACProfile.NOT_QWAC, simpleReport.getQWACProfile());
        assertTrue(checkMessageValuePresence(simpleReport.getQWACValidationErrors(certificateId),
                i18nProvider.getMessage(MessageTag.QWAC_VALID_ANS)));

        DetailedReport detailedReport = reports.getDetailedReport();
        assertEquals(QWACProfile.NOT_QWAC, detailedReport.getCertificateQWACProfile(certificateId));

        XmlCertificate xmlCertificate = detailedReport.getXmlCertificateById(certificateId);
        assertNotNull(xmlCertificate);

        XmlQWACProcess qwacProcess = xmlCertificate.getQWACProcess();
        assertNotNull(qwacProcess);
        assertEquals(certificateId, qwacProcess.getId());
        assertEquals(QWACProfile.NOT_QWAC, qwacProcess.getQWACType());
        assertEquals(Indication.FAILED, qwacProcess.getConclusion().getIndication());

        boolean isQWACValidCheckFound = false;
        for (XmlConstraint xmlConstraint : qwacProcess.getConstraint()) {
            if (MessageTag.QWAC_VALID.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                assertEquals(MessageTag.QWAC_VALID_ANS.getId(), xmlConstraint.getError().getKey());
                isQWACValidCheckFound = true;
            }
        }
        assertTrue(isQWACValidCheckFound);

        // 1-qwac checks
        boolean certPolicyCheckPresent = false;
        boolean certQualConclusiveCheckPresent = false;
        boolean wsaCheckAtIssuanceTimePresent = false;
        boolean wsaCheckAtValidationTimePresent = false;
        boolean certValidityPeriodCheckPresent = false;
        boolean domainNamePresent = false;
        boolean bbbCheckConclusive = false;

        // 2-qwac checks
        boolean is2QWACProcessPresent = false;
        for (XmlValidationQWACProcess xmlValidationQWACProcess : qwacProcess.getValidationQWACProcess()) {
            if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.QWAC_1)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                for (XmlConstraint xmlConstraint : xmlValidationQWACProcess.getConstraint()) {
                    if (MessageTag.QWAC_CERT_POLICY.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                        certPolicyCheckPresent = true;
                    } else if (MessageTag.QWAC_CERT_QUAL_CONCLUSIVE.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                        certQualConclusiveCheckPresent = true;
                    } else if (MessageTag.QWAC_IS_WSA_AT_TIME.getId().equals(xmlConstraint.getName().getKey())) {
                        if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.CERTIFICATE_ISSUANCE_TIME)).equals(xmlConstraint.getName().getValue())) {
                            assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                            wsaCheckAtIssuanceTimePresent = true;
                        } else if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.VALIDATION_TIME)).equals(xmlConstraint.getName().getValue())) {
                            assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                            wsaCheckAtValidationTimePresent = true;
                        }
                    } else if (MessageTag.QWAC_VAL_PERIOD.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                        certValidityPeriodCheckPresent = true;
                    } else if (MessageTag.QWAC_DOMAIN_NAME.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                        assertEquals(MessageTag.QWAC_DOMAIN_NAME_ANS.getId(), xmlConstraint.getError().getKey());
                        domainNamePresent = true;
                    } else if (MessageTag.BBB_ACCEPT.getId().equals(xmlConstraint.getName().getKey())) {
                        bbbCheckConclusive = true;
                    }
                }

            } else if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.TLS_BY_QWAC_2)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                is2QWACProcessPresent = true;
            }
        }
        assertTrue(certPolicyCheckPresent);
        assertTrue(certQualConclusiveCheckPresent);
        assertTrue(wsaCheckAtIssuanceTimePresent);
        assertTrue(wsaCheckAtValidationTimePresent);
        assertTrue(certValidityPeriodCheckPresent);
        assertTrue(domainNamePresent);
        assertFalse(bbbCheckConclusive);
        assertTrue(is2QWACProcessPresent);

        checkReports(reports);
    }

    @Test
    void certRevokedTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(
                new File("src/test/resources/diag-data/qwac-validation/1-qwac-valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        String certificateId = "C-8ECE05699B2196B46CBB3078FB2F2213C9C04EAF717FD1EBD2F853FA40656009";

        for (eu.europa.esig.dss.diagnostic.jaxb.XmlCertificate xmlCertificate : diagnosticData.getUsedCertificates()) {
            if (certificateId.equals(xmlCertificate.getId())) {
                List<XmlCertificateRevocation> revocations = xmlCertificate.getRevocations();
                XmlCertificateRevocation xmlCertificateRevocation = revocations.get(0);
                xmlCertificateRevocation.setStatus(CertificateStatus.REVOKED);
                xmlCertificateRevocation.setReason(RevocationReason.SUPERSEDED);
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(diagnosticData.getValidationDate());
                calendar.add(Calendar.MONTH, -1);
                xmlCertificateRevocation.setRevocationDate(calendar.getTime());
            }
        }

        QWACCertificateProcessExecutor executor = new QWACCertificateProcessExecutor();
        executor.setCertificateId(certificateId);
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(loadDefaultPolicy());
        executor.setCurrentTime(diagnosticData.getValidationDate());

        CertificateReports reports = executor.execute();

        SimpleCertificateReport simpleReport = reports.getSimpleReport();
        assertNotNull(simpleReport);

        assertEquals(QWACProfile.NOT_QWAC, simpleReport.getQWACProfile());
        assertTrue(checkMessageValuePresence(simpleReport.getQWACValidationErrors(certificateId),
                i18nProvider.getMessage(MessageTag.QWAC_VALID_ANS)));

        DetailedReport detailedReport = reports.getDetailedReport();
        assertEquals(QWACProfile.NOT_QWAC, detailedReport.getCertificateQWACProfile(certificateId));

        XmlCertificate xmlCertificate = detailedReport.getXmlCertificateById(certificateId);
        assertNotNull(xmlCertificate);

        XmlQWACProcess qwacProcess = xmlCertificate.getQWACProcess();
        assertNotNull(qwacProcess);
        assertEquals(certificateId, qwacProcess.getId());
        assertEquals(QWACProfile.NOT_QWAC, qwacProcess.getQWACType());
        assertEquals(Indication.FAILED, qwacProcess.getConclusion().getIndication());

        boolean isQWACValidCheckFound = false;
        for (XmlConstraint xmlConstraint : qwacProcess.getConstraint()) {
            if (MessageTag.QWAC_VALID.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                assertEquals(MessageTag.QWAC_VALID_ANS.getId(), xmlConstraint.getError().getKey());
                isQWACValidCheckFound = true;
            }
        }
        assertTrue(isQWACValidCheckFound);

        // 1-qwac checks
        boolean certPolicyCheckPresent = false;
        boolean certQualConclusiveCheckPresent = false;
        boolean wsaCheckAtIssuanceTimePresent = false;
        boolean wsaCheckAtValidationTimePresent = false;
        boolean certValidityPeriodCheckPresent = false;
        boolean domainNamePresent = false;
        boolean bbbCheckConclusive = false;

        // 2-qwac checks
        boolean is2QWACProcessPresent = false;
        for (XmlValidationQWACProcess xmlValidationQWACProcess : qwacProcess.getValidationQWACProcess()) {
            if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.QWAC_1)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.FAILED, xmlValidationQWACProcess.getConclusion().getIndication());
                for (XmlConstraint xmlConstraint : xmlValidationQWACProcess.getConstraint()) {
                    if (MessageTag.QWAC_CERT_POLICY.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                        certPolicyCheckPresent = true;
                    } else if (MessageTag.QWAC_CERT_QUAL_CONCLUSIVE.getId().equals(xmlConstraint.getName().getKey())) {
                        assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                        assertEquals(MessageTag.QWAC_CERT_QUAL_CONCLUSIVE_ANS.getId(), xmlConstraint.getError().getKey());
                        certQualConclusiveCheckPresent = true;
                    } else if (MessageTag.QWAC_IS_WSA_AT_TIME.getId().equals(xmlConstraint.getName().getKey())) {
                        if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.CERTIFICATE_ISSUANCE_TIME)).equals(xmlConstraint.getName().getValue())) {
                            wsaCheckAtIssuanceTimePresent = true;
                        } else if (i18nProvider.getMessage(MessageTag.QWAC_IS_WSA_AT_TIME, ValidationProcessUtils.getValidationTimeMessageTag(
                                ValidationTime.VALIDATION_TIME)).equals(xmlConstraint.getName().getValue())) {
                            wsaCheckAtValidationTimePresent = true;
                        }
                    } else if (MessageTag.QWAC_VAL_PERIOD.getId().equals(xmlConstraint.getName().getKey())) {
                        certValidityPeriodCheckPresent = true;
                    } else if (MessageTag.QWAC_DOMAIN_NAME.getId().equals(xmlConstraint.getName().getKey())) {
                        domainNamePresent = true;
                    } else if (MessageTag.BBB_ACCEPT.getId().equals(xmlConstraint.getName().getKey())) {
                        bbbCheckConclusive = true;
                    }
                }

            } else if (i18nProvider.getMessage(MessageTag.QWAC_VALIDATION_PROFILE,
                    ValidationProcessUtils.getQWACValidationMessageTag(QWACProfile.TLS_BY_QWAC_2)).equals(xmlValidationQWACProcess.getTitle())) {
                assertEquals(Indication.INDETERMINATE, xmlValidationQWACProcess.getConclusion().getIndication());
                is2QWACProcessPresent = true;
            }
        }
        assertTrue(certPolicyCheckPresent);
        assertTrue(certQualConclusiveCheckPresent);
        assertFalse(wsaCheckAtIssuanceTimePresent);
        assertFalse(wsaCheckAtValidationTimePresent);
        assertFalse(certValidityPeriodCheckPresent);
        assertFalse(domainNamePresent);
        assertFalse(bbbCheckConclusive);
        assertTrue(is2QWACProcessPresent);

        checkReports(reports);
    }

    private void checkReports(CertificateReports reports) {
        assertNotNull(reports);
        assertNotNull(reports.getDiagnosticData());
        assertNotNull(reports.getDiagnosticDataJaxb());
        assertNotNull(reports.getDetailedReport());
        assertNotNull(reports.getDetailedReportJaxb());
        assertNotNull(reports.getSimpleReport());
        assertNotNull(reports.getSimpleReportJaxb());

        unmarshallXmlReports(reports);
    }

    private void unmarshallXmlReports(CertificateReports reports) {

        unmarshallDiagnosticData(reports);
        unmarshallDetailedReport(reports);
        unmarshallSimpleReport(reports);

        mapDiagnosticData(reports);
        mapDetailedReport(reports);
        mapSimpleReport(reports);

    }

    private void unmarshallDiagnosticData(CertificateReports reports) {
        try {
            String xmlDiagnosticData = reports.getXmlDiagnosticData();
            assertTrue(Utils.isStringNotBlank(xmlDiagnosticData));
            assertNotNull(DiagnosticDataFacade.newFacade().unmarshall(xmlDiagnosticData));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void mapDiagnosticData(CertificateReports reports) {
        ObjectMapper om = getObjectMapper();

        try {
            String json = om.writeValueAsString(reports.getDiagnosticDataJaxb());
            assertNotNull(json);
            XmlDiagnosticData diagnosticDataObject = om.readValue(json, XmlDiagnosticData.class);
            assertNotNull(diagnosticDataObject);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void unmarshallDetailedReport(CertificateReports reports) {
        try {
            String xmlDetailedReport = reports.getXmlDetailedReport();
            assertTrue(Utils.isStringNotBlank(xmlDetailedReport));
            assertNotNull(DetailedReportFacade.newFacade().unmarshall(xmlDetailedReport));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void mapDetailedReport(CertificateReports reports) {
        ObjectMapper om = getObjectMapper();
        try {
            String json = om.writeValueAsString(reports.getDetailedReportJaxb());
            assertNotNull(json);
            XmlDetailedReport detailedReportObject = om.readValue(json, XmlDetailedReport.class);
            assertNotNull(detailedReportObject);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void unmarshallSimpleReport(CertificateReports reports) {
        try {
            String xmlSimpleReport = reports.getXmlSimpleReport();
            assertTrue(Utils.isStringNotBlank(xmlSimpleReport));
            assertNotNull(SimpleCertificateReportFacade.newFacade().unmarshall(xmlSimpleReport));
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private void mapSimpleReport(CertificateReports reports) {
        ObjectMapper om = getObjectMapper();
        try {
            String json = om.writeValueAsString(reports.getSimpleReportJaxb());
            assertNotNull(json);
            XmlSimpleCertificateReport simpleReportObject = om.readValue(json, XmlSimpleCertificateReport.class);
            assertNotNull(simpleReportObject);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    private static ObjectMapper getObjectMapper() {
        ObjectMapper om = new ObjectMapper();
        JakartaXmlBindAnnotationIntrospector jai = new JakartaXmlBindAnnotationIntrospector(TypeFactory.defaultInstance());
        om.setAnnotationIntrospector(jai);
        om.enable(SerializationFeature.INDENT_OUTPUT);
        return om;
    }

    @Override
    protected EtsiValidationPolicy loadDefaultPolicy() throws Exception {
        return (EtsiValidationPolicy) ValidationPolicyLoader.fromValidationPolicy(QWAC_VALIDATION_POLICY_LOCATION).create();
    }

}
