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
package org.graalvm.compiler.truffle.pelang.ncf;

import org.graalvm.compiler.truffle.pelang.PELangResultException;
import org.graalvm.compiler.truffle.pelang.PELangStatementNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangExpressionNode;

import com.oracle.truffle.api.frame.VirtualFrame;

public final class PELangReturnNode extends PELangStatementNode {

    @Child private PELangExpressionNode bodyNode;

    public PELangReturnNode(PELangExpressionNode bodyNode) {
        this.bodyNode = bodyNode;
    }

    @Override
    public void executeVoid(VirtualFrame frame) {
        Object result = bodyNode.executeGeneric(frame);
        throw new PELangResultException(result);
    }

}