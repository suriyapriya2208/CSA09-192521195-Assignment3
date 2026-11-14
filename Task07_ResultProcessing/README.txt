Q7 – Concurrent, Fault-Tolerant Result Processing (10 marks)

Compile: javac *.java
Run:     java ResultProcessor

The program creates a sample input directory named score-files, containing
valid score files plus a corrupt file. It also attempts to process one missing
file. It then processes the file list using fixed thread pools of 1, 2, 4 and
8 threads, using Callable, Future and ConcurrentLinkedQueue.

The generated sample files are for demonstration. The program can create
10,000 input files by changing FILE_COUNT to 10000, but the default is a
smaller test set so the first run is quick and easy to inspect.
