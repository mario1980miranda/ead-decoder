# EAD-SERVICE-REGISTRY
## Spring-Cloud
<https://spring.io/projects/spring-cloud>
## Spring-Cloud-Netflix
<https://docs.spring.io/spring-cloud-netflix/docs/current/reference/html/>
## Dépendance
>pom.xml
```xml
<dependency>
  <groupId>org.springframework.cloud</groupId>
  <artifactId>spring-cloud-starter-netflix-eureka-server</artifactId>
</dependency>
```
>application.yaml
```yaml
spring:
  application:
    name: ead-service-registry

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
```
