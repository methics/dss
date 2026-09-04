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

import eu.europa.esig.dss.diagnostic.jaxb.XmlAddressClaim;
import eu.europa.esig.dss.diagnostic.jaxb.XmlClaim;

import java.util.HashMap;
import java.util.Map;

/**
 * Wraps an {@code XmlAddressClaim}
 * 
 */
public class AddressClaimWrapper extends ClaimWrapper {
    
    /**
     * Default constructor
     *
     * @param wrapped {@link XmlClaim}
     */
    public AddressClaimWrapper(final XmlAddressClaim wrapped) {
        super(wrapped);
    }

    /**
     * Constructor with a parent provided
     *
     * @param wrapped {@link XmlClaim}
     * @param parent {@link ClaimWrapper}
     */
    public AddressClaimWrapper(final XmlAddressClaim wrapped, final ClaimWrapper parent) {
        super(wrapped, parent);
    }

    /**
     * Gets the user's full postal or mailing address, formatted, when present
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getPostalAddress() {
        XmlClaim postalAddress = getWrapped().getPostalAddress();
        if (postalAddress != null) {
            return new ClaimWrapper(postalAddress, this);
        }
        return null;
    }

    /**
     * Gets the user's street address, when present.
     * The component may include a house number, street name, Post Office Box, and multi-line
     * extended street address information.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getStreetAddress() {
        XmlClaim streetAddress = getWrapped().getStreetAddress();
        if (streetAddress != null) {
            return new ClaimWrapper(streetAddress, this);
        }
        return null;
    }

    /**
     * Gets the user's city or locality address, when present.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getCity() {
        XmlClaim city = getWrapped().getCity();
        if (city != null) {
            return new ClaimWrapper(city, this);
        }
        return null;
    }

    /**
     * Gets the user's state or region address, when present.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getStateOrProvince() {
        XmlClaim streetAddress = getWrapped().getStateOrProvince();
        if (streetAddress != null) {
            return new ClaimWrapper(streetAddress, this);
        }
        return null;
    }

    /**
     * Gets the user's zip code or postal code address, when present.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getPostalCode() {
        XmlClaim streetAddress = getWrapped().getPostalCode();
        if (streetAddress != null) {
            return new ClaimWrapper(streetAddress, this);
        }
        return null;
    }

    /**
     * Gets the user's country address, when present.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getCountry() {
        XmlClaim streetAddress = getWrapped().getCountryName();
        if (streetAddress != null) {
            return new ClaimWrapper(streetAddress, this);
        }
        return null;
    }

    @Override
    public boolean isMap() {
        return true;
    }

    @Override
    public Map<String, ClaimWrapper> getMap() {
        final Map<String, ClaimWrapper> result = new HashMap<>(super.getMap());
        ClaimWrapper postalAddress = getPostalAddress();
        if (postalAddress != null) {
            result.put(postalAddress.getName(), postalAddress);
        }
        ClaimWrapper streetAddress = getStreetAddress();
        if (streetAddress != null) {
            result.put(streetAddress.getName(), streetAddress);
        }
        ClaimWrapper city = getCity();
        if (city != null) {
            result.put(city.getName(), city);
        }
        ClaimWrapper stateOrProvince = getStateOrProvince();
        if (stateOrProvince != null) {
            result.put(stateOrProvince.getName(), stateOrProvince);
        }
        ClaimWrapper postalCode = getPostalCode();
        if (postalCode != null) {
            result.put(postalCode.getName(), postalCode);
        }
        ClaimWrapper country = getCountry();
        if (country != null) {
            result.put(country.getName(), country);
        }
        return result;
    }

    @Override
    public XmlAddressClaim getWrapped() {
        return (XmlAddressClaim) super.getWrapped();
    }
    
}
