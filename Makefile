# Define the subproject directories
AGGREGATION_SERVER_DIR = Aggregation-Server
CLIENT_DIR = Client
CONTENT_SERVER_DIR = Content-Server

# Define the targets for each subproject
AGGREGATION_SERVER_TARGET = -C $(AGGREGATION_SERVER_DIR)
CLIENT_TARGET = -C $(CLIENT_DIR)
CONTENT_SERVER_TARGET = -C $(CONTENT_SERVER_DIR)

# Number of Clients and CSs for testing
CS = 8
CLIENT = 8

# Define the default target (all subprojects)
all: aggregation_server client content_server

# Build the aggregation server
aggregation_server:
	$(MAKE) $(AGGREGATION_SERVER_TARGET)

# Build the client
client:
	$(MAKE) $(CLIENT_TARGET)

# Build the content server
content_server:
	$(MAKE) $(CONTENT_SERVER_TARGET)

# Clean all subprojects
clean:
	$(MAKE) clean_aggregation_server
	$(MAKE) clean_client
	$(MAKE) clean_content_server

# Clean the aggregation server
clean_aggregation_server:
	$(MAKE) clean $(AGGREGATION_SERVER_TARGET)

# Clean the client
clean_client:
	$(MAKE) clean $(CLIENT_TARGET)

# Clean the content server
clean_content_server:
	$(MAKE) clean $(CONTENT_SERVER_TARGET)

# Run Aggregation Server
run-as:
	@gnome-terminal -- bash -c "make run -C Aggregation-Server"

# Run client and content server tests after 5 seconds
run-test:
	@sleep 5
	@gnome-terminal -- bash -c "make run-test ARG='$(CS)' -C Content-Server"
	@sleep 2
	@gnome-terminal -- bash -c "make run-test ARG='$(CLIENT)' -C Client"

# Run AS and tests
run: run-as run-test

.PHONY: all aggregation_server client content_server clean clean_aggregation_server clean_client clean_content_server
