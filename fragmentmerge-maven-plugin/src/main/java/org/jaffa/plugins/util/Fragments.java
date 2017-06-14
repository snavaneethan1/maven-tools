package org.jaffa.plugins.util;

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
import java.util.List;

/**
 * Helper class to merge fragments for fragmentmerge maven plugin
 */
public class Fragments {


    /**
     * General Utility method to merge fragments
     * @param mergedFile
     * @param frag
     * @throws IOException
     */
    private static void merge(File mergedFile, Path frag) throws IOException {
        if(frag!=null && Files.exists(frag)) {
            //FileUtils.writeStringToFile(mergedFile, FileUtils.readFileToString(frag), true);
            createFileIfNotExist(mergedFile);
            Files.write(Paths.get(mergedFile.toURI()), Files.readAllBytes(frag), StandardOpenOption.APPEND);
            //Append new line after each fragment
            Files.write(Paths.get(mergedFile.toURI()), "\n".getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * General Utility method to write tag in the file
     * @param mergedFile
     * @param tag
     * @throws IOException
     */
    public static void writeTag(File mergedFile, String tag) throws IOException {
        if(tag!=null && tag.length() > 0){
            //FileUtils.writeStringToFile(mergedFile, tag, true);
            createFileIfNotExist(mergedFile);
            Files.write(Paths.get(mergedFile.toURI()), tag.getBytes(), StandardOpenOption.APPEND);
        }
    }

    /**
     * Utility method to merge fragment resources without start and end tag
     * @param finalFile
     * @param fragFiles
     * @throws IOException
     */
    public static void mergeFragmentResourcesWithNoTags(File finalFile, List<Path> fragFiles, boolean deleteFrags) throws IOException{
        mergeFragmentResources(finalFile, fragFiles, "", "", deleteFrags);
    }

    /**
     * Utility method to merge fragment resources
     * @param finalFile
     * @param fragFiles
     * @param startTag
     * @param endTag
     * @throws IOException
     */
    public static void mergeFragmentResources(File finalFile, List<Path> fragFiles, String startTag, String endTag) throws IOException{
        mergeFragmentResources(finalFile, fragFiles, startTag, endTag, true);
    }
    /**
     * Utility method to merge fragment resources
     * @param finalFile
     * @param fragFiles
     * @param startTag
     * @param endTag
     * @throws IOException
     */
    public static void mergeFragmentResources(File finalFile, List<Path> fragFiles, String startTag, String endTag, boolean deleteFrags) throws IOException{

        if(fragFiles!=null && fragFiles.size() > 0) {
            //Write start tag to the final merged file
            writeTag(finalFile, startTag);

            //Append each fragments
            for (Path fragFile : fragFiles) {
                merge(finalFile, fragFile);
                if(deleteFrags) {
                    Files.delete(fragFile);
                }
            }

            //Write end tag to the final merged file
            writeTag(finalFile, endTag);
        }
    }

    private static void createFileIfNotExist(File file) throws IOException {
        if(!Files.exists(Paths.get(file.toURI()))) {
            Files.createDirectories(Paths.get(file.toURI()).getParent());
            Files.createFile(Paths.get(file.toURI()));
        }

    }
}
