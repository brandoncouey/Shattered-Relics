@title Compiling ProtoBuffers...
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"packet.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"world.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"channel.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"vmap.proto
