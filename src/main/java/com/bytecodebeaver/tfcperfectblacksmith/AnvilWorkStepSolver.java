package com.bytecodebeaver.tfcperfectblacksmith;

import com.mojang.datafixers.util.Pair;
import com.bytecodebeaver.tfcperfectblacksmith.interfaces.IForgeRuleAccessor;
import com.bytecodebeaver.tfcperfectblacksmith.mixin.ForgeRuleAccessorMixin;
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;

import java.util.*;

public class AnvilWorkStepSolver {
    private static int MAX_SUM = 150;

    public List<ForgeStep> solve(int target, ForgeRule[] lastSteps) {
        return solve(target, 0, lastSteps);
    }

    private ForgeStep getFromStepValue(int value) {
        return Arrays.stream(ForgeStep.VALUES)
                .filter(forgeStep -> forgeStep.step() == value)
                .findFirst()
                .orElse(null);
    }

    /**
     *
     * @param numbers
     * @param target
     * @return List of ForgeSteps to reach given target
     */
    private List<ForgeStep> solveInternal(int target, int currentWork) {
        // TODO: Can cache it

        // Handles a case where an item is worked, so we can safely subtract more without worrying that we will break it
        int minSum = -currentWork;

        // BFS queue: each element is a pair of (current sum, combination list)
        Queue<Pair<Integer, List<ForgeStep>>> queue = new LinkedList<>();
        queue.add(new Pair<Integer, List<ForgeStep>>(0, new ArrayList<>()));

        // Visited set to avoid revisiting same sum
        Set<Integer> visited = new HashSet<>();
        visited.add(0);

        while (!queue.isEmpty()) {
            Pair<Integer, List<ForgeStep>> current = queue.poll();
            int currentSum = current.getFirst();
            List<ForgeStep> combination = current.getSecond();

            // Check if target is reached
            if (currentSum == target) {
                return combination;
            }

            for (ForgeStep forgeStep : ForgeStep.VALUES) {
                int nextSum = currentSum + forgeStep.step();

                // Only consider sums within bounds and not visited
                if (nextSum >= minSum && nextSum <= MAX_SUM && !visited.contains(nextSum)) {
                    visited.add(nextSum);
                    List<ForgeStep> nextCombination = new ArrayList<>(combination);
                    nextCombination.add(forgeStep);
                    queue.add(new Pair<>(nextSum, nextCombination));
                }
            }
        }

        // Target not reachable within constraints
        return null;
    }

    private ForgeRule[] sortForgeRules(ForgeRule[] originalRules) {
        ForgeRule[] sortedSteps = new ForgeRule[3];

        // Order values are descending

        return sortedSteps;
    }

    /**
     * Finds anvil target value combination of ForgeStep
     * @param target
     * @param current
     * @param lastSteps
     * @return List of ForgeSteps to reach given target, including last operations
     */
    public List<ForgeStep> solve(int target, int current, ForgeRule[] lastSteps) {
        ForgeRule[] orderedRules = Arrays.stream(lastSteps).sorted(
                Comparator.comparingInt(ForgeRule::overlayY)
        ).toArray(ForgeRule[]::new);

        IForgeRuleAccessor[] forgeRule = (IForgeRuleAccessor[]) (Object)orderedRules;

        var lastStepsSum = Arrays.stream(forgeRule).mapToInt(forgeRuleAccessor -> forgeRuleAccessor.getType().step()).sum();

        // Solve with ommiting part that must be present
        List<ForgeStep> optimalSteps = solveInternal(
                target - current - lastStepsSum
        , current);

        if(optimalSteps == null) return new ArrayList<>();

        // Add fixed steps
        for(int i = forgeRule.length - 1;i>=0;i--) {
            optimalSteps.add(forgeRule[i].getType());
        }

        return optimalSteps;
    }
}
