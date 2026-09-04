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

/**
 * This class is used to load TS 119 602 LoTE related properties
 *
 */
public interface LoTELoader {

    /**
     * Gets a {@code ListType} from the given URI
     *
     * @param uri {@link String}
     * @return {@link ListType}
     */
    ListType listTypeFromUri(String uri);

    /**
     * Gets a {@code LoTEServiceTypeIdentifier} from the given URI
     *
     * @param uri {@link String}
     * @return {@link LoTEServiceTypeIdentifier}
     */
    LoTEServiceTypeIdentifier serviceTypeIdentifierFromUri(String uri);

    /**
     * Gets a {@code LoTEServiceStatus} from the given URI
     *
     * @param uri {@link String}
     * @return {@link LoTEServiceStatus}
     */
    LoTEServiceStatus serviceStatusFromUri(String uri);

    /**
     * Gets a {@code CertificateApprovalStatus} from the given label String
     *
     * @param label {@link String}
     * @return {@link CertificateApprovalStatus}
     */
    CertificateApprovalStatus certificateApprovalStatusFromLabel(String label);

    /**
     * Gets a {@code CertificateApprovalStatus} from the given label String
     *
     * @param listType {@link ListType}
     * @param sti {@link LoTEServiceTypeIdentifier}
     * @param status {@link LoTEServiceStatus}
     * @return {@link CertificateApprovalStatus}
     */
    CertificateApprovalStatus certificateApprovalStatusFromDefinition(ListType listType, LoTEServiceTypeIdentifier sti, LoTEServiceStatus status);

}
