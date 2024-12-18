package Paradigme.MW;

import Paradigme.WriteToFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.*;

public class Pi {
    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Usage: java Paradigme.MW.Pi <totalIterations> <numWorkers>");
            System.exit(1);
        }
        int totalIterations = Integer.parseInt(args[0]);
        int numWorkers = Integer.parseInt(args[1]);

        long iterationsPerWorker = totalIterations / numWorkers;

        long total = new Master().doRun(totalIterations, numWorkers, iterationsPerWorker);

        System.out.println("Total from Master = " + total);
    }

    /**
     * Creates workers to run the Monte Carlo simulation
     * and aggregates the results.
     */
    static class Master {
        public long doRun(int totalIterations, int numWorkers, long iterationsPerWorker) throws InterruptedException, ExecutionException {

            long startTime = System.currentTimeMillis();

            // Create a collection of tasks
            List<Callable<Long>> tasks = new ArrayList<>();
            for (int i = 0; i < numWorkers; ++i) {
                tasks.add(new Worker(iterationsPerWorker));
            }

            // Run them and receive a collection of Futures
            ExecutorService exec = Executors.newFixedThreadPool(numWorkers);
            List<Future<Long>> results = exec.invokeAll(tasks);
            long totalInCircle = 0;

            // Assemble the results.
            for (Future<Long> f : results) {
                // Call to get() is an implicit barrier.  This will block
                // until result from corresponding worker is ready.
                totalInCircle += f.get();
            }

            // Correct calculation of Pi
            double pi = 4.0 * totalInCircle / totalIterations;

            long stopTime = System.currentTimeMillis();

            System.out.println("Ntot: " + totalIterations);
            System.out.println("Available processors: " + numWorkers);
            System.out.println("Time Duration (ms): " + (stopTime - startTime));
            System.out.println("Pi value : " + pi);
            System.out.println("Error: " + (Math.abs((pi - Math.PI)) / Math.PI));

            // Write results to file
            String fileName = "personal_pc_12cores_pi_forte";
            WriteToFile.write(totalIterations, numWorkers, totalInCircle, stopTime - startTime, pi, Math.abs((pi - Math.PI)) / Math.PI, fileName);

            exec.shutdown();
            return totalInCircle;
        }
    }

    /**
     * Task for running the Monte Carlo simulation.
     */
    static class Worker implements Callable<Long> {
        private long numIterations;

        public Worker(long num) {
            this.numIterations = num;
        }

        @Override
        public Long call() {
            long circleCount = 0;
            Random prng = new Random();
            for (int j = 0; j < numIterations; j++) {
                double x = prng.nextDouble();
                double y = prng.nextDouble();
                if ((x * x + y * y) < 1) ++circleCount;
            }
            return circleCount;
        }
    }
}