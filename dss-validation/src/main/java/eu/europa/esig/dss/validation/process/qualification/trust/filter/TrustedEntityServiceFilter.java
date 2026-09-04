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
package eu.europa.esig.dss.validation.process.qualification.trust.filter;

import eu.europa.esig.dss.diagnostic.TrustedEntityServiceWrapper;

import java.util.List;

/**
 * Filters {@code TrustedEntityServiceWrapper}s by the given conditions
 *
 */
public interface TrustedEntityServiceFilter {

    /**
     * Filters a list of {@code TrustedEntityServiceWrapper}s
     *
     * @param trustedServices a list of {@link TrustedEntityServiceWrapper}s to filter
     * @return filtered list of {@link TrustedEntityServiceWrapper}s
     */
    List<TrustedEntityServiceWrapper> filter(List<TrustedEntityServiceWrapper> trustedServices);

}
