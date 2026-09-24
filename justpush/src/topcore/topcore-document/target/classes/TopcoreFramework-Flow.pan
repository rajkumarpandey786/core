                   TOPCORE FRAMEWORK
                   =================

 C400 XML
    │
    ▼
┌───────────────────┐
│  UpstreamPlugin   │
│      C400         │
└────────┬──────────┘
         │
         ▼
 Canonical Request
         │
         ▼
┌───────────────────────────┐
│ ApplicationRequestProcessor│
└────────────┬──────────────┘
             │
             ▼
     Canonical Request
             │
             ▼
┌─────────────────────────────┐
│      DownstreamManager      │
└─────────────┬───────────────┘
              │
        ┌─────┴─────┐
        ▼           ▼
   TSAS copy    PAIMI copy
        │           │
        ▼           ▼
 TsasProcessor PaimiProcessor
        │           │
        ▼           ▼
   TsasClient   PaimiClient
        │           │
        ▼           ▼
 TSAS Response  PAIMI Response
        │           │
        └─────┬─────┘
              ▼
       Common Response
              │
              ▼
┌────────────────────────────┐
│ ApplicationResponseProcessor│
└─────────────┬──────────────┘
              │
              ▼
       Final Canonical
          Response
              │
              ▼
┌───────────────────┐
│  UpstreamPlugin   │
│      C400         │
└────────┬──────────┘
         │
         ▼
      C400 XML