package org.jankos.springling.options.EndPointOptions;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.jankos.springling.models.FieldModel;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
@Builder
public class GetOptions extends EndPointOptions {
    boolean usePagination;
    boolean shouldSort;
    boolean returnList;
    boolean replaceRelationWithId;
    FieldModel searchArgument;
    List<FieldModel> hiddenFields;
}
