// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.
package io.vlingo.xoom.annotation.autodispatch;

import io.vlingo.actors.Actor;
import io.vlingo.http.Method;
import io.vlingo.xoom.annotation.initializer.resources.DummyHandlers;

@Queries(actor = Actor.class, protocol = ActorProtocol.class)
@AutoDispatch(path = "/dummies", handlers = DummyHandlers.class)
public interface DummyQueries {

    @Route(method = Method.GET, path = "any-path", handler = 0)
    void dummyRouteForQueries(String parameter);

}
