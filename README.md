# PCF Container-to-container Demo

This project showcases the use of [BOSH-DNS](https://bosh.io/docs/dns) for microservices
deployed on [Pivotal Cloud Foundry](https://pivotal.io/platform).
Thanks to BOSH-DNS, microservices running on PCF do not require a service registry (such as
[Netflix Eureka](https://github.com/Netflix/eureka) or [Hashicorp Consul](https://www.consul.io)),
since all apps owning a route on the domain `apps.internal` can be resolved by all apps running
on the platform. For example, if an app has a route `foo.apps.internal`, any app can access it
from within the platform. Moreover, these apps do not require a public route in order to be
accessible from apps, and do not use the `gorouter`.

An app making a direct connection requires a network policy.
This [network policy](https://docs.cloudfoundry.org/concepts/understand-cf-networking.html)
allows a container app to open a connection to an other container:
```shell
$ cf add-network-policy <app-source> --destination-app <app-target> --protocol tcp --port <port>
```

Using BOSH-DNS and container-to-container, your microservices do not require any external libraries
to locate endpoints. BOSH-DNS also brings client-side load-balancing
(like [Ribbon](https://github.com/Netflix/ribbon)) for "free".

If you are using these features, **you do not need to install** the
[Spring Cloud Service tile](https://docs.pivotal.io/spring-cloud-services/2-0/common/index.html)
on PCF, since BOSH-DNS is a core platform feature.

**This project is not using Spring Cloud Netflix**: no Hystrix, no Eureka, no Ribbon.

REST calls are made using
[Retrofit2](https://square.github.io/retrofit), and network errors are managed using
a circuit breaker pattern implemented by [Resilience4j](https://github.com/resilience4j/resilience4j).
Yet, this app is fault tolerant, and can be scaled-out (more instances) and scaled-up (more CPU/memory)
with no downtime.

All these features are available for all apps with any language (not only Spring Boot apps
written in Java).

# How to use it?

This demo project is made of two components:
 - `pcf-c2c-backend`: a microservice exposing a REST API
 - `pcf-c2c-frontend`: a microservice connecting to backend instances
 
You can use this project on any PCF 2.2+ instances, such as [Pivotal Web Service](https://run.pivotal.io).

Compile this project with Maven and a JDK 8, and deploy these apps to Pivotal Cloud Foundry:
```shell
$ ./mvnw clean package && cf push
```

The frontend app is the only one exposing a public endpoint:
```shell
$ curl -s http://pcf-c2c-frontend-RANDOM-WORDS.domain.com
Welcome to PCF Container-to-container Demo
Frontend instance: pcf-c2c-frontend/0
Connecting to backend: pcf-c2c-backend.apps.internal:8080
Received message from backend:
  No backend service available
Time spent: 8 ms
```

As you can see, backend app instances are not seen by frontend app instances.
You need to "allow" connections between frontend app instances and backend app instances:
```shell
$ cf add-network-policy pcf-c2c-frontend --destination-app pcf-c2c-backend --protocol tcp --port 8080
```

This command enables container-to-container networking between app instances, from frontend app
instances to backend app instances.

As soon as this network policy is applied (it can take up to ten seconds), backend app instances
are now accessible by frontend app instances:
```shell
$ curl -s http://pcf-c2c-frontend-RANDOM-WORDS.domain.com
Welcome to PCF Container-to-container Demo
Frontend instance: pcf-c2c-frontend/0
Connecting to backend: pcf-c2c-backend.apps.internal:8080
Received message from backend:
  pcf-c2c-backend/3 says:
  Thank you for coming, pcf-c2c-frontend/0!
  Visitor count: 1
Time spent: 10 ms
```

If you kill a backend app instance used by a frontend app instance, an other backend app instance
will automatically be resolved by BOSH-DNS the next time a frontend app instance is making a
REST call. Notice there is no app downtime while a new backend app is being used.

## Contribute

Contributions are always welcome!

Feel free to open issues & send PR.

## License

Copyright &copy; 2018 Pivotal Software, Inc.

This project is licensed under the [Apache Software License version 2.0](https://www.apache.org/licenses/LICENSE-2.0).
