#!/usr/bin/env groovy

def fetchAvailableBranches() {
    return ['development', 'master']
}

def downloadArtifacts(String pattern, String target){
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
  def buildInfo
  try{
   buildInfo = server.download spec: downloadSpec, failNoOp: true
   server.publishBuildInfo buildInfo
  }catch(err){
    buildInfo = null
  }
  return buildInfo                 
}

def checkIfArtifactAlreadyExistInRepo(String artifactId, String version, boolean validateSnapshots){
  def artifactBuildInfo = downloadArtifacts( 
                                             prepareSearchPattern(artifactId , version , validateSnapshots),
                                             prepareTargetFolder(artifactId , version , validateSnapshots)
                                           )
  if(artifactBuildInfo == null){
    return false
  }             
  return true
}


def prepareTargetFolder(String artifactId, String version, boolean downloadSnapshot){
   version = !downloadSnapshot && version.contains("SNAPSHOT") ? version.replace("-SNAPSHOT" , "") : version
   def targetFolderInfix = downloadSnapshot ? "SNAPSHOTS" : "RELEASES"
   def targetFolder = "${artifactId}/${targetFolderInfix}/${version}/"
   return targetFolder
}

def prepareSearchPattern(String artifactId, String version , boolean downloadSnapshot) {
   version = !downloadSnapshot && version.contains("SNAPSHOT") ? version.replace("-SNAPSHOT" , "") : version
   def repositoryName = downloadSnapshot ? "cetera-maven-snapshots" : "cetera-maven-releases"
   def pattern = "${repositoryName}/com/example/${artifactId}/${version}/${artifactId}-*.war"
   return pattern
}


return this