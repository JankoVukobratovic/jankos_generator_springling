package org.jankos.springling.models.service;

import lombok.Builder;
import lombok.Value;
import org.jankos.springling.models.EntityModel;

@Value
@Builder
public class ServiceModel {
  EntityModel relevantEntity;
  String className;
  String packageName;
  String qualifiedName;
}
