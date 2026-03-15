package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Action;

import java.util.Optional;

@Repository
public interface ActionRepository extends JpaRepository<Action, Long> {

    @Query(value = "SELECT * FROM actions a WHERE a.type = :type AND a.value = :value LIMIT 1", nativeQuery = true)
    Optional<Action> findFirstByTypeAndValueNative(@Param("type") String type, @Param("value") Integer value);
}
