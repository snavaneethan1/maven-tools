package org.jaffa.plugins.definitions;

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
import java.util.*;

import static org.jaffa.plugins.definitions.ResourceDefinitions.Constants.*;

public class ResourceDefinitions {

    /** List that holds struts definitions for each configuration element **/
    public List<Definition> strutsDefinitions = new ArrayList<>();

    /** List that holds fragment definitions for each configuration element **/
    public List<Definition> fragmentDefinitions = new ArrayList<>();

    /** List that holds file definitions for each configuration element **/
    public List<Definition> fileDefinitions = new ArrayList<>();

    public ResourceDefinitions(){
        buildDefinitions();
    }

    /**
     * Builds Definition for each configuration type and adds them to the definitions list.
     */
    private void buildDefinitions(){


        //StrutsForm
        strutsDefinitions.add(new Definition(STRUTS_CONFIG_FORM_BEAN, STRUTS_CONFIG+"."+XML, STRUTS_CONFIG_FORM_BEAN+"*."+XFRAGMENT, STRUTS_FORM_START_TAG, STRUTS_FORM_END_TAG));

        //StrutsGlobalForward
        strutsDefinitions.add(new Definition(STRUTS_CONFIG_GLOBAL_FORWARD, STRUTS_CONFIG+"."+XML, STRUTS_CONFIG_GLOBAL_FORWARD+"*."+XFRAGMENT, STRUTS_GLOBAL_FWD_START_TAG, STRUTS_GLOBAL_FWD_END_TAG));

        //StrutsAction
        strutsDefinitions.add(new Definition(STRUTS_CONFIG_ACTION, STRUTS_CONFIG+"."+XML, STRUTS_CONFIG_ACTION+"*."+XFRAGMENT, STRUTS_ACTION_START_TAG, STRUTS_ACTION_END_TAG));



        //Tiles Definitions
        fragmentDefinitions.add(new Definition(COMPONENT_TILES_DEFS, TILE_DEFS+"."+XML, COMPONENT_TILES_DEFS+"*."+XFRAGMENT, STRUTS_TILE_DEFS_START_TAG, STRUTS_TILE_DEFS_END_TAG));

        //ApplicationResources
        fragmentDefinitions.add(new Definition(APPLICATION_RESOURCES, APPLICATION_RESOURCES+"."+PROPERTIES, "*"+APPLICATION_RESOURCES+"*."+PFRAGMENT, APP_RESOURCES_START_TAG, APP_RESOURCES_END_TAG));

        //Dwr
        fragmentDefinitions.add(new Definition(DWR, DWR+"."+XML, DWR+"*."+XFRAGMENT, DWR_START_TAG, DWR_END_TAG));

        //Jawr
        fragmentDefinitions.add(new Definition(JAWR, JAWR+"."+PROPERTIES, JAWR+"*."+PFRAGMENT, JAWR_START_TAG, JAWR_END_TAG));

        //ComponentDefinitions
        fragmentDefinitions.add(new Definition(COMPONENT_DEFINITIONS, COMPONENTS+"."+XML, COMPONENT_DEFINITIONS+"*."+XFRAGMENT, COMPONENTS_START_TAG, COMPONENTS_END_TAG));

        //jaffa-transaction-config
        fragmentDefinitions.add(new Definition(JAFFA_TRANSACTION_CONFIG, JAFFA_TRANSACTION_CONFIG+"."+XML, JAFFA_TRANSACTION_CONFIG+"*."+XFRAGMENT, JAFFA_CONFIG_START_TAG, JAFFA_CONFIG_END_TAG));

        //jaffa-messaging-config
        fragmentDefinitions.add(new Definition(JAFFA_MESSAGING_CONFIG, JAFFA_MESSAGING_CONFIG+"."+XML, JAFFA_MESSAGING_CONFIG+"*."+XFRAGMENT, JAFFA_CONFIG_START_TAG, JAFFA_CONFIG_END_TAG));

        //jaffa-scheduler-config
        fragmentDefinitions.add(new Definition(JAFFA_SCHEDULER_CONFIG, JAFFA_SCHEDULER_CONFIG+"."+XML, JAFFA_SCHEDULER_CONFIG+"*."+XFRAGMENT, JAFFA_CONFIG_START_TAG, JAFFA_CONFIG_END_TAG));

        //soa-events
        fragmentDefinitions.add(new Definition(SOA_EVENTS, SOA_EVENTS+"."+XML, SOA_EVENTS+"*."+XFRAGMENT, SOA_EVENTS_START_TAG, SOA_EVENTS_END_TAG));


        //jms-jndi-config
        fileDefinitions.add(new Definition(JMS_JNDI_CONFIG, JMS_JNDI_CONFIG+"."+XML, JMS_JNDI_CONFIG+"*."+XML, EMPTY_START_TAG, EMPTY_END_TAG));

        //ApplicationRules
        fileDefinitions.add(new Definition(APPLICATION_RULES_, APPLICATION_RULES_+"."+PROPERTIES, "*"+APPLICATION_RULES_+"*."+PROPERTIES, EMPTY_START_TAG, EMPTY_END_TAG));

        //roles
        fileDefinitions.add(new Definition(ROLES, ROLES+"."+XML, ROLES+"*."+XML, EMPTY_START_TAG, EMPTY_END_TAG));

        //business-functions
        fileDefinitions.add(new Definition(BUSINESS_FUNCTIONS, BUSINESS_FUNCTIONS+"."+XML, BUSINESS_FUNCTIONS+"*."+XML, EMPTY_START_TAG, EMPTY_END_TAG));

        //navigation
        fileDefinitions.add(new Definition(NAVIGATION, NAVIGATION+"."+XML, NAVIGATION+"*."+XML, EMPTY_START_TAG, EMPTY_END_TAG));

    }

    public List<Definition> getStrutsDefinitions(){
        return strutsDefinitions;
    }

    public List<Definition> getFragmentDefinitions(){
        return fragmentDefinitions;
    }

    public List<Definition> getFileDefinitions(){
        return fileDefinitions;
    }


    public void addFileDefinitions(List<Definition> definitions){
        fileDefinitions.addAll(definitions);
    }

    public void addFragmentDefinition(List<Definition> definitions){
        fragmentDefinitions.addAll(definitions);
    }



    /**
     * Definition of all the different constants used by FragmentMergeMojo
     */
    public static class Constants {

        /**
         * Default Start Tag for Struts-Config Action mappings merged file
         */
        public static String STRUTS_ACTION_START_TAG = "\n  <!-- ========== Action Mapping Definitions ============================== -->\n" +
                "  <action-mappings>";

        /**
         * Default End Tag for Struts-Config Action mappings merged files
         */
        public static String STRUTS_ACTION_END_TAG = "\n  </action-mappings>";

        /**
         * Default Start Tag for Struts-Config form bean merged file
         */
        public static String STRUTS_FORM_START_TAG = "\n  <!-- ========== Form Bean Definitions =================================== -->\n" +
                "  <form-beans>";

        /**
         * Default End Tag for Struts-Config form bean merged files
         */
        public static String STRUTS_FORM_END_TAG = "\n  </form-beans>";

        /**
         * Default Start Tag for Struts-Config merged file
         */
        public static String STRUTS_CONFIG_START_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "\n" +
                "<!DOCTYPE struts-config PUBLIC\n" +
                "          \"-//Apache Software Foundation//DTD Struts Configuration 1.2//EN\"\n" +
                "          \"http://struts.apache.org/dtds/struts-config_1_2.dtd\">\n" +
                "\n" +
                "<struts-config>";

        /**
         * Default End Tag for Struts-Config merged files
         */
        public static String STRUTS_CONFIG_END_TAG = "\n</struts-config>";

        /**
         * Default Start Tag for Struts-Config global forward merged file
         */
        public static String STRUTS_GLOBAL_FWD_START_TAG = "\n  <!-- ========== Global Forward Definitions ============================== -->\n" +
                "  <global-forwards>";

        /**
         * Default End Tag for Struts-Config global forward merged files
         */
        public static String STRUTS_GLOBAL_FWD_END_TAG = "\n  </global-forwards>";

        /**
         * Default Start Tag for tile-defs merged file
         */
        public static String STRUTS_TILE_DEFS_START_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
                "\n" +
                " <!DOCTYPE tiles-definitions PUBLIC\n" +
                "       \"-//Apache Software Foundation//DTD Tiles Configuration 1.1//EN\"\n" +
                "       \"http://jakarta.apache.org/struts/dtds/tiles-config_1_1.dtd\">\n" +
                "\n" +
                "<tiles-definitions>\n";

        /**
         * Default End Tag for tile-defs merged files
         */
        public static String STRUTS_TILE_DEFS_END_TAG = "\n</tiles-definitions>";

        /**
         * Default Start Tag for JAWR merged file
         */
        public static String JAWR_START_TAG = "#---------------------------\n #Merging Jawr from pfragments START \n#---------------------------\n";

        /**
         * Default End Tag for JAWR merged files
         */
        public static String JAWR_END_TAG = "\n#---------------------------\n #Merging Jawr from pfragments END \n#---------------------------";

        /**
         * Start Tag to make the merged dwr.xml, a valid xml literal
         */
        public static String DWR_START_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<!DOCTYPE dwr PUBLIC \"-//GetAhead Limited//DTD Direct Web Remoting 2.0//EN\" \"http://getahead.org/dwr/dwr20.dtd\">\n" +
                "<dwr>\n" +
                "  <allow>\n";

        /**
         * End Tag to make the merged dwr.xml, a valid xml literal
         */
        public static String DWR_END_TAG = "  </allow>\n" +
                "</dwr>";

        /**
         * Default start tag for merged ApplicationResources.properties
         */
        public static String APP_RESOURCES_START_TAG = "## Merging Resources from pfragments START ##\n";
        /**
         * Default end tag for merged ApplicationResources.properties
         */
        public static String APP_RESOURCES_END_TAG = "\n## Merging Resources from pfragments END ##";

        /**
         * Default start tag for merged ApplicationRules_{}.properties, business_functions_{}.xml, roles_{}.xml
         */
        public static String EMPTY_START_TAG = "";
        /**
         * Default end tag for merged ApplicationRules_{}.properties, business_functions_{}.xml, roles_{}.xml
         */
        public static String EMPTY_END_TAG = "";

        /**
         * Default start tag for merged components.xml
         */
        public static String COMPONENTS_START_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<components>\n";
        /**
         * Default end tag for merged components.xml
         */
        public static String COMPONENTS_END_TAG = "\n</components>";

        /**
         * Default start tag for merged jaffa-transaction-config.xml, jaffa-messaging-config.xml, jaffa-scheduler-config.xml
         */
        public static String JAFFA_CONFIG_START_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                "<config>\n";
        /**
         * Default end tag for merged jaffa-transaction-config.xml, jaffa-messaging-config.xml, jaffa-scheduler-config.xml
         */
        public static String JAFFA_CONFIG_END_TAG = "\n</config>";

        /**
         * Default start tag for merged soa-events.xml
         */
        public static String SOA_EVENTS_START_TAG = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<soa-events>\n";
        /**
         * Default end tag for merged soa-events.xml
         */
        public static String SOA_EVENTS_END_TAG = "\n</soa-events>";

        //Meta-Inf location inside jar
        public static final String META_INF_LOCATION = File.separator+"META-INF"+File.separator;

        //Struts-Config
        public static final String STRUTS_CONFIG = "struts-config";

        //Tile-Defs
        public static final String TILE_DEFS = "tiles-defs";

        //components
        public static final String COMPONENTS = "components";

        //pfragment
        public static final String PFRAGMENT = "pfragment";

        //xfragment
        public static final String XFRAGMENT = "xfragment";

        //xml
        public static final String XML = "xml";

        //properties
        public static final String PROPERTIES = "properties";

        //ApplicationResources
        public static final String APPLICATION_RESOURCES = "ApplicationResources";

        //ApplicationRules
        public static final String APPLICATION_RULES_ = "ApplicationRules_";

        //ApplicationRules
        public static final String APPLICATION_RULES = "ApplicationRules";


        //Business Functions
        public static final String BUSINESS_FUNCTIONS = "business-functions";

        //Jaffa Transaction Config
        public static final String JAFFA_TRANSACTION_CONFIG = "jaffa-transaction-config";

        //Jaffa Messaging Config
        public static final String JAFFA_MESSAGING_CONFIG = "jaffa-messaging-config";

        //Jaffa Schduler Config
        public static final String JAFFA_SCHEDULER_CONFIG = "jaffa-scheduler-config";

        //Jms Jndi Config
        public static final String JMS_JNDI_CONFIG = "jms-jndi-config";

        //SoaEvents
        public static final String SOA_EVENTS = "soa-events";

        //Roles
        public static final String ROLES = "roles";


        //Navigation
        public static final String NAVIGATION = "navigation";


        //Dwr
        public static final String DWR = "dwr";

        //Jawr
        public static final String JAWR = "jawr";

        //StrutsConfigFormBean
        public static final String STRUTS_CONFIG_FORM_BEAN = "StrutsConfigFormBean";

        //StrutsConfigGlobalForward
        public static final String STRUTS_CONFIG_GLOBAL_FORWARD = "StrutsConfigGlobalForward";

        //StrutsConfigAction
        public static final String STRUTS_CONFIG_ACTION = "StrutsConfigAction";

        //ComponentTilesDefinitions
        public static final String COMPONENT_TILES_DEFS = "ComponentTilesDefinitions";

        //ComponentTilesDefinitions
        public static final String COMPONENT_DEFINITIONS = "ComponentDefinition";

        //Resources Folder
        public static final String RESOURCES = "resources";

    }
}
