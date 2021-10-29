# Relic-Central
Shattered Relics Central Server


# Setup Instructions

* Go to **File->Plugins**. Download and install 'Lombok'. Restart IDE and continue to next step.
* Go to **File->Settings** and search for 'Annotation Processors'. Click 'Enable annotation processing' and hit apply.


# Registering a Server

Creating a server that registers to the Central Server must inherit our **Relic-Core** and extend the *'com.shattered.Build'* class.

Calling the *authenticate(ServerType serverType, ChannelFuture centralChannelFuture, String serverUUIDToken)* inside of *'com.shattered.networking.NetworkBootstrap'* inherited by *'com.shattered.Build'*
will automatically send the **Relic-Central** a request to register the server. If the **Relic-Central** does not authenticate it, it will refuse the connection.


#Server Token

The server token is a generated string that is used for identification of the server type.