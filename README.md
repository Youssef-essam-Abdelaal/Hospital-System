# Hospital System Project Documentation

## Overview

The Hospital System is a project designed to manage patient registrations and track hospital records. It encompasses three sub-projects: Interface, Server, and Client. The system is built using gRPC for inter-process communication, Spring Boot for the backend, H2 as the in-memory database, JPA for ORM, and Hibernate for database interactions. These components are part of a parent project called System.

# Project Structure

## 1. Interface

The Interface project contains three proto files that define the gRPC services and message types for communication:

* Patients.proto: Defines messages and services related to patients, such as patient information, adding new patients, updating patient records, and retrieving patient data.
* Hospital.proto: Contains messages and services pertaining to hospitals, including hospital information, adding hospitals, and updating hospital details.
* Treatment.proto: Tracks patient treatments, detailing which patient visited which hospital and when. It facilitates recording hospital visits and treatments.

These proto files are compiled into gRPC stub classes used by the server and client for communication.

## 2. Server

The Server project is the backend, containing business logic and database interactions. It has three packages: Patient, Hospital, and Treatment. Each package includes the following five main files:

1. Entity Class: Represents the domain model with attributes, getters, and setters. This class is annotated with @Entity to map it to a database table. It also includes any necessary database constraints such as @Id, @GeneratedValue, or @Column.
2. Repository: This interface extends JpaRepository and provides CRUD operations and custom query methods to interact with the database. It serves as the primary layer for database communication.
3. Configuration: A configuration class that uses @Configuration or @Component annotations to run specific code when the server starts. It often includes CommandLineRunner to insert initial data into the database for testing or setup purposes.
4. gRPC Service: Implements gRPC services defined in the proto files. It receives requests, applies business logic, and interacts with the repository to retrieve or modify data. This class returns responses to the client via gRPC.
5. Business Logic (ObjectService): Contains the core business logic and helper methods used by the gRPC service. This class implements the main operations for creating, updating, and retrieving entities. It ensures that the gRPC service has clear, reusable methods to perform the required actions.

All of these components in the Server project are located under the main directory, following typical project conventions. The main server application class, GrpcServerApp, is responsible for running the server and is annotated with @SpringBootApplication.

### Server Tests

The Server project also contains a test package with three test files: one for each of the main packages (Patient, Hospital, and Treatment). These files contain unit tests designed to validate basic functionality and database interactions. The tests include basic CRUD operations and other core checks to ensure the integrity of the backend operations. However, these unit tests are not exhaustive, and the main intensive testing was done using BloomRPC, which allows for detailed testing of gRPC endpoints and scenarios.

## 3. Client

The Client project acts as a gRPC client, allowing interaction with the Server. It can be used for testing and demonstrating the functionality of the Server. The client sends gRPC requests to the server, simulating user interactions such as adding patients, retrieving patient data, and tracking treatments.

# Data Schema

### Patient

The Patient entity in the Server project has the following attributes with specific constraints:

* ID: A unique identifier and the primary key of the entity.
* Name: A String representing the patient's name. This field must not be null.
* Age: An int representing the patient's age. This field is used for simplicity, but in a real production scenario, age should be a @Transient field, derived from a more robust date of birth attribute.
* Gender: A String representing the patient's gender.
* Social Security Number (SSN): A String that must be unique and not null.
* Address: A String representing the patient's address.
* Phone Number: A String representing the patient's phone number.

These constraints ensure data integrity and avoid conflicts in the database.

### Hospital

The Hospital entity has the following attributes and constraints:

* ID: A unique identifier and the primary key of the entity.
* Name: A String representing the hospital's name. This field must not be null.
* Address: A String representing the hospital's address.
* Specialization: A String indicating the hospital's area of specialization.

### Treatment

The Treatment entity has five attributes with the following constraints:

* Treatment ID: A unique identifier and the primary key of the entity.
* Patient: A foreign key reference to the Patient entity.
* Hospital: A foreign key reference to the Hospital entity.
* Treatment Detail: A String describing what treatment the patient received.
* Date: A String representing the date of treatment. This is used for simplicity, but in real production, it should be a LocalDate for better date operations and constraints.

The Treatment entity is used to avoid a direct many-to-many relationship between Patient and Hospital, providing a more flexible and scalable way to track which patient visited which hospital and when. The Treatment entity also has a cascading effect: if either the Patient or Hospital is deleted, related Treatment records are also removed to maintain data integrity and avoid orphaned records.

## Implemented Methods

Each entity in the Server project has the following gRPC methods:

* Get Methods: Retrieve a single entry or a list of entries. For example, you can get a specific Patient by ID or get all Patients from the database.
* Add Methods: Add a new single entry or a list of entries to the database. For example, you can add a new Patient or a list of Patients.
* Update Methods: Update an existing single entry or a list of entries. For example, you can update a Patient's details or update multiple Patients.
* Delete Methods: Delete a single entry or a list of entries. For example, you can delete a Patient by ID or delete multiple Patients.

Additionally, the Treatment entity has two specific methods:

* Get All Patients for a Hospital: Retrieve a list of all Patients who have been treated at a specific Hospital.
* Get All Hospitals for a Patient: Retrieve a list of all Hospitals where a specific Patient has been treated.

These methods are designed to allow for flexible and efficient CRUD operations on the different entities, as well as additional functionality to manage and query treatments and relationships between Patients and Hospitals.

## Compatibility and Framework Details

* JDK: The project uses JDK 21, providing the latest Java features and performance improvements.
* Gradle: The build tool is Gradle 8.7, which offers a robust and modern build system for Java projects.
* gRPC Framework: The project integrates with grpc-spring-boot-starter (version 3.15), allowing for seamless gRPC integration with Spring Boot.
* Spring Boot Version: The project uses Spring Boot 3.2.4, a recent version ensuring compatibility with various dependencies and offering strong support for modern Spring Boot features.
* Hibernate and JPA: The project uses Hibernate for ORM with JPA support for database interactions.
* In-memory Database: H2 is utilized for quick development and testing.
* Protocol Buffers: Use protoc (the Protocol Buffers compiler) to generate gRPC service classes from .proto files. This generation process ensures that your gRPC client and server implementations are based on the same contract, minimizing compatibility issues.
* You can integrate ProtoBuf generation into your Gradle build process with the protobuf-gradle-plugin, allowing automated generation of Java classes from the proto definitions.

This setup provides a modern development environment. Ensure that your development environment is configured to support these technologies. Make sure all external dependencies and plugins in Gradle build files align with the given versions.

