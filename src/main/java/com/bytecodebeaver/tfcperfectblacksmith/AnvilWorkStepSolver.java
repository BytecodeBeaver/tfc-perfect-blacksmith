package com.bytecodebeaver.tfcperfectblacksmith;

import com.mojang.datafixers.util.Pair;
import com.bytecodebeaver.tfcperfectblacksmith.mixin.ForgeRuleAccessor;
import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;

import java.util.*;

public class AnvilWorkStepSolver {
    private static int MIN_SUM = 0;
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
        System.out.println("Before <3");
        if(lastSteps.length < 3) return new ArrayList<>();

        ForgeRuleAccessor[] forgeRule = (ForgeRuleAccessor[]) (Object)lastSteps;

        var last = forgeRule[0].getType();
        var secondLast = forgeRule[1].getType();
        var thirdLast = forgeRule[2].getType();

        // Solve with ommiting part that must be present
        List<ForgeStep> optimalSteps = solveInternal(
                target - current - last.step() - secondLast.step() - thirdLast.step()
        );

        if(optimalSteps == null) return new ArrayList<>();

        // Add fixed steps
        optimalSteps.add(thirdLast);
        optimalSteps.add(secondLast);
        optimalSteps.add(last);

        return optimalSteps;
    }
    /*
    public Queue<ForgeStep> solve(int target, int current, ForgeRule[] lastSteps) {
        Queue<ForgeStep> stepsQueue = new LinkedList<>();

        if(lastSteps.length < 3) return new LinkedList();

        ForgeRuleAccessor[] forgeRule = (ForgeRuleAccessor[]) (Object)lastSteps;

        var last = forgeRule[3].getType();
        var secondLast = forgeRule[2].getType();
        var thirdLast = forgeRule[1].getType();

        // Hit light can be any hit - Find which one is most optimal
        int offsettedTarget = target - last.step() - secondLast.step() - thirdLast.step() - current;

        // Reduce offsetedTarget until biggest item no longer fit, then use lower value one adn repeat
        while (offsettedTarget != 0) {
            ForgeStep nextStep = getBiggestFittingStep(offsettedTarget);
            stepsQueue.add(nextStep);
            offsettedTarget -= nextStep.step();
        }

        // Add last steps
        stepsQueue.add(thirdLast);
        stepsQueue.add(secondLast);
        stepsQueue.add(last);

        return stepsQueue;
    }
     */
}
