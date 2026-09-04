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
package eu.europa.esig.dss.model.policy.crypto;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Contains metadata about the ETSI TS 119 322 cryptographic suite
 *
 */
public class CryptographicSuiteMetadata implements Serializable {

    private static final long serialVersionUID = 5256511704959101477L;

    /** The value of the /dssc:PolicyName/dssc:Name element */
    private String policyName;

    /** The value of the /dssc:PolicyName/dssc:ObjectIdentifier element */
    private String policyOID;

    /** The value of the /dssc:PolicyName/dssc:URI element */
    private String policyURI;

    /** The value of the /dssc:Publisher/dssc:Name element */
    private String publisherName;

    /** The value of the /dssc:Publisher/dssc:Address element */
    private String publisherAddress;

    /** The value of the /dssc:Publisher/dssc:URI element */
    private String publisherURI;

    /** The value of the /dssc:PolicyIssueDate element */
    private Date policyIssueDate;

    /** The value of the /dssc:NextUpdate element */
    private Date nextUpdate;

    /** The value of the /dssc:Usage element */
    private String usage;

    /** The value of the /dssc:version element */
    private String version;

    /** The value of the /dssc:lang element */
    private String lang;

    /** The value of the /dssc:id element */
    private String id;

    /**
     * Default constructor
     */
    public CryptographicSuiteMetadata() {
        // empty
    }

    /**
     * Gets the policy name
     *
     * @return {@link String}
     */
    public String getPolicyName() {
        return policyName;
    }

    /**
     * Sets the value of the /dssc:PolicyName/dssc:Name element
     *
     * @param policyName {@link String}
     */
    public void setPolicyName(String policyName) {
        this.policyName = policyName;
    }

    /**
     * Gets the policy OID
     *
     * @return {@link String}
     */
    public String getPolicyOID() {
        return policyOID;
    }

    /**
     * Sets the value of the /dssc:PolicyName/dssc:ObjectIdentifier element
     *
     * @param policyOID {@link String}
     */
    public void setPolicyOID(String policyOID) {
        this.policyOID = policyOID;
    }

    /**
     * Gets the policy URI
     *
     * @return {@link String}
     */
    public String getPolicyURI() {
        return policyURI;
    }

    /**
     * Sets the value of the /dssc:PolicyName/dssc:URI element
     *
     * @param policyURI {@link String}
     */
    public void setPolicyURI(String policyURI) {
        this.policyURI = policyURI;
    }

    /**
     * Gets the policy publisher's name
     *
     * @return {@link String}
     */
    public String getPublisherName() {
        return publisherName;
    }

    /**
     * Sets the value of the /dssc:Publisher/dssc:Name element
     *
     * @param publisherName {@link String}
     */
    public void setPublisherName(String publisherName) {
        this.publisherName = publisherName;
    }

    /**
     * Gets the policy publisher's address
     *
     * @return {@link String}
     */
    public String getPublisherAddress() {
        return publisherAddress;
    }

    /**
     * Sets the value of the /dssc:Publisher/dssc:Address element
     *
     * @param publisherAddress {@link String}
     */
    public void setPublisherAddress(String publisherAddress) {
        this.publisherAddress = publisherAddress;
    }

    /**
     * Gets the policy publisher's website URI
     *
     * @return {@link String}
     */
    public String getPublisherURI() {
        return publisherURI;
    }

    /**
     * Sets the value of the /dssc:Publisher/dssc:URI element
     *
     * @param publisherURI {@link String}
     */
    public void setPublisherURI(String publisherURI) {
        this.publisherURI = publisherURI;
    }

    /**
     * Gets the policy's issue date
     *
     * @return {@link Date}
     */
    public Date getPolicyIssueDate() {
        return policyIssueDate;
    }

    /**
     * Sets the value of the /dssc:PolicyIssueDate element
     *
     * @param policyIssueDate {@link Date}
     */
    public void setPolicyIssueDate(Date policyIssueDate) {
        this.policyIssueDate = policyIssueDate;
    }

    /**
     * Gets the policy's nest update date
     *
     * @return {@link Date}
     */
    public Date getNextUpdate() {
        return nextUpdate;
    }

    /**
     * Sets the value of the /dssc:NextUpdate element
     *
     * @param nextUpdate {@link Date}
     */
    public void setNextUpdate(Date nextUpdate) {
        this.nextUpdate = nextUpdate;
    }

    /**
     * Gets the policy usage
     *
     * @return {@link String}
     */
    public String getUsage() {
        return usage;
    }

    /**
     * Sets the value of the /dssc:Usage element
     *
     * @param usage {@link String}
     */
    public void setUsage(String usage) {
        this.usage = usage;
    }

    /**
     * Gets the policy version
     *
     * @return {@link String}
     */
    public String getVersion() {
        return version;
    }

    /**
     * Sets the value of the /dssc:version element
     *
     * @param version {@link String}
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Gets the policy language two-character ISO identifier
     *
     * @return {@link String}
     */
    public String getLang() {
        return lang;
    }

    /**
     * Sets the value of the /dssc:lang element
     *
     * @param lang {@link String}
     */
    public void setLang(String lang) {
        this.lang = lang;
    }

    /**
     * Gets the policy identifier
     *
     * @return {@link String}
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the value of the /dssc:id element
     *
     * @param id {@link String}
     */
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        CryptographicSuiteMetadata metadata = (CryptographicSuiteMetadata) object;
        return Objects.equals(policyName, metadata.policyName)
                && Objects.equals(policyOID, metadata.policyOID)
                && Objects.equals(policyURI, metadata.policyURI)
                && Objects.equals(publisherName, metadata.publisherName)
                && Objects.equals(publisherAddress, metadata.publisherAddress)
                && Objects.equals(publisherURI, metadata.publisherURI)
                && Objects.equals(policyIssueDate, metadata.policyIssueDate)
                && Objects.equals(nextUpdate, metadata.nextUpdate)
                && Objects.equals(usage, metadata.usage)
                && Objects.equals(version, metadata.version)
                && Objects.equals(lang, metadata.lang)
                && Objects.equals(id, metadata.id);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(policyName);
        result = 31 * result + Objects.hashCode(policyOID);
        result = 31 * result + Objects.hashCode(policyURI);
        result = 31 * result + Objects.hashCode(publisherName);
        result = 31 * result + Objects.hashCode(publisherAddress);
        result = 31 * result + Objects.hashCode(publisherURI);
        result = 31 * result + Objects.hashCode(policyIssueDate);
        result = 31 * result + Objects.hashCode(nextUpdate);
        result = 31 * result + Objects.hashCode(usage);
        result = 31 * result + Objects.hashCode(version);
        result = 31 * result + Objects.hashCode(lang);
        result = 31 * result + Objects.hashCode(id);
        return result;
    }

    @Override
    public String toString() {
        return "CryptographicSuiteMetadata [" +
                "policyName='" + policyName + '\'' +
                ", policyOID='" + policyOID + '\'' +
                ", policyURI='" + policyURI + '\'' +
                ", publisherName='" + publisherName + '\'' +
                ", publisherAddress='" + publisherAddress + '\'' +
                ", publisherURI='" + publisherURI + '\'' +
                ", policyIssueDate=" + policyIssueDate +
                ", nextUpdate=" + nextUpdate +
                ", usage='" + usage + '\'' +
                ", version='" + version + '\'' +
                ", lang='" + lang + '\'' +
                ", id='" + id + '\'' +
                ']';
    }

}
