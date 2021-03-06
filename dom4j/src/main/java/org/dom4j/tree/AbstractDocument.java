/*
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 *
 * This software is open source.
 * See the bottom of this file for the licence.
 */

package org.dom4j.tree;

import org.dom4j.*;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * <code>AbstractDocument</code> is an abstract base class for tree
 * implementors to use for implementation inheritance.
 * </p>
 *
 * @author James Strachan
 */
public abstract class AbstractDocument extends AbstractBranch implements
        Document {

    public AbstractDocument() {
    }

    public short getNodeType() {
        return DOCUMENT_NODE;
    }

    public String getUniquePath(Element context) {
        return "/";
    }

    public Document getDocument() {
        return this;
    }


    public String getStringValue() {
        Element root = getRootElement();

        return (root != null) ? root.getStringValue() : "";
    }

    public String asXML() {
        OutputFormat format = new OutputFormat();

        try {
            StringWriter out = new StringWriter();
            XMLWriter writer = new XMLWriter(out, format);
            writer.write(this);
            writer.flush();

            return out.toString();
        } catch (IOException e) {
            throw new RuntimeException("IOException while generating textual "
                    + "representation: " + e.getMessage());
        }
    }

    public void write(Writer out) throws IOException {
        OutputFormat format = new OutputFormat();

        XMLWriter writer = new XMLWriter(out, format);
        writer.write(this);
    }

    /**
     * <p>
     * <code>accept</code> method is the <code>Visitor Pattern</code>
     * method.
     * </p>
     *
     * @param visitor
     *            <code>Visitor</code> is the visitor.
     */
    public void accept(Visitor visitor) {
        visitor.visit(this);

        // visit content
        List<Node> content = content();

        if (content != null) {
            for (Iterator<Node> iter = content.iterator(); iter.hasNext();) {
                Node node = iter.next();
                node.accept(visitor);
            }
        }
    }

    public String toString() {
        return super.toString() + " [Document: name " + getName() + "]";
    }

    public void normalize() {
        Element element = getRootElement();

        if (element != null) {
            element.normalize();
        }
    }

    public Document addComment(String comment) {
        Comment node = getDocumentFactory().createComment(comment);
        add(node);

        return this;
    }

    public Document addProcessingInstruction(String target, String data) {
        ProcessingInstruction node = getDocumentFactory()
                .createProcessingInstruction(target, data);
        add(node);

        return this;
    }

    public Element addElement(String name) {
        Element element = getDocumentFactory().createElement(name);
        add(element);

        return element;
    }

    public Element addElement(String qualifiedName, String namespaceURI) {
        Element element = getDocumentFactory().createElement(qualifiedName,
                namespaceURI);
        add(element);

        return element;
    }

    public Element addElement(QName qName) {
        Element element = getDocumentFactory().createElement(qName);
        add(element);

        return element;
    }

    public void setRootElement(Element rootElement) {
        clearContent();

        if (rootElement != null) {
            super.add(rootElement);
            rootElementAdded(rootElement);
        }
    }

    public void add(Element element) {
        checkAddElementAllowed(element);
        super.add(element);
        rootElementAdded(element);
    }

    public boolean remove(Element element) {
        boolean answer = super.remove(element);
        Element root = getRootElement();

        if ((root != null) && answer) {
            setRootElement(null);
        }

        element.setDocument(null);

        return answer;
    }

    protected void childAdded(Node node) {
        if (node != null) {
            node.setDocument(this);
        }
    }

    protected void childRemoved(Node node) {
        if (node != null) {
            node.setDocument(null);
        }
    }

    protected void checkAddElementAllowed(Element element) {
        Element root = getRootElement();

        if (root != null) {
            throw new IllegalAddException(this, element,
                    "Cannot add another element to this "
                            + "Document as it already has a root "
                            + "element of: " + root.getQualifiedName());
        }
    }

    /**
     * Called to set the root element variable
     *
     * @param rootElement
     *            DOCUMENT ME!
     */
    protected abstract void rootElementAdded(Element rootElement);
}

/*
 * Redistribution and use of this software and associated documentation
 * ("Software"), with or without modification, are permitted provided that the
 * following conditions are met:
 *
 * 1. Redistributions of source code must retain copyright statements and
 * notices. Redistributions must also contain a copy of this document.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 *
 * 3. The name "DOM4J" must not be used to endorse or promote products derived
 * from this Software without prior written permission of MetaStuff, Ltd. For
 * written permission, please contact dom4j-info@metastuff.com.
 *
 * 4. Products derived from this Software may not be called "DOM4J" nor may
 * "DOM4J" appear in their names without prior written permission of MetaStuff,
 * Ltd. DOM4J is a registered trademark of MetaStuff, Ltd.
 *
 * 5. Due credit should be given to the DOM4J Project - http://www.dom4j.org
 *
 * THIS SOFTWARE IS PROVIDED BY METASTUFF, LTD. AND CONTRIBUTORS ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL METASTUFF, LTD. OR ITS CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Copyright 2001-2005 (C) MetaStuff, Ltd. All Rights Reserved.
 */
