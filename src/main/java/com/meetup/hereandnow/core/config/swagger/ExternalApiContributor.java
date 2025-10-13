package com.meetup.hereandnow.core.config.swagger;

import io.swagger.v3.oas.models.OpenAPI;

@FunctionalInterface
public interface ExternalApiContributor {

    void contribute(OpenAPI openAPI);
}
