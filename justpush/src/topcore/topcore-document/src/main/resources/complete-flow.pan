                  screening-app
                       │
                       │ configuration
                       │ business processors
                       │ mappings
                       ▼
                 ┌─────────────┐
                 │   TopCore   │
                 └──────┬──────┘
                        │
                        ▼
                 C400 TCP Server
                        │
                        ▼
                  XML / C400
                        │
                        ▼
               C400 UpstreamPlugin
                        │
                        ▼
                CanonicalDocument
                        │
                        ▼
             TransactionOrchestrator
                        │
              ┌─────────┴─────────┐
              │                   │
              ▼                   ▼
            TSAS                 PAIMI
              │                   │
        processor + client  processor + client
              │                   │
              ▼                   ▼
          Canonical            Canonical
          response             response
              │                   │
              └─────────┬─────────┘
                        ▼
                Response aggregation
                        │
                        ▼
               Application response
                        │
                        ▼
                 C400 Plugin
                        │
                        ▼
                    XML
                        │
                        ▼
                  TCP Client