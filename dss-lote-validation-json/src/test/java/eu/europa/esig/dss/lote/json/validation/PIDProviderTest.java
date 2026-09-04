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
package eu.europa.esig.dss.lote.json.validation;

import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.jaxb.XmlListOfTrustedEntities;
import eu.europa.esig.dss.diagnostic.jaxb.XmlTrustSourceList;
import eu.europa.esig.dss.diagnostic.jaxb.XmlTrustedEntity;
import eu.europa.esig.dss.diagnostic.jaxb.XmlTrustedEntityService;
import eu.europa.esig.dss.enumerations.CertificateQualification;
import eu.europa.esig.dss.enumerations.CertificateApprovalStatusEnum;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.lote.job.LoTEValidationJob;
import eu.europa.esig.dss.lote.source.LoTESource;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.service.http.commons.FileCacheDataLoader;
import eu.europa.esig.dss.simplecertificatereport.SimpleCertificateReport;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.MemoryDataLoader;
import eu.europa.esig.dss.spi.lote.TrustedEntitiesCertificateSource;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.validation.CommonCertificateVerifier;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.CertificateValidator;
import eu.europa.esig.dss.validation.reports.CertificateReports;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PIDProviderTest {

    private static final DSSDocument LOTE_DOC = new FileDocument(new File("src/test/resources/pid-providers.json"));

    private static final String PID_LOTE_URL = "https://dss.nowina.lu/pid-providers.json";

    private static final CertificateToken LOTE_ISSUER = DSSUtils.loadCertificate(new File("src/test/resources/pid-providers-cert.cer"));

    private static final CertificateToken CERTIFICATE = DSSUtils.loadCertificateFromBase64EncodedString(
            "MIIDBTCCAqygAwIBAgIJALyA/nuVMsUpMAoGCCqGSM49BAMEMIGCMR4wHAYDVQRhExVOVFJOTy1OT0ZPUi45OTE4MjU4MjcxLTArBgNVBAMTJGVpZGFzMnNhbmRrYXNzZSBFQUEgUHJvdmlkZXIgQ0EgdGVzdDEkMCIGA1UEChMbRElHSVRBTElTRVJJTkdTRElSRUtUT1JBVEVUMQswCQYDVQQGEwJOTzAeFw0yNTEwMTAwODE2MzZaFw0yNjEwMDgxMjQ3MzZaMGExCzAJBgNVBAYTAk5PMTIwMAYDVQQDDClEaWdpdGFsaXNlcmluZ3NkaXJla3RvcmF0ZXQgLSBCZXZpc3BvcnRlbjEeMBwGA1UEYQwVTlRSTk8tTk9GT1IuOTkxODI1ODI3MFkwEwYHKoZIzj0CAQYIKoZIzj0DAQcDQgAEggybfUUfbzJltJnTd3NjkH/OApRXdjznhsiDEShxa14362U8s1d/z8fsFMNxiO+z/ZSkHsUurkh2EiNQMcG0T6OCASkwggElMB8GA1UdIwQYMBaAFG2uFOu+dBM1aEzXwQ1nMTFpj7JfMB0GA1UdDgQWBBRTpQH9HAw203fM8Z6Nnc6Aj5RsVzAMBgNVHRMBAf8EAjAAMFwGA1UdHwRVMFMwUaBPoE2GS2h0dHBzOi8vY2EudGVzdC5laWRhczJzYW5ka2Fzc2UubmV0L3YxL2NlcnRzL2ludGVybWVkaWF0ZXMvZWFhX3Byb3ZpZGVyLmNybDBnBggrBgEFBQcBAQRbMFkwVwYIKwYBBQUHMAKGS2h0dHBzOi8vY2EudGVzdC5laWRhczJzYW5ka2Fzc2UubmV0L3YxL2NlcnRzL2ludGVybWVkaWF0ZXMvZWFhX3Byb3ZpZGVyLmNlcjAOBgNVHQ8BAf8EBAMCBaAwCgYIKoZIzj0EAwQDRwAwRAIgco5xnaAZPzLFF0aC7FbF3bEmbHsXId42CRiZIJdqyK0CIG9cjXM31kTF3kmHgN8NligxMbxEmT2HjkAYkKRKUUZR");

    @Test
    void pidProviderLoTETest() {
        CommonCertificateSource certificateSource = new CommonCertificateSource();
        certificateSource.addCertificate(LOTE_ISSUER);

        LoTEValidationJob validationJob = new LoTEValidationJob();

        LoTESource loteSource = new LoTESource();
        loteSource.setUrl(PID_LOTE_URL);
        loteSource.setCertificateSource(certificateSource);
        validationJob.setLoTESources(loteSource);

        Map<String, byte[]> tlMap = new HashMap<>();
        tlMap.put(PID_LOTE_URL, DSSUtils.toByteArray(LOTE_DOC));
        MemoryDataLoader memoryDataLoader = new MemoryDataLoader(tlMap);

        FileCacheDataLoader fileCacheDataLoader = new FileCacheDataLoader();
        fileCacheDataLoader.setDataLoader(memoryDataLoader);
        fileCacheDataLoader.setCacheExpirationTime(0);
        validationJob.setOfflineDataLoader(fileCacheDataLoader);

        TrustedEntitiesCertificateSource trustedCertificateSource = new TrustedEntitiesCertificateSource();
        validationJob.setTrustedEntitiesCertificateSource(trustedCertificateSource);

        validationJob.offlineRefresh();

        assertEquals(17, trustedCertificateSource.getCertificates().size());

        CertificateValidator certificateValidator = CertificateValidator.fromCertificate(CERTIFICATE);
        CertificateVerifier certificateVerifier = new CommonCertificateVerifier();
        certificateVerifier.setTrustedCertSources(trustedCertificateSource);
        certificateValidator.setCertificateVerifier(certificateVerifier);

        CertificateReports reports = certificateValidator.validate();
        SimpleCertificateReport simpleReport = reports.getSimpleReport();

        assertEquals(Indication.PASSED, simpleReport.getCertificateIndication(CERTIFICATE.getDSSIdAsString()));
        assertEquals(CertificateQualification.NA, simpleReport.getQualificationAtCertificateIssuance());
        assertEquals(CertificateQualification.NA, simpleReport.getQualificationAtValidationTime());
        assertEquals(1, simpleReport.getCertificateApprovalStatusAtCertificateIssuance().size());
        assertEquals(CertificateApprovalStatusEnum.PID_PROVIDER, simpleReport.getCertificateApprovalStatusAtCertificateIssuance().get(0));
        assertEquals(1, simpleReport.getCertificateApprovalStatusAtValidationTime().size());
        assertEquals(CertificateApprovalStatusEnum.PID_PROVIDER, simpleReport.getCertificateApprovalStatusAtValidationTime().get(0));

        DiagnosticData diagnosticData = reports.getDiagnosticData();
        assertNotNull(diagnosticData);

        CertificateWrapper certificate = diagnosticData.getCertificateById(CERTIFICATE.getDSSIdAsString());
        assertNotNull(certificate);

        List<XmlTrustedEntity> trustedEntities = certificate.getTrustedEntities();
        assertEquals(1, trustedEntities.size());

        XmlTrustedEntity xmlTrustedEntity = trustedEntities.get(0);
        assertEquals(1, Utils.collectionSize(xmlTrustedEntity.getTrustedEntityServices()));
        assertTrue(Utils.isCollectionNotEmpty(xmlTrustedEntity.getNames()));
        assertTrue(Utils.isCollectionNotEmpty(xmlTrustedEntity.getTradeNames()));

        XmlTrustedEntityService xmlTrustedEntityService = xmlTrustedEntity.getTrustedEntityServices().get(0);
        assertNotNull(xmlTrustedEntityService.getServiceType());
        assertNull(xmlTrustedEntityService.getStatus());
        assertNull(xmlTrustedEntityService.getStartDate());
        assertTrue(Utils.isCollectionNotEmpty(xmlTrustedEntityService.getServiceNames()));
        assertFalse(Utils.isCollectionNotEmpty(xmlTrustedEntityService.getServiceSupplyPoints()));

        List<XmlListOfTrustedEntities> lotes = diagnosticData.getListsOfTrustedEntities();
        assertEquals(1, lotes.size());

        XmlTrustSourceList pidProviderList = lotes.get(0);
        assertNotNull(pidProviderList.getUrl());
        assertNotNull(pidProviderList.getCountryCode());
        assertNotNull(pidProviderList.getSequenceNumber());
        assertNotNull(pidProviderList.getVersion());
        assertNotNull(pidProviderList.getLastLoading());
        assertNotNull(pidProviderList.getIssueDate());
        assertNotNull(pidProviderList.getNextUpdate());
        assertTrue(pidProviderList.isWellSigned());
        assertNotNull(pidProviderList.getStructuralValidation());
        assertTrue(pidProviderList.getStructuralValidation().isValid());
        assertTrue(Utils.isCollectionEmpty(pidProviderList.getStructuralValidation().getMessages()));
    }

}
