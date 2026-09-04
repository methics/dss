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
package eu.europa.esig.trustedlist211;

import eu.europa.esig.dss.jaxb.common.AbstractJaxbFacade;
import eu.europa.esig.trustedlist211.jaxb.tsl.TrustStatusListType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.xml.sax.SAXException;

import javax.xml.validation.Schema;
import java.io.IOException;

/**
 * Performs marshalling/unmarshalling operation for a TrustedList XML
 */
public class TrustedList211Facade extends AbstractJaxbFacade<TrustStatusListType> {

	/** TL utils */
	private static final TrustedList211Utils TLv211_UTILS = TrustedList211Utils.getInstance();

	/**
	 * Default constructor
	 */
	protected TrustedList211Facade() {
		// empty
	}

	/**
	 * Creates a new facade
	 *
	 * @return {@link TrustedList211Facade}
	 */
	public static TrustedList211Facade newFacade() {
		return new TrustedList211Facade();
	}

	@Override
	protected JAXBContext getJAXBContext() throws JAXBException {
		return TLv211_UTILS.getJAXBContext();
	}

	@Override
	protected Schema getSchema() throws IOException, SAXException {
		return TLv211_UTILS.getSchema();
	}

	@Override
	protected JAXBElement<TrustStatusListType> wrap(TrustStatusListType jaxbObject) {
		return TrustedList211Utils.OBJECT_FACTORY.createTrustServiceStatusList(jaxbObject);
	}

}
