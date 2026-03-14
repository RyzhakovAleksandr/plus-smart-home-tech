package ru.yandex.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.model.Condition;
import ru.yandex.practicum.model.enums.ConditionOperation;
import ru.yandex.practicum.model.enums.ConditionType;

import java.util.Optional;

@Repository
public interface ConditionRepository extends JpaRepository<Condition, Long> {
    Optional<Condition> findByTypeAndOperationAndValue(ConditionType type, ConditionOperation operation, Integer value);

    @Query(value = "SELECT * FROM conditions c WHERE c.type = :type AND c.operation = :operation AND c.value = :value LIMIT 1",
            nativeQuery = true)
    Optional<Condition> findFirstByTypeAndOperationAndValue(
            @Param("type") String type,
            @Param("operation") String operation,
            @Param("value") Integer value);
}
