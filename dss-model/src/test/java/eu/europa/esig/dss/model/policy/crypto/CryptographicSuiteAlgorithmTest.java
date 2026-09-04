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

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;

class CryptographicSuiteAlgorithmTest {

    @Test
    void testNull() {
        assertNull(CryptographicSuiteAlgorithm.copy(null));
    }

    @Test
    void testFilled() {
        CryptographicSuiteParameter param = new CryptographicSuiteParameter();
        param.setName("param1");
        param.setMin(1);
        param.setMax(10);

        CryptographicSuiteEvaluation eval = new CryptographicSuiteEvaluation();
        eval.setParameterList(Collections.singletonList(param));
        eval.setValidityStart(new Date(1000L));
        eval.setValidityEnd(new Date(2000L));
        eval.setAlgorithmUsage(Collections.singletonList(CryptographicSuiteAlgorithmUsage.SIGN_DATA));
        eval.setRecommendation(CryptographicSuiteRecommendation.RECOMMENDED);

        CryptographicSuiteAlgorithm original = new CryptographicSuiteAlgorithm();
        original.setAlgorithmIdentifierName("AlgoName");
        original.setAlgorithmIdentifierOIDs(Arrays.asList("OID1", "OID2"));
        original.setAlgorithmIdentifierURIs(Arrays.asList("URI1", "URI2"));
        original.setEvaluationList(Collections.singletonList(eval));
        original.setInformationTextList(Arrays.asList("Info1", "Info2"));

        CryptographicSuiteAlgorithm copy = CryptographicSuiteAlgorithm.copy(original);

        assertNotNull(copy);
        assertEquals("AlgoName", copy.getAlgorithmIdentifierName());
        assertEquals(Arrays.asList("OID1", "OID2"), copy.getAlgorithmIdentifierOIDs());
        assertEquals(Arrays.asList("URI1", "URI2"), copy.getAlgorithmIdentifierURIs());
        assertEquals(Arrays.asList("Info1", "Info2"), copy.getInformationTextList());

        // evaluation deep copy check
        assertNotNull(copy.getEvaluationList());
        assertEquals(1, copy.getEvaluationList().size());
        CryptographicSuiteEvaluation copiedEval = copy.getEvaluationList().get(0);
        assertNotSame(eval, copiedEval);
        assertEquals(new Date(1000L), copiedEval.getValidityStart());
        assertEquals(new Date(2000L), copiedEval.getValidityEnd());
        assertEquals(Collections.singletonList(CryptographicSuiteAlgorithmUsage.SIGN_DATA), copiedEval.getAlgorithmUsage());
        assertEquals(CryptographicSuiteRecommendation.RECOMMENDED, copiedEval.getRecommendation());

        List<CryptographicSuiteParameter> parameterList = copiedEval.getParameterList();
        assertEquals(1, parameterList.size());

        CryptographicSuiteParameter parameter = parameterList.get(0);
        assertEquals("param1", parameter.getName());
        assertEquals(1, parameter.getMin());
        assertEquals(10, parameter.getMax());
    }

    @Test
    void testCopyEmpty() {
        CryptographicSuiteAlgorithm original = new CryptographicSuiteAlgorithm();

        CryptographicSuiteAlgorithm copy = CryptographicSuiteAlgorithm.copy(original);

        assertNull(copy.getAlgorithmIdentifierName());
        assertNull(copy.getAlgorithmIdentifierOIDs());
        assertNull(copy.getAlgorithmIdentifierURIs());
        assertNull(copy.getEvaluationList());
        assertNull(copy.getInformationTextList());
    }

}
