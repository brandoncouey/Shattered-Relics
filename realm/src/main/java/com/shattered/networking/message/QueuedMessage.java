package com.shattered.networking.message;

import com.google.protobuf.Message;
import com.shattered.networking.listeners.ProtoListener;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueuedMessage {


    /**
     * Represents the ProtoListener
     */
    @Getter
    @NonNull
    private final ProtoListener<?> handler;

    /**
     * Represents the ProtoMessage
     */
    @Getter @NonNull
    private final Message message;

}
