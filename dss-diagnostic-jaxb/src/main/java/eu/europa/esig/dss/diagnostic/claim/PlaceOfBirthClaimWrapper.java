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

import eu.europa.esig.dss.diagnostic.jaxb.XmlClaim;
import eu.europa.esig.dss.diagnostic.jaxb.XmlPlaceOfBirthClaim;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an {@code eu.europa.esig.dss.diagnostic.jaxb.XmlPlaceOfBirthClaim}
 *
 */
public class PlaceOfBirthClaimWrapper extends ClaimWrapper {

    /**
     * Default constructor
     *
     * @param wrapped {@link XmlClaim}
     */
    public PlaceOfBirthClaimWrapper(final XmlClaim wrapped) {
        super(wrapped);
    }

    /**
     * Constructor with a parent claim provided
     *
     * @param wrapped {@link XmlClaim}
     * @param parent {@link ClaimWrapper}
     */
    public PlaceOfBirthClaimWrapper(final XmlClaim wrapped, final ClaimWrapper parent) {
        super(wrapped, parent);
    }

    /**
     * Gets the user's city or locality address, when present.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getCity() {
        XmlClaim wrapped = getWrapped();
        if (wrapped instanceof XmlPlaceOfBirthClaim) {
            XmlClaim city = ((XmlPlaceOfBirthClaim) wrapped).getCity();
            if (city != null) {
                return new ClaimWrapper(city, this);
            }
        }
        return null;
    }

    /**
     * Gets the user's zip code or postal code address, when present.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getRegion() {
        XmlClaim wrapped = getWrapped();
        if (wrapped instanceof XmlPlaceOfBirthClaim) {
            XmlClaim region = ((XmlPlaceOfBirthClaim) wrapped).getRegion();
            if (region != null) {
                return new ClaimWrapper(region, this);
            }
        }
        return null;
    }

    /**
     * Gets the user's country address, when present.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getCountry() {
        XmlClaim wrapped = getWrapped();
        if (wrapped instanceof XmlPlaceOfBirthClaim) {
            XmlClaim country = ((XmlPlaceOfBirthClaim) wrapped).getCountry();
            if (country != null) {
                return new ClaimWrapper(country, this);
            }
        }
        return null;
    }

    @Override
    public boolean isMap() {
        XmlClaim wrapped = getWrapped();
        if (wrapped instanceof XmlPlaceOfBirthClaim && wrapped.getText() == null) {
            return true;
        }
        return super.isMap();
    }

    @Override
    public Map<String, ClaimWrapper> getMap() {
        if (isMap()) {
            final Map<String, ClaimWrapper> result = new HashMap<>(super.getMap());
            ClaimWrapper city = getCity();
            if (city != null) {
                result.put(city.getName(), city);
            }
            ClaimWrapper region = getRegion();
            if (region != null) {
                result.put(region.getName(), region);
            }
            ClaimWrapper country = getCountry();
            if (country != null) {
                result.put(country.getName(), country);
            }
            return result;
        }
        return super.getMap();
    }

}
