/*
 * $Id$
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.tiles.autotag.freemarker;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tiles.autotag.core.runtime.ModelBody;
import org.apache.tiles.autotag.model.TemplateClass;
import org.apache.tiles.autotag.model.TemplateMethod;
import org.apache.tiles.autotag.model.TemplateParameter;
import org.apache.tiles.autotag.model.TemplateSuite;
import org.apache.tiles.request.Request;
import org.apache.velocity.app.VelocityEngine;
import org.junit.Test;

/**
 * Tests {@link TLDGenerator}.
 *
 * @version $Rev$ $Date$
 */
public class FMModelRepositoryGeneratorTest {

    /**
     * Test method for {@link FMModelRepositoryGenerator#generate(File, String, TemplateSuite, java.util.Map)}.
     * @throws Exception If something goes wrong.
     */
    @Test
    public void testGenerate() throws Exception {
        Properties props = new Properties();
        InputStream propsStream = getClass().getResourceAsStream("/org/apache/tiles/autotag/velocity.properties");
        props.load(propsStream);
        propsStream.close();
        VelocityEngine velocityEngine = new VelocityEngine(props);

        FMModelRepositoryGenerator generator = new FMModelRepositoryGenerator(velocityEngine);
        File file = File.createTempFile("autotag", null);
        file.delete();
        file.mkdir();
        file.deleteOnExit();
        TemplateSuite suite = new TemplateSuite("tldtest", "Test for TLD docs.");

        List<TemplateParameter> params = new ArrayList<TemplateParameter>();
        TemplateParameter param = new TemplateParameter("one", "one", "java.lang.String", null, true);
        param.setDocumentation("Parameter one.");
        params.add(param);
        param = new TemplateParameter("two", "two", "int", null, false);
        param.setDocumentation("Parameter two.");
        params.add(param);
        param = new TemplateParameter("three", "three", "long", null, false);
        param.setDocumentation("Parameter three.");
        params.add(param);
        param = new TemplateParameter("request", "request", Request.class.getName(), null, false);
        param.setDocumentation("The request.");
        params.add(param);
        param = new TemplateParameter("modelBody", "modelBody", ModelBody.class.getName(), null, false);
        param.setDocumentation("The body.");
        params.add(param);
        TemplateMethod executeMethod = new TemplateMethod("execute", params);

        TemplateClass clazz = new TemplateClass("org.apache.tiles.autotag.template.DoStuffTemplate",
                "doStuff", "DoStuff", executeMethod);
        clazz.setDocumentation("Documentation of the DoStuff class");

        suite.addTemplateClass(clazz);
        params = new ArrayList<TemplateParameter>();
        param = new TemplateParameter("one", "one", "java.lang.Double", null, true);
        param.setDocumentation("Parameter one.");
        params.add(param);
        param = new TemplateParameter("two", "two", "float", null, false);
        param.setDocumentation("Parameter two.");
        params.add(param);
        param = new TemplateParameter("three", "three", "java.util.Date", null, false);
        param.setDocumentation("Parameter three.");
        params.add(param);
        param = new TemplateParameter("request", "request", Request.class.getName(), null, false);
        param.setDocumentation("The request.");
        params.add(param);
        executeMethod = new TemplateMethod("execute", params);

        clazz = new TemplateClass("org.apache.tiles.autotag.template.DoStuffNoBodyTemplate",
                "doStuffNoBody", "DoStuffNoBody", executeMethod);
        clazz.setDocumentation("Documentation of the DoStuffNoBody class");

        suite.addTemplateClass(clazz);

        generator.generate(file, "org.apache.tiles.autotag.freemarker.test", suite, null);

        InputStream expected = getClass()
                .getResourceAsStream(
                        "/org/apache/tiles/autotag/freemarker/test/TldtestFMModelRepository.javat");
        File effectiveFile = new File(file, "/org/apache/tiles/autotag/freemarker/test/TldtestFMModelRepository.java");
        assertTrue(effectiveFile.exists());
        InputStream effective = new FileInputStream(effectiveFile);
        assertTrue(IOUtils.contentEquals(effective, expected));
        effective.close();
        expected.close();

        FileUtils.deleteDirectory(file);
    }

}