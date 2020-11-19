
#compiler flag variables
JFLAGS = 
#compiler
JC = javac

#build targets
.SUFFIXES: .java .class

#target entry for building .class files from .java files
.java.class:
	$(JC) $(JFLAGS) $*.java

CLASSES = \
        TCPServer.java \
        TCPClient.java \
        TCPServerThreads.java \
        Message.java 

default: classes

classes: $(CLASSES:.java=.class)

server: 
	java TCPServer

client: 
	java TCPClient

clean:
	$(RM) log.txt
	$(RM) *.class