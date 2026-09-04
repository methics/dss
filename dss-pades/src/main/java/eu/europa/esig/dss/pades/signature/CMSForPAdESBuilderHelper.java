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

import eu.europa.esig.dss.cades.signature.CAdESLevelBaselineB;
import eu.europa.esig.dss.cades.signature.CMSForCAdESBuilderHelper;
import eu.europa.esig.dss.cms.CMS;
import eu.europa.esig.dss.cms.operator.CustomContentSigner;
import eu.europa.esig.dss.model.DSSMessageDigest;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import org.bouncycastle.operator.ContentSigner;

/**
 * Creates a new CMS for a PAdES signature creation
 *
 */
public class CMSForPAdESBuilderHelper extends CMSForCAdESBuilderHelper {

    /** Message digest computed on the PAdES revision to be signed */
    private final DSSMessageDigest messageDigest;

    /**
     * Default constructor
     *
     * @param messageDigest       {@link DSSMessageDigest}
     * @param signatureParameters {@link PAdESSignatureParameters}
     * @param contentSigner       {@link CustomContentSigner}
     */
    public CMSForPAdESBuilderHelper(DSSMessageDigest messageDigest, PAdESSignatureParameters signatureParameters, ContentSigner contentSigner) {
        super(DSSUtils.toDigestDocument(messageDigest), signatureParameters, contentSigner);
        this.messageDigest = messageDigest;
    }

    @Override
    public CMSForPAdESBuilderHelper setOriginalCMS(CMS originalCMS) {
        return (CMSForPAdESBuilderHelper) super.setOriginalCMS(originalCMS);
    }

    @Override
    public CMSForPAdESBuilderHelper setTrustedCertificateSource(CertificateSource trustedCertificateSource) {
        return (CMSForPAdESBuilderHelper) super.setTrustedCertificateSource(trustedCertificateSource);
    }

    @Override
    public CMSForPAdESBuilderHelper setIncludeUnsignedAttributes(boolean includeUnsignedAttributes) {
        return (CMSForPAdESBuilderHelper) super.setIncludeUnsignedAttributes(includeUnsignedAttributes);
    }

    @Override
    protected CAdESLevelBaselineB initCAdESProfile() {
        return new PAdESLevelBaselineB(messageDigest);
    }

    @Override
    protected boolean isEncapsulateSignerData() {
        return false;
    }

}
