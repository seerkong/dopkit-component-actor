package com.dopkit.component;

@FunctionalInterface
public interface StdOuterOutputAdapter
        <TOuterRuntime, TOuterInput, TOuterDerived, TOuterConfig, TInnerOutput, TOuterOutput> {
    TOuterOutput stdMakeOuterOutput(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived,
            TInnerOutput innerOutput
    );
}
