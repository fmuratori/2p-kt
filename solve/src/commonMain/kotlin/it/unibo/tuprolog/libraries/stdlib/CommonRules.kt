package it.unibo.tuprolog.libraries.stdlib

import it.unibo.tuprolog.libraries.stdlib.rule.Arrow
import it.unibo.tuprolog.libraries.stdlib.rule.Member
import it.unibo.tuprolog.libraries.stdlib.rule.Not
import it.unibo.tuprolog.libraries.stdlib.rule.Semicolon
import it.unibo.tuprolog.rule.RuleWrapper
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.theory.ClauseDatabase

object CommonRules {
    val wrappers: Sequence<RuleWrapper<ExecutionContext>> = sequenceOf(
        Not,
        Arrow,
        Semicolon.If.Then,
        Semicolon.If.Else,
        Semicolon.Or.Left,
        Semicolon.Or.Right,
        Member.Base,
        Member.Recursive
    )

    val clauseDb: ClauseDatabase
        get() = ClauseDatabase.of(wrappers.map { it.wrappedImplementation })
}