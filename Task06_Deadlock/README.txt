Q6 – Deadlock and Inter-thread Communication (7 marks)

Compile: javac *.java
Run token counter: java TokenCounter
Run wallet transfers: java WalletTransferDemo

TokenCounter uses a bounded buffer of capacity 5, wait() and notifyAll().
WalletTransferDemo demonstrates deadlock prevention through consistent
account-lock ordering. The unsafe method is included for code analysis but
is not started in the normal run because a deliberate deadlock can hang a run.
