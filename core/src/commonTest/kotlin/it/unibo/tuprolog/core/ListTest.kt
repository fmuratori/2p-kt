package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropLast
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.CoupleUtils
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.assertSame
import it.unibo.tuprolog.core.List as LogicList

/**
 * Test class for [List] companion object
 *
 * @author Enrico
 */
internal class ListTest {

    private val emptyTerminatedInstances = CoupleUtils.onlyCoupleEmptyListTerminated(Couple.Companion::of)
    private val pipedListInstances = CoupleUtils.onlyCouplePipeTerminated(Couple.Companion::of)

    private val emptyTerminatedElementLists = CoupleUtils.onlyCoupleEmptyListTerminatedElementLists
    private val pipeTerminatedElementLists = CoupleUtils.onlyCouplePipeTerminatedElementLists

    @Test
    fun emptyReturnsEmptyList() {
        assertEqualities(Empty.list(), LogicList.empty())
        assertSame(Empty.list(), LogicList.empty())
    }

    @Test
    fun ofNoVarargTerms() {
        assertEqualities(Empty.list(), LogicList.of())
        assertSame(Empty.list(), LogicList.of())
    }

    @Test
    fun ofVarargTerms() {
        val toBeTested = emptyTerminatedElementLists.map { LogicList.of(*it.toTypedArray()) }

        onCorrespondingItems(emptyTerminatedInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun ofEmptyIterable() {
        val toBeTested = LogicList.of(emptyList<Term>().asIterable())

        assertEqualities(Empty.list(), toBeTested)
        assertSame(Empty.list(), toBeTested)
    }

    @Test
    fun ofIterableOfTerms() {
        val toBeTested = emptyTerminatedElementLists.map { LogicList.of(it.asIterable()) }
        onCorrespondingItems(emptyTerminatedInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromEmptyIterableWithoutLastSpecifiedBehavesLikeOfMethod() {
        val toBeTested = LogicList.from(emptyList<Term>().asIterable())

        assertEqualities(Empty.list(), toBeTested)
        assertSame(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptyIterableSpecifyingLastAsEmptyList() {
        val toBeTested = LogicList.from(emptyList<Term>().asIterable(), Empty.list())

        assertEqualities(Empty.list(), toBeTested)
        assertSame(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptyIterableSpecifyingLast() {
        assertFailsWith<IllegalArgumentException> { LogicList.from(emptyList<Term>().asIterable(), Truth.fail()) }
    }

    @Test
    fun fromIterableWithoutLastSpecifiedBehavesLikeOfMethod() {
        val toBeTested = emptyTerminatedElementLists.map { LogicList.from(it.asIterable()) }

        onCorrespondingItems(emptyTerminatedInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromIterableSpecifyingLast() {
        val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it.dropLast(), it.last()) }

        onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromEmptySequenceWithoutLastSpecifiedBehavesLikeOfMethod() {
        val toBeTested = LogicList.from(emptyList<Term>().asSequence())

        assertEqualities(Empty.list(), toBeTested)
        assertSame(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptySequenceSpecifyingLastAsEmptyList() {
        val toBeTested = LogicList.from(emptyList<Term>().asSequence(), Empty.list())

        assertEqualities(Empty.list(), toBeTested)
        assertSame(Empty.list(), toBeTested)
    }

    @Test
    fun fromEmptySequenceSpecifyingLast() {
        assertFailsWith<IllegalArgumentException> { LogicList.from(emptyList<Term>().asSequence(), Truth.fail()) }
    }

    @Test
    fun fromSequenceWithoutLastSpecifiedBehavesLikeOfMethod() {
        val toBeTested = emptyTerminatedElementLists.map { LogicList.from(it.asSequence()) }

        onCorrespondingItems(emptyTerminatedInstances, toBeTested, ::assertEqualities)
    }

    @Test
    fun fromSequenceSpecifyingLast() {
        val toBeTested = pipeTerminatedElementLists.map { LogicList.from(it.dropLast().asSequence(), it.last()) }

        onCorrespondingItems(pipedListInstances, toBeTested, ::assertEqualities)
    }
}