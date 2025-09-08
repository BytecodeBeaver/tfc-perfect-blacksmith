package com.bytecodebeaver.tfcperfectblacksmith;

import net.dries007.tfc.common.capabilities.forge.ForgeRule;
import net.dries007.tfc.common.capabilities.forge.ForgeStep;
import net.dries007.tfc.common.capabilities.forge.ForgeSteps;

import java.util.LinkedList;
import java.util.Queue;

public class AnvilWorkStepSolver {
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

    public Queue<ForgeStep> solve(int target, ForgeRule[] lastSteps) {
        return solve(target, 0, lastSteps);
    }

    public Queue<ForgeStep> solve(int target, int current, ForgeRule[] lastSteps) {
        Queue<ForgeStep> stepsQueue = new LinkedList<>();
/*
        lastSteps[0].

        // Hit light can be any hit - Find which one is most optimal
        int offsettedTarget = target - lastSteps.last().step() - lastSteps.secondLast().step() - lastSteps.secondLast().step() - current;

        // Reduce offsetedTarget until biggest item no longer fit, then use lower value one adn repeat
        while (offsettedTarget != 0) {
            ForgeStep nextStep = getBiggestFittingStep(offsettedTarget);
            stepsQueue.add(nextStep);
            offsettedTarget -= nextStep.step();
        }

        // Add last steps
        stepsQueue.add(lastSteps.thirdLast());
        stepsQueue.add(lastSteps.secondLast());
        stepsQueue.add(lastSteps.last());
*/
        return stepsQueue;
    }
}
