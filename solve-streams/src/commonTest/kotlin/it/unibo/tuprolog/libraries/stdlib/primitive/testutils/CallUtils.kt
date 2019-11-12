package it.unibo.tuprolog.libraries.stdlib.primitive.testutils

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Truth
import it.unibo.tuprolog.core.Tuple
import it.unibo.tuprolog.dsl.theory.prolog
import it.unibo.tuprolog.libraries.stdlib.primitive.Call
import it.unibo.tuprolog.libraries.stdlib.primitive.Conjunction
import it.unibo.tuprolog.libraries.stdlib.primitive.Cut
import it.unibo.tuprolog.libraries.stdlib.primitive.Throw
import it.unibo.tuprolog.solve.DummyInstances
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabase
import it.unibo.tuprolog.solve.TestingClauseDatabases.simpleFactDatabaseNotableGoalToSolutions
import it.unibo.tuprolog.solve.changeQueriesTo
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.SystemError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.solve.halt
import it.unibo.tuprolog.solve.testutils.SolverTestUtils.createSolveRequest
import it.unibo.tuprolog.solve.yes
import kotlin.collections.listOf as ktListOf

/**
 * Utils singleton to help testing [Call]
 *
 * @author Enrico
 */
internal object CallUtils {

    private val aContext = DummyInstances.executionContext

    /**
     * Call primitive working examples, with expected responses
     *
     * Contained requests:
     * - `call(true)` **will result in** `Yes()`
     * - `call((true, true))` **will result in** `Yes()`
     * - `call('!')` **will result in** `Yes()`
     * - `call(f(A))` against [factDatabase][simpleFactDatabase]  **will result in** `Yes(A -> a)`
     * - `call(g(A))` against [factDatabase][simpleFactDatabase]  **will result in** `Yes(A -> a), Yes(A -> b)`
     * - `call(h(A))` against [factDatabase][simpleFactDatabase]  **will result in** `Yes(A -> a), Yes(A -> b), Yes(A -> c)`
     * - `call((f(A), '!'))` against [factDatabase][simpleFactDatabase]  **will result in** `Yes(A -> a)`
     * - `call((g(A), '!'))` against [factDatabase][simpleFactDatabase]  **will result in** `Yes(A -> a)`
     * - `call((h(A), '!'))` against [factDatabase][simpleFactDatabase]  **will result in** `Yes(A -> a)`
     */
    internal val requestSolutionMap by lazy {
        prolog {
            mapOf(
                    Call.functor(true).run {
                        createSolveRequest(this, primitives = mapOf(Call.descriptionPair)) to ktListOf(yes())
                    },
                    Call.functor("true" and "true").run {
                        createSolveRequest(this, primitives = mapOf(Call.descriptionPair, Conjunction.descriptionPair)) to ktListOf(yes())
                    },
                    Call.functor("!").run {
                        createSolveRequest(this, primitives = mapOf(Call.descriptionPair, Cut.descriptionPair)) to ktListOf(yes())
                    },
                    *simpleFactDatabaseNotableGoalToSolutions.map { (goal, solutionList) ->
                        Call.functor(goal).run {
                            createSolveRequest(this, database = simpleFactDatabase, primitives = mapOf(Call.descriptionPair)) to
                                    solutionList.changeQueriesTo(this)
                        }
                    }.toTypedArray(),
                    *simpleFactDatabaseNotableGoalToSolutions.map { (goal, solutionList) ->
                        Call.functor(goal and "!").run {
                            createSolveRequest(
                                    this,
                                    database = simpleFactDatabase,
                                    primitives = mapOf(*ktListOf(Call, Cut, Conjunction).map { it.descriptionPair }.toTypedArray())
                            ) to solutionList.subList(0, 1).changeQueriesTo(this)
                        }
                    }.toTypedArray()
            )
        }
    }

    /**
     * Requests that should throw errors
     *
     * Contained requests:
     *
     * - `call(X)` **will result in** `Halt()`
     * - `call((true, 1))`  **will result in** `Halt()`
     */
    internal val requestToErrorSolutionMap by lazy {
        prolog {
            mapOf(
                    Call.functor("X").run {
                        createSolveRequest(this, primitives = mapOf(Call.descriptionPair, Throw.descriptionPair)) to ktListOf(
                                halt(HaltException(context = aContext,
                                        cause = with(InstantiationError(
                                                context = aContext,
                                                extraData = varOf("X")
                                        )) {
                                            SystemError(context = aContext,
                                                    cause = this,
                                                    extraData = this.errorStruct
                                            )
                                        }
                                ))
                        )
                    },
                    Call.functor(true and 1).run {
                        createSolveRequest(this, primitives = mapOf(Call.descriptionPair, Conjunction.descriptionPair, Throw.descriptionPair)) to ktListOf(
                                halt(HaltException(context = aContext,
                                        cause = with(TypeError(context = aContext,
                                                expectedType = TypeError.Expected.CALLABLE,
                                                actualValue = Tuple.of(Truth.`true`(), Integer.of(1))
                                        )) {
                                            SystemError(context = aContext,
                                                    cause = this,
                                                    extraData = this.errorStruct
                                            )
                                        }
                                ))
                        )
                    }
            )
        }
    }

    /**
     * A request to test that [Call] limits [Cut] to have effect only inside its goal; `call/1` is said to be *opaque* (or not transparent) to cut.
     *
     * - `call(g(A), call('!'))` against [factDatabase][simpleFactDatabase]  **will result in** `Yes(A -> a), Yes(A -> b)`
     */
    internal val requestToSolutionOfCallWithCut by lazy {
        prolog {
            ktListOf(
                    *simpleFactDatabaseNotableGoalToSolutions.map { (goal, solutionList) ->
                        Call.functor(goal and Call.functor("!")).run {
                            createSolveRequest(
                                    this,
                                    database = simpleFactDatabase,
                                    primitives = mapOf(*ktListOf(Call, Cut, Conjunction).map { it.descriptionPair }.toTypedArray())
                            ) to solutionList.changeQueriesTo(this)
                        }
                    }.toTypedArray()
            )
        }
    }
}
