package com.rkp.topcore.plugin.api.upstream;

public interface UpstreamRouter {

    UpstreamRoute route(String request);
}