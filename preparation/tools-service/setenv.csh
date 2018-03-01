setenv MAVEN_HOME /usr/local/maven/maven-1.0.2
#setenv MAVEN_HOME /Users/hyunlee/dev/pds_en/downloads/apache-maven-2.1.1

#setenv M2_HOME /usr/share/maven
#setenv M2_HOME /Users/hyunlee/dev/pds_en/downloads/apache-maven-2.1.0
setenv M2_HOME /Users/hyunlee/dev/apache-maven-2.2.1
#set MAVEN_OPTS='-Xmx1024m'
setenv MAVEN_OPTS "-Xms128m -Xmx512m"

#alias smvn 'mvn -Dmaven.test.skip=true -Dmaven.repo.local=$JV_SRCROOT/m2/repository'
#alias smvntest 'mvn -Dmaven.test.skip=false -Dmaven.repo.local=$JV_SRCROOT/m2/repository'

set path=( $path $MAVEN_HOME/bin $M2_HOME/bin )
#setenv JAVA_HOME /System/Library/Java/JavaVirtualMachines/1.6.0.jdk/Contents/Home/
#setenv JAVA_HOME /Library/Java/JavaVirtualMachines/jdk1.7.0_76.jdk/Contents/Home/

setenv JAVA_HOME /Library/Java/JavaVirtualMachines/jdk1.7.0_80.jdk/Contents/Home/

#setenv JAVA_HOME /Library/Java/JavaVirtualMachines/jdk1.8.0_131.jdk/Contents/Home/

setenv CLASSPATH /Users/hyunlee/.m2/repository/org/codehaus/mojo/jaxb2-maven-plugin/1.6/jaxb2-maven-plugin-1.6.jar

#setenv CLASSPATH /Users/hyunlee/.m2/repository/gov/nasa/pds/2010/registry/registry-core/0.1.0/registry-core-0.1.0.jar:/Users/hyunlee/Documents/workspace_pdsen/harvest/target/harvest-0.1-dev.jar:/Users/hyunlee/Documents/workspace_pdsen/2010-citool-dev/target/citool-1.2.0-dev.jar:/Users/hyunlee/Documents/workspace_pdsen/product-tools/target/product-tools-3.0.3-dev.jar:/Users/hyunlee/dev/pds_en/downloads/jconn3.jar:/Users/hyunlee/.m2/repository/log4j/log4j/1.2.12/log4j-1.2.12.jar:/Users/hyunlee/.m2/repository/antlr/antlr/2.7.6/antlr-2.7.6.jar:/Users/hyunlee/.m2/repository/org/antlr/antlr-runtime/3.2/antlr-runtime-3.2.jar:/Users/hyunlee/.m2/repository/gov/nasa/arc/pds-utils/1.0.1/pds-utils-1.0.1.jar:/Users/hyunlee/.m2/repository/javax/ws/rs/jsr311-api/1.1.1/jsr311-api-1.1.1.jar:/Users/hyunlee/.m2/repository/com/sun/jersey/jersey-client/1.5/jersey-client-1.5.jar:/Users/hyunlee/.m2/repository/com/sun/jersey/jersey-core/1.5/jersey-core-1.5.jar:/Users/hyunlee/.m2/repository/org/codehaus/jackson/jackson-jaxrs/1.5.5/jackson-jaxrs-1.5.5.jar:${CLASSPATH}

#setenv CLASSPATH /Users/hyunlee/Documents/workspace_pdsen/2010/registry/registry-core/target/registry-core-1.7.0-dev.jar:/Users/hyunlee/Documents/workspace_pdsen/harvest/target/harvest-0.1-dev.jar:/Users/hyunlee/Documents/workspace_pdsen/2010-citool-dev/target/citool-1.2.0-dev.jar:/Users/hyunlee/Documents/workspace_pdsen/product-tools/target/product-tools-3.0.3-dev.jar:/Users/hyunlee/dev/pds_en/downloads/jconn3.jar:/Users/hyunlee/.m2/repository/log4j/log4j/1.2.12/log4j-1.2.12.jar:/Users/hyunlee/.m2/repository/antlr/antlr/2.7.6/antlr-2.7.6.jar:/Users/hyunlee/.m2/repository/org/antlr/antlr-runtime/3.2/antlr-runtime-3.2.jar:/Users/hyunlee/.m2/repository/gov/nasa/arc/pds-utils/1.0.1/pds-utils-1.0.1.jar:/Users/hyunlee/.m2/repository/javax/ws/rs/jsr311-api/1.1.1/jsr311-api-1.1.1.jar:/Users/hyunlee/.m2/repository/com/sun/jersey/jersey-client/1.5/jersey-client-1.5.jar:/Users/hyunlee/.m2/repository/com/sun/jersey/jersey-core/1.5/jersey-core-1.5.jar:/Users/hyunlee/.m2/repository/org/codehaus/jackson/jackson-jaxrs/1.5.5/jackson-jaxrs-1.5.5.jar:${CLASSPATH}

