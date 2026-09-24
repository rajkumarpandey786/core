{
  "integrationId": "TSAS",

  "direction": "INBOUND",

  "mappings": [

    {
      "source": "systemID",
      "target": "TF002",
      "masking": false
    },

    {
      "source": "transactionReferenceNumber",
      "target": "TF004",
      "masking": false
    },

    {
      "source": "responseCode",
      "target": "TF006",
      "masking": false
    },

    {
      "source": "messageStatus",
      "target": "TF007",
      "masking": false
    },

    {
      "source": "responseDescription",
      "target": "TF010",
      "masking": false
    },

    {
      "source": "callbackUrl",
      "target": "TF011",
      "masking": false
    },

    {
      "source": "screeningRequestDate",
      "target": "TF012",
      "masking": false
    },

    {
      "source": "messageChecksum",
      "target": "TF013",
      "masking": false
    },

    {
      "source": "statusOwner",
      "target": "TF014",
      "masking": false
    },

    {
      "source": "statusComment",
      "target": "TF015",
      "masking": false
    },

    {
      "source": "statusLabel",
      "target": "TF016",
      "masking": false
    }

  ]
}