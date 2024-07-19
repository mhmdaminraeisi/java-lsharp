package institute.teias.oracles.test;

import de.learnlib.filter.statistic.sul.ResetCounterSUL;
import de.learnlib.filter.statistic.sul.SymbolCounterSUL;
import de.learnlib.oracle.EquivalenceOracle;
import de.learnlib.oracle.equivalence.mealy.RandomWalkEQOracle;
import de.learnlib.query.DefaultQuery;
import de.learnlib.statistic.StatisticSUL;
import de.learnlib.sul.SUL;
import institute.teias.oracles.Oracle;
import institute.teias.utils.Pair;
import net.automatalib.automaton.transducer.CompactMealy;
import net.automatalib.automaton.transducer.MealyMachine;
import net.automatalib.word.Word;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.List;
import java.util.Random;

public class TestOracle<I, O> extends Oracle<I, O> {
    private final EquivalenceOracle<MealyMachine<?, I, ?, O>, I, Word<O>> equivalenceOracle;
    private final StatisticSUL<I, O> symbolCounterSUL;
    private final StatisticSUL<I, O> resetCounterSUL;

    public TestOracle(SUL<I, O> sul) {
        this.symbolCounterSUL = new SymbolCounterSUL<>("symbolCounter", sul);
        this.resetCounterSUL = new ResetCounterSUL<>("resetCounter", this.symbolCounterSUL);

        // TODO
        this.equivalenceOracle = new RandomWalkEQOracle<>(
                this.resetCounterSUL,
                0.03,
                10000,
                true,
                new Random(System.currentTimeMillis())
        );
//        this.equivalenceOracle = new RandomWordsEQOracle<>(
//                new SULOracle<>(this.resetCounterSUL),
//                2,
//                10000,
//                30000,
//                new Random(System.currentTimeMillis())
//        );
    }

    @Override
    public String getSymbolsCount() {
        return this.symbolCounterSUL.getStatisticalData().getSummary();
    }

    @Override
    public String getResetsCount() {
        return this.resetCounterSUL.getStatisticalData().getSummary();
    }

    public Pair<List<I>, List<O>> findCounterExample(CompactMealy<I, O> hypothesis) {
        @Nullable DefaultQuery<I, Word<O>> ce = this.equivalenceOracle.findCounterExample(hypothesis, hypothesis.getInputAlphabet());
        if (ce == null) return null;
        return new Pair<>(ce.getInput().asList(), ce.getOutput().asList());
    }
}