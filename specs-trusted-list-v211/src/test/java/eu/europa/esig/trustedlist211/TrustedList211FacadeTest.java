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

import eu.europa.esig.trustedlist211.jaxb.tsl.TrustStatusListType;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TrustedList211FacadeTest {

	@Test
	void testTL() throws JAXBException, XMLStreamException, IOException, SAXException {
		marshallUnmarshall(new File("src/test/resources/tl.xml"));
	}

	@Test
	void testTLv5() throws JAXBException, XMLStreamException, IOException, SAXException {
		marshallUnmarshall(new File("src/test/resources/tlv5.xml"));
	}

	@Test
	void testTLv6() {
		Exception exception = assertThrows(UnmarshalException.class, () -> marshallUnmarshall(new File("src/test/resources/tlv6.xml")));
		assertTrue(exception.getCause().getMessage().contains("ServiceSupplyPoint"));
	}

	@Test
	void testHelloWorld() {
		File file = new File("src/test/resources/hello_world.xml");

		TrustedList211Facade facade = TrustedList211Facade.newFacade();

		JAXBException exception = assertThrows(UnmarshalException.class, () -> facade.unmarshall(file));
		assertTrue(exception.getLinkedException().getMessage().contains("hello"));

		exception = assertThrows(UnmarshalException.class, () -> facade.unmarshall(file, false));
		assertTrue(exception.getLinkedException().getMessage().contains("hello"));
	}

	@Test
	void testLOTL() throws JAXBException, XMLStreamException, IOException, SAXException {
		marshallUnmarshall(new File("src/test/resources/lotl.xml"));
	}

	private void marshallUnmarshall(File file) throws JAXBException, XMLStreamException, IOException, SAXException {
		TrustedList211Facade facade = TrustedList211Facade.newFacade();

		TrustStatusListType trustStatusListType = facade.unmarshall(file);
		assertNotNull(trustStatusListType);

		trustStatusListType = facade.unmarshall(file, false);
		assertNotNull(trustStatusListType);

		trustStatusListType = facade.unmarshall(file, true);
		assertNotNull(trustStatusListType);

		String marshall = facade.marshall(trustStatusListType);
		assertNotNull(marshall);

		marshall = facade.marshall(trustStatusListType, false);
		assertNotNull(marshall);

		marshall = facade.marshall(trustStatusListType, true);
		assertNotNull(marshall);
	}

}
