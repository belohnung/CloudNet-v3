package de.dytanic.cloudnet.command.sub;

import de.dytanic.cloudnet.command.ICommandSender;
import de.dytanic.cloudnet.common.Properties;
import de.dytanic.cloudnet.console.animation.questionlist.QuestionAnswerType;

import java.util.*;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.function.Consumer;

public class SubCommandBuilder {

    private Deque<QuestionAnswerType<?>> prefixes = new LinkedBlockingDeque<>();
    private Deque<SubCommandExecutor> preCommandExecutors = new LinkedBlockingDeque<>();
    private Deque<SubCommandExecutor> postCommandExecutors = new LinkedBlockingDeque<>();
    private Collection<SubCommand> subCommands = new ArrayList<>();

    public static SubCommandBuilder create() {
        return new SubCommandBuilder();
    }

    public SubCommandBuilder applyHandler(Consumer<SubCommandBuilder> consumer) {
        consumer.accept(this);
        return this;
    }

    public SubCommandBuilder generateCommand(SubCommandExecutor executor, QuestionAnswerType<?>... types) {
        return this.generateCommand(executor, null, types);
    }

    public SubCommandBuilder generateCommand(SubCommandExecutor executor, Consumer<SubCommand> commandModifier, QuestionAnswerType<?>... types) {
        Collection<QuestionAnswerType<?>> allTypes = new ArrayList<>(this.prefixes);
        allTypes.addAll(Arrays.asList(types));
        Collection<SubCommandExecutor> preCommandExecutors = new ArrayList<>(this.preCommandExecutors);
        Collection<SubCommandExecutor> postCommandExecutors = new ArrayList<>(this.postCommandExecutors);
        SubCommand subCommand = new SubCommand(allTypes.toArray(new QuestionAnswerType<?>[0])) {
            @Override
            public void execute(ICommandSender sender, String command, Object[] args, String commandLine, Properties properties, Map<String, Object> internalProperties) {
                for (SubCommandExecutor preCommandExecutor : preCommandExecutors) {
                    preCommandExecutor.execute(sender, command, args, commandLine, properties, internalProperties);
                }
                executor.execute(sender, command, args, commandLine, properties, internalProperties);
                for (SubCommandExecutor postCommandExecutor : postCommandExecutors) {
                    postCommandExecutor.execute(sender, command, args, commandLine, properties, internalProperties);
                }
            }
        };
        if (commandModifier != null) {
            commandModifier.accept(subCommand);
        }
        this.subCommands.add(subCommand);
        return this;
    }

    public SubCommandBuilder prefix(QuestionAnswerType<?> type) {
        this.prefixes.add(type);
        return this;
    }

    public SubCommandBuilder clearAll() {
        this.clearPreHandlers();
        this.clearPostHandlers();
        this.clearPrefixes();
        return this;
    }

    public SubCommandBuilder clearPrefixes() {
        this.prefixes.clear();
        return this;
    }

    public SubCommandBuilder removeLastPrefix() {
        this.prefixes.pollLast();
        return this;
    }

    public SubCommandBuilder preExecute(SubCommandExecutor executor) {
        this.preCommandExecutors.add(executor);
        return this;
    }

    public SubCommandBuilder clearPreHandlers() {
        this.preCommandExecutors.clear();
        return this;
    }

    public SubCommandBuilder removeLastPreHandler() {
        this.preCommandExecutors.pollLast();
        return this;
    }

    public SubCommandBuilder postExecute(SubCommandExecutor executor) {
        this.postCommandExecutors.add(executor);
        return this;
    }

    public SubCommandBuilder clearPostHandlers() {
        this.postCommandExecutors.clear();
        return this;
    }

    public SubCommandBuilder removeLastPostHandler() {
        this.postCommandExecutors.pollLast();
        return this;
    }

    public Collection<SubCommand> getSubCommands() {
        return this.subCommands;
    }




}