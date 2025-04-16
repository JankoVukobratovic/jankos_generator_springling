package org.jankos.springling.config;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class RestApiGenerationConfig {
    boolean hideIdOnGetEndpoints;
    boolean generateCrudOperations;
    boolean usePagination;
}
