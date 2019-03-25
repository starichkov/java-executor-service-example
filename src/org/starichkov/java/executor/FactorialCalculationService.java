package org.starichkov.java.executor;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * @author Vadim Starichkov
 * @since 21.01.2019 14:49
 */
public class FactorialCalculationService {

    public BigInteger factorial(long n) {
        BigInteger result = BigInteger.ONE;
        for (long i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }
        return result;
    }

    public BigInteger factorialConcurrentViaSubmit(long n, int threadCount) {
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        long batchSize = (n + threadCount - 1) / threadCount;

        try {
            List<Future<BigInteger>> results = new ArrayList<Future<BigInteger>>();

            for (long start = 1; start <= n; start += batchSize) {
                final long end = Math.min(n + 1, start + batchSize);

                results.add(service.submit(getFactorialBatchCallable(start, end)));
            }

            BigInteger result = BigInteger.ONE;
            for (Future<BigInteger> future : results) {
                result = result.multiply(future.get());
            }
            return result;

        } catch (Exception e) {
            throw new AssertionError(e);
        } finally {
            service.shutdown();
        }
    }

    public BigInteger factorialConcurrentViaInvokeAll(long n, int threadCount) {
        ExecutorService service = Executors.newFixedThreadPool(threadCount);

        long batchSize = (n + threadCount - 1) / threadCount;

        try {
            Collection<Callable<BigInteger>> callableCollection = new LinkedList<Callable<BigInteger>>();

            for (long start = 1; start <= n; start += batchSize) {
                final long end = Math.min(n + 1, start + batchSize);

                callableCollection.add(getFactorialBatchCallable(start, end));
            }

            List<Future<BigInteger>> results = service.invokeAll(callableCollection);

            BigInteger result = BigInteger.ONE;
            for (Future<BigInteger> future : results) {
                result = result.multiply(future.get());
            }
            return result;

        } catch (Exception e) {
            throw new AssertionError(e);
        } finally {
            service.shutdown();
        }
    }

    private Callable<BigInteger> getFactorialBatchCallable(final long start, final long end) {
        return new Callable<BigInteger>() {
            @Override
            public BigInteger call() {
                BigInteger result = BigInteger.valueOf(start);
                for (long i = start + 1; i < end; i++) {
                    result = result.multiply(BigInteger.valueOf(i));
                }
                return result;
            }
        };
    }
}
