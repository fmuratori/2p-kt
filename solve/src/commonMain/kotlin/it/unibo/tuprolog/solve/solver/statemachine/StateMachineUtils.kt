package it.unibo.tuprolog.solve.solver.statemachine

import it.unibo.tuprolog.solve.Solution
import it.unibo.tuprolog.solve.Solve
import it.unibo.tuprolog.solve.solver.statemachine.state.StateEnd
import kotlinx.coroutines.CoroutineScope

/**
 * Utils object to help implementing State behaviour
 *
 * @author Enrico
 */
internal object StateMachineUtils {

    /** Converts receiver solution to corresponding [StateEnd] */
    fun Solution.toStateEnd(solveRequest: Solve.Request, executionStrategy: CoroutineScope): StateEnd = when (this) {
        is Solution.Yes -> StateEnd.True(solveRequest, executionStrategy)
        is Solution.No -> StateEnd.False(solveRequest, executionStrategy)
        is Solution.Halt -> StateEnd.Halt(solveRequest, executionStrategy, this.exception)
    }

}
