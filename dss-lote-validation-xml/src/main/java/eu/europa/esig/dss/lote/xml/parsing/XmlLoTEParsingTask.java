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
package eu.europa.esig.dss.lote.xml.parsing;

import eu.europa.esig.dss.lote.parsing.LoTEParsingResult;
import eu.europa.esig.dss.lote.parsing.predicate.NonEmptyTENamePredicate;
import eu.europa.esig.dss.lote.parsing.predicate.NonEmptyTESInformationPredicate;
import eu.europa.esig.dss.lote.parsing.predicate.NonEmptyTrustedEntityServicePredicate;
import eu.europa.esig.dss.lote.source.LoTESource;
import eu.europa.esig.dss.lote.xml.parsing.function.TrustedEntityConverter;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.lote.TrustedEntity;
import eu.europa.esig.dss.model.lote.TrustedEntityService;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.lote.jaxb.ListOfTrustedEntitiesType;
import eu.europa.esig.lote.jaxb.LoTEListAndSchemeInformationType;
import eu.europa.esig.lote.jaxb.TrustedEntitiesListType;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class performs reading and information extraction from an obtained XML LoTE
 *
 */
public class XmlLoTEParsingTask extends AbstractXmlLoTEParsingTask {

    /** The List Source to parse */
    private final LoTESource loteSource;

    /**
     * The default constructor
     *
     * @param document {@link DSSDocument} List document to parse
     * @param loteSource {@link LoTESource}
     */
    public XmlLoTEParsingTask(DSSDocument document, LoTESource loteSource) {
        super(document);
        Objects.requireNonNull(loteSource, "The LoTESource is null");
        this.loteSource = loteSource;
    }

    @Override
    public LoTEParsingResult get() {
        LoTEParsingResult result = new LoTEParsingResult();
        ListOfTrustedEntitiesType jaxbObject = getJAXBObject();

        parseSchemeInformation(result, jaxbObject.getListAndSchemeInformation());
        parseTrustedEntitiesList(result, jaxbObject.getTrustedEntitiesList());
        verifyStructure(result);

        return result;
    }

    private void parseSchemeInformation(LoTEParsingResult result, LoTEListAndSchemeInformationType schemeInformation) {
        commonParseSchemeInformation(result, schemeInformation);
    }

    private void parseTrustedEntitiesList(LoTEParsingResult result, TrustedEntitiesListType trustedEntitiesListType) {
        if (trustedEntitiesListType != null && Utils.isCollectionNotEmpty(trustedEntitiesListType.getTrustedEntity())) {
            List<TrustedEntity> trustedEntities = trustedEntitiesListType.getTrustedEntity().stream()
                    .map(new TrustedEntityConverter().territory(result.getTerritory())).collect(Collectors.toList());
            List<TrustedEntity> filteredTrustedEntities = filter(trustedEntities);
            result.setTrustedEntities(Collections.unmodifiableList(filteredTrustedEntities));
        } else {
            result.setTrustedEntities(Collections.emptyList());
        }
    }

    private List<TrustedEntity> filter(List<TrustedEntity> trustedEntities) {
        List<TrustedEntity> filteredEntities = trustedEntities;

        // 1. Remove TSPs with invalid structure
        filteredEntities = filteredEntities.stream().filter(new NonEmptyTENamePredicate()).collect(Collectors.toList());

        // 2. Filter the TSP with the predicate
        if (loteSource.getTrustedEntityPredicate() != null) {
            filteredEntities = filteredEntities.stream().filter(loteSource.getTrustedEntityPredicate()).collect(Collectors.toList());
        }

        // 3. Foreach TSP, remove invalid trust services
        for (TrustedEntity trustedEntity : filteredEntities) {
            List<TrustedEntityService> services = trustedEntity.getServices();
            if (Utils.isCollectionNotEmpty(services)) {
                List<TrustedEntityService> filteredServices = services;
                filteredServices = filteredServices.stream()
                        .filter(new NonEmptyTESInformationPredicate()).collect(Collectors.toList());

                // 4. Filter the trust services with the predicate
                if (loteSource.getTrustedServicePredicate() != null) {
                    filteredServices = filteredServices.stream()
                            .filter(loteSource.getTrustedServicePredicate()).collect(Collectors.toList());
                }

                if (!filteredServices.isEmpty()) {
                    trustedEntity.setServices(Collections.unmodifiableList(filteredServices));
                }
            }
        }

        // 5. Remove TSPs with empty trust services
        return filteredEntities.stream().filter(new NonEmptyTrustedEntityServicePredicate()).collect(Collectors.toList());
    }

}
