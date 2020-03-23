// Copyright © 2012-2020 VLINGO LABS. All rights reserved.
//
// This Source Code Form is subject to the terms of the
// Mozilla Public License, v. 2.0. If a copy of the MPL
// was not distributed with this file, You can obtain
// one at https://mozilla.org/MPL/2.0/.

package io.vlingo.xoom.server;

import java.net.URI;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import io.micronaut.context.annotation.Parameter;
import io.micronaut.context.annotation.Prototype;
import io.micronaut.core.convert.value.ConvertibleValues;
import io.micronaut.core.util.CollectionUtils;
import io.micronaut.discovery.metadata.ServiceInstanceMetadataContributor;
import io.micronaut.health.HealthStatus;
import io.micronaut.runtime.server.EmbeddedServer;
import io.micronaut.runtime.server.EmbeddedServerInstance;
import io.vlingo.xoom.VlingoServer;

/**
 * Implements the {@link EmbeddedServerInstance} interface for Vlingo.
 */
@Prototype
public class VlingoServiceInstance implements EmbeddedServerInstance {

    private final VlingoServer vlingoServer;
    private final ConvertibleValues<String> metadata;
    private HealthStatus healthStatus = HealthStatus.UNKNOWN;

    /**
     * @param vlingoServer         The {@link VlingoServer}
     * @param metadataContributors The {@link ServiceInstanceMetadataContributor}
     */
    VlingoServiceInstance(@Parameter VlingoServer vlingoServer,
                          List<ServiceInstanceMetadataContributor> metadataContributors) {
        this.vlingoServer = vlingoServer;
        Map<String, String> metaMap = new LinkedHashMap<>(5);

        metaMap.put("host", getHost());
        metaMap.put("port", String.valueOf(getPort()));

        if (CollectionUtils.isNotEmpty(metadataContributors)) {
            for (ServiceInstanceMetadataContributor contributor : metadataContributors) {
                contributor.contribute(this, metaMap);
            }
        }

        this.metadata = ConvertibleValues.of(metaMap);
    }

    @Override
    public EmbeddedServer getEmbeddedServer() {
        return vlingoServer;
    }

    @Override
    public String getId() {
        return vlingoServer.getApplicationConfiguration().getName().orElse("unknown");
    }

    @Override
    public URI getURI() {
        return vlingoServer.getURI();
    }

    @Override
    public ConvertibleValues<String> getMetadata() {
        return this.metadata;
    }

    @Override
    public Optional<String> getZone() {
        return vlingoServer.getApplicationConfiguration().getInstance().getZone();
    }

    @Override
    public Optional<String> getInstanceId() {
        return Optional.of(vlingoServer.getHost() + ":" + vlingoServer.getPort());
    }

    @Override
    public HealthStatus getHealthStatus() {
        return this.healthStatus;
    }

    public void setHealthStatus(HealthStatus healthStatus) {
        this.healthStatus = healthStatus;
    }
}
