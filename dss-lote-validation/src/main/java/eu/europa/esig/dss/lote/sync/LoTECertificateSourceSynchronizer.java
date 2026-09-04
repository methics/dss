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

import eu.europa.esig.dss.lote.job.LoTEReadOnlyCacheAccess;
import eu.europa.esig.dss.lote.source.LoLoTESource;
import eu.europa.esig.dss.lote.source.LoTESource;
import eu.europa.esig.dss.lote.summary.LoTEValidationJobSummaryBuilder;
import eu.europa.esig.dss.model.job.ParsingInfoRecord;
import eu.europa.esig.dss.model.lote.LoLoTEInfo;
import eu.europa.esig.dss.model.lote.LoTEInfo;
import eu.europa.esig.dss.model.lote.LoTEValidationJobSummary;
import eu.europa.esig.dss.model.lote.ServiceStatusAndInformationExtensions;
import eu.europa.esig.dss.model.lote.TrustedEntity;
import eu.europa.esig.dss.model.lote.TrustedEntityService;
import eu.europa.esig.dss.model.lote.TrustedProperties;
import eu.europa.esig.dss.model.lote.record.LoTEParsingInfoRecord;
import eu.europa.esig.dss.model.timedependent.TimeDependentValues;
import eu.europa.esig.dss.model.tsl.CertificateTrustTime;
import eu.europa.esig.dss.model.tsl.TrustPropertiesCertificateSource;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.lote.TrustedEntitiesCertificateSource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.job.cache.CacheKey;
import eu.europa.esig.dss.validation.job.cache.access.SynchronizerCacheAccess;
import eu.europa.esig.dss.validation.job.sync.SynchronizationStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.function.Predicate;

/**
 * Loads trusted certificate source
 */
public class LoTECertificateSourceSynchronizer {

	private static final Logger LOG = LoggerFactory.getLogger(LoTECertificateSourceSynchronizer.class);

	/**
	 * List of LoTESource's to extract summary for
	 */
	private final LoTESource[] loteSources;

	/**
	 * List of LoLoTESource's to extract summary for
	 */
	private final LoLoTESource[] loloteSources;

	/**
	 * The strategy to follow for the certificate synchronization
	 */
	private final SynchronizationStrategy<LoTEInfo, LoLoTEInfo> synchronizationStrategy;

	/**
	 * The certificate source to be synchronized
	 */
	private final TrustedEntitiesCertificateSource certificateSource;

	/**
	 * The cache access
	 */
	private final SynchronizerCacheAccess syncCacheAccess;

	/**
	 * The cache access
	 */
	private final LoTEReadOnlyCacheAccess readOnlyCacheAccess;

	/**
	 * Default constructor
	 *
	 * @param loteSources {@link LoTESource}s
	 * @param loloteSources {@link LoLoTESource}s
	 * @param certificateSource {@link TrustPropertiesCertificateSource}
	 * @param synchronizationStrategy {@link SynchronizationStrategy}
	 * @param syncCacheAccess {@link SynchronizerCacheAccess}
	 * @param readOnlyCacheAccess {@link LoTEReadOnlyCacheAccess}
	 */
	public LoTECertificateSourceSynchronizer(LoTESource[] loteSources, LoLoTESource[] loloteSources, TrustedEntitiesCertificateSource certificateSource,
											 SynchronizationStrategy<LoTEInfo, LoLoTEInfo> synchronizationStrategy,
	                                         SynchronizerCacheAccess syncCacheAccess, LoTEReadOnlyCacheAccess readOnlyCacheAccess) {
		this.loteSources = loteSources;
		this.loloteSources = loloteSources;
		this.synchronizationStrategy = synchronizationStrategy;
		this.certificateSource = certificateSource;
		this.syncCacheAccess = syncCacheAccess;
		this.readOnlyCacheAccess =readOnlyCacheAccess;
	}

	/**
	 * Synchronizes the trusted certificate source based on the validation job processing result
	 */
	public void sync() {
		try {
			LoTEValidationJobSummaryBuilder summaryBuilder = new LoTEValidationJobSummaryBuilder(readOnlyCacheAccess, loteSources, loloteSources);

			LoTEValidationJobSummary summary = summaryBuilder.build();
			if (isCertificateSyncNeeded(summary)) {
				synchronizeCertificates(summary);
			}
			syncCache(summary);

			// re-build summary after synchronization
			summary = summaryBuilder.build();
			certificateSource.setSummary(summary);

		} catch (Exception e) {
			LOG.error("Unable to synchronize the TrustedEntitiesCertificateSource", e);
		}
	}

	private boolean isCertificateSyncNeeded(LoTEValidationJobSummary summary) {
		for (LoLoTEInfo loloteInfo : summary.getLoLoTEInfos()) {
			if (isLoTEParsingDesyncOrError(loloteInfo) || isLoTEParsingDesyncOrError(loloteInfo.getChildrenInfos())) {
				return true;
			}
		}
		return isLoTEParsingDesyncOrError(summary.getOtherDocumentInfos());
	}

	private boolean isLoTEParsingDesyncOrError(List<LoTEInfo> tlInfos) {
		return tlInfos.stream().anyMatch(this::isLoTEParsingDesyncOrError);
	}

	private boolean isLoTEParsingDesyncOrError(LoTEInfo tlInfo) {
		ParsingInfoRecord parsingCacheInfo = tlInfo.getParsingCacheInfo();
		return parsingCacheInfo == null || parsingCacheInfo.isDesynchronized() || parsingCacheInfo.isError();
	}


	private void synchronizeCertificates(LoTEValidationJobSummary summary) {
		final Map<CertificateToken, List<TrustedProperties>> trustPropertiesByCerts = new WeakHashMap<>();
		final Map<CertificateToken, List<CertificateTrustTime>> trustTimeByCerts = new WeakHashMap<>();
		for (LoLoTEInfo loloteInfo : summary.getLoLoTEInfos()) {
			if (synchronizationStrategy.canBeSynchronized(loloteInfo)) {
				addCertificatesFromLoTEs(trustPropertiesByCerts, trustTimeByCerts, loloteInfo.getChildrenInfos(), loloteInfo);
			} else {
				LOG.warn("Certificate synchronization is skipped for LOTL '{}' and its TLs", loloteInfo.getUrl());
			}
		}
		addCertificatesFromLoTEs(trustPropertiesByCerts, trustTimeByCerts, summary.getOtherLoTEInfos(), null);
		certificateSource.setTrustedPropertiesByCertificates(trustPropertiesByCerts);
		certificateSource.setTrustedTimeByCertificates(trustTimeByCerts);
	}

	private void addCertificatesFromLoTEs(final Map<CertificateToken, List<TrustedProperties>> trustPropertiesByCerts,
	                                      final Map<CertificateToken, List<CertificateTrustTime>> trustTimeByCerts,
	                                      final List<LoTEInfo> loteInfos, LoLoTEInfo relatedLoLoTE) {

		for (final LoTEInfo loteInfo : loteInfos) {
			if (synchronizationStrategy.canBeSynchronized(loteInfo)) {
				LoTEParsingInfoRecord parsingCacheInfo = loteInfo.getParsingCacheInfo();
				if (parsingCacheInfo == null || !parsingCacheInfo.isResultExist()) {
					LOG.warn("No Parsing result for TLInfo with url [{}]", loteInfo.getUrl());
				} else {
					final List<TrustedEntity> trustedEntities = parsingCacheInfo.getTrustedEntities();
					if (Utils.isCollectionNotEmpty(trustedEntities)) {
						final Predicate<ServiceStatusAndInformationExtensions> trustAnchorValidityPredicate =
								getTrustAnchorValidityPredicate(loteInfo, relatedLoLoTE);
						for (TrustedEntity original : trustedEntities) {
							TrustedEntity detached = getDetached(original);
							for (TrustedEntityService trustedEntityService : original.getServices()) {
								TimeDependentValues<ServiceStatusAndInformationExtensions> statusAndInformationExtensions =
										trustedEntityService.getStatusAndInformationExtensions();
								TrustedProperties trustProperties = getTrustedProperties(
										relatedLoLoTE, loteInfo, detached, statusAndInformationExtensions);
								List<CertificateTrustTime> certificateTrustTimes = getCertificateTrustTimes(statusAndInformationExtensions, trustAnchorValidityPredicate);
								for (CertificateToken certificate : trustedEntityService.getCertificates()) {
									addCertificate(trustPropertiesByCerts, trustTimeByCerts, certificate, trustProperties, certificateTrustTimes);
								}
							}
						}
					}
				}
			} else {
				LOG.warn("Certificate synchronization is skipped for TL '{}'", loteInfo.getUrl());
			}
		}
	}

	private void addCertificate(final Map<CertificateToken, List<TrustedProperties>> trustedPropertiesByCerts,
	                            final Map<CertificateToken, List<CertificateTrustTime>> trustTimeByCerts,
	                            CertificateToken certificate, TrustedProperties trustProperties, List<CertificateTrustTime> certificateTrustTimes) {
		List<TrustedProperties> trustPropertiesList = trustedPropertiesByCerts.computeIfAbsent(certificate, k -> new ArrayList<>());
		if (!trustPropertiesList.contains(trustProperties)) {
			trustPropertiesList.add(trustProperties);
		}
		List<CertificateTrustTime> certificateTrustTimeList = trustTimeByCerts.computeIfAbsent(certificate, k -> new ArrayList<>());
		for (CertificateTrustTime certificateTrustTime : certificateTrustTimes) {
			if (!certificateTrustTimeList.contains(certificateTrustTime)) {
				certificateTrustTimeList.add(certificateTrustTime);
			}
		}
	}

	private TrustedEntity getDetached(TrustedEntity original) {
		TrustedEntityBuilder builder = new TrustedEntityBuilder(original);
		builder.setServices(Collections.emptyList());
		return builder.build();
	}

	private void syncCache(LoTEValidationJobSummary summary) {
		for (LoLoTEInfo loloteInfo : summary.getLoLoTEInfos()) {
			syncLoTEInfosCache(loloteInfo.getChildrenInfos());
			// TODO : pivots not yet supported
			syncCacheAccess.sync(new CacheKey(loloteInfo.getUrl()));
		}
		syncLoTEInfosCache(summary.getOtherLoTEInfos());
	}

	private void syncLoTEInfosCache(List<? extends LoTEInfo> loteInfos) {
		for (LoTEInfo loteInfo : loteInfos) {
			syncCacheAccess.sync(new CacheKey(loteInfo.getUrl()));
		}
	}

	private TrustedProperties getTrustedProperties(LoLoTEInfo relatedLoLoTE, LoTEInfo loTEInfo, TrustedEntity detached,
	                                               TimeDependentValues<ServiceStatusAndInformationExtensions> statusAndInformationExtensions) {
		if (relatedLoLoTE != null) {
			return new TrustedProperties(relatedLoLoTE, loTEInfo, detached, statusAndInformationExtensions);
		}
		return new TrustedProperties(loTEInfo, detached, statusAndInformationExtensions);
	}

	private List<CertificateTrustTime> getCertificateTrustTimes(
			TimeDependentValues<ServiceStatusAndInformationExtensions> statusAndInformationExtensions,
			Predicate<ServiceStatusAndInformationExtensions> trustAnchorValidityPredicate) {
		if (trustAnchorValidityPredicate == null) {
			// return empty instance (always valid), when no predicate is defined
			return Collections.singletonList(new CertificateTrustTime(true));
		}

		final List<CertificateTrustTime> result = new ArrayList<>();
		for (ServiceStatusAndInformationExtensions trustServiceStatusAndInformation : statusAndInformationExtensions) {
			if (trustAnchorValidityPredicate.test(trustServiceStatusAndInformation)) {
				result.add(new CertificateTrustTime(trustServiceStatusAndInformation.getStartDate(), trustServiceStatusAndInformation.getEndDate()));
			} else {
				result.add(new CertificateTrustTime(false)); // not trusted
			}
		}
		return result;
	}

	private Predicate<ServiceStatusAndInformationExtensions> getTrustAnchorValidityPredicate(LoTEInfo loteInfo, LoLoTEInfo relatedLoLoTEInfo) {
		LoTESource loteSource = getRelatedLoTESource(loteInfo, relatedLoLoTEInfo);
		if (loteSource != null) {
			return loteSource.getTrustAnchorValidityPredicate();
		}
		return null;
	}

	private LoTESource getRelatedLoTESource(LoTEInfo loteInfo, LoLoTEInfo relatedLoLoTEInfo) {
		if (relatedLoLoTEInfo != null) {
			for (LoLoTESource loloteSource : loloteSources) {
				if (loloteSource.getUrl().equals(relatedLoLoTEInfo.getUrl())) {
					return loloteSource;
				}
			}
		}
		for (LoTESource loTESource : loteSources) {
			if (loTESource.getUrl().equals(loteInfo.getUrl())) {
				return loTESource;
			}
		}
		return null;
	}

}
