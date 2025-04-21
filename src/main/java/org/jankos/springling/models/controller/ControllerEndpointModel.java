package org.jankos.springling.models.controller;

import com.intellij.util.net.HTTPMethod;
import lombok.Builder;
import lombok.Value;
import org.jankos.springling.options.EndPointOptions.EndPointOptions;

@Value
@Builder
public class ControllerEndpointModel {
    HTTPMethod httpMethod;
    String relativeApiPath;
    EndPointOptions options;
}
