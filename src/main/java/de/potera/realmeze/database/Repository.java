package de.potera.realmeze.database;

import java.util.List;
import java.util.UUID;

public interface Repository<T> {

    T save(T type);
    T load(UUID id);
    List<T> loadAll();
    void delete(T type);
}
