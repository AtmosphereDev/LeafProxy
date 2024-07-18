package dev.vinkyv.leafproxy.server;

import org.cloudburstmc.protocol.common.util.Preconditions;

import java.net.InetSocketAddress;

public final class ServerInfo implements Comparable<ServerInfo> {

	private final String name;
	private final InetSocketAddress address;

	public ServerInfo(String name, InetSocketAddress address) {
		this.name = Preconditions.checkNotNull(name, "name");
		this.address = Preconditions.checkNotNull(address, "address");
	}

	public String getName() {
		return name;
	}

	public InetSocketAddress getAddress() {
		return address;
	}

	@Override
	public int compareTo(ServerInfo o) {
		return this.name.compareTo(o.getName());
	}
}
