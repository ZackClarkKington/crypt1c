# crypt1c
It is recommended that you launch the jar files from the command line, as in most operating systems the command to run the jar will not be correctly called if the jar file is simply double-clicked.

## Key-Exchange:
The client begins by sending it's public key to the server, the server's cryptobot responds by using the public key to encrypt the shared symmetrical key and sends this to the client. The client then uses this key to encrypt it's messages to other clients.
