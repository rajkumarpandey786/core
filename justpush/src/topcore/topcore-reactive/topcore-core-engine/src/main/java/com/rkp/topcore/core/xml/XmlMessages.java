package com.rkp.topcore.core.xml;

public final class XmlMessages {

    private XmlMessages() {
    }


    /*
     * =========================================================
     * ECHO RESPONSE
     * =========================================================
     *
     * Request:
     *
     * <root>
     *     <rec>
     *         <Echo>ECHO</Echo>
     *     </rec>
     * </root>
     *
     * Response:
     *
     * <root>
     *     <rec>
     *         <Echo>ECHO RECEIVED</Echo>
     *     </rec>
     * </root>
     */
    public static String echoResponse() {

        return "<root>"
                + "<rec>"
                + "<Echo>ECHO RECEIVED</Echo>"
                + "</rec>"
                + "</root>";
    }


    /*
     * =========================================================
     * ERROR RESPONSE
     * =========================================================
     *
     * Generic C400 error response.
     *
     * Example:
     *
     * responseCode    = 96
     * responseMessage = SYSTEM_BUSY
     *
     * becomes:
     *
     * <root>
     *     <rec>
     *         <responseCode>96</responseCode>
     *         <responseMessage>SYSTEM_BUSY</responseMessage>
     *     </rec>
     * </root>
     */
    public static String errorResponse(
            String responseCode,
            String responseMessage) {

        String code =
                responseCode == null
                        ? "96"
                        : responseCode;

        String message =
                responseMessage == null
                        ? "SYSTEM_ERROR"
                        : responseMessage;


        return "<root>"
                + "<rec>"
                + "<Ostatus>"
                + escapeXml(code)
                + "</Ostatus>"
                + "<RespMsg>"
                + escapeXml(message)
                + "</RespMsg>"
                + "</rec>"
                + "</root>";
    }


    /*
     * =========================================================
     * XML ESCAPING
     * =========================================================
     */
    private static String escapeXml(
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
}
