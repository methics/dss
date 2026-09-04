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
package eu.europa.esig.dss.validation.job.download;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.Digest;
import eu.europa.esig.dss.validation.job.cache.CachedResult;

import java.util.List;

/**
 * Interface providing methods to extract information about a download job
 *
 */
public interface DownloadResult extends CachedResult {

    /**
     * Gets the downloaded document
     *
     * @return {@link DSSDocument}
     */
    DSSDocument getDSSDocument();

    /**
     * Gets digest of a canonicalized document
     *
     * @return {@link Digest}
     */
    Digest getDigest();

    /**
     * Returns error messages occurred during sha2 processing, if applicable
     *
     * @return a list of {@link String}s if errors occurred during sha2 processing, empty list otherwise
     */
    List<String> getSha2ErrorMessages(); // TODO : remove from the interface

}
