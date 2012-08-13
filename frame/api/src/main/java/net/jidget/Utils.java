package net.jidget;

import java.util.concurrent.*;
import javafx.application.Platform;
import javafx.util.Callback;

/**
 *
 * @author Arian Treffer
 */
public class Utils {
    
    public static void runLater(Runnable r) {
        Platform.runLater(r);
    }
    
    public static void runAndWait(final Runnable r) throws InterruptedException {
        final CountDownLatch cdl = new CountDownLatch(1);
        runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    r.run();
                } finally {
                    cdl.countDown();
                }
            }
        });
        cdl.await();
    }
    
    public static <P, R> Future<R> executeLater(final Callback<P, R> c, final P arg) {
        FutureTask<R> task = new FutureTask<R>(new Callable<R>() {
            @Override
            public R call() throws Exception {
                return c.call(arg);
            }
        });
        runLater(task);
        return task;
    }
    
    public static <P, R> R executeAndWait(Callback<P, R> c, P arg) throws InterruptedException {
        try {
            return executeLater(c, arg).get();
        } catch (ExecutionException e) {
            Throwable cause = e.getCause();
            if (cause instanceof RuntimeException) {
                throw (RuntimeException) cause;
            } else {
                throw new RuntimeException(cause);
            }
        }
    }
    
}
