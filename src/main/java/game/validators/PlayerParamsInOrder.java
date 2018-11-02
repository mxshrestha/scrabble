package game.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * @author Manish Shrestha
 */
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = { PlayerParamsOrderValidator.class })
public @interface PlayerParamsInOrder {

    String message() default "Player params is not in order or player id is duplicated";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
