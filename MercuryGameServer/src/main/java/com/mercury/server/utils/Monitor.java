package com.mercury.server.utils;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.Map;

import com.mario.api.MarioApi;
import com.mario.schedule.ScheduledCallback;
import com.nhb.common.BaseLoggable;

public class Monitor extends BaseLoggable {
	public static void main(String[] args) {
		Monitor monitor = new Monitor(null);
		monitor.log();
	}

	private MarioApi api;
	private static final long PERIOD = 60000;

	public Monitor(MarioApi api) {
		this.api = api;
	}

	public void start() {
		this.api.getScheduler().scheduleAtFixedRate(PERIOD, PERIOD, new ScheduledCallback() {

			@Override
			public void call() {
				log();
			}
		});
	}

	private void log() {
		ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();
		long[] threadIds = threadMXBean.getAllThreadIds();
		Map<Thread, StackTraceElement[]> allStackTraces = Thread.getAllStackTraces();

		for (long id : threadIds) {
			ThreadInfo info = threadMXBean.getThreadInfo(id);
			long cpuTime = threadMXBean.getThreadCpuTime(id);
			if (cpuTime / (PERIOD * 1e6) >= 0.8) {
				StackTraceElement[] elements = null;

				for (Thread t : allStackTraces.keySet()) {
					if (t.getId() == id) {
						elements = t.getStackTrace();
						break;
					}
				}

				getLogger().debug("Thread {}: CPU Time: {} ms, \nStacktrace:{}", info.getThreadName(), cpuTime / 1e6,
						stacktraceToString(elements));
			}
		}
	}

	private String stacktraceToString(StackTraceElement[] elements) {
		if (elements == null) {
			return "empty stacktrace...";
		}

		StringBuilder builder = new StringBuilder();
		for (StackTraceElement element : elements) {
			builder.append(element.toString());
			builder.append("\n");
		}
		return builder.toString();
	}
}
