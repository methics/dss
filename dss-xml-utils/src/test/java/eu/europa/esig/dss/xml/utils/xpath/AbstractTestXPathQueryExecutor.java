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
package eu.europa.esig.dss.xml.utils.xpath;

import eu.europa.esig.dss.xml.common.definition.DSSAttribute;
import eu.europa.esig.dss.xml.common.definition.DSSElement;
import eu.europa.esig.dss.xml.common.xpath.XPathQuery;
import eu.europa.esig.dss.xml.common.xpath.XPathQueryBuilder;
import eu.europa.esig.dss.xml.utils.DomUtils;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import static org.junit.jupiter.api.Assertions.assertEquals;

public abstract class AbstractTestXPathQueryExecutor {
    
    private static final Document DOC = DomUtils.buildDOM("<a><b><d>Hello</d><e><e pos=\"nested\">Nested</e></e></b><c><d>Bye</d><d Id=\"world\">World</d></c></a>");

    @Test
    public void positionTest() {
        XPathQuery query = XPathQueryBuilder.all().element(getElement("a")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("b")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("c")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("d")).build();
        assertEquals(3, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("e")).build();
        assertEquals(2, getExecutor().getNodeList(DOC, query).getLength());

        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("a")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("b")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("c")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("d")).build();
        assertEquals(3, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("e")).build();
        assertEquals(2, getExecutor().getNodeList(DOC, query).getLength());

        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("a")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("b")).build();
        assertEquals(0, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("c")).build();
        assertEquals(0, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("d")).build();
        assertEquals(0, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("e")).build();
        assertEquals(0, getExecutor().getNodeList(DOC, query).getLength());

        Node bElement = DOC.getDocumentElement().getFirstChild();

        query = XPathQueryBuilder.all().element(getElement("a")).build();
        assertEquals(1, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("b")).build();
        assertEquals(1, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("c")).build();
        assertEquals(1, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("d")).build();
        assertEquals(3, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("e")).build();
        assertEquals(2, getExecutor().getNodeList(bElement, query).getLength());

        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("a")).build();
        assertEquals(0, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("b")).build();
        assertEquals(0, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("c")).build();
        assertEquals(0, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("d")).build();
        assertEquals(1, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.allFromCurrentPosition().element(getElement("e")).build();
        assertEquals(2, getExecutor().getNodeList(bElement, query).getLength());

        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("a")).build();
        assertEquals(0, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("b")).build();
        assertEquals(0, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("c")).build();
        assertEquals(0, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("d")).build();
        assertEquals(1, getExecutor().getNodeList(bElement, query).getLength());
        query = XPathQueryBuilder.fromCurrentPosition().element(getElement("e")).build();
        assertEquals(1, getExecutor().getNodeList(bElement, query).getLength());
    }

    @Test
    public void elementPathTest() {
        XPathQuery query = XPathQueryBuilder.all().elements(getElement("a"), getElement("b")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().elements(getElement("a"), getElement("c")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().elements(getElement("b"), getElement("c")).build();
        assertEquals(0, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().elements(getElement("b"), getElement("d")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().elements(getElement("c"), getElement("d")).build();
        assertEquals(2, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().elements(getElement("b"), getElement("e")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().elements(getElement("e"), getElement("e")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
    }

    @Test
    public void attributeTest() {
        XPathQuery query = XPathQueryBuilder.all().attribute(getAttribute("pos")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().attribute(getAttribute("Id")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().attribute(getAttribute("id")).build();
        assertEquals(0, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().attribute(getAttribute("evil")).build();
        assertEquals(0, getExecutor().getNodeList(DOC, query).getLength());
    }

    @Test
    public void attributeValueTest() {
        XPathQuery query = XPathQueryBuilder.all().attribute(getAttribute("pos"), "nested").build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().attribute(getAttribute("pos"), "notnested").build();
        assertEquals(0, getExecutor().getNodeList(DOC, query).getLength());
    }

    @Test
    public void idAttributeTest() {
        XPathQuery query = XPathQueryBuilder.all().idValue("world").build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().idValue("void").build();
        assertEquals(0, getExecutor().getNodeList(DOC, query).getLength());
    }

    @Test
    public void notChildOfTest() {
        XPathQuery query = XPathQueryBuilder.all().element(getElement("d")).notChildOf(getElement("b")).build();
        assertEquals(2, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("d")).notChildOf(getElement("c")).build();
        assertEquals(1, getExecutor().getNodeList(DOC, query).getLength());
        query = XPathQueryBuilder.all().element(getElement("d")).notChildOf(getElement("a")).build();
        assertEquals(3, getExecutor().getNodeList(DOC, query).getLength());
    }

    private DSSElement getElement(String localName) {
        return DSSElement.fromDefinition(localName, null);
    }

    private DSSAttribute getAttribute(String localName) {
        return DSSAttribute.fromDefinition(localName);
    }

    protected abstract XPathQueryExecutor getExecutor();

}
