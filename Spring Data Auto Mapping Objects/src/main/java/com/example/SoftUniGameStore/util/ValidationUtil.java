package com.example.SoftUniGameStore.util;

import javax.validation.ConstraintViolation;
import java.util.Set;

public interface ValidationUtil {
    <E> Set<ConstraintViolation<E>> getViolations(E entity);

}
