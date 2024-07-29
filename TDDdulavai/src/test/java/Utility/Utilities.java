package Utility;

import java.io.File;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Utilities {
	
	public static void updateNode(String file, String value, String xmlPath) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(file));

		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate(xmlPath, doc, XPathConstants.NODE);
		node.setTextContent(value);
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(file));
		transformer.transform(source, result);

		// System.out.println("Node:" + node.getTextContent());
	}

	public static void updateDeleteNode(String file, String value, String xmlPath) throws Exception {
		Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new InputSource(file));

		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate(xmlPath, doc, XPathConstants.NODE);
		NodeList list = node.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node1 = list.item(i);
			if ("Limit".equals(node1.getNodeName())) {
				node.removeChild(node1);
			}
		}

		// Add new node
		Node newNode = doc.createElement("Limit");
		node.appendChild(newNode);
		Node newNode1 = doc.createElement("FormatInteger");
		newNode1.setTextContent(value);
		newNode.appendChild(newNode1);
		Node newNode2 = doc.createElement("LimitAppliesToCd");
		newNode2.setTextContent("CSL");
		newNode.appendChild(newNode2);

		TransformerFactory transformerFactory = TransformerFactory.newInstance();
		Transformer transformer = transformerFactory.newTransformer();
		DOMSource source = new DOMSource(doc);
		StreamResult result = new StreamResult(new File(file));
		transformer.transform(source, result);

	}

	public static Document loadXMLFromString(String xml) throws Exception {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		InputSource is = new InputSource(new StringReader(xml));
		return builder.parse(is);
	}
	
	public static String getNodeValue(String requestXML, String xmlPath) throws Exception {
		Document xmlDoc = loadXMLFromString(requestXML);

		XPath xpath = XPathFactory.newInstance().newXPath();
		Node node = (Node) xpath.evaluate(xmlPath, xmlDoc, XPathConstants.NODE);

		return node.getTextContent();

	}


}


