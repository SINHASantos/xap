@echo off
set SPACE_PARTITIONS={{topology.partitions}}
set SPACE_HA={{topology.ha}}
if %SPACE_HA%==true (set SPACE_BACKUPS=1) else (set SPACE_BACKUPS=0)
if %SPACE_HA%==true (set /A SPACE_INSTANCES=%SPACE_PARTITIONS% * 2) else (set SPACE_INSTANCES=%SPACE_PARTITIONS%)