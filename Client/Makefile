# Define the Java compiler
JAVAC = javac

# Define the source directory and test directory
SRC_DIR = src
TEST_DIR = test

# Define the classpath for external libraries (JAR files)
LIB_DIR = lib

# Modify the CLASSPATH variable to include individual JAR files
CLASSPATH = $(LIB_DIR)/jackson-core-2.15.2.jar:$(LIB_DIR)/jackson-annotations-2.15.2.jar:$(LIB_DIR)/jackson-databind-2.15.2.jar

# Define the output directory for compiled classes
OUT_DIR = out

# Define the main class
MAIN_CLASS = GETClient

# Define test class
TEST_CLASS = TestClient

# Define the source files (all .java files in the source directory)
SOURCES = $(wildcard $(SRC_DIR)/*.java) $(wildcard $(TEST_DIR)/*.java)

# Default arguments for the Java program (as a space-separated list)
ARG = 8
ARGS = localhost 4567 1

# Build the project
all: compile

# Compile the Java source files to classes
compile: $(SOURCES)
	@mkdir -p $(OUT_DIR)
	@$(JAVAC) -cp $(CLASSPATH) -d $(OUT_DIR) $^

# Compile and run the Java program with arguments
run-client: compile
	java -cp $(CLASSPATH):$(OUT_DIR) $(SRC_DIR)/$(MAIN_CLASS) $(ARGS)

# Run tests
run-test:
	java -cp $(CLASSPATH):$(OUT_DIR) $(TEST_DIR)/$(TEST_CLASS) $(ARG)
	
# Clean the compiled files and JAR
clean:
	@rm -rf $(OUT_DIR)

.PHONY: all compile run clean
