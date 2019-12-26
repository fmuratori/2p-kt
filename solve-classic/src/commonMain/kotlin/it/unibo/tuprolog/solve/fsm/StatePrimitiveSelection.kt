package it.unibo.tuprolog.solve.fsm

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.primitive.Signature
import it.unibo.tuprolog.primitive.extractSignature
import it.unibo.tuprolog.solve.ExecutionContextImpl
import it.unibo.tuprolog.solve.exception.TuPrologRuntimeException
import it.unibo.tuprolog.solve.exception.prologerror.InstantiationError
import it.unibo.tuprolog.solve.exception.prologerror.TypeError
import it.unibo.tuprolog.utils.cursor

internal data class StatePrimitiveSelection(override val context: ExecutionContextImpl) : AbstractState(context) {

    private fun exceptionalState(exception: TuPrologRuntimeException): StateException {
        return StateException(
            exception,
            context.copy(step = nextStep())
        )
    }

    override fun computeNext(): State {
        return with(context) {
            when (val goal = currentGoal!!/*[substitution]*/) {
                is Var -> {
                    exceptionalState(
                        InstantiationError.forGoal(
                            context = context,
                            procedure = context.procedure!!.extractSignature(),
                            variable = goal
                        )
                    )
                }
                is Struct -> {
                    val signature = goal.extractSignature()

                    if (libraries.hasPrimitive(signature)) {

                        val req = toRequest(signature, goal.argsList)
                        val primitive = libraries.primitives[signature]
                            ?: error("Inconsistent behaviour of Library.contains and Library.get")

                        try {
                            val primitiveExecutions = primitive(req).cursor() //.also { require(!it.isOver) }

                            StatePrimitiveExecution(
                                context.createChildAppendingPrimitivesAndChoicePoints(primitiveExecutions)
                            )
                        } catch (exception: TuPrologRuntimeException) {
                            exceptionalState(exception.updateContext(context))
                        }
                    } else {
                        StateRuleSelection(context.copy(step = nextStep()))
                    }
                }
                else -> {
                    exceptionalState(
                        TypeError.forGoal(
                            context = context,
                            procedure = context.procedure?.extractSignature() ?: Signature("?-", 1),
                            expectedType = TypeError.Expected.CALLABLE,
                            actualValue = goal
                        )
                    )
                }
            }
        }
    }

}