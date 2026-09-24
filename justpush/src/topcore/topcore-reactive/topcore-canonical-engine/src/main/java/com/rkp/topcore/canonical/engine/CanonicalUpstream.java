package com.rkp.topcore.canonical.engine;

import com.rkp.topcore.canonical.document.CanonicalDocument;

public interface CanonicalUpstream<T> {

    CanonicalDocument toCanonical(T request);
}