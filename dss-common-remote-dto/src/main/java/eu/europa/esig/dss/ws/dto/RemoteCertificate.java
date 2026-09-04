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
package eu.europa.esig.dss.ws.dto;

import java.io.Serializable;
import java.util.Arrays;

/**
 * DTO containing certificateToken binaries
 */
@SuppressWarnings("serial")
public class RemoteCertificate implements Serializable {

	/** The DER-encoded binaries of the certificate */
	private byte[] encodedCertificate;

	/**
	 * Empty constructor
	 */
	public RemoteCertificate() {
		// empty
	}

	/**
	 * The default constructor
	 *
	 * @param encodedCertificate the DER-encoded binaries of the certificate
	 */
	public RemoteCertificate(byte[] encodedCertificate) {
		this.encodedCertificate = encodedCertificate;
	}

	/**
	 * Gets the DER-encoded binaries of the certificate
	 *
	 * @return DER-encoded binaries of the certificate
	 */
	public byte[] getEncodedCertificate() {
		return encodedCertificate;
	}

	/**
	 * Sets the DER-encoded binaries of the certificate
	 *
	 * @param encodedCertificate DER-encoded binaries of the certificate
	 */
	public void setEncodedCertificate(byte[] encodedCertificate) {
		this.encodedCertificate = encodedCertificate;
	}

	@Override
	public String toString() {
		return "RemoteCertificate [" +
				"encodedCertificate=" + Arrays.toString(encodedCertificate) +
				']';
	}

	@Override
	public boolean equals(Object object) {
		if (this == object) return true;
		if (object == null || getClass() != object.getClass()) return false;

		RemoteCertificate that = (RemoteCertificate) object;
		return Arrays.equals(encodedCertificate, that.encodedCertificate);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(encodedCertificate);
	}

}
