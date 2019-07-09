package it.unibo.tuprolog.core

import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.SubstitutionUtils
import kotlin.test.*

/**
 * Test class for [Substitution.Success] and [Substitution]
 *
 * @author Enrico
 */
internal class SubstitutionSuccessTest {

    private val aVar = Var.of("A")
    private val bVar = Var.of("B")
    private val xAtom = Atom.of("x")
    private val aVarToXAtomSubstitution = Substitution.Success(mapOf(aVar to xAtom))
    private val bVarToXAtomSubstitution = Substitution.Success(mapOf(bVar to xAtom))

    private val substitutions by lazy {
        SubstitutionUtils.mixedSubstitutions.map(Substitution::Success) +
                listOf(aVarToXAtomSubstitution, bVarToXAtomSubstitution)
    }

    @Test
    fun isSuccessIsTrue() {
        substitutions.forEach { assertTrue { it.isSuccess } }
    }

    @Test
    fun isFailedIsFalse() {
        substitutions.forEach { assertFalse { it.isFailed } }
    }

    @Test
    fun shouldSubstituteVariableWithProvidedSubstitution() {
        val correct = SubstitutionUtils.termsWith(xAtom)
        val toBeTested = SubstitutionUtils.termsWith(aVar).map { aVarToXAtomSubstitution.applyTo(it) }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun shouldNotSubstituteNotCorrespondingVariable() {
        val correct = SubstitutionUtils.termsWith(aVar)
        val toBeTested = SubstitutionUtils.termsWith(aVar).map { bVarToXAtomSubstitution.applyTo(it) }

        onCorrespondingItems(correct, toBeTested, ::assertEqualities)
    }

    @Test
    fun equalsWorksAsExpected() {
        assertEquals(Substitution.Success(mapOf(aVar to xAtom)), Substitution.Success(mapOf(aVar to xAtom)))
        assertNotEquals(aVarToXAtomSubstitution, bVarToXAtomSubstitution)
    }

}