package com.rkp.topcore.canonical.engine;

import com.rkp.topcore.canonical.document.CanonicalDocument;

public interface CanonicalDownstream<T> {

    T fromCanonical( CanonicalDocument document );
}