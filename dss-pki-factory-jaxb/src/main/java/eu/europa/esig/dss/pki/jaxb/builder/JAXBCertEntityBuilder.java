package eu.europa.esig.dss.pki.jaxb.builder;

import eu.europa.esig.dss.enumerations.DigestAlgorithm;
import eu.europa.esig.dss.enumerations.EncryptionAlgorithm;
import eu.europa.esig.dss.enumerations.ExtendedKeyUsage;
import eu.europa.esig.dss.enumerations.MaskGenerationFunction;
import eu.europa.esig.dss.enumerations.SignatureAlgorithm;
import eu.europa.esig.dss.pki.exception.PKIException;
import eu.europa.esig.dss.pki.jaxb.XmlCRLType;
import eu.europa.esig.dss.pki.jaxb.XmlCertificateType;
import eu.europa.esig.dss.pki.jaxb.XmlDateDefinitionType;
import eu.europa.esig.dss.pki.jaxb.XmlEntityKey;
import eu.europa.esig.dss.pki.jaxb.XmlKeyAlgo;
import eu.europa.esig.dss.pki.jaxb.XmlPki;
import eu.europa.esig.dss.pki.jaxb.model.EntityId;
import eu.europa.esig.dss.pki.jaxb.model.JAXBCertEntity;
import eu.europa.esig.dss.pki.jaxb.repository.JaxbCertEntityRepository;
import eu.europa.esig.dss.spi.DSSSecurityProvider;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.utils.Utils;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.jce.ECNamedCurveTable;
import org.bouncycastle.jce.spec.ECParameterSpec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigInteger;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.Security;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.CRL_EXTENSION;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.CRL_PATH;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.CRT_EXTENSION;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.CRT_PATH;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.CUSTOM_URL_PREFIX;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.EMPTY_URL_PREFIX;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.EXTENDED_URL_PREFIX;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.HOST;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.OCSP_PATH;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.country;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.organisation;
import static eu.europa.esig.dss.pki.jaxb.property.PKIJaxbProperties.organisationUnit;


/**
 * Builds a {@code JAXBCertEntity} object
 *
 */
public class JAXBCertEntityBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(JAXBCertEntityBuilder.class);

    static {
        Security.addProvider(DSSSecurityProvider.getSecurityProvider());
    }

    /**
     * Default constructor
     */
    public JAXBCertEntityBuilder() {
        // empty
    }

    /**
     * Generates certificate entries from configuration provided within {@code pki} and populates the {@code repository}
     *
     * @param repository {@link JaxbCertEntityRepository} to be populated
     * @param pki {@link XmlPki} to generate values from
     */
    public void persistPKI(JaxbCertEntityRepository repository, XmlPki pki) {
        LOG.info("PKI {} : {} certificates", pki.getName(), pki.getCertificate().size());

        Map<EntityId, XmlCertificateType> certificateTypeMap = new HashMap<>();
        Map<EntityId, X500Name> x500names = new HashMap<>();
        Map<EntityId, KeyPair> keyPairs = new HashMap<>();
        Map<EntityId, JAXBCertEntity> entities = new HashMap<>();
        JAXBCertEntity certEntity;

        for (XmlCertificateType certType : pki.getCertificate()) {

            LOG.info("Init '{}' ...", certType.getSubject());

            JAXBCertEntity issuer = getIssuer(entities, certType.getIssuer());
            String issuerName = issuer != null ? issuer.getSubject() : certType.getSubject();
            EntityId entityId = new EntityId(issuerName, certType.getSerialNumber());
            EntityId issuerId = new EntityId(certType.getIssuer());
            try {
                certificateTypeMap.put(entityId, certType);

                KeyPair subjectKeyPair = buildKeyPair(keyPairs, entityId, certType);

                boolean selfSigned = entityId.equals(issuerId);
                KeyPair issuerKeyPair = selfSigned ? subjectKeyPair : getKeyPair(keyPairs, issuerId);

                X500Name subjectX500Name = getX500NameSubject(x500names, certType, entityId);
                X500Name issuerX500Name = getX500NameIssuer(x500names, issuerId);

                XmlCertificateType issuerCertificate = getIssuerCertificateType(certificateTypeMap, certType, issuerId);
                X509CertificateBuilder certBuilder = getX509CertBuilder(
                        certType, subjectKeyPair, issuerCertificate, issuerKeyPair, subjectX500Name, issuerX500Name);
                X509CertificateHolder certificateHolder = certBuilder.build(BigInteger.valueOf(certType.getSerialNumber()),
                        convert(certType.getNotBefore()), convert(certType.getNotAfter()));

                certEntity = buildJaxbCertEntity(certType, certificateHolder, subjectKeyPair, entityId, issuerId, entities, pki.getName());
                saveEntity(repository, certEntity, entities, entityId);

            } catch (Exception e) {
                throw new PKIException(String.format("Unable to create a PKI. Reason : %s", e.getMessage()), e);
            }
        }

    }

    private JAXBCertEntity buildJaxbCertEntity(XmlCertificateType certificate, X509CertificateHolder certificateHolder,
                                               KeyPair subjectKeyPair, EntityId entityId, EntityId issuerKey,
                                               Map<EntityId, JAXBCertEntity> entities, String pkiName) {

        boolean selfSigned = entityId.equals(issuerKey);
        JAXBCertEntity dbCertEntity;
        //@formatter:off
        try {
            dbCertEntity = GenericBuilder.of(JAXBCertEntity::new)
                    .with(JAXBCertEntity::setSubject, certificate.getSubject())
                    .with(JAXBCertEntity::setSerialNumber, certificate.getSerialNumber())
                    .with(JAXBCertEntity::setCertificateToken, DSSUtils.loadCertificate(certificateHolder.getEncoded()))
                    .with(JAXBCertEntity::setPrivateKey, subjectKeyPair.getPrivate().getEncoded())
                    .with(JAXBCertEntity::setPrivateKeyAlgo, subjectKeyPair.getPrivate().getAlgorithm())
                    .with(JAXBCertEntity::setRevocationDate, convert(certificate.getRevocation()))
                    .with(JAXBCertEntity::setRevocationReason, certificate.getRevocation() != null ? certificate.getRevocation().getReason() : null)
                    .with(JAXBCertEntity::setSuspended, certificate.getSuspended() != null)
                    .with(JAXBCertEntity::setOcspResponder, getEntity(entities, certificate.getOcspResponder() != null ? new EntityId(certificate.getOcspResponder()) : null, false))
                    .with(JAXBCertEntity::setTrustAnchor, certificate.getTrustAnchor() != null)
                    .with(JAXBCertEntity::setCa, certificate.getCa() != null)
                    .with(JAXBCertEntity::setTsa, certificate.getTsa() != null)
                    .with(JAXBCertEntity::setOcsp, certificate.getOcspSigning() != null)
                    .with(JAXBCertEntity::setToBeIgnored, certificate.getIgnore() != null)
                    .with(JAXBCertEntity::setPkiName, pkiName)
                    .with(JAXBCertEntity::setPss, certificate.getKeyAlgo() != null && Utils.isTrue(certificate.getKeyAlgo().isPss()))
                    .with(JAXBCertEntity::setDigestAlgo, certificate.getDigestAlgo())
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //@formatter:on
        if (selfSigned) {
            dbCertEntity.setParent(dbCertEntity);
        } else {
            dbCertEntity.setParent(getEntity(entities, issuerKey, selfSigned));
        }

        return dbCertEntity;
    }

    private X509CertificateBuilder getX509CertBuilder(XmlCertificateType certificateType, KeyPair subjectKeyPair, XmlCertificateType issuerCertificateType,
                                                      KeyPair issuerKeyPair, X500Name subjectX500Name, X500Name issuerX500Name) {
        final X509CertificateBuilder certBuilder = new X509CertificateBuilder()
                .subject(subjectX500Name, subjectKeyPair.getPublic());

        EncryptionAlgorithm encryptionAlgorithm = EncryptionAlgorithm.forKey(issuerKeyPair.getPrivate());
        DigestAlgorithm digestAlgo = issuerCertificateType.getDigestAlgo();
        boolean pss = Utils.isTrue(issuerCertificateType.getKeyAlgo().isPss());
        MaskGenerationFunction mgf = pss ? MaskGenerationFunction.MGF1 : null;
        final SignatureAlgorithm signatureAlgo = SignatureAlgorithm.getAlgorithm(encryptionAlgorithm, digestAlgo, mgf);
        if (signatureAlgo == null) {
            throw new IllegalArgumentException(String.format("Unable to find a SignatureAlgorithm for combination of " +
                    "[EncryptionAlgo: %s, DigestAlgo: %s, Pss: %s]", EncryptionAlgorithm.forKey(issuerKeyPair.getPrivate()), digestAlgo, pss));
        }

        certBuilder.issuer(issuerX500Name, issuerKeyPair.getPrivate(), signatureAlgo)
                .caIssuers(getAiaUrl(certificateType.getAia()))
                .crl(getCrlUrl(certificateType.getCrl()))
                .ocsp(getOcspUrl(certificateType.getOcsp()))
                .keyUsages(certificateType.getKeyUsages() != null ? certificateType.getKeyUsages().getKeyUsage() : Collections.emptyList())
                .certificatePolicies(certificateType.getCertificatePolicies() != null ? certificateType.getCertificatePolicies().getCertificatePolicy() : Collections.emptyList())
                .qcStatements(certificateType.getQcStatementIds() != null ? certificateType.getQcStatementIds().getQcStatement() : Collections.emptyList())
                .qcTypes(certificateType.getQcTypes() != null ? certificateType.getQcTypes().getQcType() : Collections.emptyList())
                .qcCClegislations(certificateType.getQcCClegislation() != null ? certificateType.getQcCClegislation().getCountryName() : Collections.emptyList());

        if (certificateType.getCa() != null) {
            certBuilder.ca(true);
        }
        if (certificateType.getOcspNoCheck() != null) {
            certBuilder.ocspNoCheck(true);
        }

        List<ExtendedKeyUsage> extendedKeyUsageList = new ArrayList<>();
        if (certificateType.getOcspSigning() != null) {
            extendedKeyUsageList.add(ExtendedKeyUsage.OCSP_SIGNING);
        }
        if (certificateType.getTsa() != null) {
            extendedKeyUsageList.add(ExtendedKeyUsage.TIMESTAMPING);
        }
        if (Utils.isCollectionNotEmpty(extendedKeyUsageList)) {
            certBuilder.extendedKeyUsages(extendedKeyUsageList);
        }

        return certBuilder;
    }

    /**
     * Retrieves the issuer certificate entity with the given entity key from the entities map.
     *
     * @param entities  The map of certificate entities, where the key is the EntityId and the value is the DBCertEntity.
     * @param entityKey The entity key for the issuer certificate.
     * @return The issuer certificate entity associated with the given entity key, or null if not found.
     */
    private JAXBCertEntity getIssuer(Map<EntityId, JAXBCertEntity> entities, XmlEntityKey entityKey) {
        if (entityKey.getSerialNumber() != null) {
            return entities.get(new EntityId(entityKey));
        }
        return null;
    }

    private JAXBCertEntity getEntity(Map<EntityId, JAXBCertEntity> entities, EntityId key, boolean ignoreException) {
        if (key != null) {
            JAXBCertEntity certEntity = entities.get(key);
            if (certEntity == null && !ignoreException) {
                throw new IllegalArgumentException("Entity not found " + key);
            }
            return certEntity;
        }
        return null;
    }

    private String getCrlUrl(XmlCRLType crlEntity) {
        if (crlEntity != null && crlEntity.getValue() != null) {
            if (crlEntity.getDate() == null) {
                return HOST + CRL_PATH + getCertStringUrl(crlEntity, EXTENDED_URL_PREFIX) + CRL_EXTENSION;
            } else {
                Date time = crlEntity.getDate().toGregorianCalendar().getTime();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-hh-mm");
                String date = sdf.format(time);
                if (crlEntity.isFutur() == null) {
                    return HOST + CRL_PATH + date + "/" + crlEntity.getValue() + CRL_EXTENSION;
                } else {
                    return HOST + CRL_PATH + date + "/" + crlEntity.isFutur() + "/" + crlEntity.getValue() + CRL_EXTENSION;
                }
            }
        }
        return null;
    }

    private String getOcspUrl(XmlEntityKey entityKey) {
        if (entityKey != null) {
            return HOST + OCSP_PATH + getCertStringUrl(entityKey, CUSTOM_URL_PREFIX);
        }
        return null;
    }

    private String getAiaUrl(XmlEntityKey entityKey) {
        if (entityKey != null) {
            return HOST + CRT_PATH + getCertStringUrl(entityKey, EMPTY_URL_PREFIX) + CRT_EXTENSION;
        }
        return null;
    }

    private String getCertStringUrl(XmlEntityKey entityKey, String urlPrefix) {
        return entityKey.getSerialNumber() != null ? urlPrefix + entityKey.getValue() + "/" + entityKey.getSerialNumber() : entityKey.getValue();
    }

    private X500Name getX500NameIssuer(Map<EntityId, X500Name> x500names, EntityId key) {
        if (x500names.containsKey(key)) {
            return x500names.get(key);
        }
        throw new IllegalStateException("EntityId not found : " + key);
    }

    /**
     * Initialize subject based on given subject/organization (optional.)/country (optional.)
     *
     * @param x500Names          a map between {@link EntityId} and {@link X500Name}
     * @throws IllegalStateException Common name is null
     */
    private X500Name getX500NameSubject(Map<EntityId, X500Name> x500Names, XmlCertificateType certType, EntityId entityId) {
        if (x500Names.containsKey(entityId)) {
            return x500Names.get(entityId);
        } else {
            if (certType.getSubject() == null) {
                throw new IllegalStateException("Missing common name for " + entityId);
            }

            String tmpCountry;
            if (!Utils.isStringEmpty(certType.getCountry())) {
                tmpCountry = certType.getCountry();
            } else {
                tmpCountry = country;
            }

            String tmpOrganisation;
            if (!Utils.isStringEmpty(certType.getOrganization())) {
                tmpOrganisation = certType.getOrganization();
            } else {
                tmpOrganisation = organisation;
            }

            X500Name x500Name = new X500NameBuilder().commonName(certType.getSubject()).pseudo(certType.getPseudo()).country(tmpCountry).organisation(tmpOrganisation).organisationUnit(organisationUnit).build();
            x500Names.put(entityId, x500Name);
            x500Names.put(new EntityId(certType.getSubject(), null), x500Name);
            return x500Name;
        }
    }

    private XmlCertificateType getIssuerCertificateType(Map<EntityId, XmlCertificateType> wrapperMap, XmlCertificateType certificateType, EntityId entityId) {
        XmlCertificateType issuerCertificate = wrapperMap.get(entityId);
        if (issuerCertificate == null) {
            issuerCertificate = certificateType; // self-issued certificate
        }
        return issuerCertificate;
    }

    private KeyPair getKeyPair(Map<EntityId, KeyPair> keyPairs, EntityId entityId) {
        return keyPairs.get(entityId);
    }

    private KeyPair buildKeyPair(Map<EntityId, KeyPair> keyPairs, EntityId entityId, XmlCertificateType certType) throws GeneralSecurityException {
        KeyPair keyPair = build(certType.getKeyAlgo(), certType.getDigestAlgo());
        keyPairs.put(entityId, keyPair);
        return keyPair;
    }

    private KeyPair build(XmlKeyAlgo algo, DigestAlgorithm digestAlgorithm) throws GeneralSecurityException {
        if (EncryptionAlgorithm.ECDSA.isEquivalent(algo.getEncryption())) {
            ECParameterSpec ecSpec = ECNamedCurveTable.getParameterSpec(getEllipticCurveName(algo));
            KeyPairGenerator generator = KeyPairGenerator.getInstance(algo.getEncryption().getName(), DSSSecurityProvider.getSecurityProvider());
            generator.initialize(ecSpec, new SecureRandom());
            return generator.generateKeyPair();
        } else if (EncryptionAlgorithm.EDDSA.isEquivalent(algo.getEncryption())) {
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.getAlgorithm(algo.getEncryption(), digestAlgorithm);
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(signatureAlgorithm.getJCEId(), DSSSecurityProvider.getSecurityProvider());
            return keyGenerator.generateKeyPair();
        } else {
            KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance(algo.getEncryption().getName(), DSSSecurityProvider.getSecurityProvider());
            Integer keySize = algo.getLength();
            if (keySize != null) {
                keyGenerator.initialize(keySize);
            }
            return keyGenerator.generateKeyPair();
        }
    }

    // TODO : define the curve in XML
    private String getEllipticCurveName(XmlKeyAlgo algo) {
        if (algo.getLength() != null) {
            return String.format("secp%sr1", algo.getLength());
        } else {
            return "prime256v1";
        }
    }

    private Date convert(XmlDateDefinitionType ddt) {
        if (ddt != null) {
            Calendar cal = Calendar.getInstance();
            if (ddt.getYear() != null) {
                cal.add(Calendar.YEAR, ddt.getYear());
            }
            if (ddt.getMonth() != null) {
                cal.add(Calendar.MONTH, ddt.getMonth());
            }
            if (ddt.getDay() != null) {
                cal.add(Calendar.DAY_OF_MONTH, ddt.getDay());
            }
            return cal.getTime();
        }
        return null;
    }

    private void saveEntity(JaxbCertEntityRepository repository, JAXBCertEntity certEntity, Map<EntityId, JAXBCertEntity> entities, EntityId key) {
        if (repository.save(certEntity)) {
            // TODO : evaluate why two times #put is called
            entities.put(key, certEntity);
            entities.put(new EntityId(certEntity.getSubject(), null), certEntity);
            LOG.info("Creation of '{}' : DONE. Certificate Id : '{}'", certEntity.getSubject(), certEntity.getCertificateToken().getDSSIdAsString());
        } else {
            LOG.warn("Unable to add cert entity '{}' to the database. Certificate Id: '{}'", certEntity.getSubject(), certEntity.getCertificateToken().getDSSIdAsString());
        }
    }

}
