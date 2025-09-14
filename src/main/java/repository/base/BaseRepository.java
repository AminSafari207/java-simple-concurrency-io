package repository.base;

import java.util.List;
import java.util.Optional;

public interface BaseRepository<T, ID> {
    T save(T entity);
    List<T> findAll();
    Optional<T> findById(ID id);
    boolean deleteById(ID id);
    long count();

    Class<T> getEntityClassRef();
    String getEntityName();
}
