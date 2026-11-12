package ru.example;

import lombok.NonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    private final static BigInteger THREE = new BigInteger("3");
    private final static BigInteger HUNDRED_THOUSANDS = new BigInteger("100000");
    static void main() {
        System.out.println("Enter start and end of searching separated by spaces.");

        Scanner scn = new Scanner(System.in);

        String input = scn.nextLine();
        BigInteger start = new BigInteger(input.split(" ")[0]);
        BigInteger end = new BigInteger(input.split(" ")[1]);

        if (start.compareTo(end) > 0) throw new IllegalArgumentException("start cannot be > end");
        if (start.compareTo(BigInteger.ZERO) < 0) throw new IllegalArgumentException("start or end cannot be < 0");
        if (start.mod(BigInteger.TWO).equals(BigInteger.ZERO)) start = start.add(BigInteger.ONE);
        if (start.equals(BigInteger.ONE)) start = start.add(BigInteger.TWO);

        if (start.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) < 0 && end.compareTo(new BigInteger(String.valueOf(Long.MAX_VALUE))) < 0) {
            System.out.println("Using Long mode");
            System.out.println("Starting!");
            long startMs = System.currentTimeMillis();

            var outputs = start(Long.parseLong(String.valueOf(start)), Long.parseLong(String.valueOf(end)));

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

            for (CompletableFuture<List<BigInteger>> output : outputs) {
                for (BigInteger i : output.join()) {
                    System.out.println(i);
                }
            }

            System.out.println("The search took " + (System.currentTimeMillis() - startMs) + " ms");
        }
    }

    private static void work(@NonNull List<CompletableFuture<List<BigInteger>>> outputs, final BigInteger start, final BigInteger end) {
        outputs.add(CompletableFuture.supplyAsync(() -> {
            List<BigInteger> output = new ArrayList<>();
            for (BigInteger i = start; i.compareTo(end) < 1; i = i.add(BigInteger.TWO)) {
                boolean ok = true;
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
                boolean ok = true;
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
