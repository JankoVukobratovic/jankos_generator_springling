package org.jankos.springling.models;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class DTOModel {
  String className;
  String packageName;

  List<FieldModel> fields;
}
