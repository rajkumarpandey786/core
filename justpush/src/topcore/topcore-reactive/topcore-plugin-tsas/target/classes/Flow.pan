                    TOPCORE CORE
                         │
                         ▼
              Base CanonicalDocument
                         │
                         ▼
                document.copy()
                         │
                         ▼
                TSAS CanonicalDocument
                         │
                         ▼
                 TsasProcessor
                         │
                  adds TF075-081
                         │
                         ▼
                TSAS CanonicalDocument
                         │
                         ▼
                  TsasClient.call()
                         │
                         ▼
            CanonicalEngine.createOutbound()
                         │
                         ▼
                  TSAS JSON Map
                         │
                         ▼
             ObjectMapper → JSON String
                         │
                         ▼
              ┌──────────────────────┐
              │    WebClient         │
              │                      │
              │ POST                 │
              │ /api/process         │
              │ Content-Type: JSON   │
              │ Body: requestJson    │
              └──────────┬───────────┘
                         │
                         ▼
                    HTTP / TCP
                         │
                         ▼
                       TSAS
                         │
                         ▼
                  JSON Response
                         │
                         ▼
                  Map<String,Object>
                         │
                         ▼
            CanonicalEngine.createInbound()
                         │
                         ▼
              TSAS Response CanonicalDocument
                         │
                         ▼
                   TopCoreContext