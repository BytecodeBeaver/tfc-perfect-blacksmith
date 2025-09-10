package com.bytecodebeaver.tfcperfectblacksmith;

import com.mojang.datafixers.util.Pair;
import com.bytecodebeaver.tfcperfectblacksmith.mixin.ForgeRuleAccessor;
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;

import java.util.*;

public class AnvilWorkStepSolver {
    private static int MIN_SUM = -100;
    private static int MAX_SUM = 150;

    private ForgeStep getBiggestFittingStep(int value) {
        ForgeStep biggest = ForgeStep.HIT_HARD;
        for(ForgeStep step : ForgeStep.VALUES) {
            if(value - biggest.step() == 0) {
                // Exact match - nothing else will fit
                return biggest;
            }

            if(step.step() > biggest.step()) {
                biggest = step;
            }
        }

        return biggest;
    }

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
    private List<ForgeStep> solveInternal(int target) {
        // TODO: Can cache it

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
                if (nextSum >= MIN_SUM && nextSum <= MAX_SUM && !visited.contains(nextSum)) {
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

    /**
     * Finds anvil target value combination of ForgeStep
     * @param target
     * @param current
     * @param lastSteps
     * @return List of ForgeSteps to reach given target, including last operations
     */
    public List<ForgeStep> solve(int target, int current, ForgeRule[] lastSteps) {
        ForgeRuleAccessor[] forgeRule = (ForgeRuleAccessor[]) (Object)lastSteps;

        var lastStepsSum = Arrays.stream(forgeRule).mapToInt(forgeRuleAccessor -> forgeRuleAccessor.getType().step()).sum();

        // Solve with ommiting part that must be present
        List<ForgeStep> optimalSteps = solveInternal(
                target - current - lastStepsSum
        );

        if(optimalSteps == null) return new ArrayList<>();

        // Add fixed steps
        for(int i = forgeRule.length - 1;i>=0;i--) {
            optimalSteps.add(forgeRule[i].getType());
        }

        return optimalSteps;
    }
}
