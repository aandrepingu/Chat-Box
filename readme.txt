To start the application, there are 4 scripts in the script folder which must ne used. If you are on Linux, the following scripts you will run will start with the word “linux.” If you are on Windows, the scripts will start with “win.” 

Prior to running the application itself, you must have a server running. To start a server, you must run the “RunServer” script. After that, you can run as many application as you want per user. To run the application, you will run the “RunApp” script. All scripts must be run within in the scripts directory.


The following is an example to run the server on Windows starting from the root directory
@sh> cd ./scripts
@sh> ./winRunServer

The following is an example to run the application on Windows starting from the root directory
@sh> cd ./scripts
@sh> ./winRunApp

Alternatively, the app can be run by executing the command ./gradlew run from the root directory, and the server can be run by executing ./gradlew bootRun. This works on both Linux and windows.

There are also scripts in the scripts directory for generating javadocs; these must also be run within the scripts directory.

The directories build, bin, and gradle are used for building the app with gradle. Please do not delete or modify them.

This was built and tested on Windows 11 & WSL Ubuntu 22.04.4 LTS.
Built with Java 21

