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

import eu.europa.esig.dss.enumerations.CryptographicSuiteAlgorithmUsage;
import eu.europa.esig.dss.enumerations.CryptographicSuiteRecommendation;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteAlgorithm;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteCatalogue;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteEvaluation;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteMetadata;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteParameter;
import eu.europa.esig.json.JsonObjectWrapper;
import eu.europa.esig.json.RFC3339DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * This class is used to parse an ETSI TS 119 322 JSON cryptographic suite catalog and return the extracted values
 *
 */
public class CryptographicSuiteJsonCatalogue extends CryptographicSuiteCatalogue {

    private static final Logger LOG = LoggerFactory.getLogger(CryptographicSuiteJsonCatalogue.class);

    /** Default value of the "version" parameter */
    private static final String DEFAULT_VERSION = "1";

    /** Default value of the "lang" parameter */
    private static final String DEFAULT_LANG = "en";

    /** Wrapped root element of ETSI TS 119 322 JSON schema */
    private final JsonObjectWrapper securitySuitabilityPolicy;

    /**
     * Default constructor to create an instance of {@code CryptographicSuiteJsonWrapper}
     *
     * @param securitySuitabilityPolicy {@link JsonObjectWrapper}
     */
    public CryptographicSuiteJsonCatalogue(JsonObjectWrapper securitySuitabilityPolicy) {
        Objects.requireNonNull(securitySuitabilityPolicy, "securitySuitabilityPolicy cannot be null!");
        this.securitySuitabilityPolicy = securitySuitabilityPolicy;
    }

    @Override
    protected CryptographicSuiteMetadata buildMetadata() {
        final CryptographicSuiteMetadata metadata = new CryptographicSuiteMetadata();

        JsonObjectWrapper policyName = securitySuitabilityPolicy.getAsObject(CryptographicSuiteJsonConstraints.POLICY_NAME);
        if (policyName != null) {
            metadata.setPolicyName(policyName.getAsString(CryptographicSuiteJsonConstraints.NAME_C));
            metadata.setPolicyOID(policyName.getAsString(CryptographicSuiteJsonConstraints.OBJECT_IDENTIFIER));
            metadata.setPolicyURI(policyName.getAsString(CryptographicSuiteJsonConstraints.URI));
        }

        JsonObjectWrapper publisher = securitySuitabilityPolicy.getAsObject(CryptographicSuiteJsonConstraints.PUBLISHER);
        if (publisher != null) {
            metadata.setPublisherName(publisher.getAsString(CryptographicSuiteJsonConstraints.NAME_C));
            metadata.setPublisherAddress(publisher.getAsString(CryptographicSuiteJsonConstraints.ADDRESS));
            metadata.setPublisherURI(publisher.getAsString(CryptographicSuiteJsonConstraints.URI));
        }

        metadata.setPolicyIssueDate(getAsDateTime(securitySuitabilityPolicy, CryptographicSuiteJsonConstraints.POLICY_ISSUE_DATE));
        metadata.setNextUpdate(getAsDateTime(securitySuitabilityPolicy, CryptographicSuiteJsonConstraints.NEXT_UPDATE));
        metadata.setUsage(securitySuitabilityPolicy.getAsString(CryptographicSuiteJsonConstraints.USAGE));

        metadata.setVersion(getVersion());
        metadata.setLang(getLang());
        metadata.setId(securitySuitabilityPolicy.getAsString(CryptographicSuiteJsonConstraints.ID));

        return metadata;
    }

    @Override
    protected List<CryptographicSuiteAlgorithm> buildAlgorithmList() {
        final List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();
        List<JsonObjectWrapper> algorithms = securitySuitabilityPolicy.getAsObjectList(CryptographicSuiteJsonConstraints.ALGORITHM);
        for (JsonObjectWrapper algorithmType : algorithms) {
            CryptographicSuiteAlgorithm algorithm = buildAlgorithm(algorithmType);
            if (algorithm != null) {
                algorithmList.add(algorithm);
            }
        }
        return algorithmList;
    }

    private CryptographicSuiteAlgorithm buildAlgorithm(JsonObjectWrapper algorithmType) {
        try {
            final CryptographicSuiteAlgorithm algorithm = new CryptographicSuiteAlgorithm();

            JsonObjectWrapper algorithmIdentifier = algorithmType.getAsObject(CryptographicSuiteJsonConstraints.ALGORITHM_IDENTIFIER);
            if (algorithmIdentifier != null) {
                algorithm.setAlgorithmIdentifierName(algorithmIdentifier.getAsString(CryptographicSuiteJsonConstraints.NAME_C));
                algorithm.setAlgorithmIdentifierOIDs(getAlgorithmIdentifierOIDs(algorithmIdentifier));
                algorithm.setAlgorithmIdentifierURIs(getAlgorithmIdentifierURIs(algorithmIdentifier));
            }

            algorithm.setEvaluationList(buildEvaluationList(algorithmType.getAsObjectList(CryptographicSuiteJsonConstraints.EVALUATION)));
            algorithm.setInformationTextList(getInformationText(algorithmType));

            return algorithm;

        } catch (Exception e) {
            String errorMessage = "An error occurred during processing of an algorithm JSON entry : {}. The entry is skipped.";
            if (LOG.isDebugEnabled()) {
                LOG.warn(errorMessage, e.getMessage(), e);
            } else {
                LOG.warn(errorMessage, e.getMessage());
            }
            return null;
        }
    }

    private List<String> getAlgorithmIdentifierOIDs(JsonObjectWrapper algorithmIdentifier) {
        String algorithmOID = algorithmIdentifier.getAsString(CryptographicSuiteJsonConstraints.OBJECT_IDENTIFIER);
        if (algorithmOID != null) {
            return Collections.singletonList(algorithmOID);
        }
        return Collections.emptyList();
    }

    private List<String> getAlgorithmIdentifierURIs(JsonObjectWrapper algorithmIdentifier) {
        String algorithmURI = algorithmIdentifier.getAsString(CryptographicSuiteJsonConstraints.URI);
        if (algorithmURI != null) {
            return Collections.singletonList(algorithmURI);
        }
        return Collections.emptyList();
    }

    private List<CryptographicSuiteEvaluation> buildEvaluationList(List<JsonObjectWrapper> evaluations) {
        final List<CryptographicSuiteEvaluation> evaluationList = new ArrayList<>();
        for (JsonObjectWrapper evaluationType : evaluations) {
            evaluationList.add(buildEvaluation(evaluationType));
        }
        return evaluationList;
    }

    private List<String> getInformationText(JsonObjectWrapper algorithmType) {
        JsonObjectWrapper information = algorithmType.getAsObject(CryptographicSuiteJsonConstraints.INFORMATION);
        if (information == null) {
            return Collections.emptyList();
        }
        return information.getAsStringList(CryptographicSuiteJsonConstraints.TEXT);
    }

    private CryptographicSuiteEvaluation buildEvaluation(JsonObjectWrapper evaluationType) {
        final CryptographicSuiteEvaluation evaluation = new CryptographicSuiteEvaluation();
        evaluation.setParameterList(buildParameterList(evaluationType.getAsObjectList(CryptographicSuiteJsonConstraints.PARAMETER)));

        JsonObjectWrapper validity = evaluationType.getAsObject(CryptographicSuiteJsonConstraints.VALIDITY);
        if (validity != null) {
            evaluation.setValidityStart(getAsDate(validity, CryptographicSuiteJsonConstraints.START));
            evaluation.setValidityEnd(getAsDate(validity, CryptographicSuiteJsonConstraints.END));
        }

        evaluation.setAlgorithmUsage(getAlgorithmUsage(evaluationType));
        evaluation.setRecommendation(getRecommendation(evaluationType));

        return evaluation;
    }

    private List<CryptographicSuiteParameter> buildParameterList(List<JsonObjectWrapper> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return Collections.emptyList();
        }
        final List<CryptographicSuiteParameter> parameterList = new ArrayList<>();
        for (JsonObjectWrapper parameterType : parameters) {
            parameterList.add(buildParameter(parameterType));
        }
        return parameterList;
    }

    private CryptographicSuiteParameter buildParameter(JsonObjectWrapper parameterType) {
        final CryptographicSuiteParameter parameter = new CryptographicSuiteParameter();
        parameter.setName(parameterType.getAsString(CryptographicSuiteJsonConstraints.NAME));
        parameter.setMin(toInteger(parameterType.getAsNumber(CryptographicSuiteJsonConstraints.MIN)));
        parameter.setMax(toInteger(parameterType.getAsNumber(CryptographicSuiteJsonConstraints.MAX)));
        return parameter;
    }

    private List<CryptographicSuiteAlgorithmUsage> getAlgorithmUsage(JsonObjectWrapper evaluationType) {
        String algorithmUsageStr = evaluationType.getAsString(CryptographicSuiteJsonConstraints.ALGORITHM_USAGE);
        if (algorithmUsageStr == null) {
            return Collections.emptyList();
        }
        CryptographicSuiteAlgorithmUsage algorithmUsage = CryptographicSuiteAlgorithmUsage.fromUri(algorithmUsageStr);
        if (algorithmUsage == null) {
            return Collections.emptyList();
        }
        return Collections.singletonList(algorithmUsage);
    }

    private CryptographicSuiteRecommendation getRecommendation(JsonObjectWrapper evaluationType) {
        String recommendation = evaluationType.getAsString(CryptographicSuiteJsonConstraints.RECOMMENDATION);
        if (recommendation == null) {
            return null;
        }
        return CryptographicSuiteRecommendation.fromValue(recommendation);
    }

    private String getVersion() {
        String version = securitySuitabilityPolicy.getAsString(CryptographicSuiteJsonConstraints.VERSION);
        if (version != null) {
            return version;
        }
        return DEFAULT_VERSION;
    }

    private String getLang() {
        String lang = securitySuitabilityPolicy.getAsString(CryptographicSuiteJsonConstraints.LANG);
        if (lang != null) {
            return lang;
        }
        return DEFAULT_LANG;
    }

    private Integer toInteger(Number number) {
        if (number == null) {
            return null;
        }
        return number.intValue();
    }

    /**
     * Gets a value of the header {@code name} as a {@code java.util.Date}.
     * If not present, or not able to convert, returns null.
     *
     * @param name {@link String} header name to get a value for
     * @return {@link Date}
     */
    private Date getAsDate(JsonObjectWrapper jsonObject, String name) {
        String dateString = jsonObject.getAsString(name);
        if (dateString == null) {
            return null;
        }
        return RFC3339DateUtils.getDate(dateString);
    }

    /**
     * Gets a value of the header {@code name} as a {@code java.util.Date} with time.
     * If not present, or not able to convert, returns null.
     *
     * @param name {@link String} header name to get a value for
     * @return {@link Date}
     */
    private Date getAsDateTime(JsonObjectWrapper jsonObject, String name) {
        String dateString = jsonObject.getAsString(name);
        if (dateString == null) {
            return null;
        }
        return RFC3339DateUtils.getDateTime(dateString);
    }

}
