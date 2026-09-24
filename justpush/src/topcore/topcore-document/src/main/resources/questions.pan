topcore-plugin-api
        ↓
needs TopCoreContext
        ↓
topcore-core-engine

topcore-core-engine
        ↓
already depends on
        ↓
topcore-plugin-api

That is a circular Maven dependency. avoid--

2. ClientSession session =
        getOrCreateSession(sessionId); ??