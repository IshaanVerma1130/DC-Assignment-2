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

### Notes:

- By default for testing, `number of Clients` = 8 and `number of CS` = 8. This can be changed from the `makefile` using the variables `CS` and `Client`
- All JAR files have already been included in the lib folder in every project.
