package com.nokia.wordprocessor;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;

import javax.swing.JOptionPane;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLGenerator {

	private static Node rootNode;
	private static String internalTagAttrId = "";
	private static boolean isInternalWordFound = false;
	private static boolean isAppendixFound = false;
	private static String PARA_TAG = "w:p";
	private static String RUN_TAG = "w:r";
	private static String TEXT_TAG = "w:t";
	private static String TABLE_TAG = "w:tbl";
	private static String PARA_RSIDR_ATTRIBUTE = "w:rsidR";
	private static String OUTPUT_FILE_PATH = "D:/userdata/savaliya/Desktop/Zip/word/document.xml";
	private static String INPUT_FILE_PATH = "D:/userdata/savaliya/Desktop/Zip/word/document.xml";
	static List<Node> removedTagList = new ArrayList<>();
	static List<Node> allTagList;

	/*public static void main(String[] args) {
		processXMLfile();

	}*/

public static void processXMLfile(String inputFilePath) {
		try {
			File inputFile = new File(inputFilePath);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			Document doc = dBuilder.parse(inputFile);
			doc.getDocumentElement().normalize();

			rootNode = (doc.getFirstChild()).getFirstChild();
			System.out.println("Root element :" + doc.getDocumentElement().getNodeName());

			allTagList = getAllSequentialTag();

			for (int i = 0; i < allTagList.size(); i++) {
				Node node = allTagList.get(i);
				if (node.getNodeName().equals(PARA_TAG)) {
					if (isInternalWordFound) {
						String currentNodeId = node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE).getNodeValue();
						if (currentNodeId.equals(internalTagAttrId)) {
							boolean nextTableNode = checkTableNodePossibility(i + 1);
							if (nextTableNode) {
								int incremant = addPNodeToRemoveList(i + 1);
								addNodeToRemoveNodeList(node);
								i = i + incremant;
								continue;
							} else {
								isInternalWordFound = false;
								internalTagAttrId = "";
							}
						} else {
							addNodeToRemoveNodeList(node);
							continue;
						}
					} else if (isAppendixFound) {
						removeContenForAppendix(i - 1);
						break;
					}

					getParaContent(node);

				} else if (node.getNodeName().equals(TABLE_TAG) && isInternalWordFound) {
					addNodeToRemoveNodeList(node);
					isInternalWordFound = false;
				}
			}

			removeIntermediateContent2(removedTagList);
			generateNewXMLfile(doc,inputFilePath);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static int addPNodeToRemoveList(int index) {
		int counter = 0;
		for (int i = index; i < allTagList.size(); i++) {
			Node node = allTagList.get(i);
			if (node.getNodeName().equals(TABLE_TAG))
				return counter;
			else if (node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE).getNodeValue().equals(internalTagAttrId)) {
				addNodeToRemoveNodeList(node);
				counter++;
			}
		}
		return counter;
	}

	private static void addNodeToRemoveNodeList(Node node) {
		System.out.print("addRemoveList method called \n");
		if (node.getAttributes() != null && node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE) != null) {
			System.out.println(node.getNodeName() + "---> "
					+ node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE).getNodeValue() + "added to removed list");
		} else {
			System.out.println(node.getNodeName() + "---> " + " attr not found  " + "added to removed list");
		}

		removedTagList.add(node);
	}

	private static boolean checkTableNodePossibility(int index) {
		for (int i = index; i < allTagList.size(); i++) {
			Node node = allTagList.get(i);
			if (node.getNodeName().equals(TABLE_TAG))
				return true;
			else if (node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE).getNodeValue().equals(internalTagAttrId))
				continue;
			else
				return false;
		}
		return false;
	}

	private static void getParaContent(Node node) {
		String content = "";

		System.out.println("TAGID--->" + node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE).getNodeValue());

		Element el = (Element) node;
		NodeList nd = el.getElementsByTagName(TEXT_TAG);
		for (int i = 0; i < nd.getLength(); i++) {
			// System.out.println(nd.item(i).getNodeName());
			content += el.getTextContent();
		}
		// System.out.println("CONTENT--->=" + content);

		if (isAppendixFound(content)) {
			System.out.println("CONTENT=" + content);
			System.out.println("APPENDIX (internal) found");
			isAppendixFound = true;
		} else if (content.contains("(internal)")) {
			isInternalWordFound = true;
			internalTagAttrId = node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE).getNodeValue();
			addNodeToRemoveNodeList(node);
			System.out.println("CONTENT=" + content + "\ninternal found here" + "  <------>");
			System.out.println("TAG NAME= " + node.getNodeName() + "  TAG_ID=" + internalTagAttrId
					+ "   <><><>this tag is added to remove tag list");
		} else {
			System.out.println("CONTENT=" + content + "\n\n");
		}
	}

	private static boolean isAppendixFound(String content) {
		// Appendix (internal) PAGEREF _Toc432156606 \h 327Appendix (internal)
		// PAGEREF _Toc432156606 \h 327Appendix (internal) PAGEREF _Toc432156606
		// \h 32
		if (content.contains("Appendix (internal)") && !content.contains("PAGEREF"))
			return true;
		return false;
	}

	private static void generateNewXMLfile(Document doc, String outputFilePath) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			System.out.println("-----------Modified File generated on o/p path-----------");
			StreamResult consoleResult = new StreamResult(new File(outputFilePath));
			transformer.transform(source, consoleResult);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static List<Node> getAllSequentialTag() {
		Node childNode = rootNode.getFirstChild();
		List<Node> allNode = new ArrayList<>();
		while (childNode.getNextSibling() != null) {
			childNode = childNode.getNextSibling();
			allNode.add(childNode);
			// System.out.println(childNode.getNodeName());
		}
		return allNode;
	}

	private static void removeContenForAppendix(int index) {
		System.out.println("-------------------removing content for appendix------------");
		for (int j = index; j < allTagList.size(); j++) {
			Node node = allTagList.get(j);
			if (node.getAttributes() != null && node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE) != null) {
				System.out.println("J = " + j + "  TAG_NAME:" + node.getNodeName() + "  TAGID--->"
						+ node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE).getNodeValue());
			} else
				System.out.println("J = " + j + "  TAG_NAME:" + node.getNodeName() + "  TAGID---> not found");

			rootNode.removeChild(node);
		}
	}

	private static void removeIntermediateContent2(List<Node> pTagList) {
		System.out.println("-------------------removing internal content pTagList.size() = " + pTagList.size());
		for (int j = 0; j < pTagList.size(); j++) {
			Node node = pTagList.get(j);
			if (node.getAttributes() != null && node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE) != null) {
				System.out.println("J = " + j + "  TAG_NAME:" + node.getNodeName() + "  TAGID--->"
						+ node.getAttributes().getNamedItem(PARA_RSIDR_ATTRIBUTE).getNodeValue());
			} else
				System.out.println("J = " + j + "  TAG_NAME:" + node.getNodeName() + "  TAGID---> not found");

			rootNode.removeChild(node);
		}
	}
}