package com.rkp.topcore.plugin.api.upstream;
/**
  type       name
-------------------------
TRANSACTION TRANSACTION
CONTROL     ECHO
CONTROL     SIGNON
CONTROL     KEY_EXCHANGE
CONTROL     SIGNOFF
UNKNOWN     UNKNOWN
 */
public final class UpstreamRoute {

    private final UpstreamRouteType type;
    private final String name;

    private UpstreamRoute(
            UpstreamRouteType type,
            String name) {

        if (type == null) {
            throw new IllegalArgumentException(
                    "UpstreamRouteType cannot be null");
        }

        this.type = type;
        this.name = name;
    }

    public static UpstreamRoute transaction() {
        return new UpstreamRoute(
                UpstreamRouteType.TRANSACTION,
                "TRANSACTION");
    }

    public static UpstreamRoute control(String name) {
        return new UpstreamRoute(
                UpstreamRouteType.CONTROL,
                name);
    }

    public static UpstreamRoute unknown() {
        return new UpstreamRoute(
                UpstreamRouteType.UNKNOWN,
                "UNKNOWN");
    }

    public UpstreamRouteType type() {
        return type;
    }

    public String name() {
        return name;
    }

    public boolean isTransaction() {
        return UpstreamRouteType.TRANSACTION == type;
    }

    public boolean isControl() {
        return UpstreamRouteType.CONTROL == type;
    }

    public boolean isUnknown() {
        return UpstreamRouteType.UNKNOWN == type;
    }
}