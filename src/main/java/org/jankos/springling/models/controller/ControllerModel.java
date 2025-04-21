package org.jankos.springling.models.controller;

import lombok.Builder;
import lombok.Value;
import org.jankos.springling.models.EntityModel;

import java.util.List;

@Value
@Builder
public class ControllerModel {
    EntityModel relevantEntity;
    String apiPath;
    List<ControllerEndpointModel> endpoints;
}
