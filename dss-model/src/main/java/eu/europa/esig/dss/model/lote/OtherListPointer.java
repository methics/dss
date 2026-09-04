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

import eu.europa.esig.dss.model.tsl.MRA;
import eu.europa.esig.dss.model.x509.CertificateToken;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * Contains information about a reference to another List, including URL and signing certificates
 *
 */
public class OtherListPointer implements Serializable {

    private static final long serialVersionUID = 7234557623875830863L;

    /** List of ServiceDigitalIdentity X509 certificates */
    private List<CertificateToken> sdiCertificates;

    /** URL location */
    private String locationUrl;

    /** An ISO code of the country or an alliance */
    private String schemeTerritory;

    /** Type of the Trusted List */
    private String type;

    /** MimeType of the Trusted List document */
    private String mimeType;

    /** A map of defined scheme operator names between the used languages */
    private Map<String, List<String>> schemeOperatorNames;

    /** A map of defined type community rules between the used languages */
    private Map<String, List<String>> schemeTypeCommunityRules;

    /**
     * Default constructor
     */
    public OtherListPointer() {
        // empty
    }

    /**
     * Default constructor to instantiate object from {@code OtherListPointerBuilder}
     *
     * @param builder {@link OtherListPointerBuilder}
     */
    public OtherListPointer(OtherListPointerBuilder builder) {
        this.sdiCertificates = builder.getSdiCertificates();
        this.locationUrl = builder.getLocationUrl();
        this.schemeTerritory = builder.getSchemeTerritory();
        this.type = builder.getType();
        this.mimeType = builder.getMimeType();
        this.schemeOperatorNames = builder.getSchemeOperatorNames();
        this.schemeTypeCommunityRules = builder.getSchemeTypeCommunityRules();
    }

    /**
     * Gets a list of ServiceDigitalIdentity X509 certificates
     *
     * @return a list of {@link CertificateToken}s
     */
    public List<CertificateToken> getSdiCertificates() {
        return sdiCertificates;
    }

    /**
     * Gets List location url
     *
     * @return {@link String}
     */
    public String getLocationUrl() {
        return locationUrl;
    }

    /**
     * Gets the scheme territory ISO country code
     *
     * @return {@link String}
     */
    public String getSchemeTerritory() {
        return schemeTerritory;
    }

    /**
     * Gets the List Type
     *
     * @return {@link String}
     */
    public String getType() {
        return type;
    }

    /**
     * Gets the MimeType of the referenced document
     *
     * @return {@link String}
     */
    public String getMimeType() {
        return mimeType;
    }

    /**
     * Gets a map of scheme operator names
     *
     * @return a map of {@link String} language code and a list of corresponding {@link String} names
     */
    public Map<String, List<String>> getSchemeOperatorNames() {
        return schemeOperatorNames;
    }

    /**
     * Gets a map of scheme type community rules
     *
     * @return a map of {@link String} language code and a list of corresponding {@link String} names
     */
    public Map<String, List<String>> getSchemeTypeCommunityRules() {
        return schemeTypeCommunityRules;
    }

    /**
     * Builds {@code OtherListPointer}
     */
    public static final class OtherListPointerBuilder {

        /** List of ServiceDigitalIdentity X509 certificates */
        private List<CertificateToken> sdiCertificates;

        /** URL location */
        private String locationUrl;

        /** An ISO code of the country or an alliance */
        private String schemeTerritory;

        /** Type of the Trusted List */
        private String tslType;

        /** MimeType of the Trusted List document */
        private String mimeType;

        /** A map of defined scheme operator names between the used languages */
        private Map<String, List<String>> schemeOperatorNames;

        /** A map of defined type community rules between the used languages */
        private Map<String, List<String>> schemeTypeCommunityRules;

        /** Mutual Recognition Agreement block */
        private MRA mra;

        /**
         * Default constructor
         */
        public OtherListPointerBuilder() {
            // empty
        }

        /**
         * Gets the ServiceDigitalIdentity X509 certificates
         *
         * @return a list of {@link CertificateToken}s
         */
        public List<CertificateToken> getSdiCertificates() {
            return sdiCertificates;
        }

        /**
         * Sets the ServiceDigitalIdentity X509 certificates
         *
         * @param sdiCertificates a list of {@link CertificateToken}s
         * @return {@link OtherListPointerBuilder}
         */
        public OtherListPointerBuilder setSdiCertificates(List<CertificateToken> sdiCertificates) {
            this.sdiCertificates = sdiCertificates;
            return this;
        }

        /**
         * Gets the List location URL
         *
         * @return locationUrl {@link String}
         */
        public String getLocationUrl() {
            return locationUrl;
        }

        /**
         * Sets the List location URL
         *
         * @param locationUrl {@link String}
         * @return {@link OtherListPointerBuilder}
         */
        public OtherListPointerBuilder setLocationUrl(String locationUrl) {
            this.locationUrl = locationUrl;
            return this;
        }

        /**
         * Gets the scheme territory code
         *
         * @return {@link String}
         */
        public String getSchemeTerritory() {
            return schemeTerritory;
        }

        /**
         * Sets the scheme territory code
         *
         * @param schemeTerritory {@link String}
         * @return {@link OtherListPointerBuilder}
         */
        public OtherListPointerBuilder setSchemeTerritory(String schemeTerritory) {
            this.schemeTerritory = schemeTerritory;
            return this;
        }

        /**
         * Gets the TSL Type
         *
         * @return {@link String}
         */
        public String getType() {
            return tslType;
        }

        /**
         * Sets the TSL Type
         *
         * @param tslType {@link String}
         * @return {@link OtherListPointerBuilder}
         */
        public OtherListPointerBuilder setTslType(String tslType) {
            this.tslType = tslType;
            return this;
        }

        /**
         * Gets the MimeType of the Trusted List document
         *
         * @return {@link String}
         */
        public String getMimeType() {
            return mimeType;
        }

        /**
         * Sets the MimeType of the Trusted List document
         *
         * @param mimeType {@link String}
         * @return {@link OtherListPointerBuilder}
         */
        public OtherListPointerBuilder setMimeType(String mimeType) {
            this.mimeType = mimeType;
            return this;
        }

        /**
         * Gets a map of scheme operator names
         *
         * @return a map between {@link String} languages and lists of {@link String} names
         */
        public Map<String, List<String>> getSchemeOperatorNames() {
            return schemeOperatorNames;
        }

        /**
         * Sets a map of scheme operator names
         *
         * @param schemeOperatorNames a map between {@link String} languages and lists of {@link String} names
         * @return {@link OtherListPointerBuilder}
         */
        public OtherListPointerBuilder setSchemeOperatorNames(Map<String, List<String>> schemeOperatorNames) {
            this.schemeOperatorNames = schemeOperatorNames;
            return this;
        }

        /**
         * Gets a map of scheme type community rules
         *
         * @return a map between {@link String} languages and lists of {@link String} names
         */
        public Map<String, List<String>> getSchemeTypeCommunityRules() {
            return schemeTypeCommunityRules;
        }

        /**
         * Sets a map of scheme type community rules
         *
         * @param schemeTypeCommunityRules a map between {@link String} languages and lists of {@link String} names
         * @return {@link OtherListPointerBuilder}
         */
        public OtherListPointerBuilder setSchemeTypeCommunityRules(Map<String, List<String>> schemeTypeCommunityRules) {
            this.schemeTypeCommunityRules = schemeTypeCommunityRules;
            return this;
        }

        /**
         * Gets the MRA (Mutual Recognition Agreement) scheme
         *
         * @return {@link MRA}
         */
        public MRA getMra() {
            return mra;
        }

        /**
         * Sets the MRA (Mutual Recognition Agreement) scheme
         *
         * @param mra {@link MRA}
         * @return {@link OtherListPointerBuilder}
         */
        public OtherListPointerBuilder setMra(MRA mra) {
            this.mra = mra;
            return this;
        }

        /**
         * Builds the {@code OtherListPointer}
         *
         * @return {@link OtherListPointer}
         */
        public OtherListPointer build() {
            return new OtherListPointer(this);
        }

    }

}
