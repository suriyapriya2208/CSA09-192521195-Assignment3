Q1 – ATM Failure Handling (6 marks)

Compile: javac *.java
Run:     java ATM

The program handles:
- non-numeric account number / withdrawal input
- negative or zero withdrawal
- withdrawal above available balance
- missing card-reader file (checked IOException)
- successful withdrawal

To test the missing-file path, enter a card-reader filename that does not exist.
The default file card-reader.txt is included.
