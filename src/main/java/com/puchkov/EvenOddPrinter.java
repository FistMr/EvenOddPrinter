package com.puchkov;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EvenOddPrinter {
    private static final Logger logger = LoggerFactory.getLogger(EvenOddPrinter.class);
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
                            logger.error("Поток для чётных чисел был прерван во время ожидания", e);
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    logger.info("Чётное: {}", i);
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
                            logger.error("Поток для нечётных чисел был прерван во время ожидания", e);
                            Thread.currentThread().interrupt();
                            return;
                        }
                    }
                    System.out.println("Нечётное: " + i);
                    isEvenNext = true;
                    lock.notify();
                }
            }
        });
        logger.info("Запуск потоков...");
        evenThread.start();
        oddThread.start();
        try {
            evenThread.join();
            oddThread.join();
            logger.info("Оба потока успешно завершили работу");
        } catch (InterruptedException e) {
            logger.error("Главный поток был прерван во время ожидания завершения потоков", e);
            evenThread.interrupt();
            oddThread.interrupt();
            Thread.currentThread().interrupt();
            System.exit(1);
        }
    }
}
