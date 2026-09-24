{
  "settings": {
    "isValidationEnabled": true
  },

  "canonicalFields": {

    "TF001": {
      "name": "country",
      "type": "STRING",
      "isFieldValidationEnabled": true,
      "isMaskingRequired": false,
      "required": true,
      "copyToResponse": true,
      "maxLength": 3
    },

    "TF002": {
      "name": "requestId",
      "type": "STRING",
      "isFieldValidationEnabled": true,
      "isMaskingRequired": false,
      "required": true,
      "copyToResponse": true,
      "maxLength": 50
    },

    "TF003": {
      "name": "functionName",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": true,
      "copyToResponse": true,
      "maxLength": 50
    },

    "TF004": {
      "name": "externalTransactionId",
      "type": "STRING",
      "isFieldValidationEnabled": true,
      "isMaskingRequired": false,
      "required": true,
      "copyToResponse": true,
      "maxLength": 100
    },

    "TF005": {
      "name": "channel",
      "type": "STRING",
      "isFieldValidationEnabled": true,
      "isMaskingRequired": false,
      "required": true,
      "copyToResponse": true,
      "maxLength": 20
    },

    "TF006": {
      "name": "transactionType",
      "type": "STRING",
      "isFieldValidationEnabled": true,
      "isMaskingRequired": false,
      "required": true,
      "maxLength": 20
    },

    "TF007": {
      "name": "status",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF008": {
      "name": "mti",
      "type": "STRING",
      "isFieldValidationEnabled": true,
      "isMaskingRequired": false,
      "required": true,
      "maxLength": 10
    },

    "TF009": {
      "name": "cardNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 50
    },

    "TF010": {
      "name": "processingCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 20
    },

    "TF011": {
      "name": "transactionAmount",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 30
    },

    "TF012": {
      "name": "billingAmount",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 30
    },

    "TF013": {
      "name": "transmissionDateTime",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 30
    },

    "TF014": {
      "name": "systemTraceNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 30
    },

    "TF015": {
      "name": "localTransactionTime",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 20
    },

    "TF016": {
      "name": "localTransactionDate",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 20
    },

    "TF017": {
      "name": "merchantCategoryCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 10
    },

    "TF018": {
      "name": "acquiringCountryCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 10
    },

    "TF019": {
      "name": "posEntryMode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 20
    },

    "TF020": {
      "name": "cardSequenceNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 20
    },

    "TF021": {
      "name": "posConditionCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 20
    },

    "TF022": {
      "name": "acquiringInstitutionId",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF023": {
      "name": "retrievalReferenceNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF024": {
      "name": "terminalId",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF025": {
      "name": "merchantId",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF026": {
      "name": "merchantName",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 200
    },

    "TF027": {
      "name": "transactionCategoryCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF028": {
      "name": "obService",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF029": {
      "name": "obServiceResult",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF030": {
      "name": "screeningScore",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF031": {
      "name": "wlmResultsCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF032": {
      "name": "transactionCurrencyCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 10
    },

    "TF033": {
      "name": "billingCurrencyCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 10
    },

    "TF034": {
      "name": "schemeTransactionId",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 100
    },

    "TF035": {
      "name": "transactionSubType",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF036": {
      "name": "uniqueTransactionReference",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 100
    },

    "TF037": {
      "name": "senderAccountNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 100
    },

    "TF038": {
      "name": "senderName",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 200
    },

    "TF039": {
      "name": "senderAddress",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 500
    },

    "TF040": {
      "name": "senderCity",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 100
    },

    "TF041": {
      "name": "senderState",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 100
    },

    "TF042": {
      "name": "senderCountry",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 10
    },

    "TF043": {
      "name": "fundingSource",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF044": {
      "name": "senderPostalCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 30
    },

    "TF045": {
      "name": "senderPhoneNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 50
    },

    "TF046": {
      "name": "senderDateOfBirth",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 30
    },

    "TF047": {
      "name": "senderIdType",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 50
    },

    "TF048": {
      "name": "senderIdNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 100
    },

    "TF049": {
      "name": "senderIdCountryCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 20
    },

    "TF050": {
      "name": "senderIdExpiryDate",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 30
    },

    "TF051": {
      "name": "senderNationality",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF052": {
      "name": "senderCountryOfBirth",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF053": {
      "name": "recipientName",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 200
    },

    "TF054": {
      "name": "recipientAddress",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 500
    },

    "TF055": {
      "name": "recipientCity",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 100
    },

    "TF056": {
      "name": "recipientState",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 100
    },

    "TF057": {
      "name": "recipientCountry",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 20
    },

    "TF058": {
      "name": "recipientPostalCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 30
    },

    "TF059": {
      "name": "recipientPhoneNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 50
    },

    "TF060": {
      "name": "recipientDateOfBirth",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 30
    },

    "TF061": {
      "name": "recipientAccountNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 100
    },

    "TF062": {
      "name": "recipientIdType",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 50
    },

    "TF063": {
      "name": "recipientIdNumber",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 100
    },

    "TF064": {
      "name": "recipientIdCountryCode",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 20
    },

    "TF065": {
      "name": "recipientIdExpiryDate",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 30
    },

    "TF066": {
      "name": "recipientNationality",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF067": {
      "name": "recipientCountryOfBirth",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 50
    },

    "TF068": {
      "name": "additionalMessage",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 1000
    },

    "TF069": {
      "name": "participationId",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 100
    },

    "TF070": {
      "name": "transactionPurpose",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 500
    },

    "TF071": {
      "name": "languageId",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 20
    },

    "TF072": {
      "name": "languageData",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": true,
      "required": false,
      "maxLength": 1000
    },

    "TF073": {
      "name": "acceptorLegalBusinessName",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 200
    },

    "TF074": {
      "name": "paymentFacilitatorName",
      "type": "STRING",
      "isFieldValidationEnabled": false,
      "isMaskingRequired": false,
      "required": false,
      "maxLength": 200
    },

    "TF075": {
	  "name": "integrationUnit",
	  "type": "STRING",
	  "isFieldValidationEnabled": false,
	  "isMaskingRequired": false,
	  "required": false,
	  "maxLength": 50
	},
	
	"TF076": {
	  "name": "business",
	  "type": "STRING",
	  "isFieldValidationEnabled": false,
	  "isMaskingRequired": false,
	  "required": false,
	  "maxLength": 50
	},
	
	"TF077": {
	  "name": "application",
	  "type": "STRING",
	  "isFieldValidationEnabled": false,
	  "isMaskingRequired": false,
	  "required": false,
	  "maxLength": 50
	},
	
	"TF078": {
	  "name": "service",
	  "type": "STRING",
	  "isFieldValidationEnabled": false,
	  "isMaskingRequired": false,
	  "required": false,
	  "maxLength": 50
	},
	
	"TF079": {
	  "name": "userReference",
	  "type": "STRING",
	  "isFieldValidationEnabled": false,
	  "isMaskingRequired": false,
	  "required": false,
	  "maxLength": 100
	},
	
	"TF080": {
	  "name": "direction",
	  "type": "STRING",
	  "isFieldValidationEnabled": false,
	  "isMaskingRequired": false,
	  "required": false,
	  "maxLength": 10
	},
	
	"TF081": {
	  "name": "senderSystem",
	  "type": "STRING",
	  "isFieldValidationEnabled": false,
	  "isMaskingRequired": false,
	  "required": false,
	  "maxLength": 50
	},
	
	"TF082": {
	  "name": "receiverSystem",
	  "type": "STRING",
	  "isFieldValidationEnabled": false,
	  "isMaskingRequired": false,
	  "required": false,
	  "maxLength": 50
	}

  }
}