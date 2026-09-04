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

import eu.europa.esig.dss.enumerations.CryptographicSuiteAlgorithmUsage;
import eu.europa.esig.dss.enumerations.CryptographicSuiteRecommendation;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

class CryptographicSuiteEvaluationTest {

    @Test
    void testNull() {
        assertNull(CryptographicSuiteEvaluation.copy(null));
    }

    @Test
    void testFilled() {
        CryptographicSuiteParameter param = new CryptographicSuiteParameter();
        param.setName("n");
        param.setMin(1);
        param.setMax(2);

        Date start = new Date(1000L);
        Date end = new Date(2000L);

        CryptographicSuiteEvaluation original = new CryptographicSuiteEvaluation();
        original.setParameterList(Collections.singletonList(param));
        original.setValidityStart(start);
        original.setValidityEnd(end);
        original.setAlgorithmUsage(Collections.singletonList(CryptographicSuiteAlgorithmUsage.SIGN_DATA));
        original.setRecommendation(CryptographicSuiteRecommendation.RECOMMENDED);

        CryptographicSuiteEvaluation copy = CryptographicSuiteEvaluation.copy(original);

        assertNotNull(copy);
        assertEquals(start, copy.getValidityStart());
        assertEquals(end, copy.getValidityEnd());
        assertEquals(CryptographicSuiteRecommendation.RECOMMENDED, copy.getRecommendation());

        // parameter deep copy check
        assertNotNull(copy.getParameterList());
        assertEquals(1, copy.getParameterList().size());
        assertNotSame(param, copy.getParameterList().get(0));
        assertEquals("n", copy.getParameterList().get(0).getName());

        // algorithmUsage shallow copy check
        assertNotNull(copy.getAlgorithmUsage());
        assertEquals(1, copy.getAlgorithmUsage().size());
        assertEquals(CryptographicSuiteAlgorithmUsage.SIGN_DATA, copy.getAlgorithmUsage().get(0));
    }

    @Test
    void testCopyEmpty() {
        CryptographicSuiteEvaluation original = new CryptographicSuiteEvaluation();
        CryptographicSuiteEvaluation copy = CryptographicSuiteEvaluation.copy(original);

        assertNull(copy.getParameterList());
        assertNull(copy.getValidityStart());
        assertNull(copy.getValidityEnd());
        assertNull(copy.getAlgorithmUsage());
        assertNull(copy.getRecommendation());
    }

}
