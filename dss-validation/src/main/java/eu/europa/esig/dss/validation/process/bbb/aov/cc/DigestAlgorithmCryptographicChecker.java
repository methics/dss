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
package eu.europa.esig.dss.validation.process.bbb.aov.cc;

import eu.europa.esig.dss.detailedreport.jaxb.XmlCC;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCryptographicAlgorithm;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.CryptographicSuite;
import eu.europa.esig.dss.validation.policy.CryptographicSuiteUtils;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.bbb.aov.cc.checks.DigestAlgorithmAtValidationTimeCheck;
import eu.europa.esig.dss.validation.process.bbb.aov.cc.checks.DigestAlgorithmReliableCheck;

import java.util.Date;

/**
 * Checks the digest algorithm
 */
public class DigestAlgorithmCryptographicChecker extends AbstractAlgorithmCryptographicChecker {

	/** The Digest algorithm */
	private final DigestAlgorithm digestAlgorithm;

	/**
	 * Default constructor
	 *
	 * @param i18nProvider {@link I18nProvider}
	 * @param digestAlgorithm {@link DigestAlgorithm}
	 * @param validationDate {@link Date}
	 * @param position {@link MessageTag}
	 * @param constraint {@link CryptographicSuite}
	 */
	public DigestAlgorithmCryptographicChecker(I18nProvider i18nProvider, DigestAlgorithm digestAlgorithm, Date validationDate,
											   MessageTag position, CryptographicSuite constraint) {
		super(i18nProvider, validationDate, position, constraint);

		this.digestAlgorithm = digestAlgorithm;
	}

	@Override
	protected void initChain() {
		
		ChainItem<XmlCC> item = firstItem = digestAlgorithmReliable();

		item = item.setNextItem(digestAlgorithmOnValidationTime());
		
	}

	/**
	 * Checks if the {@code digestAlgorithm} is acceptable
	 *
	 * @return TRUE if the {@code digestAlgorithm} is acceptable, FALSE otherwise
	 */
	protected ChainItem<XmlCC> digestAlgorithmReliable() {
		return new DigestAlgorithmReliableCheck(i18nProvider, digestAlgorithm, result, position, cryptographicSuite);
	}

	/**
	 * Checks if the {@code digestAlgorithm} is not expired in validation time
	 *
	 * @return TRUE if the {@code digestAlgorithm} is not expired in validation time, FALSE otherwise
	 */
	protected ChainItem<XmlCC> digestAlgorithmOnValidationTime() {
		return new DigestAlgorithmAtValidationTimeCheck(i18nProvider, digestAlgorithm, validationDate, result, position, cryptographicSuite);
	}

	@Override
	protected XmlCryptographicAlgorithm getAlgorithm() {
		if (cryptographicAlgorithm == null) {
			cryptographicAlgorithm = new XmlCryptographicAlgorithm();
			if (digestAlgorithm != null) {
				// if DigestAlgorithm is defined
				cryptographicAlgorithm.setName(digestAlgorithm.getName());
				cryptographicAlgorithm.setUri(getDigestAlgorithmUri(digestAlgorithm));

			} else {
				// if DigestAlgorithm is not found (unable to build either SignatureAlgorithm nor DigestAlgorithm)
				cryptographicAlgorithm.setName(ALGORITHM_UNIDENTIFIED);
				cryptographicAlgorithm.setUri(ALGORITHM_UNIDENTIFIED_URN);
			}
		}
		return cryptographicAlgorithm;
	}

	private String getDigestAlgorithmUri(DigestAlgorithm digestAlgorithm) {
		if (digestAlgorithm != null) {
			if (digestAlgorithm.getUri() != null) {
				return digestAlgorithm.getUri();
			}
			if (digestAlgorithm.getOid() != null) {
				return digestAlgorithm.getOid();
			}
		}
		return ALGORITHM_UNIDENTIFIED_URN;
	}

	@Override
	protected Date getNotAfter() {
		if (CryptographicSuiteUtils.isDigestAlgorithmReliable(cryptographicSuite, digestAlgorithm)) {
			return CryptographicSuiteUtils.getExpirationDate(cryptographicSuite, digestAlgorithm);
		}
		return null;
	}

}
