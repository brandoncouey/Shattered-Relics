package com.shattered.networking.listeners;

import com.shattered.networking.proto.PacketOuterClass;
import lombok.Getter;

/**
 * Represents the Proto Types
 * @author JTlrFrost
 */
public enum ProtoType {

    /**
     * Represents the universal.proto
     */
    UNIVERSAL(PacketOuterClass.Opcode.U_Messages, PacketOuterClass.Opcode.U_EO_Messages),

    /**
     * Represents the router.proto
     */
    PROXY(PacketOuterClass.Opcode.Proxy_Messages, PacketOuterClass.Opcode.Proxy_EO_Messages),

    /**
     * Represents the shard.proto
     */
    SHARD(PacketOuterClass.Opcode.Sharding_Messages, PacketOuterClass.Opcode.Sharding_EO_Messages),

    /**
     * Represents the shared.proto
     */
    SHARED(PacketOuterClass.Opcode.Shared_Messages, PacketOuterClass.Opcode.Shared_EO_Messages),

    /**
     * Represents the realm.proto
     */
    REALM(PacketOuterClass.Opcode.Shattered_Realm_Messages, PacketOuterClass.Opcode.Shattered_Realm_EO_Messages),

    /**
     * Represents the world.proto
     */
    WORLD(PacketOuterClass.Opcode.Shattered_World_Messages, PacketOuterClass.Opcode.Shattered_World_EO_Messages);

    /**
     * Represents the Start Opcode Id
     */
    @Getter
    private int opcodeStart;

    /**
     * Represents the End Opcode Id
     */
    @Getter
    private int opcodeEnd;

    /**
     * @param opcodeStart
     * @param opcodeEnd
     */
    ProtoType(PacketOuterClass.Opcode opcodeStart, PacketOuterClass.Opcode opcodeEnd) {
        this.opcodeStart = opcodeStart.getNumber();
        this.opcodeEnd = opcodeEnd.getNumber();
    }

    /**
     * Checks to see which direction the opcode is for
     * @param opcode
     * @return
     */
    public static ProtoType forOpcode(PacketOuterClass.Opcode opcode) {
        for (ProtoType types : ProtoType.values()) {
            if (opcode.getNumber() > types.getOpcodeStart() && opcode.getNumber() < types.getOpcodeEnd())
                return types;
        }
        return null;
    }
}
