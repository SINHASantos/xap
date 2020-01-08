@echo off
call {{project.artifactId}}-env.bat
call ..\gs --cli-version=1 deploy -override-name {{project.artifactId}}-mirror -zones {{project.artifactId}}-mirror -properties {{project.artifactId}}-values.yaml -properties embed://partitions=%SPACE_PARTITIONS%;backups=%SPACE_BACKUPS% target\{{project.artifactId}}-mirror-{{project.version}}.jar
call ..\gs --cli-version=1 deploy -override-name {{project.artifactId}}-space -zones {{project.artifactId}}-space -properties {{project.artifactId}}-values.yaml -cluster schema=partitioned total_members=%SPACE_PARTITIONS%,%SPACE_BACKUPS% target\{{project.artifactId}}-space-{{project.version}}.jar