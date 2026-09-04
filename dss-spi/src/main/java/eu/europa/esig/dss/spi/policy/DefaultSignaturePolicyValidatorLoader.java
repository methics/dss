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
package eu.europa.esig.dss.spi.policy;

import eu.europa.esig.dss.model.signature.SignaturePolicy;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Loads a relevant {@code SignaturePolicyValidator} based on the policy content
 *
 */
public class DefaultSignaturePolicyValidatorLoader implements SignaturePolicyValidatorLoader {

    /**
     * The validator to be used when only a basic validation according to the signature format is required
     * <p>
     * NOTE: can be null (the best corresponding validator will be loaded)
     */
    private SignaturePolicyValidator defaultSignaturePolicyValidator;

    /**
     * Whether the SignaturePolicy.hashAsInTechnicalSpecification attribute is supported
     * <p>
     * Default : TRUE (SignaturePolicy.hashAsInTechnicalSpecification attribute is supported)
     */
    private boolean supportHashAsInTechnicalSpecification = true;

    /**
     * Default constructor instantiating object with null SignaturePolicyValidator
     */
    public DefaultSignaturePolicyValidatorLoader() {
        // empty
    }

    /**
     * Creates a new {@code SignaturePolicyValidatorLoader} running the signature policy validation using
     * the {@code defaultSignaturePolicyValidator}.
     * The default implementation will be used on all signature policy hash calculations.
     *
     * @param defaultSignaturePolicyValidator {@link SignaturePolicyValidator}
     * @return {@link DefaultSignaturePolicyValidatorLoader}
     */
    public static DefaultSignaturePolicyValidatorLoader defaultOnlySignaturePolicyValidatorLoader(
            SignaturePolicyValidator defaultSignaturePolicyValidator) {
        DefaultSignaturePolicyValidatorLoader loader = new DefaultSignaturePolicyValidatorLoader();
        loader.setDefaultSignaturePolicyValidator(defaultSignaturePolicyValidator);
        loader.setSupportHashAsInTechnicalSpecification(false);
        return loader;
    }

    /**
     * Creates a new {@code SignaturePolicyValidatorLoader} running the signature policy validation using
     * the {@code defaultSignaturePolicyValidator}.
     * The default implementation will be used on all signature policy hash calculations,
     * unless a "HashAsInTechnicalSpecification" parameter is set within the Signature Policy Identifier.
     *
     * @param defaultSignaturePolicyValidator {@link SignaturePolicyValidator}
     * @return {@link DefaultSignaturePolicyValidatorLoader}
     */
    public static DefaultSignaturePolicyValidatorLoader defaultUnlessSpecifiedSignaturePolicyValidatorLoader(
            SignaturePolicyValidator defaultSignaturePolicyValidator) {
        DefaultSignaturePolicyValidatorLoader loader = new DefaultSignaturePolicyValidatorLoader();
        loader.setDefaultSignaturePolicyValidator(defaultSignaturePolicyValidator);
        loader.setSupportHashAsInTechnicalSpecification(true);
        return loader;
    }

    /**
     * Creates a new {@code SignaturePolicyValidatorLoader} running the signature policy validation loading
     * the {@code SignaturePolicyValidator} based on the signature policy's specification.
     * The supported {@code SignaturePolicyValidator}s can be listed within
     * a "/resources/META-INF/services/eu.europa.esig.dss.spi.policy.SignaturePolicyValidator" file.
     * The first {@code SignaturePolicyValidator} matching the signature policy will be selected.
     * If not defined explicitly, one of the default signature policies will be used.
     *
     * @return {@link DefaultSignaturePolicyValidatorLoader}
     */
    public static DefaultSignaturePolicyValidatorLoader policyBasedSignaturePolicyValidatorLoader() {
        return new DefaultSignaturePolicyValidatorLoader();
    }

    /**
     * This method sets a {@code SignaturePolicyValidator} to be used for default signature policy processing
     * according to the signature format (when {@code SignaturePolicy.hashAsInTechnicalSpecification == false})
     *
     * @param defaultSignaturePolicyValidator {@link SignaturePolicyValidator}
     */
    public void setDefaultSignaturePolicyValidator(SignaturePolicyValidator defaultSignaturePolicyValidator) {
        this.defaultSignaturePolicyValidator = defaultSignaturePolicyValidator;
    }

    /**
     * Sets whether the SignaturePolicy.hashAsInTechnicalSpecification attribute is supported.
     * If set to TRUE, the behavior of the loader will change based on the attribute presence.
     * Otherwise, it will be ignored.
     * <p>
     * Default : TRUE (SignaturePolicy.hashAsInTechnicalSpecification attribute is supported)
     *
     * @param supportHashAsInTechnicalSpecification whether HashAsInTechnicalSpecification attribute is supported
     */
    public void setSupportHashAsInTechnicalSpecification(boolean supportHashAsInTechnicalSpecification) {
        this.supportHashAsInTechnicalSpecification = supportHashAsInTechnicalSpecification;
    }

    /**
     * Loads with a ServiceLoader and returns the relevant validator for a {@code SignaturePolicy}
     *
     * @param signaturePolicy {@link SignaturePolicy} to get a relevant validator for
     * @return {@link SignaturePolicyValidator}
     */
    @Override
    public SignaturePolicyValidator loadValidator(final SignaturePolicy signaturePolicy) {
        SignaturePolicyValidator validator = null;
        if (defaultSignaturePolicyValidator != null && (!supportHashAsInTechnicalSpecification || !signaturePolicy.isHashAsInTechnicalSpecification())) {
            validator = defaultSignaturePolicyValidator;

        } else {
            ServiceLoader<SignaturePolicyValidator> loader = ServiceLoader.load(SignaturePolicyValidator.class);
            Iterator<SignaturePolicyValidator> validatorOptions = loader.iterator();

            if (validatorOptions.hasNext()) {
                for (SignaturePolicyValidator signaturePolicyValidator : loader) {
                    if (signaturePolicyValidator.canValidate(signaturePolicy)) {
                        validator = signaturePolicyValidator;
                        break;
                    }
                }
            }
            if (validator == null) {
                // if not empty and no other implementation is found
                validator = new NonASN1SignaturePolicyValidator();
            }
        }
        return validator;
    }

}
