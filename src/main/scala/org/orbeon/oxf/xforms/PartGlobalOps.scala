/**
 * Copyright (C) 2011 Orbeon, Inc.
 *
 * This program is free software; you can redistribute it and/or modify it under the terms of the
 * GNU Lesser General Public License as published by the Free Software Foundation; either version
 * 2.1 of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * The full text of the license is available at http://www.gnu.org/copyleft/lesser.html
 */
package org.orbeon.oxf.xforms

import java.util.{Collection ⇒ JCollection}

import org.apache.commons.lang3.StringUtils
import org.orbeon.oxf.util.ScalaUtils._
import org.orbeon.oxf.xforms.analysis.ElementAnalysis
import org.orbeon.oxf.xforms.analysis.controls._
import org.orbeon.oxf.xforms.analysis.model.{Instance, Model}
import org.orbeon.oxf.xforms.event.EventHandler
import org.orbeon.oxf.xforms.xbl._
import org.orbeon.oxf.xml.SAXStore

import scala.collection.JavaConverters._

trait PartGlobalOps {

  // Global
  def getMark(prefixedId: String): Option[SAXStore#Mark]

  // Models
  def getModelsForScope(scope: Scope): Seq[Model]
  def jGetModelsForScope(scope: Scope) = getModelsForScope(scope).asJava
  def getInstances(modelPrefixedId: String): JCollection[Instance]

  // Controls
  def getControlAnalysis(prefixedId: String): ElementAnalysis
  def hasControlByName(controlName: String): Boolean
  def controlsByName(controlName: String): Traversable[ElementAnalysis]

  // Events
  def hasHandlerForEvent(eventName: String): Boolean
  def hasHandlerForEvent(eventName: String, includeAllEvents: Boolean): Boolean
  def keypressHandlers: Seq[EventHandler]

  // XBL
  def getBinding(prefixedId: String): Option[ConcreteBinding]
  def getGlobals: collection.Seq[XBLBindings#Global]
  def allBindingsMaybeDuplicates: Iterable[AbstractBinding]

  // Return the scope associated with the given prefixed id (the scope is directly associated with the prefix of the id)
  def containingScope(prefixedId: String): Scope
  def scopeForPrefixedId(prefixedId: String): Scope

  // Repeats
  def repeats: Traversable[RepeatControl]
  def getRepeatHierarchyString(ns: String): String

  // AVTs
  def hasAttributeControl(prefixedForAttribute: String): Boolean
  def getAttributeControl(prefixedForAttribute: String, attributeName: String): AttributeControl

  // Client-side resources
  def scriptsByPrefixedId: Map[String, StaticScript]
  def uniqueJsScripts: List[ShareableScript]

  // Functions derived from getControlAnalysis
  def getControlAnalysisOption(prefixedId: String) = Option(getControlAnalysis(prefixedId))
  def getControlElement(prefixedId: String) = getControlAnalysisOption(prefixedId) map (_.element) orNull
  def hasBinding(prefixedId: String) = getControlAnalysisOption(prefixedId) exists (_.hasBinding)

  def getControlPosition(prefixedId: String) = getControlAnalysisOption(prefixedId) collect {
    case viewTrait: ViewTrait ⇒ viewTrait.index
  }

  def getSelect1Analysis(prefixedId: String) = getControlAnalysisOption(prefixedId) match {
    case Some(selectionControl: SelectionControlTrait) ⇒ selectionControl
    case _ ⇒ null
  }

  def isValueControl(effectiveId: String) =
    getControlAnalysisOption(XFormsUtils.getPrefixedId(effectiveId)) exists (_.isInstanceOf[ValueTrait])

  def appendClasses(sb: java.lang.StringBuilder, prefixedId: String) =
    getControlAnalysisOption(prefixedId) foreach { controlAnalysis ⇒
      val controlClasses = controlAnalysis.classes
      if (StringUtils.isNotEmpty(controlClasses)) {
        if (sb.length > 0)
          sb.append(' ')
        sb.append(controlClasses)
      }
    }

  // LHHA
  def getLHH(prefixedId: String, lhha: String) =
    collectByErasedType[StaticLHHASupport](getControlAnalysis(prefixedId)) flatMap (_.lhh(lhha)) orNull

  def getAlerts(prefixedId: String) =
    collectByErasedType[StaticLHHASupport](getControlAnalysis(prefixedId)).toList flatMap (_.alerts)

  def hasLHHA(prefixedId: String, lhha: String) =
    collectByErasedType[StaticLHHASupport](getControlAnalysis(prefixedId)) exists (_.hasLHHA(lhha))
}