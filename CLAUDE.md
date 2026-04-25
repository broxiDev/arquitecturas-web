# Arquitecturas Web — Reglas del proyecto

## Referencia de patrones JPA

El directorio `jpa-examples/otro_ejemplo_jpa/` es la referencia canónica para todo código JPA.
Antes de generar o modificar código JPA, revisá esos ejemplos.

## Patrón Repository

Cada método abre y cierra su propio `EntityManager`:

```java
public TipoRetorno metodo(param) {
    EntityManager em = JPAUtil.getEntityManager();
    try {
        // query o persist
    } finally {
        em.close();
    }
}
```

Para operaciones de escritura, transacción explícita con rollback en catch:

```java
em.getTransaction().begin();
em.persist(entidad);
em.getTransaction().commit();
// catch: em.getTransaction().rollback()
```

## Queries JPQL

- Usar siempre proyección con DTO: `SELECT new com.tp2jpa.dto.XxxDTO(...) FROM ...`
- Nunca retornar entidades desde queries de lectura — usar DTOs
- Los parámetros van con `:nombre` y `.setParameter("nombre", valor)`

## DTOs

- Usan Lombok: `@Getter`, `@AllArgsConstructor`, `@ToString`
- Sin lógica, solo campos finales

## Entidades

- Usan Lombok: `@Getter`, `@Setter`, `@NoArgsConstructor`, `@ToString`
- Relaciones bidireccionales: `@OneToMany(mappedBy=...)` en el lado "uno", `@ManyToOne` + `@JoinColumn` en el lado "muchos"
