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

import eu.europa.esig.dss.jaxb.common.AbstractJaxbFacade;
import eu.europa.esig.lote.jaxb.ListOfTrustedEntitiesType;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBElement;
import jakarta.xml.bind.JAXBException;
import org.xml.sax.SAXException;

import javax.xml.validation.Schema;
import java.io.IOException;

/**
 * Performs marshalling/unmarshalling operation for a List of Trusted Entities XML
 *
 */
public class LOTEFacade extends AbstractJaxbFacade<ListOfTrustedEntitiesType> {

    /** TL utils */
    private static final LOTEUtils LOTE_UTILS = LOTEUtils.getInstance();

    /**
     * Default constructor
     */
    protected LOTEFacade() {
        // empty
    }

    /**
     * Creates a new facade
     *
     * @return {@link LOTEFacade}
     */
    public static LOTEFacade newFacade() {
        return new LOTEFacade();
    }

    @Override
    protected JAXBContext getJAXBContext() throws JAXBException {
        return LOTE_UTILS.getJAXBContext();
    }

    @Override
    protected Schema getSchema() throws IOException, SAXException {
        return LOTE_UTILS.getSchema();
    }

    @Override
    protected JAXBElement<ListOfTrustedEntitiesType> wrap(ListOfTrustedEntitiesType jaxbObject) {
        return LOTEUtils.OBJECT_FACTORY.createListOfTrustedEntities(jaxbObject);
    }

}
