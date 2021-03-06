<#if factoryMethod>
public static final HandlerEntry<Three<Completes<${stateName}>, Stage, ${dataName}>> ${indexName}_HANDLER =
          HandlerEntry.of(${indexName}, ($stage, data) -> ${aggregateProtocolName}.${methodName}(${methodInvocationParameters}));
<#else>
public static final HandlerEntry<Three<Completes<${stateName}>, ${aggregateProtocolName}, ${dataName}>> ${indexName}_HANDLER =
          HandlerEntry.of(${indexName}, (${aggregateProtocolVariable}, data) -> ${aggregateProtocolVariable}.${methodName}(${methodInvocationParameters}));
</#if>