/*
 * Copyright (c) 2012, 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * The Universal Permissive License (UPL), Version 1.0
 *
 * Subject to the condition set forth below, permission is hereby granted to any
 * person obtaining a copy of this software, associated documentation and/or
 * data (collectively the "Software"), free of charge and under any and all
 * copyright rights in the Software, and any and all patent rights owned or
 * freely licensable by each licensor hereunder covering either (i) the
 * unmodified Software as contributed to or provided by such licensor, or (ii)
 * the Larger Works (as defined below), to deal in both
 *
 * (a) the Software, and
 *
 * (b) any piece of software and/or hardware listed in the lrgrwrks.txt file if
 * one is included with the Software each a "Larger Work" to which the Software
 * is contributed by such licensors),
 *
 * without restriction, including without limitation the rights to copy, create
 * derivative works of, display, perform, and distribute the Software and make,
 * use, sell, offer for sale, import, export, have made, and have sold the
 * Software and the Larger Work(s), and to sublicense the foregoing rights on
 * either these or other terms.
 *
 * This license is subject to the following condition:
 *
 * The above copyright notice and either this complete permission notice or at a
 * minimum a reference to the UPL must be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package org.perl6.nqp.truffle.nodes.expression;

import com.oracle.truffle.api.CompilerAsserts;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.NodeInfo;
import com.oracle.truffle.api.dsl.Specialization;
import com.oracle.truffle.api.dsl.NodeChildren;
import com.oracle.truffle.api.dsl.NodeChild;

import org.perl6.nqp.truffle.nodes.NQPNode;
import org.perl6.nqp.truffle.nodes.NQPNumNode;

import org.perl6.nqp.truffle.runtime.Coercions;

import org.perl6.nqp.truffle.runtime.NQPCodeRef;
import org.perl6.nqp.truffle.sixmodel.reprs.VMArrayInstance;
import org.perl6.nqp.truffle.runtime.NQPNull;
import org.perl6.nqp.truffle.runtime.NQPHash;


import org.perl6.nqp.dsl.Deserializer;
import org.perl6.nqp.truffle.Debug;

@NodeInfo(shortName = "smart numify")
@NodeChildren({@NodeChild(value="valueNode", type=NQPNode.class)})
public class NQPSmartNumifyNode extends NQPNumNode {

    @Specialization
    protected double doDouble(Double value) {
        return value;
    }

    @Specialization
    protected double doLong(Long value) {
        return (long) value;
    }

    @Specialization
    protected double doString(String value) {
        return Coercions.strToNum(value);
    }

    @Specialization(guards = "isNull(value)")
    protected double doNull(Object value) {
        return 0;
    }

    @Specialization
    protected double doList(VMArrayInstance value) {
        return value.elems();
    }

    @Specialization
    protected double doHash(NQPHash value) {
        return value.elems();
    }

    protected double wrongThing(Object value) {
        throw Debug.wrongThing("can't smart numify", value);
    }

    protected final boolean isNull(Object value) {
        return value == NQPNull.SINGLETON;
    }

    @Deserializer("smart-numify")
    public static NQPNode deserialize(NQPNode valueNode) {
        return NQPSmartNumifyNodeGen.create(valueNode);
    }
}
