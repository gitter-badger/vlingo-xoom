package ${packageName};

import io.vlingo.actors.Definition;
import io.vlingo.actors.Stage;
import io.vlingo.lattice.exchange.ExchangeReceiver;

<#list imports as import>
import ${import.qualifiedClassName};
</#list>

public class ${exchangeReceiverHolderName} {

<#list exchangeReceivers as receiver>
  /**
   * See <a href="https://docs.vlingo.io/vlingo-lattice/exchange#exchangereceiver">ExchangeReceiver</a>
   */
  static class ${receiver.schemaTypeName} implements ExchangeReceiver<${receiver.localTypeName}> {

    private final Stage stage;

    public ${receiver.schemaTypeName}(final Stage stage) {
      this.stage = stage;
    }

    @Override
    public void receive(final ${receiver.localTypeName} data) {
      <#if receiver.modelFactoryMethod>
      ${receiver.modelProtocol}.${receiver.modelMethod}(${receiver.modelMethodParameters});
      <#else>
      stage.actorOf(${receiver.modelProtocol}.class, stage.addressFactory().from(data.id), Definition.has(${receiver.modelActor}.class, Definition.parameters(data.id)))
              .andFinallyConsume(${receiver.modelVariable} -> ${receiver.modelVariable}.${receiver.modelMethod}(${receiver.modelMethodParameters}));
      </#if>
    }
  }

</#list>
}
