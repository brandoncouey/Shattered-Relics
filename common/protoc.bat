@title Compiling ProtoBuffers...
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"packet.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"universal.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"proxy.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"sharding.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"shared.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"realm.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"world.proto
start protoc -I="src/main/protobuf/" --java_out="src/main/java/" "src/main/protobuf/"channel.proto
