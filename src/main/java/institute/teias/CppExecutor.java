//package hads;
//
//import java.io.BufferedReader;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.io.OutputStreamWriter;
//import java.io.Writer;
//import java.util.ArrayList;
//import java.util.Collection;
//import java.util.List;
//import java.util.Scanner;
//
//import javax.annotation.Nullable;
//
//import java.util.Random;
//import de.learnlib.api.oracle.EquivalenceOracle.MealyEquivalenceOracle;
//import de.learnlib.api.oracle.MembershipOracle.MealyMembershipOracle;
//import de.learnlib.api.query.DefaultQuery;
//import net.automatalib.automata.transducers.MealyMachine;
//import net.automatalib.serialization.dot.GraphDOT;
//import net.automatalib.words.Alphabet;
//import net.automatalib.words.Word;
//import net.automatalib.words.WordBuilder;
//import com.google.common.base.Stopwatch;
//import java.util.concurrent.TimeUnit;
///**
// * Implements the Lee & Yannakakis suffixes by invoking an external program.
// * Because of this indirection to an external program, the findCounterexample
// * method might throw a RuntimeException. Sorry for the hard-coded path to the
// * executable!
// *
// * @param <O> is the output alphabet. (So a query will have type Word<String,
// *            Word<O>>.)
// */
//public class YannakakisEQOracle implements MealyEquivalenceOracle.MealyEquivalenceOracle<String, String> {
//    //	private final MembershipOracle<String, Word<O>> sulOracle;
////	private final List<Alphabet<String>> alphabets;
//    private final MealyMembershipOracle<String, String> sulOracle;
//    private final Alphabet<String> alphabet;
//    private long inputCount = 0;
//    private final ProcessBuilder pb = new ProcessBuilder("/home/bharat/rust/automata-lib/hybrid-ads/build/main", "-p",
//            "buggy", "-m", "random", "-k", "10", "-s", "hads", "-r", "10", "-x", String.valueOf((new Random()).nextInt()));
//
//    // We buffer queries, in order to allow for parallel membership queries.
//    private int bufferSize = 1000;
//    private List<DefaultQuery<String, Word<String>>> buffer = new ArrayList<>(bufferSize);
//
//    private Process process;
//    private Writer processInput;
//    private BufferedReader processOutput;
//    private StreamGobbler errorGobbler;
//    private long input_limit;
//    private long reset_limit;
//
//    private long reset_count = 0;
//    private long input_count = 0;
//
////
////	public YannakakisEQOracle(List<Alphabet<String>> alphabets, MembershipOracle<String, Word<O>> sulOracle) throws IOException {
////		this.sulOracle = sulOracle;
//////		this.alphabets = alphabets;
////		this.alphabet = null;
////	}
//    /**
//     * @param sulOracle The membership oracle of the SUL, we need this to check the
//     *                  output on the test suite
//     * @throws IOException
//     */
//    public YannakakisEQOracle(Alphabet<String> inputAlphabet, MealyMembershipOracle<String, String> sulOracle,
//                              long inputs, long resets) {
//        this.sulOracle = sulOracle;
//        this.alphabet = inputAlphabet;
//        this.input_limit = inputs;
//
//        this.reset_limit = resets;
//        // TODO Auto-generated constructor stub
//    }
//
//    /**
//     * A small class to print all stuff to stderr. Useful as I do not want stderr
//     * and stdout of the external program to be merged, but still want to redirect
//     * stderr to java's stderr.
//     */
//    class StreamGobbler extends Thread {
//        private final InputStream stream;
//        private final String prefix;
//
//        StreamGobbler(InputStream stream, String prefix) {
//            this.stream = stream;
//            this.prefix = prefix;
//        }
//
//        public void run() {
//            try {
//                BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//                String line;
//                while ((line = reader.readLine()) != null)
//                    System.err.println(prefix + "> " + line);
//            } catch (IOException e) {
//                // It's fine if this thread crashes, nothing depends on it
//                e.printStackTrace();
//            }
//        }
//    }
//
//    /**
//     * Starts the process and creates buffered/whatnot streams for stdin stderr or
//     * the external program
//     *
//     * @throws IOException if the process could not be started (see
//     *                     ProcessBuilder.start for details).
//     */
//    private void setupProcess() throws IOException {
//        process = pb.start();
//        processInput = new OutputStreamWriter(process.getOutputStream());
//        processOutput = new BufferedReader(new InputStreamReader(process.getInputStream()));
//        errorGobbler = new StreamGobbler(process.getErrorStream(), "ERROR> main");
//        errorGobbler.start();
//    }
//
//    /**
//     * I thought this might be a good idea, but I'm not a native Java speaker, so
//     * maybe it's not needed.
//     */
//    private void closeAll() {
//        // Since we're closing, I think it's ok to continue in case of an exception
//        try {
//            processInput.close();
//            processOutput.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        try {
//            errorGobbler.join(10);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        process.destroy();
//        process.destroyForcibly();
//    }
//
//    /**
//     * Uses an external program to find counterexamples. The hypothesis will be
//     * written to stdin. Then the external program might do some calculations and
//     * write its test suite to stdout. This is in turn read here and fed to the SUL.
//     * If a discrepancy occurs, an counterexample is returned. If the external
//     * program exits (with value 0), then no counterexample is found, and the
//     * hypothesis is correct.
//     *
//     * This method might throw a RuntimeException if the external program crashes
//     * (which it shouldn't of course), or if the communication went wrong (for
//     * whatever IO reason).
//     */
//    @Nullable
//    @Override
//    public DefaultQuery<String, Word<String>> findCounterExample(MealyMachine<?, String, ?, String> machine,
//                                                                 Collection<? extends String> inputs) {
//        // we're ignoring the external alphabet, only our own are used!
//        return this.findCounterExampleImpl(machine, inputs);
//    }
//
//    private DefaultQuery<String, Word<String>> findCounterExampleImpl(MealyMachine<?, String, ?, String> machine,
//                                                                      Collection<? extends String> inputs) {
//        Stopwatch stopwatch = Stopwatch.createStarted();
//        try {
//            try {
//                setupProcess();
//            } catch (IOException e) {
//                throw new RuntimeException("Unable to start the external program: " + e);
//            }
//
//            long resetCount = 0;
//            try {
//                // Write the hypothesis to stdin of the external program
//                GraphDOT.write(machine, inputs, processInput);
//                processInput.flush();
//
//                // Read every line outputted on stdout.
//                // We buffer the queries, so that a parallel membership query can be applied
//                String line;
//                while ((line = processOutput.readLine()) != null) {
//                    // Read every string of the line, this will be a symbol of the input sequence.
//                    WordBuilder<String> wb = new WordBuilder<>();
//                    @SuppressWarnings("resource")
//                    Scanner s = new Scanner(line);
//                    while (s.hasNext()) {
//                        wb.add(s.next());
//                    }
//
//                    // Convert to a word and test on the SUL
//                    Word<String> test = wb.toWord();
//                    DefaultQuery<String, Word<String>> query = new DefaultQuery<>(test);
//                    buffer.add(query);
//
//                    // If the buffer is filled, we can perform the checks (possibly in parallel)
//                    if (buffer.size() >= bufferSize) {
//                        DefaultQuery<String, Word<String>> r = checkAndEmptyBuffer(machine);
//                        if (r != null) {
//                            closeAll();
//                            s.close();
//                            return r;
//                        }
//                    }
//                    if (resetCount >= this.reset_limit || this.inputCount >= this.input_limit) {
//                        break;
//                    }
//                    if (stopwatch.elapsed(TimeUnit.MINUTES) >= 20){
//                        break;
//                    }
//                }
//            } catch (IOException e) {
//                throw new RuntimeException("Unable to communicate with the external program: " + e);
//            }
//            closeAll();
//
//            // At this point, the external program closed its stream, so it should have
//            // exited.
////		if (process.isAlive()) {
////			System.err.println("ERROR> log> No counterexample but process stream still active!");
////			closeAll();
////			throw new RuntimeException("No counterexample but process stream still active!");
////		}
//
//            // If the program exited with a non-zero value, something went wrong (for
//            // example a segfault)!
//            int ret = process.exitValue();
////		if (ret != 0) {
////			System.err.println("ERROR> log> Something went wrong with the process: return value = " + ret);
////			closeAll();
////			throw new RuntimeException("Something went wrong with the process: return value = " + ret);
////		}
//
//            // Here, the program exited normally, without counterexample, so we may return
//            // null.
//            return null;
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    private DefaultQuery<String, Word<String>> checkAndEmptyBuffer(MealyMachine<?, String, ?, String> machine) {
//        // sulOracle.processQueries(buffer);
//        DefaultQuery<String, Word<String>> r = inspectBuffer(machine);
//        buffer.clear();
//        return r;
//    }
//
//    private DefaultQuery<String, Word<String>> inspectBuffer(MealyMachine<?, String, ?, String> machine) {
//        for (DefaultQuery<String, Word<String>> query : buffer) {
//            Word<String> o1 = machine.computeOutput(query.getInput());
//            Word<String> o2 = sulOracle.answerQuery(query.getInput());
//            // this.inputCount += 1;
//            // this.reset_count += 1;
//            // this.input_count += query.getInput().length();
//            // System.out.println("Query sent to the SUL: " + query.getInput());
//            // System.out.println("Response recvd: " + o2);
//            assert o1 != null;
//            assert o2 != null;
//
//
//            // If equal => no counterexample :(
//            if (!o1.equals(o2)){
//                query.answer(o2);
//                System.out.println("CE is " + query);
//                return query;
//            }}
//        return null;
//    }
//
//    // public long reportInputs() {
//    // 	return this.input_count;
//    // }
//
//    // public long reportResets() {
//    // 	return this.reset_count;
//    // }
//
//}
