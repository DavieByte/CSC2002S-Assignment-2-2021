BIN=./bin
SRC=./src
DOC=./doc
BINA=bin

.SUFFIXES: .java .class

$(BIN)/%.class: $(SRC)/%.java
	javac $< -cp $(SRC) -d $(BIN)

all: $(BIN)/WordApp.class

$(BIN)/WordApp.class: $(BIN)/Score.class $(BIN)/WordDictionary.class $(BIN)/WordRecord.class $(BIN)/WordPanel.class $(SRC)/WordApp.java

clean:
	del $(BINA)\*.class

docs:
	javadoc -classpath $(BIN) -d $(DOC) $(SRC)/*.java
