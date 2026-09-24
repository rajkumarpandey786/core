package com.rkp.topcore.core.observability;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.List;

import org.xml.sax.InputSource;

public final class XmlMasker {

    private XmlMasker() {
    }

    public static String mask(String xml,
                              List<MaskingRule> rules) {

        try {

            DocumentBuilderFactory factory =
                    DocumentBuilderFactory.newInstance();

            factory.setNamespaceAware(false);

            DocumentBuilder builder =
                    factory.newDocumentBuilder();

            Document document =
                    builder.parse(new InputSource(new StringReader(xml)));

            Element root = document.getDocumentElement();

            for (MaskingRule rule : rules) {

                Node node = findByPath(root, rule.getPath());

                if (node != null) {

                    node.setTextContent(
                            MaskingUtil.applyMask(
                                    node.getTextContent(),
                                    rule.getType()));
                }
            }

            Transformer transformer =
                    TransformerFactory.newInstance().newTransformer();

            transformer.setOutputProperty(
                    OutputKeys.OMIT_XML_DECLARATION,
                    "yes");

            transformer.setOutputProperty(
                    OutputKeys.INDENT,
                    "no");

            StringWriter writer = new StringWriter();

            transformer.transform(
                    new DOMSource(document),
                    new StreamResult(writer));

            return writer.toString();

        } catch (Exception ex) {

            return xml;
        }
    }

    private static Node findByPath(Element root,
                                   String path) {

        if (root == null || path == null || path.isEmpty()) {
            return null;
        }

        String[] parts = path.split("\\\\.");

        int index = 0;

        if (parts.length > 0
                && parts[0].equals(root.getNodeName())) {

            index = 1;
        }

        Node current = root;

        for (; index < parts.length; index++) {

            String part = parts[index];

            Node child = null;

            for (int i = 0; i < current.getChildNodes().getLength(); i++) {

                Node n = current.getChildNodes().item(i);

                if (n.getNodeType() == Node.ELEMENT_NODE
                        && part.equals(n.getNodeName())) {

                    child = n;
                    break;
                }
            }

            if (child == null) {
                return null;
            }

            current = child;
        }

        return current;
    }
}