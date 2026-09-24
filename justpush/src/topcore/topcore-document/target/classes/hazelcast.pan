The `namespace` is important.

Instead of TopCore having arbitrary maps everywhere:

```text
map1
map2
map3
...

we can logically separate data:

TOPCORE_TXN_CONTEXT
TOPCORE_SESSION
TOPCORE_ROUTING
TOPCORE_CACHE

For example:

store.put(
    "TOPCORE_TXN_CONTEXT",
    txnId,
    context
);