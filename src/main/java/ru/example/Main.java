package ru.example;

import lombok.NonNull;

import java.io.PrintStream;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class Main {
    private final static BigInteger THREE = new BigInteger("3");
    private final static BigInteger HUNDRED_THOUSANDS = new BigInteger("100000");
    static void main() {
        System.out.println("Choose, what you want to do:\n1 -- standard: find prime numbers\n2 -- find prime factors of a number");

        Scanner scn = new Scanner(System.in);

        switch (scn.nextLine()) {
            case "1": break;
            case "2": findFactors(scn, null); return;
            default: System.out.println("Invalid input"); main(); break;
        }

        System.out.println("Enter start and end of searching separated by spaces.");

        String input = scn.nextLine();

        BigInteger start;
        BigInteger end;

        try {
            start = new BigInteger(input.split(" ")[0]);
            end = new BigInteger(input.split(" ")[1]);
        } catch (NullPointerException | ArrayIndexOutOfBoundsException ignored) { System.out.println("Invalid input"); main(); return; }

        if (start.compareTo(end) > 0) throw new IllegalArgumentException("start cannot be > end");
        if (start.compareTo(BigInteger.ZERO) < 0) throw new IllegalArgumentException("start or end cannot be < 0");
        if (start.mod(BigInteger.TWO).equals(BigInteger.ZERO)) start = start.add(BigInteger.ONE);
        //if (start.equals(BigInteger.ONE)) start = start.add(BigInteger.TWO);

        if (start.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) < 0 && end.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) < 0) {
            System.out.println("Using Long mode");
            System.out.println("Starting!");
            long startMs = System.currentTimeMillis();

            var outputs = start(Long.parseLong(String.valueOf(start)), Long.parseLong(String.valueOf(end)));

            if (Long.parseLong(String.valueOf(start)) <= 2 && Long.parseLong(String.valueOf(end)) >= 2)
                System.out.println("2");

            Iterator<CompletableFuture<List<Long>>> it = outputs.iterator();
            while (it.hasNext()) {
                CompletableFuture<List<Long>> output = it.next();
                for (Long i : output.join()) {
                    System.out.println(i);
                }
                it.remove();
            }

            System.out.println("The search took " + (System.currentTimeMillis() - startMs) + " ms");
        }

        else {
            System.out.println("Using BigInteger mode");
            System.out.println("Starting!");
            long startMs = System.currentTimeMillis();

            var outputs = start(start, end);

            if (start.compareTo(BigInteger.TWO) <= 0 && end.compareTo(BigInteger.TWO) >= 0)
                System.out.println("2");

            for (CompletableFuture<List<BigInteger>> output : outputs) {
                for (BigInteger i : output.join()) {
                    System.out.println(i);
                }
            }

            System.out.println("The search took " + (System.currentTimeMillis() - startMs) + " ms");
        }
    }

    public static void findFactors(Scanner scn, PrintStream out) {
        if (scn == null) scn = new Scanner(System.in);
        if (out == null) out = System.out;
        out.println("Enter a number");
        AtomicLong number = new AtomicLong(Long.parseLong(scn.nextLine()));
        if (number.longValue() < 2) {
            out.println("Invalid input");
            main();
        } else {
            final List<Long> primes = Stream.concat(start(1L, (long) Math.ceil(Math.sqrt(number.doubleValue()))).stream().map(CompletableFuture::join).flatMap(Collection::stream), Stream.of(1L, 2L)).toList();

            var result = Collections.synchronizedList(new ArrayList<Long>());

            out.println("Starting!");

            long startMs = System.currentTimeMillis();

            Set<CompletableFuture<Void>> futures = new HashSet<>();

            for (long prime : primes) {
                futures.add(CompletableFuture.runAsync(() -> {
                    long current;
                    while ((current = number.get()) % prime == 0 && prime != 1) {
                        if (number.compareAndSet(current, current / prime)) {
                            result.add(prime);
                        }
                    }
                }));
            }

            for (CompletableFuture<Void> future : futures) {
                future.join();
            }

            /*while (!primes.contains(number)) {
                for (Long prime : primes) {
                    if (number % prime == 0 && prime != 1) {
                        number = number / prime;
                        result.add(prime);
                    }
                }
            }*/

            out.println("Result:");
            if (!(number.get() == new AtomicLong(1).get())) out.print(number + " ");
            for (Long prime : result) {
                out.print(prime + " ");
            }
            out.print("\nThe search took " + (System.currentTimeMillis() - startMs) + " ms");
        }
    }

    private static void work(@NonNull List<CompletableFuture<List<BigInteger>>> outputs, final BigInteger start, final BigInteger end) {
        outputs.add(CompletableFuture.supplyAsync(() -> {
            List<BigInteger> output = new ArrayList<>();
            for (BigInteger i = start; i.compareTo(end) < 1; i = i.add(BigInteger.TWO)) {
                boolean ok = !i.equals(BigInteger.ONE);
                for (BigInteger j = THREE; j.multiply(j).compareTo(i) < 1; j = j.add(BigInteger.TWO)) {
                    if (i.mod(j).equals(BigInteger.ZERO) && !i.equals(j)) {
                        ok = false;
                        break;
                    }
                }
                if (ok){
                    output.add(i);
                }
            }
            return output;
        }));
    }

    public static @NonNull List<CompletableFuture<List<BigInteger>>> start(final BigInteger start, final BigInteger end) {
        List<CompletableFuture<List<BigInteger>>> outputs = new ArrayList<>();
        BigInteger max = start;
        while (true) {
            if (max.add(HUNDRED_THOUSANDS).compareTo(end) < 0) {
                work(outputs, max, max.add(HUNDRED_THOUSANDS));
                max = max.add(HUNDRED_THOUSANDS);
            } else {
                work(outputs, max, end);
                break;
            }
        }

        return outputs;
    }

    private static void work(@NonNull List<CompletableFuture<List<Long>>> outputs, final long start, final long end) {
        outputs.add(CompletableFuture.supplyAsync(() -> {
            List<Long> output = new ArrayList<>();
            for (long i = start; i <= end; i+=2){
                boolean ok = i != 1;
                for (long j = 3; j * j <= i; j+=2) {
                    if (i%j == 0 && i != j) {
                        ok = false;
                        break;
                    }
                }
                if (ok){
                    output.add(i);
                }
            }
            return output;
        }));
    }

    public static @NonNull List<CompletableFuture<List<Long>>> start(long start, long end) {
        List<CompletableFuture<List<Long>>> outputs = new ArrayList<>();
        long max = start;
        while (true) {
            if (max + 1000 < end) {
                work(outputs, max, max + 1000);
                max += 1000;
            } else {
                work(outputs, max, end);
                break;
            }
        }

        return outputs;
    }
}
