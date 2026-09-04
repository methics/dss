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
package eu.europa.esig.dss.enumerations;

public enum LoTEServiceStatusEnum implements LoTEServiceStatus {

    PUB_EAA_PROVIDER_NOTIFIED("http://uri.etsi.org/19602/PubEAAProvidersList/SvcStatus/notified", "Notified Pub-EAA provider service"),

    PUB_EAA_PROVIDER_WITHDRAWN("http://uri.etsi.org/19602/PubEAAProvidersList/SvcStatus/withdrawn", "Withdrawn Pub-EAA provider service");

    /** Service Status URI */
    private final String statusUri;

    /** User-friendly label */
    private final String label;

    LoTEServiceStatusEnum(final String statusUri, final String label) {
        this.statusUri = statusUri;
        this.label = label;
    }

    @Override
    public String getUri() {
        return statusUri;
    }

    @Override
    public String getLabel() {
        return label;
    }

}
