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

# Define the main class (replace 'AggregationServer' with your actual main class)
MAIN_CLASS = AggregationServer

# Define the source files (all .java files in the source directory)
SOURCES = $(wildcard $(SRC_DIR)/*.java)

# Build the project
all: compile

# Compile the Java source files to classes
compile: $(SOURCES)
	@mkdir -p $(OUT_DIR)
	@$(JAVAC) -cp $(CLASSPATH) -d $(OUT_DIR) $^

# Run the Java program
run-as: compile
	java -cp $(CLASSPATH):$(OUT_DIR) $(SRC_DIR)/$(MAIN_CLASS)

# Clean the compiled files and JAR
clean:
	@rm -rf $(OUT_DIR)

.PHONY: all compile run clean
