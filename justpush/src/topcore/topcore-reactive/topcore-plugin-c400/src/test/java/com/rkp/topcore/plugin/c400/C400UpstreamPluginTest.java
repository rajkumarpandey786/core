package com.rkp.topcore.plugin.c400;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.rkp.topcore.canonical.document.CanonicalDocument;
import com.rkp.topcore.canonical.document.CanonicalDocumentFactory;

@SpringBootTest(classes = C400PluginTestConfiguration.class)
class C400UpstreamPluginTest {

    @Autowired
    private C400UpstreamPlugin plugin;

    @Autowired
    private CanonicalDocumentFactory documentFactory; 
    @Test
    void shouldConvertC400XmlToCanonicalDocument() {

        String xml =
                "<root><rec>"
                + "<Country>GH</Country>"
                + "<ReqID>notifyTSAS</ReqID>"
                + "<FunctionName>CPHTSAAS</FunctionName>"
                + "<externalTransactionId>TSA2881789288760858</externalTransactionId>"
                + "<Chnl>TSAS</Chnl>"
                + "<type>DOM</type>"
                + "<status>Approve</status>"
                + "<MTI>0100</MTI>"
                + "<CardNbr>486056******8643</CardNbr>"
                + "<ProcessingCode>260000</ProcessingCode>"
                + "<TrxnAmt>0000000006.00</TrxnAmt>"
                + "<BillingAmt>0000000006.00</BillingAmt>"
                + "<TransmissionDateTime>0502162109</TransmissionDateTime>"
                + "<SysTrace>287796</SysTrace>"
                + "<MCC>4829</MCC>"
                + "<AcqCountryCode>288</AcqCountryCode>"
                + "<POSEntryMode>010</POSEntryMode>"
                + "<AcqInstID>416080</AcqInstID>"
                + "<RetrievalRefNbr>312216287796</RetrievalRefNbr>"
                + "<TerminalID>EXPAYGH</TerminalID>"
                + "<MerchantID>EXPAYGH</MerchantID>"
                + "<MerchantName>exPay Accra GH</MerchantName>"
                + "<TrxnCurrCode>936</TrxnCurrCode>"
                + "<BillingCurrCode>936</BillingCurrCode>"
                + "<SchemeTrxnID>583122588698861</SchemeTrxnID>"
                + "<TrxnType>PP</TrxnType>"
                + "<SenderAcctNbr>312216287796</SenderAcctNbr>"
                + "<SenderCountry>GHA</SenderCountry>"
                + "<FundingSource>02</FundingSource>"
                + "<RecipientName>Joseph Ampah</RecipientName>"
                + "<RecipientCountry>GHA</RecipientCountry>"
                + "<AcceptorLegalBusinessName>UN</AcceptorLegalBusinessName>"
                + "<PaymentFacilitatorName>UN</PaymentFacilitatorName>"
                + "</rec></root>";

        assertEquals("C400", plugin.integrationId());

        CanonicalDocument document =
                plugin.toCanonical(xml);

        assertEquals("GH", document.getTF("TF001"));
        assertEquals("notifyTSAS", document.getTF("TF002"));
        assertEquals("CPHTSAAS", document.getTF("TF003"));
        assertEquals(
                "TSA2881789288760858",
                document.getTF("TF004"));
        assertEquals("TSAS", document.getTF("TF005"));
        assertEquals("DOM", document.getTF("TF006"));
        assertEquals("Approve", document.getTF("TF007"));
        assertEquals("0100", document.getTF("TF008"));
        assertEquals(
                "486056******8643",
                document.getTF("TF009"));
        assertEquals("260000", document.getTF("TF010"));

        assertEquals(
                "Joseph Ampah",
                document.getTF("TF053"));

        assertEquals(
                "GHA",
                document.getTF("TF057"));

        assertEquals(
                "UN",
                document.getTF("TF073"));

        assertEquals(
                "UN",
                document.getTF("TF074"));
    }

    @Test
    void shouldConvertCanonicalDocumentToC400Xml() {

    	CanonicalDocument document =
    	        documentFactory.createResponse();

        document.addTF("TF001", "GH");
        document.addTF("TF002", "notifyTSAS");
        document.addTF("TF003", "CPHTSAAS");
        document.addTF(
                "TF004",
                "TSA2881789288760858");
        document.addTF("TF005", "TSAS");
        document.addTF("TF006", "Approved");
        document.addTF("TF007", "Transaction approved");
        document.addTF("TF008", "00");
        document.addTF("TF009", "ERR001");

        System.out.println("TF009 = " + document.getTF("TF009"));
        String xml =
                plugin.fromCanonical(document);
        
        System.out.println("GENERATED XML:");
        System.out.println(xml);

        assertTrue(xml.contains("<root>"));
        assertTrue(xml.contains("<rec>"));
        assertTrue(xml.contains("<Country>GH</Country>"));
        assertTrue(xml.contains(
                "<ReqID>notifyTSAS</ReqID>"));
        assertTrue(xml.contains(
                "<FunctionName>CPHTSAAS</FunctionName>"));
        assertTrue(xml.contains(
                "<externalTransactionId>"
                + "TSA2881789288760858"
                + "</externalTransactionId>"));
        assertTrue(xml.contains(
                "<Chnl>TSAS</Chnl>"));
        assertTrue(xml.contains(
                "<Ostatus>Approved</Ostatus>"));
        assertTrue(xml.contains(
                "<RespMsg>Transaction approved</RespMsg>"));
        assertTrue(xml.contains(
                "<ErrorCode>00</ErrorCode>"));
        assertTrue(xml.contains(
                "<ErrorDesc>ERR001</ErrorDesc>"));
    }
}