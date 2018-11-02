package game.services;

import game.core.Game;
import game.data.PlayerMove;
import game.data.validation.FailReason;

import java.util.Optional;

/**
 * @author Manish Shrestha
 */
public interface ValidationManager {

    Optional<FailReason> validateMove(Game game, PlayerMove move);
}
