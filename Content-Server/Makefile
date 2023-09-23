# Define the Java compiler
JAVAC = javac

# Define the source directory
SRC_DIR = src

# Define the classpath for external libraries (JAR files)
LIB_DIR = lib

# Modify the CLASSPATH variable to include individual JAR files
CLASSPATH = $(LIB_DIR)/jackson-core-2.15.2.jar:$(LIB_DIR)/jackson-annotations-2.15.2.jar:$(LIB_DIR)/jackson-databind-2.15.2.jar

# Define the output directory for compiled classes
OUT_DIR = out

# Define the main class
MAIN_CLASS = CS

# Define the source files (all .java files in the source directory)
SOURCES = $(wildcard $(SRC_DIR)/*.java)

# Define the target JAR file
JAR_FILE = CS.jar

# Default arguments for the Java program (as a space-separated list)
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
run: compile
	java -cp $(CLASSPATH):$(OUT_DIR) $(MAIN_CLASS) $(ARGS)

# Clean the compiled files and JAR
clean:
	rm -rf $(OUT_DIR) $(JAR_FILE)

.PHONY: all compile jar run clean
