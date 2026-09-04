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
import eu.europa.esig.dss.utils.Utils;

import java.util.Collection;
import java.util.Collections;

/**
 * Filters trusted entity services by the given URLs
 *
 */
public class ServiceByTrustedEntityServiceUrlFilter extends AbstractTrustedEntityServiceFilter {

    /** Collection of Trusted Source URLs to filter by */
    private final Collection<String> tlUrls;

    /**
     * Constructor to instantiate the filter with a single Trusted Source URL
     *
     * @param tlUrl {@link String}
     */
    public ServiceByTrustedEntityServiceUrlFilter(String tlUrl) {
        this(Collections.singleton(tlUrl));
    }

    /**
     * Constructor to instantiate the filter with a set of Trusted Source URLs
     *
     * @param tlUrls a collection of {@link String}s
     */
    public ServiceByTrustedEntityServiceUrlFilter(Collection<String> tlUrls) {
        this.tlUrls = tlUrls;
    }

    @Override
    protected boolean isAcceptable(TrustedEntityServiceWrapper service) {
        for (String url : tlUrls) {
            if (Utils.areStringsEqualIgnoreCase(url, service.getTrustedSourceList().getUrl())) {
                return true;
            }
        }
        return false;
    }

}
