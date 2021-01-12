package ${packageName};

import io.vlingo.lattice.exchange.ExchangeMapper;
import io.vlingo.common.serialization.JsonSerialization;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${exchangeMapperName} implements ExchangeMapper<${localTypeName},String> {

  @Override
  public String localToExternal(final ${localTypeName} local) {
    return JsonSerialization.serialized(local);
  }

  @Override
  public ${localTypeName} externalToLocal(final String external) {
    return JsonSerialization.deserialized(external, ${localTypeName}.class);
  }
}