package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestClause
import kotlin.test.Ignore
import kotlin.test.Test

class TestClassicClause : TestClause, SolverFactory by ClassicSolverFactory  {
    private val prototype = TestClause.prototype(this)

    @Test
    override fun testClauseXBody() {
        prototype.testClauseXBody()
    }

    @Test
    @Ignore
    override fun testClauseAnyB() {
        prototype.testClauseAnyB()
    }

    @Test
    @Ignore
    override fun testClauseNumB() {
        prototype.testClauseNumB()
    }

    @Test
    @Ignore
    override fun testClauseFAnyNum() {
        prototype.testClauseFAnyNum()
    }

    @Test
    @Ignore
    override fun testClauseAtomBody() {
        prototype.testClauseAtomBody()
    }
}