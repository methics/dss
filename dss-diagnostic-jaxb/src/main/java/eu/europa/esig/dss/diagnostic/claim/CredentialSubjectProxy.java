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

import eu.europa.esig.dss.diagnostic.jaxb.XmlCredentialSubjectClaim;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides a NPE safe initialization and returns always the first credential sibject value, when applicable
 * 
 */
public class CredentialSubjectProxy {
    
    /** Wrapped list of credential subjects */
    private final List<XmlCredentialSubjectClaim> xmlCredentialSubjectList;

    /**
     * Default constructor
     * 
     * @param xmlCredentialSubjectList a list of {@link XmlCredentialSubjectClaim}s
     */
    public CredentialSubjectProxy(final List<XmlCredentialSubjectClaim> xmlCredentialSubjectList) {
        this.xmlCredentialSubjectList = xmlCredentialSubjectList;
    }

    /**
     * Gets a list of credential subjects
     * 
     * @return a list of {@link CredentialSubjectClaimWrapper}s
     */
    public List<CredentialSubjectClaimWrapper> getCredentialSubjects() {
        if (xmlCredentialSubjectList == null || xmlCredentialSubjectList.isEmpty()) {
            return Collections.emptyList();
        }
        return xmlCredentialSubjectList.stream().map(CredentialSubjectClaimWrapper::new).collect(Collectors.toList());
    }

    /**
     * Gets the first credential subject, when defined. Returns null otherwise
     * 
     * @return {@link CredentialSubjectClaimWrapper}
     */
    public CredentialSubjectClaimWrapper getFirstCredentialSubject() {
        if (xmlCredentialSubjectList == null || xmlCredentialSubjectList.isEmpty()) {
            return null;
        }
        return new CredentialSubjectClaimWrapper(xmlCredentialSubjectList.get(0));
    }
    
    /**
     * Gets 's full name when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getFullName() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getFullName();
        }
        return null;
    }

    /**
     * Gets 's first name when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getGivenName() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getGivenName();
        }
        return null;
    }

    /**
     * Gets 's last or family name when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getFamilyName() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getFamilyName();
        }
        return null;
    }

    /**
     * Gets 's middle name when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getMiddleName() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getMiddleName();
        }
        return null;
    }

    /**
     * Gets 's alternative name when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getNickname() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getNickname();
        }
        return null;
    }

    /**
     * Gets 's preferred or short name when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getShortName() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getShortName();
        }
        return null;
    }

    /**
     * Gets 's profile URL when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getProfileUrl() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getProfileUrl();
        }
        return null;
    }

    /**
     * Gets 's picture URL when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getPictureUrl() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getPictureUrl();
        }
        return null;
    }

    /**
     * Gets 's website when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getWebsiteUrl() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getWebsiteUrl();
        }
        return null;
    }

    /**
     * Gets 's email when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getEmail() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getEmail();
        }
        return null;
    }

    /**
     * Gets whether the 's website has been verified if defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getEmailVerified() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getEmailVerified();
        }
        return null;
    }

    /**
     * Gets 's gender when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getGender() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getGender();
        }
        return null;
    }

    /**
     * Gets 's birthdate when defined within the first Credential Subject claim
     *
     * @return {@link BirthdateClaimWrapper}
     */
    public BirthdateClaimWrapper getBirthdate() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getBirthdate();
        }
        return null;
    }

    /**
     * Gets 's timezone when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getTimezone() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getTimezone();
        }
        return null;
    }

    /**
     * Gets 's locale when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getLocale() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getLocale();
        }
        return null;
    }

    /**
     * Gets 's full address, when defined within the first Credential Subject claim
     *
     * @return {@link AddressClaimWrapper}
     */
    public AddressClaimWrapper getAddress() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getAddress();
        }
        return null;
    }

    /**
     * Gets 's city address when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getAddressCity() {
        AddressClaimWrapper Address = getAddress();
        if (Address != null) {
            return Address.getCity();
        }
        return null;
    }

    /**
     * Gets 's state or region address when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getAddressStateOrProvince() {
        AddressClaimWrapper Address = getAddress();
        if (Address != null) {
            return Address.getStateOrProvince();
        }
        return null;
    }

    /**
     * Gets 's postal code address when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getAddressPostalCode() {
        AddressClaimWrapper Address = getAddress();
        if (Address != null) {
            return Address.getPostalCode();
        }
        return null;
    }

    /**
     * Gets 's country address when defined within the first Credential Subject claim.
     * NOTE: The returned value is usually represented by 2-letter ISO country code.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getAddressCountry() {
        AddressClaimWrapper Address = getAddress();
        if (Address != null) {
            return Address.getCountry();
        }
        return null;
    }

    /**
     * Gets 's street address when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getStreetAddress() {
        AddressClaimWrapper Address = getAddress();
        if (Address != null) {
            return Address.getStreetAddress();
        }
        return null;
    }

    /**
     * Gets 's phone number when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getPhoneNumber() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getPhoneNumber();
        }
        return null;
    }

    /**
     * Gets whether the 's phone number has been verified if defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getPhoneNumberVerified() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getPhoneNumberVerified();
        }
        return null;
    }

    /**
     * Gets 's place of birth when defined within the first Credential Subject claim
     *
     * @return {@link PlaceOfBirthClaimWrapper}
     */
    public PlaceOfBirthClaimWrapper getPlaceOfBirth() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getPlaceOfBirth();
        }
        return null;
    }

    /**
     * Gets 's nationalities list when defined within the first Credential Subject claim.
     * NOTE: The values are usually represented by 3-letter nationality codes.
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getNationalities() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getNationalities();
        }
        return null;
    }

    /**
     * Gets 's last or family name at birth when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getBirthFamilyName() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getBirthFamilyName();
        }
        return null;
    }

    /**
     * Gets 's first name at birth when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getBirthGivenName() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getBirthGivenName();
        }
        return null;
    }

    /**
     * Gets 's middle name at birth when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getBirthMiddleName() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getBirthMiddleName();
        }
        return null;
    }

    /**
     * Gets 's preferred salutation when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getSalutation() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getSalutation();
        }
        return null;
    }

    /**
     * Gets 's title when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getTitle() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getTitle();
        }
        return null;
    }

    /**
     * Gets 's mobile phone number when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getMobilePhoneNumber() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getMobilePhoneNumber();
        }
        return null;
    }

    /**
     * Gets 's scenic name or pseudonym, they are known as, when defined within the first Credential Subject claim
     *
     * @return {@link ClaimWrapper}
     */
    public ClaimWrapper getPseudonym() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getPseudonym();
        }
        return null;
    }

    /**
     * Gets a list of claims incorporated within the Credential Subject or provided as disclosures,
     * which are not (yet) directly supported by the implementation.
     *
     * @return a lust of {@link ClaimWrapper}s
     */
    public List<ClaimWrapper> getOtherClaims() {
        CredentialSubjectClaimWrapper credentialSubject = getFirstCredentialSubject();
        if (credentialSubject != null) {
            return credentialSubject.getOtherClaims();
        }
        return Collections.emptyList();
    }
    
}
