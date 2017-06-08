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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;


import org.apache.maven.project.MavenProject;
import org.jaffa.plugins.util.Fragments;

import static org.jaffa.plugins.util.Constants.*;

/**
 * Maven Plugin to merge the resource files during Maven Life Cycle Phase of Process Classes and place them under META-INF
 */
@Mojo(name="fragmentmerge", defaultPhase = LifecyclePhase.PROCESS_CLASSES, requiresDependencyResolution = ResolutionScope.RUNTIME)
public class FragmentMergeMojo extends AbstractMojo{




    /**
     * Source for type of resources to look for
     */
    @Parameter
    File resources;



    /**
     * Directory containing the classes and resource files that should be packaged into the JAR.
     */
    @Parameter( defaultValue = "${project.build.outputDirectory}", required = true )
    File classesDirectory;

    /**
     * The {@link {MavenProject}.
     */
    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    MavenProject project;


    /**
     * Target directory .
     */
    File targetDirectory;



    /**
     * Execute method for Fragment Merging
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        getLog().info("Initialize Fragment Merging Process");
        try {

            if(targetDirectory == null) {
                targetDirectory = new File(project.getBuild().getDirectory());
            }

            File applicationResources = new File(classesDirectory + META_INF_LOCATION + PROPERTIES_FILE);
            File dwr = new File(classesDirectory + META_INF_LOCATION + DWR_FILE);
            File jawr = new File(classesDirectory + META_INF_LOCATION + JAWR_FILE);
            File strutsConfig = new File(classesDirectory + META_INF_LOCATION + STRUTS_CONFIG_FILE);
            File tilesDef = new File(classesDirectory + META_INF_LOCATION + TILE_DEFS_FILE);
            File components = new File(classesDirectory + META_INF_LOCATION + COMPONENTS_FILE);

            if (targetDirectory.exists()) {

                //Merging ApplicationResourceFragments
                mergeApplicationResources(applicationResources);

                //Merging Dwr Resource Fragments
                mergeDwrResources(dwr);

                //Merging jawr Resource Fragments
                mergeJawrResources(jawr);

                //Start Merging Struts-Config
                Fragments.writeTag(strutsConfig, STRUTS_CONFIG_START_TAG);
                //Merging StrutsFormBean
                mergeStrutsFormResources(strutsConfig);

                //Merging StrutsGlobalForward
                mergeStrutsGlobalForwardResources(strutsConfig);

                //Merging StrutsConfigAction
                mergeStrutsConfigActionResources(strutsConfig);
                //End Merging Struts-Config
                Fragments.writeTag(strutsConfig, STRUTS_CONFIG_END_TAG);

                //Merging tiles-def
                mergeTileDefsResources(tilesDef);

                //Merging componentDefinitions
                mergeComponentsResources(components);

                //Merge ApplicationRules
                mergeApplicationRules();
            }
        }catch(IOException io){
            getLog().error(io);
        }
        getLog().info("Completed Fragment Merging Process");
    }


    private void mergeApplicationResources(File applicationResources) throws IOException {
        getLog().debug("Starting ApplicationResources Merge Process");
        IOFileFilter applicationResourceFileFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(APPLICATION_RESOURCES), FileFilterUtils.suffixFileFilter(PFRAGMENT));
        Collection<File> applicationResourceFragFiles = FileUtils.listFiles(targetDirectory, applicationResourceFileFilter, TrueFileFilter.INSTANCE);
        Fragments.mergeFragmentResources(applicationResources, applicationResourceFragFiles, APP_RESOURCES_START_TAG, APP_RESOURCES_END_TAG);
        getLog().debug("End of ApplicationResources Merge Process");
    }

    private void mergeDwrResources(File dwr) throws IOException {
        getLog().debug("Starting ApplicationResources Merge Process");
        IOFileFilter dwrResourceFileFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(DWR), FileFilterUtils.suffixFileFilter(XFRAGMENT));
        Collection<File> dwrResourceFragFiles = FileUtils.listFiles(targetDirectory, dwrResourceFileFilter, TrueFileFilter.INSTANCE);
        Fragments.mergeFragmentResources(dwr, dwrResourceFragFiles, DWR_START_TAG, DWR_END_TAG);
        getLog().debug("End of ApplicationResources Merge Process");
    }

    private void mergeJawrResources(File jawr) throws IOException {
        getLog().debug("Starting ApplicationResources Merge Process");
        IOFileFilter jawrResourceFileFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(JAWR), FileFilterUtils.suffixFileFilter(PFRAGMENT));
        Collection<File> jawrResourceFragFiles = FileUtils.listFiles(targetDirectory, jawrResourceFileFilter, TrueFileFilter.INSTANCE);
        Fragments.mergeFragmentResources(jawr, jawrResourceFragFiles, JAWR_START_TAG, JAWR_END_TAG);
        getLog().debug("End of ApplicationResources Merge Process");
    }

    private void mergeStrutsFormResources(File strutsConfig) throws IOException {
        getLog().debug("Starting StrutsFormResources Merge Process");
        IOFileFilter strutsConfigFormBeanResourceFileFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(STRUTS_CONFIG_FORM_BEAN), FileFilterUtils.suffixFileFilter(XFRAGMENT));
        Collection<File> strutsConfigFormBeanResourceFragFiles = FileUtils.listFiles(targetDirectory, strutsConfigFormBeanResourceFileFilter, TrueFileFilter.INSTANCE);
        Fragments.mergeFragmentResources(strutsConfig, strutsConfigFormBeanResourceFragFiles, STRUTS_FORM_START_TAG, STRUTS_FORM_END_TAG);
        getLog().debug("End of StrutsFormResources Merge Process");
    }

    private void mergeStrutsGlobalForwardResources(File strutsConfig) throws IOException {
        getLog().debug("Starting StrutsGlobalForward Merge Process");
        IOFileFilter strutsConfigGlobalForwardResourceFileFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(STRUTS_CONFIG_GLOBAL_FORWARD), FileFilterUtils.suffixFileFilter(XFRAGMENT));
        Collection<File> strutsConfigGlobalForwardResourceFragFiles = FileUtils.listFiles(targetDirectory, strutsConfigGlobalForwardResourceFileFilter, TrueFileFilter.INSTANCE);
        Fragments.mergeFragmentResources(strutsConfig, strutsConfigGlobalForwardResourceFragFiles, STRUTS_GLOBAL_FWD_START_TAG, STRUTS_GLOBAL_FWD_END_TAG);
        getLog().debug("End of StrutsGlobalForward Merge Process");
    }

    private void mergeStrutsConfigActionResources(File strutsConfig) throws IOException {
        getLog().debug("Starting StrutsConfig Merge Process");
        IOFileFilter strutsConfigActionResourceFileFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(STRUTS_CONFIG_ACTION), FileFilterUtils.suffixFileFilter(XFRAGMENT));
        Collection<File> strutsConfigActionResourceFragFiles = FileUtils.listFiles(targetDirectory, strutsConfigActionResourceFileFilter, TrueFileFilter.INSTANCE);
        Fragments.mergeFragmentResources(strutsConfig, strutsConfigActionResourceFragFiles, STRUTS_ACTION_START_TAG, STRUTS_ACTION_END_TAG);
        getLog().debug("End of StrutsConfig Merge Process");
    }

    private void mergeTileDefsResources(File tilesDef) throws IOException {
        getLog().debug("Starting TilesDef Merge Process");
        IOFileFilter componentTilesDefinitionsResourceFileFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(TILE_DEFS), FileFilterUtils.suffixFileFilter(XFRAGMENT));
        Collection<File> componentTilesDefinitionsResourceFragFiles = FileUtils.listFiles(targetDirectory, componentTilesDefinitionsResourceFileFilter, TrueFileFilter.INSTANCE);
        Fragments.mergeFragmentResources(tilesDef, componentTilesDefinitionsResourceFragFiles, STRUTS_TILE_DEFS_START_TAG, STRUTS_TILE_DEFS_END_TAG);
        getLog().debug("End of TilesDef Merge Process");
    }

    private void mergeComponentsResources(File components) throws IOException {
        getLog().debug("Starting Components Merge Process");
        IOFileFilter componentDefinitionsResourceFileFilter = FileFilterUtils.and(FileFilterUtils.prefixFileFilter(COMPONENT_DEFINITIONS), FileFilterUtils.suffixFileFilter(XFRAGMENT));
        Collection<File> componentTilesDefinitionsResourceFragFiles = FileUtils.listFiles(targetDirectory, componentDefinitionsResourceFileFilter, TrueFileFilter.INSTANCE);
        Fragments.mergeFragmentResources(components, componentTilesDefinitionsResourceFragFiles, COMPONENTS_START_TAG, COMPONENTS_END_TAG);
        getLog().debug("End of Components Merge Process");
    }


    private void mergeApplicationRules() throws IOException {
        getLog().debug("Starting ApplicationRules Merge Process");
        IOFileFilter applicationRulesFileFilter = FileFilterUtils.prefixFileFilter(APPLICATION_RULES);
        Collection<File> applicationRulesFiles = FileUtils.listFiles(targetDirectory, applicationRulesFileFilter, TrueFileFilter.INSTANCE);
        for(File applicationRulesFile : applicationRulesFiles){
            File mergedApplicationRule = new File(classesDirectory + META_INF_LOCATION + applicationRulesFile.getName());
            List<File> applicationRuleFilesList = new ArrayList<>();
            applicationRuleFilesList.add(applicationRulesFile);
            Fragments.mergeFragmentResources(mergedApplicationRule, applicationRuleFilesList, APP_RULES_START_TAG, APP_RULES_END_TAG);
        }
        getLog().debug("End of ApplicationResources Merge Process");
    }

}

