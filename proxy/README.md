# Relic-Proxy
**Shattered Relics Proxy Server**

*Relic-Proxy* is used for the routing of messages between the client and the core servers of Shattered Relics. 
Ability to deploy multiple instances around the world to provide players with better latency and gameplay experience.

# Setup Instructions

*  Go to **File->Plugins**. Download and install *'Lombok'*. Restart IDE and continue to next step.

* Go to **File->Settings** and search for *'Annotation Processors'*. Click 'Enable annotation processing' and hit apply.

# Running an Instance
* **In order to deploy a *Relic-Proxy* instance - A valid *Relic-Central* instance must be up and running, with a valid connection.**

This allows for proper authentication of an instance before our clients connect to them.
A successful **Relic-Central** connection will provide information regarding all running **Relic-World** and **Relic-Realm** instances for the players to connect to.
