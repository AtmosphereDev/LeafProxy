package dev.vinkyv.leafproxy;

import com.sun.tools.javac.Main;
import dev.vinkyv.leafproxy.config.LeafConfiguration;
import dev.vinkyv.leafproxy.console.TerminalConsole;
import dev.vinkyv.leafproxy.logger.MainLogger;
import dev.vinkyv.leafproxy.network.handler.upstream.UpstreamPacketHandler;
import dev.vinkyv.leafproxy.network.session.ProxyClientSession;
import dev.vinkyv.leafproxy.network.session.ProxyPlayerSession;
import dev.vinkyv.leafproxy.network.session.ProxyServerSession;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import lombok.Getter;
import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPeer;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v748.Bedrock_v748;
import org.cloudburstmc.protocol.bedrock.data.PacketCompressionAlgorithm;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockChannelInitializer;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;

public class LeafServer {
	@Getter
	private boolean running = true;
	private final NioEventLoopGroup eventLoopGroup = new NioEventLoopGroup();
	private final Set<Channel> clients = ConcurrentHashMap.newKeySet();
	private static LeafServer instance;
	private final TerminalConsole console;
	public static final BedrockCodec CODEC = Bedrock_v748.CODEC.toBuilder().build();
	public static final PacketCompressionAlgorithm compressionAlgorithm = PacketCompressionAlgorithm.ZLIB;
	private final InetSocketAddress address;
	private ChannelFuture channel;
	private final BedrockPong pong;
	private final long serverId;
	public HashMap<String, ProxyPlayerSession> players = new HashMap<>();

	public LeafServer(LeafConfiguration config) {
		instance = this;
		this.console = new TerminalConsole(this);
		this.console.getConsoleThread().start();
		this.address = new InetSocketAddress(config.address, config.port);
		this.serverId = ThreadLocalRandom.current().nextLong();
		this.pong = new BedrockPong()
				.edition("MCPE")
				.gameType("Survival")
				.motd(config.motd)
				.subMotd(config.subMotd)
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
				.group(eventLoopGroup)
				.option(RakChannelOption.RAK_GUID, this.serverId)
				//.option(RakChannelOption.RAK_HANDLE_PING, true)
				.option(RakChannelOption.RAK_ADVERTISEMENT, pong.toByteBuf())
				.childOption(RakChannelOption.RAK_SESSION_TIMEOUT, 10000L)
				.childOption(RakChannelOption.RAK_ORDERING_CHANNELS, 1)
				.childHandler(new BedrockChannelInitializer<ProxyServerSession>() {
					@Override
					protected ProxyServerSession createSession0(BedrockPeer peer, int subClientId) {
						MainLogger.getLogger().info("Incomming connection {}", peer.getSocketAddress());
						return new ProxyServerSession(peer, subClientId);
					}
					@Override
					protected void initSession(ProxyServerSession session) {
						session.setPacketHandler(new UpstreamPacketHandler(session, LeafServer.instance));
					}
				})
				.bind(address)
				.awaitUninterruptibly();
		MainLogger.getLogger().info("Proxy server started at {}", address.getAddress() + ":" + address.getPort());
		Runtime.getRuntime().addShutdownHook(new Thread(this::shutdown));
	}

	public void newClient(InetSocketAddress socketAddress, Consumer<ProxyClientSession> sessionConsumer) {
		Channel channel = new Bootstrap()
				.group(new NioEventLoopGroup())
				.channelFactory(RakChannelFactory.client(NioDatagramChannel.class))
				.option(RakChannelOption.RAK_PROTOCOL_VERSION, CODEC.getRaknetProtocolVersion())
				.option(RakChannelOption.RAK_ORDERING_CHANNELS, 1)
				.option(RakChannelOption.RAK_GUID, ThreadLocalRandom.current().nextLong())
				.option(RakChannelOption.RAK_SESSION_TIMEOUT, 10000L)
				.handler(new BedrockChannelInitializer<ProxyClientSession>() {

					@Override
					protected ProxyClientSession createSession0(BedrockPeer peer, int subClientId) {
						return new ProxyClientSession(peer, subClientId);
					}

					@Override
					protected void initSession(ProxyClientSession session) {
						sessionConsumer.accept(session);
					}
				})
				.connect(socketAddress).addListener((ChannelFuture future) -> {
					if (!future.isSuccess()) {
						MainLogger.getLogger().info("Connection unsuccessful: " + future.cause().getMessage());
						future.channel().close();
					}
				})
				.awaitUninterruptibly()
				.channel();

		this.clients.add(channel);
	}

	public void shutdown() {
		if (!this.running) {
			return;
		}

		this.players.forEach((String name, ProxyPlayerSession session) -> {
			MainLogger.getLogger().info("Player " + name + " disconnected!");
			session.disconnect("Proxy shutdown");
		});

		this.channel.channel().disconnect();

		try {
			Thread.sleep(500);
		} catch (Exception err) {
			MainLogger.getLogger().error(err.getMessage());
		}

		this.console.getConsoleThread().interrupt();

		if (!channel.isCancelled()) {
			MainLogger.getLogger().info("Closing proxy server...");
			channel.cancel(true);
		}

		this.running = false;
		MainLogger.getLogger().info("Shutdown complete!");

		Leaf.shutdownHook();
	}
}