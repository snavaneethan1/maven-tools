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

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;


import org.apache.maven.execution.DefaultMavenExecutionRequest;
import org.apache.maven.execution.MavenExecutionRequest;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuilder;
import org.apache.maven.project.ProjectBuildingRequest;
import org.jaffa.plugins.util.FileFinder;
import org.junit.Test;

import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;



/**
 * Unit Test for FragmentMergeMojo
 */
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
        File pom = getTestFile("testpom.xml");
        assertNotNull(pom);
        assertTrue(pom.exists());
        FragmentMergeMojo myMojo = (FragmentMergeMojo) lookupMojo("fragmentmerge", pom);
        MavenProject testProject = readMavenProject(pom);
        myMojo.targetDirectory = new File(testProject.getBuild().getTestOutputDirectory()).getParentFile();
        myMojo.classesDirectory = new File(testProject.getBuild().getTestOutputDirectory());
        assertNotNull(myMojo);
        myMojo.execute();
        FileFinder filesFinder = new FileFinder("*.properties");
        Path testTarget = new File(myMojo.targetDirectory+File.separator+"test-classes"+File.separator+"META-INF").toPath();
        Files.walkFileTree(testTarget, filesFinder);
        List<Path> testResourceFiles = filesFinder.getFiles();
        assertNotNull(findEntryInJar(testResourceFiles, "ApplicationResources.properties"));
        assertNotNull(findEntryInJar(testResourceFiles, "ApplicationRules_ABC.properties"));

        filesFinder = new FileFinder("*.xml");
        Files.walkFileTree(testTarget, filesFinder);
        testResourceFiles = filesFinder.getFiles();

        assertNotNull(findEntryInJar(testResourceFiles, "dwr.xml"));
        assertNotNull(findEntryInJar(testResourceFiles, "components.xml"));
        assertNotNull(findEntryInJar(testResourceFiles, "business-functions.xml"));
        assertNotNull(findEntryInJar(testResourceFiles, "roles.xml"));
        assertNotNull(findEntryInJar(testResourceFiles, "struts-config.xml"));
        assertNotNull(findEntryInJar(testResourceFiles, "navigation.xml"));

    }

    private MavenProject readMavenProject(File pom) throws Exception {
        MavenExecutionRequest request = new DefaultMavenExecutionRequest();
        request.setBaseDirectory(pom.getParentFile());
        ProjectBuildingRequest configuration = request.getProjectBuildingRequest();
        MavenProject project = lookup(ProjectBuilder.class).build(pom, configuration).getProject();
        return project;
    }

    private Path findEntryInJar(List<Path> testResourceFiles, String entry) throws IOException {
        for(Path testResourceFile : testResourceFiles){
            if(testResourceFile.getFileName().toString().equals(entry))
                return testResourceFile;
        }
        return null;
    }
}

