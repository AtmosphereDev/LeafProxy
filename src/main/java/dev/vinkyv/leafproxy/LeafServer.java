package dev.vinkyv.leafproxy;

import dev.vinkyv.leafproxy.config.LeafConfiguration;
import dev.vinkyv.leafproxy.console.TerminalConsole;
import dev.vinkyv.leafproxy.network.handler.RequestNetworkSettingsHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v671.Bedrock_v671;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.common.PacketSignal;

import java.net.InetSocketAddress;

public class LeafServer {
	private boolean running = false;

	private static LeafServer instance;

	private final TerminalConsole console;

	public static final BedrockCodec CODEC = Bedrock_v671.CODEC.toBuilder().build();

	private final InetSocketAddress address;
	private ChannelFuture channel;
	private final BedrockPong pong;

	public LeafServer(LeafConfiguration config) {
		instance = this;
		this.console = new TerminalConsole(this);
		this.console.start();
		InetSocketAddress address = new InetSocketAddress(config.address, config.port);
		this.address = address;
		this.pong = new BedrockPong()
				.edition("MCPE")
				.gameType("Survival")
				.motd(config.motd)
				.subMotd(config.name)
				.playerCount(0)
				.maximumPlayerCount(config.maxPlayers)
				.ipv4Port(this.address.getPort())
				.nintendoLimited(false)
				.protocolVersion(CODEC.getProtocolVersion())
				.version("1.0.0");
	}

	public void start() {
		this.channel = new ServerBootstrap()
				.channelFactory(RakChannelFactory.server(NioDatagramChannel.class))
				.option(RakChannelOption.RAK_ADVERTISEMENT, pong.toByteBuf())
				.group(new NioEventLoopGroup())
				.childHandler(new BedrockServerInitializer() {
					@Override
					protected void initSession(BedrockServerSession session) {
						session.setCodec(CODEC);
						session.setPacketHandler(new RequestNetworkSettingsHandler(instance, session));
					}
				})
				.bind(address)
				.syncUninterruptibly();

		this.running = true;
		Leaf.getLogger().info("Proxy server started at {}", address.getAddress() + ":" + address.getPort());
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
	}

	public void shutdown() {
		if (!this.running) {
			return;
		}

		try {
			Thread.sleep(500);
		} catch (Exception err) {
			Leaf.getLogger().error(err);
		}

		this.console.getConsoleThread().interrupt();

		if (!channel.isCancelled()) {
			Leaf.getLogger().info("Closing proxy server...");
			channel.cancel(true);
		}

		this.running = false;
		Leaf.getLogger().info("Shutdown complete!");

		Leaf.shutdownHook();
	}

	public boolean isRunning() {
		return this.running;
	}
}
