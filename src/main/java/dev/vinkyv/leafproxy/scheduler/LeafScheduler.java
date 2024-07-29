/*
 * Copyright 2022 WaterdogTEAM
 * Licensed under the GNU General Public License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.vinkyv.leafproxy.scheduler;

import dev.vinkyv.leafproxy.LeafServer;
import dev.vinkyv.leafproxy.logger.MainLogger;
import dev.vinkyv.leafproxy.utils.ThreadFactoryBuilder;
import io.netty.util.internal.PlatformDependent;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class LeafScheduler {

	private static LeafScheduler instance;
	private final LeafServer proxy;

	private final ExecutorService threadedExecutor;

	private final Map<Integer, TaskHandler<?>> taskHandlerMap = new ConcurrentHashMap<>();
	private final Map<Integer, LinkedList<TaskHandler<?>>> assignedTasks = new ConcurrentHashMap<>();
	private final Queue<TaskHandler<?>> pendingTasks = PlatformDependent.newMpscQueue();

	private final AtomicInteger currentId = new AtomicInteger();

	public LeafScheduler(LeafServer proxy) {
		if (instance != null) {
			throw new RuntimeException("Scheduler was already initialized!");
		}
		instance = this;
		this.proxy = proxy;

		//ThreadFactoryBuilder builder = ThreadFactoryBuilder.builder()
		//		.format("LeafScheduler Executor - #%d")
		//		.build();
		ThreadFactoryBuilder builder = new ThreadFactoryBuilder(true, "LeafScheduler Executor - #%d", new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable e) {
                try {
                    throw e.getCause();
                } catch (Throwable ex) {
                    throw new RuntimeException(ex);
                }
            }
		});
		//TODO: int idleThreads = this.proxy.getConfiguration().getIdleThreads();
		int idleThreads = 4;
		this.threadedExecutor = new ThreadPoolExecutor(idleThreads, Integer.MAX_VALUE, 60, TimeUnit.SECONDS, new SynchronousQueue<>(), builder);
	}

	public static LeafScheduler getInstance() {
		return instance;
	}

	public <T extends Runnable> TaskHandler<T> scheduleAsync(T task) {
		return this.scheduleTask(task, true);
	}

	public <T extends Runnable> TaskHandler<T> scheduleTask(T task, boolean async) {
		return this.addTask(task, 0, 0, async);
	}

	public <T extends Runnable> TaskHandler<T> scheduleDelayed(T task, int delay) {
		return this.scheduleDelayed(task, delay, false);
	}

	public <T extends Runnable> TaskHandler<T> scheduleDelayed(T task, int delay, boolean async) {
		return this.addTask(task, delay, 0, async);
	}

	public <T extends Runnable> TaskHandler<T> scheduleRepeating(T task, int period) {
		return this.scheduleRepeating(task, period, false);
	}

	public <T extends Runnable> TaskHandler<T> scheduleRepeating(T task, int period, boolean async) {
		return this.addTask(task, 0, period, async);
	}

	public <T extends Runnable> TaskHandler<T> scheduleDelayedRepeating(T task, int delay, int period) {
		return this.scheduleDelayedRepeating(task, delay, period, false);
	}

	public <T extends Runnable> TaskHandler<T> scheduleDelayedRepeating(T task, int delay, int period, boolean async) {
		return this.addTask(task, delay, period, async);
	}

	public <T extends Runnable> TaskHandler<T> addTask(T task, int delay, int period, boolean async) {
		if (delay < 0 || period < 0) {
			//TODO: throw new SchedulerException("Attempted to register a task with negative delay or period!");
		}

		int currentTick = this.getCurrentTick();
		int taskId = this.currentId.getAndIncrement();

		TaskHandler<T> handler = new TaskHandler<>(task, taskId, async);
		handler.setDelay(delay);
		handler.setPeriod(period);
		handler.setNextRunTick(handler.isDelayed() ? currentTick + delay : currentTick);

		this.pendingTasks.offer(handler);
		this.taskHandlerMap.put(taskId, handler);
		return handler;
	}

	public void onTick(int currentTick) {
		// 1. Assign all tasks to queue by nextRunTick
		TaskHandler<?> task;
		while ((task = this.pendingTasks.poll()) != null) {
			int tick = Math.max(currentTick, task.getNextRunTick());
			this.assignedTasks.computeIfAbsent(tick, integer -> new LinkedList<>()).add(task);
		}

		// 2. Run all tasks assigned to current tick
		LinkedList<TaskHandler<?>> queued = this.assignedTasks.remove(currentTick);
		if (queued == null) return;

		for (TaskHandler<?> taskHandler : queued) {
			this.runTask(taskHandler, currentTick);
		}
	}

	private void runTask(TaskHandler<?> taskHandler, int currentTick) {
		if (taskHandler.isCancelled()) {
			this.taskHandlerMap.remove(taskHandler.getTaskId());
			return;
		}

		if (taskHandler.isAsync()) {
			this.threadedExecutor.execute(() -> taskHandler.onRun(currentTick));
		} else {
			taskHandler.onRun(currentTick);
		}

		if (taskHandler.calculateNextTick(currentTick)) {
			this.pendingTasks.offer(taskHandler);
			return;
		}

		this.taskHandlerMap.remove(taskHandler.getTaskId()).cancel();
	}

	public void shutdown() {
		MainLogger.getLogger().debug("Scheduler shutdown initialized!");
		this.threadedExecutor.shutdown();

		int count = 25;
		while (!this.threadedExecutor.isTerminated() && count-- > 0) {
			try {
				this.threadedExecutor.awaitTermination(100, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				// Ignore
			}
		}
	}

	public ExecutorService getThreadedExecutor() {
		return this.threadedExecutor;
	}

	public int getCurrentTick() {
		//TODO: return this.proxy.getCurrentTick();
		return 1;
	}
}