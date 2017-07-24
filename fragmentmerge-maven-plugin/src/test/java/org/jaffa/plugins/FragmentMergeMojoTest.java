package org.jaffa.plugins;

/*
 * ====================================================================
 * JAFFA - Java Application Framework For All
 *
 * Copyright (C) 2002 JAFFA Development Group
 *
 *     This library is free software; you can redistribute it and/or
 *     modify it under the terms of the GNU Lesser General Public
 *     License as published by the Free Software Foundation; either
 *     version 2.1 of the License, or (at your option) any later version.
 *
 *     This library is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *     Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public
 *     License along with this library; if not, write to the Free Software
 *     Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 * Redistribution and use of this software and associated documentation ("Software"),
 * with or without modification, are permitted provided that the following conditions are met:
 * 1.	Redistributions of source code must retain copyright statements and notices.
 *         Redistributions must also contain a copy of this document.
 * 2.	Redistributions in binary form must reproduce the above copyright notice,
 * 	this list of conditions and the following disclaimer in the documentation
 * 	and/or other materials provided with the distribution.
 * 3.	The name "JAFFA" must not be used to endorse or promote products derived from
 * 	this Software without prior written permission. For written permission,
 * 	please contact mail to: jaffagroup@yahoo.com.
 * 4.	Products derived from this Software may not be called "JAFFA" nor may "JAFFA"
 * 	appear in their names without prior written permission.
 * 5.	Due credit should be given to the JAFFA Project (http://jaffa.sourceforge.net).
 *
 * THIS SOFTWARE IS PROVIDED "AS IS" AND ANY EXPRESSED OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES
 * OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED.  IN NO EVENT SHALL THE APACHE SOFTWARE FOUNDATION OR
 * ITS CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF
 * USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT
 * OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF
 * SUCH DAMAGE.
 * ====================================================================
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
import static org.jaffa.plugins.definitions.ResourceDefinitions.Constants.*;


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
        Path testTarget = new File(myMojo.targetDirectory+File.separator+"test-classes"+File.separator+"META-INF").toPath();
        FileFinder filesFinder = FileFinder.getInstance(testTarget);
        List<Path> testResourceFiles = filesFinder.getFilteredFiles("*."+PROPERTIES);
        assertNotNull(findEntryInJar(testResourceFiles, APPLICATION_RESOURCES+"."+PROPERTIES));
        assertNotNull(findEntryInJar(testResourceFiles, APPLICATION_RULES_+"ABC."+PROPERTIES));

        testResourceFiles = filesFinder.getFilteredFiles("*."+XML);

        assertNotNull(findEntryInJar(testResourceFiles, DWR+"."+XML));
        assertNotNull(findEntryInJar(testResourceFiles, COMPONENTS+"."+XML));
        assertNotNull(findEntryInJar(testResourceFiles, BUSINESS_FUNCTIONS+"."+XML));
        assertNotNull(findEntryInJar(testResourceFiles, ROLES+"."+XML));
        assertNotNull(findEntryInJar(testResourceFiles, STRUTS_CONFIG+"."+XML));
        assertNotNull(findEntryInJar(testResourceFiles, NAVIGATION+"."+XML));
        assertNotNull(findEntryInJar(testResourceFiles, JAFFA_TRANSACTION_CONFIG+"."+XML));
        assertNotNull(findEntryInJar(testResourceFiles, JAFFA_MESSAGING_CONFIG+"."+XML));
        assertNotNull(findEntryInJar(testResourceFiles, JAFFA_SCHEDULER_CONFIG+"."+XML));
        assertNotNull(findEntryInJar(testResourceFiles, JMS_JNDI_CONFIG+"."+XML));

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

