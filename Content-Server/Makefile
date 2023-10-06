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
MAIN_CLASS = CS

# Define test class
TEST_CLASS = TestDataGenerator

# Define the source files (all .java files in the source directory)
SOURCES = $(wildcard $(SRC_DIR)/*.java) $(wildcard $(TEST_DIR)/*.java)

# Define the target JAR file
JAR_FILE = CS.jar

# Default arguments for the Java program (as a space-separated list)
ARG = 1
ARGS = localhost 4567 1

# Build the project
all: compile

# Compile the Java source files to classes
compile: $(SOURCES)
	@mkdir -p $(OUT_DIR)
	$(JAVAC) -cp $(CLASSPATH) -d $(OUT_DIR) $^

# Create a JAR file for the project
jar:
	jar cvf $(JAR_FILE) -C $(OUT_DIR) .

# Compile and run the Java program with arguments
run-cs: compile
	java -cp $(CLASSPATH):$(OUT_DIR) $(MAIN_CLASS) $(ARGS)

# Generate test file
gen-test: compile
	java -cp $(CLASSPATH):$(OUT_DIR) $(TEST_CLASS) $(ARG)

# Generate all test files
gen-all-tests:
	@gnome-terminal -- bash -c "make gen-test arg=1"
	@gnome-terminal -- bash -c "make gen-test arg=2"
	@gnome-terminal -- bash -c "make gen-test arg=3"

# Compile and run CSs in parallel
run-all-cs:
	@gnome-terminal -- bash -c "make run-cs args=localhost 4567 1"
	@gnome-terminal -- bash -c "make run-cs args=localhost 4567 2"
	@gnome-terminal -- bash -c "make run-cs args=localhost 4567 3"

# Run all tests
test: gen-all-tests run-all-cs

# Clean the compiled files and JAR
clean:
	rm -rf $(OUT_DIR) $(JAR_FILE)

.PHONY: all compile jar run clean
