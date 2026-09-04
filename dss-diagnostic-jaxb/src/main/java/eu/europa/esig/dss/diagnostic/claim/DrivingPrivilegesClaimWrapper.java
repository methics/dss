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

import eu.europa.esig.dss.diagnostic.jaxb.XmlDrivingPrivilegeClaim;
import eu.europa.esig.dss.diagnostic.jaxb.XmlDrivingPrivilegesClaim;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Provides user-friendly access to the information present within driving privileges claim
 *
 */
public class DrivingPrivilegesClaimWrapper extends ClaimWrapper {

    /**
     * Default constructor
     *
     * @param wrapped {@link XmlDrivingPrivilegesClaim}
     */
    public DrivingPrivilegesClaimWrapper(final XmlDrivingPrivilegesClaim wrapped) {
        super(wrapped);
    }

    /**
     * Constructor with a parent provided
     *
     * @param wrapped {@link XmlDrivingPrivilegesClaim}
     * @param parent {@link ClaimWrapper}
     */
    public DrivingPrivilegesClaimWrapper(final XmlDrivingPrivilegesClaim wrapped, final ClaimWrapper parent) {
        super(wrapped, parent);
    }

    /**
     * Gets a list of all driving privileges defined within the claim
     *
     * @return a list oif {@link DrivingPrivilegeClaimWrapper}s
     */
    public List<DrivingPrivilegeClaimWrapper> getDrivingPrivileges() {
        List<XmlDrivingPrivilegeClaim> xmlDrivingPrivileges = getWrapped().getDrivingPrivilege();
        if (xmlDrivingPrivileges != null && !xmlDrivingPrivileges.isEmpty()) {
            return xmlDrivingPrivileges.stream().filter(Objects::nonNull)
                    .map(x -> new DrivingPrivilegeClaimWrapper(x, this)).collect(Collectors.toList());
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
        List<DrivingPrivilegeClaimWrapper> drivingPrivileges = getDrivingPrivileges();
        if (drivingPrivileges != null && !drivingPrivileges.isEmpty()) {
            result.addAll(drivingPrivileges.stream().map(c -> (ClaimWrapper) c).collect(Collectors.toList()));
        }
        return result;
    }

    @Override
    public XmlDrivingPrivilegesClaim getWrapped() {
        return (XmlDrivingPrivilegesClaim) super.getWrapped();
    }

}
