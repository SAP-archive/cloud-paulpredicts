package com.sap.pto.util;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.lang3.StringUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Utility class providing commonly needed XML functionality.
 *
 */
public class XmlUtilsExt {
    private final static String DEFAULT_ENCODING = "UTF-8";

    /**
     * Reads and parses an XML document from a file.
     */
    public static Document loadXMLDoc(File file) throws IOException {
        DocumentBuilder parser;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IOException("Parser could not be created.", e);
        }

        try {
            return parser.parse(file.getAbsolutePath());
        } catch (SAXException e) {
            throw new IOException("Unable to parse file.", e);
        }
    }

    /**
     * Parses an XML document from an InputSource.
     */
    public static Document loadXMLDoc(InputSource source) throws IOException {
        DocumentBuilder parser;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IOException("Parser could not be created.", e);
        }

        try {
            return parser.parse(source);
        } catch (SAXException e) {
            throw new IOException("Unable to parse source.", e);
        }
    }

    /**
     * Parses an XML document from an URL or other source string.
     */
    public static Document loadXMLDoc(String uri) throws IOException {
        if (StringUtils.isBlank(uri)) {
            throw new IllegalArgumentException("URI should not be null or empty.");
        }

        DocumentBuilder parser;
        try {
            parser = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new IOException("Parser could not be created.", e);
        }

        try {
            return parser.parse(uri);
        } catch (SAXException e) {
            throw new IOException("Unable to parse document from URI " + uri, e);
        }
    }

    /**
     * Parses an XML document from a string.
     */
    public static Document parseXMLString(String xmlString) throws IOException {
        return loadXMLDoc(new InputSource(new ByteArrayInputStream(xmlString.getBytes(DEFAULT_ENCODING))));
    }

    /**
     * Returns the list of nodes matching the XPath.
     */
    public static NodeList getXPathResultSet(Object doc, String expression) {
        XPath xpath = XPathFactory.newInstance().newXPath();

        try {
            XPathExpression expr = xpath.compile(expression);
            return (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    /**
     * Returns the list of nodes matching the XPath.
     */
    public static NodeList getXPathResultSet(File file, String expression) {
        try {
            Document doc = loadXMLDoc(file);

            return getXPathResultSet(doc, expression);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the node matching the XPath.
     */
    public static Node getXPathResultNode(Object doc, String expression) {
        XPath xpath = XPathFactory.newInstance().newXPath();

        try {
            XPathExpression expr = xpath.compile(expression);
            return (Node) expr.evaluate(doc, XPathConstants.NODE);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    /**
     * Returns the node matching the XPath.
     */
    public static Node getXPathResultNode(File file, String expression) {
        try {
            Document doc = loadXMLDoc(file);

            return getXPathResultNode(doc, expression);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Returns the return value for the matching the XPath.
     */
    public static String getXPathResultValue(Object doc, String expression) {
        XPath xpath = XPathFactory.newInstance().newXPath();

        try {
            XPathExpression expr = xpath.compile(expression);
            return (String) expr.evaluate(doc, XPathConstants.STRING);
        } catch (XPathExpressionException e) {
            return null;
        }
    }

    /**
     * Returns the return value for the matching the XPath.
     */
    public static String getXPathResultValue(File file, String expression) {
        try {
            Document doc = loadXMLDoc(file);

            return getXPathResultValue(doc, expression);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Creates a string array out of the text contents of a node list.
     */
    public static String[] nodesToArray(NodeList xPathResult) {
        String[] arr = new String[xPathResult.getLength()];
        for (int i = 0; i < xPathResult.getLength(); i++) {
            arr[i] = xPathResult.item(i).getTextContent();
        }
        return arr;
    }

    /**
     * Returns the first child node matching the {@code tagName}.
     */
    public static Node findNode(Node parent, String tagName) {
        Node result = null;

        NodeList nl = parent.getChildNodes();
        for (int i = 0; i < nl.getLength(); i++) {
            if (nl.item(i).getNodeName().equals(tagName)) {
                return nl.item(i);
            }
        }

        return result;
    }

}
