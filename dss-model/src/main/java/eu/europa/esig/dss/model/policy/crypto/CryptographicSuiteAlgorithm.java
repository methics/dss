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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides a representation of an "Algorithm" element extracted
 * from an ETSI TS 119 322 cryptographic suite catalogue
 *
 */
public class CryptographicSuiteAlgorithm implements Serializable {

    private static final long serialVersionUID = -273833860519018516L;

    /** The value of the /dssc:Algorithm/dssc:AlgorithmIdentifier/dssc:Name element */
    private String algorithmIdentifierName;

    /** A list of values from the /dssc:Algorithm/dssc:AlgorithmIdentifier/dssc:ObjectIdentifier elements */
    private List<String> algorithmIdentifierOIDs;

    /** A list of values from the /dssc:Algorithm/dssc:AlgorithmIdentifier/dssc:URI elements */
    private List<String> algorithmIdentifierURIs;

    /** A list of the /dssc:Algorithm/dssc:Evaluation elements */
    private List<CryptographicSuiteEvaluation> evaluationList;

    /** A list of values from the /dssc:Algorithm/dssc:Information/dssc:Text elements */
    private List<String> informationTextList;

    /**
     * Default constructor
     */
    public CryptographicSuiteAlgorithm() {
        // empty
    }

    /**
     * Gets the algorithm identifier name
     *
     * @return {@link String}
     */
    public String getAlgorithmIdentifierName() {
        return algorithmIdentifierName;
    }

    /**
     * Sets the value of the /dssc:Algorithm/dssc:AlgorithmIdentifier/dssc:Name element
     *
     * @param algorithmIdentifierName {@link String}
     */
    public void setAlgorithmIdentifierName(String algorithmIdentifierName) {
        this.algorithmIdentifierName = algorithmIdentifierName;
    }

    /**
     * Gets the algorithm identifier OIDs list
     *
     * @return a list of {@link String}
     */
    public List<String> getAlgorithmIdentifierOIDs() {
        return algorithmIdentifierOIDs;
    }

    /**
     * Sets a list of values from the /dssc:Algorithm/dssc:AlgorithmIdentifier/dssc:ObjectIdentifier elements
     *
     * @param algorithmIdentifierOIDs a list of {@link String}s
     */
    public void setAlgorithmIdentifierOIDs(List<String> algorithmIdentifierOIDs) {
        this.algorithmIdentifierOIDs = algorithmIdentifierOIDs;
    }

    /**
     * Gets the algorithm identifier URIs list
     *
     * @return a list of {@link String}
     */
    public List<String> getAlgorithmIdentifierURIs() {
        return algorithmIdentifierURIs;
    }

    /**
     * Sets a list of values from the /dssc:Algorithm/dssc:AlgorithmIdentifier/dssc:URI elements
     *
     * @param algorithmIdentifierURIs a list of {@link String}s
     */
    public void setAlgorithmIdentifierURIs(List<String> algorithmIdentifierURIs) {
        this.algorithmIdentifierURIs = algorithmIdentifierURIs;
    }

    /**
     * Gets a collection of algorithm evaluation requirements
     *
     * @return a list of {@link CryptographicSuiteEvaluation}s
     */
    public List<CryptographicSuiteEvaluation> getEvaluationList() {
        return evaluationList;
    }

    /**
     * Sets a list of the/dssc:Algorithm/dssc:Evaluation elements
     *
     * @param evaluationList a list of {@link CryptographicSuiteEvaluation}s
     */
    public void setEvaluationList(List<CryptographicSuiteEvaluation> evaluationList) {
        this.evaluationList = evaluationList;
    }

    /**
     * Gets a list of information text strings
     *
     * @return a list of {@link String}s
     */
    public List<String> getInformationTextList() {
        return informationTextList;
    }

    /**
     * Sets a list of values from the /dssc:Algorithm/dssc:Information/dssc:Text elements
     *
     * @param informationTextList a list of {@link String}s
     */
    public void setInformationTextList(List<String> informationTextList) {
        this.informationTextList = informationTextList;
    }

    /**
     * Instantiates a new CryptographicSuiteAlgorithm by copying the values of {@code algorithm}
     *
     * @param algorithm {@link CryptographicSuiteAlgorithm}
     * @return {@link CryptographicSuiteAlgorithm}
     */
    public static CryptographicSuiteAlgorithm copy(CryptographicSuiteAlgorithm algorithm) {
        if (algorithm == null) {
            return null;
        }
        final CryptographicSuiteAlgorithm copy = new CryptographicSuiteAlgorithm();
        copy.algorithmIdentifierName = algorithm.algorithmIdentifierName;
        if (algorithm.algorithmIdentifierOIDs != null) {
            copy.algorithmIdentifierOIDs = new ArrayList<>(algorithm.algorithmIdentifierOIDs);
        }
        if (algorithm.algorithmIdentifierURIs != null) {
            copy.algorithmIdentifierURIs = new ArrayList<>(algorithm.algorithmIdentifierURIs);
        }
        if (algorithm.evaluationList != null) {
            copy.evaluationList = algorithm.evaluationList.stream().map(CryptographicSuiteEvaluation::copy).collect(Collectors.toList());
        }
        if (algorithm.informationTextList != null) {
            copy.informationTextList = new ArrayList<>(algorithm.informationTextList);
        }
        return copy;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        CryptographicSuiteAlgorithm algorithm = (CryptographicSuiteAlgorithm) object;
        return Objects.equals(algorithmIdentifierName, algorithm.algorithmIdentifierName)
                && Objects.equals(algorithmIdentifierOIDs, algorithm.algorithmIdentifierOIDs)
                && Objects.equals(algorithmIdentifierURIs, algorithm.algorithmIdentifierURIs)
                && Objects.equals(evaluationList, algorithm.evaluationList)
                && Objects.equals(informationTextList, algorithm.informationTextList);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(algorithmIdentifierName);
        result = 31 * result + Objects.hashCode(algorithmIdentifierOIDs);
        result = 31 * result + Objects.hashCode(algorithmIdentifierURIs);
        result = 31 * result + Objects.hashCode(evaluationList);
        result = 31 * result + Objects.hashCode(informationTextList);
        return result;
    }

    @Override
    public String toString() {
        return "CryptographicSuiteAlgorithm [" +
                "algorithmIdentifierName='" + algorithmIdentifierName + '\'' +
                ", algorithmIdentifierOIDs=" + algorithmIdentifierOIDs +
                ", algorithmIdentifierURIs=" + algorithmIdentifierURIs +
                ", evaluationList=" + evaluationList +
                ", informationTextList=" + informationTextList +
                ']';
    }

}
