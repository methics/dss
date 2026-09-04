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
package eu.europa.esig.dss.validation.process.qualification.trust;

import eu.europa.esig.dss.detailedreport.jaxb.XmlTLAnalysis;
import eu.europa.esig.dss.diagnostic.jaxb.XmlTrustSourceList;
import eu.europa.esig.dss.diagnostic.jaxb.XmlTrustedList;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.DurationRule;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.model.policy.MultiValuesRule;
import eu.europa.esig.dss.model.policy.ValidationPolicy;
import eu.europa.esig.dss.validation.process.Chain;
import eu.europa.esig.dss.validation.process.ChainItem;
import eu.europa.esig.dss.validation.process.qualification.trust.checks.TLFreshnessCheck;
import eu.europa.esig.dss.validation.process.qualification.trust.checks.TLNotExpiredCheck;
import eu.europa.esig.dss.validation.process.qualification.trust.checks.TLStructureCheck;
import eu.europa.esig.dss.validation.process.qualification.trust.checks.TLVersionCheck;
import eu.europa.esig.dss.validation.process.qualification.trust.checks.TLWellSignedCheck;

import java.util.Date;

/**
 * Performs validation of a LoTE
 *
 */
public class LoTEValidationBlock extends Chain<XmlTLAnalysis> {

    /** Trusted list to be validated */
    private final XmlTrustSourceList currentList;

    /** Validation time */
    private final Date currentTime;

    /** The signature validation policy */
    private final ValidationPolicy policy;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param currentList {@link XmlTrustedList}
     * @param currentTime {@link Date}
     * @param policy {@link ValidationPolicy}
     */
    public LoTEValidationBlock(I18nProvider i18nProvider, XmlTrustSourceList currentList, Date currentTime,
                               ValidationPolicy policy) {
        super(i18nProvider, new XmlTLAnalysis());

        result.setCountryCode(currentList.getCountryCode());
        result.setURL(currentList.getUrl());
        result.setId(currentList.getId());

        this.currentList = currentList;
        this.currentTime = currentTime;
        this.policy = policy;
    }

    @Override
    protected String buildChainTitle() {
        return i18nProvider.getMessage(MessageTag.LOTE, currentList.getCountryCode());
    }

    @Override
    protected void initChain() {

        ChainItem<XmlTLAnalysis> item = firstItem = loteFreshness();

        item = item.setNextItem(loteNotExpired());

        item = item.setNextItem(loteVersion());

        item = item.setNextItem(loteStructure());

        item = item.setNextItem(loteWellSigned());

    }

    private ChainItem<XmlTLAnalysis> loteFreshness() {
        DurationRule constraint = policy.getLoTEFreshnessConstraint();
        return new TLFreshnessCheck(i18nProvider, result, currentList, currentTime, constraint);
    }

    private ChainItem<XmlTLAnalysis> loteNotExpired() {
        LevelRule constraint = policy.getLoTENotExpiredConstraint();
        return new TLNotExpiredCheck(i18nProvider, result, currentList, currentTime, constraint);
    }

    private ChainItem<XmlTLAnalysis> loteVersion() {
        MultiValuesRule constraint = policy.getLoTEVersionConstraint();
        return new TLVersionCheck(i18nProvider, result, currentList, currentTime, constraint);
    }

    private ChainItem<XmlTLAnalysis> loteStructure() {
        LevelRule constraint = policy.getLoTEStructureConstraint();
        return new TLStructureCheck(i18nProvider, result, currentList, constraint);
    }

    private ChainItem<XmlTLAnalysis> loteWellSigned() {
        LevelRule constraint = policy.getLoTEWellSignedConstraint();
        return new TLWellSignedCheck(i18nProvider, result, currentList, constraint);
    }

}
