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
package eu.europa.esig.dss.service;

import java.security.SecureRandom;

/**
 * This class produces nonce values based on a SecureRandom.
 *
 */
public class SecureRandomNonceSource implements NonceSource {

	private static final long serialVersionUID = 8999041563539837258L;

	/** The secure random instance */
	private final SecureRandom secureRandom = new SecureRandom();

	/** The size of the nonce value for the requests */
	private final int nonceSize;

	/**
	 * Default constructor instantiating a SecureRandom.
	 * This constructor creates a nonce with a value length of 32 octets.
	 */
	public SecureRandomNonceSource() {
		this(32);
	}

	/**
	 * Constructor instantiating a SecureRandom with the defined target nonce size.
	 * The nonce will be generated with the octets length equal to the provided value.
	 *
	 * @param nonceSize the size of the nonce value to be generated on request
	 */
	public SecureRandomNonceSource(final int nonceSize) {
		if (nonceSize < 1) {
			throw new IllegalArgumentException("The nonce size cannot be 0 or smaller!");
		}
		this.nonceSize = nonceSize;
	}

	@Override
	public byte[] getNonceValue() {
		byte[] bytes = new byte[nonceSize];
		secureRandom.nextBytes(bytes);
		return bytes;
	}

}
