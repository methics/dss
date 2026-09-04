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
package eu.europa.esig.dss.lote.sync;

import eu.europa.esig.dss.model.lote.ServiceStatusAndInformationExtensions;
import eu.europa.esig.dss.model.lote.TrustedEntity;
import eu.europa.esig.dss.model.lote.TrustedEntityService;
import eu.europa.esig.dss.model.lote.TrustedEntityServiceStatusAndInformationExtensions;
import eu.europa.esig.dss.model.timedependent.TimeDependentValues;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Builds a {@code eu.europa.esig.dss.model.lote.TrustedEntity}
 *
 */
public class TrustedEntityBuilder {

    /** Map of names (the key is the language) */
    private Map<String, List<String>> names;

    /** Map of trade names */
    private Map<String, List<String>> tradeNames;

    /** List of registration identifiers */
    private List<String> registrationIdentifiers;

    /** Map of postal addresses */
    private Map<String, String> postalAddresses;

    /** Map of electronic addresses */
    private Map<String, List<String>> electronicAddresses;

    /** Map of information */
    private Map<String, List<String>> information;

    /** List of trust services */
    private List<TrustedEntityService> services;

    /** The territory (country) */
    private String territory;

    /**
     * Default constructor
     */
    public TrustedEntityBuilder() {
        // empty
    }

    /**
     * Copy the original object
     *
     * @param original the original trust service provider
     */
    public TrustedEntityBuilder(TrustedEntity original) {
        Objects.requireNonNull(original, "TrustedEntity cannot be null!");
        this.names = original.getNames();
        this.tradeNames = original.getTradeNames();
        this.registrationIdentifiers = original.getRegistrationIdentifiers();
        this.postalAddresses = original.getPostalAddresses();
        this.electronicAddresses = original.getElectronicAddresses();
        this.information = original.getInformation();
        this.services = original.getServices();
        this.territory = original.getTerritory();
    }

    /**
     * Builds {@code TrustedEntity}
     *
     * @return {@link TrustedEntity}
     */
    public TrustedEntity build() {
        final TrustedEntity trustedEntity = new TrustedEntity();
        trustedEntity.setNames(getNames());
        trustedEntity.setTradeNames(getTradeNames());
        trustedEntity.setRegistrationIdentifiers(getRegistrationIdentifiers());
        trustedEntity.setPostalAddresses(getPostalAddresses());
        trustedEntity.setElectronicAddresses(getElectronicAddresses());
        trustedEntity.setInformation(getInformation());
        trustedEntity.setServices(getServices());
        trustedEntity.setTerritory(getTerritory());
        return trustedEntity;
    }

    /**
     * Gets a map of names (first key is the language)
     *
     * @return a map of names
     */
    public Map<String, List<String>> getNames() {
        return getUnmodifiableMapWithLists(names);
    }

    /**
     * Sets a map of names
     *
     * @param names a map of names (first key is the language)
     * @return this {@link TrustedEntityBuilder}
     */
    public TrustedEntityBuilder setNames(Map<String, List<String>> names) {
        this.names = names;
        return this;
    }

    /**
     * Gets a map of trade names
     *
     * @return a map of trade names
     */
    public Map<String, List<String>> getTradeNames() {
        return getUnmodifiableMapWithLists(tradeNames);
    }

    /**
     * Sets a map of trade names
     *
     * @param tradeNames a map of trade names (first key is the language)
     * @return this {@link TrustedEntityBuilder}
     */
    public TrustedEntityBuilder setTradeNames(Map<String, List<String>> tradeNames) {
        this.tradeNames = tradeNames;
        return this;
    }

    /**
     * Gets registration identifiers
     *
     * @return a list of {@link String}s
     */
    public List<String> getRegistrationIdentifiers() {
        return getUnmodifiableList(registrationIdentifiers);
    }

    /**
     * Sets registration identifiers
     *
     * @param registrationIdentifiers a list of {@link String}s
     * @return this {@link TrustedEntityBuilder}
     */
    public TrustedEntityBuilder setRegistrationIdentifiers(List<String> registrationIdentifiers) {
        this.registrationIdentifiers = registrationIdentifiers;
        return this;
    }

    /**
     * Gets a map of postal addresses
     *
     * @return a map of postal addresses
     */
    public Map<String, String> getPostalAddresses() {
        return getUnmodifiableMap(postalAddresses);
    }

    /**
     * Sets a map of postal addresses
     *
     * @param postalAddresses a map of postal addresses
     * @return this {@link TrustedEntityBuilder}
     */
    public TrustedEntityBuilder setPostalAddresses(Map<String, String> postalAddresses) {
        this.postalAddresses = postalAddresses;
        return this;
    }

    /**
     * Gets a map of electronic addresses
     *
     * @return a map of electronic addresses
     */
    public Map<String, List<String>> getElectronicAddresses() {
        return getUnmodifiableMapWithLists(electronicAddresses);
    }

    /**
     * Sets a map of electronic addresses
     *
     * @param electronicAddresses a map of electronic addresses
     * @return this {@link TrustedEntityBuilder}
     */
    public TrustedEntityBuilder setElectronicAddresses(Map<String, List<String>> electronicAddresses) {
        this.electronicAddresses = electronicAddresses;
        return this;
    }

    /**
     * Gets a map of information
     *
     * @return a map of information
     */
    public Map<String, List<String>> getInformation() {
        return getUnmodifiableMap(information);
    }

    /**
     * Sets a map of information
     *
     * @param information a map of information
     * @return this {@link TrustedEntityBuilder}
     */
    public TrustedEntityBuilder setInformation(Map<String, List<String>> information) {
        this.information = information;
        return this;
    }

    /**
     * Gets a list of trust services
     *
     * @return a list of {@link TrustedEntityService}s
     */
    public List<TrustedEntityService> getServices() {
        return getUnmodifiableTrustServices(services);
    }

    /**
     * Sets a list of trust services
     *
     * @param services a list of {@link TrustedEntityService}s
     * @return this {@link TrustedEntityBuilder}
     */
    public TrustedEntityBuilder setServices(List<TrustedEntityService> services) {
        this.services = services;
        return this;
    }

    /**
     * Gets territory (country)
     *
     * @return {@link String}
     */
    public String getTerritory() {
        return territory;
    }

    /**
     * Sets territory (country)
     *
     * @param territory {@link String}
     * @return this {@link TrustedEntityBuilder}
     */
    public TrustedEntityBuilder setTerritory(String territory) {
        this.territory = territory;
        return this;
    }

    private <T extends Object> List<T> getUnmodifiableList(List<T> originalList) {
        List<T> newList = new ArrayList<>();
        if (originalList != null && !originalList.isEmpty()) {
            newList.addAll(originalList);
        }
        return Collections.unmodifiableList(newList);
    }

    private <T extends Object, K extends Object> Map<T, K> getUnmodifiableMap(Map<T, K> originalMap) {
        Map<T, K> newMap = new HashMap<>();
        if (originalMap != null && !originalMap.isEmpty()) {
            newMap.putAll(originalMap);
        }
        return Collections.unmodifiableMap(newMap);
    }

    private Map<String, List<String>> getUnmodifiableMapWithLists(Map<String, List<String>> originalMap) {
        Map<String, List<String>> copyMap = new HashMap<>();
        if (originalMap != null && !originalMap.isEmpty()) {
            for (Map.Entry<String, List<String>> mapEntry : originalMap.entrySet()) {
                copyMap.put(mapEntry.getKey(), Collections.unmodifiableList(mapEntry.getValue()));
            }
        }
        return Collections.unmodifiableMap(copyMap);
    }

    private List<TrustedEntityService> getUnmodifiableTrustServices(List<TrustedEntityService> originalTrustServices) {
        List<TrustedEntityService> copyTrustServices = new ArrayList<>();
        if (originalTrustServices != null && !originalTrustServices.isEmpty()) {
            for (TrustedEntityService trustService : originalTrustServices) {
                TrustedEntityService.TrustEntityServiceBuilder trustServiceBuilder = new TrustedEntityService.TrustEntityServiceBuilder();
                TrustedEntityService copyTrustService = trustServiceBuilder.setCertificates(getUnmodifiableList(trustService.getCertificates()))
                        .setStatusAndInformationExtensions(getUnmodifiableTimeDependentValues(trustService.getStatusAndInformationExtensions()))
                        .build();
                copyTrustServices.add(copyTrustService);
            }
        }
        return Collections.unmodifiableList(copyTrustServices);
    }

    private TimeDependentValues<ServiceStatusAndInformationExtensions> getUnmodifiableTimeDependentValues(
            TimeDependentValues<ServiceStatusAndInformationExtensions> timeDependentValues) {
        List<ServiceStatusAndInformationExtensions> copyTSSAndIEs = new ArrayList<>();

        for (ServiceStatusAndInformationExtensions status : timeDependentValues) {
            TrustedEntityServiceStatusAndInformationExtensions.ServiceStatusAndInformationExtensionsBuilder builder =
                    new TrustedEntityServiceStatusAndInformationExtensions.ServiceStatusAndInformationExtensionsBuilder();
            TrustedEntityServiceStatusAndInformationExtensions copyStatus = builder.setNames(getUnmodifiableMapWithLists(status.getNames()))
                    .setType(status.getType())
                    .setStatus(status.getStatus())
                    .setServiceSupplyPoints(getUnmodifiableList(status.getServiceSupplyPoints()))
                    .setStartDate(status.getStartDate())
                    .setEndDate(status.getEndDate())
                    .build();
            copyTSSAndIEs.add(copyStatus);
        }

        return new TimeDependentValues<>(copyTSSAndIEs);
    }

}
