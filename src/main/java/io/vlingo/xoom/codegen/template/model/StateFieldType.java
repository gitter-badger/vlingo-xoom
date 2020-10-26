// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.codegen.template.model;

import io.vlingo.xoom.codegen.parameter.CodeGenerationParameter;

import java.util.Arrays;
import java.util.List;

import static io.vlingo.xoom.codegen.parameter.Label.FIELD_TYPE;
import static io.vlingo.xoom.codegen.parameter.Label.STATE_FIELD;

public class StateFieldType {

    private static String UNKNOWN_FIELD_MESSAGE = "%s is not a field in %s state";
    private static final List<String> NUMERIC_TYPES = Arrays.asList("byte", "short", "int", "integer", "long", "double", "float");

    public static String retrieve(final CodeGenerationParameter aggregate, final String fieldName) {
        return aggregate.retrieveAll(STATE_FIELD)
                .filter(stateField -> stateField.value.equals(fieldName))
                .map(stateField -> stateField.relatedParameterValueOf(FIELD_TYPE)).findFirst()
                .orElseThrow(() -> new IllegalArgumentException(UNKNOWN_FIELD_MESSAGE.format(fieldName, aggregate.value)));
    }

    public static String resolveDefaultValue(final CodeGenerationParameter aggregate, final String fieldName) {
        final String type = retrieve(aggregate, fieldName);
        if(type.equalsIgnoreCase(Boolean.class.getSimpleName())) {
            return "false";
        }
        if(NUMERIC_TYPES.contains(retrieve(aggregate, fieldName).toLowerCase())) {
            return "0";
        }
        return "null";
    }
}