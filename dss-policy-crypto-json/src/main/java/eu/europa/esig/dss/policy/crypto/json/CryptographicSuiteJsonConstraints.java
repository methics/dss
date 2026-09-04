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
package eu.europa.esig.dss.policy.crypto.json;

/**
 * Contains a list of constraints for a JSON cryptographic suite as per ETSI TS 119 322.
 *
 */
public final class CryptographicSuiteJsonConstraints {

    /** Header name 'Address' definition */
    public static final String ADDRESS = "Address";

    /** Header name 'Algorithm' definition */
    public static final String ALGORITHM = "Algorithm";

    /** Header name 'AlgorithmIdentifier' definition */
    public static final String ALGORITHM_IDENTIFIER = "AlgorithmIdentifier";

    /** Header name 'AlgorithmUsage' definition */
    public static final String ALGORITHM_USAGE = "AlgorithmUsage";

    /** Header name 'Any' definition */
    public static final String ANY = "Any";

    /** Header name 'End' definition */
    public static final String END = "End";

    /** Header name 'Evaluation' definition */
    public static final String EVALUATION = "Evaluation";

    /** Header name 'id' definition */
    public static final String ID = "id";

    /** Header name 'Information' definition */
    public static final String INFORMATION = "Information";

    /** Header name 'lang' definition */
    public static final String LANG = "lang";

    /** Header name 'Max' definition */
    public static final String MAX = "Max";

    /** Header name 'Min' definition */
    public static final String MIN = "Min";

    /** Header name 'name' definition */
    public static final String NAME = "name";

    /** Header name 'Name' definition (capitalized) */
    public static final String NAME_C = "Name";

    /** Header name 'NextUpdate' definition (capitalized) */
    public static final String NEXT_UPDATE = "NextUpdate";

    /** Header name 'ObjectIdentifier' definition */
    public static final String OBJECT_IDENTIFIER = "ObjectIdentifier";

    /** Header name 'Parameter' definition */
    public static final String PARAMETER = "Parameter";

    /** Header name 'PolicyIssueDate' definition */
    public static final String POLICY_ISSUE_DATE = "PolicyIssueDate";

    /** Header name 'PolicyName' definition */
    public static final String POLICY_NAME = "PolicyName";

    /** Header name 'Publisher' definition */
    public static final String PUBLISHER = "Publisher";

    /** Header name 'Recommendation' definition */
    public static final String RECOMMENDATION = "Recommendation";

    /** SecuritySuitabilityPolicy */
    public static final String SECURITY_SUITABILITY_POLICY = "SecuritySuitabilityPolicy";

    /** Header name 'Start' definition */
    public static final String START = "Start";

    /** Header name 'Text' definition */
    public static final String TEXT = "Text";

    /** Header name 'URI' definition */
    public static final String URI = "URI";

    /** Header name 'Usage' definition */
    public static final String USAGE = "Usage";

    /** Header name 'Validity' definition */
    public static final String VALIDITY = "Validity";

    /** Header name 'Version' definition */
    public static final String VERSION = "version";

    /**
     * Utils class
     */
    private CryptographicSuiteJsonConstraints() {
        // empty
    }

}
