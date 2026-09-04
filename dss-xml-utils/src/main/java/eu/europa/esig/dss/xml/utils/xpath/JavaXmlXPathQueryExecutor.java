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

import eu.europa.esig.dss.model.DSSException;
import eu.europa.esig.dss.xml.common.xpath.XPathQuery;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

/**
 * Executes XPath expression query based on the {@code javax.xml.xpath.XPathExpression} class.
 *
 */
public class JavaXmlXPathQueryExecutor extends AbstractXPathQueryExecutor implements XPathStringExecutor {

    /** The used XPathFactory */
    private static final XPathFactory factory = XPathFactory.newInstance();

    /**
     * Default constructor
     */
    public JavaXmlXPathQueryExecutor() {
        // empty
    }

    @Override
    public NodeList getNodeList(Node xmlNode, XPathQuery xPathQuery) {
        return getNodeList(xmlNode, xPathQuery.getQueryString());
    }

    /**
     * Returns the NodeList corresponding to the XPath query.
     *
     * @param xmlNode
     *                    The node where the search should be performed.
     * @param xPathString
     *                    {@link String} XPath query string
     * @return the NodeList corresponding to the XPath query
     */
    @Override
    public NodeList getNodeList(Node xmlNode, String xPathString) {
        try {
            final XPathExpression expr = createXPathExpression(xPathString);
            return (NodeList) expr.evaluate(xmlNode, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            throw new DSSException(String.format("Unable to find a NodeList by the given xPathString '%s'. Reason : %s",
                    xPathString, e.getMessage()), e);
        }
    }

    /**
     * This method creates a new instance of XPathExpression with the given xpath query
     *
     * @param xpathString {@link String} representing the XPath expression to be executed
     * @return an instance of {@code XPathExpression} for the given xpathString
     */
    protected XPathExpression createXPathExpression(final String xpathString) {
        final XPath xpath = factory.newXPath();
        if (namespaceContext != null) {
            xpath.setNamespaceContext(namespaceContext);
        }
        try {
            return xpath.compile(xpathString);
        } catch (XPathExpressionException e) {
            throw new DSSException(String.format("Unable to create an XPath expression : %s", e.getMessage()), e);
        }
    }

}
