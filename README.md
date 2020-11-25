# Network_TP

This is our code for the Network labs. We are Bassel Slim and Clément Parret (B02).
This project contains 3 .jar files.

## For the TCP chat :
- Open 3 terminals at the root of the project.
- Use the command "java -jar lib/Server.jar" in one of those terminals.
- In the user interface, enter a port (for instance 1234) and click "start server".
- Use the command "java -jar lib/Client.jar" in the other two terminals.
- In each client user interface, choose a pseudo, enter the IP address of the server and the port that you chose for the server, then click "join chat".
- The clients can now chat among them.

## For the multicast/UDP chat
- Open 2 terminals at the root of the project.
- Use the command "java -jar lib/Multicast.jar" in each terminal.
- In each user interface, choose a pseudo, a host address, and a port, then click "join chat".
- If you chose the same host and port, the clients can now chat among them.