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
package eu.europa.esig.dss.enumerations.loader;

import eu.europa.esig.dss.enumerations.CertificateApprovalStatus;
import eu.europa.esig.dss.enumerations.ListType;
import eu.europa.esig.dss.enumerations.LoTEServiceStatus;
import eu.europa.esig.dss.enumerations.LoTEServiceTypeIdentifier;

public class LoTEEmptyLoader implements LoTELoader {

    /**
     * Default constructor
     */
    public LoTEEmptyLoader() {
        // empty
    }

    @Override
    public ListType listTypeFromUri(String uri) {
        return new ListType() {

            private static final long serialVersionUID = -2473908260368214513L;

            @Override
            public String getLabel() {
                return null;
            }

            @Override
            public String getUri() {
                return uri;
            }

        };
    }

    @Override
    public LoTEServiceTypeIdentifier serviceTypeIdentifierFromUri(String uri) {
        return new LoTEServiceTypeIdentifier() {

            private static final long serialVersionUID = 6585326102373562248L;

            @Override
            public String getLabel() {
                return null;
            }

            @Override
            public String getUri() {
                return uri;
            }

        };
    }

    @Override
    public LoTEServiceStatus serviceStatusFromUri(String uri) {
        return new LoTEServiceStatus() {

            private static final long serialVersionUID = -2749490865159156345L;

            @Override
            public String getLabel() {
                return null;
            }

            @Override
            public String getUri() {
                return uri;
            }

        };
    }

    @Override
    public CertificateApprovalStatus certificateApprovalStatusFromLabel(String label) {
        return new CertificateApprovalStatus() {

            @Override
            public ListType getListType() {
                return null;
            }

            @Override
            public LoTEServiceTypeIdentifier getServiceTypeIdentifier() {
                return null;
            }

            @Override
            public LoTEServiceStatus getServiceStatus() {
                return null;
            }

            @Override
            public String getLabel() {
                return label;
            }

        };
    }

    @Override
    public CertificateApprovalStatus certificateApprovalStatusFromDefinition(ListType listType, LoTEServiceTypeIdentifier sti, LoTEServiceStatus status) {
        // not supported
        return null;
    }

}
