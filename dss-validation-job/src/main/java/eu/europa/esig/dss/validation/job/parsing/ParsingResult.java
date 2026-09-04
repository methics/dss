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
package eu.europa.esig.dss.validation.job.parsing;

import eu.europa.esig.dss.validation.job.cache.CachedResult;

import java.util.List;

/**
 * Provides an interface to extract information about a parsing task result
 *
 */
public interface ParsingResult extends CachedResult {

    /**
     * Gets a list of error messages when occurred during the structure validation
     *
     * @return a list of {@link String} structure validation messages, empty list if the structure validation succeeded
     */
    List<String> getStructureValidationMessages();

}
