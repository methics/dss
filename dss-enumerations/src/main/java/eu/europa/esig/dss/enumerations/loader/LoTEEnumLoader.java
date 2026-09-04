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
import eu.europa.esig.dss.enumerations.CertificateApprovalStatusEnum;
import eu.europa.esig.dss.enumerations.ListType;
import eu.europa.esig.dss.enumerations.LoTEServiceStatus;
import eu.europa.esig.dss.enumerations.LoTEServiceStatusEnum;
import eu.europa.esig.dss.enumerations.LoTEServiceTypeIdentifier;
import eu.europa.esig.dss.enumerations.LoTEServiceTypeIdentifierEnum;
import eu.europa.esig.dss.enumerations.LoTETypeEnum;

public class LoTEEnumLoader implements LoTELoader {

    /**
     * Default constructor
     */
    public LoTEEnumLoader() {
        // empty
    }

    @Override
    public ListType listTypeFromUri(String uri) {
        for (ListType type : LoTETypeEnum.values()) {
            if (uri.equalsIgnoreCase(type.getUri())) {
                return type;
            }
        }
        return null;
    }

    @Override
    public LoTEServiceTypeIdentifier serviceTypeIdentifierFromUri(String uri) {
        for (LoTEServiceTypeIdentifier sti : LoTEServiceTypeIdentifierEnum.values()) {
            if (uri.equalsIgnoreCase(sti.getUri())) {
                return sti;
            }
        }
        return null;
    }

    @Override
    public LoTEServiceStatus serviceStatusFromUri(String uri) {
        for (LoTEServiceStatus status : LoTEServiceStatusEnum.values()) {
            if (uri.equalsIgnoreCase(status.getUri())) {
                return status;
            }
        }
        return null;
    }

    @Override
    public CertificateApprovalStatus certificateApprovalStatusFromLabel(String label) {
        for (CertificateApprovalStatus certApprovalStatus : CertificateApprovalStatusEnum.values()) {
            if (label.equalsIgnoreCase(certApprovalStatus.getLabel())) {
                return certApprovalStatus;
            }
        }
        return null;
    }

    @Override
    public CertificateApprovalStatus certificateApprovalStatusFromDefinition(ListType listType, LoTEServiceTypeIdentifier sti, LoTEServiceStatus status) {
        for (CertificateApprovalStatus certApprovalStatus : CertificateApprovalStatusEnum.values()) {
            if (((listType == null && certApprovalStatus.getListType() == null) || (listType != null && listType.equals(certApprovalStatus.getListType())) &&
                    (sti == null && certApprovalStatus.getServiceTypeIdentifier() == null) || (sti != null && sti.equals(certApprovalStatus.getServiceTypeIdentifier())) &&
                    (status == null && certApprovalStatus.getServiceStatus() == null) || (status != null && status.equals(certApprovalStatus.getServiceStatus())))) {
                return certApprovalStatus;
            }
        }
        return CertificateApprovalStatusEnum.CERT_FOR_UNKNOWN;
    }

}
