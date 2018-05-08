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
package org.graalvm.compiler.truffle.pelang.test;

import org.graalvm.compiler.truffle.pelang.PELangBasicBlockNode;
import org.graalvm.compiler.truffle.pelang.PELangBuilder;
import org.junit.Test;

import com.oracle.truffle.api.nodes.RootNode;

public class PELangDispatchTest extends PELangTest {

    protected Object constant10() {
        return 10;
    }

    @Test
    public void dispatchTest() {
        PELangBuilder b = new PELangBuilder();

        // @formatter:off
        RootNode rootNode = b.root(
            b.dispatch(
                b.basicBlock(b.write(0, "flag"), 1),                                         // block 0
                b.basicBlock(b.write(0, "counter"), 2),                                      // block 1
                b.basicBlock(b.read("flag"), 3, 6),                                          // block 2
                b.basicBlock(b.increment(1, "counter"), 4),                                  // block 3
                b.basicBlock(b.equals(10, "counter"), 5, 2),                                 // block 4
                b.basicBlock(b.write(1, "flag"), 2),                                         // block 5
                b.basicBlock(b.ret(b.read("counter")), PELangBasicBlockNode.NO_SUCCESSOR))); // block 6
        // @formatter:on

        assertCallResult(10L, rootNode);

        // TODO: some assertions about PE should be done here
        // assertPartialEvalEquals("constant10", rootNode);
        // StructuredGraph g = prepareGraphPE(rootNode.toString(), rootNode, new Object[]{});
        // new LoopFullUnrollPhase(new CanonicalizerPhase(), new DefaultLoopPolicies()).apply(g,
        // getDefaultHighTierContext());
    }

}