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

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.nio.file.FileVisitResult.CONTINUE;

import static org.jaffa.plugins.util.Constants.*;


/**
 * A {@link FileVisitor} implementation that scans files and directories.
 */
public class FileFinder extends SimpleFileVisitor<Path> {

    private PathMatcher matcher;

    /** singleton instances List of FileFinders **/
    private static volatile Map<Path, FileFinder> fileFinders = new HashMap<>();


    private List<Path> files;


    /**
     * Method to return Files based on the pattern
     * @param pattern
     * @return
     */
    public List<Path> getFilteredFiles(String pattern) {
        List<Path> filteredFiles = new ArrayList<>();
        if(files!=null && files.size() > 0){
            for(Path file : files){
                Path name = file.getFileName();
                PathMatcher filteredFileMatcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
                if(name!=null && filteredFileMatcher.matches(name)){
                    filteredFiles.add(file);
                }
            }
        }
        return filteredFiles;
    }

    public void addFile(Path file) {
        if(files == null){
            files = new ArrayList<>();
        }
        files.add(file);
    }

    private FileFinder(){
        super();
    }

    /**
     * Creates an instance of FileFinder, if not already instantiated.
     * @return An instance of the FileFinder.
     */
    public static FileFinder getInstance(Path directory) throws IOException {
        if(!fileFinders.containsKey(directory)){
            createFileFinder(directory);
        }
        return fileFinders.get(directory);
    }

    private static synchronized void createFileFinder(Path directory) throws IOException {
        if(!fileFinders.containsKey(directory)){
            FileFinder fileFinder = new FileFinder("*.{"+PFRAGMENT+","+XFRAGMENT+","+PROPERTIES+","+XML+"}");
            fileFinders.put(directory, fileFinder);
            Files.walkFileTree(directory, fileFinder);
        }
    }

    private FileFinder(String pattern) {
        matcher = FileSystems.getDefault().getPathMatcher("glob:" + pattern);
    }

    // Compares the glob pattern against
    // the file or directory name.
    private void find(Path file) {
        Path name = file.getFileName();
        if (name != null && matcher.matches(name)) {
            addFile(file);
        }
    }

    // Invoke the pattern matching
    // method on each file.
    @Override
    public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
        find(file);
        return CONTINUE;
    }

    // Invoke the pattern matching
    // method on each directory.
    @Override
    public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
        find(dir);
        return CONTINUE;
    }

    @Override
    public FileVisitResult visitFileFailed(Path file, IOException exc) {
        return CONTINUE;
    }
}