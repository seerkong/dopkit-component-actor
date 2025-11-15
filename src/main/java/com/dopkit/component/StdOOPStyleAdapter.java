package com.dopkit.component;

public interface StdOOPStyleAdapter<TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TOuterOutput,
        TInnerRuntime, TInnerInput, TInnerConfig, TInnerOutput> {
    TOuterDerived stdMakeOuterComputed(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig);

    TInnerRuntime stdMakeInnerRuntime(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived);

    TInnerInput stdMakeInnerInput(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived);

    TInnerConfig stdMakeInnerConfig(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived);

    TInnerOutput stdCoreLogic(
            TInnerRuntime runtime, TInnerInput input, TInnerConfig config);

    TOuterOutput stdMakeOuterOutput(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived,
            TInnerOutput innerOutput
    );
}
