package org.gigaspaces.cli.commands;

import com.gigaspaces.start.SystemInfo;
import org.gigaspaces.cli.commands.utils.XapCliUtils;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.*;

/**
 * @since 12.3
 * @author Rotem Herzberg
 */
@Command(name = "run", header = "Run a standalone Space")
public class SpaceRunCommand extends AbstractRunCommand {

    @Parameters(index = "0", description = "Name of Space to run")
    String name;
    @Option(names = {"--partitions"}, description = "Specify the number of partitions for the Processing Unit")
    int partitions;
    @Option(names = {"--ha"}, description = "High availability (add one backup per partition)")
    boolean ha;
    @Option(names = {"--instances"}, split = ",", description = "Specify one or more instances to run (for example: --instances=1_1,1_2). "
                                                                    + "If no instances are specified, runs all instances.")
    String[] instances;
    @Option(names = {"--lus"}, description = "Start a lookup service")
    boolean lus;

    @Override
    protected void execute() throws Exception {

        validateOptions(partitions, ha, instances);

        final List<ProcessBuilder> processBuilders = new ArrayList<ProcessBuilder>();
        if (lus) {
            processBuilders.add(buildStartLookupServiceCommand());
        }
        if (partitions == 0) {
            processBuilders.add(buildSpaceCommand(0, false));
        } else {
            for (int id = 1; id < partitions+1; id++) {
                if (instances == null) {
                    processBuilders.add(buildSpaceCommand(id, false));
                    if (ha) {
                        processBuilders.add(buildSpaceCommand(id, true));
                    }
                } else {
                    if (containsInstance(instances,id + "_" + 1)) {
                        processBuilders.add(buildSpaceCommand(id, false));
                    }
                    if (containsInstance(instances, id + "_" + 2)) {
                        processBuilders.add(buildSpaceCommand(id, true));
                    }
                }
            }
        }

        XapCliUtils.executeProcesses(processBuilders);
    }

    private static String getSpaceClassPath(Map<String, String> env) {
        return toClassPath(env.get("PRE_CLASSPATH"), getDataGridTemplate(), getGsJars(env), env.get("POST_CLASSPATH"));
    }

    private static String getDataGridTemplate() {
        return SystemInfo.singleton().locations().deploy() +
                File.separatorChar + "templates" +
                File.separatorChar + "datagrid";
    }

    private ProcessBuilder buildSpaceCommand(int id, boolean isBackup) {
        ProcessBuilder pb = new CommandBuilder(name).topology(partitions, ha).instance(id, isBackup).toProcessBuilder();
        showCommand("Starting Space with line:", pb.command());
        return pb;
    }

    public static class CommandBuilder {
        final String name;
        private int partitions;
        private boolean ha;
        private int partitionId;
        private boolean isBackupInstance;
        private String javaHeap;

        public CommandBuilder(String name) {
            this.name = name;
        }

        public CommandBuilder topology(int partitions, boolean ha) {
            this.partitions = partitions;
            this.ha = ha;
            return this;
        }

        public CommandBuilder instance(int partitionId, boolean isBackupInstance) {
            this.partitionId = partitionId;
            this.isBackupInstance = isBackupInstance;
            return this;
        }

        public ProcessBuilder toProcessBuilder() {
            final ProcessBuilder pb = createJavaProcessBuilder();
            final Collection<String> commands = new LinkedHashSet<String>();
            commands.add("-Dcom.gs.start-embedded-lus=false");

            if (javaHeap !=null && javaHeap.length() != 0) {
                commands.add("-Xms" + javaHeap);
                commands.add("-Xmx" + javaHeap);
            }
            String[] options = {"XAP_SPACE_INSTANCE_OPTIONS", "XAP_OPTIONS"};
            addOptions(commands, options);

            commands.add("-classpath");
            commands.add(getSpaceClassPath(pb.environment()));

            commands.add("org.openspaces.pu.container.integrated.IntegratedProcessingUnitContainer");
            commands.add("-name");
            commands.add(name);

            if (partitionId != 0) {
                commands.add("-cluster");
                commands.add("schema=partitioned");
                if (ha) {
                    commands.add("total_members=" + partitions + ",1");
                } else {
                    commands.add("total_members=" + partitions + ",0");
                }
                commands.add("id=" + partitionId);
                if (isBackupInstance) {
                    commands.add("backup_id=1");
                }
            }

            pb.command().addAll(commands);
            return pb;
        }

        public CommandBuilder javaHeap(String javaHeap) {
            this.javaHeap = javaHeap;
            return this;
        }
    }
}
