package lab7_1;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class PrefixHelper {
    public static List<Integer> prefixSequential(List<Integer> sequence) {
        List<Integer> result = new ArrayList<>(sequence);
        for (int i = 1; i < result.size(); i++) {
            result.set(i, result.get(i) + result.get(i - 1));
        }
        return result;
    }

    public static List<Integer> prefixParallel(List<Integer> sequence, Integer nrThreads) {
        List<Integer> result = new ArrayList<>(sequence);

        try {
            ThreadPoolExecutor threadPool;

            int k;
            for (k = 1; k < result.size(); k = k * 2) {
                threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nrThreads);
                for (int i = 2 * k - 1; i < result.size(); i = i + 2 * k) {
                    int finalI = i;
                    int finalK = k;
                    threadPool.submit(() -> {
                        result.set(finalI, result.get(finalI) + result.get(finalI - finalK));
                    });
                }
                threadPool.shutdown();
                threadPool.awaitTermination(5, TimeUnit.MINUTES);
            }

            k = k / 4;

            for (; k > 0; k = k / 2) {
                threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(nrThreads);
                for (int i = 3 * k - 1; i < result.size(); i = i + 2 * k) {
                    int finalI = i;
                    int finalK = k;
                    threadPool.submit(() -> {
                        result.set(finalI, result.get(finalI) + result.get(finalI - finalK));
                    });
                }
                threadPool.shutdown();
                threadPool.awaitTermination(5, TimeUnit.MINUTES);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }
}
