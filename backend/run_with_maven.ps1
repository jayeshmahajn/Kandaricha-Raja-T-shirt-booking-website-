$ErrorActionPreference = 'Stop'
$url = "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"
$zip = "$env:TEMP\maven.zip"
$dest = "$env:TEMP\maven"

if (-not (Test-Path $dest)) {
    Write-Host "Downloading Maven from $url ..."
    Invoke-WebRequest -Uri $url -OutFile $zip
    Write-Host "Extracting Maven..."
    Expand-Archive -Path $zip -DestinationPath $dest -Force
}

$mvnCmd = "$dest\apache-maven-3.9.9\bin\mvn.cmd"
Write-Host "Starting Spring Boot with Maven..."
& $mvnCmd spring-boot:run
