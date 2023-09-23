# Content Server (CS)

## Overview

The Content Server (CS) reads the JSON file present in its resource directory and sends the data to the Aggregation Server.

## Table of Contents

1. **Prerequisites**
2. **Project Structure**
3. **Building the Application**
4. **Running the Application**
5. **Usage**
6. **Cleaning Up**

---

### 1. Prerequisites

Before using the Content Server (CS) application, ensure that you have the following prerequisites installed on your system:

- Java Development Kit (JDK) 8 or higher
- External JAR libraries mentioned in the makefile (Jackson libraries)

### 2. Project Structure

The project has the following structure:

- **src**: Contains the Java source code files.
- **lib**: Contains external JAR libraries, including Jackson libraries.
- **resources**: Directory for storing JSON data files.
- **out**: The output directory for compiled Java classes.
- **makefile**: A makefile for building, running, and cleaning the project.

### 3. Building the Application

To build the CS application, follow these steps:

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

By default, this command runs the CS application with the following arguments:

- `<SERVER URL>`: localhost
- `<PORT>`: 4567
- `<CS ID>`: 1

You can modify the default arguments in the makefile (under the ARGS variable) or specify your own arguments when running the application.

### 5. Usage

The CS application interacts with a remote server by sending JSON content requests stored in files named CS-{CS ID}.json. These JSON files are located in the resources directory. The CS application reads these files, sends HTTP POST requests to the server, and processes server responses.

### 6. Cleaning up

To clean the compiled files and JAR, run the following command:

```shell
make clean
```

This will remove the out directory.
