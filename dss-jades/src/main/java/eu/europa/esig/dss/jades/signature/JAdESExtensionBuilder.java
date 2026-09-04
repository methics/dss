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
package eu.europa.esig.dss.jades.signature;

import eu.europa.esig.dss.enumerations.JWSSerializationType;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.spi.exception.IllegalInputException;
import eu.europa.esig.dss.jades.DSSJsonUtils;
import eu.europa.esig.dss.jades.JWSJsonSerializationObject;
import eu.europa.esig.dss.jades.validation.JWS;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The abstract class allowing the signature extension
 */
public abstract class JAdESExtensionBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(JAdESExtensionBuilder.class);

	/**
	 * Default constructor
	 */
	protected JAdESExtensionBuilder() {
		// empty
	}

	/**
	 * Checks if the type of etsiU components is consistent
	 *
	 * @param jws {@link JWS} to check
	 * @param signatureParameters {@link JAdESSignatureParameters}
	 */
	protected void assertEtsiUComponentsConsistent(JWS jws, JAdESSignatureParameters signatureParameters) {
		Boolean isBase64UrlEtsiUComponents = signatureParameters.isBase64UrlEncodedEtsiUComponents();
		isBase64UrlEtsiUComponents = assertEtsiUComponentsConsistent(jws, isBase64UrlEtsiUComponents);
		signatureParameters.setBase64UrlEncodedEtsiUComponents(isBase64UrlEtsiUComponents);
	}

	/**
	 * Checks if the type of etsiU components is consistent and returns the target encoding
	 *
	 * @param jws {@link JWS} to check
	 * @param isBase64UrlEtsiUComponents if the new component shall be base64url encoded
	 * @return TRUE if the etsiU parameters shall be base64url encoded, FALSE otherwise
	 */
	protected boolean assertEtsiUComponentsConsistent(JWS jws, Boolean isBase64UrlEtsiUComponents) {
		List<Object> etsiU = DSSJsonUtils.getEtsiU(jws);
		if (Utils.isCollectionNotEmpty(etsiU)) {
			if (!DSSJsonUtils.checkComponentsUnicity(etsiU)) {
				throw new IllegalInputException("Extension is not possible, because components of the 'etsiU' header have "
						+ "not common format! Shall be all Strings or Objects.");
			}

			boolean isEtsiUInBase64UrlForm = DSSJsonUtils.areAllBase64UrlComponents(etsiU);
			if (isBase64UrlEtsiUComponents == null) {
				LOG.info("base64UrlEtsiUComponents parameter is not defined. " +
						"The check of etsiU unsigned header structure is skipped. Use the current value.");

			} else if (isBase64UrlEtsiUComponents != isEtsiUInBase64UrlForm) {
				throw new IllegalInputException(String.format("Extension is not possible! The encoding of 'etsiU' "
								+ "components shall match! Use jadesSignatureParameters.setBase64UrlEncodedEtsiUComponents(%s)",
						!isBase64UrlEtsiUComponents));
			}
			return isEtsiUInBase64UrlForm;

		} else if (isBase64UrlEtsiUComponents == null) {
			if (LOG.isTraceEnabled()) {
				LOG.trace("base64UrlEtsiUComponents parameters is not defined. Use the default value (true).");
			}
			return true;
		}

		return isBase64UrlEtsiUComponents;
	}

	/**
	 * Checks if the {@code jwsJsonSerializationObject} is valid and can be extended
	 *
	 * @param jwsJsonSerializationObject {@link JWSJsonSerializationObject} to check
	 */
	protected void assertJWSJsonSerializationObjectValid(JWSJsonSerializationObject jwsJsonSerializationObject) {
		if (jwsJsonSerializationObject == null) {
			throw new IllegalInputException("The provided document is not a valid JAdES signature! Unable to extend.");
		}
		if (Utils.isCollectionEmpty(jwsJsonSerializationObject.getSignatures())) {
			throw new IllegalInputException("No signatures found to be extended!");
		}
		if (!jwsJsonSerializationObject.isValid()) {
			throw new IllegalInputException(String.format("Signature extension is not supported for invalid RFC 7515 files "
							+ "(shall be a Serializable JAdES signature). Reason(s) : %s",
					jwsJsonSerializationObject.getStructuralValidationErrors()));
		}
	}

	/**
	 * Checks if the given {@code jwsJsonSerializationObject} can be extended
	 *
	 * @param jwsJsonSerializationObject {@link JWSJsonSerializationObject} to check
	 */
	protected void assertJSONSerializationObjectMayBeExtended(JWSJsonSerializationObject jwsJsonSerializationObject) {
		assertJWSJsonSerializationObjectValid(jwsJsonSerializationObject);

		JWSSerializationType jwsSerializationType = jwsJsonSerializationObject.getJWSSerializationType();
		if (!JWSSerializationType.JSON_SERIALIZATION.equals(jwsSerializationType) &&
				!JWSSerializationType.FLATTENED_JSON_SERIALIZATION.equals(jwsSerializationType)) {
			throw new IllegalInputException("The extended signature shall have JSON Serialization (or Flattened) type! " +
					"Use JWSConverter to convert the signature.");
		}
	}

}
