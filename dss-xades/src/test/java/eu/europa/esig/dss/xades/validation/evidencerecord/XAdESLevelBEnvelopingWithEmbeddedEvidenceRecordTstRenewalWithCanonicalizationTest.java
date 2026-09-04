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
package eu.europa.esig.dss.xades.validation.evidencerecord;

import eu.europa.esig.dss.diagnostic.DiagnosticData;
import eu.europa.esig.dss.diagnostic.EvidenceRecordWrapper;
import eu.europa.esig.dss.diagnostic.TimestampWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDigestMatcher;
import eu.europa.esig.dss.enumerations.DigestMatcherType;
import eu.europa.esig.dss.enumerations.EvidenceRecordTimestampType;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.FileDocument;
import eu.europa.esig.dss.utils.Utils;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class XAdESLevelBEnvelopingWithEmbeddedEvidenceRecordTstRenewalWithCanonicalizationTest extends AbstractXAdESWithEvidenceRecordTestValidation {

    @Override
    protected DSSDocument getSignedDocument() {
        return new FileDocument("src/test/resources/validation/evidence-record/XAdES-B_ER_Renewed_can.xml");
    }

    @Override
    protected void checkSignatureLevel(DiagnosticData diagnosticData) {
        assertEquals(SignatureLevel.XAdES_BASELINE_B, diagnosticData.getSignatureFormat(diagnosticData.getFirstSignatureId()));
    }

    @Override
    protected int getNumberOfExpectedEvidenceScopes() {
        return 2;
    }

    @Override
    protected void checkEvidenceRecordTimestampedReferences(DiagnosticData diagnosticData) {
        List<EvidenceRecordWrapper> evidenceRecords = diagnosticData.getEvidenceRecords();
        assertEquals(1, evidenceRecords.size());

        EvidenceRecordWrapper evidenceRecord = evidenceRecords.get(0);

        assertEquals(2, Utils.collectionSize(evidenceRecord.getCoveredSignedData()));
        assertEquals(1, Utils.collectionSize(evidenceRecord.getCoveredSignatures()));
        assertEquals(1, Utils.collectionSize(evidenceRecord.getCoveredCertificates()));
        assertEquals(0, Utils.collectionSize(evidenceRecord.getCoveredRevocations()));
        assertEquals(0, Utils.collectionSize(evidenceRecord.getCoveredTimestamps()));
        assertEquals(0, Utils.collectionSize(evidenceRecord.getCoveredEvidenceRecords()));
    }

    @Override
    protected void checkEvidenceRecordTimestamps(DiagnosticData diagnosticData) {
        super.checkEvidenceRecordTimestamps(diagnosticData);

        List<EvidenceRecordWrapper> evidenceRecords = diagnosticData.getEvidenceRecords();
        EvidenceRecordWrapper evidenceRecord = evidenceRecords.get(0);

        List<TimestampWrapper> timestampList = evidenceRecord.getTimestampList();
        assertEquals(2, timestampList.size());

        int arcTstCounter = 0;
        int arcTstRenewalCounter = 0;
        for (TimestampWrapper timestampWrapper : timestampList) {
            assertTrue(timestampWrapper.isMessageImprintDataFound());
            assertTrue(timestampWrapper.isMessageImprintDataIntact());
            assertTrue(timestampWrapper.isSignatureIntact());
            assertTrue(timestampWrapper.isSignatureValid());
            if (EvidenceRecordTimestampType.ARCHIVE_TIMESTAMP == timestampWrapper.getEvidenceRecordTimestampType()) {
                assertEquals(1, timestampWrapper.getDigestMatchers().size());
                ++arcTstCounter;
            } else if (EvidenceRecordTimestampType.TIMESTAMP_RENEWAL_ARCHIVE_TIMESTAMP == timestampWrapper.getEvidenceRecordTimestampType()) {
                assertEquals(2, timestampWrapper.getDigestMatchers().size());
                int messageImprintDMCounter = 0;
                int arcTstDMCounter = 0;
                for (XmlDigestMatcher digestMatcher : timestampWrapper.getDigestMatchers()) {
                    assertTrue(digestMatcher.isDataFound());
                    assertTrue(digestMatcher.isDataIntact());
                    if (DigestMatcherType.MESSAGE_IMPRINT == digestMatcher.getType()) {
                        ++messageImprintDMCounter;
                    } else if (DigestMatcherType.EVIDENCE_RECORD_ARCHIVE_TIME_STAMP == digestMatcher.getType()) {
                        ++arcTstDMCounter;
                    }
                }
                assertEquals(1, messageImprintDMCounter);
                assertEquals(1, arcTstDMCounter);
                ++arcTstRenewalCounter;
            }
        }
        assertEquals(1, arcTstCounter);
        assertEquals(1, arcTstRenewalCounter);
    }

}
