package org.jankos.springling.models.repository;

import lombok.Builder;
import lombok.Value;
import org.jankos.springling.models.EntityModel;

import java.util.List;

@Value
@Builder
public class JPARepositoryModel {
    EntityModel relevantEntity;
    List<JPARepositoryMethodModel> methods;
}
