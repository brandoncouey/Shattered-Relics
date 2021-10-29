package com.shattered.networking.messages;

import com.google.protobuf.Message;
import com.shattered.networking.listeners.WorldProtoListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class QueuedMessage {

    @Getter
    private final WorldProtoListener listener;

    @Getter
    private final Message message;
}
