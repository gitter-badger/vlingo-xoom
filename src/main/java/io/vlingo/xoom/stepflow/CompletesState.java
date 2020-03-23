// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.stepflow;

import io.vlingo.common.Completes;

/**
 * A functional interface that transforms a {@link StateTransition} into a {@link Completes}.
 *
 * @param <T> is the source {@link State}
 * @param <R> is the target {@link State}
 */
@FunctionalInterface
@SuppressWarnings("rawtypes")
public interface CompletesState<T extends State, R extends State> {
    void apply(StateTransition<T, ?, ?> transition, R state);
}
