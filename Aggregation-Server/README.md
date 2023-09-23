# Aggregation Server

## Overview

The Aggregation Server is designed to serve HTTP requests, handle GET and PUT requests for city data, and maintain Lamport timestamps for synchronization. It maintains a queue for requests that cannot be executed the moment they were recieved. There are executors setup to continuously check of new entries in this queue. The architecture of the server is multithreaded.

## Table of Contents

1. **Prerequisites**
2. **Project Structure**
3. **Building the Application**
4. **Running the Application**
5. **Usage**
6. **Cleaning Up**

---

### 1. Prerequisites

Before using the Aggregation Server application, ensure that you have the following prerequisites installed on your system:

- Java Development Kit (JDK) 8 or higher
- External JAR libraries mentioned in the makefile (Jackson libraries)

### 2. Project Structure

The project has the following structure:

- **src**: Contains the Java source code files.
- **lib**: Contains external JAR libraries, including Jackson libraries.
- **out**: The output directory for compiled Java classes.
- **makefile**: A makefile for building, running, and cleaning the project.

### 3. Building the Application

To build the Aggregation Server application, follow these steps:

1. Open your terminal/command prompt.
2. Navigate to the project directory containing the `makefile`.
3. Run the following command to compile the Java source code:

   ```shell
   make compile
   ```

   This will compile the source code and generate the out directory with the compiled classes.

### 4. Running the Application

After building the application, you can run it with the following command:

```shell
make run
```

### 5. Usage

The Aggregation Server listens for incoming HTTP GET and PUT requests. It handles these requests based on Lamport timestamps for synchronization. The server maintains city data and responds to requests accordingly.

### 6. Cleaning up

To clean the compiled files and JAR, run the following command:

```shell
make clean
```

This will remove the out directory.
