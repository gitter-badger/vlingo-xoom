package ${packageName};

import java.util.ArrayList;
import java.util.List;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

import io.vlingo.lattice.model.DomainEvent;
import io.vlingo.lattice.model.IdentifiedDomainEvent;
import io.vlingo.lattice.model.projection.Projectable;
import io.vlingo.lattice.model.projection.StateStoreProjectionActor;
import io.vlingo.symbio.Entry;

public class ${projectionName} extends StateStoreProjectionActor<${dataName}> {
  private static final ${dataName} Empty = ${dataName}.empty();

  public ${projectionName}() {
    super(${storeProviderName}.instance().store);
  }

  @Override
  protected ${dataName} currentDataFor(final Projectable projectable) {
    return Empty;
  }

  @Override
  protected ${dataName} merge(
      ${dataName} previousData,
      final int previousVersion,
      final ${dataName} currentData,
      final int currentVersion) {

    if (previousData == null) {
      previousData = currentData;
    }

    for (final Source<?> event : sources()) {
      switch (${eventTypesName}.valueOf(event.typeName())) {
      <#list eventsNames as name>
        case ${name}:
          return ${dataName}.empty();   // TODO: implement actual merge
      </#list>
      default:
        logger().warn("Event of type " + event.typeName() + " was not matched.");
        break;
      }
    }

    return previousData;
  }
}
