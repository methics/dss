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
package eu.europa.esig.dss.validation.qwac;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.http.ResponseEnvelope;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.spi.DSSUtils;
import eu.europa.esig.dss.spi.client.http.AdvancedDataLoader;
import eu.europa.esig.dss.spi.client.http.NativeHTTPDataLoader;
import eu.europa.esig.dss.spi.client.http.Protocol;
import eu.europa.esig.dss.spi.signature.AdvancedSignature;
import eu.europa.esig.dss.spi.validation.CertificateVerifier;
import eu.europa.esig.dss.spi.validation.CertificateVerifierBuilder;
import eu.europa.esig.dss.spi.validation.ValidationContext;
import eu.europa.esig.dss.spi.x509.CommonCertificateSource;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.validation.AbstractCertificateValidator;
import eu.europa.esig.dss.validation.SignedDocumentValidator;
import eu.europa.esig.dss.validation.executor.certificate.CertificateProcessExecutor;
import eu.europa.esig.dss.validation.executor.certificate.qwac.QWACCertificateProcessExecutor;
import eu.europa.esig.dss.validation.reports.CertificateReports;
import eu.europa.esig.dss.validation.reports.diagnostic.DiagnosticDataBuilder;
import eu.europa.esig.dss.validation.reports.diagnostic.QWACCertificateDiagnosticDataBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.cert.Certificate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * This class performs a validation of a TLS/SSL certificate as per ETSI TS 119 411-5
 * "Policy and security requirements for Trust Service Providers issuing certificates;
 * Part 5: Implementation of qualified certificates for website authentication as in amended Regulation 910/2014"
 *
 */
public class QWACValidator extends AbstractCertificateValidator<CertificateReports, CertificateProcessExecutor> {

    private static final Logger LOG = LoggerFactory.getLogger(QWACValidator.class);

    /** The path for default QWAC validation policy */
    private static final String QWAC_VALIDATION_POLICY_LOCATION = "/policy/qwac-constraint.xml";

    /**
     * URL to verify the used TSL/SSL certificate
     */
    private final String url;

    /**
     * TSL/SSL certificate derived from the {@code url}
     */
    private CertificateToken certificateToken;

    /**
     * DataLoader used to establish the secure TLS/SSL connection with a remote server and/or
     * load any applicable information required for a QWAC validation
     */
    private AdvancedDataLoader dataLoader;

    /**
     * Default constructor to instantiate a TSL/SSL certificate validation for the given URL
     *
     * @param url {@link String}
     */
    private QWACValidator(final String url) {
        this(url, null);
    }

    /**
     * Constructor to instantiate a certificate token validation whether
     * its suitable as a TSL/SSL certificate for the given URL.
     *
     * @param url {@link String}
     */
    private QWACValidator(final String url, final CertificateToken certificateToken) {
        Objects.requireNonNull(url, "URL cannot be null!");
        this.url = url;
        this.certificateToken = certificateToken;
    }

    /**
     * Gets the data loader to be used for accessing the information from remote sources.
     * If not defined with a setter, the method instantiates a {@code NativeHTTPDataLoader} by default.
     *
     * @return {@link AdvancedDataLoader}
     */
    protected AdvancedDataLoader getDataLoader() {
        if (dataLoader == null) {
            LOG.info("NativeHTTPDataLoader is instantiated for QWACValidator execution.");
            dataLoader = new NativeHTTPDataLoader();
        }
        return dataLoader;
    }

    /**
     * Sets a data loader used to establish TLS/SSL connection and retrieve any related information over.
     * If not set, a default instance of {@code NativeHTTPDataLoader} will be used for remote calls, if any.
     *
     * @param dataLoader {@link AdvancedDataLoader}
     */
    public void setDataLoader(AdvancedDataLoader dataLoader) {
        this.dataLoader = dataLoader;
    }

    /**
     * Instantiates a new QWAC Validator to verify the TSL/SSL certificate from the specified {@code url}.
     * When loaded with this method, QWACValidator will perform a request to the remote {@code url} to retrieve
     * the actual TSL/SSL certificate and perform its validation.
     *
     * @param url {@link String} to validate a used TSL/SSL certificate from
     * @return {@link QWACValidator}
     */
    public static QWACValidator fromUrl(final String url) {
        return new QWACValidator(url);
    }

    /**
     * Instantiates a new QWAC Validator to verify the provided TSL/SSL {@code certificateToken} against the specified {@code url}.
     * When loaded with this method, QWACValidator will validate the provided {@code certificateToken} whether
     * it can be used as a QWAC TSL/SSL certificate for the {@code url}.
     *
     * @param url {@link String} to validate the TSL/SSL certificate against
     * @param certificateToken {@link CertificateToken} representing a TSL/SSL certificate to be validated
     * @return {@link QWACValidator}
     */
    public static QWACValidator fromUrlAndCertificate(final String url, final CertificateToken certificateToken) {
        return new QWACValidator(url, certificateToken);
    }

    @Override
    protected DiagnosticDataBuilder prepareDiagnosticDataBuilder() {
        ResponseEnvelope response = connectToUrl();
        assertResponseValid(response);

        List<CertificateToken> tlsCertificates = toCertificateTokenList(response.getTLSCertificates());
        String tlsCertificateBindingUrl = readTLSCertificateBindingUrl(response);
        SignedDocumentValidator signedDocumentValidator = initSignedDocumentValidator(tlsCertificateBindingUrl, tlsCertificates);
        AdvancedSignature signature = getTLSCertificateBindingSignature(signedDocumentValidator);

        CertificateToken tlsCertificate = getTLSCertificate(tlsCertificates);
        final ValidationContext validationContext = prepareValidationContext(tlsCertificate, tlsCertificates, signature);
        validateContext(validationContext);
        return createDiagnosticDataBuilder(validationContext, signedDocumentValidator, tlsCertificate, tlsCertificateBindingUrl, signature);
    }

    /**
     * Connects to the {@code url}
     *
     * @return {@link ResponseEnvelope} containing metadata and context received from the server
     */
    protected ResponseEnvelope connectToUrl() {
        final String trimmedUrl = Utils.trim(url);
        if (Protocol.isHttpUrl(trimmedUrl)) {
            return getDataLoader().requestGet(url, true, false);
        }
        throw new UnsupportedOperationException(String.format(
                "DSS framework supports only HTTP(S) certificate extraction. Obtained URL : '%s'", url));
    }

    /**
     * This method is used to prepare a {@code ValidationContext} using the configuration and provided data objects
     *
     * @param tlsCertificate {@link CertificateToken} used to establish the secure TLS/SSL connection
     * @param otherTlsCertificates a list of TLS/SSL {@link CertificateToken}s returned by a server
     * @param signature {@link AdvancedSignature} TLS Certificate Binding signature, when present
     * @return {@link ValidationContext}
     */
    protected ValidationContext prepareValidationContext(CertificateToken tlsCertificate, List<CertificateToken> otherTlsCertificates,
                                                         AdvancedSignature signature) {
        final CertificateVerifier certificateVerifierForValidation = getOfflineCertificateVerifier();

        ValidationContext validationContext = super.prepareValidationContext(certificateVerifierForValidation);
        validationContext.addCertificateTokenForVerification(tlsCertificate);

        CommonCertificateSource adjunctCertificateSource = new CommonCertificateSource();
        for (CertificateToken certificate : otherTlsCertificates) {
            adjunctCertificateSource.addCertificate(certificate);
        }
        certificateVerifierForValidation.addAdjunctCertSources(adjunctCertificateSource);

        if (signature != null) {
            validationContext.addSignatureForVerification(signature);
        }

        return validationContext;
    }

    private CertificateToken getTLSCertificate(List<CertificateToken> tlsCertificates) {
        if (certificateToken != null) {
            return certificateToken;
        } else if (Utils.isCollectionNotEmpty(tlsCertificates)) {
            // first certificate shall be the peer's end-entity certificate
            certificateToken = tlsCertificates.iterator().next();
            return certificateToken;
        } else {
            throw new DSSException("No valid TLS/SSL certificates have been obtained from the URL '{}'.");
        }
    }

    private SignedDocumentValidator initSignedDocumentValidator(String tlsCertificateBindingUrl, List<CertificateToken> tlsCertificates) {
        if (tlsCertificateBindingUrl != null) {
            byte[] tlsCertificateBindingSignatureBytes = getDataLoader().get(tlsCertificateBindingUrl);
            if (tlsCertificateBindingSignatureBytes != null) {
                DSSDocument signatureDocument = new InMemoryDocument(tlsCertificateBindingSignatureBytes, tlsCertificateBindingUrl);
                SignedDocumentValidator documentValidator = SignedDocumentValidator.fromDocument(signatureDocument);
                documentValidator.setCertificateVerifier(getOfflineCertificateVerifier());
                documentValidator.setDetachedContents(toDetachedDocumentsList(tlsCertificates));
                return documentValidator;
            }
        }
        return null;
    }

    private AdvancedSignature getTLSCertificateBindingSignature(SignedDocumentValidator signedDocumentValidator) {
        if (signedDocumentValidator != null) {
            List<AdvancedSignature> signatures = signedDocumentValidator.getSignatures();
            if (signatures.size() == 1) {
                return signatures.get(0);
            } else {
                LOG.warn("Only one signature is expected within the TLS Certificate Binding URL. Obtained : {}.", signatures.size());
            }
        }
        return null;
    }

    private List<CertificateToken> toCertificateTokenList(Certificate[] certificates) {
        if (Utils.isArrayEmpty(certificates)) {
            return Collections.emptyList();
        }
        return Arrays.stream(certificates).map(this::toCertificateToken).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private CertificateToken toCertificateToken(Certificate certificate) {
        try {
            return DSSUtils.loadCertificate(certificate.getEncoded());
        } catch (Exception e) {
            LOG.warn("Unable to load certificate : {}. The entry is skipped.", e.getMessage(), e);
            return null;
        }
    }

    private List<DSSDocument> toDetachedDocumentsList(List<CertificateToken> certificates) {
        return certificates.stream().map(this::toDSSDocument).filter(Objects::nonNull).collect(Collectors.toList());
    }

    private DSSDocument toDSSDocument(CertificateToken certificate) {
        try {
            return new InMemoryDocument(certificate.getEncoded(), identifierProvider.getIdAsString(certificate));
        } catch (Exception e) {
            LOG.warn("Unable to load certificate : {}. The entry is skipped.", e.getMessage(), e);
            return null;
        }
    }

    /**
     * This method reads the HTTP response headers and extracts the value of the "Link" header
     * with a "rel" value of "tls-certificate-binding".
     *
     * @param responseEnvelope {@code ResponseEnvelope} to process
     * @return {@link System} TSL Certificate Binding URL, when present
     */
    protected String readTLSCertificateBindingUrl(ResponseEnvelope responseEnvelope) {
        return QWACUtils.getTLSCertificateBindingUrl(responseEnvelope.getHeaders());
    }

    /**
     * Verifies whether the {@code response} is valid and contains the information required
     * to continue the QWAC validation process
     *
     * @param response {@link ResponseEnvelope}
     */
    protected void assertResponseValid(ResponseEnvelope response) {
        if (certificateToken == null && Utils.isArrayEmpty(response.getTLSCertificates())) {
            throw new IllegalArgumentException(String.format("No TSL/SSL certificates have been returned from the URL '%s'. " +
                    "Please ensure the URL is valid and uses the HTTP(S) scheme, or provide the TLS/SSL certificate on validation explicitly.", url));
        }
    }

    /**
     * Creates and configures a new {@code DiagnosticDataBuilder}
     *
     * @param validationContext {@link ValidationContext}
     * @param signedDocumentValidator {@link SignedDocumentValidator} used to validate a signature
     * @param tlsCertificate {@link CertificateToken} used to establish a TLS/SSL connection
     * @param tlsCertificateBindingUrl {@link String} TLS Certificate Binding URL, when present
     * @param signature {@link AdvancedSignature} TLS Certificate Binding signature, when present
     * @return {@link DiagnosticDataBuilder}
     */
    protected DiagnosticDataBuilder createDiagnosticDataBuilder(
            ValidationContext validationContext, SignedDocumentValidator signedDocumentValidator,
            CertificateToken tlsCertificate, String tlsCertificateBindingUrl, AdvancedSignature signature) {
        QWACCertificateDiagnosticDataBuilder diagnosticDataBuilder =
                (QWACCertificateDiagnosticDataBuilder) super.createDiagnosticDataBuilder(validationContext);

        diagnosticDataBuilder = diagnosticDataBuilder
                .websiteUrl(url)
                .tlsCertificate(tlsCertificate)
                .tlsCertificateBindingUrl(tlsCertificateBindingUrl)
                .tlsCertificateBindingSignature(signature);

        if (signedDocumentValidator != null) {
            diagnosticDataBuilder = diagnosticDataBuilder
                    .setSignatureDiagnosticDataBuilder(signedDocumentValidator.initializeDiagnosticDataBuilder());
        }

        return diagnosticDataBuilder
                .foundSignatures(validationContext.getProcessedSignatures())
                .documentCertificateSource(validationContext.getDocumentCertificateSource());
    }

    /**
     * Builds a complete copy of the CertificateVerifier for the offline validation
     *
     * @return {@link CertificateVerifier}
     */
    protected CertificateVerifier getOfflineCertificateVerifier() {
        return new CertificateVerifierBuilder(certificateVerifier).buildCompleteCopyForValidation();
    }

    @Override
    protected DiagnosticDataBuilder initDiagnosticDataBuilder() {
        return new QWACCertificateDiagnosticDataBuilder();
    }

    @Override
    protected String getDefaultValidationPolicyPath() {
        return QWAC_VALIDATION_POLICY_LOCATION;
    }

    @Override
    protected CertificateProcessExecutor provideProcessExecutorInstance() {
        if (processExecutor == null) {
            processExecutor = getDefaultProcessExecutor();
        }
        if (certificateToken != null) {
            processExecutor.setCertificateId(identifierProvider.getIdAsString(certificateToken));
        }
        return processExecutor;
    }

    @Override
    public CertificateProcessExecutor getDefaultProcessExecutor() {
        return new QWACCertificateProcessExecutor();
    }

}
