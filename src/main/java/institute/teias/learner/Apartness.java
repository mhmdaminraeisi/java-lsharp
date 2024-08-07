package institute.teias.learner;

import institute.teias.obsTree.Node;
import institute.teias.obsTree.ObservationTree;
import institute.teias.utils.Pair;
import net.automatalib.automaton.transducer.CompactMealy;

import java.util.*;

public class Apartness {

    public static <I, O> List<I> computeWitness(
            Node<I, O> first, Node<I, O> second,
            ObservationTree<I, O> firstTree
    ) {
        Node<I, O> firstDestination = showsStatesAreApart(first, second, firstTree.getAlphabet());
        if (firstDestination == null) return null;
        return firstTree.getTransferSequence(first, firstDestination);
    }

    public static <I, O> boolean statesAreApart(
            Node<I, O> first, Node<I, O> second,
            ObservationTree<I, O> firstTree
    ) {
        return showsStatesAreApart(first, second, firstTree.getAlphabet()) != null;
    }

    private static <I, O> Node<I, O> showsStatesAreApart(Node<I, O> first, Node<I, O> second, HashSet<I> alphabet) {
        Queue<Pair<Node<I, O>, Node<I, O>>> pairs = new LinkedList<>();
        pairs.add(new Pair<>(first, second));

        while (!pairs.isEmpty()) {
            Pair<Node<I, O>, Node<I, O>> topPair = pairs.poll();
            Node<I, O> firstNode = topPair.first();
            Node<I, O> secondNode = topPair.second();

            for (I input : alphabet) {
                O firstOutput = firstNode.getOutput(input);
                O secondOutput = secondNode.getOutput(input);
                if (firstOutput != null && secondOutput != null) {
                    if (!firstOutput.equals(secondOutput)) {
                        return firstNode.getSuccessor(input);
                    }
                    pairs.add(new Pair<>(firstNode.getSuccessor(input), secondNode.getSuccessor(input)));
                }
            }
        }

        return null;
    }

    public static <I, O> List<I> computeWitnessInTreeAndHypothesis(
            ObservationTree<I, O> tree,
            CompactMealy<I, O> hypothesis
    ) {
        Node<I, O> treeDestination = showsStatesAreApartInTreeAndHypothesis(tree, hypothesis);
        if (treeDestination == null) return null;
        return tree.getAccessSequence(treeDestination);
    }

    private static <I, O> Node<I, O> showsStatesAreApartInTreeAndHypothesis(
            ObservationTree<I, O> tree,
            CompactMealy<I, O> hypothesis
    ) {
        Queue<Pair<Node<I, O>, Integer>> pairs = new LinkedList<>();
        pairs.add(new Pair<>(tree.getRoot(), hypothesis.getInitialState()));

        while (!pairs.isEmpty()) {
            Pair<Node<I, O>, Integer> topPair = pairs.poll();
            Node<I, O> treeNode = topPair.first();
            Integer state = topPair.second();

            for (I input : tree.getAlphabet()) {
                O treeOutput = treeNode.getOutput(input);
                O hypothesisOutput = hypothesis.getOutput(state, input);
                if (treeOutput != null && hypothesisOutput != null) {
                    if (!treeOutput.equals(hypothesisOutput)) {
                        return treeNode.getSuccessor(input);
                    }
                    pairs.add(new Pair<>(treeNode.getSuccessor(input), hypothesis.getSuccessor(state, input)));
                }
            }
        }
        return null;
    }
}
