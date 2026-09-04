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
import eu.europa.esig.dss.model.lote.ServiceStatusAndInformationExtensions;
import eu.europa.esig.dss.model.lote.TrustedEntityService;
import eu.europa.esig.dss.model.lote.TrustedEntityServiceStatusAndInformationExtensions;
import eu.europa.esig.dss.model.timedependent.MutableTimeDependentValues;
import eu.europa.esig.dss.model.timedependent.TimeDependentValues;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This class converts a Json map object into a POJO {@code TrustedEntityService}
 *
 */
public class JsonTrustedEntityServiceConverter implements Function<Map<?, ?>, TrustedEntityService> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonTrustedEntityServiceConverter.class);

    /**
     * Default constructor
     */
    public JsonTrustedEntityServiceConverter() {
        // empty
    }

    @Override
    public TrustedEntityService apply(Map<?, ?> trustedEntityService) {
        TrustedEntityService.TrustEntityServiceBuilder serviceBuilder = new TrustedEntityService.TrustEntityServiceBuilder();
        Map<?, ?> serviceInformation = DSSJsonUtils.getAsMap(trustedEntityService, JsonLoTEHeaderParameterNames.SERVICE_INFORMATION);
        if (Utils.isMapNotEmpty(serviceInformation)) {
            serviceBuilder.setCertificates(extractCertificates(serviceInformation));
            serviceBuilder.setStatusAndInformationExtensions(extractStatusAndHistory(trustedEntityService));
        } else {
            LOG.warn("No mandatory TSPServiceInformation element found within TSPService element!");
        }
        return serviceBuilder.build();
    }

    private List<CertificateToken> extractCertificates(Map<?, ?> serviceInformation) {
        JsonServiceDigitalIdentityConverter converter = new JsonServiceDigitalIdentityConverter();
        Map<?, ?> serviceDigitalIdentity = DSSJsonUtils.getAsMap(serviceInformation, JsonLoTEHeaderParameterNames.SERVICE_DIGITAL_IDENTITY);
        return Collections.unmodifiableList(converter.apply(serviceDigitalIdentity));
    }

    private TimeDependentValues<ServiceStatusAndInformationExtensions> extractStatusAndHistory(Map<?, ?> trustedEntityService) {
        MutableTimeDependentValues<ServiceStatusAndInformationExtensions> statusHistoryList = new MutableTimeDependentValues<>();

        Map<?, ?> serviceInformation = DSSJsonUtils.getAsMap(trustedEntityService, JsonLoTEHeaderParameterNames.SERVICE_INFORMATION);
        TrustedEntityServiceStatusAndInformationExtensions statusAndInformationExtensions =
                buildTrustedEntityServiceStatusAndInformationExtensions(serviceInformation, null);
        statusHistoryList.addOldest(statusAndInformationExtensions);

        Date nextEndDate = statusAndInformationExtensions.getStartDate();

        List<?> serviceHistoryList = DSSJsonUtils.getAsList(trustedEntityService, JsonLoTEHeaderParameterNames.SERVICE_HISTORY);
        if (Utils.isCollectionNotEmpty(serviceHistoryList)) {
            for (Object serviceHistoryObject : serviceHistoryList) {
                Map<?, ?> serviceHistory = DSSJsonUtils.toMap(serviceHistoryObject);
                if (Utils.isMapEmpty(serviceHistory)) {
                    LOG.warn("Empty or invalid ServiceHistory entry has been found! The entry is skipped.");
                    continue;
                }

                String historyStatusStartingTime = DSSJsonUtils.getAsString(serviceHistory, JsonLoTEHeaderParameterNames.HISTORY_STATUS_STARTING_TIME);
                if (Utils.isStringEmpty(historyStatusStartingTime)) {
                    LOG.warn("No StatusStartingTime is found within a ServiceHistory item. The entry is skipped.");
                    continue;
                }

                TrustedEntityServiceStatusAndInformationExtensions historyStatusAndInformationExtensions =
                        buildTrustedEntityServiceStatusAndInformationExtensions(serviceInformation, nextEndDate);
                statusHistoryList.addOldest(historyStatusAndInformationExtensions);

                nextEndDate = historyStatusAndInformationExtensions.getEndDate();
            }
        }

        return statusHistoryList;
    }

    private TrustedEntityServiceStatusAndInformationExtensions buildTrustedEntityServiceStatusAndInformationExtensions(Map<?, ?> serviceInformation, Date endDate) {
        MultiLangStringListConverter converter = new MultiLangStringListConverter();

        TrustedEntityServiceStatusAndInformationExtensions.ServiceStatusAndInformationExtensionsBuilder statusBuilder =
                new TrustedEntityServiceStatusAndInformationExtensions.ServiceStatusAndInformationExtensionsBuilder();
        List<?> serviceName = DSSJsonUtils.getAsList(serviceInformation, JsonLoTEHeaderParameterNames.SERVICE_NAME);
        if (Utils.isCollectionNotEmpty(serviceName)) {
            statusBuilder.setNames(converter.apply(serviceName));
        }
        String serviceTypeIdentifier = DSSJsonUtils.getAsString(serviceInformation, JsonLoTEHeaderParameterNames.SERVICE_TYPE_IDENTIFIER);
        if (Utils.isStringNotEmpty(serviceTypeIdentifier)) {
            statusBuilder.setType(serviceTypeIdentifier);
        }
        String serviceStatus = DSSJsonUtils.getAsString(serviceInformation, JsonLoTEHeaderParameterNames.SERVICE_STATUS);
        if (Utils.isStringNotEmpty(serviceStatus)) {
            statusBuilder.setStatus(serviceStatus);
        }
        List<?> serviceSupplyPoints = DSSJsonUtils.getAsList(serviceInformation, JsonLoTEHeaderParameterNames.SERVICE_SUPPLY_POINTS);
        if (Utils.isCollectionNotEmpty(serviceSupplyPoints)) {
            statusBuilder.setServiceSupplyPoints(getServiceSupplyPoints(serviceSupplyPoints));
        }

        parseExtensionsList(DSSJsonUtils.getAsList(serviceInformation, JsonLoTEHeaderParameterNames.SERVICE_INFORMATION_EXTENSIONS), statusBuilder);

        String statusStartingTime = DSSJsonUtils.getAsString(serviceInformation, JsonLoTEHeaderParameterNames.STATUS_STARTING_TIME);
        if (Utils.isStringNotEmpty(statusStartingTime)) {
            statusBuilder.setStartDate(DSSUtils.parseRFCDate(statusStartingTime));
        }

        if (endDate != null) {
            statusBuilder.setEndDate(endDate);
        }

        return statusBuilder.build();
    }

    private void parseExtensionsList(List<?> serviceInformationExtensions, TrustedEntityServiceStatusAndInformationExtensions.ServiceStatusAndInformationExtensionsBuilder statusBuilder) {
        if (Utils.isCollectionNotEmpty(serviceInformationExtensions)) {
            // TODO : not yet supported
        }
    }

    private List<String> getServiceSupplyPoints(List<?> serviceSupplyPoints) {
        List<String> result = new ArrayList<>();
        if (Utils.isCollectionNotEmpty(serviceSupplyPoints)) {
            for (Object nonEmptyURI : serviceSupplyPoints) {
                Map<?, ?> serviceSupplyPointURI = DSSJsonUtils.toMap(nonEmptyURI);
                if (Utils.isMapNotEmpty(serviceSupplyPointURI)) {
                    String uriValue = DSSJsonUtils.getAsString(serviceSupplyPointURI, JsonLoTEHeaderParameterNames.URI_VALUE);
                    if (Utils.isStringNotEmpty(uriValue)) {
                        result.add(uriValue);
                    }
                }
            }
        }
        return result;
    }

}
