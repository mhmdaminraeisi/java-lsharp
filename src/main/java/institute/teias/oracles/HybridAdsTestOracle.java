package institute.teias.oracles;

import institute.teias.utils.Pair;
import net.automatalib.automaton.transducer.CompactMealy;
import net.automatalib.serialization.dot.GraphDOT;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class HybridAdsTestOracle<I, O> extends TestOracle<I, O> {
    private int resets = 0;
    private int inputSymbols = 0;

    private final ProcessBuilder pb = new ProcessBuilder("/home/amin/projects/teias/java-lsharp/hybrid-ads/build/main",
            "-p", "buggy", "-m", "random", "-k", "10", "-s", "hads", "-r", "10", "-x", String.valueOf((new Random()).nextInt()));

    // We buffer queries, in order to allow for parallel membership queries.
    private final int BufferSize = 10;
    private final List<List<I>> buffer = new ArrayList<>(BufferSize);

    private Process process;
    private Writer processInput;
    private BufferedReader processOutput;

    public HybridAdsTestOracle(CompactMealy<I, O> reference) {
        super(reference);
    }

    private void setupProcess() throws IOException {
        process = pb.start();
        processInput = new OutputStreamWriter(process.getOutputStream());
        processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
    }

    private void closeAll() throws IOException {
        processInput.close();
        processOutput.close();
        process.destroy();
        process.destroyForcibly();
    }

    @Override
    public Pair<List<I>, List<O>> findCounterExample(CompactMealy<I, O> hypothesis) {
        long startTime = System.currentTimeMillis();

        try {
            try {
                setupProcess();
            } catch (IOException e) {
                throw new RuntimeException("Unable to start the external program: " + e);
            }

            try {
                // Write the hypothesis to stdin of the external program
                GraphDOT.write(hypothesis, hypothesis.getInputAlphabet(), processInput);
                processInput.flush();

                // Read every line outputted on stdout.
                // We buffer the queries, so that a parallel membership query can be applied
                String line;
                while ((line = processOutput.readLine()) != null) {
                    // Read every string of the line, this will be a symbol of the input sequence.
                    List<I> testSuit = new ArrayList<>();
                    Scanner s = new Scanner(line);
                    while (s.hasNext()) {
                        testSuit.add((I) s.next());
                    }
                    buffer.add(testSuit);

                    // If the buffer is filled, we can perform the checks (possibly in parallel)
                    if (buffer.size() >= BufferSize) {

                        for (List<I> word : buffer) {
                            System.out.println(word);
                        }
                        return null;
//                        DefaultQuery<String, Word<String>> r = checkAndEmptyBuffer(machine);
//                        if (r != null) {
//                            closeAll();
//                            s.close();
//                            return r;
//                        }
                    }

                    long endTime = System.currentTimeMillis();
                    if (endTime - startTime >= 20 * 60 * 1000){ // 20 minutes
                        break;
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException("Unable to communicate with the external program: " + e);
            }
            closeAll();

            return null;
        } catch (Exception e) {
            return null;
        }
    }

    private Pair<List<I>, List<O>> checkAndClearBuffer(CompactMealy<I, O> hypothesis) {
        for (List<I> test : buffer) {
            this.resets += 1;

        }
    }

    @Override
    public int getSymbolsCount() {
        return this.inputSymbols;
    }

    @Override
    public int getResetsCount() {
        return this.resets;
    }
}
