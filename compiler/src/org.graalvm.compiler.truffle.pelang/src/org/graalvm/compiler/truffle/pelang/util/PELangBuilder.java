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
package org.graalvm.compiler.truffle.pelang.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.graalvm.compiler.truffle.pelang.PELangExpressionNode;
import org.graalvm.compiler.truffle.pelang.PELangFunction;
import org.graalvm.compiler.truffle.pelang.PELangPrintNode;
import org.graalvm.compiler.truffle.pelang.PELangRootNode;
import org.graalvm.compiler.truffle.pelang.PELangState;
import org.graalvm.compiler.truffle.pelang.PELangStatementNode;
import org.graalvm.compiler.truffle.pelang.bcf.PELangBasicBlockDispatchNode;
import org.graalvm.compiler.truffle.pelang.bcf.PELangBasicBlockNode;
import org.graalvm.compiler.truffle.pelang.bcf.PELangDoubleSuccessorNode;
import org.graalvm.compiler.truffle.pelang.bcf.PELangMultiSuccessorNode;
import org.graalvm.compiler.truffle.pelang.bcf.PELangSingleSuccessorNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangAddNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangDivNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangEqualsNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangGreaterThanNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangInvokeNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangLeftShiftNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangLessThanNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangLiteralArrayNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangLiteralFunctionNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangLiteralLongNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangLiteralNullNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangNewObjectNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangLiteralStringNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangMinusNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangNewArrayNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangNotNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangReadArgumentNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangReadArrayNode;
import org.graalvm.compiler.truffle.pelang.expr.PELangWriteArrayNode;
import org.graalvm.compiler.truffle.pelang.ncf.PELangBlockNode;
import org.graalvm.compiler.truffle.pelang.ncf.PELangIfNode;
import org.graalvm.compiler.truffle.pelang.ncf.PELangReturnNode;
import org.graalvm.compiler.truffle.pelang.ncf.PELangSwitchNode;
import org.graalvm.compiler.truffle.pelang.ncf.PELangWhileNode;
import org.graalvm.compiler.truffle.pelang.obj.PELangPropertyReadNode;
import org.graalvm.compiler.truffle.pelang.obj.PELangPropertyWriteNode;
import org.graalvm.compiler.truffle.pelang.var.PELangGlobalReadNode;
import org.graalvm.compiler.truffle.pelang.var.PELangGlobalWriteNode;
import org.graalvm.compiler.truffle.pelang.var.PELangLocalReadNode;
import org.graalvm.compiler.truffle.pelang.var.PELangLocalWriteNode;

import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;

public class PELangBuilder {

    private final FrameDescriptor frameDescriptor;
    private final PELangState state;

    private PELangBuilder(FrameDescriptor frameDescriptor, PELangState state) {
        this.frameDescriptor = frameDescriptor;
        this.state = state;
    }

    public static PELangBuilder create() {
        return new PELangBuilder(new FrameDescriptor(), new PELangState());
    }

    public PELangRootNode root(String name, PELangStatementNode bodyNode) {
        return new PELangRootNode(frameDescriptor, name, state, bodyNode);
    }

    public PELangExpressionNode long$(long value) {
        return new PELangLiteralLongNode(value);
    }

    public PELangExpressionNode string(String value) {
        return new PELangLiteralStringNode(value);
    }

    public PELangExpressionNode function(Function<PELangBuilder, FunctionHeader> headerFunction, Function<PELangBuilder, PELangStatementNode> bodyFunction) {
        PELangBuilder functionBuilder = new PELangBuilder(new FrameDescriptor(), state);
        FunctionHeader header = headerFunction.apply(functionBuilder);
        List<PELangStatementNode> bodyNodes = new ArrayList<>();

        // read arguments and make them available as local variables
        for (int i = 0; i < header.getArgs().length; i++) {
            bodyNodes.add(functionBuilder.writeLocal(header.getArgs()[i], functionBuilder.readArgument(i)));
        }
        PELangStatementNode bodyNode = bodyFunction.apply(functionBuilder);
        bodyNodes.add(bodyNode);

        PELangRootNode rootNode = functionBuilder.root(header.getName(), functionBuilder.block(bodyNodes.stream().toArray(PELangStatementNode[]::new)));
        return new PELangLiteralFunctionNode(new PELangFunction(header, Truffle.getRuntime().createCallTarget(rootNode)));
    }

    public PELangExpressionNode newObject() {
        return new PELangNewObjectNode();
    }

    public PELangExpressionNode newArray(Class<?> type, PELangExpressionNode dimensionsNode) {
        return new PELangNewArrayNode(type, dimensionsNode);
    }

    public PELangExpressionNode array(Object array) {
        return PELangLiteralArrayNode.create(array);
    }

    public PELangExpressionNode null$() {
        return new PELangLiteralNullNode();
    }

    public FunctionHeader header(String name, String... args) {
        return new FunctionHeader(name, args);
    }

    public PELangExpressionNode add(PELangExpressionNode leftNode, PELangExpressionNode rightNode) {
        return PELangAddNode.create(leftNode, rightNode);
    }

    public PELangExpressionNode add(PELangExpressionNode... nodes) {
        if (nodes.length < 2) {
            throw new IllegalArgumentException("length of nodes must be greater than two");
        } else {
            return add_(Arrays.asList(nodes));
        }
    }

    private PELangExpressionNode add_(List<PELangExpressionNode> nodes) {
        if (nodes.size() == 2) {
            return PELangAddNode.create(nodes.get(0), nodes.get(1));
        } else {
            return PELangAddNode.create(nodes.get(0), add_(nodes.subList(1, nodes.size())));
        }
    }

    public PELangExpressionNode minus(PELangExpressionNode leftNode, PELangExpressionNode rightNode) {
        return PELangMinusNode.create(leftNode, rightNode);
    }

    public PELangExpressionNode minus(PELangExpressionNode... nodes) {
        if (nodes.length < 2) {
            throw new IllegalArgumentException("length of nodes must be greater than two");
        } else {
            return minus_(Arrays.asList(nodes));
        }
    }

    private PELangExpressionNode minus_(List<PELangExpressionNode> nodes) {
        if (nodes.size() == 2) {
            return PELangMinusNode.create(nodes.get(0), nodes.get(1));
        } else {
            return PELangMinusNode.create(nodes.get(0), add_(nodes.subList(1, nodes.size())));
        }
    }

    public PELangExpressionNode leftShift(PELangExpressionNode leftNode, PELangExpressionNode rightNode) {
        return PELangLeftShiftNode.create(leftNode, rightNode);
    }

    public PELangExpressionNode div(PELangExpressionNode leftNode, PELangExpressionNode rightNode) {
        return PELangDivNode.create(leftNode, rightNode);
    }

    public PELangExpressionNode eq(PELangExpressionNode leftNode, PELangExpressionNode rightNode) {
        return PELangEqualsNode.create(leftNode, rightNode);
    }

    public PELangExpressionNode not(PELangExpressionNode bodyNode) {
        return new PELangNotNode(bodyNode);
    }

    public PELangExpressionNode lt(PELangExpressionNode leftNode, PELangExpressionNode rightNode) {
        return PELangLessThanNode.create(leftNode, rightNode);
    }

    public PELangExpressionNode gt(PELangExpressionNode leftNode, PELangExpressionNode rightNode) {
        return PELangGreaterThanNode.create(leftNode, rightNode);
    }

    public PELangStatementNode block(PELangStatementNode... bodyNodes) {
        return new PELangBlockNode(bodyNodes);
    }

    public PELangStatementNode if$(PELangExpressionNode conditionNode, PELangStatementNode thenNode,
                    PELangStatementNode elseNode) {
        return new PELangIfNode(conditionNode, thenNode, elseNode);
    }

    public PELangStatementNode if$(PELangExpressionNode conditionNode, PELangStatementNode thenNode) {
        return if$(conditionNode, thenNode, block());
    }

    public PELangStatementNode while$(PELangExpressionNode conditionNode, PELangStatementNode bodyNode) {
        return new PELangWhileNode(conditionNode, bodyNode);
    }

    public PELangStatementNode switch$(PELangExpressionNode valueNode, Case... cases) {
        PELangExpressionNode[] caseValueNodes = Arrays.stream(cases).map(Case::getValueNode).toArray(PELangExpressionNode[]::new);
        PELangStatementNode[] caseBodyNodes = Arrays.stream(cases).map(Case::getBodyNode).toArray(PELangStatementNode[]::new);

        return new PELangSwitchNode(valueNode, caseValueNodes, caseBodyNodes);
    }

    public Case case$(PELangExpressionNode valueNode, PELangStatementNode bodyNode) {
        return new Case(valueNode, bodyNode);
    }

    public PELangExpressionNode readLocal(String identifier) {
        return PELangLocalReadNode.create(frameDescriptor.findOrAddFrameSlot(identifier));
    }

    public PELangExpressionNode readGlobal(String identifier) {
        return PELangGlobalReadNode.create(identifier);
    }

    public PELangExpressionNode readArgument(int index) {
        return new PELangReadArgumentNode(index);
    }

    public PELangExpressionNode readArray(PELangExpressionNode arrayNode, PELangExpressionNode indicesNode) {
        return PELangReadArrayNode.create(arrayNode, indicesNode);
    }

    public PELangExpressionNode readProperty(PELangExpressionNode receiverNode, String name) {
        return new PELangPropertyReadNode(receiverNode, name);
    }

    public PELangExpressionNode writeLocal(String identifier, PELangExpressionNode valueNode) {
        return PELangLocalWriteNode.create(frameDescriptor.findOrAddFrameSlot(identifier), valueNode);
    }

    public PELangExpressionNode writeGlobal(String identifier, PELangExpressionNode valueNode) {
        return PELangGlobalWriteNode.create(identifier, valueNode);
    }

    public PELangExpressionNode writeArray(PELangExpressionNode arrayNode, PELangExpressionNode indicesNode, PELangExpressionNode valueNode) {
        return PELangWriteArrayNode.create(arrayNode, indicesNode, valueNode);
    }

    public PELangExpressionNode writeProperty(PELangExpressionNode receiverNode, String name, PELangExpressionNode valueNode) {
        return new PELangPropertyWriteNode(receiverNode, name, valueNode);
    }

    public PELangExpressionNode incrementLocal(String identifier, PELangExpressionNode valueNode) {
        return writeLocal(identifier, add(valueNode, readLocal(identifier)));
    }

    public PELangExpressionNode incrementGlobal(String identifier, PELangExpressionNode valueNode) {
        return writeGlobal(identifier, add(valueNode, readGlobal(identifier)));
    }

    public PELangExpressionNode invoke(PELangExpressionNode functionNode, PELangExpressionNode... argumentNodes) {
        return new PELangInvokeNode(functionNode, argumentNodes);
    }

    public PELangStatementNode return$(PELangExpressionNode bodyNode) {
        return new PELangReturnNode(bodyNode);
    }

    public PELangStatementNode print(PELangExpressionNode argumentNode) {
        return PELangPrintNode.create(argumentNode);
    }

    public PELangStatementNode dispatch(PELangBasicBlockNode... blockNodes) {
        return new PELangBasicBlockDispatchNode(blockNodes);
    }

    public PELangBasicBlockNode basicBlock(PELangStatementNode bodyNode, int successor) {
        return new PELangSingleSuccessorNode(bodyNode, successor);
    }

    public PELangBasicBlockNode basicBlock(PELangExpressionNode bodyNode, int trueSuccessor, int falseSuccessor) {
        return new PELangDoubleSuccessorNode(bodyNode, trueSuccessor, falseSuccessor);
    }

    public PELangBasicBlockNode basicBlock(PELangExpressionNode valueNode, PELangExpressionNode[] caseValueNodes, int[] caseBodySuccessors, int defaultSuccessor) {
        return new PELangMultiSuccessorNode(valueNode, caseValueNodes, caseBodySuccessors, defaultSuccessor);
    }

    public static final class FunctionHeader {

        private final String name;
        private final String[] args;

        public FunctionHeader(String name, String... args) {
            this.name = name;
            this.args = args;
        }

        public String getName() {
            return name;
        }

        public String[] getArgs() {
            return args;
        }

    }

    public static final class Case {

        private final PELangExpressionNode valueNode;
        private final PELangStatementNode bodyNode;

        public Case(PELangExpressionNode valueNode, PELangStatementNode bodyNode) {
            this.valueNode = valueNode;
            this.bodyNode = bodyNode;
        }

        public PELangExpressionNode getValueNode() {
            return valueNode;
        }

        public PELangStatementNode getBodyNode() {
            return bodyNode;
        }

    }

}
