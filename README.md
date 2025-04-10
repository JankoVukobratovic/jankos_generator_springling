# Springling Generator Plugin

A JetBrains plugin that automatically generates essential Spring components such as **Controller**, **Service**, **Repository**, **DTOs**, and **Mappers**. This tool simplifies backend development by automating the creation of boilerplate code, improving development speed and reducing repetitive tasks.

##WORK IN PROGRESS :)

## Features

- **Controller Generation**: Generates Spring Boot controllers for handling HTTP requests.
- **Service Generation**: Creates service classes to manage business logic.
- **Repository Generation**: Generates Spring Data repositories for database interaction.
- **DTO Generation**: Creates Data Transfer Object (DTO) classes with basic fields and Lombok annotations.
- **Mapper Generation**: Generates mappers for transforming entities into DTOs.

## Requirements

- IntelliJ IDEA (2021 or later)
- Java 11 or higher
- 
## Usage

1. Right-click on a class annotated with `@Entity` (inside the class context).
2. From the context menu, select the desired generation action:
    - **Generate REST**: Generates a Controller, Service, and a JPA Repository with basic functionality such as add, delete by id, find by id, find by name, find all.
    - **Generate DTO and Mapper**: Generates a DTO for the entity with fields and Lombok annotations, optionally create a mapper directly.
  
## Contributions

Feel free to fork the repository, create issues and submit PR's. Opinions are welcome. I'm making this project to get familiar with the IntelliJ API, and because i think it'll be useful!



