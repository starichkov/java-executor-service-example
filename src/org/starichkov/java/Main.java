package org.starichkov.java;

import org.starichkov.java.executor.FactorialCalculationService;

import java.math.BigInteger;

public class Main {

    public static void main(String[] args) {
        FactorialCalculationService service = new FactorialCalculationService();

        final int tests = 200;

        final int cores = Runtime.getRuntime().availableProcessors();
        System.out.println("Cores found: " + cores);

        System.out.println("Warming up and self-testing...");

        // warm up
        for (int i = 1; i <= tests; i++) {
            BigInteger f = service.factorial(i * i);
            BigInteger f1 = service.factorialConcurrentViaSubmit(i * i, cores);
            BigInteger f2 = service.factorialConcurrentViaInvokeAll(i * i, cores);
            assert (f == null ? f1 == null : f.equals(f1)) : String.format("%s vs. %s", f, f1);
            assert (f1 == null ? f2 == null : f1.equals(f2)) : String.format("Concurrent V1 vs V2: %s > %s", f1, f2);
        }

        System.out.println("Calculating...");

        long start = System.nanoTime();

        for (int i = 1; i <= tests; i++) {
            BigInteger result = service.factorial(i * i);
        }

        long end = System.nanoTime();

        System.out.printf("Single threaded took %.3f sec", (end - start) / 1e9);
        System.out.println();

        if (cores > 1) {
            for (int threads = 2; threads <= cores; threads++) {
                start = System.nanoTime();

                for (int i = 1; i <= tests; i++) {
                    BigInteger result = service.factorialConcurrentViaSubmit(i * i, threads);
                }

                end = System.nanoTime();
                System.out.printf("Multi-threaded [%s] (ExecutorService.submit) took %.3f sec", threads, (end - start) / 1e9);
                System.out.println();

                start = System.nanoTime();

                for (int i = 1; i <= tests; i++) {
                    BigInteger result = service.factorialConcurrentViaInvokeAll(i * i, threads);
                }

                end = System.nanoTime();
                System.out.printf("Multi-threaded [%s] (ExecutorService.invokeAll) took %.3f sec", threads, (end - start) / 1e9);
                System.out.println();
            }
        }
    }
}
