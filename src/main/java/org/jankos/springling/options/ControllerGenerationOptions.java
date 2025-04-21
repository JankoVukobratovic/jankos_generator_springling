package org.jankos.springling.options;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ControllerGenerationOptions {
    boolean hideIdOnGetEndpoints;
    boolean usePagination;

}
