package it.unibo.tuprolog.ide.web.redux.actions

import it.unibo.tuprolog.ide.web.tuprolog.TuPrologPage
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.exception.Warning
import it.unibo.tuprolog.ui.gui.Page
import redux.RAction

class UpdatePagesList(val list: Collection<TuPrologPage>) : RAction
class UpdateSelectedPage(val page: TuPrologPage?) : RAction
class NewSolution(val solution: TuPrologSolution) : RAction
class UpdateExecutionContext(val context: ExecutionContextAware) : RAction
class ResetPage : RAction
class UpdateStatus(val newStatus: Page.Status) : RAction
class PageError(val page: Page, val exception: Throwable) : RAction
class CleanPageError : RAction
class CleanSolutions : RAction

class StdOut(val output: String) : RAction
class StdErr(val error: String) : RAction
class Warnings(val warning: Warning) : RAction
