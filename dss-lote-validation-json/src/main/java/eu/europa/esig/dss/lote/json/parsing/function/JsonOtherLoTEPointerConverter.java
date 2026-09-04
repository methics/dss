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
package eu.europa.esig.dss.lote.json.parsing.function;

import eu.europa.esig.dss.jades.DSSJsonUtils;
import eu.europa.esig.dss.lote.json.parsing.JsonLoTEHeaderParameterNames;
import eu.europa.esig.dss.model.lote.OtherListPointer;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This class is used to convert a Json map object to a POJO {@code OtherListPointer}
 *
 */
public class JsonOtherLoTEPointerConverter implements Function<Map<?, ?>, OtherListPointer> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonOtherLoTEPointerConverter.class);

    /**
     * Default constructor with null territory country code
     */
    public JsonOtherLoTEPointerConverter() {
        // empty
    }

    @Override
    public OtherListPointer apply(Map<?, ?> original) {
        return new OtherListPointer.OtherListPointerBuilder()
                .setSdiCertificates(getCertificates(DSSJsonUtils.getAsList(original, JsonLoTEHeaderParameterNames.SERVICE_DIGITAL_IDENTITIES)))
                .setLocationUrl(DSSJsonUtils.getAsString(original, JsonLoTEHeaderParameterNames.LOTE_LOCATION))
                .setSchemeTerritory(getSchemeTerritory(original))
                .setTslType(getType(original))
                .setMimeType(getMimeType(original))
                .setSchemeOperatorNames(getSchemeOperatorNames(original))
                .setSchemeTypeCommunityRules(getSchemeTypeCommunityRules(original))
                .build();
    }

    private List<CertificateToken> getCertificates(List<?> serviceDigitalIdentity) {
        List<CertificateToken> certificates = new ArrayList<>();
        if (Utils.isCollectionNotEmpty(serviceDigitalIdentity)) {
            JsonServiceDigitalIdentityConverter converter = new JsonServiceDigitalIdentityConverter();
            for (Object sdiObject : serviceDigitalIdentity) {
                Map<?, ?> sdiMap = DSSJsonUtils.toMap(sdiObject);
                if (Utils.isMapNotEmpty(sdiMap)) {
                    certificates.addAll(converter.apply(sdiMap));
                }
            }
        }
        return certificates;
    }

    private String getSchemeTerritory(Map<?, ?> otherLoTEPointer) {
        Object schemeTerritoryQualifier = getLoteQualifierProperty(otherLoTEPointer, JsonLoTEHeaderParameterNames.SCHEME_TERRITORY);
        if (schemeTerritoryQualifier != null) {
            return DSSJsonUtils.toString(schemeTerritoryQualifier);
        }
        return null;
    }

    private String getType(Map<?, ?> otherLoTEPointer) {
        Object loteTypeQualifier = getLoteQualifierProperty(otherLoTEPointer, JsonLoTEHeaderParameterNames.LOTE_TYPE);
        if (loteTypeQualifier != null) {
            return DSSJsonUtils.toString(loteTypeQualifier);
        }
        return null;
    }

    private String getMimeType(Map<?, ?> otherLoTEPointer) {
        Object mimeTypeQualifier = getLoteQualifierProperty(otherLoTEPointer, JsonLoTEHeaderParameterNames.MIME_TYPE);
        if (mimeTypeQualifier != null) {
            return DSSJsonUtils.toString(mimeTypeQualifier);
        }
        return null;
    }

    private Map<String, List<String>> getSchemeOperatorNames(Map<?, ?> otherLoTEPointer) {
        Object schemeOperatorNameQualifier = getLoteQualifierProperty(otherLoTEPointer, JsonLoTEHeaderParameterNames.SCHEME_OPERATOR_NAME);
        if (schemeOperatorNameQualifier != null) {
            List<?> schemeOperatorNames = DSSJsonUtils.toList(schemeOperatorNameQualifier);
            if (Utils.isCollectionNotEmpty(schemeOperatorNames)) {
                MultiLangStringListConverter converter = new MultiLangStringListConverter();
                return converter.apply(schemeOperatorNames);
            }
        }
        return null;
    }

    private Map<String, List<String>> getSchemeTypeCommunityRules(Map<?, ?> otherLoTEPointer) {
        Object schemeTypeCommunityRulesQualifier = getLoteQualifierProperty(otherLoTEPointer, JsonLoTEHeaderParameterNames.SCHEME_TYPE_COMMUNITY_RULES);
        if (schemeTypeCommunityRulesQualifier != null) {
            List<?> schemeTypeCommunityRules = DSSJsonUtils.toList(schemeTypeCommunityRulesQualifier);
            if (Utils.isCollectionNotEmpty(schemeTypeCommunityRules)) {
                NonEmptyMultiLangURIListConverter converter = new NonEmptyMultiLangURIListConverter();
                return converter.apply(schemeTypeCommunityRules);
            }
        }
        return null;
    }

    private Object getLoteQualifierProperty(Map<?, ?> otherLoTEPointer, String loteQualifierHeaderName) {
        List<?> loteQualifiers = DSSJsonUtils.getAsList(otherLoTEPointer, JsonLoTEHeaderParameterNames.LOTE_QUALIFIERS);
        if (Utils.isCollectionEmpty(loteQualifiers)) {
            return null;
        }
        for (Object loteQualifierObject : loteQualifiers) {
            Map<?, ?> loteQualifierMap = DSSJsonUtils.toMap(loteQualifierObject);
            if (Utils.isCollectionNotEmpty(loteQualifiers)) {
                Object loteQualifier = loteQualifierMap.get(loteQualifierHeaderName);
                if (loteQualifier != null) {
                    return loteQualifier;
                }
            }
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("No LoTEQualifier found for name '{}'", loteQualifierHeaderName);
        }
        return null;
    }

}
