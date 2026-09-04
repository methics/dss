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
package eu.europa.esig.dss.model.job;

import eu.europa.esig.dss.model.identifier.Identifier;
import eu.europa.esig.dss.model.identifier.IdentifierBasedObject;

/**
 * Contains a validation result for a document.
 *
 */
public interface DocumentInfo extends IdentifierBasedObject {

    /**
     * Returns Download Cache Info
     *
     * @return {@link DownloadInfoRecord}
     */
    DownloadInfoRecord getDownloadCacheInfo();

    /**
     * Returns Parsing Cache Info
     *
     * @return {@link ParsingInfoRecord}
     */
    ParsingInfoRecord getParsingCacheInfo();

    /**
     * Returns Validation Cache Info
     *
     * @return {@link ValidationInfoRecord}
     */
    ValidationInfoRecord getValidationCacheInfo();

    /**
     * Returns a URL that was used to download the remote file
     *
     * @return {@link String} url
     */
    String getUrl();

    /**
     * Returns the {@code DocumentInfo} referencing the current Trusted List
     *
     * @return {@link DocumentInfo}
     */
    DocumentInfo getParent();

    /**
     * Returns the TL id
     *
     * @return {@link String} id
     */
    Identifier getDSSId();

    /**
     * Returns the String representation of the identifier
     *
     * @return {@link String}
     */
    String getDSSIdAsString();

}
