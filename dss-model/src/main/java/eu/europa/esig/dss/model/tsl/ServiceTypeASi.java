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
package eu.europa.esig.dss.model.tsl;

import java.io.Serializable;
import java.util.Objects;

/**
 * Contains information extracted from TrustServiceTSLType element
 *
 */
public class ServiceTypeASi implements Serializable {

	private static final long serialVersionUID = 4310435264375542204L;

	/** ServiceTypeIdentifier value */
	private String type;

	/** AdditionalServiceInformation value */
	private String asi;

	/**
	 * Default constructor instantiating object with null values
	 */
	public ServiceTypeASi() {
		// empty
	}

	/**
	 * Gets the ServiceTypeIdentifier value
	 *
	 * @return {@link String}
	 */
	public String getType() {
		return type;
	}

	/**
	 * Sets the ServiceTypeIdentifier value
	 *
	 * @param type {@link String}
	 */
	public void setType(String type) {
		this.type = type;
	}

	/**
	 * Gets the AdditionalServiceInformation value
	 *
	 * @return {@link String}
	 */
	public String getAsi() {
		return asi;
	}

	/**
	 * Sets the AdditionalServiceInformation value
	 *
	 * @param asi {@link String}
	 */
	public void setAsi(String asi) {
		this.asi = asi;
	}

	@Override
	public String toString() {
		return "ServiceTypeASi [" +
				"type='" + type + '\'' +
				", asi='" + asi + '\'' +
				']';
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;

		ServiceTypeASi that = (ServiceTypeASi) object;
		return Objects.equals(type, that.type)
				&& Objects.equals(asi, that.asi);
	}

	@Override
	public int hashCode() {
		int result = Objects.hashCode(type);
		result = 31 * result + Objects.hashCode(asi);
		return result;
	}
}
