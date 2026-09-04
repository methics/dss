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
package eu.europa.esig.dss.validation.job.runnable;

import eu.europa.esig.dss.model.DSSDocument;
import eu.europa.esig.dss.spi.client.http.DSSFileLoader;
import eu.europa.esig.dss.spi.x509.CertificateSource;
import eu.europa.esig.dss.validation.job.AbstractTestValidationJob;
import eu.europa.esig.dss.validation.job.cache.access.CacheAccessByKey;
import eu.europa.esig.dss.validation.job.download.DownloadResult;
import eu.europa.esig.dss.validation.job.download.DownloadTask;
import eu.europa.esig.dss.validation.job.parsing.ParsingResult;
import eu.europa.esig.dss.validation.job.parsing.ParsingTask;
import eu.europa.esig.dss.validation.job.source.DocumentSource;
import eu.europa.esig.dss.validation.job.validation.ValidationResult;
import eu.europa.esig.dss.validation.job.validation.ValidationTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public abstract class AbstractTestRunnable extends AbstractTestValidationJob {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractTestRunnable.class);

    protected void shutdownNowAndAwaitTermination(ExecutorService executorService) {
        executorService.shutdownNow();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                LOG.warn("More than 10s to terminate the service executor");
            }
        } catch (InterruptedException e) {
            LOG.warn("Unable to interrupt the service executor", e);
            Thread.currentThread().interrupt();
        }
    }

    protected class MockRunnableAnalysis extends AbstractRunnableAnalysis {

        /**
         * Default constructor
         *
         * @param source        {@link DocumentSource}
         * @param cacheAccess   {@link CacheAccessByKey}
         * @param dssFileLoader {@link DSSFileLoader}
         * @param latch         {@link CountDownLatch}
         */
        public MockRunnableAnalysis(DocumentSource source, CacheAccessByKey cacheAccess, DSSFileLoader dssFileLoader,
                                    CountDownLatch latch) {
            super(source, cacheAccess, dssFileLoader, latch);
        }

        @Override
        protected DSSDocument download(String url) {
            throw new Error("An error occurred during the download task.");
        }

        @Override
        protected DownloadTask getDownloadTask(DSSFileLoader dssFileLoader, String url) {
            return new DownloadTask() {
                @Override
                public DownloadResult get() {
                    return getDownloadResult(null, null);
                }
            };
        }
        @Override
        protected ParsingTask getParsingTask(DSSDocument document) {
            return new ParsingTask() {
                @Override
                public ParsingResult get() {
                    return getParsingResult();
                }
            };
        }
        @Override
        protected ValidationTask getValidationTask(DSSDocument document, CertificateSource certificateSource) {
            return new ValidationTask() {
                @Override
                public ValidationResult get() {
                    return getValidationResult(null);
                }
            };
        }

    }

}
