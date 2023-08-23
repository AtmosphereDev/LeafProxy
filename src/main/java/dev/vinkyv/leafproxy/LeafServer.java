package dev.vinkyv.leafproxy;

import java.net.InetSocketAddress;

import org.cloudburstmc.netty.channel.raknet.RakChannelFactory;
import org.cloudburstmc.netty.channel.raknet.config.RakChannelOption;
import org.cloudburstmc.protocol.bedrock.BedrockPong;
import org.cloudburstmc.protocol.bedrock.BedrockServerSession;
import org.cloudburstmc.protocol.bedrock.codec.BedrockCodec;
import org.cloudburstmc.protocol.bedrock.codec.v594.Bedrock_v594;
import org.cloudburstmc.protocol.bedrock.netty.initializer.BedrockServerInitializer;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacket;
import org.cloudburstmc.protocol.bedrock.packet.BedrockPacketHandler;
import org.cloudburstmc.protocol.common.PacketSignal;

import dev.vinkyv.leafproxy.config.LeafConfiguration;
import dev.vinkyv.leafproxy.console.TerminalConsole;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class LeafServer {
  private boolean running = false;

  private final TerminalConsole console;

  public static final BedrockCodec CODEC = Bedrock_v594.CODEC.toBuilder().build();

  private final InetSocketAddress address;
  private ChannelFuture channel;
  private final BedrockPong pong;

  public LeafServer(LeafConfiguration config) {
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
          session.setPacketHandler(new BedrockPacketHandler() {
            @Override
            public PacketSignal handlePacket(BedrockPacket packet) {
              Leaf.getLogger().info("Handled packet {}", packet.getPacketType());
              Leaf.getLogger().info(packet.toString());
              return PacketSignal.HANDLED;
            }
          });
        }
      })
      .bind(address)
      .syncUninterruptibly();

    this.running = true;
    Leaf.getLogger().info("Proxy server started at {}", address.getAddress() + ":" + address.getPort());
  }

  public void shutdown() {
    this.console.getConsoleThread().interrupt();

    if (!channel.isCancelled()) {
      Leaf.getLogger().info("Closing proxy server...");
      channel.cancel(true);
    }

    this.running = false;
    Leaf.getLogger().info("Shutdown complete!");
  }

  public boolean isRunning() {
    return this.running;
  }
}
