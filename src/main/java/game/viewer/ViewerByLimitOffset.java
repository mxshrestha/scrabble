package game.viewer;

import java.util.List;

/**
 * @author Manish Shrestha
 */
public interface ViewerByLimitOffset<EntityT> {

    List<EntityT> getViewItems(int offset, int limit);

    int getViewItemCount();
}
