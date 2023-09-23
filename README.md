# Assignment 2

### Getting started:

1. Open the 3 directories in 3 different terminals.
2. Build all of them using the following command from the root directory

   ```shell
   make
   ```

3. First run the `Aggregation Server`.
4. Once the `Aggregation Server` is up, you can run both the `GETClient` and `Content Server`.

### Notes:

- `Content Server` stored the data to be sent in a JSON file.
- There are default `SERVER_URL` and `PORT` setup for both the `Content Server` and `GETClient`.
- All JAR files have already been included in the lib folder in every project.

### TO-DO:

1. Make the queue in the `Aggregation Server` a `priority queue` that compares the timestamps of the incoming the request to order them.
2. Implement `testing` scripts.
3. Make a `file buffer` for modifiying the JSON data file on the `Aggregation Server` so that the original file doesn't get disturbed if the server crashes.
4. Implement code logic for `periodic deletions` and also checking the connection time for the server when no new request has been sent
