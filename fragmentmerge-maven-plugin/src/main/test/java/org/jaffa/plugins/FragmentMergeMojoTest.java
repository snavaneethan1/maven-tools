package org.jaffa.plugins;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;


import org.apache.commons.io.FileUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;

import org.apache.maven.execution.MavenSession;

import org.apache.maven.project.MavenProject;
import org.jaffa.plugins.util.DwrFragments;
import org.jaffa.plugins.util.AppResourceFragments;
import org.jaffa.plugins.util.JawrResourceFragments;
import java.io.File;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;



public class FragmentMergeMojoTest extends AbstractMojoTestCase{

    protected void setUp() throws Exception {
        // required
        super.setUp();
    }

    protected void tearDown() throws Exception {
        // required
        super.tearDown();
    }

    @Test
    public void test() throws Exception {
        File pom = getTestFile("src/test/resources/unit/testpom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        FragmentMergeMojo myMojo = (FragmentMergeMojo) lookupMojo("fragmentmerge", pom);
        assertNotNull(myMojo);
        myMojo.execute();

        //@todo implement test for merged files
    }
}

