package institute.teias;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.learnlib.driver.simulator.MealySimulatorSUL;
import de.learnlib.sul.SUL;
import institute.teias.io.FileManager;
import institute.teias.learner.LSharp;
import institute.teias.oracles.OutputOracle;
import institute.teias.oracles.TestOracle;
import net.automatalib.automaton.transducer.CompactMealy;

public class Main {

    public static void main(String[] args) throws Exception {

//        CompactMealy<String, String> targetMealy = FileManager.loadMealyFromDotFile(new File("src/main/resources/1.dot"));

        String folderName = "src/main/resources/experimentModels";
        File folder = new File(folderName);
        String[] files = folder.list();
        if (files != null) {
            for (String fileName : files) {
                CompactMealy<String, String> targetMealy = FileManager.loadMealyFromDotFile(new File(folderName + "/" + fileName));

                SUL<String, String> sul = new MealySimulatorSUL<>(targetMealy);
                OutputOracle<String, String> outputOracle = new OutputOracle<>(sul);
                TestOracle<String, String> testOracle = new TestOracle<>(sul);

                HashSet<String> alphabet = new HashSet<>(targetMealy.getInputAlphabet());
                LSharp<String, String> lsharp = new LSharp<>(outputOracle, testOracle, alphabet, false);
                CompactMealy<String, String> fsm = lsharp.learnMealy();
                System.out.println();
                System.out.println("finished normal " + fileName + " !");
                System.out.println(outputOracle.getSymbolsCount());
                System.out.println(outputOracle.getResetsCount());
                System.out.println(testOracle.getSymbolsCount());
                System.out.println(testOracle.getResetsCount());



                SUL<String, String> sul2 = new MealySimulatorSUL<>(targetMealy);
                OutputOracle<String, String> outputOracle2 = new OutputOracle<>(sul2);
                TestOracle<String, String> testOracle2 = new TestOracle<>(sul2);

                HashSet<String> alphabet2 = new HashSet<>(targetMealy.getInputAlphabet());
                LSharp<String, String> lsharp2 = new LSharp<>(outputOracle2, testOracle2, alphabet2, true);
                CompactMealy<String, String> fsm2 = lsharp2.learnMealy();
                System.out.println();
                System.out.println("finished ADS " + fileName + " !");
                System.out.println(outputOracle2.getSymbolsCount());
                System.out.println(outputOracle2.getResetsCount());
                System.out.println(testOracle2.getSymbolsCount());
                System.out.println(testOracle2.getResetsCount());
                System.out.println("\n\n");
            }
        }
    }
}