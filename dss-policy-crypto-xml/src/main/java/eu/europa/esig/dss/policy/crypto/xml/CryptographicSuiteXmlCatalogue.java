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
package eu.europa.esig.dss.policy.crypto.xml;

import eu.europa.esig.dss.enumerations.CryptographicSuiteAlgorithmUsage;
import eu.europa.esig.dss.enumerations.CryptographicSuiteRecommendation;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteCatalogue;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteAlgorithm;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteEvaluation;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteMetadata;
import eu.europa.esig.dss.model.policy.crypto.CryptographicSuiteParameter;
import eu.europa.esig.dss.policy.crypto.xml.jaxb.AlgorithmIdentifierType;
import eu.europa.esig.dss.policy.crypto.xml.jaxb.AlgorithmType;
import eu.europa.esig.dss.policy.crypto.xml.jaxb.EvaluationType;
import eu.europa.esig.dss.policy.crypto.xml.jaxb.ParameterType;
import eu.europa.esig.dss.policy.crypto.xml.jaxb.PolicyNameType;
import eu.europa.esig.dss.policy.crypto.xml.jaxb.PublisherType;
import eu.europa.esig.dss.policy.crypto.xml.jaxb.SecuritySuitabilityPolicyType;
import eu.europa.esig.dss.policy.crypto.xml.jaxb.ValidityType;
import eu.europa.esig.dss.policy.crypto.xml.jaxb.algocat.ExtensionType;
import jakarta.xml.bind.JAXBElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.datatype.XMLGregorianCalendar;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * This class is used to parse an ETSI TS 119 322 XML cryptographic suite catalog and return the extracted values
 *
 */
public class CryptographicSuiteXmlCatalogue extends CryptographicSuiteCatalogue {

    private static final Logger LOG = LoggerFactory.getLogger(CryptographicSuiteXmlCatalogue.class);

    /** Wrapped SecuritySuitabilityPolicyType */
    private final SecuritySuitabilityPolicyType securitySuitabilityPolicy;

    /**
     * Default constructor
     *
     * @param securitySuitabilityPolicy {@link SecuritySuitabilityPolicyType}
     */
    public CryptographicSuiteXmlCatalogue(final SecuritySuitabilityPolicyType securitySuitabilityPolicy) {
        Objects.requireNonNull(securitySuitabilityPolicy, "securitySuitabilityPolicy cannot be null!");
        this.securitySuitabilityPolicy = securitySuitabilityPolicy;
    }

    @Override
    protected CryptographicSuiteMetadata buildMetadata() {
        final CryptographicSuiteMetadata metadata = new CryptographicSuiteMetadata();

        PolicyNameType policyName = securitySuitabilityPolicy.getPolicyName();
        if (policyName != null) {
            metadata.setPolicyName(policyName.getName());
            metadata.setPolicyOID(policyName.getObjectIdentifier());
            metadata.setPolicyURI(policyName.getURI());
        }

        PublisherType publisher = securitySuitabilityPolicy.getPublisher();
        if (publisher != null) {
            metadata.setPublisherName(publisher.getName());
            metadata.setPublisherAddress(publisher.getAddress());
            metadata.setPublisherURI(publisher.getURI());
        }

        metadata.setPolicyIssueDate(toDate(securitySuitabilityPolicy.getPolicyIssueDate()));
        metadata.setNextUpdate(toDate(securitySuitabilityPolicy.getNextUpdate()));
        metadata.setUsage(securitySuitabilityPolicy.getUsage());

        metadata.setVersion(securitySuitabilityPolicy.getVersion());
        metadata.setLang(securitySuitabilityPolicy.getLang());
        metadata.setId(securitySuitabilityPolicy.getId());

        return metadata;
    }

    @Override
    protected List<CryptographicSuiteAlgorithm> buildAlgorithmList() {
        final List<CryptographicSuiteAlgorithm> algorithmList = new ArrayList<>();
        for (AlgorithmType algorithmType : securitySuitabilityPolicy.getAlgorithm()) {
            CryptographicSuiteAlgorithm algorithm = buildAlgorithm(algorithmType);
            if (algorithm != null) {
                algorithmList.add(algorithm);
            }
        }
        return algorithmList;
    }

    private CryptographicSuiteAlgorithm buildAlgorithm(AlgorithmType algorithmType) {
        try {
            final CryptographicSuiteAlgorithm algorithm = new CryptographicSuiteAlgorithm();

            AlgorithmIdentifierType algorithmIdentifier = algorithmType.getAlgorithmIdentifier();
            algorithm.setAlgorithmIdentifierName(algorithmIdentifier.getName());
            algorithm.setAlgorithmIdentifierOIDs(algorithmIdentifier.getObjectIdentifier());
            algorithm.setAlgorithmIdentifierURIs(algorithmIdentifier.getURI());

            algorithm.setEvaluationList(buildEvaluationList(algorithmType.getEvaluation()));
            algorithm.setInformationTextList(getInformationText(algorithmType));

            return algorithm;

        } catch (Exception e) {
            String errorMessage = "An error occurred during processing of an algorithm XML entry : {}. The entry is skipped.";
            if (LOG.isDebugEnabled()) {
                LOG.warn(errorMessage, e.getMessage(), e);
            } else {
                LOG.warn(errorMessage, e.getMessage());
            }
            return null;
        }
    }

    private List<CryptographicSuiteEvaluation> buildEvaluationList(List<EvaluationType> evaluations) {
        final List<CryptographicSuiteEvaluation> evaluationList = new ArrayList<>();
        for (EvaluationType evaluationType : evaluations) {
            evaluationList.add(buildEvaluation(evaluationType));
        }
        return evaluationList;
    }

    private List<String> getInformationText(AlgorithmType algorithmType) {
        if (algorithmType.getInformation() == null) {
            return Collections.emptyList();
        }
        return algorithmType.getInformation().getText();
    }

    private CryptographicSuiteEvaluation buildEvaluation(EvaluationType evaluationType) {
        final CryptographicSuiteEvaluation evaluation = new CryptographicSuiteEvaluation();
        evaluation.setParameterList(buildParameterList(evaluationType.getParameter()));

        ValidityType validity = evaluationType.getValidity();
        if (validity != null) {
            evaluation.setValidityStart(toDate(validity.getStart()));
            evaluation.setValidityEnd(toDate(validity.getEnd()));
        }

        ExtensionType extensionType = getExtensionType(evaluationType.getAny());
        evaluation.setAlgorithmUsage(getAlgorithmUsageList(extensionType));
        evaluation.setRecommendation(getRecommendation(extensionType));

        return evaluation;
    }

    private ExtensionType getExtensionType(Object anyObject) {
        return toTargetType(anyObject, ExtensionType.class, "MoreDetails");
    }

    @SuppressWarnings("unchecked")
    private <T> T toTargetType(Object object, Class<T> targetClass, String elementName) {
        if (object instanceof JAXBElement) {
            JAXBElement<?> jaxbElement = (JAXBElement<?>) object;
            Object value = jaxbElement.getValue();
            if (jaxbElement.getName().getLocalPart().equals(elementName) && targetClass.isInstance(value)) {
                return (T) value;
            }
        }
        return null;
    }

    private List<CryptographicSuiteAlgorithmUsage> getAlgorithmUsageList(ExtensionType extensionType) {
        if (extensionType == null) {
            return Collections.emptyList();
        }
        final List<CryptographicSuiteAlgorithmUsage> algorithmUsageList = new ArrayList<>();
        for (Object object : extensionType.getContent()) {
            String algorithmUsageUri = toTargetType(object, String.class, "AlgorithmUsage");
            CryptographicSuiteAlgorithmUsage algorithmUsage = CryptographicSuiteAlgorithmUsage.fromUri(algorithmUsageUri);
            if (algorithmUsage != null) {
                algorithmUsageList.add(algorithmUsage);
            }
        }
        return algorithmUsageList;
    }

    private CryptographicSuiteRecommendation getRecommendation(ExtensionType extensionType) {
        if (extensionType == null) {
            return null;
        }
        for (Object object : extensionType.getContent()) {
            String recommendationValue = toTargetType(object, String.class, "Recommendation");
            CryptographicSuiteRecommendation recommendation = CryptographicSuiteRecommendation.fromValue(recommendationValue);
            if (recommendation != null) {
                return recommendation;
            }
        }
        return null;
    }

    private Date toDate(XMLGregorianCalendar xmlGregorianCalendar) {
        if (xmlGregorianCalendar == null) {
            return null;
        }
        return xmlGregorianCalendar.toGregorianCalendar().getTime();
    }

    private List<CryptographicSuiteParameter> buildParameterList(List<ParameterType> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return Collections.emptyList();
        }
        final List<CryptographicSuiteParameter> parameterList = new ArrayList<>();
        for (ParameterType parameterType : parameters) {
            parameterList.add(buildParameter(parameterType));
        }
        return parameterList;
    }

    private CryptographicSuiteParameter buildParameter(ParameterType parameterType) {
        final CryptographicSuiteParameter parameter = new CryptographicSuiteParameter();
        parameter.setName(parameterType.getName());
        parameter.setMin(parameterType.getMin());
        parameter.setMax(parameterType.getMax());
        return parameter;
    }

}
