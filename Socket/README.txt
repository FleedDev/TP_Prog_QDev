Distributed version of MC for PI using Java socket.

usage on a localhost: 
One terminal for pi.Master
One terminal for each pi.Worker

on each server terminal (pi.Worker):
make
java WorkerSocket <port>

on client terminal (pi.Master):
make
make run