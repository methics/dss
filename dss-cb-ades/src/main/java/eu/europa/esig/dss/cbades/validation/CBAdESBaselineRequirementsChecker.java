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
package eu.europa.esig.dss.cbades.validation;

import eu.europa.esig.dss.cbades.COSEHeaderParameter;
import eu.europa.esig.dss.cbades.COSEProtectedHeader;
import eu.europa.esig.dss.cbades.cbor.CBORArray;
import eu.europa.esig.dss.cbades.cbor.CBORMap;
import eu.europa.esig.dss.cbades.cbor.CBORObject;
import eu.europa.esig.dss.cbades.cbor.CBORUtils;
import eu.europa.esig.dss.cbades.cwt.CWTClaims;
import eu.europa.esig.dss.spi.signature.BaselineRequirementsChecker;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Performs checks according to TS 119 152-1
 * "6.3 Requirements on CB-AdES components and services"
 *
 */
public class CBAdESBaselineRequirementsChecker extends BaselineRequirementsChecker<CBAdESSignature> {

    private static final Logger LOG = LoggerFactory.getLogger(CBAdESBaselineRequirementsChecker.class);

    /**
     * Default constructor
     *
     * @param signature                  {@link CBAdESSignature} to validate
     * @param offlineCertificateVerifier {@link CertificateVerifier} offline copy of a used CertificateVerifier
     */
    public CBAdESBaselineRequirementsChecker(CBAdESSignature signature, CertificateVerifier offlineCertificateVerifier) {
        super(signature, offlineCertificateVerifier);
    }

    @Override
    public boolean hasAdESProfile() {
        CBORSignature cose = signature.getCoseSignature();

        COSEProtectedHeader signatureProtectedHeader = cose.getSignatureProtectedHeader();
        if (signatureProtectedHeader == null) {
            LOG.warn("Signature protected header shall be present for CB-AdES signature!");
            return false;
        }

        // 5.1.2 The alg (algorithm) header parameter
        if (signatureProtectedHeader.getAsLong(COSEHeaderParameter.ALG.cbor()) == null &&
                Utils.isStringEmpty(signatureProtectedHeader.getAsString(COSEHeaderParameter.ALG.cbor()))) {
            LOG.warn("'alg' header shall be present for CB-AdES signature!");
            return false;
        }

        // 5.1.3 The content type (content type) header parameter
        if (Utils.isStringNotEmpty(signatureProtectedHeader.getAsString(COSEHeaderParameter.CONTENT_TYPE.cbor()))) {
            if (signatureProtectedHeader.getAsMap(COSEHeaderParameter.SIG_D.cbor()) != null) {
                LOG.warn("'content type' header shall not be present for a detached CB-AdES with 'sigD' header parameter!");
                return false;
            }
            if (signature.isCounterSignature()) {
                LOG.warn("'content type' header shall not be present for a CB-AdES counter signature!");
                return false;
            }
        }
        // 5.1.10 The crit (critical) header parameter
        if (!critRequirements(signatureProtectedHeader, "CB-AdES")) {
            // validation errors returned inside
            return false;
        }
        // 5.2.2 The x5ts (X.509 certificates Thumbprints) header parameter
        int certHeaders = 0;
        if (signatureProtectedHeader.getAsArray(COSEHeaderParameter.X5T.cbor()) != null) ++certHeaders;
        if (signatureProtectedHeader.getAsArray(COSEHeaderParameter.X5TS.cbor()) != null) ++certHeaders;
        if (signatureProtectedHeader.getAsArray(COSEHeaderParameter.X5CHAIN.cbor()) != null) ++certHeaders;
        if (signatureProtectedHeader.getAsBinaries(COSEHeaderParameter.X5CHAIN.cbor()) != null) ++certHeaders;
        if (certHeaders == 0) {
            LOG.warn("At least one of 'x5t', 'x5ts' or 'x5chain' headers shall be present in the protected headers map for CB-AdES signature (cardinality == 1)!");
            return false;
        }
        return true;
    }

    @Override
    public boolean hasBaselineBProfile() {
        CBORSignature cose = signature.getCoseSignature();
        CBAdESUHeaders uHeaders = signature.getUHeaders();

        COSEProtectedHeader signatureProtectedHeader = cose.getSignatureProtectedHeader();
        if (signatureProtectedHeader == null) {
            LOG.warn("Signature protected header shall be present for CB-AdES-BASELINE-B signature!");
            return false;
        }

        // alg (Cardinality == 1)
        if (signatureProtectedHeader.getAsLong(COSEHeaderParameter.ALG.cbor()) == null &&
                Utils.isStringEmpty(signatureProtectedHeader.getAsString(COSEHeaderParameter.ALG.cbor()))) {
            LOG.warn("'alg' header shall be present for CB-AdES-BASELINE-B signature (cardinality == 1)!");
            return false;
        }
        // content type (Conditional presence)
        if (signature.isCounterSignature() && Utils.isStringNotEmpty(signatureProtectedHeader.getAsString(COSEHeaderParameter.CONTENT_TYPE.cbor()))) {
            LOG.warn("'content type' header shall not be present for a CB-AdES-BASELINE-B counter signature!");
            return false;
        }
        // verify 'crit' as of RFC 9052 and ETSI TS 119 152-1
        if (!critRequirements(signatureProtectedHeader, "CB-AdES-BASELINE-B")) {
            // validation errors returned inside
            return false;
        }
        // iat (Cardinality == 1)
        CBORMap cwtClaims = signatureProtectedHeader.getAsMap(COSEHeaderParameter.CWT_CLAIMS.cbor());
        if (cwtClaims == null || cwtClaims.getAsLong(CWTClaims.IAT.cbor()) == null) {
            LOG.warn("'CWT Claims enclosing the iat' header shall be present for CB-AdES-BASELINE-B signature (cardinality == 1)!");
            return false;
        }
        // x5t / x5ts / x5chain (Cardinality == 1)
        int certHeaders = 0;
        if (signatureProtectedHeader.getAsArray(COSEHeaderParameter.X5T.cbor()) != null) ++certHeaders;
        if (signatureProtectedHeader.getAsArray(COSEHeaderParameter.X5TS.cbor()) != null) ++certHeaders;
        if (signatureProtectedHeader.getAsArray(COSEHeaderParameter.X5CHAIN.cbor()) != null) ++certHeaders;
        if (signatureProtectedHeader.getAsBinaries(COSEHeaderParameter.X5CHAIN.cbor()) != null) ++certHeaders;
        if (certHeaders == 0) {
            LOG.warn("At least one of 'x5t', 'x5ts' or 'x5chain' headers shall be present in the protected headers map for CB-AdES-BASELINE-B signature (cardinality == 1)!");
            return false;
        }

        // sigPSt (Cardinality 0 or 1)
        if (uHeaders.getUnsignedPropertiesWithHeaderId(COSEHeaderParameter.SIG_PST.cbor()).size() > 1) {
            LOG.warn("Only one 'sigPSt' header shall be present for CB-AdES-BASELINE-B signature (cardinality 0 or 1)!");
            return false;
        }
        // Additional requirement (b)
        if ((signatureProtectedHeader.getAsMap(COSEHeaderParameter.SIG_PID.cbor()) == null ||
                !isSignaturePolicyIdentifierHashPresent()) && signature.getSignaturePolicyStore() != null) {
            LOG.warn("'sigPSt' header shall not be incorporated " +
                    "for CB-AdES-BASELINE-B signature with not defined 'sigPId/digVal' (requirement (b))!");
            return false;
        }
        return true;
    }

    private boolean critRequirements(COSEProtectedHeader protectedHeader, String profile) {
        // NOTE: RFC 9052 requirements are more lax than RFC 7515 for JWS
        List<CBORObject> critList = new ArrayList<>();

        // crit (conditional presence, required only for some elements)
        CBORArray crit = protectedHeader.getAsArray(COSEHeaderParameter.CRIT.cbor());
        if (crit != null) {
            // crit cannot be empty
            critList.addAll(crit.getValueAsList());
            if (crit.isEmpty()) {
                LOG.warn("'crit' header shall not be empty for a {} signature (see RFC 9052)!", profile);
                return false;
            }
        }

        Set<CBORObject> keySet = protectedHeader.getKeys();
        for (CBORObject key : keySet) {
            if (CBORUtils.isRequiredCriticalHeader(key)) {
                if (crit == null) {
                    LOG.warn("'crit' header shall be present when '{}' header is present in a signature for {} signature!", key, profile);
                    return false;
                } else if (!critList.contains(key)) {
                    LOG.warn("'crit' header shall contain '{}' header when present in a signature for {} signature!", key, profile);
                    return false;
                }
            }
        }
        for (CBORObject critEntry : critList) {
            // crit shall not contain not-used entries
            if (!keySet.contains(critEntry)) {
                LOG.warn("'crit' header can contain only entries used within a protected header " +
                        "for {} signature (see RFC 9052)! Found header : '{}'", profile, critEntry);
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean hasBaselineTProfile() {
        if (!minimalTRequirement()) {
            return false;
        }
        CBAdESUHeaders uHeaders = signature.getUHeaders();
        // Additional requirement (c)
        for (CBAdESUHeadersComponent uHeaderComponent : uHeaders.getUnsignedPropertiesWithHeaderId(COSEHeaderParameter.SIG_TST.cbor())) {
            CBORObject sigTst = uHeaderComponent.getValue();
            if (sigTst == null) {
                LOG.warn("'sigTst' shall be a type of CBOR Map for CB-AdES-BASELINE-T signature!");
                return false;
            }
            CBORMap tstContainer = (CBORMap) sigTst;
            CBORArray tstTokens = tstContainer.getAsArray(COSEHeaderParameter.TST_CONTAINER_TST_TOKENS.cbor());
            if (tstTokens.getSize() != 1) {
                LOG.warn("'sigTst' shall contain only one electronic timestamp for CB-AdES-BASELINE-T signature (requirement (c))!");
                return false;
            }
        }
        // Additional requirement (d)
        if (!signatureTimestampsCreatedBeforeSignCertExpiration()) {
            LOG.warn("sigTst shall be created before expiration of the signing-certificate " +
                    "for CB-AdES-BASELINE-T signature (requirement (d))!");
            return false;
        }
        return true;
    }

    @Override
    public boolean hasBaselineLTProfile() {
        if (!minimalLTRequirement()) {
            return false;
        }
        CBAdESUHeaders uHeaders = signature.getUHeaders();
        // refs (Cardinality == 0)
        if (!uHeaders.getUnsignedPropertiesWithHeaderId(COSEHeaderParameter.REFS.cbor()).isEmpty()) {
            LOG.warn("'refs' header shall not be present for CB-AdES-BASELINE-LT signature (cardinality == 0)!");
            return false;
        }
        // sigRTst (Cardinality == 0)
        if (!uHeaders.getUnsignedPropertiesWithHeaderId(COSEHeaderParameter.SIG_R_TST.cbor()).isEmpty()) {
            LOG.warn("'sigRTst' header shall not be present for CB-AdES-BASELINE-LT signature (cardinality == 0)!");
            return false;
        }
        // rfsTst (Cardinality == 0)
        if (!uHeaders.getUnsignedPropertiesWithHeaderId(COSEHeaderParameter.RFS_TST.cbor()).isEmpty()) {
            LOG.warn("'rfsTst' header shall not be present for CB-AdES-BASELINE-LT signature (cardinality == 0)!");
            return false;
        }
        return true;
    }

    @Override
    protected boolean containsLTLevelCertificates() {
        CBAdESUHeaders uHeaders = signature.getUHeaders();
        List<CBAdESUHeadersComponent> valDataEntries = uHeaders.getUnsignedPropertiesWithHeaderId(COSEHeaderParameter.VAL_DATA.cbor());
        for (CBAdESUHeadersComponent uHeadersComponent : valDataEntries) {
            CBORObject valData = uHeadersComponent.getValue();
            if (valData.isMap() && ((CBORMap) valData).getAsArray(COSEHeaderParameter.VAL_DATA_X_VALS.cbor()) != null) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasBaselineLTAProfile() {
        return minimalLTARequirement();
    }

}
