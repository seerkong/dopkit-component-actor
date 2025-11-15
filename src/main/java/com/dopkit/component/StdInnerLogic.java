package com.dopkit.component;

public interface StdInnerLogic
        <TInnerRuntime, TInnerInput, TInnerConfig, TInnerOutput> {
    TInnerOutput stdInnerLogic(
            TInnerRuntime runtime, TInnerInput input, TInnerConfig config);
}
