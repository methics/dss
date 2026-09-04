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
package eu.europa.esig.dss.validation.process.bbb.sav;

import eu.europa.esig.dss.detailedreport.jaxb.XmlAOV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlSAV;
import eu.europa.esig.dss.diagnostic.AbstractTokenProxy;
import eu.europa.esig.dss.enumerations.Context;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.model.policy.ValidationPolicy;
import eu.europa.esig.dss.validation.process.Chain;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;
import eu.europa.esig.dss.validation.process.bbb.aov.checks.AlgorithmObsolescenceValidationCheck;
import eu.europa.esig.dss.validation.process.bbb.sav.checks.AllCertificatesInPathReferencedCheck;
import eu.europa.esig.dss.validation.process.bbb.sav.checks.SigningCertificateAttributePresentCheck;
import eu.europa.esig.dss.validation.process.bbb.sav.checks.SigningCertificateReferencesValidityCheck;
import eu.europa.esig.dss.validation.process.bbb.sav.checks.UnicitySigningCertificateAttributeCheck;

import java.util.Date;

/**
 * 5.2.8 Signature acceptance validation (SAV) This building block covers any
 * additional verification to be performed on the signature itself or on the
 * attributes of the signature ETSI EN 319 132-1
 *
 * @param <T> validation token wrapper
 */
public abstract class AbstractAcceptanceValidation<T extends AbstractTokenProxy> extends Chain<XmlSAV> {

	/** The token to be validated */
	protected final T token;

	/** The validation time */
	protected final Date currentTime;

	/** The validation context */
	protected final Context context;

	/** Result of Algorithm Obsolescence Validation block */
	protected final XmlAOV aov;

	/** The validation policy */
	protected final ValidationPolicy validationPolicy;

	/**
	 * Default constructor
	 *
	 * @param i18nProvider {@link I18nProvider}
	 * @param token to validate
	 * @param currentTime {@link Date}
	 * @param context {@link Context}
	 * @param aov {@link XmlAOV}
	 * @param validationPolicy {@link ValidationPolicy}
	 */
	protected AbstractAcceptanceValidation(I18nProvider i18nProvider, T token, Date currentTime, Context context,
										   XmlAOV aov, ValidationPolicy validationPolicy) {
		super(i18nProvider, new XmlSAV());
		this.token = token;
		this.currentTime = currentTime;
		this.context = context;
		this.aov = aov;
		this.validationPolicy = validationPolicy;
	}

	/**
	 * Checks whether a signing-certificate signed attribute is present
	 *
	 * @return {@link ChainItem}
	 */
	protected ChainItem<XmlSAV> signingCertificateAttributePresent() {
		LevelRule constraint = validationPolicy.getSigningCertificateAttributePresentConstraint(context);
		return new SigningCertificateAttributePresentCheck(i18nProvider, result, token, constraint);
	}

	/**
	 * Checks if only one signing-certificate signed attribute is present
	 *
	 * @return {@link ChainItem}
	 */
	protected ChainItem<XmlSAV> unicitySigningCertificateAttribute() {
		LevelRule constraint = validationPolicy.getUnicitySigningCertificateAttributeConstraint(context);
		return new UnicitySigningCertificateAttributeCheck(i18nProvider, result, token, constraint);
	}

	/**
	 * Checks whether a signing-certificate signed attribute is valid to the determined signing certificate
	 *
	 * @return {@link ChainItem}
	 */
	protected ChainItem<XmlSAV> signingCertificateReferencesValidity() {
		LevelRule constraint = validationPolicy.getSigningCertificateRefersCertificateChainConstraint(context);
		return new SigningCertificateReferencesValidityCheck(i18nProvider, result, token, constraint);
	}

	/**
	 * Checks if all certificates in a signing certificate chain are references
	 * within signing-certificate signed attribute
	 *
	 * @return {@link ChainItem}
	 */
	protected ChainItem<XmlSAV> allCertificatesInPathReferenced() {
		LevelRule constraint = validationPolicy.getReferencesToAllCertificateChainPresentConstraint(context);
		return new AllCertificatesInPathReferencedCheck(i18nProvider, result, token, constraint);
	}

	/**
	 * Verifies cryptographic validity of signature references and signing-certificate signed attribute
	 *
	 * @param item {@link ChainItem} the last initialized chain item to be processed
	 * @return {@link ChainItem}
	 */
	protected ChainItem<XmlSAV> cryptographic(ChainItem<XmlSAV> item) {
		// The basic signature constraints validation
		MessageTag position = ValidationProcessUtils.getCryptoPosition(context);

		if (item == null) {
			item = firstItem = algorithmObsolescenceValidationCheck(result, aov, position);
		} else {
			item = item.setNextItem(algorithmObsolescenceValidationCheck(result, aov, position));
		}
		
		return item;
	}

	/**
	 * Verifies the result of the Algorithm Obsolescence Validation building block
	 *
	 * @param result {@link XmlSAV}
	 * @param aovResult {@link XmlAOV}
	 * @param position {@link MessageTag}
	 * @return {@link ChainItem}
	 */
	protected ChainItem<XmlSAV> algorithmObsolescenceValidationCheck(XmlSAV result, XmlAOV aovResult, MessageTag position) {
		return new AlgorithmObsolescenceValidationCheck<>(i18nProvider, result, aovResult, currentTime, position, token.getId());
	}

}
