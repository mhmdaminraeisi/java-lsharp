package institute.teias;

import java.io.File;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import de.learnlib.driver.simulator.MealySimulatorSUL;
import de.learnlib.sul.SUL;
import institute.teias.IO.FileManager;
import institute.teias.obsTree.Node;
import institute.teias.oracles.OutputOracle;
import institute.teias.oracles.TestOracle;
import institute.teias.utils.Pair;
import net.automatalib.automaton.transducer.CompactMealy;
import org.checkerframework.checker.units.qual.A;

public class Main {

    public static void main(String[] args) throws Exception {
        CompactMealy<String, String> targetMealy = FileManager.loadMealyFromDotFile(new File("src/main/resources/0.dot"));
        CompactMealy<String, String> hypothesis = FileManager.loadMealyFromDotFile(new File("src/main/resources/00.dot"));

        SUL<String, String> sul = new MealySimulatorSUL<>(targetMealy);
        OutputOracle<String, String> outputOracle = new OutputOracle<>(sul);
        TestOracle<String, String> testOracle = new TestOracle<>(sul);


//        HashMap<String, HashSet<Integer>> map = new HashMap<>();
//        HashSet<Integer> set = new HashSet<>();
//        set.add(1);
//        set.add(2);
//        set.add(3);
//        set.add(4);
//
//        map.put("salam", set);
//        HashSet<Integer> dali = map.get("salam");
//        map.replace("salam", dali.stream().filter(s -> s % 2 == 0).collect(Collectors.toCollection(HashSet::new)));
//        System.out.println(map);

        int x = 2;
        int y = 3;
        int h = (x + y) / 2;
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(2);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);
        list.add(7);

        List<Integer> one = list.subList(0, h);
        List<Integer> two = list.subList(h, list.size());

        System.out.println(one);
        System.out.println(two);
    }
}