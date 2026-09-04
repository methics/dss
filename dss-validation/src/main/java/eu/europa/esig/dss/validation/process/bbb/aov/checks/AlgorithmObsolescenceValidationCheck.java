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
package eu.europa.esig.dss.validation.process.bbb.aov.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlAOV;
import eu.europa.esig.dss.detailedreport.jaxb.XmlBlockType;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConclusion;
import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraintsConclusion;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCryptographicAlgorithm;
import eu.europa.esig.dss.detailedreport.jaxb.XmlCryptographicValidation;
import eu.europa.esig.dss.detailedreport.jaxb.XmlMessage;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.ValidationProcessUtils;

import java.util.Date;
import java.util.List;

/**
 * Verifies result of the {@code eu.europa.esig.dss.validation.process.bbb.aov.AlgorithmObsolescenceValidation} process
 *
 * @param <T> {@link XmlConstraintsConclusion}
 */
public class AlgorithmObsolescenceValidationCheck<T extends XmlConstraintsConclusion> extends ChainItem<T> {

    /** Result of the AOV validation process */
    protected final XmlAOV aovResult;

    /** Check execution time */
    protected final Date validationDate;

    /** The validating constraint position */
    protected final MessageTag position;

    /** Type of the validation block */
    protected final XmlBlockType blockType;

    /**
     * Constructor to extract and provide an Id of the token
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlConstraintsConclusion}
     * @param aovResult {@link XmlAOV}
     * @param validationDate {@link Date}
     * @param position {@link MessageTag}
     * @param tokenId {@link String} identifier of the token to be validated
     */
    public AlgorithmObsolescenceValidationCheck(I18nProvider i18nProvider, T result, XmlAOV aovResult,
                                                Date validationDate, MessageTag position, String tokenId) {
        this(i18nProvider, result, aovResult, validationDate, position, XmlBlockType.AOV, tokenId);
    }

    /**
     * Constructor to extract and provide an Id of the token
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlConstraintsConclusion}
     * @param aovResult {@link XmlAOV}
     * @param validationDate {@link Date}
     * @param position {@link MessageTag}
     * @param blockType {@link XmlBlockType}
     * @param tokenId {@link String} identifier of the token to be validated
     */
    public AlgorithmObsolescenceValidationCheck(I18nProvider i18nProvider, T result, XmlAOV aovResult,
                                                Date validationDate, MessageTag position, XmlBlockType blockType, String tokenId) {
        super(i18nProvider, result, getLevelRule(aovResult), tokenId);
        this.aovResult = aovResult;
        this.validationDate = validationDate;
        this.position = position;
        this.blockType = blockType;
    }

    @Override
    protected XmlBlockType getBlockType() {
        return blockType;
    }

    private static LevelRule getLevelRule(XmlAOV aovResult) {
        if (Utils.isCollectionNotEmpty(aovResult.getConclusion().getErrors())) {
            return ValidationProcessUtils.getLevelRule(Level.FAIL);
        } else if (Utils.isCollectionNotEmpty(aovResult.getConclusion().getWarnings())) {
            return ValidationProcessUtils.getLevelRule(Level.WARN);
        } else if (Utils.isCollectionNotEmpty(aovResult.getConclusion().getInfos())) {
            return ValidationProcessUtils.getLevelRule(Level.INFORM);
        }
        return ValidationProcessUtils.getLevelRule(Level.FAIL); // default
    }

    @Override
    protected boolean process() {
        return isValid(aovResult);
    }

    @Override
    protected boolean isValidConclusion(XmlConclusion conclusion) {
        return super.isValidConclusion(conclusion) && Utils.isCollectionEmpty(conclusion.getWarnings())
                && Utils.isCollectionEmpty(conclusion.getInfos());
    }

    @Override
    protected String buildAdditionalInfo() {
        String dateTime = ValidationProcessUtils.getFormattedDate(validationDate);
        if (process()) {
            XmlCryptographicValidation cryptographicValidation = ValidationProcessUtils.getPrimaryCryptographicValidation(aovResult);
            if (cryptographicValidation != null) {
                XmlCryptographicAlgorithm algorithm = cryptographicValidation.getAlgorithm();
                if (Utils.isStringNotEmpty(algorithm.getKeyLength())) {
                    return i18nProvider.getMessage(MessageTag.CRYPTOGRAPHIC_CHECK_SUCCESS_KEY_SIZE,
                            algorithm.getName(), algorithm.getKeyLength(), dateTime);
                } else {
                    return i18nProvider.getMessage(MessageTag.CRYPTOGRAPHIC_CHECK_SUCCESS,
                            algorithm.getName(), dateTime);
                }
            }
            return null;

        } else {
            return i18nProvider.getMessage(MessageTag.CRYPTOGRAPHIC_CHECK_FAILURE, getErrorMessage(), dateTime);
        }
    }

    @Override
    protected Indication getFailedIndicationForConclusion() {
        return aovResult.getConclusion().getIndication();
    }

    @Override
    protected SubIndication getFailedSubIndicationForConclusion() {
        return aovResult.getConclusion().getSubIndication();
    }

    @Override
    protected XmlMessage buildConstraintMessage() {
        return buildXmlMessage(MessageTag.ACCM, position);
    }

    @Override
    protected XmlMessage buildErrorMessage() {
        return buildXmlMessage(MessageTag.ACCM_ANS, position);
    }

    /**
     * Gets error message
     *
     * @return {@link String}, or empty string if check succeeded
     */
    protected String getErrorMessage() {
        List<XmlMessage> errors = aovResult.getConclusion().getErrors();
        if (Utils.isCollectionNotEmpty(errors)) {
            return errors.iterator().next().getValue();
        }
        List<XmlMessage> warnings = aovResult.getConclusion().getWarnings();
        if (Utils.isCollectionNotEmpty(warnings)) {
            return warnings.iterator().next().getValue();
        }
        List<XmlMessage> infos = aovResult.getConclusion().getInfos();
        if (Utils.isCollectionNotEmpty(infos)) {
            return infos.iterator().next().getValue();
        }
        return Utils.EMPTY_STRING;
    }

    @Override
    protected List<XmlMessage> getPreviousErrors() {
        return aovResult.getConclusion().getErrors();
    }

}
