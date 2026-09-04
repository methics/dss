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
package eu.europa.esig.lote.xml;

import eu.europa.esig.lote.jaxb.ListOfTrustedEntitiesType;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.UnmarshalException;
import org.junit.jupiter.api.Test;
import org.xml.sax.SAXException;

import javax.xml.stream.XMLStreamException;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class LOTEFacadeTest {

    @Test
    void test() throws JAXBException, XMLStreamException, IOException, SAXException {
        File file = new File("src/test/resources/valid.xml");
        marshallUnmarshall(file);
        List<String> errors = validateWithErrors(file);
        assertEquals(0, errors.size());
    }

    @Test
    void fullTest() throws JAXBException, XMLStreamException, IOException, SAXException {
        File file = new File("src/test/resources/valid-full.xml");
        marshallUnmarshall(file);
        List<String> errors = validateWithErrors(file);
        assertEquals(0, errors.size());
    }

    @Test
    void signedTest() throws JAXBException, XMLStreamException, IOException, SAXException {
        File file = new File("src/test/resources/valid-signed.xml");
        marshallUnmarshall(file);
        List<String> errors = validateWithErrors(file);
        assertEquals(0, errors.size());
    }

    @Test
    void emptyTETest() throws JAXBException, XMLStreamException, IOException, SAXException {
        File file = new File("src/test/resources/valid-emptyTE.xml");
        marshallUnmarshall(file);
        List<String> errors = validateWithErrors(file);
        assertEquals(0, errors.size());
    }

    @Test
    void invalidTest() throws JAXBException, XMLStreamException, IOException, SAXException {
        File file = new File("src/test/resources/invalid.xml");
        assertThrows(UnmarshalException.class, () -> marshallUnmarshall(file));
        List<String> errors = validateWithErrors(file);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("ListAndSchemeInformation"), errors.toString());
    }

    @Test
    void emptySchemeExtensionsTest() throws JAXBException, XMLStreamException, IOException, SAXException {
        File file = new File("src/test/resources/empty-scheme-extensions.xml");
        assertThrows(UnmarshalException.class, () -> marshallUnmarshall(file));
        List<String> errors = validateWithErrors(file);
        assertEquals(1, errors.size());
        assertTrue(errors.get(0).contains("SchemeExtensions"));
    }

    private void marshallUnmarshall(File file) throws JAXBException, XMLStreamException, IOException, SAXException {
        LOTEFacade facade = LOTEFacade.newFacade();

        ListOfTrustedEntitiesType loteType = facade.unmarshall(file);
        assertNotNull(loteType);

        loteType = facade.unmarshall(file, false);
        assertNotNull(loteType);

        loteType = facade.unmarshall(file, true);
        assertNotNull(loteType);

        String marshall = facade.marshall(loteType);
        assertNotNull(marshall);

        marshall = facade.marshall(loteType, false);
        assertNotNull(marshall);

        marshall = facade.marshall(loteType, true);
        assertNotNull(marshall);
    }

    private List<String> validateWithErrors(File file) throws IOException {
        return LOTEUtils.getInstance().validateAgainstXSD(new StreamSource(Files.newInputStream(file.toPath())));
    }
    
}
