/**
 * Copyright (C) 2010 Orbeon, Inc.
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
package org.orbeon.oxf.xforms.control;

import org.dom4j.Element;
import org.orbeon.oxf.util.PropertyContext;
import org.orbeon.oxf.xforms.xbl.XBLContainer;

import java.util.ArrayList;
import java.util.List;

public abstract class XFormsSingleNodeContainerControl extends XFormsSingleNodeControl implements XFormsContainerControl {

    private List<XFormsControl> children;

    public XFormsSingleNodeContainerControl(XBLContainer container, XFormsControl parent, Element element, String name, String effectiveId) {
        super(container, parent, element, name, effectiveId);
    }

    public void addChild(XFormsControl XFormsControl) {
        if (children == null)
            children = new ArrayList<XFormsControl>();
        children.add(XFormsControl);
    }

    public List<XFormsControl> getChildren() {
        return children;
    }

    public int getSize() {
        return (children != null) ? children.size() : 0;
    }

    protected void setChildren(List<XFormsControl> children) {
        this.children = children;
    }

    /**
     * Update this control's effective id and its descendants based on the parent's effective id.
     */
    @Override
    public void updateEffectiveId() {
        super.updateEffectiveId();
        final List<XFormsControl> children = getChildren();
        if (children != null) {
            for (XFormsControl child: children) {
                child.updateEffectiveId();
            }
        }
    }

    @Override
    public Object getBackCopy(PropertyContext propertyContext) {

        // Clone this
        final XFormsSingleNodeContainerControl cloned = (XFormsSingleNodeContainerControl) super.getBackCopy(propertyContext);

        // Clone children if any
        if (children != null) {
            cloned.children = new ArrayList<XFormsControl>(children.size());
            for (final XFormsControl currentChildControl: children) {
                final XFormsControl currentChildClone = (XFormsControl) currentChildControl.getBackCopy(propertyContext);
                currentChildClone.setParent(cloned);
                cloned.children.add(currentChildClone);
            }
        }

        return cloned;
    }

    public void childrenAdded(PropertyContext propertyContext) {
        // For subclasses
    }

    @Override
    public void iterationRemoved(PropertyContext propertyContext) {
        final List<XFormsControl> children = getChildren();
        if (children != null) {
            for (XFormsControl child: children) {
                child.iterationRemoved(propertyContext);
            }
        }
    }

    @Override
    public boolean setFocus() {
        // "4.3.7 The xforms-focus Event [...] Setting the focus to a group or switch container form control set the
        // focus to the first form control in the container that is able to accept focus"
        if (children != null && children.size() > 0) {
            for (XFormsControl currentControl: children) {
                final boolean didSetFocus = currentControl.setFocus();
                if (didSetFocus)
                    return true;
            }
        }
        return false;
    }
}
