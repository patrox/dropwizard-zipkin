/**
 * Copyright 2016 Smoke Turner, LLC.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.smoketurner.dropwizard.zipkin;

import javax.annotation.Nonnull;
import javax.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.github.kristofa.brave.Brave;
import com.github.kristofa.brave.SpanCollector;
import com.github.kristofa.brave.SpanCollectorMetricsHandler;
import com.github.kristofa.brave.scribe.ScribeSpanCollector;
import com.github.kristofa.brave.scribe.ScribeSpanCollectorParams;
import com.google.common.net.HostAndPort;
import com.smoketurner.dropwizard.zipkin.metrics.DropwizardSpanCollectorMetricsHandler;
import io.dropwizard.setup.Environment;

@JsonTypeName("scribe")
public class ScribeZipkinFactory extends AbstractZipkinFactory {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(ScribeZipkinFactory.class);
    private static final String DEFAULT_ZIPKIN_SCRIBE = "127.0.0.1:9140";

    @NotNull
    private HostAndPort endpoint = HostAndPort
            .fromString(DEFAULT_ZIPKIN_SCRIBE);

    @JsonProperty
    public HostAndPort getEndpoint() {
        return endpoint;
    }

    @JsonProperty
    public void setEndpoint(HostAndPort endpoint) {
        this.endpoint = endpoint;
    }

    /**
     * Build a new {@link Brave} instance for interfacing with Zipkin
     *
     * @param environment
     *            Environment
     * @return Brave instance
     */
    @Override
    public Brave build(@Nonnull final Environment environment) {
        final SpanCollectorMetricsHandler metricsHandler = new DropwizardSpanCollectorMetricsHandler(
                environment.metrics());
        final ScribeSpanCollectorParams params = new ScribeSpanCollectorParams();
        params.setMetricsHandler(metricsHandler);

        final SpanCollector spanCollector = new ScribeSpanCollector(
                endpoint.getHostText(), endpoint.getPort(), params);

        LOGGER.info("Sending spans to Scribe collector at <{}:{}>",
                endpoint.getHostText(), endpoint.getPort());

        return buildBrave(environment, spanCollector);
    }
}
