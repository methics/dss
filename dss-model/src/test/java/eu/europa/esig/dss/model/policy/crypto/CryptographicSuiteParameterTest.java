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
package eu.europa.esig.dss.model.policy.crypto;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

class CryptographicSuiteParameterTest {

    @Test
    void testCopy_nullInput_returnsNull() {
        assertNull(CryptographicSuiteParameter.copy(null));
    }

    @Test
    void testCopy_allFieldsCopied() {
        CryptographicSuiteParameter original = new CryptographicSuiteParameter();
        original.setName("Param");
        original.setMin(5);
        original.setMax(10);

        CryptographicSuiteParameter copy = CryptographicSuiteParameter.copy(original);

        assertNotNull(copy);
        assertEquals("Param", copy.getName());
        assertEquals(5, copy.getMin());
        assertEquals(10, copy.getMax());

        // ensure deep copy (different object)
        assertNotSame(original, copy);
    }

    @Test
    void testCopy_handlesNullFields() {
        CryptographicSuiteParameter original = new CryptographicSuiteParameter();
        CryptographicSuiteParameter copy = CryptographicSuiteParameter.copy(original);

        assertNull(copy.getName());
        assertNull(copy.getMin());
        assertNull(copy.getMax());
    }

}
