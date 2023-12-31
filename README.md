
<h1 style="font-size: 42px;">TCP/IP Server Application</h1>

# Summary
The server application implements various functionalities to handle client information, execute commands, and maintain periodic communications.

# Functionalities
- Processing Client Information
    - Receive data and perform operations based on the input type.
    - Transform letter case, manipulate strings, and multiply numbers.
    - Log incoming and outgoing messages in "LogFile".

- Handling Client Commands
    - Accept client commands and provide corresponding responses from a pre-defined list.
    - Record command interactions in "LogFile".

- Periodic Counter Messages
    - Every 10 seconds, sends a message with a counter and timestamp.
  
# Project structure
- src/main/java: contains all the source code for the application.
- logs/LogFile.log - is a file for logging client-server interaction.
- checkstyle/checkstyle.xml - is a configuration file for the checkstyle tool, which is used to check the code style. It contains settings for various checkstyle modules that perform various code checks for compliance with style standards.
- pom.xml - used to configure and create a Maven project, add the necessary dependencies.
- resources - contains the "Commands.txt" file, which provides a list of commands that are available to the client, as well as values for each individual command, and log4j.xml - file for logger configuration.

# How to run the application
To start the application, it is enough to get the source code of the project and that it is important to run the server first and then connect the client.
