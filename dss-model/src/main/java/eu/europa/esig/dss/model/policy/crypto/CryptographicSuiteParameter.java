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
import java.util.Objects;

/**
 * This class provides a representation of a "Parameter" element extracted
 * from an ETSI TS 119 322 cryptographic suite catalogue
 *
 */
public class CryptographicSuiteParameter implements Serializable {

    private static final long serialVersionUID = 8736984724050475430L;

    /** The value of the /dssc:Parameter/dssc:name element */
    private String name;

    /** The value of the /dssc:Parameter/dssc:Min element */
    private Integer min;

    /** The value of the /dssc:Parameter/dssc:Max element */
    private Integer max;

    /**
     * Default constructor
     */
    public CryptographicSuiteParameter() {
        // empty
    }

    /**
     * Gets the name identifier of parameter
     *
     * @return {@link String}
     */
    public String getName() {
        return name;
    }

    /**
     * Sets a value of the /dssc:Parameter/dssc:name element
     *
     * @param name {@link String}
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the min value of parameter
     *
     * @return {@link String}
     */
    public Integer getMin() {
        return min;
    }

    /**
     * Sets a value of the /dssc:Parameter/dssc:Min element
     *
     * @param min {@link Integer}
     */
    public void setMin(Integer min) {
        this.min = min;
    }

    /**
     * Gets the max value of parameter
     *
     * @return {@link String}
     */
    public Integer getMax() {
        return max;
    }

    /**
     * Sets a value of the /dssc:Parameter/dssc:Max element
     *
     * @param max {@link Integer}
     */
    public void setMax(Integer max) {
        this.max = max;
    }

    /**
     * Instantiates a new CryptographicSuiteParameter by copying the values of {@code parameter}
     *
     * @param parameter {@link CryptographicSuiteParameter}
     * @return {@link CryptographicSuiteParameter}
     */
    public static CryptographicSuiteParameter copy(CryptographicSuiteParameter parameter) {
        if (parameter == null) {
            return null;
        }
        final CryptographicSuiteParameter copy = new CryptographicSuiteParameter();
        copy.name = parameter.name;
        copy.min = parameter.min;
        copy.max = parameter.max;
        return copy;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;

        CryptographicSuiteParameter parameter = (CryptographicSuiteParameter) object;
        return Objects.equals(name, parameter.name)
                && Objects.equals(min, parameter.min)
                && Objects.equals(max, parameter.max);
    }

    @Override
    public int hashCode() {
        int result = Objects.hashCode(name);
        result = 31 * result + Objects.hashCode(min);
        result = 31 * result + Objects.hashCode(max);
        return result;
    }

    @Override
    public String toString() {
        return "CryptographicSuiteParameter [" +
                "name='" + name + '\'' +
                ", min=" + min +
                ", max=" + max +
                ']';
    }

}
