import org.junit.jupiter.api.Test;
import ru.example.Main;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    @Test
    void check() {
        var outputs = Main.start(11, 40);
        Set<Integer> numbers = new HashSet<>();
        Set<Integer> expected = Set.of(11, 13, 17, 19, 23, 29, 31, 37);

        for (CompletableFuture<List<Integer>> output : outputs) {
            numbers.addAll(output.join());
        }

        assertTrue(numbers.containsAll(expected));
        assertTrue(expected.containsAll(numbers));
    }
}
