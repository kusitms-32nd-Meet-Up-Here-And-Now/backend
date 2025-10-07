package com.meetup.hereandnow.core.config.swagger;

import io.swagger.v3.oas.models.examples.Example;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ExampleHolder {

    private Example holder;

    private int code;

    private String name;
}
