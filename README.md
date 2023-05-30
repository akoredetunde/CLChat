# CLChat
CLChat is a Simple Comand Line Server/Client chat program. 
This program is a simple comand line interface program
The program behave as a server that wait for a connetion from a client.
The program only support one connection. As soon as a connection is establish the server stop listening for new connection.
The port that the server is listening on or running can be specify as a command line argument and if none is specify a defualt port is used which is port 5000.
The two end of the connected both send a unique string to each other to verify the program at each end is a CLChat program.
Then after both end has confirm that the program is the right program, then the connected programs alternate send messages to each other.
Both end of the connected can close the connection by entering the string 'quit'.
