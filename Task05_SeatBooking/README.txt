Q5 – Race Conditions and Synchronization (6 marks)

Compile: javac *.java
Run:     java SeatBooking

The program compares:
1. Unsynchronized check-then-book (race condition demonstration).
2. Synchronized method.
3. Synchronized block around the critical section.

There are 10 seats and 100 users (10 users compete for each seat).
The correct synchronized versions should sell exactly 10 seats.
