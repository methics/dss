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
package eu.europa.esig.dss.model.tsl;

import eu.europa.esig.dss.model.identifier.Identifier;
import eu.europa.esig.dss.model.job.AbstractDocumentInfo;
import eu.europa.esig.dss.model.job.DownloadInfoRecord;
import eu.europa.esig.dss.model.job.ValidationInfoRecord;
import eu.europa.esig.dss.model.tsl.identifier.TrustedListIdentifier;

/**
 * Computes summary for a single Trusted List processing result
 *
 */
public class TLInfo extends AbstractDocumentInfo<LOTLInfo> {
	
	private static final long serialVersionUID = -1505115221927652721L;

	/** OtherTSLPointer element extracted from the pointing TL/LOTL */
	private final OtherTSLPointer otherTSLPointer;
	
	/**
	 * The default constructor
	 *
	 * @param downloadCacheInfo {@link DownloadInfoRecord} a download cache result
	 * @param parsingCacheInfo {@link TLParsingInfoRecord} a parsing cache result
	 * @param validationCacheInfo {@link ValidationInfoRecord} a validation cache result
	 * @param url {@link String} address used to extract the entry
	 */
	public TLInfo(final DownloadInfoRecord downloadCacheInfo, final TLParsingInfoRecord parsingCacheInfo,
				  final ValidationInfoRecord validationCacheInfo, final String url) {
		this(downloadCacheInfo, parsingCacheInfo, validationCacheInfo, url, null);
	}

	/**
	 * The default constructor with parent TLInfo
	 *
	 * @param downloadCacheInfo {@link DownloadInfoRecord} a download cache result
	 * @param parsingCacheInfo {@link TLParsingInfoRecord} a parsing cache result
	 * @param validationCacheInfo {@link ValidationInfoRecord} a validation cache result
	 * @param url {@link String} address used to extract the entry
	 * @param parent {@link LOTLInfo} referencing the current Trusted List
	 */
	public TLInfo(final DownloadInfoRecord downloadCacheInfo, final TLParsingInfoRecord parsingCacheInfo,
				  final ValidationInfoRecord validationCacheInfo, final String url, final LOTLInfo parent) {
		this(downloadCacheInfo, parsingCacheInfo, validationCacheInfo, url, parent, null);
	}
	
	/**
	 * The constructor with parent LOTLInfo and Mutual Recognition Agreement
	 *
	 * @param downloadCacheInfo {@link DownloadInfoRecord} a download cache result
	 * @param parsingCacheInfo {@link TLParsingInfoRecord} a parsing cache result
	 * @param validationCacheInfo {@link ValidationInfoRecord} a validation cache result
	 * @param url {@link String} address used to extract the entry
	 * @param parent {@link LOTLInfo} referencing the current Trusted List
	 * @param otherTSLPointer {@link OtherTSLPointer} element from the pointing TL/LOTL
	 */
	public TLInfo(final DownloadInfoRecord downloadCacheInfo, final TLParsingInfoRecord parsingCacheInfo,
				  final ValidationInfoRecord validationCacheInfo, final String url, final LOTLInfo parent,
				  final OtherTSLPointer otherTSLPointer) {
		super(downloadCacheInfo, parsingCacheInfo, validationCacheInfo, url, parent);
		this.otherTSLPointer = otherTSLPointer;
	}

	@Override
	public TLParsingInfoRecord getParsingCacheInfo() {
		return (TLParsingInfoRecord) super.getParsingCacheInfo();
	}

	/**
	 * Gets the OtherTSLPointer element to referencing the current TL from the pointing TL/LOTL
	 *
	 * @return {@link OtherTSLPointer}
	 */
	public OtherTSLPointer getOtherTSLPointer() {
		return otherTSLPointer;
	}

	@Override
	protected Identifier buildIdentifier() {
		return new TrustedListIdentifier(this);
	}

}
