// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.parameter;

import java.util.*;
import java.util.Map.Entry;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CodeGenerationParameters {

    private final List<CodeGenerationParameter> parameters = new ArrayList<>();

    public static CodeGenerationParameters from(final Label label, final Object value) {
        return from(label, value.toString());
    }

    public static CodeGenerationParameters from(final Label label, final String value) {
        return from(CodeGenerationParameter.of(label, value));
    }

    public static CodeGenerationParameters from(final CodeGenerationParameter ...codeGenerationParameters) {
        return new CodeGenerationParameters(Arrays.asList(codeGenerationParameters));
    }

    public static CodeGenerationParameters empty() {
        return new CodeGenerationParameters(new ArrayList<>());
    }

    private CodeGenerationParameters(final List<CodeGenerationParameter> parameters) {
        this.parameters.addAll(parameters);
    }

    public CodeGenerationParameters add(final Label label, final Object value) {
        return add(label, value.toString());
    }

    public CodeGenerationParameters add(final Label label, final String value) {
        return add(CodeGenerationParameter.of(label, value));
    }

    public CodeGenerationParameters add(final CodeGenerationParameter parameter) {
        this.parameters.add(parameter);
        return this;
    }

    public void addAll(final Map<Label, String> parameterEntries) {
        final Function<Entry<Label, String>, CodeGenerationParameter> mapper =
                entry -> CodeGenerationParameter.of(entry.getKey(), entry.getValue());

        addAll(parameterEntries.entrySet().stream().map(mapper).collect(Collectors.toList()));
    }

    public void addAll(final CodeGenerationParameters parameters) {
        addAll(parameters.list());
    }

    public CodeGenerationParameters addAll(final List<CodeGenerationParameter> parameters) {
        this.parameters.addAll(parameters);
        return this;
    }

    public String retrieveValue(final Label label) {
        return retrieveOne(label).value;
    }

    public <T> T retrieveValue(final Label label, final Function<String, T> mapper) {
        return mapper.apply(retrieveValue(label));
    }

    public CodeGenerationParameter retrieveOne(final Label label) {
        return parameters.stream()
                .filter(param -> param.isLabeled(label)).findFirst()
                .orElse(CodeGenerationParameter.of(label, ""));
    }

    protected List<CodeGenerationParameter> list() {
        return Collections.unmodifiableList(parameters);
    }

    public Stream<CodeGenerationParameter> retrieveAll(final Label label) {
        return parameters.stream().filter(param -> param.isLabeled(label));
    }

    public boolean isEmpty() {
        return parameters.isEmpty();
    }

}
