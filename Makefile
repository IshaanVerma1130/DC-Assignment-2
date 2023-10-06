# Define the subproject directories
AGGREGATION_SERVER_DIR = Aggregation-Server
CLIENT_DIR = Client
CONTENT_SERVER_DIR = Content-Server

# Define the targets for each subproject
AGGREGATION_SERVER_TARGET = -C $(AGGREGATION_SERVER_DIR)
CLIENT_TARGET = -C $(CLIENT_DIR)
CONTENT_SERVER_TARGET = -C $(CONTENT_SERVER_DIR)

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

# Run client and content server tests
test:
	$(MAKE) run $(AGGREGATION_SERVER_TARGET)
	$(MAKE) test $(CONTENT_SERVER_TARGET)
	$(MAKE) test $(CLIENT_TARGET)

.PHONY: all aggregation_server client content_server clean clean_aggregation_server clean_client clean_content_server
