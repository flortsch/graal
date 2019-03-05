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
package org.graalvm.compiler.truffle.pelang.expr;

import org.graalvm.compiler.truffle.pelang.PELangExpressionNode;

import com.oracle.truffle.api.CompilerDirectives.TruffleBoundary;
import com.oracle.truffle.api.dsl.NodeChild;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.Specialization;

@NodeChildren({@NodeChild("leftNode"), @NodeChild("rightNode")})
public abstract class PELangAddNode extends PELangExpressionNode {

    @Specialization
    public long add(long left, long right) {
        return left + right;
    }

    @Specialization
    public String add(String left, String right) {
        return left + right;
    }

    @Specialization
    @TruffleBoundary
    public String add(Object left, Object right) {
        return left.toString() + right.toString();
    }

    public static PELangAddNode create(PELangExpressionNode leftNode, PELangExpressionNode rightNode) {
        return PELangAddNodeGen.create(leftNode, rightNode);
    }

}
