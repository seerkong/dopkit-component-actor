package com.dopkit.component;

@FunctionalInterface
public interface StdInnerRuntimeAdapter
        <TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TInnerRuntime> {
    TInnerRuntime stdMakeInnerRuntime(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived);
}
