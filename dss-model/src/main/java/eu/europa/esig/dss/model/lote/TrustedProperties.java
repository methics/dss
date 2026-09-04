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
package eu.europa.esig.dss.model.lote;

import eu.europa.esig.dss.model.timedependent.TimeDependentValues;

import java.io.Serializable;
import java.util.Objects;

/**
 * Contains a list of trusted certificates and their properties
 *
 */
public class TrustedProperties implements Serializable {

    private static final long serialVersionUID = -3151960723009323199L;

    /** The LoLoTE id */
    private final LoLoTEInfo loloteInfo;

    /** The LoTE id */
    private final LoTEInfo listInfo;

    /** The trustedEntity */
    private final TrustedEntity trustedEntity;

    /** The trustedServices */
    private final TimeDependentValues<ServiceStatusAndInformationExtensions> trustedServices;

    /**
     * Constructor for extracted information from an "independent" list
     *
     * @param listInfo             {@link LoTEInfo}
     * @param trustedEntity        {@link TrustedEntity}
     * @param trustedServices      the current trust service
     */
    public TrustedProperties(LoTEInfo listInfo, TrustedEntity trustedEntity,
                             TimeDependentValues<ServiceStatusAndInformationExtensions> trustedServices) {
        this(null, listInfo, trustedEntity, trustedServices);
    }

    /**
     * Constructor for extracted information with a related List of Lists
     *
     * @param loloteInfo           {@link LoLoTEInfo}
     * @param listInfo             {@link LoTEInfo}
     * @param trustedEntity        {@link TrustedEntity}
     * @param trustedServices      the current trust service
     */
    public TrustedProperties(LoLoTEInfo loloteInfo, LoTEInfo listInfo, TrustedEntity trustedEntity,
                             TimeDependentValues<ServiceStatusAndInformationExtensions> trustedServices) {
        Objects.requireNonNull(listInfo, "tlInfo cannot be null!");
        Objects.requireNonNull(trustedEntity, "trustedEntity cannot be null!");
        Objects.requireNonNull(trustedServices, "trustedServices cannot be null!");
        this.loloteInfo = loloteInfo;
        this.listInfo = listInfo;
        this.trustedEntity = trustedEntity;
        this.trustedServices = trustedServices;
    }

    /**
     * Gets LoLoTE
     *
     * @return {@link LoLoTEInfo}
     */
    public LoLoTEInfo getLoLoTEInfo() {
        return loloteInfo;
    }

    /**
     * Gets List
     *
     * @return {@link LoTEInfo}
     */
    public LoTEInfo getLoTEInfo() {
        return listInfo;
    }

    /**
     * Gets trusted entity
     *
     * @return {@link TrustedEntity}
     */
    public TrustedEntity getTrustedEntity() {
        return trustedEntity;
    }

    /**
     * Gets trust service
     *
     * @return {@link TimeDependentValues}
     */
    public TimeDependentValues<ServiceStatusAndInformationExtensions> getTrustedServices() {
        return trustedServices;
    }

}
