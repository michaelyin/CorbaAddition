#CORBA demo (all in cmd window)

It is assumed that the corba util classes have been generated: idlj -fall Addition.idl

#1. start orb broker

start orbd -ORBInitialPort 1050

#2. start server

C:\workspace\CorbaAddition>java -classpath ./bin StartServer -ORBInitialPort 1050 -ORBInitialHost localhost

#3. start client

C:\workspace\CorbaAddition>java -classpath ./bin StartClient  -ORBInitialPort 1050 -ORBInitialHost localhost
