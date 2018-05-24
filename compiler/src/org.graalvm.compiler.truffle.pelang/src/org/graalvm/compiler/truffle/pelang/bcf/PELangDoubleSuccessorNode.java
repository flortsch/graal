/*
 * Copyright (c) 2018, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */
package org.graalvm.compiler.truffle.pelang.bcf;

import org.graalvm.compiler.truffle.pelang.PELangExpressionNode;

import com.oracle.truffle.api.CompilerDirectives.CompilationFinal;

public final class PELangDoubleSuccessorNode extends PELangBasicBlockNode {

    @Child private PELangExpressionNode bodyNode;

    @CompilationFinal private int trueSuccessor;
    @CompilationFinal private int falseSuccessor;

    public PELangDoubleSuccessorNode(PELangExpressionNode bodyNode) {
        this(bodyNode, PELangBasicBlockNode.NO_SUCCESSOR, PELangBasicBlockNode.NO_SUCCESSOR);
    }

    public PELangDoubleSuccessorNode(PELangExpressionNode bodyNode, int trueSuccessor, int falseSuccessor) {
        this.bodyNode = bodyNode;
        this.trueSuccessor = trueSuccessor;
        this.falseSuccessor = falseSuccessor;
    }

    public PELangExpressionNode getBodyNode() {
        return bodyNode;
    }

    public int getTrueSuccessor() {
        return trueSuccessor;
    }

    public int getFalseSuccessor() {
        return falseSuccessor;
    }

    public void setTrueSuccessor(int trueSuccessor) {
        this.trueSuccessor = trueSuccessor;
    }

    public void setFalseSuccessor(int falseSuccessor) {
        this.falseSuccessor = falseSuccessor;
    }

}
