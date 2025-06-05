package com.puchkov;

public class EvenOddPrinter {
    private static final Object lock = new Object();
    private static boolean isEvenNext = false;
    private static final int MAX_NUMBER = 10;

    public static void main(String[] args) {
        Thread evenThread = new Thread(() -> {
            for (int i = 2; i <= MAX_NUMBER; i += 2) {
                synchronized (lock) {
                    while (!isEvenNext) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.println("Чётное: " + i);
                    isEvenNext = false;
                    lock.notify();
                }
            }
        });
        Thread oddThread = new Thread(() -> {
            for (int i = 1; i <= MAX_NUMBER; i += 2) {
                synchronized (lock) {
                    while (isEvenNext) {
                        try {
                            lock.wait();
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                        }
                    }
                    System.out.println("Нечётное: " + i);
                    isEvenNext = true;
                    lock.notify();
                }
            }
        });

        evenThread.start();
        oddThread.start();
    }
}
