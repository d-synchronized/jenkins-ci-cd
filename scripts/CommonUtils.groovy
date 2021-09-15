#!/usr/bin/env groovy

def fetchAvailableBranches() {
    return ['development', 'master']
}

def downloadArtifacts(string pattern, string target){
  echo "Downloading artifact against pattern ${pattern}  ,Target folder ${target}"
  def downloadSpec = """{
                          "files": [
                                     {
                                       "pattern": "${pattern}",
                                       "target": "${target}",
                                       "recursive": "true",
                                       "flat" : "true",
                                       "sortBy" : [ "created" ],
                                       "sortOrder" : "desc",
                                       "limit": "1"
                                     }
                                   ]
                        }"""
   return buildInfo = server.download spec: downloadSpec, failNoOp: false                 
}


def prepareTargetFolder(string artifactId, string version, boolean downloadSnapshot){
   version = !downloadSnapshot && version.contains("SNAPSHOT") ? version.replace("-SNAPSHOT" , "") : version
   def targetFolderInfix = downloadSnapshot ? "SNAPSHOTS" : "RELEASES"
   def targetFolder = "${artifactId}/%{targetFolderInfix}/${version}/"
   return targetFolder
}

def prepareSearchPattern(string artifactId, string version , boolean downloadSnapshot) {
   version = !downloadSnapshot && version.contains("SNAPSHOT") ? version.replace("-SNAPSHOT" , "") : version
   def repositoryName = downloadSnapshot ? "cetera-maven-snapshots" : "cetera-maven-releases"
   def pattern = "${snapshotRepository}/com/example/${artifactId}/${version}/${artifactId}-*.war"
   return pattern
}

return this