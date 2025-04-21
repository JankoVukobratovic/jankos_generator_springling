package org.jankos.springling.options.EndPointOptions;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jankos.springling.models.FieldModel;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
public class PostOptions extends EndPointOptions {
    List<FieldModel> ignoredFields;
}
