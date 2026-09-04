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
package eu.europa.esig.dss.service.ocsp;

import eu.europa.esig.dss.enumerations.RevocationOrigin;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.model.x509.revocation.ocsp.OCSP;
import eu.europa.esig.dss.spi.CertificateExtensionsUtils;
import eu.europa.esig.dss.spi.DSSRevocationUtils;
import eu.europa.esig.dss.spi.x509.revocation.FileRevocationSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPSource;
import eu.europa.esig.dss.spi.x509.revocation.ocsp.OCSPToken;
import org.bouncycastle.cert.ocsp.BasicOCSPResp;
import org.bouncycastle.cert.ocsp.OCSPResp;
import org.bouncycastle.cert.ocsp.SingleResp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * OCSPSource that stores its OCSP responses on the file system.
 * <p>
 * WARNING: The class represents an experimental functionality and included in the DSS version 6.3.
 *          Please note the class was not extensively tested.
 */
public class FileCacheOCSPSource extends FileRevocationSource<OCSP> implements OCSPSource {

	private static final long serialVersionUID = 4464882643639588480L;

	private static final Logger LOG = LoggerFactory.getLogger(FileCacheOCSPSource.class);

	/** Extension used for the OCSP filename definition */
	private static final String OCSP_FILE_EXTENSION = ".ocsp";

	/**
	 * Empty constructor.
	 * The proxied OCSPSource can be provided using the {@code #setProxySource} method.
	 */
	public FileCacheOCSPSource() {
		super();
	}

	/**
	 * Constructor that initializes the file cache OCSP source with a proxiedOCSPSource provided.
	 *
	 * @param proxiedSource {@link OCSPSource} to be used to load OCSP when the corresponding 
	 *                                       revocation document is not available in the file system.
	 */
	public FileCacheOCSPSource(OCSPSource proxiedSource) {
		super(proxiedSource);
	}

	@Override
	protected OCSPToken reconstructTokenFromEncodedData(FileRevocationSource<OCSP>.FileCacheEntry revocationCache,
			CertificateToken certificateToken, CertificateToken issuerCertToken) {
		try {
			OCSPResp ocspResp = new OCSPResp(revocationCache.getRevocationDataBinaries());
			Object responseObject = ocspResp.getResponseObject();
			BasicOCSPResp basicOCSPResp = (BasicOCSPResp) responseObject;
			SingleResp latestSingleResponse = DSSRevocationUtils.getLatestSingleResponse(
					basicOCSPResp, certificateToken, issuerCertToken);

			OCSPToken token = new OCSPToken(basicOCSPResp, latestSingleResponse, certificateToken, issuerCertToken);
			token.setExternalOrigin(RevocationOrigin.CACHED);
			token.setSourceURL(revocationCache.getRevocationDataSourceUrl());
			return token;

		} catch (Exception e) {
			LOG.error("Failed to create OCSP token from cached data for certificate '{}': {}",
					certificateToken.getDSSIdAsString(), e.getMessage());
			return null;
		}
	}

	@Override
	protected String getRevocationFileExtension() {
		return OCSP_FILE_EXTENSION;
	}

	@Override
	protected String getRevocationTokenKey(CertificateToken certificateToken, String revocationAccessUrl) {
		return DSSRevocationUtils.getOcspRevocationKey(certificateToken, revocationAccessUrl);
	}

	@Override
	protected List<String> getRevocationAccessUrls(CertificateToken certificateToken) {
		return CertificateExtensionsUtils.getOCSPAccessUrls(certificateToken);
	}

	@Override
	protected List<String> initRevocationTokenKeys(CertificateToken certificateToken) {
		final List<String> revocationKeys = new ArrayList<>();
		final List<String> ocspUrls = getRevocationAccessUrls(certificateToken);
		for (String ocspUrl : ocspUrls) {
			revocationKeys.add(getRevocationTokenKey(certificateToken, ocspUrl));
		}
		return revocationKeys;
	}

	@Override
	public OCSPToken getRevocationToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken,
			boolean forceRefresh) {
		return (OCSPToken) super.getRevocationToken(certificateToken, issuerCertificateToken, forceRefresh);
	}

	@Override
	public OCSPToken getRevocationToken(CertificateToken certificateToken, CertificateToken issuerCertificateToken) {
		return this.getRevocationToken(certificateToken, issuerCertificateToken, false);
	}

}
