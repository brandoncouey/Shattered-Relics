package com.shattered.networking.message;

import com.google.protobuf.GeneratedMessageV3;
import com.google.protobuf.Message;
import com.shattered.networking.listeners.ProtoListener;
import com.shattered.networking.proto.PacketOuterClass;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

public class ProtoMessageRepository {

    /**
     * Represents a List of Decoders
     */
    @Getter
    private Map<PacketOuterClass.Opcode, ProtoListener<?>> handlers = new HashMap<>();

    /**
     * Represents a List of Builders
     */
    @Getter
    private  Map<PacketOuterClass.Opcode, GeneratedMessageV3> builders = new HashMap<>();

    /**
     *  @param opcode
     * @param listener
     */
    public <T extends GeneratedMessageV3> void registerListener(PacketOuterClass.Opcode opcode, ProtoListener<?> listener, T instance) {
        //Registers he Listener
        if (!handlers.containsKey(opcode))
            handlers.put(opcode, listener);

        //Registers the Builder
        if (!builders.containsKey(opcode))
            builders.put(opcode, instance);
    }

    /**
     *
     * @param opcode
     * @return
     */
    public ProtoListener<?> forOpcode(PacketOuterClass.Opcode opcode) {
        for (Map.Entry<PacketOuterClass.Opcode, ProtoListener<?>> op : handlers.entrySet()) {
            if (op == null) continue;
            if (opcode == op.getKey()) return op.getValue();
        }
        return null;
    }


    /**
     * Unregisters an Opcode
     * @param opcode
     */
    public void unregisterOpcode(PacketOuterClass.Opcode opcode) {
        if (!handlers.containsKey(opcode))  return;
        handlers.remove(opcode);
    }

    /**
     * Decodes and merges the Payload to the {@link Message}.
     * @param packet
     * @return
     */
    public Message decode(PacketOuterClass.Packet packet) throws Exception {
        return builders.get(packet.getOpcode()).newBuilderForType().mergeFrom(packet.getPayload()).build();
    }

}
