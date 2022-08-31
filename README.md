# Proj EAD-DECODER
## Système dévelopée avec l'ecosystème SpringFramework.

<https://spring.io/projects/spring-framework>

* Communication d'entre les microservices (synchrone et assynchrone)
* Base de données par microservice
* Rest X RestFull

### @JsonView
<https://spring.io/blog/2014/12/02/latest-jackson-integration-improvements-in-spring>

Il s'agit d'un outil qui sert a filtrer multiples champs d'une objet dependant du context de la serialization.
C'est a dire qu'on peut reutiliser un même DTO pour créer plusieurs versions d'un objet au lieu de créer multiples DTOs.

### Spring Validation

<https://docs.spring.io/spring-framework/docs/3.2.x/spring-framework-reference/html/validation.html>

```xml
<dependency> 
    <groupId>org.springframework.boot</groupId> 
    <artifactId>spring-boot-starter-validation</artifactId> 
</dependency>
```

> :warning: **Validation avec @JsonView** : Il faut qu'on fasse attention à l'utilisation de la propreté "groups" dans les annotations de validations
on doit référencer le/les @JsonView(s) en utilisation.

```java
@NotBlank(groups = UserView.RegistrationPost.class)
@Size(min = 4, max = 50, groups = UserView.RegistrationPost.class)
@UsernameConstraint(groups = UserView.RegistrationPost.class)
@JsonView(UserView.RegistrationPost.class)
private String username;
```

### @Constraint

### SpecificationArgumentResolver

<https://github.com/tkaczmarzyk/specification-arg-resolver>

```xml
<!-- https://mvnrepository.com/artifact/net.kaczmarzyk/specification-arg-resolver -->
<dependency>
  <groupId>net.kaczmarzyk</groupId>
  <artifactId>specification-arg-resolver</artifactId>
  <version>2.6.3</version>
</dependency>
```

### Spring HATEOAS

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-hateoas</artifactId>
</dependency>
```

