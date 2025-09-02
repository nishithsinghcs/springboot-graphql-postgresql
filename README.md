
# Spring Boot + GraphQL + PostgreSQL — Product CRUD (GraphQL-only) + Swagger dependency

This build exposes **only GraphQL** operations (no REST endpoints). Swagger dependency remains included, but with no REST controllers there are no REST routes to document.

## Run Postgres
```bash
docker compose up -d
```

## Run the app
```bash
gradle bootRun
```

## GraphQL
- Endpoint: `POST http://localhost:8080/graphql`
- Schema: `src/main/resources/graphql/schema.graphqls`

### Single product (requested)
```graphql
query {
  product(id: 1) {
    id name description avail createdBy createdDate modifiedBy modifiedDate
  }
}
```

### Create
```graphql
mutation {
  createProduct(input: {name:"Laptop", description:"15-inch", avail:true, createdBy:"nishith"}) {
    id name avail createdBy createdDate
  }
}
```

### List (page)
```graphql
query {
  products(page:0, size:10, sortBy:"createdDate", sortDir:"desc", nameLike:"lap", avail:true) {
    total page size
    nodes { id name avail createdDate modifiedDate }
  }
}
```

### Update
```graphql
mutation {
  updateProduct(id:1, input:{name:"Laptop Pro", description:"Upgraded", avail:true, modifiedBy:"nishith"}) {
    id name modifiedBy modifiedDate
  }
}
```

### Delete
```graphql
mutation { deleteProduct(id:1) }
```

## Notes
- `id` is auto-increment integer.
- Timestamps are JPA-managed.
- To document GraphQL, use GraphQL tooling (Swagger documents REST; kept here only if you add REST in future).
