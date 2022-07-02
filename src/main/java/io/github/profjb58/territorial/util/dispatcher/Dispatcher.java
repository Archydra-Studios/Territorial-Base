package io.github.profjb58.territorial.util.dispatcher;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TranslatableText;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.*;

public class Dispatcher {
    private static final TranslatableText DISPATCHER_ALREADY_RUNNING = new TranslatableText("message.territorial.dispatcher.already_running");
    private static final TranslatableText DISPATCHER_NO_TASK = new TranslatableText("message.territorial.dispatcher.not_running");

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private final ConcurrentHashMap<UUID, EnumMap<TaskType, ScheduledTask>> playerTasks = new ConcurrentHashMap<>();

    public void scheduleTask(ServerPlayerEntity player, ScheduledTask task) {
        EnumMap<TaskType, ScheduledTask> tasks;
        UUID playerUuid = player.getUuid();

        if(isTaskActive(playerUuid, task.taskType))
            player.sendMessage(DISPATCHER_ALREADY_RUNNING, false);
        else {
            if(playerTasks.containsKey(playerUuid))
                tasks = playerTasks.get(playerUuid);
            else
                tasks = new EnumMap<>(TaskType.class);

            tasks.put(task.taskType, task);
            task.setFuture(scheduler.schedule(task.success, task.duration, task.timeUnit));
            playerTasks.put(playerUuid, tasks);
        }
    }

    public void failTask(ServerPlayerEntity player, TaskType taskType) {
        var task = getTask(player.getUuid(), taskType);
        if(task != null) {
            task.fail.run();
            cancelTask(player, taskType);
        }
    }

    public void cancelTask(ServerPlayerEntity player, TaskType taskType) {
        var task = getTask(player.getUuid(), taskType);

        if(playerTasks.containsKey(player) && task != null && task.future != null && !task.future.isDone())
            task.future.cancel(true);
        else
            player.sendMessage(DISPATCHER_NO_TASK, false);
    }

    public boolean isTaskActive(UUID player, TaskType taskType) {
        var task = getTask(player, taskType);
        return task != null && !task.isDone();
    }

    @Nullable
    private ScheduledTask getTask(UUID player, TaskType taskType) {
        if(playerTasks.containsKey(player)) {
            var tasks = playerTasks.get(player);
            return tasks.get(taskType);
        }
        return null;
    }
}
