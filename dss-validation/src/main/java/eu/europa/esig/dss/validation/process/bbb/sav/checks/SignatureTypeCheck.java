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
package eu.europa.esig.dss.validation.process.bbb.sav.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlSAV;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.MultiValuesRule;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.validation.process.bbb.AbstractMultiValuesCheckItem;

import java.util.ArrayList;
import java.util.List;

/**
 * This class verifies whether the 'typ' (Type) protected header parameter has one of the expected values
 *
 */
public class SignatureTypeCheck extends AbstractMultiValuesCheckItem<XmlSAV> {

    /** RFC 7515 signature type prefix */
    private static final String SIGNATURE_TYPE_APPLICATION_PREFIX = "application/";

    /** The signature to check */
    private final SignatureWrapper signature;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlSAV}
     * @param signature {@link SignatureWrapper}
     * @param constraint {@link MultiValuesRule}
     */
    public SignatureTypeCheck(I18nProvider i18nProvider, XmlSAV result, SignatureWrapper signature,
                            MultiValuesRule constraint) {
        super(i18nProvider, result, constraint);
        this.signature = signature;
    }

    @Override
    protected boolean process() {
        return processValuesCheck(getSignatureType());
    }

    private List<String> getSignatureType() {
        final List<String> signatureTypes = new ArrayList<>();
        if (signature.getSignatureType() != null) {
            signatureTypes.add(signature.getSignatureType());
        }
        if (SignatureForm.JAdES == signature.getSignatureFormat().getSignatureForm() && signature.getSignatureType() != null) {
            signatureTypes.add(getRFC7515SignatureType(signature.getSignatureType()));
        }
        return signatureTypes;
    }

    private String getRFC7515SignatureType(String signatureType) {
        if (signatureType == null) {
            return null;
        }
        /*
         * RFC 7515 "4.1.9. "typ" (Type) Header Parameter":
         *
         * To keep messages compact in common situations, it is RECOMMENDED that
         * producers omit an "application/" prefix of a media type value in a
         * "typ" Header Parameter when no other '/' appears in the media type
         * value.  A recipient using the media type value MUST treat it as if
         * "application/" were prepended to any "typ" value not containing a '/'.
         */
        String shortMimeTypeString = DSSUtils.stripFirstLeadingOccurrence(signatureType, SIGNATURE_TYPE_APPLICATION_PREFIX);
        if (!shortMimeTypeString.contains("/")) {
            return shortMimeTypeString;
        } else {
            // return original if contains other '/'
            return signatureType;
        }
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.BBB_SAV_ISQPSTYPP;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.BBB_SAV_ISQPSTYPP_ANS;
    }

    @Override
    protected Indication getFailedIndicationForConclusion() {
        return Indication.INDETERMINATE;
    }

    @Override
    protected SubIndication getFailedSubIndicationForConclusion() {
        return SubIndication.SIG_CONSTRAINTS_FAILURE;
    }

}
