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
import eu.europa.esig.dss.detailedreport.jaxb.XmlBasicBuildingBlocks;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraint;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSAV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlStatus;
import eu.europa.esig.dss.diagnostic.DiagnosticDataFacade;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.policy.EtsiValidationPolicy;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.simplereport.SimpleReport;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.executor.signature.DefaultSignatureProcessExecutor;
import eu.europa.esig.dss.validation.reports.Reports;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SigningTimeInCertRangeExecutorTest extends AbstractProcessExecutorTest {

    @Test
    void validTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(new File("src/test/resources/diag-data/valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        EtsiValidationPolicy validationPolicy = loadDefaultPolicy();
        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);
        validationPolicy.getSignatureConstraints().getSignedAttributes().setSigningTimeInCertRange(constraint);

        DefaultSignatureProcessExecutor executor = new DefaultSignatureProcessExecutor();
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(validationPolicy);
        executor.setCurrentTime(diagnosticData.getValidationDate());

        Reports reports = executor.execute();

        SimpleReport simpleReport = reports.getSimpleReport();
        assertEquals(Indication.TOTAL_PASSED, simpleReport.getIndication(simpleReport.getFirstSignatureId()));
        assertTrue(Utils.isCollectionEmpty(simpleReport.getAdESValidationErrors(simpleReport.getFirstSignatureId())));

        DetailedReport detailedReport = reports.getDetailedReport();
        XmlBasicBuildingBlocks sigBBB = detailedReport.getBasicBuildingBlockById(detailedReport.getFirstSignatureId());
        assertNotNull(sigBBB);
        assertEquals(Indication.PASSED, sigBBB.getConclusion().getIndication());

        XmlSAV xmlSAV = sigBBB.getSAV();
        assertNotNull(xmlSAV);
        assertEquals(Indication.PASSED, xmlSAV.getConclusion().getIndication());

        boolean signingTimeCheckPresent = false;
        boolean signingTimeInValRangeCheckPresent = false;
        for (XmlConstraint xmlConstraint : xmlSAV.getConstraint()) {
            if (MessageTag.BBB_SAV_ISQPSTP.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                signingTimeCheckPresent = true;
            } else if (MessageTag.BBB_SAV_ISQPSTWSCVR.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                signingTimeInValRangeCheckPresent = true;
            }
        }
        assertTrue(signingTimeCheckPresent);
        assertTrue(signingTimeInValRangeCheckPresent);

        validateBestSigningTimes(reports);
        checkReports(reports);
    }

    @Test
    void expiredTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(new File("src/test/resources/diag-data/valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        XmlSignature xmlSignature = diagnosticData.getSignatures().get(0);
        xmlSignature.setClaimedSigningTime(new Date());

        EtsiValidationPolicy validationPolicy = loadDefaultPolicy();
        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);
        validationPolicy.getSignatureConstraints().getSignedAttributes().setSigningTimeInCertRange(constraint);

        DefaultSignatureProcessExecutor executor = new DefaultSignatureProcessExecutor();
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(validationPolicy);
        executor.setCurrentTime(diagnosticData.getValidationDate());

        Reports reports = executor.execute();

        SimpleReport simpleReport = reports.getSimpleReport();
        assertEquals(Indication.INDETERMINATE, simpleReport.getIndication(simpleReport.getFirstSignatureId()));
        assertEquals(SubIndication.SIG_CONSTRAINTS_FAILURE, simpleReport.getSubIndication(simpleReport.getFirstSignatureId()));
        assertFalse(checkMessageValuePresence(simpleReport.getAdESValidationErrors(simpleReport.getFirstSignatureId()),
                i18nProvider.getMessage(MessageTag.BBB_SAV_ISQPSTP_ANS)));
        assertTrue(checkMessageValuePresence(simpleReport.getAdESValidationErrors(simpleReport.getFirstSignatureId()),
                i18nProvider.getMessage(MessageTag.BBB_SAV_ISQPSTWSCVR_ANS)));

        DetailedReport detailedReport = reports.getDetailedReport();
        XmlBasicBuildingBlocks sigBBB = detailedReport.getBasicBuildingBlockById(detailedReport.getFirstSignatureId());
        assertNotNull(sigBBB);
        assertEquals(Indication.INDETERMINATE, sigBBB.getConclusion().getIndication());
        assertEquals(SubIndication.SIG_CONSTRAINTS_FAILURE, sigBBB.getConclusion().getSubIndication());

        XmlSAV xmlSAV = sigBBB.getSAV();
        assertNotNull(xmlSAV);
        assertEquals(Indication.INDETERMINATE, xmlSAV.getConclusion().getIndication());
        assertEquals(SubIndication.SIG_CONSTRAINTS_FAILURE, xmlSAV.getConclusion().getSubIndication());

        boolean signingTimeCheckPresent = false;
        boolean signingTimeInValRangeCheckPresent = false;
        for (XmlConstraint xmlConstraint : xmlSAV.getConstraint()) {
            if (MessageTag.BBB_SAV_ISQPSTP.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.OK, xmlConstraint.getStatus());
                signingTimeCheckPresent = true;
            } else if (MessageTag.BBB_SAV_ISQPSTWSCVR.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                assertEquals(MessageTag.BBB_SAV_ISQPSTWSCVR_ANS.getId(), xmlConstraint.getError().getKey());
                signingTimeInValRangeCheckPresent = true;
            }
        }
        assertTrue(signingTimeCheckPresent);
        assertTrue(signingTimeInValRangeCheckPresent);

        validateBestSigningTimes(reports);
        checkReports(reports);
    }

    @Test
    void noSigningTimeTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(new File("src/test/resources/diag-data/valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        XmlSignature xmlSignature = diagnosticData.getSignatures().get(0);
        xmlSignature.setClaimedSigningTime(null);

        EtsiValidationPolicy validationPolicy = loadDefaultPolicy();
        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);
        validationPolicy.getSignatureConstraints().getSignedAttributes().setSigningTime(constraint);
        validationPolicy.getSignatureConstraints().getSignedAttributes().setSigningTimeInCertRange(constraint);

        DefaultSignatureProcessExecutor executor = new DefaultSignatureProcessExecutor();
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(validationPolicy);
        executor.setCurrentTime(diagnosticData.getValidationDate());

        Reports reports = executor.execute();

        SimpleReport simpleReport = reports.getSimpleReport();
        assertEquals(Indication.INDETERMINATE, simpleReport.getIndication(simpleReport.getFirstSignatureId()));
        assertEquals(SubIndication.SIG_CONSTRAINTS_FAILURE, simpleReport.getSubIndication(simpleReport.getFirstSignatureId()));
        assertTrue(checkMessageValuePresence(simpleReport.getAdESValidationErrors(simpleReport.getFirstSignatureId()),
                i18nProvider.getMessage(MessageTag.BBB_SAV_ISQPSTP_ANS)));
        assertFalse(checkMessageValuePresence(simpleReport.getAdESValidationErrors(simpleReport.getFirstSignatureId()),
                i18nProvider.getMessage(MessageTag.BBB_SAV_ISQPSTWSCVR_ANS)));

        DetailedReport detailedReport = reports.getDetailedReport();
        XmlBasicBuildingBlocks sigBBB = detailedReport.getBasicBuildingBlockById(detailedReport.getFirstSignatureId());
        assertNotNull(sigBBB);
        assertEquals(Indication.INDETERMINATE, sigBBB.getConclusion().getIndication());
        assertEquals(SubIndication.SIG_CONSTRAINTS_FAILURE, sigBBB.getConclusion().getSubIndication());

        XmlSAV xmlSAV = sigBBB.getSAV();
        assertNotNull(xmlSAV);
        assertEquals(Indication.INDETERMINATE, xmlSAV.getConclusion().getIndication());
        assertEquals(SubIndication.SIG_CONSTRAINTS_FAILURE, xmlSAV.getConclusion().getSubIndication());

        boolean signingTimeCheckPresent = false;
        boolean signingTimeInValRangeCheckPresent = false;
        for (XmlConstraint xmlConstraint : xmlSAV.getConstraint()) {
            if (MessageTag.BBB_SAV_ISQPSTP.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.NOT_OK, xmlConstraint.getStatus());
                assertEquals(MessageTag.BBB_SAV_ISQPSTP_ANS.getId(), xmlConstraint.getError().getKey());
                signingTimeCheckPresent = true;
            } else if (MessageTag.BBB_SAV_ISQPSTWSCVR.getId().equals(xmlConstraint.getName().getKey())) {
                signingTimeInValRangeCheckPresent = true;
            }
        }
        assertTrue(signingTimeCheckPresent);
        assertFalse(signingTimeInValRangeCheckPresent);

        validateBestSigningTimes(reports);
        checkReports(reports);
    }

    @Test
    void noSigningTimeWarnTest() throws Exception {
        XmlDiagnosticData diagnosticData = DiagnosticDataFacade.newFacade().unmarshall(new File("src/test/resources/diag-data/valid-diag-data.xml"));
        assertNotNull(diagnosticData);

        XmlSignature xmlSignature = diagnosticData.getSignatures().get(0);
        xmlSignature.setClaimedSigningTime(null);

        EtsiValidationPolicy validationPolicy = loadDefaultPolicy();
        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.WARN);
        validationPolicy.getSignatureConstraints().getSignedAttributes().setSigningTime(constraint);
        validationPolicy.getSignatureConstraints().getSignedAttributes().setSigningTimeInCertRange(constraint);

        DefaultSignatureProcessExecutor executor = new DefaultSignatureProcessExecutor();
        executor.setDiagnosticData(diagnosticData);
        executor.setValidationPolicy(validationPolicy);
        executor.setCurrentTime(diagnosticData.getValidationDate());

        Reports reports = executor.execute();

        SimpleReport simpleReport = reports.getSimpleReport();
        assertEquals(Indication.TOTAL_PASSED, simpleReport.getIndication(simpleReport.getFirstSignatureId()));
        assertTrue(Utils.isCollectionEmpty(simpleReport.getAdESValidationErrors(simpleReport.getFirstSignatureId())));
        assertTrue(checkMessageValuePresence(simpleReport.getAdESValidationWarnings(simpleReport.getFirstSignatureId()),
                i18nProvider.getMessage(MessageTag.BBB_SAV_ISQPSTP_ANS)));
        assertFalse(checkMessageValuePresence(simpleReport.getAdESValidationWarnings(simpleReport.getFirstSignatureId()),
                i18nProvider.getMessage(MessageTag.BBB_SAV_ISQPSTWSCVR_ANS)));

        DetailedReport detailedReport = reports.getDetailedReport();
        XmlBasicBuildingBlocks sigBBB = detailedReport.getBasicBuildingBlockById(detailedReport.getFirstSignatureId());
        assertNotNull(sigBBB);
        assertEquals(Indication.PASSED, sigBBB.getConclusion().getIndication());

        XmlSAV xmlSAV = sigBBB.getSAV();
        assertNotNull(xmlSAV);
        assertEquals(Indication.PASSED, xmlSAV.getConclusion().getIndication());

        boolean signingTimeCheckPresent = false;
        boolean signingTimeInValRangeCheckPresent = false;
        for (XmlConstraint xmlConstraint : xmlSAV.getConstraint()) {
            if (MessageTag.BBB_SAV_ISQPSTP.getId().equals(xmlConstraint.getName().getKey())) {
                assertEquals(XmlStatus.WARNING, xmlConstraint.getStatus());
                assertEquals(MessageTag.BBB_SAV_ISQPSTP_ANS.getId(), xmlConstraint.getWarning().getKey());
                signingTimeCheckPresent = true;
            } else if (MessageTag.BBB_SAV_ISQPSTWSCVR.getId().equals(xmlConstraint.getName().getKey())) {
                signingTimeInValRangeCheckPresent = true;
            }
        }
        assertTrue(signingTimeCheckPresent);
        assertFalse(signingTimeInValRangeCheckPresent);

        validateBestSigningTimes(reports);
        checkReports(reports);
    }

}
