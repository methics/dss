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
package eu.europa.esig.dss.diagnostic.claim;

import eu.europa.esig.dss.diagnostic.jaxb.XmlDrivingPrivilegeCodeClaim;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDrivingPrivilegeCodesClaim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Represents an array of codes information for the corresponding driving privilege
 *
 */
public class DrivingPrivilegeCodesClaimWrapper extends ClaimWrapper {

    /**
     * Default constructor
     *
     * @param wrapped {@link eu.europa.esig.dss.diagnostic.jaxb.XmlDrivingPrivilegeCodesClaim}
     */
    public DrivingPrivilegeCodesClaimWrapper(final XmlDrivingPrivilegeCodesClaim wrapped) {
        super(wrapped);
    }

    /**
     * Constructor with a parent provided
     *
     * @param wrapped {@link XmlDrivingPrivilegeCodesClaim}
     * @param parent {@link ClaimWrapper}
     */
    public DrivingPrivilegeCodesClaimWrapper(final XmlDrivingPrivilegeCodesClaim wrapped, final ClaimWrapper parent) {
        super(wrapped, parent);
    }

    /**
     * Gets a list of codes information for the given driving privilege
     *
     * @return a lis of {@link DrivingPrivilegeCodeClaimWrapper}s
     */
    public List<DrivingPrivilegeCodeClaimWrapper> getCodes() {
        List<XmlDrivingPrivilegeCodeClaim> xmlDrivingPrivilegeCodeClaims = getWrapped().getCode();
        if (xmlDrivingPrivilegeCodeClaims != null && !xmlDrivingPrivilegeCodeClaims.isEmpty()) {
            return xmlDrivingPrivilegeCodeClaims.stream().filter(Objects::nonNull)
                    .map(x -> new DrivingPrivilegeCodeClaimWrapper(x, this)).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    @Override
    public boolean isList() {
        return true;
    }

    @Override
    public List<ClaimWrapper> getList() {
        final List<ClaimWrapper> result = new ArrayList<>(super.getList());
        List<DrivingPrivilegeCodeClaimWrapper> codes = getCodes();
        if (codes != null && !codes.isEmpty()) {
            result.addAll(codes.stream().map(c -> (ClaimWrapper) c).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public XmlDrivingPrivilegeCodesClaim getWrapped() {
        return (XmlDrivingPrivilegeCodesClaim) super.getWrapped();
    }

}
