# AdvSpring

This project aims to provide a fast and easy way to create a REST API with Spring Boot and Spring Data JPA.

## Features

- CRUD operations
- Pagination
- Filtering
- Sorting
- Custom hooks
- Custom controllers
- Custom DTOs
- Custom mappers
- Custom streams

## Getting Started

### Prerequisites

Before you start, make sure you have the following installed:

- [Java Development Kit (JDK)](https://www.oracle.com/java/technologies/downloads/)
- [Apache Maven](https://maven.apache.org/)
- [Git](https://git-scm.com/)

### Installation

1. Clone the repository:

   ```bash
   git clone https://github.com/ossnass/advSpring.git
   ```
   
2. Open with your favorite IDE
3. Start adding your bits and pieces of spring boot code

    In this example we will use H2 database with a simple book management system 

4. Create an Entity class, eg: Book.java and Author.java
```java 
   @Entity
   @Table(name = "books")
   @Accessors(chain = true)
   @Getter
   @Setter
   public class Book extends Deletable {
       @Id
       @Column(name = "id")
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Integer id;
   
       @Column(name = "title", nullable = false, length = 255)
       private String title;
   }
```
```java
   @Entity
   @Table(name = "authors")
   @Getter
   @Setter
   @Accessors(chain = true)
   public class Author extends Deletable {
   
       @Id
       @Column(name = "id")
       @GeneratedValue(strategy = GenerationType.IDENTITY)
       private Integer id;
   
       @Column(name = "name", length = 255, nullable = false)
       private String name;
   }
```
5. The setup is extensive, it is suitable for large scale projects:

   We need to extend `Service`, `Contoller` classes and `DtoMapper` and `JpaRepository` interfaces

```java
   @Service
   @ServiceInfo(id = "author", entityClass = Author.class)
   public class AuthorService extends CRUDService<Author, Integer> {
       public AuthorService(FilterAndSortInfoService filterService,
                            JpaRepository<Author, Integer> repository,
                            EntityManager em,
                            SearchSession searchSession,
                            JinqStreamService streamService) {
           super(filterService, repository, em, searchSession, streamService);
       }

       @Override
       protected Author processUpdate(Author dbObject, Author requestEntity) {
           return dbObject.setName(requestEntity.getName());
       }

       @Override
       protected Integer partsToIdClass(String[] idParts) {
           return Integer.parseInt(idParts[0]);
       }

       @Override
       protected int idFieldCount() {
           return 1;
       }
   }
   ```
   
   The `idFoeldCount` method is used to determine the number of fields in the id, in this case it is 1, the `partsToIdClass` method is used to convert the id parts as string to the suitable ID class

   ```java
   @ControllerInfo(mapper = AuthorMapper.class)
   @RestController
   public class AuthorController extends CRUDController<Author,Integer,AuthorDto> {
       public AuthorController(CRUDService<Author, Integer> service) {
           super(service);
       }
   }
   ```

   Nothing here.

   We opt to choose Records as DTOs, but you can use a standard class.

6. We can now launch the application and see the endpoints.
