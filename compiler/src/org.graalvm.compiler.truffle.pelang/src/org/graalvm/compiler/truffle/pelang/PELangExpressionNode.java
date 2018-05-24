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

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.UnexpectedResultException;

public abstract class PELangExpressionNode extends PELangStatementNode {

    @Override
    public void executeVoid(VirtualFrame frame) {
        executeGeneric(frame);
    }

    public abstract Object executeGeneric(VirtualFrame frame);

    public long executeLong(VirtualFrame frame) throws UnexpectedResultException {
        return expectLong(executeGeneric(frame));
    }

    public String executeString(VirtualFrame frame) throws UnexpectedResultException {
        return expectString(executeGeneric(frame));
    }

    public PELangFunction executeFunction(VirtualFrame frame) throws UnexpectedResultException {
        return expectFunction(executeGeneric(frame));
    }

    public long evaluateCondition(VirtualFrame frame) {
        try {
            return executeLong(frame);
        } catch (UnexpectedResultException ex) {
            throw new PELangException("expected value of type Long", this);
        }
    }

    public PELangFunction evaluateFunction(VirtualFrame frame) {
        try {
            return executeFunction(frame);
        } catch (UnexpectedResultException ex) {
            throw new PELangException("expected value of type PELangFunction", this);
        }
    }

    private static long expectLong(Object value) throws UnexpectedResultException {
        if (value instanceof Long) {
            return (long) value;
        }
        throw new UnexpectedResultException(value);
    }

    private static String expectString(Object value) throws UnexpectedResultException {
        if (value instanceof String) {
            return (String) value;
        }
        throw new UnexpectedResultException(value);
    }

    private static PELangFunction expectFunction(Object value) throws UnexpectedResultException {
        if (value instanceof PELangFunction) {
            return (PELangFunction) value;
        }
        throw new UnexpectedResultException(value);
    }

}
