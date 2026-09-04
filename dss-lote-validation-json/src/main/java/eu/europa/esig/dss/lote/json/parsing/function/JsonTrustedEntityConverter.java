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
import eu.europa.esig.dss.lote.sync.TrustedEntityBuilder;
import eu.europa.esig.dss.model.lote.TrustedEntity;
import eu.europa.esig.dss.model.lote.TrustedEntityService;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is used to convert a Json map object to a POJO {@code TrustedEntity}
 *
 */
public class JsonTrustedEntityConverter implements Function<Map<?, ?>, TrustedEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonTrustedEntityConverter.class);

    /** The country code */
    private String territory;

    /**
     * Default constructor with null territory country code
     */
    public JsonTrustedEntityConverter() {
        // empty
    }

    /**
     * Sets territory
     *
     * @param territory {@link String}
     * @return {@link JsonTrustedEntityConverter}
     */
    public JsonTrustedEntityConverter territory(String territory) {
        this.territory = territory;
        return this;
    }

    @Override
    public TrustedEntity apply(Map<?, ?> original) {

        TrustedEntityBuilder builder = new TrustedEntityBuilder();

        extractTEInfo(builder, DSSJsonUtils.getAsMap(original, JsonLoTEHeaderParameterNames.TRUSTED_ENTITY_INFORMATION));
        builder.setServices(extractTrustServices(DSSJsonUtils.getAsList(original, JsonLoTEHeaderParameterNames.TRUSTED_ENTITY_SERVICES)));

        return builder.build();
    }

    private void extractTEInfo(TrustedEntityBuilder tspBuilder, Map<?, ?> trustedEntityInformation) {
        if (Utils.isMapNotEmpty(trustedEntityInformation)) {
            tspBuilder.setTerritory(territory);

            MultiLangStringListConverter converter = new MultiLangStringListConverter();
            tspBuilder.setNames(converter.apply(DSSJsonUtils.getAsList(trustedEntityInformation, JsonLoTEHeaderParameterNames.TE_NAME)));

            converter = new MultiLangStringListConverter();
            tspBuilder.setTradeNames(converter.apply(DSSJsonUtils.getAsList(trustedEntityInformation, JsonLoTEHeaderParameterNames.TE_TRADE_NAME)));

            // TODO : registration identifiers ?
            // tspBuilder.setRegistrationIdentifiers(extractRegistrationIdentifiers(DSSJsonUtils.getAsList(trustedEntityInformation, JsonLoTEHeaderParameterNames.TE_TRADE_NAME)));

            Map<?, ?> teAddress = DSSJsonUtils.getAsMap(trustedEntityInformation, JsonLoTEHeaderParameterNames.TE_ADDRESS);
            if (Utils.isMapNotEmpty(teAddress)) {
                tspBuilder.setPostalAddresses(extractPostalAddress(DSSJsonUtils.getAsList(teAddress, JsonLoTEHeaderParameterNames.TE_POSTAL_ADDRESS)));
                tspBuilder.setElectronicAddresses(extractElectronicAddress(DSSJsonUtils.getAsList(teAddress, JsonLoTEHeaderParameterNames.TE_ELECTRONIC_ADDRESS)));
            }

            tspBuilder.setInformation(extractInformationURI(DSSJsonUtils.getAsList(trustedEntityInformation, JsonLoTEHeaderParameterNames.TE_INFORMATION_URI)));

        } else {
            LOG.warn("No mandatory TSPInformation element found in the TrustedEntity element!");
        }
    }

    private Map<String, String> extractPostalAddress(List<?> postalAddressList) {
        Map<String, String> result = new HashMap<>();
        if (Utils.isCollectionNotEmpty(postalAddressList)) {
            for (Object postalAddressObject : postalAddressList) {
                Map<?, ?> postalAddress = DSSJsonUtils.toMap(postalAddressObject);
                String lang = DSSJsonUtils.getAsString(postalAddress, JsonLoTEHeaderParameterNames.LANG);
                // Collect 1st / lang
                result.computeIfAbsent(lang, k -> getPostalAddress(postalAddress));
            }
        }
        return result;
    }

    private String getPostalAddress(Map<?, ?> postalAddress) {
        StringBuilder sb = new StringBuilder();
        String streetAddress = DSSJsonUtils.getAsString(postalAddress, JsonLoTEHeaderParameterNames.STREET_ADDRESS);
        if (Utils.isStringNotEmpty(streetAddress)) {
            sb.append(streetAddress);
            sb.append(", ");
        }
        String postalCode = DSSJsonUtils.getAsString(postalAddress, JsonLoTEHeaderParameterNames.POSTAL_CODE);
        if (Utils.isStringNotEmpty(postalCode)) {
            sb.append(postalCode);
            sb.append(", ");
        }
        String locality = DSSJsonUtils.getAsString(postalAddress, JsonLoTEHeaderParameterNames.LOCALITY);
        if (Utils.isStringNotEmpty(locality)) {
            sb.append(locality);
            sb.append(", ");
        }
        String stateOrProvince = DSSJsonUtils.getAsString(postalAddress, JsonLoTEHeaderParameterNames.STATE_OR_PROVINCE);
        if (Utils.isStringNotEmpty(stateOrProvince)) {
            sb.append(stateOrProvince);
            sb.append(", ");
        }
        String country = DSSJsonUtils.getAsString(postalAddress, JsonLoTEHeaderParameterNames.COUNTRY);
        if (Utils.isStringNotEmpty(country)) {
            sb.append(country);
        }
        return sb.toString();
    }

    private Map<String, List<String>> extractElectronicAddress(List<?> electronicAddress) {
        NonEmptyMultiLangURIListConverter converter = new NonEmptyMultiLangURIListConverter();
        return converter.apply(electronicAddress);
    }

    private Map<String, List<String>> extractInformationURI(List<?> tspInformationURI) {
        NonEmptyMultiLangURIListConverter converter = new NonEmptyMultiLangURIListConverter();
        return converter.apply(tspInformationURI);
    }

    private List<TrustedEntityService> extractTrustServices(List<?> servicesList) {
        if (Utils.isCollectionNotEmpty(servicesList)) {
            return servicesList.stream().map(DSSJsonUtils::toMap).filter(Utils::isMapNotEmpty)
                    .map(new JsonTrustedEntityServiceConverter()).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
