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
package eu.europa.esig.dss.validation.process.qualification.certificate.qwac.sub.checks;

import eu.europa.esig.dss.detailedreport.jaxb.XmlValidationQWACProcess;
import eu.europa.esig.dss.diagnostic.CertificateWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlGeneralName;
import eu.europa.esig.dss.enumerations.Indication;
import eu.europa.esig.dss.enumerations.SubIndication;
import eu.europa.esig.dss.i18n.I18nProvider;
import eu.europa.esig.dss.i18n.MessageTag;
import eu.europa.esig.dss.model.policy.LevelRule;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.validation.process.ChainItem;

/**
 * This class verifies whether the website domain name in question appears in the QWAC's subject alternative name(s)
 *
 */
public class QWACDomainNameCheck extends ChainItem<XmlValidationQWACProcess> {

    /** Certificate to be validated */
    private final CertificateWrapper certificate;

    /** The URL of the website */
    private final String websiteUrl;

    /**
     * Default constructor
     *
     * @param i18nProvider {@link I18nProvider}
     * @param result {@link XmlValidationQWACProcess}
     * @param certificate {@link CertificateWrapper}
     * @param websiteUrl {@link String}
     * @param constraint {@link LevelRule}
     */
    public QWACDomainNameCheck(final I18nProvider i18nProvider, final XmlValidationQWACProcess result,
                               final CertificateWrapper certificate, final String websiteUrl, final LevelRule constraint) {
        super(i18nProvider, result, constraint);

        this.certificate = certificate;
        this.websiteUrl = websiteUrl;
    }

    @Override
    protected boolean process() {
        String host = DSSUtils.getHost(websiteUrl);

        for (XmlGeneralName generalName : certificate.getSubjectAlternativeNames()) {
            switch (generalName.getType()) {
                case DNS_NAME:
                    if (matchesDNSName(host, generalName.getValue())) {
                        return true;
                    }
                    break;
                case IP_ADDRESS:
                    if (matchesIPAddress(host, generalName.getValue())) {
                        return true;
                    }
                    break;
                default:
                    // not supported
                    break;
            }
        }
        return false;
    }

    /**
     * Verifies if the given domain name matches the SAN DNS name,
     * according to CAB Forum BR section 3.2.2.6 and RFC 6125 section 6.4.3 (wildcard matching).
     *
     * @param hostname {@link String} of the website
     * @param subAltName {@link String} dns pattern value extracted from the certificate
     */
    private boolean matchesDNSName(String hostname, String subAltName) {
        subAltName = subAltName.toLowerCase();
        hostname = hostname.toLowerCase();

        if (subAltName.equals(hostname)) {
            return true;
        }

        /*
         *  If a client matches the reference identifier against a presented
         * identifier whose DNS domain name portion contains the wildcard
         * character '*', the following rules apply:
         *
         * 1. The client SHOULD NOT attempt to match a presented identifier in
         * which the wildcard character comprises a label other than the
         * left-most label (e.g., do not match bar.*.example.net).
         *
         * 2. If the wildcard character is the only character of the left-most
         * label in the presented identifier, the client SHOULD NOT compare
         * against anything but the left-most label of the reference
         * identifier (e.g., *.example.com would match foo.example.com but
         * not bar.foo.example.com or example.com).
         */

        // NOTE: Only Full Qualified Domain Names are considered (FQDN)

        // Wildcard match
        if (subAltName.startsWith("*.") && hostname.contains(".")) {
            String suffix = subAltName.substring(2);
            return hostname.endsWith(suffix)
                    && countParts(hostname) == countParts(suffix) + 1;
        }

        return false;
    }

    private int countParts(String domain) {
        return domain.split("\\.").length;
    }

    /**
     * Verifies if the given domain name matches one the IP Address,
     * according to CAB Forum BR section 7.1.2.7.12
     *
     * @param hostname {@link String} of the website
     * @param subAltName {@link String} dns pattern value extracted from the certificate
     */
    private boolean matchesIPAddress(String hostname, String subAltName) {
        // NOTE: must be a complete match, no wildcards supported
        return hostname.equals(subAltName);
    }

    @Override
    protected MessageTag getMessageTag() {
        return MessageTag.QWAC_DOMAIN_NAME;
    }

    @Override
    protected MessageTag getErrorMessageTag() {
        return MessageTag.QWAC_DOMAIN_NAME_ANS;
    }

    @Override
    protected Indication getFailedIndicationForConclusion() {
        return Indication.FAILED;
    }

    @Override
    protected SubIndication getFailedSubIndicationForConclusion() {
        return null;
    }

}
