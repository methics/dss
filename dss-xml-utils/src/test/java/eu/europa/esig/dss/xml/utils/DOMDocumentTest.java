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
package eu.europa.esig.dss.xml.utils;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.MimeTypeEnum;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.Digest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DOMDocumentTest {

    private static final String XML_CONTENT = "<el id=\"signedData\">Text</el>";

    @Test
    void test() {
        DOMDocument doc = new DOMDocument(DomUtils.buildDOM(XML_CONTENT));
        assertNotNull(doc);
        assertNotNull(doc.getNode());
        assertEquals(MimeTypeEnum.XML, doc.getMimeType());
        assertNull(doc.getName());
        assertNotNull(doc.getBytes());
        assertNotNull(doc.getDigestValue(DigestAlgorithm.SHA256));

        doc = new DOMDocument(DomUtils.buildDOM(XML_CONTENT), "doc.xml");
        assertNotNull(doc);
        assertNotNull(doc.getNode());
        assertEquals(MimeTypeEnum.XML, doc.getMimeType());
        assertEquals("doc.xml", doc.getName());
        assertNotNull(doc.getBytes());
        assertNotNull(doc.getDigestValue(DigestAlgorithm.SHA256));
    }

    @Test
    void testSetter() {
        DOMDocument doc = new DOMDocument(DomUtils.buildDOM(XML_CONTENT));
        doc.setName("doc.xml");
        doc.setMimeType(MimeTypeEnum.TEXT);
        assertEquals(MimeTypeEnum.TEXT, doc.getMimeType());
        assertEquals("doc.xml", doc.getName());
        assertNotNull(doc.getDigestValue(DigestAlgorithm.SHA256));
    }

    @Test
    void persistenceTest() {
        final Set<DSSDocument> hashSet = new HashSet<>();

        DSSDocument document = getPersistenceTestDocument();
        hashSet.add(document);
        assertTrue(hashSet.contains(document));

        Digest digest = document.getDigest(DigestAlgorithm.SHA256);
        assertNotNull(digest);

        assertTrue(hashSet.contains(document));
        assertTrue(hashSet.contains(getPersistenceTestDocument()));

        for (DSSDocument altDocument : getPersistenceTestAlternativeDocuments()) {
            assertFalse(hashSet.contains(altDocument));
        }
    }

    private DSSDocument getPersistenceTestDocument() {
        return new DOMDocument(DomUtils.buildDOM(XML_CONTENT), "xmlDoc");
    }

    private List<DSSDocument> getPersistenceTestAlternativeDocuments() {
        return Arrays.asList(
                new DOMDocument(DomUtils.buildDOM(XML_CONTENT)),
                new DOMDocument(DomUtils.buildDOM(XML_CONTENT), "wrong name"),
                new DOMDocument(DomUtils.buildDOM("<el id=\"signedData\">Alt Text</el>"))
        );
    }

}
