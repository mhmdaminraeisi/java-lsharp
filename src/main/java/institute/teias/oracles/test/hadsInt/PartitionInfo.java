package institute.teias.oracles.test.hadsInt;

import institute.teias.utils.Pair;
import lombok.RequiredArgsConstructor;
import net.automatalib.automaton.transducer.CompactMealy;

import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class PartitionInfo<O> {
    private final HashMap<O, HashMap<Integer, HashSet<Integer>>> splitMap;

    // A partition is injective iff all source states which have the same output do not have the same destination state.
    private boolean isInjective() {
        return this.splitMap.values().stream()
                .flatMap(d -> d.values().stream())
                .allMatch(s -> s.size() == 1);
    }
    private boolean mergesAllStates() {
        return this.splitMap.values().stream()
                .flatMap(d -> d.keySet().stream())
                .collect(Collectors.toSet())
                .size() == 1;
    }
    // A partition is separating iff the set of source states produce at least two distinct outputs.
    private boolean isSeparating() {
        return this.splitMap.size() > 1;
    }

    private boolean isUseless() {
        boolean useless = true;

        for (HashMap<Integer, HashSet<Integer>> destinationSources : this.splitMap.values()) {
            boolean allDestinationSourcePairsSame = true;
            for (Map.Entry<Integer, HashSet<Integer>> e : destinationSources.entrySet()) {
                if (e.getValue().size() != 1 || !e.getValue().iterator().next().equals(e.getKey())) {
                    allDestinationSourcePairsSame = false;
                    break;
                }
            }
            if (!allDestinationSourcePairsSame) {
                useless = false;
                break;
            }
        }
        if (this.splitMap.size() > 1) useless = false;
        return useless;
    }

    public SplitType iType() {
        if (this.isUseless()) return SplitType.Useless;

        if (this.isSeparating()) {
            if (this.isInjective()) return SplitType.SepInj;
            return SplitType.SepNonInj;
        }
        if (this.isInjective()) return SplitType.XferInj;
        if (!this.mergesAllStates()) return SplitType.XferNonInj;

        return SplitType.Useless;
    }

    public boolean nonInjSepInput() {
        return this.iType().equals(SplitType.SepNonInj);
    }

    public HashSet<Integer> allDestinations() {
        return this.splitMap.values().stream()
                .flatMap(d -> d.keySet().stream())
                .collect(Collectors.toCollection(HashSet::new));
    }

    public static <I, O> boolean isInputsSepInj(CompactMealy<I, O> fsm, List<I> inputs, List<Integer> rBlocks) {
        HashSet<Pair<List<O>, Integer>> oldOutsDest = new HashSet<>();
        HashSet<List<O>> numOuts = new HashSet<>();

        for (Integer s : rBlocks) {
            List<O> outputs = fsm.computeStateOutput(s, inputs).asList();
            Integer dest = fsm.getSuccessor(s, inputs);
            Pair<List<O>, Integer> pair = new Pair<>(outputs, dest);
            if (oldOutsDest.contains(pair)) return false;
            oldOutsDest.add(pair);
            numOuts.add(outputs);
        }
        return numOuts.size() > 1;
    }

    public static <I, O> PartitionInfo<O> analyseInputSymbol(CompactMealy<I, O> fsm, I input, List<Integer> block) {
        HashMap<O, HashMap<Integer, HashSet<Integer>>> outputDestMap = new HashMap<>();
        for (Integer s : block) {
            Integer dest = fsm.getSuccessor(s, input);
            O output = fsm.getOutput(s, input);
            if (!outputDestMap.containsKey(output)) {
                outputDestMap.put(output, new HashMap<>());
            }
            if (!outputDestMap.get(output).containsKey(dest)) {
                outputDestMap.get(output).put(dest, new HashSet<>());
            }
            outputDestMap.get(output).get(dest).add(s);
        }
        return new PartitionInfo<O>(outputDestMap);
    }
}
