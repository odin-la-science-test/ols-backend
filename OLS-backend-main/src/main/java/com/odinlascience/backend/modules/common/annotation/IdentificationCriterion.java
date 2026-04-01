package com.odinlascience.backend.modules.common.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation pour marquer les champs d'un DTO qui doivent être utilisés
 * comme critères d'identification dans l'algorithme de matching.
 * Les champs annotés seront pris en compte pour calculer le score de correspondance.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface IdentificationCriterion {}
