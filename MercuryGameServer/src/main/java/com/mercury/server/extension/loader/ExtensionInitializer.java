package com.mercury.server.extension.loader;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

import com.mario.config.WorkerPoolConfig;
import com.mercury.server.api.PluginApiFactory;
import com.mercury.server.callback.JoinRoomCallback;
import com.mercury.server.entity.room.RoomExecutor;
import com.mercury.server.entity.session.SessionManager;
import com.mercury.server.entity.zone.ZoneImpl;
import com.mercury.server.entity.zone.ZoneManager;
import com.mercury.server.extension.config.SchedulerConfig;
import com.mercury.server.extension.config.ZoneConfig;
import com.mercury.server.navigator.JoinRoomNavigator;
import com.mercury.server.plugin.PluginManager;
import com.mercury.server.plugin.ZonePlugin;
import com.mercury.server.processor.ProcessorManager;
import com.nhb.common.BaseLoggable;
import com.nhb.common.async.executor.DisruptorAsyncTaskExecutor;

public class ExtensionInitializer extends BaseLoggable {

	private ExtensionManager extensionManager;
	private PluginApiFactory pluginApiFactory;
	private ZoneManager zoneManager;
	private SessionManager sessionManager;

	public ExtensionInitializer(ExtensionManager extensionManager, PluginApiFactory pluginApiFactory,
			ZoneManager zoneManager, ProcessorManager processorManager, SessionManager sessionManager) {
		this.extensionManager = extensionManager;
		this.pluginApiFactory = pluginApiFactory;
		this.zoneManager = zoneManager;
		this.sessionManager = sessionManager;
	}

	public void init() throws Exception {
		if (extensionManager.isLoaded()) {
			List<ZoneConfig> zoneConfigs = extensionManager.getZoneConfigs();
			for (ZoneConfig zoneConfig : zoneConfigs) {
				ZonePlugin zonePlugin = extensionManager.newInstance(zoneConfig.getExtensionName(),
						zoneConfig.getHandleClass());
				zonePlugin.setMarioApi(this.pluginApiFactory.newMarioApi());

				ZoneImpl zone = new ZoneImpl(zoneConfig.getName(), zonePlugin, this.pluginApiFactory.newMarioApi(),
						sessionManager);
				zone.setExtensionName(zoneConfig.getExtensionName());
				this.zoneManager.addZone(zone);
				zonePlugin.setPluginApi(this.pluginApiFactory.newPluginApi(zone.getZoneName()));
				if (zoneConfig.getJoinRoomCallbackClass() != null) {
					JoinRoomCallback joinRoomCallback = extensionManager.newInstance(zoneConfig.getExtensionName(),
							zoneConfig.getJoinRoomCallbackClass());
					if (joinRoomCallback != null) {
						joinRoomCallback.setMarioApi(this.pluginApiFactory.newMarioApi());
						joinRoomCallback.setPluginApi(this.pluginApiFactory.newPluginApi(zone.getZoneName()));
						zone.setJoinRoomCallback(joinRoomCallback);
					}
				}

				if (zoneConfig.getJoinRoomNavigatorClass() != null) {
					JoinRoomNavigator joinRoomNavigator = extensionManager.newInstance(zoneConfig.getExtensionName(),
							zoneConfig.getJoinRoomNavigatorClass());
					if (joinRoomNavigator != null) {
						joinRoomNavigator.setMarioApi(this.pluginApiFactory.newMarioApi());
						joinRoomNavigator.setPluginApi(this.pluginApiFactory.newPluginApi(zone.getZoneName()));
						zone.setJoinRoomNavigator(joinRoomNavigator);
					}
				}

				SchedulerConfig schedulerConfig = zoneConfig.getSchedulerConfig();
				WorkerPoolConfig workerPoolConfig = schedulerConfig.getWorkerPoolConfig();
				zone.setExecutor(
						DisruptorAsyncTaskExecutor.createSingleProducerExecutor(workerPoolConfig.getRingBufferSize(),
								workerPoolConfig.getPoolSize(), workerPoolConfig.getThreadNamePattern()));
				System.out.println("create zone executor " + zone.getExecutor());
				zone.setScheduledService(
						Executors.newScheduledThreadPool(schedulerConfig.getCounter(), new ThreadFactory() {
							private AtomicInteger idSeed = new AtomicInteger(1);

							@Override
							public Thread newThread(Runnable runnale) {
								return new Thread(runnale,
										String.format("MGS Scheduler #%d", idSeed.getAndIncrement()));
							}
						}));
				System.out.println("create zone scheduled service " + zone.getScheduledService());
				zone.setRoomExecutor(new RoomExecutor(zoneConfig.getRoomConfig()));
				System.out.println("create room executor" + zone.getRoomExecutor());
				zonePlugin.init(zoneConfig.getInitParams());
				
				zone.getExecutor().start();
				zone.getRoomExecutor().start();

				PluginManager pluginManager = new PluginManager(extensionManager, pluginApiFactory, zone);
				if (zone instanceof ZoneImpl) {
					((ZoneImpl) zone).setPluginManager(pluginManager);
				}
				pluginManager.init();

				getLogger().info("Zone created: zoneName {}", zone.getZoneName());
			}
		}
	}
}
