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
package eu.europa.esig.dss.validation.process.bbb.fc;

import eu.europa.esig.dss.detailedreport.jaxb.XmlConstraint;
import eu.europa.esig.dss.detailedreport.jaxb.XmlFC;
import eu.europa.esig.dss.detailedreport.jaxb.XmlStatus;
import eu.europa.esig.dss.diagnostic.SignatureWrapper;
import eu.europa.esig.dss.diagnostic.jaxb.XmlBasicSignature;
import eu.europa.esig.dss.diagnostic.jaxb.XmlSignature;
import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.Level;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.policy.LevelConstraintWrapper;
import eu.europa.esig.dss.policy.jaxb.LevelConstraint;
import eu.europa.esig.dss.validation.process.bbb.AbstractTestCheck;
import eu.europa.esig.dss.validation.process.bbb.fc.checks.EllipticCurveKeySizeCheck;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EllipticCurveKeySizeCheckTest extends AbstractTestCheck {

    @Test
    void validTest() {
        XmlSignature xmlSignature = new XmlSignature();

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA256);
        basicSignature.setKeyLengthUsedToSignThisToken("256");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    void invalidTest() {
        XmlSignature xmlSignature = new XmlSignature();

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA256);
        basicSignature.setKeyLengthUsedToSignThisToken("384");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

    @Test
    void notEcdsaTest() {
        XmlSignature xmlSignature = new XmlSignature();

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.RSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA256);
        basicSignature.setKeyLengthUsedToSignThisToken("384");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    void notIdentifiedEncryptionAlgoTest() {
        XmlSignature xmlSignature = new XmlSignature();

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA256);
        basicSignature.setKeyLengthUsedToSignThisToken("384");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

    @Test
    void notIdentifiedDigestAlgoTest() {
        XmlSignature xmlSignature = new XmlSignature();

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.RSA);
        basicSignature.setKeyLengthUsedToSignThisToken("384");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

    @Test
    void notIdentifiedKeyLengthTest() {
        XmlSignature xmlSignature = new XmlSignature();

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.RSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA256);

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

    @Test
    void notAuthorizedDigestAlgoTest() {
        XmlSignature xmlSignature = new XmlSignature();

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA1);
        basicSignature.setKeyLengthUsedToSignThisToken("256");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

    @Test
    void brainpool256CBAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.CB_AdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA256);
        basicSignature.setKeyLengthUsedToSignThisToken("256");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    void brainpool256CBAdESInvalidDigestAlgoTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.CB_AdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA384);
        basicSignature.setKeyLengthUsedToSignThisToken("256");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

    @Test
    void brainpool320CBAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.CB_AdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA384);
        basicSignature.setKeyLengthUsedToSignThisToken("320");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    void p320JAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.JAdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA384);
        basicSignature.setKeyLengthUsedToSignThisToken("320");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

    @Test
    void brainpool384CBAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.CB_AdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA384);
        basicSignature.setKeyLengthUsedToSignThisToken("384");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    void p384BAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.CB_AdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA384);
        basicSignature.setKeyLengthUsedToSignThisToken("384");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    void p384JAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.JAdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA384);
        basicSignature.setKeyLengthUsedToSignThisToken("384");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    void brainpool512CBAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.CB_AdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA512);
        basicSignature.setKeyLengthUsedToSignThisToken("512");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    void brainpool512JAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.JAdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA512);
        basicSignature.setKeyLengthUsedToSignThisToken("512");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.NOT_OK, constraints.get(0).getStatus());
    }

    @Test
    void p521CBAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.CB_AdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA512);
        basicSignature.setKeyLengthUsedToSignThisToken("521");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

    @Test
    void p521JAdESTest() {
        XmlSignature xmlSignature = new XmlSignature();
        xmlSignature.setSignatureFormat(SignatureLevel.JAdES_BASELINE_B);

        XmlBasicSignature basicSignature = new XmlBasicSignature();
        basicSignature.setEncryptionAlgoUsedToSignThisToken(EncryptionAlgorithm.ECDSA);
        basicSignature.setDigestAlgoUsedToSignThisToken(DigestAlgorithm.SHA512);
        basicSignature.setKeyLengthUsedToSignThisToken("521");

        xmlSignature.setBasicSignature(basicSignature);

        LevelConstraint constraint = new LevelConstraint();
        constraint.setLevel(Level.FAIL);

        XmlFC result = new XmlFC();
        EllipticCurveKeySizeCheck ecksc = new EllipticCurveKeySizeCheck(i18nProvider, result, new SignatureWrapper(xmlSignature), new LevelConstraintWrapper(constraint));
        ecksc.execute();

        List<XmlConstraint> constraints = result.getConstraint();
        assertEquals(1, constraints.size());
        assertEquals(XmlStatus.OK, constraints.get(0).getStatus());
    }

}
