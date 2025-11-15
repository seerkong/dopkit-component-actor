package com.dopkit.component;

@FunctionalInterface
public interface StdInnerConfigAdapter <TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TInnerConfig> {
    TInnerConfig stdMakeInnerConfig(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived);
}
