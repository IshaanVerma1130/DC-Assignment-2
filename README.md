# Assignment 2

### Getting started:

`Note`: This makefile is a single point of contact for running a simulation of the distributed system. If you would like to run each component individually, please check their individual README files.

1. Open the root folder in a terminal.
2. Build all of them using the following command from the root directory

   ```shell
   make
   ```

3. Running the Aggregation Server, Content Servers and Clients

   ```shell
   make run
   ```

   Testing `Data` for both `CSs` and `Clients` will be generated `automatically`.

4. To clean all output files
   ```shell
   make clean
   ```
5. Logs for all of the components are found in the logs folder in each directory according to their respective IDs.

### Testing:

1. Data generation for GETClient GET requests and Content-Server PUT requests has been generated programatically.
2. You can change the number of entries for GET requests using the `numOfEntries` variable in `GETClient/test/TestDataGenerator.java`.
3. There are a total of 10 cities I have tested but if you want more, you can add them in the mappings `idCityMap` and `idStateMap` in `Content-Server/test/TestDataGenerator.java`. Dont forget to add the CITY IDs in `ids` array.
4. 6 threads are assigned to both the Clients and Content-Servers to send their respective requests.
5. All logs files are named based on the ID of Clients/Content-Servers. You can find them in their respective folders.
6. Please also read the individual README.md files in all directories.

### Notes:

- By default for testing, `number of Clients = 8` and `number of CS = 8`. This can be changed from the `makefile` using the variables `CS` and `CLIENT`
- All JAR files have already been included in the lib folder in every project.
