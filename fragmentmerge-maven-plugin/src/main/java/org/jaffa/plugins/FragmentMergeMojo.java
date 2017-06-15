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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;


import org.apache.maven.project.MavenProject;
import org.jaffa.plugins.util.FileFinder;
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
     * Directory containing the source files
     */
    @Parameter( defaultValue = "${project.build.sourceDirectory}", required = true )
    File sourceDirectory;
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
     * Skip Tags For configuration files
     */
    @Parameter
    String[] skipTagForConfigFiles;

    /**
     * Skip Listed configuration files
     */
    @Parameter
    String[] skipConfigFiles;


    /**
     * Getter for skipTagForConfigFiles parameter
     * @return
     */
    public List<String> getSkipTagForConfigFilesList(){
        return skipTagForConfigFiles !=null ? Arrays.asList(skipTagForConfigFiles) : new ArrayList<String>();
    }


    /**
     * Getter for skipConfigFiles parameter
     * @return
     */
    public List<String> getSkipConfigFilesList(){
        return skipConfigFiles !=null ? Arrays.asList(skipConfigFiles) : new ArrayList<String>();
    }

    /**
     * Execute method for Fragment Merging
     * @throws MojoExecutionException
     */
    public void execute() throws MojoExecutionException {
        getLog().info("Initialize Fragment Merging Process");
        try {

            if(targetDirectory == null && project!=null) {
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
                if(!getSkipConfigFilesList().contains(APPLICATION_RESOURCES)) {
                    mergeApplicationResources(applicationResources);
                }

                //Merging Dwr Resource Fragments
                if(!getSkipConfigFilesList().contains(DWR)) {
                    mergeDwrResources(dwr);
                }

                //Merging jawr Resource Fragments
                if(!getSkipConfigFilesList().contains(JAWR)) {
                    mergeJawrResources(jawr);
                }

                //Start Merging Struts-Config
                if(!getSkipConfigFilesList().contains(STRUTS_CONFIG)) {
                    boolean strutsConfigFragsFound = false;
                    Fragments.writeTag(strutsConfig, STRUTS_CONFIG_START_TAG);
                    //Merging StrutsFormBean
                    strutsConfigFragsFound = mergeStrutsFormResources(strutsConfig);

                    //Merging StrutsGlobalForward
                    strutsConfigFragsFound |= mergeStrutsGlobalForwardResources(strutsConfig);

                    //Merging StrutsConfigAction
                    strutsConfigFragsFound |= mergeStrutsConfigActionResources(strutsConfig);

                    //End Merging Struts-Config
                    Fragments.writeTag(strutsConfig, STRUTS_CONFIG_END_TAG);

                    if(!strutsConfigFragsFound){
                        strutsConfig.delete();
                    }
                    //Merging tiles-def
                    mergeTileDefsResources(tilesDef);
                }

                if(!getSkipConfigFilesList().contains(COMPONENTS)) {
                    //Merging componentDefinitions
                    mergeComponentsResources(components);
                }

                if(!getSkipConfigFilesList().contains(APPLICATION_RULES)) {
                    //Merge ApplicationRules
                    mergeApplicationRules();
                }

                if(!getSkipConfigFilesList().contains(BUSINESS_FUNCTIONS)) {
                    //Merge business-functions
                    mergeBusinessFunctions();
                }

                if(!getSkipConfigFilesList().contains(ROLES)) {
                    //Merge roles
                    mergeRoles();
                }

                if(!getSkipConfigFilesList().contains(NAVIGATION)) {
                    //Merge Navigation
                    mergeNavigation();
                }
            }
        }catch(IOException io){
            getLog().error(io);
        }
        getLog().info("Completed Fragment Merging Process");
    }


    private void mergeApplicationResources(File applicationResources) throws IOException {
        getLog().debug("Starting ApplicationResources Merge Process");
        FileFinder appResourcesFinder = new FileFinder("*"+APPLICATION_RESOURCES+"*."+PFRAGMENT);
        Files.walkFileTree(targetDirectory.toPath(), appResourcesFinder);
        List<Path> applicationResourceFragFiles = appResourcesFinder.getFiles();
        mergeFragmentResources(applicationResources, applicationResourceFragFiles, APP_RESOURCES_START_TAG, APP_RESOURCES_END_TAG);
        getLog().debug("End of ApplicationResources Merge Process");
    }

    private void mergeDwrResources(File dwr) throws IOException {
        getLog().debug("Starting ApplicationResources Merge Process");
        FileFinder dwrResourcesFinder = new FileFinder(DWR+"*."+XFRAGMENT);
        Files.walkFileTree(targetDirectory.toPath(), dwrResourcesFinder);
        List<Path> dwrResourceFragFiles = dwrResourcesFinder.getFiles();
        mergeFragmentResources(dwr, dwrResourceFragFiles, DWR_START_TAG, DWR_END_TAG, getSkipTagForConfigFilesList().contains(DWR), true);
        getLog().debug("End of ApplicationResources Merge Process");
    }

    private void mergeJawrResources(File jawr) throws IOException {
        getLog().debug("Starting ApplicationResources Merge Process");
        FileFinder jawrResourceFinder = new FileFinder(JAWR+"*."+PFRAGMENT);
        Path htmlDirectory = sourceDirectory!=null ? Paths.get(sourceDirectory.getParent() + File.separator + "html") : null;
        if(htmlDirectory!=null) {
            Files.walkFileTree(htmlDirectory, jawrResourceFinder);
            List<Path> jawrResourceFragFiles = jawrResourceFinder.getFiles();
            mergeFragmentResources(jawr, jawrResourceFragFiles, JAWR_START_TAG, JAWR_END_TAG, false, false);
        }
        getLog().debug("End of ApplicationResources Merge Process");
    }

    private boolean mergeStrutsFormResources(File strutsConfig) throws IOException {
        getLog().debug("Starting StrutsFormResources Merge Process");
        FileFinder strutsConfigFormBeanResourceFinder = new FileFinder(STRUTS_CONFIG_FORM_BEAN+"*."+XFRAGMENT);
        Files.walkFileTree(targetDirectory.toPath(), strutsConfigFormBeanResourceFinder);
        List<Path> strutsConfigFormBeanResourceFragFiles = strutsConfigFormBeanResourceFinder.getFiles();
        mergeFragmentResources(strutsConfig, strutsConfigFormBeanResourceFragFiles, STRUTS_FORM_START_TAG, STRUTS_FORM_END_TAG);
        getLog().debug("End of StrutsFormResources Merge Process");
        return strutsConfigFormBeanResourceFragFiles!=null && strutsConfigFormBeanResourceFragFiles.size() > 0;
    }

    private boolean mergeStrutsGlobalForwardResources(File strutsConfig) throws IOException {
        getLog().debug("Starting StrutsGlobalForward Merge Process");
        FileFinder strutsConfigGlobalForwardResourceFinder = new FileFinder(STRUTS_CONFIG_GLOBAL_FORWARD+"*."+XFRAGMENT);
        Files.walkFileTree(targetDirectory.toPath(), strutsConfigGlobalForwardResourceFinder);
        List<Path> strutsConfigGlobalForwardResourceFragFiles = strutsConfigGlobalForwardResourceFinder.getFiles();
        mergeFragmentResources(strutsConfig, strutsConfigGlobalForwardResourceFragFiles, STRUTS_GLOBAL_FWD_START_TAG, STRUTS_GLOBAL_FWD_END_TAG);
        getLog().debug("End of StrutsGlobalForward Merge Process");
        return strutsConfigGlobalForwardResourceFragFiles!=null && strutsConfigGlobalForwardResourceFragFiles.size() > 0;
    }

    private boolean mergeStrutsConfigActionResources(File strutsConfig) throws IOException {
        getLog().debug("Starting StrutsConfig Merge Process");
        FileFinder strutsConfigActionResourceResourceFinder = new FileFinder(STRUTS_CONFIG_ACTION+"*."+XFRAGMENT);
        Files.walkFileTree(targetDirectory.toPath(), strutsConfigActionResourceResourceFinder);
        List<Path> strutsConfigActionResourceFragFiles = strutsConfigActionResourceResourceFinder.getFiles();
        mergeFragmentResources(strutsConfig, strutsConfigActionResourceFragFiles, STRUTS_ACTION_START_TAG, STRUTS_ACTION_END_TAG);
        getLog().debug("End of StrutsConfig Merge Process");
        return strutsConfigActionResourceFragFiles!=null && strutsConfigActionResourceFragFiles.size() > 0;
    }

    private void mergeTileDefsResources(File tilesDef) throws IOException {
        getLog().debug("Starting TilesDef Merge Process");
        FileFinder componentTilesDefinitionsResourceFinder = new FileFinder(TILE_DEFS+"*."+XFRAGMENT);
        Files.walkFileTree(targetDirectory.toPath(), componentTilesDefinitionsResourceFinder);
        List<Path> componentTilesDefinitionsResourceFragFiles = componentTilesDefinitionsResourceFinder.getFiles();
        mergeFragmentResources(tilesDef, componentTilesDefinitionsResourceFragFiles, STRUTS_TILE_DEFS_START_TAG, STRUTS_TILE_DEFS_END_TAG);
        getLog().debug("End of TilesDef Merge Process");
    }

    private void mergeComponentsResources(File components) throws IOException {
        getLog().debug("Starting Components Merge Process");
        FileFinder componentDefinitionsResourceFinder = new FileFinder(COMPONENT_DEFINITIONS+"*."+XFRAGMENT);
        Files.walkFileTree(targetDirectory.toPath(), componentDefinitionsResourceFinder);
        List<Path> componentTilesDefinitionsResourceFragFiles = componentDefinitionsResourceFinder.getFiles();
        mergeFragmentResources(components, componentTilesDefinitionsResourceFragFiles, COMPONENTS_START_TAG, COMPONENTS_END_TAG);
        getLog().debug("End of Components Merge Process");
    }


    private void mergeApplicationRules() throws IOException {
        getLog().debug("Starting ApplicationRules Merge Process");
        FileFinder applicationRulesResourceFinder = new FileFinder(APPLICATION_RULES_ +"*."+PROPERTIES);
        Files.walkFileTree(targetDirectory.toPath(), applicationRulesResourceFinder);
        List<Path> applicationRulesFiles = applicationRulesResourceFinder.getFiles();
        if(applicationRulesFiles!=null && applicationRulesFiles.size() > 0) {
            for (Path applicationRulesFile : applicationRulesFiles) {
                File mergedApplicationRule = new File(classesDirectory + META_INF_LOCATION + applicationRulesFile.getFileName());
                List<Path> applicationRuleFilesList = new ArrayList<>();
                applicationRuleFilesList.add(applicationRulesFile);
                mergeFragmentResources(mergedApplicationRule, applicationRuleFilesList, EMPTY_START_TAG, EMPTY_END_TAG);
            }
        }
        getLog().debug("End of ApplicationResources Merge Process");
    }

    private void mergeBusinessFunctions() throws IOException {
        getLog().debug("Starting Business Functions Merge Process");
        FileFinder businessFunctionResourceFinder = new FileFinder(BUSINESS_FUNCTIONS+"*."+XML);
        Files.walkFileTree(targetDirectory.toPath(), businessFunctionResourceFinder);
        List<Path> businessFunctionFiles = businessFunctionResourceFinder.getFiles();
        if(businessFunctionFiles!=null && businessFunctionFiles.size() > 0) {
            for (Path businessFunctionFile : businessFunctionFiles) {
                File mergedBusinessFunctionFile = new File(classesDirectory + META_INF_LOCATION + businessFunctionFile.getFileName());
                List<Path> businessFunctionFilesList = new ArrayList<>();
                businessFunctionFilesList.add(businessFunctionFile);
                mergeFragmentResources(mergedBusinessFunctionFile, businessFunctionFilesList, EMPTY_START_TAG, EMPTY_END_TAG);
            }
        }
        getLog().debug("End of Business Functions Merge Process");
    }

    private void mergeRoles() throws IOException {
        getLog().debug("Starting Roles Merge Process");
        FileFinder rolesResourceFinder = new FileFinder(ROLES+"*."+XML);
        Files.walkFileTree(targetDirectory.toPath(), rolesResourceFinder);
        List<Path> rolesFiles = rolesResourceFinder.getFiles();
        if(rolesFiles!=null && rolesFiles.size() > 0) {
            for (Path rolesFile : rolesFiles) {
                File mergedRoles = new File(classesDirectory + META_INF_LOCATION + rolesFile.getFileName());
                List<Path> rolesFilesList = new ArrayList<>();
                rolesFilesList.add(rolesFile);
                mergeFragmentResources(mergedRoles, rolesFilesList, EMPTY_START_TAG, EMPTY_END_TAG);
            }
        }
        getLog().debug("End of Roles Merge Process");
    }

    private void mergeNavigation() throws IOException {
        getLog().debug("Starting Navigation Merge Process");
        FileFinder navigationResourceFinder = new FileFinder(NAVIGATION+"*."+XML);
        Files.walkFileTree(targetDirectory.toPath(), navigationResourceFinder);
        List<Path> navigationFiles = navigationResourceFinder.getFiles();
        if(navigationFiles!=null && navigationFiles.size() > 0) {
            for (Path navigationFile : navigationFiles) {
                File mergedNavigation = new File(classesDirectory + META_INF_LOCATION + navigationFile.getFileName());
                List<Path> navigationFilesList = new ArrayList<>();
                navigationFilesList.add(navigationFile);
                mergeFragmentResources(mergedNavigation, navigationFilesList, EMPTY_START_TAG, EMPTY_END_TAG);
            }
        }
        getLog().debug("End of Navigation Merge Process");
    }

    private void mergeFragmentResources(File mergedFile, List<Path> fragments, String startTag, String endTag) throws IOException {
        mergeFragmentResources(mergedFile, fragments, startTag, endTag, false, true);
    }

    private void mergeFragmentResources(File mergedFile, List<Path> fragments, String startTag, String endTag, Boolean skipTags, Boolean deleteFrags) throws IOException {
        if(skipTags!=null && Boolean.TRUE.equals(skipTags)){
            Fragments.mergeFragmentResourcesWithNoTags(mergedFile, fragments, deleteFrags);
        }else{
            Fragments.mergeFragmentResources(mergedFile, fragments, startTag, endTag, deleteFrags);
        }
    }
}

