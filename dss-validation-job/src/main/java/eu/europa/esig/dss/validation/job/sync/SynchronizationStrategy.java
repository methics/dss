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
package eu.europa.esig.dss.validation.job.sync;

import eu.europa.esig.dss.model.job.DocumentInfo;
import eu.europa.esig.dss.model.job.DocumentListInfo;

/**
 * Defines a behaviour for a trusted certificate source synchronization
 *
 * @param <D> current {@link DocumentInfo}
 * @param <L> parent {@link DocumentListInfo}
 */
public interface SynchronizationStrategy<D extends DocumentInfo, L extends DocumentListInfo<D>> {

	/**
	 * Returns true if the certificates from the trusted list can be synchronized
	 * 
	 * @param document
	 *                    the trusted list to be tested
	 * @return true if the trusted list can be synchronized
	 */
	boolean canBeSynchronized(D document);

	/**
	 * Returns true if the certificates from the list of trusted lists and its
	 * trusted list can be synchronized
	 * 
	 * @param documentList
	 *                          the list of trusted lists to be tested
	 * @return true if the list of trusted lists can be synchronized
	 */
	boolean canBeSynchronized(L documentList);

}
