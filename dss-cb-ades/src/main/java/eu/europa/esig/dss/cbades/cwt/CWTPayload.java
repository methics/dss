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
package eu.europa.esig.dss.cbades.cwt;

import eu.europa.esig.dss.cbades.cbor.CBORMap;
import eu.europa.esig.dss.cbades.cbor.CBORUtils;
import eu.europa.esig.dss.spi.WebTokenPayload;

import java.util.Date;

/**
 * Represents a payload of the RFC 8392 "CBOR Web Token (CWT)"
 *
 */
public class CWTPayload implements WebTokenPayload {

    /** Map representing a CWT payload */
    protected final CBORMap payload;

    /**
     * Default constructor
     *
     * @param payload {@link CBORMap}
     */
    public CWTPayload(final CBORMap payload) {
        this.payload = payload;
    }

    @Override
    public String getIssuer() {
        return payload.getAsString(CWTClaims.ISS.cbor());
    }

    @Override
    public String getSubject() {
        return payload.getAsString(CWTClaims.SUB.cbor());
    }

    @Override
    public String getAudience() {
        return payload.getAsString(CWTClaims.AUD.cbor());
    }

    @Override
    public Date getExpirationTime() {
        return CBORUtils.fromNumericDate(payload.getHeader(CWTClaims.EXP.cbor()));
    }

    @Override
    public Date getNotBefore() {
        return CBORUtils.fromNumericDate(payload.getHeader(CWTClaims.NBF.cbor()));
    }

    @Override
    public Date getIssuedAt() {
        return CBORUtils.fromNumericDate(payload.getHeader(CWTClaims.IAT.cbor()));
    }

    @Override
    public String getTokenId() {
        return payload.getAsString(CWTClaims.CTI.cbor());
    }

}
