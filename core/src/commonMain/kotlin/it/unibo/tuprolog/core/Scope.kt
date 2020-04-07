package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.impl.ScopeImpl
import org.gciatto.kt.math.BigDecimal
import org.gciatto.kt.math.BigInteger
import kotlin.js.JsName
import kotlin.jvm.JvmStatic
import it.unibo.tuprolog.core.List as LogicList
import it.unibo.tuprolog.core.Set as LogicSet

interface Scope {

    @JsName("variables")
    val variables: Map<String, Var>

    @JsName("containsVar")
    operator fun contains(variable: Var): Boolean

    @JsName("contains")
    operator fun contains(variable: String): Boolean

    @JsName("get")
    operator fun get(variable: String): Var?

    @JsName("where")
    fun where(lambda: Scope.() -> Unit): Scope

    @JsName("with")
    fun <R> with(lambda: Scope.() -> R): R

    @JsName("varOf")
    fun varOf(name: String): Var

    @JsName("atomOf")
    fun atomOf(value: String): Atom

    @JsName("structOf")
    fun structOf(functor: String, vararg args: Term): Struct

    @JsName("structOfSequence")
    fun structOf(functor: String, args: Sequence<Term>): Struct

    @JsName("tupleOf")
    fun tupleOf(vararg terms: Term): Tuple

    @JsName("tupleOfIterable")
    fun tupleOf(terms: Iterable<Term>): Tuple

    @JsName("listOf")
    fun listOf(vararg terms: Term): LogicList

    @JsName("listOfIterable")
    fun listOf(terms: Iterable<Term>): LogicList

    @JsName("emptyList")
    fun emptyList(): EmptyList

    @JsName("emptySet")
    fun emptySet(): EmptySet

    @JsName("listFrom")
    fun listFrom(terms: Iterable<Term>, last: Term? = null): LogicList

    @JsName("setOf")
    fun setOf(vararg terms: Term): LogicSet

    @JsName("setOfIterable")
    fun setOf(terms: Iterable<Term>): LogicSet

    @JsName("factOf")
    fun factOf(head: Struct): Fact

    @JsName("ruleOf")
    fun ruleOf(head: Struct, body1: Term, vararg body: Term): Rule

    @JsName("directiveOf")
    fun directiveOf(body1: Term, vararg body: Term): Directive

    @JsName("clauseOf")
    fun clauseOf(head: Struct?, vararg body: Term): Clause

    @JsName("consOf")
    fun consOf(head: Term, tail: Term): Cons

    @JsName("indicatorOf")
    fun indicatorOf(name: Term, arity: Term): Indicator

    @JsName("indicatorOfStringInt")
    fun indicatorOf(name: String, arity: Int): Indicator

    @JsName("anonymous")
    fun anonymous(): Var

    @Suppress("PropertyName")
    @JsName("_")
    val `_`: Var
        get() = anonymous()

    @JsName("whatever")
    fun whatever(): Var

    @JsName("numOfBigDecimal")
    fun numOf(value: BigDecimal): Real

    @JsName("numOfDouble")
    fun numOf(value: Double): Real

    @JsName("numOfFloat")
    fun numOf(value: Float): Real

    @JsName("numOfBigInteger")
    fun numOf(value: BigInteger): Integer

    @JsName("numOfInt")
    fun numOf(value: Int): Integer

    @JsName("numOfLong")
    fun numOf(value: Long): Integer

    @JsName("numOfShort")
    fun numOf(value: Short): Integer

    @JsName("numOfByte")
    fun numOf(value: Byte): Integer

    @JsName("parseNum")
    fun numOf(value: String): Numeric

    @JsName("numOf")
    fun numOf(value: Number): Numeric

    @JsName("truthOf")
    fun truthOf(value: Boolean): Truth

    @JsName("fail")
    fun fail(): Truth

    companion object {

        @JvmStatic
        @JsName("empty")
        fun empty(): Scope = ScopeImpl(mutableMapOf())

        @JvmStatic
        @JsName("of")
        fun of(vararg vars: String): Scope = of(*vars) {}

        @JvmStatic
        @JsName("ofWithFunction")
        fun of(vararg vars: String, lambda: Scope.() -> Unit): Scope =
            of(*vars.map { Var.of(it) }.toTypedArray(), lambda = lambda)

        @JvmStatic
        @JsName("ofVar")
        fun of(vararg vars: Var): Scope = of(*vars) {}

        @JvmStatic
        @JsName("ofVarWithFunction")
        fun of(vararg vars: Var, lambda: Scope.() -> Unit): Scope =
            ScopeImpl(vars.map { it.name to it }.toMap(mutableMapOf()))
                .where(lambda)

        @JvmStatic
        @JsName("emptyWithFunction")
        fun <R> empty(lambda: Scope.() -> R): R = empty().with(lambda)

        @JvmStatic
        @JsName("ofAndThen")
        fun <R> of(vararg vars: String, lambda: Scope.() -> R): R = of(*vars).with(lambda)

        @JvmStatic
        @JsName("ofVarAndThen")
        fun <R> of(vararg vars: Var, lambda: Scope.() -> R): R = of(*vars).with(lambda)
    }
}