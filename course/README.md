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


