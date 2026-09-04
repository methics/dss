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
package eu.europa.esig.dss.spi.security;

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.spi.DSSSecurityProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.Provider;
import java.util.Objects;

/**
 * This class provides an abstract logic to load securely Java Cryptographic Architecture classes,
 * based on {@code eu.europa.esig.dss.spi.DSSSecurityProvider}'s configuration.
 * <p>
 * The implementation will initially use the primary security provider to load a given token,
 * and only in case of a failure of the main security provider, the implementation will try to perform
 * the same operation using alternative security providers, until the first success.
 * The first successful result is returned immediately, while in case of failure of all security providers,
 * the class will throw a {@code eu.europa.esig.dss.model.DSSException}.
 *
 * @param <I> represents an input object for the result generation
 * @param <O> represents an output of the building process
 */
public abstract class DSSSecurityFactory<I, O> {

    private static final Logger LOG = LoggerFactory.getLogger(DSSSecurityFactory.class);

    /**
     * Default constructor
     *
     */
    protected DSSSecurityFactory() {
        // empty
    }

    /**
     * This method implements the main logic to load a given token.
     * It will first try to generate the output result using a main security provider from {@code DSSSecurityProvider}, 
     * and only in case of failure will try to use alternative security providers until the first success.
     * 
     * @param input object to be used as an input for result generation
     * @return output object result
     */
    public O build(final I input) {
        Objects.requireNonNull(input, "Input cannot be null");
        O output = buildWithPrimarySecurityProvider(input);
        if (output != null) {
            return output;
        }
        output = buildWithAlternativeSecurityProviders(input);
        if (output != null) {
            return output;
        }
        throw new DSSException(String.format("Unable to load %s for the given %s. " +
                "All security providers have failed. More detail in debug mode.", getFactoryClassName(), input.getClass().getSimpleName()));
    }

    /**
     * Builds the result based on the given input using the primary security provider.
     * If the build fails, it returns a NULL value.
     *
     * @param input object to be used as an input for result generation
     * @return output object result
     */
    protected O buildWithPrimarySecurityProvider(I input) {
        try {
            return buildWithProvider(input, DSSSecurityProvider.getSecurityProvider());
        } catch (Exception e) {
            if (LOG.isDebugEnabled()) {
                LOG.warn("Unable to build {} using the default security provider. {}. Input: {}",
                        getFactoryClassName(), e.getMessage(), toString(input), e);
            } else {
                LOG.warn("Unable to build {} using the default security provider. {}.",
                        getFactoryClassName(), e.getMessage());
            }
            return null;
        }
    }

    /**
     * Builds the result based on the given input using the alternative security providers.
     * The method iterates accords all available alternative security providers until
     * the first successful result and returns it.
     * If the build fails for all security providers, it returns a NULL value.
     *
     * @param input object to be used as an input for result generation
     * @return output object result
     */
    protected O buildWithAlternativeSecurityProviders(I input) {
        for (Provider provider : DSSSecurityProvider.getAlternativeSecurityProviders()) {
            try {
                O result = buildWithProvider(input, provider);
                if (result != null) {
                    LOG.info("{} has been successfully built using the alternative security provider '{}'. " +
                                    "More detail in debug mode.", getFactoryClassName(), provider.getName());
                    return result;
                }
            } catch (Exception e) {
                String errorMessage = "Unable to build {} using the alternative security provider '{}'. {}";
                if (LOG.isDebugEnabled()) {
                    LOG.warn(errorMessage, getFactoryClassName(), provider.getName(), e.getMessage(), e);
                } else {
                    LOG.warn(errorMessage, getFactoryClassName(), provider.getName(), e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Gets the factory class name
     *
     * @return {@link String}
     */
    protected abstract String getFactoryClassName();

    /**
     * Gets a string representation for the given {@code input}
     *
     * @param input object to be used as an input for result generation
     * @return {@link String}
     */
    protected abstract String toString(I input);

    /**
     * Builds the result using a given {@code securityProvider}
     *
     * @param input object to be used as an input for result generation
     * @param securityProvider {@link Provider}
     * @return output object result if the build is successful
     * @throws Exception if an error occurred
     */
    protected abstract O buildWithProvider(final I input, Provider securityProvider) throws Exception;

}
