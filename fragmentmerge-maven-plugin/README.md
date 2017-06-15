# fragmentmerge-maven-plugin
Fragment Merge Plugin for Jaffa Framework and applications built on Jaffa Framework

This plugin will merge the following fragments in the source and place them under /META-INF/ inside the jar

jawr.pfragment

dwr.xfragment

ApplicationRules_*.properties

ApplicationResources.pfragment

ComponentDefinition.xfragment

StrutsConfigFormBean.xfragment

StrutsConfigGlobalForward.xfragment

StrutsConfigAction.xfragment

ComponentTilesDefinitions.xfragment

navigation.xml

business-functions.xml

roles.xml

### Usage 
Add this fragement to your pom

```sh

      <plugin>
        <groupId>org.jaffa.maven</groupId>
        <artifactId>fragmentmerge-maven-plugin</artifactId>
        <configuration>
        <!-- Optional configuration -->
          <skipTagForConfigFiles>
          <!-- to skip start and end tags after all the dwr fragments are merged -->
            <skipTagForConfigFile>dwr</skipTagForConfigFile>
          </skipTagForConfigFiles>
          <skipConfigFiles>
          <!-- to completely skip merging jawr files -->
            <skipConfigFile>jawr</skipConfigFile>
          </skipConfigFiles>
        </configuration>
      </plugin>
```


