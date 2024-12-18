package Paradigme.Iteration;
// Estimate the value of Pi using Monte-Carlo Method, using parallel program

import Paradigme.WriteToFile;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class PiMonteCarlo {
    AtomicInteger nAtomSuccess;
    int nThrows;
    double value;

    class MonteCarlo implements Runnable {
        @Override
        public void run() {
            double x = Math.random();
            double y = Math.random();
            if (x * x + y * y <= 1)
                nAtomSuccess.incrementAndGet();
        }
    }

    public PiMonteCarlo(int i) {
        this.nAtomSuccess = new AtomicInteger(0);
        this.nThrows = i;
        this.value = 0;
    }

    public double getPi(int numWorkers) {
        ExecutorService executor = Executors.newFixedThreadPool(numWorkers);
        for (int i = 1; i <= nThrows; i++) {
            Runnable worker = new MonteCarlo();
            executor.execute(worker);
        }
        executor.shutdown();
        while (!executor.isTerminated()) {
        }
        value = 4.0 * nAtomSuccess.get() / nThrows;
        return value;
    }
}

public class Assignment102 {
    public Assignment102() {}

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Usage: java Paradigme.Iteration.Assignment102 <totalCount> <numWorkers>");
            return;
        }

        int totalCount = Integer.parseInt(args[0]);
        int numWorkers = Integer.parseInt(args[1]);

        PiMonteCarlo PiVal = new PiMonteCarlo(totalCount);
        long startTime = System.currentTimeMillis();
        double value = PiVal.getPi(numWorkers);
        long stopTime = System.currentTimeMillis();

        long durationMs = stopTime - startTime;
        double error = (value - Math.PI) / Math.PI * 100.0;

        System.out.println("Total iterations: " + totalCount);
        System.out.println("Available processors: " + numWorkers);
        System.out.println("Time Duration: " + durationMs + "ms");
        System.out.println("Pi value : " + value);
        System.out.println("Error: " + error + " %");

        String fileName = "personal_pc_12cores_Assignment102_faible";
        WriteToFile.write(totalCount, numWorkers, PiVal.nAtomSuccess.get(), durationMs, value, error, fileName);
    }
}