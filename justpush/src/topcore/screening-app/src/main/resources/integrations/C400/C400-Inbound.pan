{
  "integrationId": "C400",
  "direction": "INBOUND",
  "mappings": [

    {
      "source": "root.rec.Country",
      "target": "TF001",
      "masking": false
    },
    {
      "source": "root.rec.ReqID",
      "target": "TF002",
      "masking": false
    },
    {
      "source": "root.rec.FunctionName",
      "target": "TF003",
      "masking": false
    },
    {
      "source": "root.rec.externalTransactionId",
      "target": "TF004",
      "masking": false
    },
    {
      "source": "root.rec.Chnl",
      "target": "TF005",
      "masking": false
    },
    {
      "source": "root.rec.type",
      "target": "TF006",
      "masking": false
    },
    {
      "source": "root.rec.status",
      "target": "TF007",
      "masking": false
    },
    {
      "source": "root.rec.MTI",
      "target": "TF008",
      "masking": false
    },
    {
      "source": "root.rec.CardNbr",
      "target": "TF009",
      "masking": true
    },
    {
      "source": "root.rec.ProcessingCode",
      "target": "TF010",
      "masking": false
    },
    {
      "source": "root.rec.TrxnAmt",
      "target": "TF011",
      "masking": false
    },
    {
      "source": "root.rec.BillingAmt",
      "target": "TF012",
      "masking": false
    },
    {
      "source": "root.rec.TransmissionDateTime",
      "target": "TF013",
      "masking": false
    },
    {
      "source": "root.rec.SysTrace",
      "target": "TF014",
      "masking": false
    },
    {
      "source": "root.rec.LocalTrxnTime",
      "target": "TF015",
      "masking": false
    },
    {
      "source": "root.rec.LocalTrxnDate",
      "target": "TF016",
      "masking": false
    },
    {
      "source": "root.rec.MCC",
      "target": "TF017",
      "masking": false
    },
    {
      "source": "root.rec.AcqCountryCode",
      "target": "TF018",
      "masking": false
    },
    {
      "source": "root.rec.POSEntryMode",
      "target": "TF019",
      "masking": false
    },
    {
      "source": "root.rec.CardSeqNbr",
      "target": "TF020",
      "masking": true
    },
    {
      "source": "root.rec.POSConditionCode",
      "target": "TF021",
      "masking": false
    },
    {
      "source": "root.rec.AcqInstID",
      "target": "TF022",
      "masking": false
    },
    {
      "source": "root.rec.RetrievalRefNbr",
      "target": "TF023",
      "masking": false
    },
    {
      "source": "root.rec.TerminalID",
      "target": "TF024",
      "masking": false
    },
    {
      "source": "root.rec.MerchantID",
      "target": "TF025",
      "masking": false
    },
    {
      "source": "root.rec.MerchantName",
      "target": "TF026",
      "masking": false
    },
    {
      "source": "root.rec.TrxnCatCode",
      "target": "TF027",
      "masking": false
    },
    {
      "source": "root.rec.OBService",
      "target": "TF028",
      "masking": false
    },
    {
      "source": "root.rec.OBSResult1",
      "target": "TF029",
      "masking": false
    },
    {
      "source": "root.rec.ScreeningScore",
      "target": "TF030",
      "masking": false
    },
    {
      "source": "root.rec.WLMResultsCode",
      "target": "TF031",
      "masking": false
    },
    {
      "source": "root.rec.TrxnCurrCode",
      "target": "TF032",
      "masking": false
    },
    {
      "source": "root.rec.BillingCurrCode",
      "target": "TF033",
      "masking": false
    },
    {
      "source": "root.rec.SchemeTrxnID",
      "target": "TF034",
      "masking": false
    },
    {
      "source": "root.rec.TrxnType",
      "target": "TF035",
      "masking": false
    },
    {
      "source": "root.rec.UniqueTrxnRef",
      "target": "TF036",
      "masking": false
    },
    {
      "source": "root.rec.SenderAcctNbr",
      "target": "TF037",
      "masking": true
    },
    {
      "source": "root.rec.SenderName",
      "target": "TF038",
      "masking": true
    },
    {
      "source": "root.rec.SenderAddr",
      "target": "TF039",
      "masking": true
    },
    {
      "source": "root.rec.SenderCity",
      "target": "TF040",
      "masking": false
    },
    {
      "source": "root.rec.SenderState",
      "target": "TF041",
      "masking": false
    },
    {
      "source": "root.rec.SenderCountry",
      "target": "TF042",
      "masking": false
    },
    {
      "source": "root.rec.FundingSource",
      "target": "TF043",
      "masking": false
    },
    {
      "source": "root.rec.SenderPostalCode",
      "target": "TF044",
      "masking": true
    },
    {
      "source": "root.rec.SenderPhoneNbr",
      "target": "TF045",
      "masking": true
    },
    {
      "source": "root.rec.SenderDOB",
      "target": "TF046",
      "masking": true
    },
    {
      "source": "root.rec.SenderIDType",
      "target": "TF047",
      "masking": true
    },
    {
      "source": "root.rec.SenderID",
      "target": "TF048",
      "masking": true
    },
    {
      "source": "root.rec.SenderIDCountryCode",
      "target": "TF049",
      "masking": false
    },
    {
      "source": "root.rec.SenderIDExpDate",
      "target": "TF050",
      "masking": true
    },
    {
      "source": "root.rec.SenderNationality",
      "target": "TF051",
      "masking": false
    },
    {
      "source": "root.rec.SenderCountryOfBirth",
      "target": "TF052",
      "masking": false
    },
    {
      "source": "root.rec.RecipientName",
      "target": "TF053",
      "masking": true
    },
    {
      "source": "root.rec.RecipientAddr",
      "target": "TF054",
      "masking": true
    },
    {
      "source": "root.rec.RecipientCity",
      "target": "TF055",
      "masking": false
    },
    {
      "source": "root.rec.RecipientState",
      "target": "TF056",
      "masking": false
    },
    {
      "source": "root.rec.RecipientCountry",
      "target": "TF057",
      "masking": false
    },
    {
      "source": "root.rec.RecipientPostalCode",
      "target": "TF058",
      "masking": true
    },
    {
      "source": "root.rec.RecipientPhoneNbr",
      "target": "TF059",
      "masking": true
    },
    {
      "source": "root.rec.RecipientDOB",
      "target": "TF060",
      "masking": true
    },
    {
      "source": "root.rec.RecipientAcctNbr",
      "target": "TF061",
      "masking": true
    },
    {
      "source": "root.rec.RecipientIDType",
      "target": "TF062",
      "masking": true
    },
    {
      "source": "root.rec.RecipientID",
      "target": "TF063",
      "masking": true
    },
    {
      "source": "root.rec.RecipientIDCountryCode",
      "target": "TF064",
      "masking": false
    },
    {
      "source": "root.rec.RecipientIDExpDate",
      "target": "TF065",
      "masking": true
    },
    {
      "source": "root.rec.RecipientNationality",
      "target": "TF066",
      "masking": false
    },
    {
      "source": "root.rec.RecipientCountryOfBirth",
      "target": "TF067",
      "masking": false
    },
    {
      "source": "root.rec.AdditionalMsg",
      "target": "TF068",
      "masking": true
    },
    {
      "source": "root.rec.ParticipationID",
      "target": "TF069",
      "masking": false
    },
    {
      "source": "root.rec.TrxnPurpose",
      "target": "TF070",
      "masking": false
    },
    {
      "source": "root.rec.LanguageID",
      "target": "TF071",
      "masking": false
    },
    {
      "source": "root.rec.LanguageData",
      "target": "TF072",
      "masking": true
    },
    {
      "source": "root.rec.AcceptorLegalBusinessName",
      "target": "TF073",
      "masking": false
    },
    {
      "source": "root.rec.PaymentFacilitatorName",
      "target": "TF074",
      "masking": false
    }
  ]
}
