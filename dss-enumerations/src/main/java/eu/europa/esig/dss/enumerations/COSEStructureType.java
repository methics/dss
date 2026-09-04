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

/**
 * Represents COSE signature structure types defined in RFC 9052, 4. Signing Objects
 *
 */
public enum COSEStructureType {

    /**
     * 4.1. Signing with One or More Signers
     * <p>
     * The COSE_Sign structure allows for one or more signatures to be
     * applied to a message payload.  Header parameters relating to the
     * content and header parameters relating to the signature are carried
     * along with the signature itself.  These header parameters may be
     * authenticated by the signature, or just be present.  An example of a
     * header parameter about the content is the content type header
     * parameter.  An example of a header parameter about the signature
     * would be the algorithm and key used to create the signature.
     * <p>
     * The signature structure can be encoded as either tagged or untagged,
     * depending on the context it will be used in.  A tagged COSE_Sign
     * structure is identified by the CBOR tag 98.  The CDDL fragment that
     * represents this is:
     * <p>
     * COSE_Sign_Tagged = #6.98(COSE_Sign)
     * <p>
     * A COSE Signed Message is defined in two parts.  The CBOR object that
     * carries the body and information about the message is called the
     * COSE_Sign structure.  The CBOR object that carries the signature and
     * information about the signature is called the COSE_Signature
     * structure.  Examples of COSE Signed Messages can be found in
     * Appendix C.1.
     * <p>
     * The COSE_Sign structure is a CBOR array.  The fields of the array, in
     * order, are:
     * <p>
     * protected:  This is as described in Section 3.
     * <p>
     * unprotected:  This is as described in Section 3.
     * <p>
     * payload:  This field contains the serialized content to be signed.
     *   If the payload is not present in the message, the application is
     *   required to supply the payload separately.  The payload is wrapped
     *   in a bstr to ensure that it is transported without changes.  If
     *   the payload is transported separately ("detached content"), then a
     *   nil CBOR object is placed in this location, and it is the
     *   responsibility of the application to ensure that it will be
     *   transported without changes.
     * <p>
     *   Note: When a signature with a message recovery algorithm is used
     *   (Section 8.1), the maximum number of bytes that can be recovered
     *   is the length of the original payload.  The size of the encoded
     *   payload is reduced by the number of bytes that will be recovered.
     *   If all of the bytes of the original payload are consumed, then the
     *   transmitted payload is encoded as a zero-length byte string rather
     *   than as being absent.
     * <p>
     * signatures:  This field is an array of signatures.  Each signature is
     *   represented as a COSE_Signature structure.
     * <p>
     * The CDDL fragment that represents the above text for COSE_Sign
     * follows.
     * <p>
     * COSE_Sign = [
     *    Headers,
     *    payload : bstr / nil,
     *    signatures : [+ COSE_Signature]
     * ]
     * <p>
     * The COSE_Signature structure is a CBOR array.  The fields of the
     * array, in order, are:
     * <p>
     * protected:  This is as described in Section 3.
     * <p>
     * unprotected:  This is as described in Section 3.
     * <p>
     * signature:  This field contains the computed signature value.  The
     *   type of the field is a bstr.  Algorithms MUST specify padding if
     *   the signature value is not a multiple of 8 bits.
     * <p>
     * The CDDL fragment that represents the above text for COSE_Signature
     * follows.
     * <p>
     * COSE_Signature =  [
     *    Headers,
     *    signature : bstr
     * ]
     */
    COSE_SIGN,

    /**
     * 4.2. Signing with One Signer
     * <p>
     * The COSE_Sign1 signature structure is used when only one signature is
     * going to be placed on a message.  The header parameters dealing with
     * the content and the signature are placed in the same pair of buckets,
     * rather than having the separation of COSE_Sign.
     *  <p>
     * The structure can be encoded as either tagged or untagged depending
     * on the context it will be used in.  A tagged COSE_Sign1 structure is
     * identified by the CBOR tag 18.  The CDDL fragment that represents
     * this is:
     * <p>
     * COSE_Sign1_Tagged = #6.18(COSE_Sign1)
     * <p>
     * The CBOR object that carries the body, the signature, and the
     * information about the body and signature is called the COSE_Sign1
     * structure.  Examples of COSE_Sign1 messages can be found in
     * Appendix C.2.
     * <p>
     * The COSE_Sign1 structure is a CBOR array.  The fields of the array,
     * in order, are:
     * <p>
     * protected:  This is as described in Section 3.
     * <p>
     * unprotected:  This is as described in Section 3.
     * <p>
     * payload:  This is as described in Section 4.1.
     * <p>
     * signature:  This field contains the computed signature value.  The
     *   type of the field is a bstr.
     * <p>
     * The CDDL fragment that represents the above text for COSE_Sign1
     * follows.
     * <p>
     * COSE_Sign1 = [
     *    Headers,
     *    payload : bstr / nil,
     *    signature : bstr
     * ]
     */
    COSE_SIGN1

}
