package com.dopkit.component;

@FunctionalInterface
public interface StdInnerInputAdapter
        <TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TInnerInput> {
    TInnerInput stdMakeInnerInput(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived);
}
