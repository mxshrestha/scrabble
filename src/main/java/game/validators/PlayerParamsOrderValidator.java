package game.validators;

import game.request.parameters.PlayerParams;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Manish Shrestha
 */
class PlayerParamsOrderValidator implements ConstraintValidator<PlayerParamsInOrder, List<PlayerParams>> {

    @Override
    public boolean isValid(List<PlayerParams> playerParams, ConstraintValidatorContext constraintValidatorContext) {
        if (playerParams == null) {
            return true;
        }
        final List<PlayerParams> players = playerParams.stream()
                .sorted((o1, o2) -> {
                    if (o1.getOrder() < o2.getOrder()) {
                        return -1;
                    } else if (o1.getOrder() > o2.getOrder()) {
                        return 1;
                    }
                    return 0;
                })
                .collect(Collectors.toList());

        boolean isValidOrder = true;
        for (int order = 1; order <= playerParams.size(); order++) {
            if (order != playerParams.get(order - 1).getOrder()) {
                isValidOrder = false;
                break;
            }
        }

        return isValidOrder && playerParams.stream().map(player -> player.getId()).distinct().count() == players.size();
    }
}
