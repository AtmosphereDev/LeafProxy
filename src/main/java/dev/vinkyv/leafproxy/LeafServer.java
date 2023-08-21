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

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;

public class LeafServer {
  public static final BedrockCodec CODEC = Bedrock_v594.CODEC.toBuilder().build();

  private final InetSocketAddress address;
  private ChannelFuture channel;
  private final BedrockPong pong;

  public LeafServer(InetSocketAddress address) {
    this.address = address;
    this.pong = new BedrockPong()
      .edition("MCPE")
      .gameType("Survival")
      .motd("§aLeaf§rProxy")
      .subMotd("§aLeaf§rProxy")
      .playerCount(0)
      .maximumPlayerCount(20)
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
    Leaf.getLogger().info("Proxy server started at {}", address.getAddress() + ":" + address.getPort());
  }
}
