{
  "integrationId": "TSAS",
  "direction": "OUTBOUND",
  "mappings": [

    {
      "source": "TF004",
      "target": "uuMid",
      "masking": false
    },

    {
      "source": "TF075",
      "target": "unit",
      "masking": false
    },

    {
      "source": "TF076",
      "target": "business",
      "masking": false
    },

    {
      "source": "TF077",
      "target": "application",
      "masking": false
    },

    {
      "source": "TF035",
      "target": "type",
      "masking": false
    },

    {
      "source": "TF078",
      "target": "service",
      "masking": false
    },

    {
      "source": "TF079",
      "target": "userRef",
      "masking": false
    },

    {
      "source": "TF080",
      "target": "direction",
      "masking": false
    },

    {
      "source": "TF023",
      "target": "reference",
      "masking": false
    },


    {
      "source": "TF008",
      "target": "details.msgType",
      "masking": false
    },

    {
      "source": "TF001",
      "target": "details.country",
      "masking": false
    },

    {
      "source": "TF009",
      "target": "details.pan",
      "masking": true
    },

    {
      "source": "TF010",
      "target": "details.processingCode",
      "masking": false
    },

    {
      "source": "TF011",
      "target": "details.txnAmount",
      "masking": false
    },

    {
      "source": "TF012",
      "target": "details.holderBillingAmount",
      "masking": false
    },

    {
      "source": "TF018",
      "target": "details.acquiringCountryCode",
      "masking": false
    },

    {
      "source": "TF022",
      "target": "details.institutionCode",
      "masking": false
    },

    {
      "source": "TF023",
      "target": "details.referenceNumber",
      "masking": false
    },

    {
      "source": "TF026",
      "target": "details.cardAcceptName",
      "masking": false
    },

    {
      "source": "TF032",
      "target": "details.currency",
      "masking": false
    },

    {
      "source": "TF033",
      "target": "details.cardHolderCurrency",
      "masking": false
    },

    {
      "source": "TF036",
      "target": "details.senderRef",
      "masking": false
    },

    {
      "source": "TF037",
      "target": "details.senderAccount",
      "masking": true
    },

    {
      "source": "TF038",
      "target": "details.senderName",
      "masking": true
    },

    {
      "source": "TF053",
      "target": "details.recipientName",
      "masking": true
    },

    {
      "source": "TF040",
      "target": "details.senderCity",
      "masking": false
    },

    {
      "source": "TF041",
      "target": "details.senderState",
      "masking": false
    },

    {
      "source": "TF042",
      "target": "details.senderCountry",
      "masking": false
    },

    {
      "source": "TF043",
      "target": "details.sourceOfFund",
      "masking": false
    },

    {
      "source": "TF016",
      "target": "details.transactionDate",
      "masking": false
    },

    {
      "source": "TF028",
      "target": "details.obService1",
      "masking": false
    },

    {
      "source": "TF029",
      "target": "details.obService2",
      "masking": false
    },

    {
      "source": "TF054",
      "target": "details.recipientAddress",
      "masking": true
    },

    {
      "source": "TF055",
      "target": "details.recipientCity",
      "masking": false
    },

    {
      "source": "TF056",
      "target": "details.provinceCode",
      "masking": false
    },

    {
      "source": "TF057",
      "target": "details.recipientCountry",
      "masking": false
    },

    {
      "source": "TF058",
      "target": "details.recipientPostalCode",
      "masking": true
    },

    {
      "source": "TF060",
      "target": "details.recipientDob",
      "masking": true
    },

    {
      "source": "TF061",
      "target": "details.recipientAcctNumber",
      "masking": true
    },

    {
      "source": "TF038",
      "target": "details.senderFirstName",
      "masking": true
    },

    {
      "source": "TF044",
      "target": "details.senderPostalCode",
      "masking": true
    },

    {
      "source": "TF039",
      "target": "details.senderAddress",
      "masking": true
    },

    {
      "source": "TF068",
      "target": "details.additionalMessage",
      "masking": true
    },

    {
      "source": "TF043",
      "target": "details.fundingSource",
      "masking": false
    },

    {
      "source": "TF024",
      "target": "details.institutionId",
      "masking": false
    },

    {
      "source": "TF048",
      "target": "details.senderIdNumber",
      "masking": true
    },

    {
      "source": "TF051",
      "target": "details.senderNationality",
      "masking": false
    },

    {
      "source": "TF052",
      "target": "details.sendCob",
      "masking": false
    },

    {
      "source": "TF046",
      "target": "details.senderDob",
      "masking": true
    },

    {
      "source": "TF049",
      "target": "details.senderIdCountryCode",
      "masking": false
    },

    {
      "source": "TF063",
      "target": "details.recipientIdtNo",
      "masking": true
    },

    {
      "source": "TF064",
      "target": "details.recipientIdtCc",
      "masking": false
    },

    {
      "source": "TF065",
      "target": "details.recipientExpiry",
      "masking": true
    },

    {
      "source": "TF066",
      "target": "details.recipientNationality",
      "masking": false
    },

    {
      "source": "TF067",
      "target": "details.recipientCob",
      "masking": false
    },

    {
      "source": "TF070",
      "target": "details.transactionPurpose",
      "masking": false
    },

    {
      "source": "TF073",
      "target": "details.acceptorLegalBusinessName",
      "masking": false
    },

    {
      "source": "TF074",
      "target": "details.paymentFacilitatorName",
      "masking": false
    },


    {
      "source": "TF053",
      "target": "recipientName",
      "masking": true
    },

    {
      "source": "TF038",
      "target": "senderName",
      "masking": true
    },

    {
      "source": "TF008",
      "target": "msgType",
      "masking": false
    },

    {
      "source": "TF052",
      "target": "sendCob",
      "masking": false
    },

    {
      "source": "TF009",
      "target": "pan",
      "masking": true
    },

    {
      "source": "TF001",
      "target": "country",
      "masking": false
    },

    {
      "source": "TF023",
      "target": "referenceNumber",
      "masking": false
    },

    {
      "source": "TF054",
      "target": "streetAddress",
      "masking": true
    },

    {
      "source": "TF055",
      "target": "recipientCity",
      "masking": false
    },

    {
      "source": "TF056",
      "target": "provinceCode",
      "masking": false
    },

    {
      "source": "TF039",
      "target": "senderAddress",
      "masking": true
    },

    {
      "source": "TF042",
      "target": "senderCountry",
      "masking": false
    },

    {
      "source": "TF040",
      "target": "senderCity",
      "masking": false
    },

    {
      "source": "TF037",
      "target": "senderAccount",
      "masking": true
    },

    {
      "source": "TF060",
      "target": "recipientDob",
      "masking": true
    },

    {
      "source": "TF038",
      "target": "senderFirstName",
      "masking": true
    },

    {
      "source": "TF041",
      "target": "senderState",
      "masking": false
    },

    {
      "source": "TF046",
      "target": "senderDob",
      "masking": true
    },

    {
      "source": "TF011",
      "target": "txnAmount",
      "masking": false
    },

    {
      "source": "TF053",
      "target": "firstName",
      "masking": true
    },

    {
      "source": "TF026",
      "target": "cardAcceptName",
      "masking": false
    },

    {
      "source": "TF010",
      "target": "processingCode",
      "masking": false
    },

    {
      "source": "TF016",
      "target": "transactionDate",
      "masking": false
    },

    {
      "source": "TF018",
      "target": "acquiringCountryCode",
      "masking": false
    },

    {
      "source": "TF057",
      "target": "recipientCountry",
      "masking": false
    },

    {
      "source": "TF044",
      "target": "senderPostalCode",
      "masking": true
    },

    {
      "source": "TF054",
      "target": "recipientAddress",
      "masking": true
    },

    {
      "source": "TF051",
      "target": "senderNationality",
      "masking": false
    },

    {
      "source": "TF049",
      "target": "senderIdCountryCode",
      "masking": false
    },

    {
      "source": "TF058",
      "target": "recipientPostalCode",
      "masking": true
    },

    {
      "source": "TF033",
      "target": "cardHolderCurrency",
      "masking": false
    },

    {
      "source": "TF012",
      "target": "holderBillingAmount",
      "masking": false
    },

    {
      "source": "TF022",
      "target": "institutionCode",
      "masking": false
    },

    {
      "source": "TF063",
      "target": "recipientIdTNo",
      "masking": true
    },

    {
      "source": "TF064",
      "target": "recipientIdTCc",
      "masking": false
    },

    {
      "source": "TF065",
      "target": "recipientExpiry",
      "masking": true
    },

    {
      "source": "TF048",
      "target": "senderIdNumber",
      "masking": true
    },

    {
      "source": "TF067",
      "target": "recipientCob",
      "masking": false
    },

    {
      "source": "TF028",
      "target": "obService1",
      "masking": false
    },

    {
      "source": "TF043",
      "target": "fundingSource",
      "masking": false
    },

    {
      "source": "TF024",
      "target": "institutionId",
      "masking": false
    },

    {
      "source": "TF036",
      "target": "senderRef",
      "masking": false
    },

    {
      "source": "TF029",
      "target": "obService2",
      "masking": false
    },

    {
      "source": "TF062",
      "target": "recipientIdType",
      "masking": true
    },

    {
      "source": "TF061",
      "target": "recipientAcctNumber",
      "masking": true
    },

    {
      "source": "TF066",
      "target": "recipientNationality",
      "masking": false
    },

    {
      "source": "TF068",
      "target": "additionalMessage",
      "masking": true
    },

    {
      "source": "TF070",
      "target": "transactionPurpose",
      "masking": false
    },

    {
      "source": "TF032",
      "target": "currency",
      "masking": false
    },

    {
      "source": "TF006",
      "target": "txntype",
      "masking": false
    },

    {
      "source": "TF016",
      "target": "valuedate",
      "masking": false
    },

    {
      "source": "TF081",
      "target": "SENDER",
      "masking": false
    },

    {
      "source": "TF082",
      "target": "RECEIVER",
      "masking": false
    }

  ]
}