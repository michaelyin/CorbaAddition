#CORBA demo (all in cmd window)

It is assumed that the corba util classes have been generated: idlj -fall Addition.idl

#1. start orb broker

start orbd -ORBInitialPort 1050

#2. start server

C:\workspace\CorbaAddition>java -classpath ./bin StartServer -ORBInitialPort 1050 -ORBInitialHost localhost

#3. start client

C:\workspace\CorbaAddition>java -classpath ./bin StartClient  -ORBInitialPort 1050 -ORBInitialHost localhost


#4. run applet with appletviewer
c:/Java/jdk1.8.0_45/bin/appletviewer demo.html -J-Djava.security.policy=applet.policy

# run applet in browser
Install jboss wildfly 8.2.0 at c:/opt/

open http://localhost:8080/corba