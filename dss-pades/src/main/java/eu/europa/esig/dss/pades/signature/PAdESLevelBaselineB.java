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
package eu.europa.esig.dss.pades.signature;

import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESLevelBaselineB;
import eu.europa.esig.dss.model.DSSMessageDigest;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.cms.Attribute;
import org.bouncycastle.asn1.cms.CMSAttributes;

/**
 * PAdES Baseline B signature
 *
 */
public class PAdESLevelBaselineB extends CAdESLevelBaselineB {

	/** Message digest computed on the PAdES revision */
	private final DSSMessageDigest messageDigest;

	/**
	 * Default constructor
	 *
	 * @param messageDigest {@link DSSMessageDigest}
	 */
	public PAdESLevelBaselineB(final DSSMessageDigest messageDigest) {
		this.messageDigest = messageDigest;
	}

	@Override
	protected void addSigningTimeAttribute(final CAdESSignatureParameters parameters, final ASN1EncodableVector signedAttributes) {
		// In PAdES, we don't include the signing time : ETSI TS 102 778-3 V1.2.1
		// (2010-07): 4.5.3 signing-time Attribute
	}

	@Override
	protected void addSignerLocation(final CAdESSignatureParameters parameters, final ASN1EncodableVector signedAttributes) {
		// In PAdES, the role is in the signature dictionary
	}

	@Override
	protected void addContentIdentifier(final CAdESSignatureParameters parameters, final ASN1EncodableVector signedAttributes) {
		// this attribute is prohibited in PAdES B
	}

	@Override
	protected void addMimeType(final CAdESSignatureParameters parameters, final ASN1EncodableVector signedAttributes) {
		// skip for PAdES
	}

	@Override
	protected void addSignedAttributes(final CAdESSignatureParameters parameters, final ASN1EncodableVector signedAttributes) {
		super.addSignedAttributes(parameters, signedAttributes);
		addMessageImprint(parameters, signedAttributes);
	}

	/**
	 * Adds a message-imprint property
	 *
	 * @param parameters {@link CAdESSignatureParameters}
	 * @param signedAttributes {@link ASN1EncodableVector}
	 */
	protected void addMessageImprint(final CAdESSignatureParameters parameters, final ASN1EncodableVector signedAttributes) {
		signedAttributes.add(new Attribute(CMSAttributes.messageDigest, new DERSet(new DEROctetString(messageDigest.getValue()))));
	}

}
