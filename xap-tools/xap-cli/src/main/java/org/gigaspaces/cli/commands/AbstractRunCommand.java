/*
 * Copyright (c) 2008-2016, GigaSpaces Technologies, Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.gigaspaces.cli.commands;

import com.gigaspaces.start.GsCommandFactory;
import org.gigaspaces.cli.CliCommand;
import org.gigaspaces.cli.CliCommandException;
import com.gigaspaces.start.JavaCommandBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public abstract class AbstractRunCommand extends CliCommand {

    protected void validateOptions(int partitions, boolean ha, List<String> instances) throws CliCommandException {
        //if partitions is not defined
        if (partitions == 0) {
            if (ha) {
                throw new CliCommandException("Missing argument: '--partitions' when used in conjunction with '--ha' option");
            }
            if (instances != null && !instances.isEmpty()) {
                throw new CliCommandException("Missing argument: '--partitions' when used in conjunction with '--instances' option");
            }
        } else if (partitions < 0) {
            throw new CliCommandException("Illegal argument: '--partitions="+partitions+"' must be positive");
        }
    }

    protected static ProcessBuilder buildStartLookupServiceCommand() {
        return toProcessBuilder(new GsCommandFactory().lus(), "lookup service");
    }

    protected static ProcessBuilder toProcessBuilder(JavaCommandBuilder command, String desc) {
        ProcessBuilder processBuilder = command.toProcessBuilder();
        processBuilder.inheritIO();
        if (LOGGER.isLoggable(Level.FINE)) {
            String message = "Starting " + desc + " with line:";
            String commandline = String.join(" ", processBuilder.command());
            LOGGER.fine(message + System.lineSeparator() + commandline + System.lineSeparator());
            //System.out.println(message + System.lineSeparator() + commandline + System.lineSeparator());
        }

        return processBuilder;
    }

    protected List<ProcessBuilder> toProcessBuilders(List<String> instances, int partitions, boolean ha, boolean lus) {
        final List<ProcessBuilder> processBuilders = new ArrayList<ProcessBuilder>();
        if (lus) {
            processBuilders.add(buildStartLookupServiceCommand());
        }

        if (partitions == 0) {
            processBuilders.add(buildInstanceCommand(0, false));
        } else {
            for (int id = 1; id < partitions+1; id++) {
                if (instances == null) {
                    processBuilders.add(buildInstanceCommand(id, false));
                    if (ha) {
                        processBuilders.add(buildInstanceCommand(id, true));
                    }
                } else {
                    if (instances.remove(id + "_" + 1)) {
                        processBuilders.add(buildInstanceCommand(id, false));
                    }
                    if (instances.remove(id + "_" + 2)) {
                        processBuilders.add(buildInstanceCommand(id, true));
                    }
                }
            }
        }

        return processBuilders;
    }

    protected abstract ProcessBuilder buildInstanceCommand(int id, boolean isBackup);
}
