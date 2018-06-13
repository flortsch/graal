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
package org.graalvm.compiler.truffle.pelang;

import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.RootNode;

public final class PELangRootNode extends RootNode {

    @Child private PELangStatementNode bodyNode;
    private final PELangState state;

    public PELangRootNode(FrameDescriptor frameDescriptor, PELangState state, PELangStatementNode bodyNode) {
        super(null, frameDescriptor);
        this.state = state;
        this.bodyNode = bodyNode;
    }

    public PELangState getState() {
        return state;
    }

    public PELangStatementNode getBodyNode() {
        return bodyNode;
    }

    @Override
    public Object execute(VirtualFrame frame) {
        try {
            bodyNode.executeVoid(frame);
            return PELangNull.getInstance();
        } catch (PELangResultException e) {
            return e.getResult();
        }
    }

}
