package it.unibo.tuprolog.ide.web.components

import Message
import AppState
import csstype.HtmlAttributes.Companion.width
import csstype.pct
import csstype.vh
import mui.lab.TabContext
import mui.lab.TabPanel
import mui.material.Alert
import mui.material.List
import mui.material.Stack
import mui.material.Tab
import mui.material.Tabs
import react.FC
import react.Props
import react.ReactNode
import react.redux.useSelector
import emotion.react.css
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.ide.web.redux.actions.StdOut
import it.unibo.tuprolog.ide.web.tuprolog.TuPrologSolution
import it.unibo.tuprolog.ide.web.utils.xs
import it.unibo.tuprolog.solve.ExecutionContextAware
import it.unibo.tuprolog.solve.exception.Warning
import mui.base.TextareaAutosize
import mui.icons.material.Mail
import mui.lab.TreeItem
import mui.lab.TreeView
import mui.material.Button
import mui.material.ButtonColor
import mui.material.ButtonVariant
import mui.material.Grid
import mui.material.IconPosition
import mui.material.MuiInput.Companion.fullWidth
import mui.material.Size
import mui.material.Typography
import react.create
import react.dom.html.ReactHTML.div
import react.dom.html.ReactHTML.li
import react.dom.html.ReactHTML.p
import react.dom.html.ReactHTML.table
import react.dom.html.ReactHTML.td
import react.dom.html.ReactHTML.textarea
import react.dom.html.ReactHTML.th
import react.dom.html.ReactHTML.tr
import react.dom.html.ReactHTML.ul
import react.useState

val SolutionsContainer = FC<Props> {

    val solutions = useSelector<AppState, Collection<TuPrologSolution>> { s -> s.tuProlog.solutions }
    val eContext = useSelector<AppState, ExecutionContextAware?> { s -> s.tuProlog.executionContext }
    val outputs = useSelector<AppState, String> { s -> s.tuProlog.stdOutMessage }
    val errors = useSelector<AppState, String> {s -> s.tuProlog.stdErrMessage }
    val warnings = useSelector<AppState, Warning?> { s -> s.tuProlog.warningMessage }

    val inputs = useSelector<AppState, String> { s -> s.tuProlog.stdInMessage }


    val messages = useSelector<AppState, List<Message>> { s -> s.messages }

    var activeTab by useState("solutionsTab")

    TabContext {
        value = activeTab
        Tabs {
            value = activeTab
            onChange = { _, newValue -> activeTab = newValue }

            Tab {
                value = "solutionsTab"
                label = ReactNode("Solutions")
                //wrapped = true
            }
//            Tab {
//                value = "stdInTab"
//                label = ReactNode("Standard In")
//            }
//            Tab {
//                value = "stdOutTab"
//                label = ReactNode("Standard Out")
//            }

            Tab {
                value = "stdInTab"
                label = ReactNode("Standard In")
            }
            Tab {
                value = "stdOutTab"
                label = ReactNode("Standard Out")
            }
            Tab {
                value = "stdErr"
                label = ReactNode("Standard Error")
            }
            Tab {
                value = "warnings"
                label = ReactNode("Warnings")
            }
            Tab {
                value = "operators"
                label = ReactNode("Operators")
            }
            Tab {
                value = "libraries"
                label = ReactNode("Libraries")
            }
            Tab {
                value = "staticKb"
                label = ReactNode("Static KB")
            }
            Tab {
                value = "dynamicKb"
                label = ReactNode("Dynamic KB")
            }
            Tab {
                value = "messagesTab"
                icon = NumberedIcon.create {
                    icon = Mail
                    number = messages.size
                }
                iconPosition = IconPosition.end
            }
        }

        div {
            css {
            }

            TabPanel {
                value = "solutionsTab"

                List {
                    dense = true
                    solutions.forEach {
                        if (it.isYes)
                            YesView {
                                solution = it
                            }
                        if (it.isNo)
                            NoView {
                                solution = it
                            }
                        if (it.isHalt)
                            HaltView {
                                solution = it
                            }
                    }
                }
            }
            TabPanel {
                value = "stdInTab"
                //+inputs
               textarea {
                   css  {
                       width = 100.pct
                       height = 30.vh
                   }
                   onChange?.let {
                       it2 -> console.log(it2)
                   }
               }
               /* if (eContext != null)
                    +eContext.standardInput.toString()
                else
                    +"Empty stdIn"*/
            }
            TabPanel {
                value = "stdOutTab"
                +outputs
                /*if (eContext != null)
                    +eContext.standardOutput.toString()
                else
                    +"Empty stdIn"*/
            }
            TabPanel {
                value = "stdErr"
                +errors
                /*if (eContext != null)
                    +eContext.standardError.toString()
                else
                    +"Empty stdErr"*/
            }
            TabPanel {
                value = "warnings"
                if (warnings != null) {
                    +warnings.message.toString()
                }
//                if (eContext != null)
//                    +eContext.warnings.toString()
//                else
//                    +"Empty warnings"
            }
            TabPanel {
                value = "operators"
                if (eContext != null)
                    table {
                        tr {
                            th {
                                +"FUNCTOR"
                            }
                            th {
                                +"PRIORITY"
                            }
                            th {
                                +"SPECIFIER"
                            }
                        }
                        for (operator in eContext.operators) {
                                tr {
                                    td {
                                        +operator.functor
                                    }
                                    td {
                                        +operator.priority.toString()
                                    }
                                    td {
                                        +operator.specifier.toString()
                                    }
                                }
                        }
                    }
                else
                    +"Empty operators"
            }
            TabPanel {
                value = "libraries"
                if (eContext != null) {

                    TreeView {
                        var idCounter = 0
                        eContext.libraries.libraries.forEach {
                            idCounter += 1
                            TreeItem {
                                nodeId = (idCounter).toString()
                                label = ReactNode(it.alias)
                                TreeItem {
                                    nodeId = (idCounter+1).toString()
                                    label = ReactNode("Functions")
                                    ul {
                                        it.functions.keys.forEach { it2 ->
                                            li {
                                                +it2.toIndicator().toString()
                                            }
                                        }
                                    }
                                }
                                TreeItem {
                                    nodeId = (idCounter + 2).toString()
                                    label = ReactNode("Predicates")
                                    ul {
                                        val myPredicates: MutableList<String> = mutableListOf()
                                        it.clauses.filterIsInstance<Rule>().forEach { it2 ->
                                                myPredicates += it2.head.indicator.toString()
                                            }
                                        it.primitives.keys.forEach { it3 ->
                                                myPredicates += it3.toIndicator().toString()
                                            }
                                        li {
                                            myPredicates.distinct().sorted().forEach {
                                                +it
                                            }
                                        }
                                    }
                                }
                                TreeItem {
                                    nodeId = (idCounter+3).toString()
                                    label = ReactNode("Operators")
                                    ul {
                                        it.operators.forEach { it2 ->
                                            li {
                                                +it2.functor.plus(" , ").plus(it2.specifier.toString()).plus(" , ")
                                                    .plus(it2.priority.toString())
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                else
                    +"Empty libraries"
            }
            TabPanel {
                value = "staticKb"
                if (eContext != null)
                    +eContext.staticKb.toString()
                else
                    +"Empty staticKb"
            }
            TabPanel {
                value = "dynamicKb"
                if (eContext != null)
                    eContext.dynamicKb
                else
                    +"Empty dynamicKb"
            }
            TabPanel {
                value = "messagesTab"

                Grid {
                    container = true

                    Grid {
                        item = true
                        xs = 4

                        Stack {
                            messages.forEach { m ->
                                Alert {
                                    severity = m.color
                                    onClose = {}
                                    +m.text
                                }
                            }
                        }
                    }

                    Grid {
                        item = true
                        xs = true

                        Button {
                            color = ButtonColor.error
                            size = Size.small
                            variant = ButtonVariant.text
                            + "Delete all"
                        }
                        Button {
                            color = ButtonColor.error
                            size = Size.small
                            variant = ButtonVariant.text
                            + "Sort by severity"
                        }
                        Button {
                            color = ButtonColor.error
                            size = Size.small
                            variant = ButtonVariant.text
                            + "Sort recent"
                        }
                        Typography {
                            + "total messages: 1123"
                        }
                    }
                }
            }
        }
    }
}
