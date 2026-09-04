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
package eu.europa.esig.dss.tsl.sync;

import eu.europa.esig.dss.model.tsl.LOTLInfo;
import eu.europa.esig.dss.model.tsl.TLInfo;
import eu.europa.esig.dss.validation.job.sync.SynchronizationStrategy;

/**
 * Accepts all trusted lists
 *
 * @deprecated since DSS 6.5. Please use {@code eu.europa.esig.dss.validation.job.sync.AcceptAllStrategy} instead
 */
@Deprecated
public class AcceptAllStrategy implements SynchronizationStrategy<TLInfo, LOTLInfo> {

	/**
	 * Default constructor
	 */
	public AcceptAllStrategy() {
		// empty
	}

	@Override
	public boolean canBeSynchronized(TLInfo trustedList) {
		return true;
	}

	@Override
	public boolean canBeSynchronized(LOTLInfo listOfTrustedList) {
		return true;
	}

}
