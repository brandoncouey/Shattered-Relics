# Relic-Realm
**Shattered Relics Realm Server**

# Setup Instructions

* Go to **File->Plugins**. Download and install 'Lombok'. Restart IDE and continue to next step.
* Go to **File->Settings** and search for 'Annotation Processors'. Click 'Enable annotation processing' and hit apply.

# Running an Instance

* **In order to deploy a *Relic-World* instance - A valid *Relic-Central* instance must be up and running, with a valid connection.**

This allows for a proper authentication of an instance before our clients connect to them.
A successful connect will provide the **Relic-Central** current information regarding the **Relic-Relic** (i.e INetSocket, and UUID).
Allowing other servers to be able to connect to the instance given the provided UUID. 


#Connecting to the Realm

The **Relic-Proxy** will pull the instance information from the **Relic-Central** [See Also: #Running an Instance] to establish a connection to the instance
upon the player's request of login. 


