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
package eu.europa.esig.dss.extension;

import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.SignatureLevel;
import eu.europa.esig.dss.enumerations.SignatureProfile;
import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.model.SerializableSignatureParameters;
import eu.europa.esig.dss.model.TimestampParameters;
import eu.europa.esig.dss.signature.AbstractSignatureParameters;
import eu.europa.esig.dss.signature.DocumentSignatureService;
import eu.europa.esig.dss.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * This class provides an abstract implementation of the {@code eu.europa.esig.dss.spi.augmentation.DocumentExtender}
 *
 * @param <SP> {@code SerializableSignatureParameters} specifying the signature creation parameters
 * @param <TP> {@code SerializableTimestampParameters} specifying the timestamp creation parameters, when applicable
 */
public abstract class AbstractDocumentExtender<SP extends AbstractSignatureParameters<?>, TP extends TimestampParameters> extends SignedDocumentExtender {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractDocumentExtender.class);

    /** Internal variable used to define empty format specific signature parameters */
    private static final SerializableSignatureParameters[] EMPTY_PARAMETERS = new SerializableSignatureParameters[]{};

    /**
     * The document to be augmented (with the signatures)
     */
    protected DSSDocument document;

    /**
     * Empty constructor
     */
    protected AbstractDocumentExtender() {
        // empty
    }

    @Override
    public DSSDocument extendDocument(SignatureProfile signatureProfile) {
        return extendDocument(signatureProfile, Collections.emptyList());
    }

    @Override
    public DSSDocument extendDocument(SignatureProfile signatureProfile, List<DSSDocument> detachedContents) {
        return extendDocument(signatureProfile, detachedContents, EMPTY_PARAMETERS);
    }

    @Override
    public DSSDocument extendDocument(SignatureProfile signatureProfile, SerializableSignatureParameters... extensionParameters) {
        return extendDocument(signatureProfile, Collections.emptyList(), extensionParameters);
    }

    @Override
    public DSSDocument extendDocument(SignatureProfile signatureProfile, List<DSSDocument> detachedContents,
                                      SerializableSignatureParameters... extensionParameters) {
        Objects.requireNonNull(document, "Document is not provided to the extender");
        Objects.requireNonNull(signatureProfile, "SignatureProfile cannot be null!");

        DocumentSignatureService<SP, TP> service = initSignatureService();
        SP parameters = initSignatureParameters(signatureProfile, detachedContents, extensionParameters);
        return service.extendDocument(document, parameters);
    }

    /**
     * This method initializes a new {@code DocumentSignatureService}
     *
     * @return {@link DocumentSignatureService}
     */
    @SuppressWarnings("unchecked")
    protected DocumentSignatureService<SP, TP> initSignatureService() {
        if (Utils.isArrayNotEmpty(services)) {
            for (DocumentSignatureService<?, ?> service : services) {
                if (isSupportedService(service)) {
                    return (DocumentSignatureService<SP, TP>) service;
                }
            }
        }
        return createSignatureService();
    }

    /**
     * This method created a new instance of {@code DocumentSignatureService}
     *
     * @return {@link DocumentSignatureService}
     */
    protected abstract DocumentSignatureService<SP, TP> createSignatureService();

    /**
     * This method verifies whether the provided document signature service is supported by the current implementation
     *
     * @param service {@link DocumentSignatureService} to check
     * @return TRUE if the service is supported by the current implementation, FALSE otherwise
     */
    protected abstract boolean isSupportedService(DocumentSignatureService<?, ?> service);

    /**
     * This method initializes signature parameters to be used on the signature augmentation
     *
     * @param signatureProfile {@link SignatureProfile} representing a target level on signature augmentation
     * @param detachedContents a list of {@link DSSDocument} for a detached signature
     * @param extensionParameters array of format specific {@code SerializableSignatureParameters}, when applicable
     * @return {@link SerializableSignatureParameters}
     */
    protected SP initSignatureParameters(SignatureProfile signatureProfile, List<DSSDocument> detachedContents,
                                         SerializableSignatureParameters... extensionParameters) {
        SP signatureParameters = getFromProvidedParameters(extensionParameters);
        return fillSignatureParameters(signatureParameters, signatureProfile, detachedContents);
    }

    @SuppressWarnings("unchecked")
    private SP getFromProvidedParameters(SerializableSignatureParameters... explicitParameters) {
        if (Utils.isArrayNotEmpty(explicitParameters)) {
            for (SerializableSignatureParameters parameters : explicitParameters) {
                if (isSupportedParameters(parameters)) {
                    return (SP) parameters;
                }
            }
        }
        return emptySignatureParameters();
    }

    /**
     * This method returns a new instance of empty signature parameters, according to the given format implementation
     *
     * @return {@link SerializableSignatureParameters}
     */
    protected abstract SP emptySignatureParameters();

    /**
     * This method verifies whether the provided signature parameters are supported by the current implementation
     *
     * @param parameters {@link SerializableSignatureParameters} to check
     * @return TRUE if the parameters are supported by the current implementation, FALSE otherwise
     */
    protected abstract boolean isSupportedParameters(SerializableSignatureParameters parameters);

    /**
     * This method fills the {@code signatureParameters} with the parameters from the {@code augmentationParameters}
     *
     * @param signatureParameters {@link SerializableSignatureParameters} to fill
     * @param signatureProfile {@link SignatureProfile} representing a target level on signature augmentation
     * @param detachedContents a list of {@link DSSDocument} for a detached signature
     * @return {@link SerializableSignatureParameters}
     */
    protected SP fillSignatureParameters(SP signatureParameters, SignatureProfile signatureProfile, List<DSSDocument> detachedContents) {
        if (signatureParameters.getSignatureLevel() == null) {
            signatureParameters.setSignatureLevel(getSignatureLevel(signatureProfile));
        } else if (signatureProfile != signatureParameters.getSignatureLevel().getSignatureProfile()) {
            LOG.info("Signature level '{}' defined within the applicable signature parameters does not match the target signature profile '{}'. " +
                    "The signature level from signature parameters is used.", signatureParameters.getSignatureLevel(), signatureProfile);
        }
        if (Utils.isCollectionEmpty(signatureParameters.getDetachedContents())) {
            signatureParameters.setDetachedContents(detachedContents);
        }
        return signatureParameters;
    }

    /**
     * Gets the target {@code SignatureLevel} for the given {@code SignatureProfile} relatively to the signature format
     *
     * @param signatureProfile {@link SignatureProfile}
     * @return {@link SignatureLevel}
     */
    protected SignatureLevel getSignatureLevel(SignatureProfile signatureProfile) {
        Objects.requireNonNull(signatureProfile, "SignatureProfile cannot be null!");
        SignatureForm signatureForm = getSignatureForm();
        SignatureLevel signatureLevel = SignatureLevel.getSignatureLevel(signatureForm, signatureProfile);
        if (signatureLevel == null) {
            throw new IllegalArgumentException(String.format("No SignatureLevel found for the given " +
                    "SignatureForm '%s' and SignatureProfile '%s'.", signatureForm.name(), signatureProfile.name()));
        }
        return signatureLevel;
    }

}
