package com.dopkit.component;

public class StdRunComponentLogic {
    public static <TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TOuterOutput,
            TInnerRuntime, TInnerInput, TInnerConfig, TInnerOutput>
    TOuterOutput runByFuncStyleAdapter(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            StdOuterComputedAdapter<TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived>
                    outerDerivedAdapter,
            StdInnerRuntimeAdapter<TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TInnerRuntime>
                    innerRuntimeAdapter,
            StdInnerInputAdapter
                    <TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TInnerInput>
                    innerInputAdapter,
            StdInnerConfigAdapter<TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TInnerConfig>
                    innerConfigAdapter,
            StdInnerLogic
                    <TInnerRuntime, TInnerInput, TInnerConfig, TInnerOutput>
                    coreLogicAdapter,
            StdOuterOutputAdapter
                    <TOuterRuntime, TOuterInput, TOuterDerived, TOuterConfig, TInnerOutput, TOuterOutput>
                    outputAdapter
    ) {
        // 基于输入衍生出额外输入
        TOuterDerived outerDerived = outerDerivedAdapter
                .stdMakeOuterComputed(outerRuntime, outerInput, outerConfig);
        // 内部实现的上下文
        TInnerRuntime innerRuntime = innerRuntimeAdapter
                .stdMakeInnerRuntime(outerRuntime, outerInput, outerConfig, outerDerived);
        // 将外部封装入参转换为内部实现入参
        TInnerInput innerInput = innerInputAdapter
                .stdMakeInnerInput(outerRuntime, outerInput, outerConfig, outerDerived);
        // 内部实现的配置
        TInnerConfig innerConfig = innerConfigAdapter
                .stdMakeInnerConfig(outerRuntime, outerInput, outerConfig, outerDerived);
        // 调用内部逻辑，返回内部实现结果
        TInnerOutput innerOutput = coreLogicAdapter
                .stdInnerLogic(innerRuntime, innerInput, innerConfig);
        // 将内部结果转换为外部结果
        TOuterOutput outerOutput = outputAdapter
                .stdMakeOuterOutput(outerRuntime, outerInput, outerConfig, outerDerived, innerOutput);
        return outerOutput;
    }

    public static
            <TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived>
        TOuterDerived stdMakeNullOuterComputed(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig) {
        return null;
    }

    public static <TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TInnerRuntime>
    TInnerRuntime stdMakeIdentityInnerRuntime(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived) {
        return (TInnerRuntime) outerRuntime;
    }

    public static <TOuterRuntime, TOuterInput, TOuterConfig, TOuterDerived, TInnerConfig>
    TInnerConfig stdMakeIdentityInnerConfig(
            TOuterRuntime outerRuntime,
            TOuterInput outerInput,
            TOuterConfig outerConfig,
            TOuterDerived outerDerived) {
        return (TInnerConfig) outerConfig;
    }
}
