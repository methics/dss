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
package eu.europa.esig.dss.lote.xml.download;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.lote.download.LoTEDownloadResult;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.validation.job.download.DownloadTask;
import eu.europa.esig.dss.xades.DSSXMLUtils;
import eu.europa.esig.dss.xml.utils.DomUtils;
import eu.europa.esig.dss.xml.utils.XMLCanonicalizer;

import java.util.Objects;

/**
 * This class is used to process the result of document download process for XML LoTE processing
 *
 */
public class XmlLoTEDownloadTask implements DownloadTask {

    /** Default digest algorithm used for document integrity identification */
    private static final DigestAlgorithm DEFAULT_DIGEST_ALGORITHM = DigestAlgorithm.SHA256;

    /** Default canonicalization method to be used on a document's digest computation */
    private static final String DEFAULT_CANONICALIZATION_METHOD = XMLCanonicalizer.DEFAULT_DSS_C14N_METHOD;

    /** The document */
    private final DSSDocument document;

    /** The URL to download the document from */
    private final String url;

    /**
     * Default constructor
     *
     * @param document {@link DSSDocument} to verify
     * @param url {@link String} to download the document from
     */
    public XmlLoTEDownloadTask(DSSDocument document, String url) {
        Objects.requireNonNull(url, "The url is null");
        this.document = document;
        this.url = url;
    }

    @Override
    public LoTEDownloadResult get() {
        try {
            assertDocumentIsValidXML(document);

            final Digest digest = DSSXMLUtils.getDigestOnCanonicalizedInputStream(document.openStream(),
                    DEFAULT_DIGEST_ALGORITHM, DEFAULT_CANONICALIZATION_METHOD);
            return new LoTEDownloadResult(document, digest);
        } catch (DSSException e) {
            throw e;
        } catch (Exception e) {
            throw new DSSException(String.format("Unable to retrieve the content for url '%s'. Reason : '%s'", url, e.getMessage()), e);
        }
    }

    private void assertDocumentIsValidXML(DSSDocument document) {
        if (document == null) {
            throw new NullPointerException(String.format("No document has been retrieved from URL '%s'!", url));
        }
        if (!DomUtils.isDOM(document)) {
            throw new DSSException(String.format("The document obtained from URL '%s' is not a valid XML!", url));
        }
    }

}
