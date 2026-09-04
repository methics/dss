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
package eu.europa.esig.dss.model.policy.crypto;

import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class CryptographicSuiteMetadataTest {

    @Test
    void test() {
        CryptographicSuiteMetadata metadata = new CryptographicSuiteMetadata();

        String policyName = "TestPolicy";
        String policyOID = "1.2.3.4";
        String policyURI = "http://example.com/policy";
        String publisherName = "Test Publisher";
        String publisherAddress = "123 Test Street";
        String publisherURI = "http://example.com/publisher";
        Date policyIssueDate = new Date(1000L);
        Date nextUpdate = new Date(2000L);
        String usage = "Test Usage";
        String version = "1.0";
        String lang = "en";
        String id = "ID-123";

        metadata.setPolicyName(policyName);
        metadata.setPolicyOID(policyOID);
        metadata.setPolicyURI(policyURI);
        metadata.setPublisherName(publisherName);
        metadata.setPublisherAddress(publisherAddress);
        metadata.setPublisherURI(publisherURI);
        metadata.setPolicyIssueDate(policyIssueDate);
        metadata.setNextUpdate(nextUpdate);
        metadata.setUsage(usage);
        metadata.setVersion(version);
        metadata.setLang(lang);
        metadata.setId(id);

        assertEquals(policyName, metadata.getPolicyName());
        assertEquals(policyOID, metadata.getPolicyOID());
        assertEquals(policyURI, metadata.getPolicyURI());
        assertEquals(publisherName, metadata.getPublisherName());
        assertEquals(publisherAddress, metadata.getPublisherAddress());
        assertEquals(publisherURI, metadata.getPublisherURI());
        assertEquals(policyIssueDate, metadata.getPolicyIssueDate());
        assertEquals(nextUpdate, metadata.getNextUpdate());
        assertEquals(usage, metadata.getUsage());
        assertEquals(version, metadata.getVersion());
        assertEquals(lang, metadata.getLang());
        assertEquals(id, metadata.getId());
    }

    @Test
    void testNull() {
        CryptographicSuiteMetadata metadata = new CryptographicSuiteMetadata();

        assertNull(metadata.getPolicyName());
        assertNull(metadata.getPolicyOID());
        assertNull(metadata.getPolicyURI());
        assertNull(metadata.getPublisherName());
        assertNull(metadata.getPublisherAddress());
        assertNull(metadata.getPublisherURI());
        assertNull(metadata.getPolicyIssueDate());
        assertNull(metadata.getNextUpdate());
        assertNull(metadata.getUsage());
        assertNull(metadata.getVersion());
        assertNull(metadata.getLang());
        assertNull(metadata.getId());
    }

}
