package com.mercury.server.entity.zone;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ZoneManager {

	private Map<String, Zone> zones;

	public ZoneManager() {
		zones = new HashMap<>();
	}

	public void addZone(Zone zone) {
		zones.put(zone.getZoneName(), zone);
	}

	public void removeZone(String zoneName) {
		zones.remove(zoneName);
	}

	public Collection<Zone> getZones() {
		return this.zones.values();
	}

	public Zone getZone(String zoneName) {
		return this.zones.get(zoneName);
	}

	public int getUserCount() {
		int userCount = 0;
		for (Zone zone : this.zones.values()) {
			userCount += zone.getUserManager().size();
		}
		return userCount;
	}

	public int getRoomCount() {
		int roomCount = 0;
		for (Zone zone : this.zones.values()) {
			roomCount += zone.getRoomManager().size();
		}
		return roomCount;
	}
}
