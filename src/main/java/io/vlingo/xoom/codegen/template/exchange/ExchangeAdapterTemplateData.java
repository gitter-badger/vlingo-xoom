// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.codegen.template.exchange;

import io.vlingo.lattice.model.IdentifiedDomainEvent;
import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.content.ContentQuery;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static io.vlingo.xoom.codegen.parameter.Label.ROLE;
import static io.vlingo.xoom.codegen.parameter.Label.SCHEMA_GROUP;
import static io.vlingo.xoom.codegen.template.TemplateParameter.*;
import static io.vlingo.xoom.codegen.template.TemplateStandard.*;

public class ExchangeAdapterTemplateData extends TemplateData {

    private final TemplateParameters parameters;

    public static List<TemplateData> from(final String exchangePackage,
                                          final Stream<CodeGenerationParameter> aggregates,
                                          final List<Content> contents) {
        return aggregates.flatMap(aggregate -> aggregate.retrieveAllRelated(Label.EXCHANGE))
                .map(exchange -> new ExchangeAdapterTemplateData(exchangePackage, exchange, contents))
                .collect(Collectors.toList());
    }

    private ExchangeAdapterTemplateData(final String exchangePackage,
                                        final CodeGenerationParameter exchange,
                                        final List<Content> contents) {
        this.parameters =
                TemplateParameters.with(PACKAGE_NAME, exchangePackage)
                        .and(AGGREGATE_PROTOCOL_NAME, exchange.parent().value)
                        .and(SCHEMA_GROUP_NAME, exchange.retrieveRelatedValue(SCHEMA_GROUP))
                        .and(EXCHANGE_ROLE, exchange.retrieveRelatedValue(ROLE, ExchangeRole::of))
                        .and(LOCAL_TYPE_NAME, DATA_OBJECT.resolveClassname(exchange.parent().value))
                        .andResolve(EXCHANGE_ADAPTER_NAME, params -> EXCHANGE_ADAPTER.resolveClassname(params))
                        .andResolve(EXCHANGE_MAPPER_NAME, params -> EXCHANGE_MAPPER.resolveClassname(params))
                        .addImport(resolveImports(exchange, contents));
    }

    private String resolveImports(final CodeGenerationParameter exchange,
                                  final List<Content> contents) {
        if(exchange.retrieveRelatedValue(ROLE, ExchangeRole::of).isConsumer()) {
            final String dataObjectName = DATA_OBJECT.resolveClassname(exchange.parent().value);
            return ContentQuery.findFullyQualifiedClassName(DATA_OBJECT, dataObjectName, contents);
        }
        return IdentifiedDomainEvent.class.getCanonicalName();
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public TemplateStandard standard() {
        return EXCHANGE_ADAPTER;
    }

}
