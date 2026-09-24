package com.rkp.topcore.core.xml;

import java.io.StringReader;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

@Component
public class XmlCodec {

    private final XmlMapper mapper =
            XmlMapper.builder(
                    new XmlFactory()
            ).build();


    /*
     * =========================================================
     * XML → MAP
     * =========================================================
     *
     * Uses StAX.
     *
     * IMPORTANT:
     *
     * This does NOT build a DOM tree.
     *
     * The parser maintains only the current XML
     * element path.
     *
     * Example:
     *
     * <root>
     *   <rec>
     *     <Country>GH</Country>
     *   </rec>
     * </root>
     *
     * becomes:
     *
     * root.rec.Country = GH
     *
     * This matches the .pan source definitions:
     *
     * root.rec.Country
     * root.rec.ReqID
     * root.rec.CardNbr
     * etc.
     */
    public Map<String, String> parse(
            String xml) {

        if (xml == null ||
                xml.isBlank()) {

            throw new IllegalArgumentException(
                    "XML cannot be null or blank"
            );
        }


        try {

            XMLStreamReader reader =
                    mapper.getFactory()
                            .getXMLInputFactory()
                            .createXMLStreamReader(
                                    new StringReader(xml)
                            );


            Map<String, String> result =
                    new LinkedHashMap<>();


            /*
             * Stores the current XML element hierarchy.
             *
             * Example:
             *
             * root
             * rec
             * Country
             */
            Deque<String> path =
                    new ArrayDeque<>();


            while (reader.hasNext()) {

                int event =
                        reader.next();


                /*
                 * =================================================
                 * START ELEMENT
                 * =================================================
                 */

                if (event ==
                        XMLStreamConstants.START_ELEMENT) {

                    path.addLast(
                            reader.getLocalName()
                    );

                    continue;
                }


                /*
                 * =================================================
                 * TEXT
                 * =================================================
                 */

                if (event ==
                        XMLStreamConstants.CHARACTERS
                        || event ==
                        XMLStreamConstants.CDATA) {

                    if (path.isEmpty()) {

                        continue;
                    }


                    String value =
                            reader.getText();


                    if (value == null) {

                        continue;
                    }


                    value =
                            value.trim();


                    /*
                     * Empty XML elements are intentionally
                     * ignored here.
                     *
                     * Example:
                     *
                     * <ErrorCode></ErrorCode>
                     *
                     * does not create a map entry.
                     */
                    if (value.isEmpty()) {

                        continue;
                    }


                    /*
                     * Build the complete source path.
                     *
                     * Example:
                     *
                     * root.rec.Country
                     */
                    String sourcePath =
                            buildPath(path);


                    /*
                     * Root/container elements do not
                     * normally contain business values.
                     */
                    if (!"root".equals(
                            path.peekLast())) {

                        result.put(
                                sourcePath,
                                value
                        );
                    }

                    continue;
                }


                /*
                 * =================================================
                 * END ELEMENT
                 * =================================================
                 */

                if (event ==
                        XMLStreamConstants.END_ELEMENT) {

                    if (!path.isEmpty()) {

                        path.removeLast();
                    }
                }
            }


            reader.close();


            return result;


        } catch (Exception e) {

            throw new IllegalStateException(
                    "Invalid XML",
                    e
            );
        }
    }


    /*
     * =========================================================
     * BUILD XML PATH
     * =========================================================
     *
     * Converts:
     *
     * [root, rec, Country]
     *
     * into:
     *
     * root.rec.Country
     */
    private String buildPath(
            Deque<String> path) {

        StringBuilder result =
                new StringBuilder(64);


        boolean first =
                true;


        for (String element :
                path) {

            if (!first) {

                result.append('.');
            }


            result.append(element);


            first = false;
        }


        return result.toString();
    }


    /*
     * =========================================================
     * MAP → XML
     * =========================================================
     *
     * Generic recursive XML builder.
     *
     * Map<String, ?> accepts both:
     *
     * Map<String,String>
     * Map<String,Object>
     */
    public String build(
            Map<String, ?> fields) {

        if (fields == null) {

            throw new IllegalArgumentException(
                    "XML fields cannot be null"
            );
        }

        StringBuilder sb =
                new StringBuilder(512);

        for (Map.Entry<String, ?> entry :
                fields.entrySet()) {

            appendValue(
                    sb,
                    entry.getKey(),
                    entry.getValue()
            );
        }

        return sb.toString();
    }


    /*
     * =========================================================
     * RECURSIVE VALUE → XML
     * =========================================================
     */
    private void appendValue(
            StringBuilder sb,
            String name,
            Object value) {

        if (name == null ||
                name.isBlank()) {

            return;
        }


        sb.append('<')
          .append(name)
          .append('>');


        /*
         * ---------------------------------------------------------
         * NESTED MAP
         * ---------------------------------------------------------
         */

        if (value instanceof Map<?, ?> nestedMap) {

            for (Map.Entry<?, ?> entry :
                    nestedMap.entrySet()) {

                if (entry.getKey() == null) {

                    continue;
                }


                appendValue(
                        sb,
                        String.valueOf(
                                entry.getKey()
                        ),
                        entry.getValue()
                );
            }
        }


        /*
         * ---------------------------------------------------------
         * COLLECTION
         * ---------------------------------------------------------
         */

        else if (value instanceof Iterable<?> iterable) {

            for (Object item :
                    iterable) {

                if (item instanceof Map<?, ?> itemMap) {

                    appendAnonymousMapChildren(
                            sb,
                            itemMap
                    );

                } else if (item != null) {

                    sb.append(
                            escapeXml(
                                    String.valueOf(
                                            item
                                    )
                            )
                    );
                }
            }
        }


        /*
         * ---------------------------------------------------------
         * SIMPLE VALUE
         * ---------------------------------------------------------
         */

        else if (value != null) {

            sb.append(
                    escapeXml(
                            String.valueOf(
                                    value
                            )
            ));
        }


        sb.append("</")
          .append(name)
          .append('>');
    }


    /*
     * =========================================================
     * MAP CHILDREN
     * =========================================================
     */
    private void appendAnonymousMapChildren(
            StringBuilder sb,
            Map<?, ?> map) {

        for (Map.Entry<?, ?> entry :
                map.entrySet()) {

            if (entry.getKey() == null) {

                continue;
            }


            appendValue(
                    sb,
                    String.valueOf(
                            entry.getKey()
                    ),
                    entry.getValue()
            );
        }
    }


    /*
     * =========================================================
     * XML ESCAPING
     * =========================================================
     */
    private String escapeXml(
            String value) {

        if (value == null) {

            return "";
        }


        return value
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }


    /*
     * =========================================================
     * MESSAGE TYPE
     * =========================================================
     *
     * Kept for compatibility with the current gateway.
     */
    public String type(
            Map<String, String> map) {

        if (map == null) {

            return "";
        }


        return map.getOrDefault(
                "rec",
                ""
        );
    }


    /*
     * =========================================================
     * MESSAGE ID
     * =========================================================
     */
    public String msgId(
            Map<String, String> map) {

        if (map == null) {

            return "";
        }


        return map.getOrDefault(
                "msgId",
                ""
        );
    }


    /*
     * =========================================================
     * ECHO RESPONSE
     * =========================================================
     */
    public String buildEchoResponse1() {

        return "<root>"
                + "<rec>"
                + "<Echo>ECHO RECEIVED</Echo>"
                + "</rec>"
                + "</root>";
    }
}
