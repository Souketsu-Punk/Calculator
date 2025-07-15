import java.util.concurrent.ThreadLocalRandom;

class CounterMonitor {
    private int counter = 0;

    // Synchronized method
    public synchronized void increment(String threadName, int attempt) {
        int current = counter;
        System.out.println(threadName + " sees counter at attempt " + attempt + ": " + current);
        counter = current + 1;
        System.out.println(threadName + " increments counter to: " + counter);
    }

    public synchronized int getCounter() {
        return counter;
    }
}

class Worker implements Runnable {
    private final CounterMonitor monitor;
    private final int increments;
    private final String name;

    public Worker(CounterMonitor monitor, int increments, String name) {
        this.monitor = monitor;
        this.increments = increments;
        this.name = name;
    }

    @Override
    public void run() {
        for (int i = 0; i < increments; i++) {
            monitor.increment(name, i + 1);
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println(name + " was interrupted.");
            }
        }
    }
}

public class MonitorDemo {
    public static void main(String[] args) {
        CounterMonitor monitor = new CounterMonitor();

        Thread t1 = new Thread(new Worker(monitor, 5, "Thread-1"));
        Thread t2 = new Thread(new Worker(monitor, 5, "Thread-2"));

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("Main thread was interrupted.");
        }

        System.out.println("Final counter value: " + monitor.getCounter());
    }
}
