package it.unibo.tuprolog.solve.libs.oop.exceptions

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.exception.PrologError
import it.unibo.tuprolog.solve.exception.error.RepresentationError
import it.unibo.tuprolog.solve.libs.oop.fullName
import kotlin.reflect.KClass

@Suppress("MemberVisibilityCanBePrivate")
class TermToObjectConversionException(
    val term: Term,
    val targetType: KClass<*>? = null
) : OopException(
    targetType?.let {
        "Term `$term` cannot be converted into an object of type ${targetType.fullName}"
    } ?: "Term `$term` cannot be converted into an object"
) {
    constructor(term: Term) : this(term, null)

    override fun toPrologError(context: ExecutionContext, signature: Signature): PrologError {
        return RepresentationError.of(
            context,
            signature,
            RepresentationError.Limit.OOP_OBJECT
        )
    }

    override val culprit: Term
        get() = term
}
