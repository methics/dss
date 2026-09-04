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
package eu.europa.esig.dss.ws.signature.common;

import eu.europa.esig.dss.asic.cades.ASiCWithCAdESSignatureParameters;
import eu.europa.esig.dss.asic.cades.ASiCWithCAdESTimestampParameters;
import eu.europa.esig.dss.asic.xades.ASiCWithXAdESSignatureParameters;
import eu.europa.esig.dss.cades.CAdESSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESCounterSignatureParameters;
import eu.europa.esig.dss.cades.signature.CAdESTimestampParameters;
import eu.europa.esig.dss.cbades.signature.CBAdESCounterSignatureParameters;
import eu.europa.esig.dss.cbades.signature.CBAdESTimestampParameters;
import eu.europa.esig.dss.enumerations.ASiCContainerType;
import eu.europa.esig.dss.enumerations.CommitmentType;
import eu.europa.esig.dss.enumerations.CommitmentTypeEnum;
import eu.europa.esig.dss.enumerations.JWSSerializationType;
import eu.europa.esig.dss.enumerations.SignatureForm;
import eu.europa.esig.dss.enumerations.TimestampContainerForm;
import eu.europa.esig.dss.jades.JAdESSignatureParameters;
import eu.europa.esig.dss.jades.JAdESTimestampParameters;
import eu.europa.esig.dss.jades.signature.JAdESCounterSignatureParameters;
import eu.europa.esig.dss.model.BLevelParameters;
import eu.europa.esig.dss.model.InMemoryDocument;
import eu.europa.esig.dss.model.Policy;
import eu.europa.esig.dss.model.SerializableCounterSignatureParameters;
import eu.europa.esig.dss.model.SerializableSignatureParameters;
import eu.europa.esig.dss.model.SignatureValue;
import eu.europa.esig.dss.model.SignerLocation;
import eu.europa.esig.dss.model.TimestampParameters;
import eu.europa.esig.dss.model.x509.CertificateToken;
import eu.europa.esig.dss.pades.DSSFileFont;
import eu.europa.esig.dss.pades.DSSFont;
import eu.europa.esig.dss.pades.PAdESSignatureParameters;
import eu.europa.esig.dss.pades.PAdESTimestampParameters;
import eu.europa.esig.dss.pades.SignatureFieldParameters;
import eu.europa.esig.dss.pades.SignatureImageParameters;
import eu.europa.esig.dss.pades.SignatureImageTextParameters;
import eu.europa.esig.dss.signature.AbstractSignatureParameters;
import eu.europa.esig.dss.utils.Utils;
import eu.europa.esig.dss.ws.converter.ColorConverter;
import eu.europa.esig.dss.ws.converter.RemoteCertificateConverter;
import eu.europa.esig.dss.ws.converter.RemoteDocumentConverter;
import eu.europa.esig.dss.ws.dto.RemoteCertificate;
import eu.europa.esig.dss.ws.dto.SignatureValueDTO;
import eu.europa.esig.dss.ws.signature.dto.parameters.RemoteBLevelParameters;
import eu.europa.esig.dss.ws.signature.dto.parameters.RemoteSignatureFieldParameters;
import eu.europa.esig.dss.ws.signature.dto.parameters.RemoteSignatureImageParameters;
import eu.europa.esig.dss.ws.signature.dto.parameters.RemoteSignatureImageTextParameters;
import eu.europa.esig.dss.ws.signature.dto.parameters.RemoteSignatureParameters;
import eu.europa.esig.dss.ws.signature.dto.parameters.RemoteTimestampParameters;
import eu.europa.esig.dss.xades.XAdESSignatureParameters;
import eu.europa.esig.dss.xades.XAdESTimestampParameters;
import eu.europa.esig.dss.xades.signature.XAdESCounterSignatureParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * The abstract remote signature service
 */
public abstract class AbstractRemoteSignatureServiceImpl {

	private static final Logger LOG = LoggerFactory.getLogger(AbstractRemoteSignatureServiceImpl.class);

	/**
	 * Default constructor
	 */
	protected AbstractRemoteSignatureServiceImpl() {
		// empty
	}

	/**
	 * Gets the ASiC Signature Parameters
	 *
	 * @param asicContainerType {@link ASiCContainerType}
	 * @param signatureForm {@link SignatureForm}
	 * @return {@link SerializableSignatureParameters}
	 */
	protected SerializableSignatureParameters getASiCSignatureParameters(ASiCContainerType asicContainerType,
			SignatureForm signatureForm) {
		switch (signatureForm) {
		case CAdES:
			ASiCWithCAdESSignatureParameters asicWithCAdESParameters = new ASiCWithCAdESSignatureParameters();
			asicWithCAdESParameters.aSiC().setContainerType(asicContainerType);
			return asicWithCAdESParameters;
		case XAdES:
			ASiCWithXAdESSignatureParameters asicWithXAdESParameters = new ASiCWithXAdESSignatureParameters();
			asicWithXAdESParameters.aSiC().setContainerType(asicContainerType);
			return asicWithXAdESParameters;
		default:
			throw new UnsupportedOperationException("Unrecognized format (only XAdES or CAdES are allowed with ASiC) : " + signatureForm);
		}
	}

	/**
	 * Creates {@code SerializableSignatureParameters} from {@code RemoteSignatureParameters}
	 *
	 * @param remoteParameters {@link RemoteSignatureParameters}
	 * @return {@link SerializableSignatureParameters}
	 */
	protected SerializableSignatureParameters createParameters(RemoteSignatureParameters remoteParameters) {
		SerializableSignatureParameters parameters;
		ASiCContainerType asicContainerType = remoteParameters.getAsicContainerType();
		SignatureForm signatureForm = remoteParameters.getSignatureLevel().getSignatureForm();
		if (asicContainerType != null) {
			parameters = getASiCSignatureParameters(asicContainerType, signatureForm);
		} else {
			parameters = getSignatureParameters(signatureForm, remoteParameters);
		}
		fillParameters(parameters, remoteParameters, signatureForm);
		return parameters;
	}

	/**
	 * Creates parameters for a signature creation (not container)
	 *
	 * @param signatureForm {@link SignatureForm}
	 * @param remoteParameters {@link RemoteSignatureParameters}
	 * @return {@link SerializableSignatureParameters}
	 */
	protected SerializableSignatureParameters getSignatureParameters(SignatureForm signatureForm, RemoteSignatureParameters remoteParameters) {
		switch (signatureForm) {
			case XAdES:
				return getXAdESSignatureParameters(remoteParameters);
			case CAdES:
				return new CAdESSignatureParameters();
			case PAdES:
				return getPAdESSignatureParameters(remoteParameters);
			case JAdES:
				return getJAdESSignatureParameters(remoteParameters);
			case CBAdES:
				return getCBAdESSignatureParameters(remoteParameters);
			default:
				throw new UnsupportedOperationException("Unsupported signature form : " + signatureForm);
		}
	}

	/**
	 * Creates parameters for a signature extension
	 *
	 * @param signatureForm {@link SignatureForm}
	 * @param remoteParameters {@link RemoteSignatureParameters}
	 * @return {@link SerializableSignatureParameters}
	 */
	protected SerializableSignatureParameters getExtensionParameters(SignatureForm signatureForm, RemoteSignatureParameters remoteParameters) {
		SerializableSignatureParameters parameters = getSignatureParameters(signatureForm, remoteParameters);
		if (SignatureForm.JAdES == signatureForm) {
			JAdESSignatureParameters jadesParameters = (JAdESSignatureParameters) parameters;
			if (remoteParameters.getJwsSerializationType() == null) {
				LOG.info("No JAdES related signature parameters found within the configuration. " +
						"Fallback to '{}' JwsSerializationType", JWSSerializationType.JSON_SERIALIZATION);
				jadesParameters.setJwsSerializationType(JWSSerializationType.JSON_SERIALIZATION);
			}
		}
		return parameters;
	}

	/**
	 * Gets XAdES signature parameters
	 *
	 * @param remoteParameters {@link RemoteSignatureParameters}
	 * @return {@link SerializableSignatureParameters}
	 */
	protected SerializableSignatureParameters getXAdESSignatureParameters(RemoteSignatureParameters remoteParameters) {
		XAdESSignatureParameters xadesParams = new XAdESSignatureParameters();
		xadesParams.setEmbedXML(remoteParameters.isEmbedXML());
		xadesParams.setManifestSignature(remoteParameters.isManifestSignature());
		return xadesParams;
	}

	/**
	 * Gets PAdES signature parameters
	 *
	 * @param remoteParameters {@link RemoteSignatureParameters}
	 * @return {@link SerializableSignatureParameters}
	 */
	protected SerializableSignatureParameters getPAdESSignatureParameters(RemoteSignatureParameters remoteParameters) {
		PAdESSignatureParameters padesParams = new PAdESSignatureParameters();
		padesParams.setContentSize(9472 * 2); // double reserved space for signature
		padesParams.setImageParameters(toImageParameters(remoteParameters.getImageParameters()));
		return padesParams;
	}
	
	/**
	 * Return {@code SerializableCounterSignatureParameters} in order to support
	 * counter signature
	 *
	 * @param remoteParameters {@link RemoteSignatureParameters}
	 * @return {@link SerializableCounterSignatureParameters}
	 */
	protected SerializableCounterSignatureParameters getJAdESSignatureParameters(
			RemoteSignatureParameters remoteParameters) {
		JAdESCounterSignatureParameters jadesParameters = new JAdESCounterSignatureParameters();
		if (remoteParameters.getJwsSerializationType() != null) {
			jadesParameters.setJwsSerializationType(remoteParameters.getJwsSerializationType());
		}
		if (remoteParameters.getSigDMechanism() != null) {
			jadesParameters.setSigDMechanism(remoteParameters.getSigDMechanism());
		}
		if (remoteParameters.isBase64UrlEncodedPayload() != null) {
			jadesParameters.setBase64UrlEncodedPayload(remoteParameters.isBase64UrlEncodedPayload());
		}
		if (remoteParameters.isBase64UrlEncodedEtsiUComponents() != null) {
			jadesParameters.setBase64UrlEncodedEtsiUComponents(remoteParameters.isBase64UrlEncodedEtsiUComponents());
		}
		if (remoteParameters.getSignatureType() != null) {
			jadesParameters.setSignatureType(remoteParameters.getSignatureType());
		}
		if (remoteParameters.getKeyIdentifier() != null) {
			jadesParameters.setKeyIdentifier(remoteParameters.getKeyIdentifier());
		}
		if (remoteParameters.getX509Url() != null) {
			jadesParameters.setX509Url(remoteParameters.getX509Url());
		}
		return jadesParameters;
	}

	/**
	 * Return {@code SerializableCounterSignatureParameters} in order to support
	 * counter signature
	 *
	 * @param remoteParameters {@link RemoteSignatureParameters}
	 * @return {@link SerializableCounterSignatureParameters}
	 */
	protected SerializableCounterSignatureParameters getCBAdESSignatureParameters(
			RemoteSignatureParameters remoteParameters) {
		CBAdESCounterSignatureParameters cbadesParameters = new CBAdESCounterSignatureParameters();
		if (remoteParameters.getCoseStructureType() != null) {
			cbadesParameters.setCoseStructureType(remoteParameters.getCoseStructureType());
		}
		if (remoteParameters.getTagged() != null) {
			cbadesParameters.setTagged(remoteParameters.getTagged());
		}
		if (remoteParameters.getExternallySuppliedData() != null) {
			cbadesParameters.setExternallySuppliedData(RemoteDocumentConverter.toDSSDocument(remoteParameters.getExternallySuppliedData()));
		}
		if (remoteParameters.getSigDMechanism() != null) {
			cbadesParameters.setSigDMechanism(remoteParameters.getSigDMechanism());
		}
		if (remoteParameters.getSignatureType() != null) {
			cbadesParameters.setSignatureType(remoteParameters.getSignatureType());
		}
		if (remoteParameters.getKeyIdentifier() != null) {
			cbadesParameters.setKeyIdentifier(remoteParameters.getKeyIdentifier().getBytes());
		}
		if (remoteParameters.getX509Url() != null) {
			cbadesParameters.setX509Url(remoteParameters.getX509Url());
		}
		return cbadesParameters;
	}

	/**
	 * Fills the parameters
	 *
	 * @param signatureParameters {@link SerializableSignatureParameters} to fill
	 * @param remoteParameters {@link RemoteSignatureParameters} to get values from
	 * @param signatureForm {@link SignatureForm}
	 */
	@SuppressWarnings("unchecked")
	protected void fillParameters(SerializableSignatureParameters signatureParameters,
								  RemoteSignatureParameters remoteParameters, SignatureForm signatureForm) {
		if (!(signatureParameters instanceof AbstractSignatureParameters<?>)) {
			return;
		}

		AbstractSignatureParameters<TimestampParameters> parameters =
				(AbstractSignatureParameters<TimestampParameters>) signatureParameters;
		// certificate shall be provided first
		RemoteCertificate signingCertificate = remoteParameters.getSigningCertificate();
		if (signingCertificate != null) { // extends do not require signing certificate
			CertificateToken certificateToken = RemoteCertificateConverter.toCertificateToken(signingCertificate);
			parameters.setSigningCertificate(certificateToken);
		}

		List<RemoteCertificate> remoteCertificateChain = remoteParameters.getCertificateChain();
		if (Utils.isCollectionNotEmpty(remoteCertificateChain)) {
			parameters.setCertificateChain(RemoteCertificateConverter.toCertificateTokens(remoteCertificateChain));
		}

		parameters.setBLevelParams(toBLevelParameters(remoteParameters.getBLevelParams()));
		parameters.setDetachedContents(RemoteDocumentConverter.toDSSDocuments(remoteParameters.getDetachedContents()));

		if (remoteParameters.getDigestAlgorithm() != null) {
			parameters.setDigestAlgorithm(remoteParameters.getDigestAlgorithm());
		}
		if (remoteParameters.getEncryptionAlgorithm() != null) {
			parameters.setEncryptionAlgorithm(remoteParameters.getEncryptionAlgorithm());
		}
		if (remoteParameters.getReferenceDigestAlgorithm() != null) {
			parameters.setReferenceDigestAlgorithm(remoteParameters.getReferenceDigestAlgorithm());
		}

		if (remoteParameters.getSignatureLevel() != null) {
			parameters.setSignatureLevel(remoteParameters.getSignatureLevel());
		}
		if (remoteParameters.getSignaturePackaging() != null) {
			parameters.setSignaturePackaging(remoteParameters.getSignaturePackaging());
		}
		if (remoteParameters.getContentTimestamps() != null) {
			parameters.setContentTimestamps(TimestampTokenConverter.toTimestampTokens(remoteParameters.getContentTimestamps()));
		}
		if (signatureForm != null) {
			parameters.setSignatureTimestampParameters(toTimestampParameters(remoteParameters.getSignatureTimestampParameters(),
					signatureForm, remoteParameters.getAsicContainerType()));
			parameters.setArchiveTimestampParameters(toTimestampParameters(remoteParameters.getArchiveTimestampParameters(),
					signatureForm, remoteParameters.getAsicContainerType()));
			parameters.setContentTimestampParameters(toTimestampParameters(remoteParameters.getContentTimestampParameters(),
					signatureForm, remoteParameters.getAsicContainerType()));
		}
		parameters.setGenerateTBSWithoutCertificate(remoteParameters.isGenerateTBSWithoutCertificate());
	}

	/**
	 * Converts {@code RemoteBLevelParameters} to {@code BLevelParameters}
	 *
	 * @param remoteBLevelParameters {@link RemoteBLevelParameters}
	 * @return {@link BLevelParameters}
	 */
	protected BLevelParameters toBLevelParameters(RemoteBLevelParameters remoteBLevelParameters) {
		BLevelParameters bLevelParameters = new BLevelParameters();
		bLevelParameters.setClaimedSignerRoles(remoteBLevelParameters.getClaimedSignerRoles());
		bLevelParameters.setSignedAssertions(remoteBLevelParameters.getSignedAssertions());
		if (remoteBLevelParameters.getCommitmentTypeIndications() != null) {
			bLevelParameters.setCommitmentTypeIndications(toCommitmentTypeList(remoteBLevelParameters.getCommitmentTypeIndications()));
		}
		if (remoteBLevelParameters.getSigningDate() != null) {
			bLevelParameters.setSigningDate(remoteBLevelParameters.getSigningDate());
		}
		bLevelParameters.setTrustAnchorBPPolicy(remoteBLevelParameters.isTrustAnchorBPPolicy());
		
		Policy policy = new Policy();
		policy.setDescription(remoteBLevelParameters.getPolicyDescription());
		policy.setDigestAlgorithm(remoteBLevelParameters.getPolicyDigestAlgorithm());
		policy.setDigestValue(remoteBLevelParameters.getPolicyDigestValue());
		policy.setId(remoteBLevelParameters.getPolicyId());
		policy.setQualifier(remoteBLevelParameters.getPolicyQualifier());
		policy.setSpuri(remoteBLevelParameters.getPolicySpuri());
		if (!policy.isEmpty()) {
			bLevelParameters.setSignaturePolicy(policy);
		}
		
		SignerLocation signerLocation = new SignerLocation();
		signerLocation.setCountry(remoteBLevelParameters.getSignerLocationCountry());
		signerLocation.setLocality(remoteBLevelParameters.getSignerLocationLocality());
		signerLocation.setPostalAddress(remoteBLevelParameters.getSignerLocationPostalAddress());
		signerLocation.setPostalCode(remoteBLevelParameters.getSignerLocationPostalCode());
		signerLocation.setStateOrProvince(remoteBLevelParameters.getSignerLocationStateOrProvince());
		signerLocation.setStreetAddress(remoteBLevelParameters.getSignerLocationStreet());
		if (!signerLocation.isEmpty()) {
			bLevelParameters.setSignerLocation(signerLocation);
		}
		
		return bLevelParameters;
	}

	/**
	 * Transforms {@code RemoteTimestampParameters} to {@code TimestampParameters}
	 *
	 * @param remoteTimestampParameters {@link RemoteTimestampParameters}
	 * @return {@link TimestampParameters}
	 */
	protected TimestampParameters toTimestampParameters(RemoteTimestampParameters remoteTimestampParameters) {
		Objects.requireNonNull(remoteTimestampParameters.getTimestampContainerForm(), "Timestamp container form is not defined!");
		TimestampContainerForm timestampForm = remoteTimestampParameters.getTimestampContainerForm();
		switch (timestampForm) {
			case PDF:
				return toTimestampParameters(remoteTimestampParameters, SignatureForm.PAdES, null);
			case ASiC_E:
				return toTimestampParameters(remoteTimestampParameters, SignatureForm.CAdES, ASiCContainerType.ASiC_E);
			case ASiC_S:
				return toTimestampParameters(remoteTimestampParameters, SignatureForm.CAdES, ASiCContainerType.ASiC_S);
			default:
				throw new UnsupportedOperationException(String.format("Unsupported timestamp container form [%s]", timestampForm.getReadable()));
		}
	}

	/**
	 * Transforms ASiC {@code RemoteTimestampParameters} to {@code TimestampParameters}
	 *
	 * @param remoteTimestampParameters {@link RemoteTimestampParameters}
	 * @param signatureForm {@link SignatureForm}
	 * @param asicContainerType {@link ASiCContainerType}
	 * @return {@link TimestampParameters}
	 */
	protected TimestampParameters toTimestampParameters(RemoteTimestampParameters remoteTimestampParameters, 
			SignatureForm signatureForm, ASiCContainerType asicContainerType) {
		TimestampParameters timestampParameters;
		if (asicContainerType != null) {
			switch (signatureForm) {
				case CAdES:
					ASiCWithCAdESTimestampParameters asicWithCAdESTimestampParameters = new ASiCWithCAdESTimestampParameters(
							remoteTimestampParameters.getDigestAlgorithm());
					asicWithCAdESTimestampParameters.aSiC().setContainerType(asicContainerType);
					timestampParameters = asicWithCAdESTimestampParameters;
					break;
				case XAdES:
					timestampParameters = new XAdESTimestampParameters(remoteTimestampParameters.getDigestAlgorithm(), 
							remoteTimestampParameters.getCanonicalizationMethod());
					break;
				default:
					throw new UnsupportedOperationException(String.format("Unsupported signature form [%s] for asic container type [%s]", signatureForm, asicContainerType));
			}
		} else {
			switch (signatureForm) {
				case CAdES:
					timestampParameters = new CAdESTimestampParameters(remoteTimestampParameters.getDigestAlgorithm());
					break;
				case PAdES:
					timestampParameters = new PAdESTimestampParameters(remoteTimestampParameters.getDigestAlgorithm());
					break;
				case XAdES:
					timestampParameters = new XAdESTimestampParameters(remoteTimestampParameters.getDigestAlgorithm(), 
							remoteTimestampParameters.getCanonicalizationMethod());
					break;
				case JAdES:
					timestampParameters = new JAdESTimestampParameters(remoteTimestampParameters.getDigestAlgorithm());
					break;
				case CBAdES:
					timestampParameters = new CBAdESTimestampParameters(remoteTimestampParameters.getDigestAlgorithm());
					break;
				default:
					throw new UnsupportedOperationException("Unsupported signature form : " + signatureForm);
			}
		}
		return timestampParameters;
	}

	/**
	 * Transforms {@code SignatureValueDTO} to {@code SignatureValue}
	 *
	 * @param signatureValueDTO {@link SignatureValueDTO}
	 * @return {@link SignatureValue}
	 */
	protected SignatureValue toSignatureValue(SignatureValueDTO signatureValueDTO) {
		return new SignatureValue(signatureValueDTO.getAlgorithm(), signatureValueDTO.getValue());
	}

	/**
	 * Transforms a list of {@code CommitmentTypeEnum}s to a list of {@code CommitmentType}s
	 *
	 * @param commitmentTypeEnums a list of {@link CommitmentTypeEnum}s
	 * @return a list of {@link CommitmentType}s
	 */
	protected List<CommitmentType> toCommitmentTypeList(List<CommitmentTypeEnum> commitmentTypeEnums) {
		if (Utils.isCollectionNotEmpty(commitmentTypeEnums)) {
			return commitmentTypeEnums.stream().map(CommitmentType.class::cast).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	private SignatureImageParameters toImageParameters(final RemoteSignatureImageParameters remoteImageParameters) {
		if (remoteImageParameters == null) {
			return null;
		}

		final SignatureImageParameters imageParameters = new SignatureImageParameters();
		// alignmentHorizontal
		if (remoteImageParameters.getAlignmentHorizontal() != null) {
			imageParameters.setAlignmentHorizontal(remoteImageParameters.getAlignmentHorizontal());
		}
		// alignmentVertical
		if (remoteImageParameters.getAlignmentVertical() != null) {
			imageParameters.setAlignmentVertical(remoteImageParameters.getAlignmentVertical());
		}
		// imageScaling
		if (remoteImageParameters.getImageScaling() != null) {
			imageParameters.setImageScaling(remoteImageParameters.getImageScaling());
		}
		// backgroundColor
		if (remoteImageParameters.getBackgroundColor() != null) {
			imageParameters.setBackgroundColor(ColorConverter.toColor(remoteImageParameters.getBackgroundColor()));
		}
		// dpi
		imageParameters.setDpi(remoteImageParameters.getDpi());
		// image
		if (remoteImageParameters.getImage() != null && remoteImageParameters.getImage().getBytes() != null && remoteImageParameters.getImage().getName() != null) {
			imageParameters.setImage(new InMemoryDocument(remoteImageParameters.getImage().getBytes(), remoteImageParameters.getImage().getName()));
		}
		// fieldParameters
		imageParameters.setFieldParameters(toFieldParameters(remoteImageParameters.getFieldParameters()));
		// textParameters
		imageParameters.setTextParameters(toTextParameters(remoteImageParameters.getTextParameters()));
		// zoom
		if (remoteImageParameters.getZoom() != null) {
			imageParameters.setZoom(remoteImageParameters.getZoom());
		}

		return imageParameters;
	}
	
	private SignatureFieldParameters toFieldParameters(final RemoteSignatureFieldParameters remoteFieldParameters) {
		if (remoteFieldParameters == null) {
			return null;
		}
		
		SignatureFieldParameters fieldParameters = new SignatureFieldParameters();
		if (remoteFieldParameters.getFieldId() != null) {
			fieldParameters.setFieldId(remoteFieldParameters.getFieldId());
		}
		if (remoteFieldParameters.getPage() != null) {
			fieldParameters.setPage(remoteFieldParameters.getPage());
		}
		if (remoteFieldParameters.getOriginX() != null) {
			fieldParameters.setOriginX(remoteFieldParameters.getOriginX());
		}
		if (remoteFieldParameters.getOriginY() != null) {
			fieldParameters.setOriginY(remoteFieldParameters.getOriginY());
		}
		if (remoteFieldParameters.getWidth() != null) {
			fieldParameters.setWidth(remoteFieldParameters.getWidth());
		}
		if (remoteFieldParameters.getHeight() != null) {
			fieldParameters.setHeight(remoteFieldParameters.getHeight());
		}
		// rotation
		if (remoteFieldParameters.getRotation() != null) {
			fieldParameters.setRotation(remoteFieldParameters.getRotation());
		}
		
		return fieldParameters;
	}

	private SignatureImageTextParameters toTextParameters(final RemoteSignatureImageTextParameters remoteTextParameters) {
		if (remoteTextParameters == null) {
			return null;
		}

		final SignatureImageTextParameters textParameters = new SignatureImageTextParameters();
		// backgroundColor
		if (remoteTextParameters.getBackgroundColor() != null) {
			textParameters.setBackgroundColor(ColorConverter.toColor(remoteTextParameters.getBackgroundColor()));
		}
		// font
		if (remoteTextParameters.getFont() != null && remoteTextParameters.getFont().getBytes() != null) {
			textParameters.setFont(new DSSFileFont(new ByteArrayInputStream(remoteTextParameters.getFont().getBytes())));
		}
		// size
		if (remoteTextParameters.getSize() != null) {
			DSSFont font = textParameters.getFont();
			font.setSize(remoteTextParameters.getSize());
		}
		// text wrapping
		if (remoteTextParameters.getTextWrapping() != null) {
			textParameters.setTextWrapping(remoteTextParameters.getTextWrapping());
		}
		// padding
		if (remoteTextParameters.getPadding() != null) {
			textParameters.setPadding(remoteTextParameters.getPadding());
		}
		// signerTextHorizontalAlignment
		if (remoteTextParameters.getSignerTextHorizontalAlignment() != null) {
			textParameters.setSignerTextHorizontalAlignment(remoteTextParameters.getSignerTextHorizontalAlignment());
		}
		// signerTextPosition
		if (remoteTextParameters.getSignerTextPosition() != null) {
			textParameters.setSignerTextPosition(remoteTextParameters.getSignerTextPosition());
		}
		// signerTextVerticalAlignment
		if (remoteTextParameters.getSignerTextVerticalAlignment() != null) {
			textParameters.setSignerTextVerticalAlignment(remoteTextParameters.getSignerTextVerticalAlignment());
		}
		// text
		textParameters.setText(remoteTextParameters.getText());
		// textColor
		if (remoteTextParameters.getTextColor() != null) {
			textParameters.setTextColor(ColorConverter.toColor(remoteTextParameters.getTextColor()));
		}

		return textParameters;
	}

	/**
	 * Creates counter signature parameters
	 *
	 * @param remoteParameters {@link RemoteSignatureParameters}
	 * @return {@link SerializableCounterSignatureParameters}
	 */
	protected SerializableCounterSignatureParameters createCounterSignatureParameters(
			RemoteSignatureParameters remoteParameters) {
		SerializableCounterSignatureParameters parameters;

		SignatureForm signatureForm = remoteParameters.getSignatureLevel().getSignatureForm();
		switch (signatureForm) {
			case XAdES:
				parameters = new XAdESCounterSignatureParameters();
				break;
			case CAdES:
				parameters = new CAdESCounterSignatureParameters();
				break;
			case JAdES:
				parameters = getJAdESSignatureParameters(remoteParameters);
				break;
			case CBAdES:
				parameters = getCBAdESSignatureParameters(remoteParameters);
				break;
			default:
				throw new UnsupportedOperationException("Unsupported signature form for counter signature : " + signatureForm);
		}
		
		fillCounterSignatureParameters(parameters, remoteParameters);
		return parameters;
	}

	@SuppressWarnings("unchecked")
	private void fillCounterSignatureParameters(SerializableCounterSignatureParameters parameters,
			RemoteSignatureParameters remoteParameters) {
		parameters.setSignatureIdToCounterSign(remoteParameters.getSignatureIdToCounterSign());
		if (parameters instanceof AbstractSignatureParameters<?>) {
			AbstractSignatureParameters<TimestampParameters> abstractSignatureParameters = (AbstractSignatureParameters<TimestampParameters>) parameters;
			fillParameters(abstractSignatureParameters, remoteParameters, remoteParameters.getSignatureLevel().getSignatureForm());
		}
	}


}