import lombok.NonNull;
import org.junit.jupiter.api.Test;
import ru.example.Main;

import java.io.*;
import java.math.BigInteger;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    @Test
    void checkFindingPrimes() {
        var outputs = Main.start(toBigInteger(11), toBigInteger(40));
        Set<BigInteger> numbers = new HashSet<>();
        Set<BigInteger> expected = toBigIntegersSet(Set.of(11L, 13L, 17L, 19L, 23L, 29L, 31L, 37L));

        for (CompletableFuture<List<BigInteger>> output : outputs) {
            numbers.addAll(output.join());
        }

        assertTrue(numbers.containsAll(expected));
        assertTrue(expected.containsAll(numbers));
    }

    private static BigInteger toBigInteger(long number) {
        return BigInteger.valueOf(number);
    }

    private static Set<BigInteger> toBigIntegersSet(@NonNull Set<Long> numbers) {
        return numbers.stream().map(l -> new BigInteger(l.toString())).collect(Collectors.toUnmodifiableSet());
    }

    @Test
    void checkFindingFactors() throws IOException {
        PipedInputStream in = new PipedInputStream();
        PipedOutputStream out = new PipedOutputStream(in);
        PipedInputStream factorsIn = new PipedInputStream();
        PipedOutputStream factorsOut = new PipedOutputStream(factorsIn);
        PrintStream factorsOutput = new PrintStream(factorsOut);
        Scanner factorsScanner = new Scanner(factorsIn);

        out.write("41151741145572\n".getBytes());

        Main.findFactors(new Scanner(in), factorsOutput);

        for (short i = 0; i < 3; i++) {
            factorsScanner.nextLine();
        }
        assertEquals("149100511397 3 23 2 2 ", factorsScanner.nextLine());
    }
}
