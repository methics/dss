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
package eu.europa.esig.dss.spi;

import java.util.Date;

/**
 * Represents a payload of a web token (e.g. RFC 7519 token or RFC 8392 CWT)
 *
 */
public interface WebTokenPayload {

    /**
     * Gets the value of the Issuer claim identifying the principal that issued the token.
     *
     * @return {@link String}
     */
    String getIssuer();

    /**
     * Gets the value of the Subject claim identifying the principal that is the subject of the token.
     *
     * @return {@link String}
     */
    String getSubject();

    /**
     * Gets the value of the Audience claim identifying the recipients that the token is intended for.
     *
     * @return {@link String}
     */
    String getAudience();

    /**
     * Gets the value of the Expiration Time claim identifying the expiration time on
     * or after which the token MUST NOT be accepted for processing.
     *
     * @return {@link String}
     */
    Date getExpirationTime();

    /**
     * Gets the value of the Not Before claim identifying the time before which the token
     * MUST NOT be accepted for processing.
     *
     * @return {@link String}
     */
    Date getNotBefore();

    /**
     * Gets the value of the Issued At claim identifying the time before which the token
     * MUST NOT be accepted for processing.
     *
     * @return {@link String}
     */
     Date getIssuedAt();

    /**
     * Gets the value of the token ID claim representing a unique identifier for the token.
     *
     * @return {@link String}
     */
    String getTokenId();
    
}
