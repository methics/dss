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
package eu.europa.esig.dss.enumerations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Defines QC Identification Method OID identifiers as defined in the ETSI EN 319 412-5
 * "4.3.5 QCStatement stating the used eIDAS/eIDAS2 Article 24. identification method"
 *
 */
public interface QCIdentMethod extends OidDescription {

    /** Logger */
    Logger LOG = LoggerFactory.getLogger(QCIdentMethod.class);

    /** Defines a description for an unknown method by the current implementation */
    String UNKNOWN_METHOD = "qc-identification-method-unknown";

    /**
     * Returns a {@code QCType} by the given OID, if exists
     *
     * @param oid {@link String} to get {@link QCType} for
     * @return {@link QCType} if exists, NULL otherwise
     */
    static QCIdentMethod fromOid(String oid) {
        for (QCIdentMethod type : QCIdentMethodEnum.values()) {
            if (type.getOid().equals(oid)) {
                return type;
            }
        }

        LOG.debug("Unknown QCIdentMethod : '{}'", oid);
        return new QCIdentMethod() {

            private static final long serialVersionUID = 6089958556390661665L;

            @Override
            public String getDescription() { return UNKNOWN_METHOD; }
            @Override
            public String getOid() { return oid; }

        };
    }

}
