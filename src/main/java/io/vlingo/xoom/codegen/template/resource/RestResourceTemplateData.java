// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.resource;

import static io.vlingo.xoom.codegen.content.ContentQuery.findFullyQualifiedClassName;
import static io.vlingo.xoom.codegen.template.TemplateParameter.MODEL;
import static io.vlingo.xoom.codegen.template.TemplateParameter.MODEL_ACTOR;
import static io.vlingo.xoom.codegen.template.TemplateParameter.MODEL_PROTOCOL;
import static io.vlingo.xoom.codegen.template.TemplateParameter.PACKAGE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.QUERIES;
import static io.vlingo.xoom.codegen.template.TemplateParameter.REST_RESOURCE_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.ROUTE_DECLARATIONS;
import static io.vlingo.xoom.codegen.template.TemplateParameter.ROUTE_METHODS;
import static io.vlingo.xoom.codegen.template.TemplateParameter.STORAGE_TYPE;
import static io.vlingo.xoom.codegen.template.TemplateParameter.STORE_PROVIDER_NAME;
import static io.vlingo.xoom.codegen.template.TemplateParameter.URI_ROOT;
import static io.vlingo.xoom.codegen.template.TemplateParameter.USE_AUTO_DISPATCH;
import static io.vlingo.xoom.codegen.template.TemplateParameter.USE_CQRS;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.AGGREGATE_PROTOCOL;
import static io.vlingo.xoom.codegen.template.TemplateStandard.DATA_OBJECT;
import static io.vlingo.xoom.codegen.template.TemplateStandard.REST_RESOURCE;
import static io.vlingo.xoom.codegen.template.TemplateStandard.STORE_PROVIDER;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import io.vlingo.xoom.codegen.content.Content;
import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;
import io.vlingo.xoom.codegen.parameter.Label;
import io.vlingo.xoom.codegen.template.TemplateData;
import io.vlingo.xoom.codegen.template.TemplateParameters;
import io.vlingo.xoom.codegen.template.TemplateStandard;
import io.vlingo.xoom.codegen.template.storage.Model;
import io.vlingo.xoom.codegen.template.storage.QueriesParameter;
import io.vlingo.xoom.codegen.template.storage.StorageType;

public class RestResourceTemplateData extends TemplateData {

    private final String packageName;
    private final String aggregateName;
    private final TemplateParameters parameters;

    @SuppressWarnings("unchecked")
    public RestResourceTemplateData(final String basePackage,
                                    final CodeGenerationParameter aggregateParameter,
                                    final List<Content> contents,
                                    final Boolean useCQRS) {
        this.aggregateName = aggregateParameter.value;
        this.packageName = resolvePackage(basePackage);
        this.parameters = loadParameters(aggregateParameter, contents, useCQRS);
        this.dependOn(RouteMethodTemplateData.from(aggregateParameter, parameters));
    }

    private TemplateParameters loadParameters(final CodeGenerationParameter aggregateParameter,
                                              final List<Content> contents,
                                              final Boolean useCQRS) {
        final String uriRoot =
                aggregateParameter.retrieveRelatedValue(Label.URI_ROOT);

        final QueriesParameter queriesParameter =
                QueriesParameter.from(aggregateParameter, contents, useCQRS);

        final Function<TemplateParameters, Object> modelProtocolResolver =
                params -> requireModelTypes(aggregateParameter) ? aggregateName : "";

        if(useCQRS) {
            aggregateParameter.relate(RouteDetail.defaultQueryRouteParameter(aggregateParameter));
        }

        return TemplateParameters.with(REST_RESOURCE_NAME, REST_RESOURCE.resolveClassname(aggregateName))
                .and(QUERIES, queriesParameter).and(PACKAGE_NAME, packageName).and(USE_CQRS, useCQRS)
                .and(ROUTE_DECLARATIONS, RouteDeclarationParameter.from(aggregateParameter))
                .addImports(resolveImports(aggregateParameter, contents, useCQRS))
                .and(MODEL_ACTOR, AGGREGATE.resolveClassname(aggregateName))
                .and(STORE_PROVIDER_NAME, resolveQueryStoreProviderName())
                .and(URI_ROOT, PathFormatter.formatRootPath(uriRoot))
                .andResolve(MODEL_PROTOCOL, modelProtocolResolver)
                .and(ROUTE_METHODS, new ArrayList<String>())
                .and(USE_AUTO_DISPATCH, false);
    }

    private Set<String> resolveImports(final CodeGenerationParameter aggregateParameter,
                                       final List<Content> contents,
                                       final Boolean useCQRS) {
        final Set<String> imports = new HashSet<>();
        if(RouteDetail.requireEntityLoad(aggregateParameter)) {
            final String aggregateEntityName = AGGREGATE.resolveClassname(aggregateName);
            imports.add(findFullyQualifiedClassName(AGGREGATE, aggregateEntityName, contents));
            imports.add(findFullyQualifiedClassName(AGGREGATE_PROTOCOL, aggregateName, contents));
            imports.add(findFullyQualifiedClassName(DATA_OBJECT, DATA_OBJECT.resolveClassname(aggregateName), contents));
        }
        if(RouteDetail.requireModelFactory(aggregateParameter)) {
            imports.add(findFullyQualifiedClassName(AGGREGATE_PROTOCOL, aggregateName, contents));
            imports.add(findFullyQualifiedClassName(DATA_OBJECT, DATA_OBJECT.resolveClassname(aggregateName), contents));
        }
        if(useCQRS) {
            final String queriesName = TemplateStandard.QUERIES.resolveClassname(aggregateName);
            imports.add(findFullyQualifiedClassName(STORE_PROVIDER, resolveQueryStoreProviderName(), contents));
            imports.add(findFullyQualifiedClassName(TemplateStandard.QUERIES, queriesName, contents));
        }
        return imports;
    }

    private String resolveQueryStoreProviderName() {
        final TemplateParameters queryStoreProviderParameters =
                TemplateParameters.with(STORAGE_TYPE, StorageType.STATE_STORE)
                        .and(MODEL, Model.QUERY);

        return STORE_PROVIDER.resolveClassname(queryStoreProviderParameters);
    }

    private String resolvePackage(final String basePackage) {
        return String.format("%s.%s.%s", basePackage, "infrastructure", "resource");
    }

    private boolean requireModelTypes(final CodeGenerationParameter aggregateParameter) {
        return RouteDetail.requireEntityLoad(aggregateParameter);
    }

    @Override
    public void handleDependencyOutcome(final TemplateStandard standard, final String outcome) {
        this.parameters.<List<String>>find(ROUTE_METHODS).add(outcome);
    }

    @Override
    public TemplateStandard standard() {
        return REST_RESOURCE;
    }

    @Override
    public TemplateParameters parameters() {
        return parameters;
    }

    @Override
    public String filename() {
        return standard().resolveFilename(aggregateName, parameters);
    }

}