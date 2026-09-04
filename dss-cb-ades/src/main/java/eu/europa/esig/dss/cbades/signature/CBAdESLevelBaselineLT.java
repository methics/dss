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
package eu.europa.esig.dss.cbades.signature;

import eu.europa.esig.dss.cbades.COSEHeaderParameter;
import eu.europa.esig.dss.cbades.cbor.CBORArray;
import eu.europa.esig.dss.cbades.cbor.CBORByteString;
import eu.europa.esig.dss.cbades.cbor.CBORMap;
import eu.europa.esig.dss.cbades.cbor.CBORObject;
import eu.europa.esig.dss.cbades.validation.CBAdESSignature;
import eu.europa.esig.dss.cbades.validation.CBAdESUHeaders;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.Token;
import eu.europa.esig.dss.signature.SignatureRequirementsChecker;
import eu.europa.esig.dss.spi.signature.AdvancedSignature;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.validation.ValidationData;
import eu.europa.esig.dss.spi.validation.ValidationDataContainer;
import eu.europa.esig.dss.spi.x509.revocation.crl.CRLToken;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import eu.europa.esig.dss.utils.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static eu.europa.esig.dss.enumerations.SignatureLevel.CB_AdES_BASELINE_LT;

/**
 * This class provides a functionality for the CB-AdES-BASELINE-LT profile augmentation.
 *
 */
public class CBAdESLevelBaselineLT extends CBAdESLevelBaselineT {

    /**
     * The default constructor
     *
     * @param certificateVerifier {@link CertificateVerifier} to use
     */
    public CBAdESLevelBaselineLT(CertificateVerifier certificateVerifier) {
        super(certificateVerifier);
    }

    @Override
    protected void extendSignatures(List<AdvancedSignature> signatures, CBAdESSignatureParameters params) {
        super.extendSignatures(signatures, params);

        final List<AdvancedSignature> signaturesToExtend = getExtendToLTLevelSignatures(signatures, params);
        if (Utils.isCollectionEmpty(signaturesToExtend)) {
            return;
        }

        // Reset sources
        for (AdvancedSignature signature : signaturesToExtend) {
            CBAdESSignature cbadesSignature = (CBAdESSignature) signature;

            // Data sources can already be loaded in memory (force reload)
            cbadesSignature.resetCertificateSource();
            cbadesSignature.resetRevocationSources();
            cbadesSignature.resetTimestampSource();
        }

        final SignatureRequirementsChecker signatureRequirementsChecker = getSignatureRequirementsChecker(params);
        if (CB_AdES_BASELINE_LT.equals(params.getSignatureLevel())) {
            signatureRequirementsChecker.assertExtendToLTLevelPossible(signaturesToExtend);
        }
        signatureRequirementsChecker.assertSignaturesValid(signaturesToExtend);
        signatureRequirementsChecker.assertCertificateChainValidForLTLevel(signaturesToExtend);

        // Perform signature validation
        ValidationDataContainer validationDataContainer = getValidationData(signatures, params);

        // Append ValidationData
        for (AdvancedSignature signature : signaturesToExtend) {
            CBAdESSignature cbadesSignature = (CBAdESSignature) signature;
            if (cbadesSignature.hasLTAProfile()) {
                continue;
            }

            assertUHeadersComponentsConsistent(cbadesSignature);

            CBAdESUHeaders uHeaders = cbadesSignature.getUHeaders();
            removeValidationData(cbadesSignature, uHeaders);

            final ValidationData validationDataForInclusion = validationDataContainer.getAllValidationDataForSignatureForInclusion(signature);
            incorporateValidationData(uHeaders, validationDataForInclusion);
        }
    }

    /**
     * Removes the 'valData' component, when present
     *
     * @param cbadesSignature {@link CBAdESSignature} being augmented
     * @param uHeaders {@link CBAdESUHeaders}
     */
    private void removeValidationData(CBAdESSignature cbadesSignature, CBAdESUHeaders uHeaders) {
        uHeaders.removeComponent(COSEHeaderParameter.VAL_DATA.cbor());
        cbadesSignature.resetCertificateSource();
        cbadesSignature.resetRevocationSources();
    }
    
    private void incorporateValidationData(CBAdESUHeaders uHeaders, final ValidationData validationDataForInclusion) {
        if (!validationDataForInclusion.isEmpty()) {
            CBORObject valData = getValData(validationDataForInclusion);
            uHeaders.addComponent(COSEHeaderParameter.VAL_DATA.cbor(), valData);
        }
    }

    /**
     * Gets a generated 'valData' uHeaders component containing the {@code validationDataForInclusion}
     *
     * @param validationDataForInclusion {@link ValidationData} to be incorporated
     * @return {@link CBORObject} 'valData' CBOR object
     */
    protected CBORObject getValData(final ValidationData validationDataForInclusion) {
        Set<CertificateToken> certificateTokens = validationDataForInclusion.getCertificateTokens();
        Set<CRLToken> crlTokens = validationDataForInclusion.getCrlTokens();
        Set<OCSPToken> ocspTokens = validationDataForInclusion.getOcspTokens();

        CBORMap valData = new CBORMap();
        if (Utils.isCollectionNotEmpty(certificateTokens)) {
            CBORObject xVals = getXVals(certificateTokens);
            valData.put(COSEHeaderParameter.VAL_DATA_X_VALS.cbor(), xVals);
        }
        if (Utils.isCollectionNotEmpty(crlTokens) || Utils.isCollectionNotEmpty(ocspTokens)) {
            CBORObject rVals = getRVals(crlTokens, ocspTokens);
            valData.put(COSEHeaderParameter.VAL_DATA_R_VALS.cbor(), rVals);
        }
        return valData;
    }

    /**
     * Builds and returns 'xVals' CBOR array
     *
     * @param certificateValuesToAdd a set of {@link CertificateToken}s to add
     * @return {@link CBORObject} 'xVals' CBOR array
     */
    private CBORObject getXVals(Set<CertificateToken> certificateValuesToAdd) {
        CBORArray xVals = new CBORArray();
        for (CertificateToken certificateToken : certificateValuesToAdd) {
            xVals.add(getX509CertObject(certificateToken));
        }
        return xVals;
    }

    private CBORObject getX509CertObject(CertificateToken certificateToken) {
        CBORMap x509OrOther = new CBORMap();
        x509OrOther.put(COSEHeaderParameter.X509_OR_OTHER_X509_CERT.cbor(), getPkiOb(certificateToken));

        return x509OrOther;
    }

    private CBORObject getPkiOb(Token token) {
        CBORMap pkiOb = new CBORMap();
        pkiOb.put(COSEHeaderParameter.PKI_OB_VAL.cbor(), new CBORByteString(token.getEncoded()));
        return pkiOb;
    }

    /**
     * Builds and returns 'rVals' CBOR Map
     *
     * @param crlsToAdd  a set of {@link CRLToken}s to add
     * @param ocspsToAdd a set of {@link OCSPToken}s to add
     * @return {@link CBORObject} 'rVals' CBOR Map
     */
    protected CBORObject getRVals(Set<CRLToken> crlsToAdd, Set<OCSPToken> ocspsToAdd) {
        CBORMap rVals = new CBORMap();
        if (Utils.isCollectionNotEmpty(crlsToAdd)) {
            rVals.put(COSEHeaderParameter.R_VALS_CRL_VALS.cbor(), getCrlVals(crlsToAdd));
        }
        if (Utils.isCollectionNotEmpty(ocspsToAdd)) {
            rVals.put(COSEHeaderParameter.R_VALS_OCSP_VALS.cbor(), getOcspVals(ocspsToAdd));
        }
        return rVals;
    }

    private CBORObject getCrlVals(Set<CRLToken> crlsToAdd) {
        CBORArray cborArray = new CBORArray();
        for (CRLToken crlToken : crlsToAdd) {
            cborArray.add(getPkiOb(crlToken));
        }
        return cborArray;
    }

    private CBORObject getOcspVals(Set<OCSPToken> ocspsToAdd) {
        CBORArray cborArray = new CBORArray();
        for (OCSPToken ocspToken : ocspsToAdd) {
            cborArray.add(getPkiOb(ocspToken));
        }
        return cborArray;
    }

    private List<AdvancedSignature> getExtendToLTLevelSignatures(List<AdvancedSignature> signatures, CBAdESSignatureParameters parameters) {
        final List<AdvancedSignature> toBeExtended = new ArrayList<>();
        for (AdvancedSignature signature : signatures) {
            if (ltLevelExtensionRequired(signature, parameters)) {
                toBeExtended.add(signature);
            }
        }
        return toBeExtended;
    }

    /**
     * Verifies if the LT-level extension is required and possible
     *
     * @param signature {@link AdvancedSignature} to check
     * @param parameters {@link CBAdESSignatureParameters}
     * @return TRUE if the extension is possible, FALSE otherwise
     */
    protected boolean ltLevelExtensionRequired(AdvancedSignature signature, CBAdESSignatureParameters parameters) {
        return CB_AdES_BASELINE_LT.equals(parameters.getSignatureLevel()) || !signature.hasLTAProfile();
    }

}
