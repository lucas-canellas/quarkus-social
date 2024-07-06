package io.github.lucascanellas.quarkussocial.domain.repository;

import io.github.lucascanellas.quarkussocial.domain.model.User;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class UserRepository implements PanacheRepository<User> {
}

/**
 * PanacheRepository vai me oferecer as operações de CRUD para a entidade que desejar, no caso é User
 *
 * @ApplicationScoped:
 * Cria uma instancia da classe dentro do contexto da aplicação, para que eu possa usa-lo onde eu quiser.
 * Apenas uma instancia será criada e usada em toda aplicação
 *
 */