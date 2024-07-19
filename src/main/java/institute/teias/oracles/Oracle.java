package institute.teias.oracles;

import de.learnlib.filter.statistic.sul.ResetCounterSUL;
import de.learnlib.filter.statistic.sul.SymbolCounterSUL;
import de.learnlib.statistic.StatisticSUL;
import de.learnlib.sul.SUL;

public abstract class Oracle<I, O> {
    public abstract String getSymbolsCount();
    public abstract String getResetsCount();
}
