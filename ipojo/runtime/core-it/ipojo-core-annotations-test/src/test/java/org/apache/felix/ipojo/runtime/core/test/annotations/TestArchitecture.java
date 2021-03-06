/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.apache.felix.ipojo.runtime.core.test.annotations;

import org.apache.felix.ipojo.metadata.Element;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class TestArchitecture extends Common {

    @Test
    public void testArch() {
        Element meta = ipojoHelper.getMetadata(getTestBundle(), "org.apache.felix.ipojo.runtime.core.test.components" +
                ".Arch");
        String arch = meta.getAttribute("architecture");
        assertNotNull("Architecture exists ", arch);
        assertEquals("Architecture value", "true", arch);
    }

    @Test
    public void testNoArch() {
        Element meta = ipojoHelper.getMetadata(getTestBundle(), "org.apache.felix.ipojo.runtime.core.test.components" +
                ".NoArch");
        String arch = meta.getAttribute("architecture");
        assertNotNull("Architecture exists ", arch);
        assertEquals("Architecture value", "false", arch);
    }


}

