package com.dopkit.component;

@FunctionalInterface
public interface StdOuterComputedAdapter<TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived> {
    TOuterDerived stdMakeOuterComputed(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig);
}
