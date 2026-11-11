package ru.example;

import lombok.NonNull;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    private final static BigInteger THOUSAND = new BigInteger("1000");
    private final static BigInteger THREE = new BigInteger("3");
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
            if (max.add(THOUSAND).compareTo(end) < 0) {
                work(outputs, max, max.add(THOUSAND));
                max = max.add(THOUSAND);
            } else {
                work(outputs, max, end);
                break;
            }
        }

        return outputs;
    }
}
