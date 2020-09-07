package it.unibo.tuprolog.solve.systemtest

import it.unibo.tuprolog.solve.ClassicSolverFactory
import it.unibo.tuprolog.solve.SolverFactory
import it.unibo.tuprolog.solve.TestFail
import kotlin.test.Ignore
import kotlin.test.Test

class TestClassicFail : TestFail, SolverFactory by ClassicSolverFactory  {
    private val prototype = TestFail.prototype(this)

    @Test
    override fun testFail() {
        prototype.testFail()
    }

    @Test
    override fun testUndefPred() {
        prototype.testUndefPred()
    }

    @Test
    override fun testSetFlagFail() {
        prototype.testSetFlagFail()
    }

    @Test
    override fun testSetFlagWarning() {
        prototype.testSetFlagWarning()
    }
}