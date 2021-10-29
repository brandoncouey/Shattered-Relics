package com.shattered.networking;

import com.google.protobuf.GeneratedMessageV3;
import com.shattered.ServerConstants;
import com.shattered.networking.proto.PacketOuterClass;
import com.shattered.networking.proto.Proxy;
import com.shattered.networking.proto.Sharding;
import com.shattered.networking.session.ext.*;
import com.shattered.connections.ServerType;
import com.shattered.system.SystemLogger;
import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32FrameDecoder;
import io.netty.handler.codec.protobuf.ProtobufVarint32LengthFieldPrepender;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.util.UUID;

/**
 * @author JTlr Frost <brradc@gmail.com> 6/12/2019
 */
public class NetworkBootstrap extends ChannelInitializer<SocketChannel> {

    /**
     * Represents the LoopGroup Boss
     */
    @Setter
    @Getter
    private EventLoopGroup boss;

    /**
     * Represents the LoopGroup Worker
     */
    @Getter
    @Setter
    private EventLoopGroup worker;

    /**
     * Represents the Bootstrapped Network Handler
     */
    @Getter
    @Setter
    private NetworkHandler networkHandler;

    /**
     * Represents the Current Channel Future
     */
    @Getter
    @Setter
    private ChannelFuture future;

    /**
     * Represents the Index of the Port
     */
    @Getter
    @Setter
    private int portIndex;

    /**
     * Represents the Central Session
     */
    @Getter
    @Setter
    private CentralSession centralSession;

    /**
     * Represents the Channel Session
     */
    @Getter
    @Setter
    private ChannelSession channelSession;

    /**
     * Represents the Connection UUID
     */
    @Getter
    private final String connectionUuid = UUID.randomUUID().toString();

    /**
     * Creates a new Network Build
     * @param networkHandler
     */
    public NetworkBootstrap(NetworkHandler networkHandler) {
        setBoss(new NioEventLoopGroup());
        setWorker(new NioEventLoopGroup());
        setNetworkHandler(networkHandler);
    }

    /**
     *
     * @param socketChannel
     * @throws Exception
     */
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        if (socketChannel.isActive())  socketChannel.attr(getNetworkHandler().getSessionKey()).set(new RegisterSession(socketChannel));
        socketChannel.pipeline().addLast(new ProtobufVarint32FrameDecoder());
        socketChannel.pipeline().addLast(new ProtobufDecoder(PacketOuterClass.Packet.getDefaultInstance()));
        socketChannel.pipeline().addLast("channel-handler", getNetworkHandler());
        socketChannel.pipeline().addLast(new ProtobufVarint32LengthFieldPrepender());
        socketChannel.pipeline().addLast(new ProtobufEncoder());
    }

    /**
     * Binds the Server to Assigned INetSocketAddress
     */
    public ChannelFuture bootstrap(String address, int port) {
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(getBoss(), getWorker());
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.TCP_NODELAY, true);
            bootstrap.childHandler(this);
            setFuture(bootstrap.bind(new InetSocketAddress(address, port)).sync());
            if (future.isSuccess())
                SystemLogger.sendSystemMessage("Successfully binded to Socket[Host=" + address + ", Port=" + port + "]");
            return future;
        } catch (Exception e) {
            SystemLogger.sendSystemErrMessage("Unable to bind socket address on Socket[Host=" + address + ", Port=" + port + "]");
        }
        return null;
    }

    /**
     *
     * @param host
     * @param port
     */
    public ChannelFuture connect(String host, int port) {
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(new NioEventLoopGroup());
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
            bootstrap.option(ChannelOption.TCP_NODELAY, true);
            bootstrap.handler(this);
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port)).awaitUninterruptibly();
            if (!future.isSuccess()) {
                SystemLogger.sendSystemErrMessage("Could not establish a connection with Address=" + host + ":" + port +".");
                return future;
            }
            return future;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     *
     * @param channel
     * @param token
     */
    public void authenticate(ServerType type, ChannelFuture channel, String token) {
        //Ensures a Successful Channel
        if (channel == null) return;
        if (!channel.isSuccess()) return;

        //Registers the Server as it's type identifier 'token'
        sendPacket(channel.channel(), PacketOuterClass.Opcode.S_Register, Sharding.RegisterServer.newBuilder().setCuuid(getConnectionUuid()).setToken(token).build());
        SystemLogger.sendSystemMessage("S_Register -> Sending Registry with tokenId=" + token + ", connId=" + getConnectionUuid() + ".");


        //Central Does not need to Ping Central ->
        //Ensures all other connections keep alive with the central.
        if (type != ServerType.CENTRAL) {
            setCentralSession(new CentralSession(channel.channel(), getConnectionUuid()));
            channel.channel().attr(getNetworkHandler().getSessionKey()).set(getCentralSession());
        }

        //Dispatches the Connection Info (i.e their host/port) to be redirected to.
        switch (type) {
            case PROXY: {
                sendPacket(channel.channel(), PacketOuterClass.Opcode.P_RequestRealm, Proxy.RequestRealm.newBuilder().setCuuid(getConnectionUuid()).build());
                break;
            }
            case REALM: {
                sendPacket(channel.channel(), PacketOuterClass.Opcode.S_ConnectionInfo, Sharding.ConnectionInfo.newBuilder().setCuuid(getConnectionUuid()).setHost(ServerConstants.CENTRAL_HOST).setPort(ServerConstants.REALM_DEFAULT_PORT + getPortIndex()).build());
                sendPacket(channel.channel(), PacketOuterClass.Opcode.S_RequestConnectionInfo, Sharding.RequestConnectionInfo.newBuilder().setToken(ChannelSession.CHANNEL_TOKEN).build());
                SystemLogger.sendSystemMessage("S_ConnectionInfo -> Updating Realm Server Connection Information with tokenId=" + token + ", connId=" + getConnectionUuid() + ", host=" + (ServerConstants.CENTRAL_HOST) + ", port=" + ServerConstants.REALM_DEFAULT_PORT + getPortIndex());
                break;
            }
            case WORLD: {
                sendPacket(channel.channel(), PacketOuterClass.Opcode.S_ConnectionInfo, Sharding.ConnectionInfo.newBuilder().setCuuid(getConnectionUuid()).setHost(ServerConstants.CENTRAL_HOST).setPort(ServerConstants.WORLD_DEFAULT_PORT + getPortIndex()).build());
                sendPacket(channel.channel(), PacketOuterClass.Opcode.S_RequestConnectionInfo, Sharding.RequestConnectionInfo.newBuilder().setToken(ChannelSession.CHANNEL_TOKEN).build());
                SystemLogger.sendSystemMessage("S_ConnectionInfo -> Updating World Server Connection Information with tokenId=" + token + ", connId=" + getConnectionUuid() + ", host=" + (ServerConstants.CENTRAL_HOST) + ", port=" + ServerConstants.WORLD_DEFAULT_PORT + getPortIndex());
                break;
            }
            case CHANNEL: {
                sendPacket(channel.channel(), PacketOuterClass.Opcode.S_ConnectionInfo, Sharding.ConnectionInfo.newBuilder().setCuuid(getConnectionUuid()).setHost(ServerConstants.CENTRAL_HOST).setPort(ServerConstants.CHANNEL_DEFAULT_PORT + getPortIndex()).build());
                SystemLogger.sendSystemMessage("S_ConnectionInfo -> Updating Channel Server Connection Information with tokenId=" + token + ", connId=" + getConnectionUuid() + ", host=" + (ServerConstants.CENTRAL_HOST) + ", port=" + ServerConstants.CHANNEL_DEFAULT_PORT + getPortIndex());
                break;
            }
        }

    }

    /**
     * Check if the Network has an Active Channel Server Session
     * @return has session
     */
    public boolean hasChannelSession() {
       return (getChannelSession() != null && getChannelSession().getChannel().isActive());
    }

    /**
     * Checks if the Network has an Active Central Server Session
     * @return has session
     */
    public boolean hasCentralSession() {
        return (getCentralSession() != null&& getCentralSession().getChannel().isActive());
    }

    /**
     * Writes an EmptyPayload Packet.
     * @param channel
     * @param opcode
     */
    public static void sendPacket(Channel channel, PacketOuterClass.Opcode opcode) {
        sendPacket(channel, opcode, PacketOuterClass.EmptyPayload.newBuilder().build());
    }

    /**
     *
     * @param channel
     * @param opcode
     * @param message
     */
    public static void sendPacket(Channel channel, PacketOuterClass.Opcode opcode, GeneratedMessageV3 message) {
        if (channel == null || !channel.isActive() || !channel.isOpen()) return;
        channel.writeAndFlush(PacketOuterClass.Packet.newBuilder().setOpcode(opcode).setPayload(message.toByteString()).build())
                .addListener(ChannelFutureListener.FIRE_EXCEPTION_ON_FAILURE);
    }

}
