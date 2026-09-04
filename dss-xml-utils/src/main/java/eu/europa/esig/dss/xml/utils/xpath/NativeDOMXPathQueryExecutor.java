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

import eu.europa.esig.dss.xml.common.xpath.XPathQuery;
import eu.europa.esig.dss.xml.common.xpath.item.XPathQueryItem;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * (Experimental) Implementation of {@code eu.europa.esig.dss.xml.utils.xpath.XPathQueryExecutor}
 * based on the native XML DOM Node's processing.
 *
 */
public class NativeDOMXPathQueryExecutor extends AbstractXPathQueryExecutor {

    /**
     * Default constructor
     */
    public NativeDOMXPathQueryExecutor() {
        // empty
    }

    @Override
    public NodeList getNodeList(Node xmlNode, XPathQuery xPathQuery) {
        if (xmlNode == null || xPathQuery.isEmpty()) {
            return emptyNodeList();
        }

        boolean deepSearch = xPathQuery.isAll();

        List<Node> currentNodes = new ArrayList<>();
        if (xPathQuery.isFromCurrentPosition()) {
            currentNodes.add(xmlNode);
        } else {
            if (xmlNode.getNodeType() == Node.DOCUMENT_NODE) {
                currentNodes.add(xmlNode);
            } else {
                currentNodes.add(xmlNode.getOwnerDocument());
            }
        }

        boolean firstLoop = true;
        XPathQueryItem queryItem = xPathQuery.getFirstXPathQueryItem();
        while (queryItem != null){
            List<Node> nextNodes = new ArrayList<>();

            for (Node parent : currentNodes) {
                collectDescendants(parent, queryItem, nextNodes, deepSearch && firstLoop);
            }

            currentNodes = nextNodes;

            if (currentNodes.isEmpty()) {
                break;
            }

            queryItem = queryItem.nextItem();
            firstLoop = false;
        }

        return toNodeList(currentNodes);
    }

    private static void collectDescendants(Node node, XPathQueryItem queryItem, List<Node> result, boolean deepSearch) {
        if (queryItem.isEmpty()) {
            result.add(node);
            return;
        }

        if (queryItem.isElementRelated()) {
            NodeList children = node.getChildNodes();
            if (children != null && children.getLength() > 0) {
                for (int i = 0; i < children.getLength(); i++) {
                    Node child = children.item(i);

                    if (queryItem.matchNode(child)) {
                        result.add(child);
                    }

                    if (deepSearch) {
                        collectDescendants(child, queryItem, result, true);
                    }
                }
            }
        }

        if (queryItem.isAttributeRelated()) {
            NamedNodeMap attributes = node.getAttributes();
            if (attributes != null && attributes.getLength() > 0) {
                for (int i = 0; i < attributes.getLength(); i++) {
                    Node attr = attributes.item(i);

                    if (queryItem.matchNode(attr)) {
                        result.add(attr);
                    }
                }
            }
        }
    }

    private static NodeList toNodeList(List<Node> nodes) {

        return new NodeList() {
            @Override
            public Node item(int index) {
                return index >= 0 && index < nodes.size() ? nodes.get(index) : null;
            }

            @Override
            public int getLength() {
                return nodes.size();
            }
        };

    }

    private static NodeList emptyNodeList() {
        return toNodeList(Collections.emptyList());
    }

}
