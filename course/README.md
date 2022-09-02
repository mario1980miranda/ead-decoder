# Microservice EAD-COURSE

## JPA

- @OneToMany
- @ManyToOne
> ⚠️ **@Fetch avec FetchType.LAZY** : Dans ce cas, si on utilise @Fetch(FetchMode.JOIN) la configuration FetchType.LAZY sera ignorée.

### @EntityGraph
On l'utilise quand on veut donner un comportement EAGER à un attribut déjà marqué comme LAZY, mais seulement dans des requêtes spécifiques.

### @Query
On l'utilise lorsque n'est pas possible de profiter des recherches prêts de l'interface JPARepository :

#### JPQL
```java
@Query("SELECT u FROM User u WHERE u.status = 1")
Collection<User> findAllActiveUsers();
```

#### Native
```java
@Query(value = "select * from tb_modules where course_course_id = :course_id", nativeQuery = true)
List<ModuleModel> findAllModulesIntoCourse(@Param("courseId") UUID courseId); 
```
> avec pagination
```java
@Query(
  value = "SELECT * FROM Users ORDER BY id", 
  countQuery = "SELECT count(*) FROM Users", 
  nativeQuery = true)
Page<User> findAllUsersWithPagination(Pageable pageable);
```

### @Modify

<https://www.baeldung.com/spring-data-jpa-modifying-annotation>

```java
@Modifying
@Query("update User u set u.active = false where u.lastLoginDate < :date")
void deactivateUsersNotLoggedInSince(@Param("date") LocalDate date);
```
```java
@Modifying
@Query("delete User u where u.active = false")
int deleteDeactivatedUsers();
```
## Suppression des donnés
### @OneToMany(mappedBy = "course", fetch = FetchType.LAZY, ***cascade = CascadeType.ALL, orphanRemoval = true***)
Nous laissons à la "JPA" la responsabilité d'effacer les données liées.
> :thumbsdown: : Perte de performances possible car JPA a tendance à créer une requête pour chaque enregistrement enfant.

### @OnDelete(action = OnDeleteAction.CASCADE)
Nous déléguons à la base de données la suppression des enregistrements enfants.
> :thumbsup: : Meilleur performance, si on compare avec **CascadeType.ALL**.
> :thumbsdown: : Moins de contrôle sur ce qui est affecté par la commande.
> :thumbsdown: : Comme pour l'élément précédent, il peut y avoir une perte de performance.

### Specification Arg Resolver
il facilite l'utilisation d'arguments plus élaborés pour les recherches dans les bases de données.

#### Ajoute de la dependence maven
```xml
<!-- https://mvnrepository.com/artifact/net.kaczmarzyk/specification-arg-resolver -->
<dependency>
  <groupId>net.kaczmarzyk</groupId>
  <artifactId>specification-arg-resolver</artifactId>
  <version>2.6.3</version>
</dependency>
```
#### Crée la configuration ResolverConfig
> ResolverConfig extends WebMvcConfigurationSupport

#### Crée le fichier et l'objet pour les "specifications"
> SpecificationTemplate.java
> public interface CourseSpec extends Specification<CourseModel> {}

#### Étendre l'exécuteur de spécification JPA
> extends JpaRepository<UserModel, UUID>, ***JpaSpecificationExecutor\<UserModel\>***
  
#### Utilization
```java
@GetMapping
    public ResponseEntity<List<CourseModel>> getAllCourses(SpecificationTemplate.CourseSpec spec) {  
```
<https://docs.oracle.com/javaee/7/api/javax/persistence/criteria/CriteriaBuilder.html#isMember-E-javax.persistence.criteria.Expression->
<https://docs.oracle.com/javaee/7/api/javax/persistence/criteria/CriteriaQuery.html#distinct-boolean->


  
