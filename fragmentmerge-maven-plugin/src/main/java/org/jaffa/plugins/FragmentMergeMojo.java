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
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.*;


import org.apache.maven.project.MavenProject;
import org.jaffa.plugins.definitions.Definition;
import org.jaffa.plugins.definitions.ICustomResourceDefinition;
import org.jaffa.plugins.definitions.ResourceDefinitions;
import org.jaffa.plugins.util.FileFinder;
import org.jaffa.plugins.util.Fragments;

import static org.jaffa.plugins.definitions.ResourceDefinitions.Constants.*;



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
     * fully qualified className of the Custom Resource Definition
     */
    @Parameter
    String customResourceDefinitionClass;

    /**
     * Skip Listed configuration files
     */
    @Parameter
    String[] skipConfigFiles;



    /**
     * Getter for skipConfigFiles parameter
     * @return
     */
    public List<String> getSkipConfigFilesList(){
        return skipConfigFiles !=null ? Arrays.asList(skipConfigFiles) : new ArrayList<String>();
    }

    /* Holds singleton instance of fileFinder */
    private FileFinder fileFinder;

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

            if (targetDirectory.exists()) {

                fileFinder = FileFinder.getInstance(targetDirectory.toPath());

                ResourceDefinitions resourceDefinitions = new ResourceDefinitions();

                if(customResourceDefinitionClass!=null && customResourceDefinitionClass.length() > 0){
                    try {
                        ICustomResourceDefinition customResourceDefinition = (ICustomResourceDefinition) Class.forName(customResourceDefinitionClass).newInstance();

                        if(customResourceDefinition.getFragmentDefinitions()!=null) {
                            resourceDefinitions.addFragmentDefinition(customResourceDefinition.getFragmentDefinitions());
                        }
                        if(customResourceDefinition.getFileDefinitions()!=null) {
                            resourceDefinitions.addFileDefinitions(customResourceDefinition.getFileDefinitions());
                        }

                    } catch (InstantiationException | IllegalAccessException | ClassNotFoundException e) {
                        getLog().error("Unable to instantiate custom resource definition class", e);
                    }
                }

                mergeStrutsDefinitions(resourceDefinitions.getStrutsDefinitions());
                mergeFragmentDefinitions(resourceDefinitions.getFragmentDefinitions());
                mergeFileDefinitions(resourceDefinitions.getFileDefinitions());

                //cleanup any leftover resource files
                cleanUpResources(resourceDefinitions.getFileDefinitions());
            }
        }catch(IOException io){
            getLog().error(io);
        }
        getLog().info("End of Fragment Merging Process");
    }

    private void mergeJawrResources(Definition jawrDefinition) throws IOException {
        Path htmlDirectory = sourceDirectory!=null ? Paths.get(sourceDirectory.getParent() + File.separator + "html") : null;
        File jawr = jawrDefinition.getResourceFromMetaInf(classesDirectory);
        if(htmlDirectory!=null) {
            FileFinder jawrFileFinder = FileFinder.getInstance(htmlDirectory);
            List<Path> jawrResourceFragFiles = jawrFileFinder.getFilteredFiles(jawrDefinition.getConfigFileName());
            if(jawrResourceFragFiles.size() > 0) {
                mergeFragmentResources(jawr, jawrResourceFragFiles, jawrDefinition.getStartTag(), jawrDefinition.getEndTag(), false);
                getLog().info("Completed "+ jawrDefinition.getDefinitionType()+" Merge");
            }
        }
    }

    private void mergeFragmentDefinitions(List<Definition> fragmentDefinitions){
        for(Definition fragmentDefinition : fragmentDefinitions) {
            try {
                if (!getSkipConfigFilesList().contains(fragmentDefinition.getDefinitionType()) && !moveResourceFileIfExist(fragmentDefinition.getFinalResource())) {
                    if(JAWR.equals(fragmentDefinition.getDefinitionType())){
                        mergeJawrResources(fragmentDefinition);
                    }else {
                        mergeFragmentResources(fragmentDefinition);
                    }
                }
            }catch(IOException io){
                getLog().error(io);
            }
        }
    }

    private void mergeFragmentResources(Definition fragmentDefinition) throws IOException {
        File resources = fragmentDefinition.getResourceFromMetaInf(classesDirectory);
        List<Path> fragFiles = fileFinder.getFilteredFiles(fragmentDefinition.getConfigFileName());
        if(fragFiles.size() > 0) {
            mergeFragmentResources(resources, fragFiles, fragmentDefinition.getStartTag(), fragmentDefinition.getEndTag());
            getLog().info("Completed "+ fragmentDefinition.getDefinitionType()+" Merge");
        }
    }


    private void mergeFileDefinitions(List<Definition> fileDefinitions){
        for(Definition fileDefinition : fileDefinitions) {
            try {
                if (!getSkipConfigFilesList().contains(fileDefinition.getDefinitionType())) {
                    mergeFileResources(fileDefinition);
                }
            } catch (IOException io) {
                getLog().error(io);
            }
        }
    }

    private void mergeFileResources(Definition fileDefinition) throws IOException {
        List<Path> definitionFiles = fileFinder.getFilteredFiles(fileDefinition.getConfigFileName());
        if(definitionFiles!=null && definitionFiles.size() > 0) {
            for (Path definitionFile : definitionFiles) {
                File resourceFile = new File(classesDirectory + META_INF_LOCATION + definitionFile.getFileName());
                List<Path> resourceFilesList = new ArrayList<>();
                resourceFilesList.add(definitionFile);
                mergeFragmentResources(resourceFile, resourceFilesList, fileDefinition.getStartTag(), fileDefinition.getEndTag());
            }
            getLog().info("Completed "+ fileDefinition.getDefinitionType() +" Merge");
        }
    }


    private void mergeStrutsDefinitions(List<Definition> strutsDefinitions){
        try {
            File strutsConfig = new File(classesDirectory + META_INF_LOCATION + STRUTS_CONFIG+"."+XML);
            if(!getSkipConfigFilesList().contains(STRUTS_CONFIG) && !moveResourceFileIfExist(STRUTS_CONFIG+"."+XML)) {
                boolean strutsConfigFragsFound = false;
                Fragments.writeTag(strutsConfig, STRUTS_CONFIG_START_TAG);
                for(Definition strutsDefinition : strutsDefinitions){
                    strutsConfigFragsFound |= mergeStrutsResources(strutsDefinition);
                }
                Fragments.writeTag(strutsConfig, STRUTS_CONFIG_END_TAG);
                if(!strutsConfigFragsFound){
                    strutsConfig.delete();
                }
            }
        }catch(IOException io){
            getLog().error(io);
        }
    }

    private boolean mergeStrutsResources(Definition strutsDefinition) throws IOException {
        File strutsConfig = strutsDefinition.getResourceFromMetaInf(classesDirectory);
        List<Path> strutsConfigResourceFragFiles = fileFinder.getFilteredFiles(strutsDefinition.getConfigFileName());
        if(strutsConfigResourceFragFiles.size() > 0) {
            mergeFragmentResources(strutsConfig, strutsConfigResourceFragFiles, strutsDefinition.getStartTag(), strutsDefinition.getEndTag());
            getLog().info("Completed " + strutsDefinition.getDefinitionType() + " Merge");
        }
        return strutsConfigResourceFragFiles!=null && strutsConfigResourceFragFiles.size() > 0;
    }


    private void mergeFragmentResources(File mergedFile, List<Path> fragments, String startTag, String endTag) throws IOException {
        mergeFragmentResources(mergedFile, fragments, startTag, endTag, true);
    }

    private void mergeFragmentResources(File mergedFile, List<Path> fragments, String startTag, String endTag, Boolean deleteFrags) throws IOException {
        getLog().debug("Merging Resources "+fragments+" to "+mergedFile+" with Start and End Tags");
        Fragments.mergeFragmentResources(mergedFile, fragments, startTag, endTag, deleteFrags);
    }

    private boolean moveResourceFileIfExist(String mergedFileName) throws IOException {
        Path resourceFile = Paths.get(classesDirectory+File.separator+RESOURCES, mergedFileName);
        Path mergedResourceFile = Paths.get(classesDirectory+META_INF_LOCATION+mergedFileName);
        boolean resourceFileExist = Files.exists(resourceFile);
        if(resourceFileExist){
            if(!Files.exists(mergedResourceFile.getParent())){
                mergedResourceFile.getParent().toFile().mkdirs();
            }
            Files.move(resourceFile, mergedResourceFile, StandardCopyOption.ATOMIC_MOVE);
        }
        getLog().debug(resourceFileExist? "Not doing fragment merge since Resource File "+ mergedFileName +" exist under resources folder" : "Resource file "+ mergedFileName +" doesnt exist under resource folder. Looking for fragments");
        return resourceFileExist;
    }

    private void cleanUpResources(List<Definition> resourceDefinitions) throws IOException {
        for(Definition definition : resourceDefinitions) {
            Path resourceFile = Paths.get(classesDirectory + File.separator + RESOURCES, definition.getFinalResource());
            if (Files.exists(resourceFile)) {
                Files.delete(resourceFile);
            }
        }
    }
}

