package com.mercury.server.extension.loader;

import java.util.ArrayList;
import java.util.List;

import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.mario.config.LifeCycleConfig;
import com.mario.config.ManagedObjectConfig;
import com.mario.config.WorkerPoolConfig;
import com.mario.extension.XmlConfigReader;
import com.mercury.server.extension.config.PluginConfig;
import com.mercury.server.extension.config.SchedulerConfig;
import com.mercury.server.extension.config.ZoneConfig;
import com.nhb.common.data.PuObject;

final class ExtensionConfigReader extends XmlConfigReader {
	private String pluginName;
	private ZoneConfig zoneConfig;
	private List<LifeCycleConfig> lifeCycleConfigs;

	@Override
	protected void read(Document document) throws Exception {
		this.pluginName = ((Node) xPath.compile("/mercury/name").evaluate(document, XPathConstants.NODE))
				.getTextContent();
		if (pluginName == null || pluginName.trim().length() == 0) {
			throw new RuntimeException("extension cannot be empty");
		}

		try {
			readZoneConnfig((Node) xPath.compile("/mercury/zone").evaluate(document, XPathConstants.NODE));
		} catch (Exception e) {
			getLogger().debug("read zone config error", e);
		}

		try {
			this.readLifeCycleConfigs(
					(Node) xPath.compile("/mercury/lifecycles").evaluate(document, XPathConstants.NODE));
		} catch (Exception ex) {
			getLogger().error("read life cycle error", ex);
		}

	}

	private void readZoneConnfig(Node node) throws XPathExpressionException {
		String zoneName = ((Node) xPath.compile("name").evaluate(node, XPathConstants.NODE)).getTextContent();
		String handle = ((Node) xPath.compile("handle").evaluate(node, XPathConstants.NODE)).getTextContent();
		SchedulerConfig schedulerConfig = readSchedulerConfig(
				(Node) xPath.compile("scheduler").evaluate(node, XPathConstants.NODE));
		Object variableObj = xPath.compile("variables").evaluate(node, XPathConstants.NODE);
		Object joinRoomCallbackObj = xPath.compile("joinRoomCallback").evaluate(node, XPathConstants.NODE);

		this.zoneConfig = new ZoneConfig();
		this.zoneConfig.setName(zoneName);
		this.zoneConfig.setExtensionName(pluginName);
		this.zoneConfig.setHandleClass(handle);
		this.zoneConfig.setSchedulerConfig(schedulerConfig);

		if (variableObj != null) {
			this.zoneConfig.setInitParams(PuObject.fromXML((Node) variableObj));
		}

		if (joinRoomCallbackObj != null) {
			this.zoneConfig.setJoinRoomCallbackClass(((Node) joinRoomCallbackObj).getTextContent());
		}
	}

	private SchedulerConfig readSchedulerConfig(Node node) throws XPathExpressionException {
		SchedulerConfig schedulerConfig = new SchedulerConfig();
		if (node != null) {
			schedulerConfig.setCounter(Integer
					.parseInt(((Node) xPath.compile("counter").evaluate(node, XPathConstants.NODE)).getTextContent()));
			schedulerConfig.setWorkerPoolConfig(
					readWorkerPoolConfig(((Node) xPath.compile("workerpool").evaluate(node, XPathConstants.NODE))));
		}
		return schedulerConfig;
	}

	private WorkerPoolConfig readWorkerPoolConfig(Node node) throws XPathExpressionException {
		WorkerPoolConfig workerPoolConfig = null;
		if (node != null) {
			workerPoolConfig = new WorkerPoolConfig();
			Node element = node.getFirstChild();
			while (element != null) {
				if (element.getNodeType() == 1) {
					String value = element.getTextContent().trim();
					String nodeName = element.getNodeName();
					if (nodeName.equalsIgnoreCase("poolsize")) {
						workerPoolConfig.setPoolSize(Integer.valueOf(value));
					} else if (nodeName.equalsIgnoreCase("ringbuffersize")) {
						workerPoolConfig.setRingBufferSize(Integer.valueOf(value));
					} else if (nodeName.equalsIgnoreCase("threadnamepattern")) {
						workerPoolConfig.setThreadNamePattern(value);
					}
				}
				element = element.getNextSibling();
			}
		}
		return workerPoolConfig;
	}

	private void readLifeCycleConfigs(Node node) throws XPathExpressionException {
		// read startup config
		this.lifeCycleConfigs = new ArrayList<LifeCycleConfig>();
		if (node == null) {
			return;
		}
		Node item = node.getFirstChild();
		while (item != null) {
			if (item.getNodeType() == 1) {
				LifeCycleConfig config = null;
				if (item.getNodeName().equalsIgnoreCase("plugin")) {
					config = new PluginConfig();
				} else if (item.getNodeName().equalsIgnoreCase("managedobject")) {
					config = new ManagedObjectConfig();
				} else if (item.getNodeName().equalsIgnoreCase("entry")) {
					config = new LifeCycleConfig();
				}

				if (config != null) {
					String name = ((Node) xPath.compile("name").evaluate(item, XPathConstants.NODE)).getTextContent();
					String handleClass = ((Node) xPath.compile("handle").evaluate(item, XPathConstants.NODE))
							.getTextContent();
					config.setName(name);
					config.setExtensionName(pluginName);
					config.setHandleClass(handleClass);

					Object variableObj = xPath.compile("variables").evaluate(item, XPathConstants.NODE);
					if (variableObj != null) {
						try {
							config.setInitParams(PuObject.fromXML((Node) variableObj));
						} catch (Exception e) {
							getLogger().error("parse variable get error, varibales {}", variableObj, e);
						}
					}

					this.lifeCycleConfigs.add(config);
				} else {
					getLogger().warn("lifecycle definition cannot be recognized: " + item);
				}
			}
			item = item.getNextSibling();
		}
	}

	public String getPluginName() {
		return this.pluginName;
	}

	public List<LifeCycleConfig> getLifeCycleConfigs() {
		return this.lifeCycleConfigs;
	}

	public ZoneConfig getZoneConfig() {
		return this.zoneConfig;
	}
}
