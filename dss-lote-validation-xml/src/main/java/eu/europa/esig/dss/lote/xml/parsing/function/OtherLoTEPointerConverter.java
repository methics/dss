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
package eu.europa.esig.dss.lote.xml.parsing.function;

import eu.europa.esig.dss.model.lote.OtherListPointer;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.lote.jaxb.AdditionalInformationType;
import eu.europa.esig.lote.jaxb.AnyType;
import eu.europa.esig.lote.jaxb.DigitalIdentityListType;
import eu.europa.esig.lote.jaxb.InternationalNamesType;
import eu.europa.esig.lote.jaxb.NonEmptyMultiLangURIListType;
import eu.europa.esig.lote.jaxb.OtherLoTEPointerType;
import eu.europa.esig.lote.jaxb.ServiceDigitalIdentityListType;
import jakarta.xml.bind.JAXBElement;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * Converts a JAXB {@code OtherLoTEPointerType} into a POJO {@code OtherListPointer}
 *
 */
public class OtherLoTEPointerConverter implements Function<OtherLoTEPointerType, OtherListPointer> {

    /** Additional information "SchemeTerritory" element */
    private static final String SCHEME_TERRITORY = "SchemeTerritory";

    /** Additional information "LoTEType" element */
    private static final String LOTE_TYPE = "LoTEType";

    /** Additional information "MimeType" element */
    private static final String MIME_TYPE = "MimeType";

    /** Additional information "SchemeOperatorName" element */
    private static final String SCHEME_OPERATOR_NAME = "SchemeOperatorName";

    /** Additional information "SchemeTypeCommunityRules" element */
    private static final String SCHEME_TYPE_COMMUNITY_RULES = "SchemeTypeCommunityRules";

    /**
     * Default constructor
     */
    public OtherLoTEPointerConverter() {
        // empty
    }

    @Override
    public OtherListPointer apply(OtherLoTEPointerType original) {
        return new OtherListPointer.OtherListPointerBuilder()
                .setSdiCertificates(getCertificates(original.getServiceDigitalIdentities()))
                .setLocationUrl(original.getLoTELocation())
                .setSchemeTerritory(getSchemeTerritory(original.getAdditionalInformation()))
                .setTslType(getType(original.getAdditionalInformation()))
                .setMimeType(getMimeType(original.getAdditionalInformation()))
                .setSchemeOperatorNames(getSchemeOperatorNames(original.getAdditionalInformation()))
                .setSchemeTypeCommunityRules(getSchemeTypeCommunityRules(original.getAdditionalInformation()))
                .build();
    }

    private List<CertificateToken> getCertificates(ServiceDigitalIdentityListType serviceDigitalIdentities) {
        List<CertificateToken> certificates = new ArrayList<>();
        if (serviceDigitalIdentities != null
                && Utils.isCollectionNotEmpty(serviceDigitalIdentities.getServiceDigitalIdentity())) {
            DigitalIdentityListTypeConverter converter = new DigitalIdentityListTypeConverter();
            for (DigitalIdentityListType digitalIdentityList : serviceDigitalIdentities.getServiceDigitalIdentity()) {
                certificates.addAll(converter.apply(digitalIdentityList));
            }
        }
        return certificates;
    }

    private String getSchemeTerritory(AdditionalInformationType additionalInformation) {
        return getOtherInformationValue(additionalInformation, String.class, SCHEME_TERRITORY);
    }

    private String getType(AdditionalInformationType additionalInformation) {
        return getOtherInformationValue(additionalInformation, String.class, LOTE_TYPE);
    }

    private String getMimeType(AdditionalInformationType additionalInformation) {
        return getOtherInformationValue(additionalInformation, String.class, MIME_TYPE);
    }

    private Map<String, List<String>> getSchemeOperatorNames(AdditionalInformationType additionalInformation) {
        InternationalNamesType schemeOperatorNames = getOtherInformationValue(
                additionalInformation, InternationalNamesType.class, SCHEME_OPERATOR_NAME);
        if (schemeOperatorNames != null) {
            return new InternationalNamesTypeConverter().apply(schemeOperatorNames);
        }
        return null;
    }

    private Map<String, List<String>> getSchemeTypeCommunityRules(AdditionalInformationType additionalInformation) {
        NonEmptyMultiLangURIListType schemeTypeCommunityRules = getOtherInformationValue(
                additionalInformation, NonEmptyMultiLangURIListType.class, SCHEME_TYPE_COMMUNITY_RULES);
        if (schemeTypeCommunityRules != null) {
            return new NonEmptyMultiLangURIListTypeConverter().apply(schemeTypeCommunityRules);
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private <T> T getOtherInformationValue(AdditionalInformationType additionalInformation, Class<T> targetClass, String elementName) {
        if (additionalInformation != null &&
                Utils.isCollectionNotEmpty(additionalInformation.getTextualInformationOrOtherInformation())) {
            for (Object obj : additionalInformation.getTextualInformationOrOtherInformation()) {
                if (obj instanceof AnyType) {
                    AnyType anytype = (AnyType) obj;
                    List<Object> content = anytype.getContent();
                    for (Object objectValue : content) {
                        if (objectValue instanceof JAXBElement) {
                            JAXBElement<?> jaxbElement = (JAXBElement<?>) objectValue;
                            Object value = jaxbElement.getValue();
                            if (jaxbElement.getName().getLocalPart().equals(elementName) && targetClass.isInstance(value)) {
                                return (T) value;
                            }
                        }
                    }
                }
            }
        }
        return null;
    }

}
