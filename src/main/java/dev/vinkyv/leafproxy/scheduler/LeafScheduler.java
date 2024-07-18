package dev.vinkyv.leafproxy.scheduler;

import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.utils.ThreadFactoryBuilder;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.concurrent.*;
import java.util.function.Consumer;

public class LeafScheduler implements Scheduler {
	private static LeafServer instance;

	private final ScheduledExecutorService timerExecutionService;


	public LeafScheduler(ScheduledExecutorService timerExecutionService) {
		this.timerExecutionService = Executors.newSingleThreadScheduledExecutor(new ThreadFactoryBuilder(true, "Leaf Task Scheduler Timer", (t, e) -> {
			try {
				throw e.getCause();
			} catch (Throwable ex) {
				throw new RuntimeException(ex);
			}
		}));
	}

	@Override
	public TaskBuilder buildTask(@NotNull Object plugin, @NotNull Runnable runnable) {
		return null;
	}

	@Override
	public TaskBuilder buildTask(@NotNull Object plugin, @NotNull Consumer<ScheduledTask> consumer) {
		return null;
	}

	@Override
	public @NotNull Collection<ScheduledTask> tasksByPlugin(@NotNull Object plugin) {
		return null;
	}
}
