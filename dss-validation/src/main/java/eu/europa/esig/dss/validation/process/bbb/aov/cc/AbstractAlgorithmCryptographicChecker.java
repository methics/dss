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
import eu.europa.esig.dss.detailedreport.jaxb.XmlCryptographicValidation;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.CryptographicSuite;
import eu.europa.esig.dss.validation.process.Chain;

import java.util.Date;

/**
 * Abstract class to perform cryptographic validation
 */
public abstract class AbstractAlgorithmCryptographicChecker extends Chain<XmlCC> {

	/** The name string for a unidentified (unsupported) algorithm */
	protected static final String ALGORITHM_UNIDENTIFIED = "UNIDENTIFIED";

	/** The urn for a not identified (unsupported) algorithm */
	protected static final String ALGORITHM_UNIDENTIFIED_URN = "urn:etsi:019102:algorithm:unidentified";

	/** The validation time */
	protected final Date validationDate;

	/** Cryptographic constraint */
	protected final CryptographicSuite cryptographicSuite;

	/** The validation constraint position */
	protected final MessageTag position;

	/** The verified cryptographic algorithm */
	protected XmlCryptographicAlgorithm cryptographicAlgorithm;

	/**
	 * Default constructor
	 *
	 * @param i18nProvider {@link I18nProvider}
	 * @param validationDate {@link Date}
	 * @param position {@link MessageTag}
	 * @param cryptographicSuite {@link CryptographicSuite}
	 */
	protected AbstractAlgorithmCryptographicChecker(I18nProvider i18nProvider, Date validationDate, MessageTag position,
													CryptographicSuite cryptographicSuite) {
		super(i18nProvider, new XmlCC());

		this.validationDate = validationDate;
		this.cryptographicSuite = cryptographicSuite;
		this.position = position;
	}
    
	@Override
	protected MessageTag getTitle() {
		return MessageTag.CC;
	}

	@Override
	protected void addAdditionalInfo() {
		super.addAdditionalInfo();
		result.setCryptographicValidation(getCryptographicValidation());
	}

	/**
	 * Builds a {@code XmlCryptographicValidation} information
	 *
	 * @return {@link XmlCryptographicValidation}
	 */
	protected XmlCryptographicValidation getCryptographicValidation() {
		XmlCryptographicValidation xmlCryptographicValidation = new XmlCryptographicValidation();
		xmlCryptographicValidation.setAlgorithm(getAlgorithm());
		xmlCryptographicValidation.setNotAfter(getNotAfter());
		xmlCryptographicValidation.setConclusion(result.getConclusion());
		return xmlCryptographicValidation;
	}

	/**
	 * Builds and returns the validated algorithm
	 *
	 * @return {@link XmlCryptographicAlgorithm}
	 */
	protected abstract XmlCryptographicAlgorithm getAlgorithm();

	/**
	 * Returns time after which the used cryptographic algorithm(s) is no longer considered secure
	 *
	 * @return {@link Date}
	 */
	protected abstract Date getNotAfter();

}
