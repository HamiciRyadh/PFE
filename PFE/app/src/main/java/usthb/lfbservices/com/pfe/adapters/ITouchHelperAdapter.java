package usthb.lfbservices.com.pfe.adapters;


public interface ITouchHelperAdapter {

    /**
     * An interface that allows you to listen for “move” and “swipe” events.
     *
     */

    boolean onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);

    void onItemDismiss(int position, int direction);
}
