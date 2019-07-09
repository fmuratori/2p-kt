package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertEqualities
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertNotStrictlyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.assertStructurallyEquals
import it.unibo.tuprolog.core.testutils.AssertionUtils.onCorrespondingItems
import it.unibo.tuprolog.core.testutils.TermTypeAssertionUtils
import it.unibo.tuprolog.core.testutils.VarUtils
import it.unibo.tuprolog.core.testutils.VarUtils.assertDifferentVariableExceptForName
import it.unibo.tuprolog.core.testutils.VarUtils.assertSameVariable
import kotlin.test.*

/**
 * Test class for [VarImpl] and [Var]
 *
 * @author Enrico
 */
internal class VarImplTest {

    /**
     * Contains mixed variables instances, correctly and incorrectly named
     */
    private val mixedVarInstances = VarUtils.mixedVars.map { VarImpl(it) }

    /**
     * Contains same mixed variables instances created a second time
     * @see mixedVarInstances
     */
    private val mixedVarsSecondInstance = VarUtils.mixedVars.map { VarImpl(it) }

    @Test
    fun correctName() {
        onCorrespondingItems(VarUtils.mixedVars, mixedVarInstances.map { it.name }) { expectedName, varName ->
            assertEquals(expectedName, varName)
        }
    }

    @Test
    fun nameHandlingForDifferentInstancesOfSameVar() {
        onCorrespondingItems(mixedVarInstances, mixedVarsSecondInstance) { firstInstance, secondInstance ->
            assertEquals(firstInstance.name, secondInstance.name)
            assertNotEquals(firstInstance.completeName, secondInstance.completeName)
        }
    }

    @Test
    fun completeNameStartsWithName() {
        VarUtils.mixedVars.zip(mixedVarInstances).forEach { (varString, varInstance) ->
            assertTrue { varInstance.completeName.startsWith(varString) }
        }
    }

    @Test
    fun completeNamesContainsDifferentGeneratedNumbers() {
        val toTestUniqueIdentifiers = mixedVarInstances.map { it.completeName.substringAfterLast("_") }
        assertTrue { toTestUniqueIdentifiers.groupingBy { it }.eachCount().values.all { it == 1 } }
    }

    @Test
    fun providingIdentifierOverridesProgressiveInstanceCountNaming() {
        assertEqualities(VarImpl("Var", 0), VarImpl("Var", 0))
    }

    @Test
    fun sameVarInstancesAreEquals() {
        onCorrespondingItems(mixedVarInstances, mixedVarInstances, ::assertEqualities)
    }

    @Test
    fun strictlyEqualsWorksAsExpected() {
        onCorrespondingItems(mixedVarInstances, mixedVarsSecondInstance, ::assertNotStrictlyEquals)
    }

    @Test
    fun structurallyEqualsWorksAsExpected() {
        onCorrespondingItems(mixedVarInstances, mixedVarsSecondInstance, ::assertStructurallyEquals)
    }

    @Test
    fun equalsWorksAsExpected() {
        onCorrespondingItems(mixedVarInstances, mixedVarsSecondInstance) { firstCreatedVar, secondCreatedVar ->
            assertEquals(firstCreatedVar, secondCreatedVar)
        }
    }

    @Test
    fun anonymousVarDetection() {
        VarUtils.mixedVars.filter { it != Var.ANONYMOUS_VAR_NAME }.map { VarImpl(it) }.forEach { nonAnonymousVarInstance ->
            assertFalse { nonAnonymousVarInstance.isAnonymous }
        }

        assertTrue(VarImpl("_").isAnonymous)
    }

    @Test
    fun testIsNameWellFormedProperty() {
        VarUtils.correctlyNamedVars.map { VarImpl(it) }.forEach { assertTrue { it.isNameWellFormed } }
        VarUtils.incorrectlyNamedVars.map { VarImpl(it) }.forEach { assertFalse { it.isNameWellFormed } }
    }

    @Test
    fun freshCopyWorksAsExpected() {
        val refreshedVarInstances = mixedVarInstances.map { it.freshCopy() }

        onCorrespondingItems(refreshedVarInstances, mixedVarInstances, ::assertDifferentVariableExceptForName)
    }

    @Test
    fun freshCopyWithScopeAndNoMatchingVariablesInsideIt() {
        val refreshedVarInstances = mixedVarInstances.map { it.freshCopy(Scope.empty()) }

        onCorrespondingItems(refreshedVarInstances, mixedVarInstances, ::assertDifferentVariableExceptForName)
    }

    @Test
    fun freshCopyWithScopeAndMatchingVariableInsideIt() {
        val notAnonymousVarInstances = mixedVarInstances.filterNot { it.isAnonymous }
        val refreshedNamedVarInstances = notAnonymousVarInstances.map { it.freshCopy(Scope.of(it)) }

        onCorrespondingItems(refreshedNamedVarInstances, notAnonymousVarInstances, ::assertSameVariable)
    }

    @Test
    fun freshCopyOfAnonymousVariableAlwaysReturnsDifferentVariable() {
        val anonymousVar = VarImpl("_")
        val refreshedAnonymous = anonymousVar.freshCopy(Scope.of("_"))

        assertDifferentVariableExceptForName(anonymousVar, refreshedAnonymous)
    }

    @Test
    fun toStringWorksAsExpected() {
        val correctNamedVarsToString = VarUtils.correctlyNamedVars.map { VarImpl(it).toString() }
        val incorrectNamedVarsToString = VarUtils.incorrectlyNamedVars.map { VarImpl(it).toString() }

        onCorrespondingItems(correctNamedVarsToString, VarUtils.correctlyNamedVars) { underTestToString, varName ->
            assertEquals(varName, underTestToString)
        }

        onCorrespondingItems(incorrectNamedVarsToString, VarUtils.incorrectlyNamedVars) { underTestToString, varName ->
            assertEquals("¿$varName?", underTestToString)
        }
    }

    @Test
    fun testIsPropertiesAndTypes() {
        mixedVarInstances.forEach(TermTypeAssertionUtils::assertIsVar)
    }

    @Test
    fun groundToReplacesVariableIfCorrectSubstitution() {
        val myAtom = Atom.of("hello")
        val myVar = Var.of("X")

        val toBeTested1 = myVar.apply(Substitution.of(myVar to myAtom))
        val toBeTested2 = myVar.apply(Substitution.of(myVar to myAtom), Substitution.empty())
        val toBeTested3 = myVar[Substitution.of(myVar to myAtom)]

        assertEqualities(myAtom, toBeTested1)
        assertEqualities(myAtom, toBeTested2)
        assertEqualities(myAtom, toBeTested3)
    }

    @Test
    fun groundToDoesntReplaceAnythingIfNoCorrespondingVariableFound() {
        val myAtom = Atom.of("hello")
        val myVar = Var.of("X")

        val toBeTested1 = myVar.apply(Substitution.of(Var.of("A") to myAtom))
        val toBeTested2 = myVar.apply(Substitution.empty(), Substitution.empty())
        val toBeTested3 = myVar[Substitution.of(Var.anonymous() to myAtom)]

        assertEqualities(myVar, toBeTested1)
        assertEqualities(myVar, toBeTested2)
        assertEqualities(myVar, toBeTested3)
    }
}
