package ru.example;

import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CompletableFuture;

public class Main {
    static void main() {
        System.out.println("Enter start and end of searching separated by spaces.");

        Scanner scn = new Scanner(System.in);

        String input = scn.nextLine();
        long start = Long.parseLong(input.split(" ")[0]);
        long end = Long.parseLong(input.split(" ")[1]);

        if (start > end) throw new IllegalArgumentException("start cannot be > end");
        if (start < 0) throw new IllegalArgumentException("start or end cannot be < 0");
        if (start % 2 == 0) start++;
        if (start == 1) start += 2;

        System.out.println("Starting!");
        long startMs = System.currentTimeMillis();

        var outputs = start(start, end);

        for (CompletableFuture<List<Long>> output : outputs) {
            for (Long i : output.join()) {
                System.out.println(i);
            }
        }

        System.out.println("The search took " + (System.currentTimeMillis() - startMs) + " ms");
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

    public static List<CompletableFuture<List<Long>>> start(long start, long end) {
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
