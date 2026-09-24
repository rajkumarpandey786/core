package com.rkp.topcore.core.security;

import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayDeque;
import java.util.Deque;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;

import org.springframework.stereotype.Component;

import com.rkp.topcore.canonical.runtime.RuntimeMapping;

@Component
public final class XmlMaskingUtility {

    private static final String MASK_VALUE = "********";

    private final XMLInputFactory inputFactory;

    private final XMLOutputFactory outputFactory;


    public XmlMaskingUtility() {

        inputFactory =
                XMLInputFactory.newFactory();

        outputFactory =
                XMLOutputFactory.newFactory();

        /*
         * Security:
         *
         * The inbound C400 message must never be allowed
         * to resolve external entities or external DTDs.
         */
        setPropertyIfSupported(
                inputFactory,
                XMLInputFactory.SUPPORT_DTD,
                false
        );

        setPropertyIfSupported(
                inputFactory,
                "javax.xml.stream.isSupportingExternalEntities",
                false
        );
    }


    /**
     * Masks sensitive fields in an XML message according
     * to the supplied RuntimeMapping.
     *
     * The supplied XML string is never modified.
     *
     * Example:
     *
     * root.rec.CardNbr
     *
     * configured as:
     *
     * masking=true
     *
     * becomes:
     *
     * <CardNbr>********</CardNbr>
     */
    public String mask(
            String xml,
            RuntimeMapping runtimeMapping) {

        if (xml == null || xml.isBlank()) {
            return xml;
        }

        if (runtimeMapping == null) {
            return xml;
        }

        /*
         * No masked fields means there is nothing to do.
         *
         * This avoids parsing XML unnecessarily.
         */
        if (runtimeMapping
                .getMaskedSources()
                .isEmpty()) {

            return xml;
        }

        try {

            XMLStreamReader reader =
                    inputFactory.createXMLStreamReader(
                            new StringReader(xml)
                    );

            StringWriter writer =
                    new StringWriter(
                            xml.length()
                    );

            XMLStreamWriter xmlWriter =
                    outputFactory.createXMLStreamWriter(
                            writer
                    );

            Deque<String> path =
                    new ArrayDeque<>();

            while (reader.hasNext()) {

                int event =
                        reader.next();

                switch (event) {

                    case XMLStreamConstants.START_DOCUMENT:

                        xmlWriter.writeStartDocument();

                        break;


                    case XMLStreamConstants.END_DOCUMENT:

                        xmlWriter.writeEndDocument();

                        break;


                    case XMLStreamConstants.START_ELEMENT:

                        String elementName =
                                reader.getLocalName();

                        path.addLast(
                                elementName
                        );

                        xmlWriter.writeStartElement(
                                elementName
                        );

                        /*
                         * Preserve namespace information
                         * when present.
                         */
                        writeNamespaces(
                                reader,
                                xmlWriter
                        );

                        writeAttributes(
                                reader,
                                xmlWriter
                        );

                        break;


                    case XMLStreamConstants.CHARACTERS:

                        String currentPath =
                                buildPath(path);

                        if (runtimeMapping.isMasked(
                                currentPath)) {

                            xmlWriter.writeCharacters(
                                    MASK_VALUE
                            );

                        } else {

                            xmlWriter.writeCharacters(
                                    reader.getText()
                            );
                        }

                        break;


                    case XMLStreamConstants.CDATA:

                        String cdataPath =
                                buildPath(path);

                        if (runtimeMapping.isMasked(
                                cdataPath)) {

                            xmlWriter.writeCharacters(
                                    MASK_VALUE
                            );

                        } else {

                            xmlWriter.writeCData(
                                    reader.getText()
                            );
                        }

                        break;


                    case XMLStreamConstants.END_ELEMENT:

                        xmlWriter.writeEndElement();

                        path.removeLast();

                        break;


                    case XMLStreamConstants.COMMENT:

                        xmlWriter.writeComment(
                                reader.getText()
                        );

                        break;


                    case XMLStreamConstants.PROCESSING_INSTRUCTION:

                        xmlWriter.writeProcessingInstruction(
                                reader.getPITarget(),
                                reader.getPIData()
                        );

                        break;


                    default:

                        /*
                         * Other XML events do not need
                         * special handling for C400.
                         */
                        break;
                }
            }

            xmlWriter.flush();
            xmlWriter.close();
            reader.close();

            return writer.toString();

        } catch (XMLStreamException e) {

            /*
             * IMPORTANT:
             *
             * Never include the original XML in the
             * exception message because it can contain
             * PAN/account/PII data.
             */
            throw new IllegalStateException(
                    "Unable to mask XML payload",
                    e
            );
        }
    }


    /**
     * Build the current XML path.
     *
     * Example:
     *
     * [root, rec, CardNbr]
     *
     * becomes:
     *
     * root.rec.CardNbr
     */
    private String buildPath(
            Deque<String> path) {

        StringBuilder result =
                new StringBuilder(
                        path.size() * 16
                );

        for (String element :
                path) {

            if (result.length() > 0) {
                result.append('.');
            }

            result.append(element);
        }

        return result.toString();
    }


    /**
     * Copy XML attributes without changing
     * their values.
     */
    private void writeAttributes(
            XMLStreamReader reader,
            XMLStreamWriter writer)
            throws XMLStreamException {

        for (int i = 0;
             i < reader.getAttributeCount();
             i++) {

            String prefix =
                    reader.getAttributePrefix(i);

            String namespace =
                    reader.getAttributeNamespace(i);

            String localName =
                    reader.getAttributeLocalName(i);

            String value =
                    reader.getAttributeValue(i);

            if (namespace != null
                    && !namespace.isBlank()) {

                writer.writeAttribute(
                        prefix,
                        namespace,
                        localName,
                        value
                );

            } else if (prefix != null
                    && !prefix.isBlank()) {

                writer.writeAttribute(
                        prefix,
                        localName,
                        value
                );

            } else {

                writer.writeAttribute(
                        localName,
                        value
                );
            }
        }
    }


    /**
     * Preserve XML namespace declarations.
     */
    private void writeNamespaces(
            XMLStreamReader reader,
            XMLStreamWriter writer)
            throws XMLStreamException {

        for (int i = 0;
             i < reader.getNamespaceCount();
             i++) {

            String prefix =
                    reader.getNamespacePrefix(i);

            String namespace =
                    reader.getNamespaceURI(i);

            if (prefix == null
                    || prefix.isBlank()) {

                writer.writeDefaultNamespace(
                        namespace
                );

            } else {

                writer.writeNamespace(
                        prefix,
                        namespace
                );
            }
        }
    }


    /**
     * Set an XML parser property only when
     * the underlying implementation supports it.
     */
    private void setPropertyIfSupported(
            XMLInputFactory factory,
            String property,
            Object value) {

        try {

            if (factory.isPropertySupported(
                    property)) {

                factory.setProperty(
                        property,
                        value
                );
            }

        } catch (IllegalArgumentException ignored) {

            /*
             * Some StAX implementations expose
             * different optional properties.
             *
             * The core masking functionality
             * does not depend on these optional
             * properties.
             */
        }
    }
}
