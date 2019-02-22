package com.lkk.everything.core.interceptor;

import com.lkk.everything.core.model.Thing;

@FunctionalInterface
public interface ThingInterceptor {
    void apply(Thing thing);
}
