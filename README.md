# Red Hat SSO Group Add SPI

This RHSSO (Keycloak) SPI will automatically grant group membership on registration. 


## Installing the SPI
 
To install the SPI, first build the jar using Maven:
`mvn clean install`

Then, copy the jar to `[RHSSO-ROOT]/standalone/deployments` and start the server. The server will
automatically detect the jar and deploy it. After it is deployed, it can be enabled for your realm
in the RHSSO admin dashboard by clicking `Events -> Config` adding `group-assignment-event-listener` 
in the `Event Listeners` box.


