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
package eu.europa.esig.dss.validation.job.alerts;

import eu.europa.esig.dss.alert.Alert;
import eu.europa.esig.dss.model.job.DocumentInfo;
import eu.europa.esig.dss.model.job.DocumentListInfo;
import eu.europa.esig.dss.model.job.ValidationJobSummary;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * The class to process alerts on ValidationJob
 *
 * @param <D> current {@link DocumentInfo}
 * @param <L> parent {@link DocumentListInfo}
 */
public class ValidationJobAlerter<D extends DocumentInfo, L extends DocumentListInfo<D>> {

	private static final Logger LOG = LoggerFactory.getLogger(ValidationJobAlerter.class);

	/** Contains a list for document lists alerts */
	private final List<Alert<L>> documentListAlerts;

	/** Contains a list for document alerts */
	private final List<Alert<D>> documentAlerts;

	/**
	 * The constructor to instantiate a ValidationJobAlerter
	 * 
	 * @param documentListAlerts a list of {@link Alert}s to be applied on document list changes
	 * @param documentAlerts a list of {@link Alert}s to be applied on document changes
	 */
	public ValidationJobAlerter(final List<Alert<L>> documentListAlerts, final List<Alert<D>> documentAlerts) {
		this.documentListAlerts = documentListAlerts;
		this.documentAlerts = documentAlerts;
	}
	
	/**
	 * The method to run alerts on the given ValidationJobSummary
	 * 
	 * @param jobSummary {@link ValidationJobSummary} to execute alerts on
	 */
	public void detectChanges(final ValidationJobSummary<D, L> jobSummary) {
		for (L docListInfo : jobSummary.getDocumentListInfos()) {
			// run document list alerts
			if (Utils.isCollectionNotEmpty(documentListAlerts)) {
				for (Alert<L> docListAlert : documentListAlerts) {
					execute(docListAlert, docListInfo);
				}
			}
			// run document alerts
			if (Utils.isCollectionNotEmpty(documentAlerts)) {
				for (D docInfo : docListInfo.getChildrenInfos()) {
					for (Alert<D> docAlert : documentAlerts) {
						execute(docAlert, docInfo);
					}
				}
			}
		}
		// other documents
		if (Utils.isCollectionNotEmpty(documentAlerts)) {
			for (D docInfo : jobSummary.getOtherDocumentInfos()) {
				for (Alert<D> docAlert : documentAlerts) {
					execute(docAlert, docInfo);
				}
			}
		}
	}
	
	private <T extends DocumentInfo> void execute(Alert<T> alert, T info) {
		try {
			alert.alert(info);
		} catch (Exception e) {
			LOG.warn("An error occurred while trying to detect changes inside '{}'. Reason : {}", 
					info.getDSSId().asXmlId(), e.getMessage());
		}
	}
	
}
