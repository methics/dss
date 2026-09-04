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
package eu.europa.esig.dss.enumerations;

import java.util.Objects;

import static eu.europa.esig.dss.enumerations.SignatureForm.CAdES;
import static eu.europa.esig.dss.enumerations.SignatureForm.PAdES;
import static eu.europa.esig.dss.enumerations.SignatureForm.PKCS7;
import static eu.europa.esig.dss.enumerations.SignatureForm.XAdES;
import static eu.europa.esig.dss.enumerations.SignatureProfile.AdES;
import static eu.europa.esig.dss.enumerations.SignatureProfile.BASELINE_B;
import static eu.europa.esig.dss.enumerations.SignatureProfile.BASELINE_LT;
import static eu.europa.esig.dss.enumerations.SignatureProfile.BASELINE_LTA;
import static eu.europa.esig.dss.enumerations.SignatureProfile.BASELINE_T;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_A;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_BES;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_C;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_EPES;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_ERS;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_LT;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_LTV;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_T;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_X;
import static eu.europa.esig.dss.enumerations.SignatureProfile.EXTENDED_XL;
import static eu.europa.esig.dss.enumerations.SignatureProfile.NOT_ETSI;

/**
 * Signature profiles (form+level) handled by the SD-DSS framework.
 *
 */
public enum SignatureLevel {

	XML_NOT_ETSI(XAdES, NOT_ETSI), XAdES_BES(XAdES, EXTENDED_BES), XAdES_EPES(XAdES, EXTENDED_EPES), XAdES_T(XAdES, EXTENDED_T), XAdES_LT(XAdES, EXTENDED_LT),
	XAdES_C(XAdES, EXTENDED_C), XAdES_X(XAdES, EXTENDED_X), XAdES_XL(XAdES, EXTENDED_XL), XAdES_A(XAdES, EXTENDED_A), XAdES_ERS(XAdES, EXTENDED_ERS),
	XAdES_BASELINE_B(XAdES, BASELINE_B), XAdES_BASELINE_T(XAdES, BASELINE_T), XAdES_BASELINE_LT(XAdES, BASELINE_LT), XAdES_BASELINE_LTA(XAdES, BASELINE_LTA),

	CMS_NOT_ETSI(CAdES, NOT_ETSI), CAdES_BES(CAdES, EXTENDED_BES), CAdES_EPES(CAdES, EXTENDED_EPES), CAdES_T(CAdES, EXTENDED_T), CAdES_LT(CAdES, EXTENDED_LT),
	CAdES_C(CAdES, EXTENDED_C), CAdES_X(CAdES, EXTENDED_X), CAdES_XL(CAdES, EXTENDED_XL), CAdES_A(CAdES, EXTENDED_A), CAdES_ERS(CAdES, EXTENDED_ERS),
	CAdES_BASELINE_B(CAdES, BASELINE_B), CAdES_BASELINE_T(CAdES, BASELINE_T), CAdES_BASELINE_LT(CAdES, BASELINE_LT), CAdES_BASELINE_LTA(CAdES, BASELINE_LTA),

	PDF_NOT_ETSI(PAdES, NOT_ETSI), PKCS7_B(PKCS7, NOT_ETSI), PKCS7_T(PKCS7, NOT_ETSI), PKCS7_LT(PKCS7, NOT_ETSI), PKCS7_LTA(PKCS7, NOT_ETSI),
	PAdES_BES(PAdES, EXTENDED_BES), PAdES_EPES(PAdES, EXTENDED_EPES), PAdES_LTV(PAdES, EXTENDED_LTV),
	PAdES_BASELINE_B(PAdES, BASELINE_B), PAdES_BASELINE_T(PAdES, BASELINE_T), PAdES_BASELINE_LT(PAdES, BASELINE_LT), PAdES_BASELINE_LTA(PAdES, BASELINE_LTA),

	JSON_NOT_ETSI(SignatureForm.JAdES, NOT_ETSI), JAdES(SignatureForm.JAdES, AdES), JAdES_BASELINE_B(SignatureForm.JAdES, BASELINE_B),
	JAdES_BASELINE_T(SignatureForm.JAdES, BASELINE_T), JAdES_BASELINE_LT(SignatureForm.JAdES, BASELINE_LT), JAdES_BASELINE_LTA(SignatureForm.JAdES, BASELINE_LTA),

	CBOR_NOT_ETSI(SignatureForm.CBAdES, NOT_ETSI), CB_AdES(SignatureForm.CBAdES, AdES), CB_AdES_BASELINE_B(SignatureForm.CBAdES, BASELINE_B),
	CB_AdES_BASELINE_T(SignatureForm.CBAdES, BASELINE_T), CB_AdES_BASELINE_LT(SignatureForm.CBAdES, BASELINE_LT), CB_AdES_BASELINE_LTA(SignatureForm.CBAdES, BASELINE_LTA),
	
	UNKNOWN(null, NOT_ETSI);

	/** Defines the signature format */
	private final SignatureForm signatureForm;

	/** Defines the signature profile */
	private final SignatureProfile signatureProfile;

	/**
	 * Default constructor
	 *
	 * @param signatureForm {@link SignatureForm}
	 * @param signatureProfile {@link SignatureProfile}
	 */
	SignatureLevel(final SignatureForm signatureForm, final SignatureProfile signatureProfile) {
		this.signatureForm = signatureForm;
		this.signatureProfile = signatureProfile;
	}

	/**
	 * Returns the SignatureLevel based on the name (String)
	 *
	 * @param name
	 *            the signature level's name to retrieve
	 * @return the SignatureLevel
	 */
	public static SignatureLevel valueByName(String name) {
		return valueOf(name.replace('-', '_'));
	}

	@Override
	public String toString() {
		return super.toString().replace('_', '-');
	}

	/**
	 * Returns the corresponding {@code SignatureForm}
	 *
	 * @return the {@link SignatureForm} depending on the {@link SignatureLevel}
	 */
	public SignatureForm getSignatureForm() {
		if (signatureForm == null) {
			throw new UnsupportedOperationException(String.format("The signature level '%s' is not supported!", this));
		}
		return signatureForm;
	}
	/**
	 * Returns the corresponding {@code SignatureProfile}
	 *
	 * @return the {@link SignatureProfile} depending on the {@link SignatureLevel}
	 */
	public SignatureProfile getSignatureProfile() {
		if (signatureProfile == null) {
			throw new UnsupportedOperationException(String.format("The signature level '%s' is not supported!", this));
		}
		return signatureProfile;
	}

	/**
	 * This method returns an applicable SignatureLevel for the given {@code SignatureForm} and {@code SignatureProfile}
	 *
	 * @param signatureForm {@link SignatureForm}
	 * @param signatureProfile {@link SignatureProfile}
	 * @return {@link SignatureLevel} if a corresponding signature level is supported, null otherwise
	 */
	public static SignatureLevel getSignatureLevel(SignatureForm signatureForm, SignatureProfile signatureProfile) {
		for (SignatureLevel currentLevel : values()) {
			if (Objects.equals(signatureForm, currentLevel.getSignatureForm())
					&& Objects.equals(signatureProfile, currentLevel.getSignatureProfile())) {
				return currentLevel;
			}
		}
		return null;
	}

}
