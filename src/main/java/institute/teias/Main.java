package institute.teias;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import institute.teias.io.FileManager;
import institute.teias.learner.LSharp;
import institute.teias.learner.Rule2;
import institute.teias.learner.Rule3;
import institute.teias.oracles.*;
import institute.teias.oracles.perfect.PerfectTestOracle;
import institute.teias.utils.Pair;
import net.automatalib.automaton.transducer.CompactMealy;

public class Main {

    public static void main(String[] args) throws Exception {
        String fileName = null;
        for (String arg : args) {
            if (arg.startsWith("-f=")) fileName = arg.substring(3);
        }
        if (fileName == null) {
            throw new IllegalAccessException("File name must be specified with -f arg.");
        }

        String folderPath = "src/main/resources/experimentModels";
        for (String arg : args) {
            if (arg.startsWith("-fo=")) folderPath = arg.substring(4);
        }

        String filePath = folderPath + "/" + fileName;
        CompactMealy<String, String> targetMealy = FileManager.loadMealyFromDotFile(new File(filePath));
        Rule2 rule2 = Rule2.Ads;
        Rule3 rule3 = Rule3.Ads;
        OutputOracle<String, String> outputOracle = new OutputOracle<>(targetMealy);
        TestOracle<String, String> perfectOracle = new PerfectTestOracle<>(targetMealy);
        int seed = (new Random()).nextInt();
        TestOracle<String, String> testOracle = new HybridAdsTestOracle<>(targetMealy, seed);
        HashSet<String> alphabet = new HashSet<>(targetMealy.getInputAlphabet());

        for (String arg : args) {
            if (arg.startsWith("-r2=")) {
                String ruleName = arg.substring(4);
                rule2 = switch (ruleName) {
                    case "nothing" -> Rule2.Nothing;
                    case "sep_seq" -> Rule2.SepSeq;
                    case "ads" -> Rule2.Ads;
                    default -> throw new IllegalAccessException("-r2 value must be one of [nothing, sep_seq, ads].");
                };
            }
            if (arg.startsWith("-r3=")) {
                String ruleName = arg.substring(4);
                rule3 = switch (ruleName) {
                    case "sep_seq" -> Rule3.SepSeq;
                    case "ads" -> Rule3.Ads;
                    default -> throw new IllegalAccessException("-r3 value must be one of [sep_seq, ads].");
                };
            }
            if (arg.startsWith("-eq=")) {
                String oracleName = arg.substring(4);
                testOracle = switch (oracleName) {
                    case "hads" -> new HybridAdsTestOracle<>(targetMealy, seed);
                    case "perfect" -> new PerfectTestOracle<>(targetMealy);
                    case "rand_walk" -> new RandomWalkTestOracle<>(targetMealy);
                    case "rand_words" -> new RandomWordsTestOracle<>(targetMealy);
                    default ->
                            throw new IllegalAccessException("-eq value must be one of [hads, perfect, rand_walk, rand_words].");
                };
            }
        }

        LSharp<String, String> lSharp = new LSharp<>(outputOracle, testOracle, perfectOracle, alphabet, rule2, rule3);
        boolean isSuccessful = lSharp.learnMealy().learned();
        System.out.println("Learn symbols count: " + outputOracle.getSymbolsCount());
        System.out.println("Learn resets count: " + outputOracle.getResetsCount());
        System.out.println("Test symbols count: " + testOracle.getSymbolsCount());
        System.out.println("Test resets count: " + testOracle.getResetsCount());
        System.out.println("Succeed: " + isSuccessful);
    }
}
