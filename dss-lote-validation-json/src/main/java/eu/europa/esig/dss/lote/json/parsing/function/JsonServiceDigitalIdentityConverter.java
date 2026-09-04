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
package eu.europa.esig.dss.lote.json.parsing.function;

import eu.europa.esig.dss.jades.DSSJsonUtils;
import eu.europa.esig.dss.lote.json.parsing.JsonLoTEHeaderParameterNames;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

/**
 * This class converts a Json map object into a list of {@code CertificateToken}s
 *
 */
public class JsonServiceDigitalIdentityConverter implements Function<Map<?, ?>, List<CertificateToken>> {

    private static final Logger LOG = LoggerFactory.getLogger(JsonServiceDigitalIdentityConverter.class);

    /**
     * Default constructor
     */
    public JsonServiceDigitalIdentityConverter() {
        // empty
    }

    @Override
    public List<CertificateToken> apply(Map<?, ?> serviceDigitalIdentity) {
        List<CertificateToken> certificates = new ArrayList<>();
        if (Utils.isMapNotEmpty(serviceDigitalIdentity)) {
            List<?> x509Certificates = DSSJsonUtils.getAsList(serviceDigitalIdentity, JsonLoTEHeaderParameterNames.X509_CERTIFICATES);
            if (Utils.isCollectionNotEmpty(x509Certificates)) {
                for (Object pkiObject : x509Certificates) {
                    Map<?, ?> pkiOb = DSSJsonUtils.toMap(pkiObject);
                    if (Utils.isMapNotEmpty(pkiOb)) {
                        String valBase64 = DSSJsonUtils.getAsString(pkiOb, JsonLoTEHeaderParameterNames.VAL);
                        if (Utils.isStringNotEmpty(valBase64)) {
                            try {
                                certificates.add(DSSUtils.loadCertificateFromBase64EncodedString(valBase64));
                            } catch (Exception e) {
                                if (LOG.isDebugEnabled()) {
                                    LOG.debug(String.format("Unable to load certificate '%s' : ", valBase64), e);
                                } else {
                                    LOG.warn(String.format("Unable to load certificate '%s' " +
                                                    "(more details with enabled DEBUG mode)", valBase64));
                                }
                            }
                        } else {
                            LOG.warn("No 'val' header is present for the x509Certificate definition!");
                        }
                    }
                }
            }
        }
        return certificates;
    }

}
