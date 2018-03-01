The Tools Service provides a service-based interface to selected PDS tools. 
The core functionality for this service is satisfied by the AMMOS Web 
Resource Platform (WRP) Tools Service.

The software can be packaged with the "mvn package" command. The 
documentation including release notes, installation and operation of the 
software should be online at 
http://pds-cm.jpl.nasa.gov/pds4/preparation/tools-service/. If it is not 
accessible, you can execute the "mvn site:run" command and view the 
documentation locally at http://localhost:8080.

In order to create a complete distribution package, execute the 
following commands: 

% mvn site
% mvn package

Copy ts.war (follow the instructions below) into src/main/wrp-tools-service directory before creating a package.




To Install WRP Tools Service  (https://github.jpl.nasa.gov/MIPL/wrp-tools-service/blob/develop/README.md)

# WRP Tools Service

The Tools Service component of the Web Resource Platform enables users to install applications (or "tools") in a central location and enable other users to execute these tools via a ReSTful API.

Two types of tools are supported: *command line executables* (such as `ls` and `pwd`) and *Java methods* (such as `Java.lang.String.concat` and `Java.lang.String.upper`). Custom tools can be developed and used with the Tools Service as well.

## Prerequisites

1. [Java 8 or later](https://java.com/en/)
2. [Apache Maven](https://maven.apache.org/download.cgi)
3. [Python 2.7 and Pip](https://www.python.org/downloads/)
4. [Docopt Python module](https://github.com/docopt/docopt) (install with `pip install docopt`)

## Getting started

### Downloading the code

The first step to getting the Tools Service set up is to obtain the code. There are four GitHub repositories that are used as dependencies. To obtain them, execute the following commands:

```bash
mkdir wrp && cd wrp
```

to create a location for the code, and

```bash
git clone https://github.jpl.nasa.gov/MIPL/wrp-tools-service.git && \
git clone https://github.jpl.nasa.gov/MIPL/wrp-tools-service-ui.git && \
git clone https://github.jpl.nasa.gov/MIPL/wrp-tools-service-webapp.git && \
git clone https://github.jpl.nasa.gov/MIPL/sxn.git
```

to place it into this location.

### Building the code

All the necessary code should now be on your system. Enter into the primary repository, `wrp-tools-service`:

```bash
cd wrp-tools-service
```

and build the code with the `create_release.sh` script:


```bash
./create_release.sh
```

A tarball with the name `wrp-tools-service-beta.tar.gz` should have been created. Unpack the tarball with:

```bash
tar xvf wrp-tools-service-beta.tar.gz
```

and enter it with:

```bash
cd wrp-tools-service-beta
```

### Setting up the Tools Service

There are three available deployments for the Tools Service. You can [stand it up in the included Tomcat](#using-the-included-tomcat), you can [place it in a .war file](#create-a-standalone-war-file) that can be dropped in a pre-existing Tomcat, or you can [spin up a Docker container](#spin-up-docker-container) with the Tools Service running inside it.

Type the following to get usage information:

```bash
./setup.sh --help
```

You should get output similar to the following:

```
[user@home test]$ ./setup.sh warfile --help
Tools Service Setup Script.

Usage:
  ./setup.sh (tomcat|warfile|docker) [options] [--help]

Universal Options:
  --data=<TS_DATA_DIR>               Directory for files created by tools.
  --vicar=<TS_R2LIB>                 VICAR variable VICAR tools will use.
  --java=<TS_JAVA_HOME>              Java directory to be used by tools.
  --email-send=<ADDR_TO_SEND>        E-mail address to send e-mail alerts.
  --email-receive=<ADDR_TO_RECEIVE>  Comma-separated list of alert subscribers.
  --host=<EMAIL_HOST>                ADDR_TO_SEND\'s host server.
  --port=<EMAIL_PORT>                ADDR_TO_SEND\'s host\'s port.
  -v --version                       Print version number.
  -h --help                          Show this screen.

Tomcat Deployment Options:
  --ts=<TS_HOME>                     Install directory the Tools Service.
  -r --reset                         Ignore TS_HOME removal warning.

Warfile Creation Options:
  --outdir=<TS_WAR_OUTDIR>           Directory to place ts.war and ui.war.
  --confdir=<CONF_FILE_DIR>          Directory to contain tool config files.
```

Note that none of these options *have* to be specified. There are defaults in place for most options. Those that require a value will say so.

Most of these options can also be specified in the `ts-config.sh` file, located at the root directory of wrp-tools-service.

#### Create a standalone .war file

An example setup command is as follows:

```
[user@home test]$ ./setup.sh warfile \
	--outdir=/Users/doe/Scratch/myRunningTomcat/webapps \
	--confdir=/Users/doe/Scratch/conf
	--data=/Users/doe/Scratch/ts_data \
	--vicar=/Users/doe/Applications/vicar_open_2.0/p2/lib/x86-macosx \
	--java=/usr/lib/jvm/java-1.8.0 \
	--email-send=john.doe@company.com \
	--email-receive=jane.doe@company.com,sally.doe@company.com \
	--host=smtp.company.com \
	--port=25
```

Once setup is complete, place the .war files in a Tomcat and start it.

## Interacting with the Tools Service

If the Tools Service is running locally at port 8080, you should be able to get a complete list of all available tools at this endpoint:

`http://localhost:8080/ts/`

