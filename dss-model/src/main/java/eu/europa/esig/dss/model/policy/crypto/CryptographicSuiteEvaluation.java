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

import eu.europa.esig.dss.enumerations.CryptographicSuiteAlgorithmUsage;
import eu.europa.esig.dss.enumerations.CryptographicSuiteRecommendation;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class provides a representation of an "Evaluation" element extracted
 * from an ETSI TS 119 322 cryptographic suite catalogue
 *
 */
public class CryptographicSuiteEvaluation implements Serializable {

    private static final long serialVersionUID = 993827242614523749L;

    /** A list of the /dssc:Evaluation/dssc:Parameter elements */
    private List<CryptographicSuiteParameter> parameterList;

    /** The value of the /dssc:Evaluation/dssc:Validity/dssc:Start element */
    private Date validityStart;

    /** The value of the /dssc:Evaluation/dssc:Validity/dssc:End element */
    private Date validityEnd;

    /** A list of the /dssc:Evaluation/etsi19322:MoreDetails/etsi19322:AlgorithmUsage elements */
    private List<CryptographicSuiteAlgorithmUsage> algorithmUsage;

    /** The value of the /dssc:Evaluation/dssc:Validity/etsi19322:MoreDetails/etsi19322:Recommendation element */
    private CryptographicSuiteRecommendation recommendation;

    /**
     * Default constructor
     */
    public CryptographicSuiteEvaluation() {
        // empty
    }

    /**
     * Gets the list of algorithm evaluation parameters
     *
     * @return a list of {@link String}
     */
    public List<CryptographicSuiteParameter> getParameterList() {
        return parameterList;
    }

    /**
     * Sets list of the /dssc:Evaluation/dssc:Parameter elements
     *
     * @param parameterList a list of {@link CryptographicSuiteParameter}s
     */
    public void setParameterList(List<CryptographicSuiteParameter> parameterList) {
        this.parameterList = parameterList;
    }

    /**
     * Gets the algorithm evaluation validity start date
     *
     * @return {@link Date}
     */
    public Date getValidityStart() {
        return validityStart;
    }

    /**
     * Sets a value of the /dssc:Evaluation/dssc:Validity/dssc:Start element
     *
     * @param validityStart {@link Date}
     */
    public void setValidityStart(Date validityStart) {
        this.validityStart = validityStart;
    }

    /**
     * Gets the algorithm evaluation validity end date
     *
     * @return {@link Date}
     */
    public Date getValidityEnd() {
        return validityEnd;
    }

    /**
     * Sets a value of the /dssc:Evaluation/dssc:Validity/dssc:End element
     *
     * @param validityEnd {@link Date}
     */
    public void setValidityEnd(Date validityEnd) {
        this.validityEnd = validityEnd;
    }

    /**
     * Gets the algorithm evaluation's usage scope
     *
     * @return a list of {@link CryptographicSuiteAlgorithmUsage}s
     */
    public List<CryptographicSuiteAlgorithmUsage> getAlgorithmUsage() {
        return algorithmUsage;
    }

    /**
     * Sets a list of the /dssc:Evaluation/etsi19322:MoreDetails/etsi19322:AlgorithmUsage elements
     *
     * @param algorithmUsage a list of {@link CryptographicSuiteAlgorithmUsage}s
     */
    public void setAlgorithmUsage(List<CryptographicSuiteAlgorithmUsage> algorithmUsage) {
        this.algorithmUsage = algorithmUsage;
    }

    /**
     * Gets the algorithm evaluation recommendation
     *
     * @return {@link CryptographicSuiteRecommendation}
     */
    public CryptographicSuiteRecommendation getRecommendation() {
        return recommendation;
    }

    /**
     * Sets a value of the /dssc:Evaluation/dssc:Validity/etsi19322:MoreDetails/etsi19322:Recommendation element
     *
     * @param recommendation a list of {@link CryptographicSuiteRecommendation}s
     */
    public void setRecommendation(CryptographicSuiteRecommendation recommendation) {
        this.recommendation = recommendation;
    }

    /**
     * Instantiates a new CryptographicSuiteEvaluation by copying the values of {@code evaluation}
     *
     * @param evaluation {@link CryptographicSuiteEvaluation}
     * @return {@link CryptographicSuiteEvaluation}
     */
    public static CryptographicSuiteEvaluation copy(CryptographicSuiteEvaluation evaluation) {
        if (evaluation == null) {
            return null;
        }
        final CryptographicSuiteEvaluation copy = new CryptographicSuiteEvaluation();
        if (evaluation.parameterList != null) {
            copy.parameterList = evaluation.parameterList.stream().map(CryptographicSuiteParameter::copy).collect(Collectors.toList());
        }
        copy.validityStart = evaluation.validityStart;
        copy.validityEnd = evaluation.validityEnd;
        if (evaluation.algorithmUsage != null) {
            copy.algorithmUsage = new ArrayList<>(evaluation.algorithmUsage);
        }
        copy.recommendation = evaluation.recommendation;
        return copy;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        CryptographicSuiteEvaluation that = (CryptographicSuiteEvaluation) object;
        return Objects.equals(parameterList, that.parameterList)
                && Objects.equals(validityStart, that.validityStart)
                && Objects.equals(validityEnd, that.validityEnd)
                && Objects.equals(algorithmUsage, that.algorithmUsage)
                && recommendation == that.recommendation;
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(parameterList);
        result = 31 * result + Objects.hashCode(validityStart);
        result = 31 * result + Objects.hashCode(validityEnd);
        result = 31 * result + Objects.hashCode(algorithmUsage);
        result = 31 * result + Objects.hashCode(recommendation);
        return result;
    }

    @Override
    public String toString() {
        return "CryptographicSuiteEvaluation [" +
                "parameterList=" + parameterList +
                ", validityStart=" + validityStart +
                ", validityEnd=" + validityEnd +
                ", algorithmUsage=" + algorithmUsage +
                ", recommendation=" + recommendation +
                ']';
    }

}
