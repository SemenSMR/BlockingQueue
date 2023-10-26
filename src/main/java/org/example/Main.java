package org.example;

import java.util.Random;
import java.util.concurrent.*;

public class Main {
    public static BlockingQueue<String> queue1 = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queue2 = new ArrayBlockingQueue<>(100);
    public static BlockingQueue<String> queue3 = new ArrayBlockingQueue<>(100);
    public static Thread textGenerator;

    public static void main(String[] args) throws InterruptedException {

        textGenerator = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                String text = generateText("abc", 100000);
                try {
                    queue1.put(text);
                    queue2.put(text);
                    queue3.put(text);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        textGenerator.start();

        Thread threadQueue1 = getThread(queue1, 'a');
        Thread threadQueue2 = getThread(queue2, 'b');
        Thread threadQueue3 = getThread(queue3, 'c');

        threadQueue1.start();
        threadQueue2.start();
        threadQueue3.start();

        threadQueue1.join();
        threadQueue2.join();
        threadQueue3.join();


    }
        public static String generateText (String letters,int length){
            Random random = new Random();
            StringBuilder text = new StringBuilder();
            for (int i = 0; i < length; i++) {
                text.append(letters.charAt(random.nextInt(letters.length())));
            }
            return text.toString();
        }

        public static Thread getThread(BlockingQueue<String> queue1, char letter){
            return new Thread(() -> {
                int max = findMaxCounter(queue1, letter);
                System.out.println("Максимальное количество " + letter + " в тексте: " + max);
            });
        }

        private static int findMaxCounter(BlockingQueue<String> queue1, char letter){
            int count = 0;
            int max = 0;
            String text;
            try {
                while (textGenerator.isAlive()) {
                    text = queue1.take();
                    for (char c : text.toCharArray()) {
                        if (c == letter)
                            count++;
                    }
                        if (count > max)
                            max = count;
                        count = 0;


                }
            } catch (InterruptedException e) {
                System.out.println(Thread.currentThread().getName() + "was interrupted");
                return -1;
            }
            return max;
        }
    }




