package com.shattered.networking.listeners;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.Message;
import com.shattered.networking.proto.PacketOuterClass;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author JTlrFrost
 */
public class ProtoEventListener {

    /**
     * Represents a List of Decoders
     */
    @Getter
    private static Map<ProtoListener<?>, PacketOuterClass.Opcode> listeners = new HashMap<>();

    /**
     * Represents a List of Builders
     */
    @Getter
    private static Map<PacketOuterClass.Opcode, GeneratedMessageV3> builders = new HashMap<>();

    /**
     *  @param opcode
     * @param listener
     */
    public static <T extends GeneratedMessageV3> void registerListener(PacketOuterClass.Opcode opcode, ProtoListener<?> listener, T instance) {
        //Registers he Listener
        if (!listeners.containsKey(opcode))
            listeners.put(listener, opcode);

        //Registers the Builder
        if (!builders.containsKey(opcode))
            builders.put(opcode, instance);
    }

    /**
     *
     * @param opcode
     * @return
     */
    public static ProtoListener forOpcode(PacketOuterClass.Opcode opcode) {
        for (Map.Entry<ProtoListener<?>, PacketOuterClass.Opcode> op : listeners.entrySet()) {
            if (op == null) continue;
            if (opcode == op.getValue()) return op.getKey();
        }
        return null;
    }


    /**
     * Unregisters an Opcode
     * @param opcode
     */
    public static void unregisterOpcode(PacketOuterClass.Opcode opcode) {
        if (!listeners.containsKey(opcode))  return;
        listeners.remove(opcode);
    }

    /**
     * Decodes and merges the Payload to the {@link Message}.
     * @param packet
     * @return
     */
    public static Message decode(PacketOuterClass.Packet packet) throws Exception {
        return builders.get(packet.getOpcode()).newBuilderForType().mergeFrom(packet.getPayload()).build();
    }

}
