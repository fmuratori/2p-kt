package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.Substitution.Companion.asUnifier
import it.unibo.tuprolog.core.testutils.AssertionUtils.dropFirst
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName
import kotlin.test.*

/**
 * Test class for [Substitution] companion object
 *
 * @author Enrico
 */
internal class SubstitutionTest {

    private val correctInstances by lazy { SubstitutionUtils.mixedSubstitutions.map(Substitution::Unifier) }

    @Test
    fun failedShouldReturnTheFailedSubstitutionInstance() {
        assertEquals(Substitution.Fail, Substitution.failed())
        assertSame(Substitution.Fail, Substitution.failed())
    }

    @Test
    fun failedSubstitutionIsFailed() {
        assertTrue(Substitution.failed().isFailed)
    }

    @Test
    fun failedSubstitutionIsNotSuccess() {
        assertFalse(Substitution.failed().isSuccess)
    }

    @Test
    fun emptyReturnsAnEmptySubstitution() {
        assertTrue(Substitution.empty().isEmpty())
    }

    @Test
    fun emptySubstitutionIsSuccess() {
        assertTrue(Substitution.empty().isSuccess)
    }

    @Test
    fun emptySubstitutionIsNotFailed() {
        assertFalse(Substitution.empty().isFailed)
    }

    @Test
    fun asUnifierConvertsAMapVarTermToSubstitution() {
        val toBeTested = SubstitutionUtils.mixedSubstitutions.map { it.asUnifier() }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofVariableAndTerm() {
        val correct = correctInstances.filter { it.size == 1 }
        val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.filter { it.size == 1 }.map { it.first() }
                .map { (variable, withTerm) -> Substitution.of(variable, withTerm) }

        onCorrespondingItems(correct, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofStringVariableAndTerm() {
        val correct = correctInstances.filter { it.size == 1 }
        val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.filter { it.size == 1 }.map { it.first() }
                .map { (variable, withTerm) -> Substitution.of(variable.name, withTerm) }

        onCorrespondingItems(correct, toBeTested) { expectedMap, actualMap ->
            onCorrespondingItems(expectedMap.keys, actualMap.keys, ::assertDifferentVariableExceptForName)
            assertEquals(expectedMap.values.toList(), actualMap.values.toList())
        }
    }

    @Test
    fun ofVariableTermPairs() {
        val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.map {
            Substitution.of(it.first(), *it.dropFirst().toTypedArray())
        }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofIterableVariableTermPairs() {
        val toBeTested = SubstitutionUtils.mixedSubstitutionsAsPairs.map { Substitution.of(it) }

        onCorrespondingItems(correctInstances, toBeTested) { expected, actual -> assertEquals(expected, actual) }
    }

    @Test
    fun ofEmptyIterable() {
        assertEquals(Substitution.empty(), Substitution.of(emptyList()))
    }

    @Test
    fun ofSubstitutions() {
        val correct = Substitution.Unifier(SubstitutionUtils.mixedSubstitutions.reduce { map1, map2 -> map1 + map2 })
        val toBeTested = Substitution.of(correctInstances.first(), *correctInstances.dropFirst().toTypedArray())

        assertEquals(correct, toBeTested)
    }

    @Test
    fun ofSubstitutionsWithAlwaysSameSubstitutionsReturnsThemNotDuplicated() {
        Scope.of("A", "B") {
            val correct = Substitution.Unifier(mapOf(
                    varOf("A") to atomOf("a"),
                    varOf("B") to atomOf("b")
            ))

            val toBeTested = Substitution.of(
                    Substitution.Unifier(mapOf(varOf("B") to atomOf("b"))),
                    Substitution.Unifier(mapOf(varOf("A") to atomOf("a"))),
                    Substitution.Unifier(mapOf(varOf("B") to atomOf("b"))),
                    Substitution.Unifier(mapOf(varOf("A") to atomOf("a"))),
                    Substitution.Unifier(mapOf(varOf("B") to atomOf("b")))
            )

            assertEquals(correct, toBeTested)
        }
    }

    @Test
    fun ofSubstitutionsWithContradictingOnesReturnsFailed() {
        Scope.of("A", "B") {
            val correct = Substitution.Unifier(mapOf(
                    varOf("A") to atomOf("a"),
                    varOf("B") to atomOf("b")
            ))

            val toBeTested = Substitution.of(
                    Substitution.Unifier(mapOf(varOf("B") to atomOf("f"))),
                    Substitution.Unifier(mapOf(varOf("B") to varOf("A"))),
                    Substitution.Unifier(mapOf(varOf("A") to atomOf("b"))),
                    Substitution.Unifier(mapOf(varOf("A") to atomOf("a"))),
                    Substitution.Unifier(mapOf(varOf("B") to atomOf("b")))
            )

            assertEquals(Substitution.Fail, toBeTested)
        }
    }
}
