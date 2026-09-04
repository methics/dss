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

import eu.europa.esig.dss.lote.sync.TrustedEntityBuilder;
import eu.europa.esig.dss.model.lote.TrustedEntity;
import eu.europa.esig.dss.model.lote.TrustedEntityService;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.lote.jaxb.AddressType;
import eu.europa.esig.lote.jaxb.ElectronicAddressType;
import eu.europa.esig.lote.jaxb.NonEmptyMultiLangURIListType;
import eu.europa.esig.lote.jaxb.NonEmptyMultiLangURIType;
import eu.europa.esig.lote.jaxb.PostalAddressListType;
import eu.europa.esig.lote.jaxb.PostalAddressType;
import eu.europa.esig.lote.jaxb.TEType;
import eu.europa.esig.lote.jaxb.TrustedEntityInformationType;
import eu.europa.esig.lote.jaxb.TrustedEntityServicesListType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * This class is used to convert a JAXB {@code TEType} object to a POJO {@code TrustedEntity}
 *
 */
public class TrustedEntityConverter implements Function<TEType, TrustedEntity> {

    private static final Logger LOG = LoggerFactory.getLogger(TrustedEntityConverter.class);

    /** The country code */
    private String territory;

    /**
     * Default constructor with null territory country code
     */
    public TrustedEntityConverter() {
        // empty
    }

    /**
     * Sets territory
     *
     * @param territory {@link String}
     * @return {@link TrustedEntityConverter}
     */
    public TrustedEntityConverter territory(String territory) {
        this.territory = territory;
        return this;
    }

    @Override
    public TrustedEntity apply(TEType original) {
        TrustedEntityBuilder builder = new TrustedEntityBuilder();

        extractTEInfo(builder, original.getTrustedEntityInformation());
        builder.setServices(extractTrustServices(original.getTrustedEntityServices()));

        return builder.build();
    }

    private void extractTEInfo(TrustedEntityBuilder tspBuilder, TrustedEntityInformationType informationType) {
        if (informationType != null) {
            tspBuilder.setTerritory(territory);

            InternationalNamesTypeConverter converter = new InternationalNamesTypeConverter();
            tspBuilder.setNames(converter.apply(informationType.getTEName()));

            converter = new InternationalNamesTypeConverter(); // TODO : filter by trade name ?
            tspBuilder.setTradeNames(converter.apply(informationType.getTETradeName()));

            // TODO : registration identifiers ?
            // tspBuilder.setRegistrationIdentifiers(extractRegistrationIdentifiers(informationType.getTETradeName()));

            AddressType address = informationType.getTEAddress();
            if (address != null) {
                tspBuilder.setPostalAddresses(extractPostalAddress(address.getPostalAddresses()));
                tspBuilder.setElectronicAddresses(extractElectronicAddress(address.getElectronicAddress()));
            }

            tspBuilder.setInformation(extractInformationURI(informationType.getTEInformationURI()));

        } else {
            LOG.warn("No mandatory TSPInformation element found in the TrustedEntity element!");
        }
    }

    private Map<String, String> extractPostalAddress(PostalAddressListType postalAddressList) {
        Map<String, String> result = new HashMap<>();
        if (postalAddressList != null && Utils.isCollectionNotEmpty(postalAddressList.getPostalAddress())) {
            for (PostalAddressType postalAddress : postalAddressList.getPostalAddress()) {
                String lang = postalAddress.getLang();
                // Collect 1st / lang
                result.computeIfAbsent(lang, k -> getPostalAddress(postalAddress));
            }
        }
        return result;
    }

    private String getPostalAddress(PostalAddressType postalAddress) {
        StringBuilder sb = new StringBuilder();
        if (Utils.isStringNotEmpty(postalAddress.getStreetAddress())) {
            sb.append(postalAddress.getStreetAddress());
            sb.append(", ");
        }
        if (Utils.isStringNotEmpty(postalAddress.getPostalCode())) {
            sb.append(postalAddress.getPostalCode());
            sb.append(", ");
        }
        if (Utils.isStringNotEmpty(postalAddress.getLocality())) {
            sb.append(postalAddress.getLocality());
            sb.append(", ");
        }
        if (Utils.isStringNotEmpty(postalAddress.getStateOrProvince())) {
            sb.append(postalAddress.getStateOrProvince());
            sb.append(", ");
        }
        if (Utils.isStringNotEmpty(postalAddress.getCountryName())) {
            sb.append(postalAddress.getCountryName());
        }
        return sb.toString();
    }

    private Map<String, List<String>> extractElectronicAddress(ElectronicAddressType electronicAddress) {
        Map<String, List<String>> result = new HashMap<>();
        if (electronicAddress != null && Utils.isCollectionNotEmpty(electronicAddress.getURI())) {
            for (NonEmptyMultiLangURIType uriAndLang : electronicAddress.getURI()) {
                addEntry(result, uriAndLang.getLang(), uriAndLang.getValue());
            }
        }
        return result;
    }

    private Map<String, List<String>> extractInformationURI(NonEmptyMultiLangURIListType teInformationURI) {
        Map<String, List<String>> result = new HashMap<>();
        if (teInformationURI != null && Utils.isCollectionNotEmpty(teInformationURI.getURI())) {
            for (NonEmptyMultiLangURIType uriAndLang : teInformationURI.getURI()) {
                addEntry(result, uriAndLang.getLang(), uriAndLang.getValue());
            }
        }
        return result;
    }

    private void addEntry(Map<String, List<String>> result, final String lang, final String value) {
        List<String> resultsByLang = result.computeIfAbsent(lang, k -> new ArrayList<>());
        resultsByLang.add(value);
    }

    private List<TrustedEntityService> extractTrustServices(TrustedEntityServicesListType servicesList) {
        if (servicesList != null && Utils.isCollectionNotEmpty(servicesList.getTrustedEntityService())) {
            return servicesList.getTrustedEntityService().stream().map(new TrustedEntityServiceConverter()).collect(Collectors.toList());
        } else {
            return Collections.emptyList();
        }
    }

}
