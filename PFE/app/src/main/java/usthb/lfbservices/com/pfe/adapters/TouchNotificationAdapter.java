package usthb.lfbservices.com.pfe.adapters;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import usthb.lfbservices.com.pfe.R;

/**
 * ItemTouchHelper allows you to easily determine the direction of an even
 */

public class TouchNotificationAdapter extends ItemTouchHelper.Callback {

    private final ITouchHelperAdapter mAdapter;


    /**
     * constructor
     * @param adapter  {@link NotificationListAdapter} .
     */

    public TouchNotificationAdapter(NotificationListAdapter adapter) {
        mAdapter = adapter;
    }

    /**
     * @return true
     * in order to support starting drag events from a long press on a RecyclerView item.
     */
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    /**
     *
     * @return true
     * in order to enable swiping from touch events that start anywhere within the view
     */

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


    /**
     * @param recyclerView instance.
     * @param viewHolder of the view.
     * @return dragFlags and swipeFlags
     * drag supports both up and down directions, and swipe supports left and right directions.
     * to specify which directions of drags and swipes are supported.
     */
    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    /**
     *gets called when a view is dragged from its position to other positions.
     * @param recyclerView instance
     * @param viewHolder of the view.
     * @param target
     * @return true if the item has been moved from its old position to a new position.
     *
     */
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    /**
     *gets called when a view is completely swiped out.
     * @param viewHolder of the view.
     * @param direction appropriate set of direction flags. (LEFT, RIGHT, START, END, UP, DOWN)
     */
    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.START) {
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
        }
    }

    /**
     *
     * @param c
     * @param recyclerView
     * @param viewHolder
     * @param dX
     * @param dY
     * @param actionState
     * @param isCurrentlyActive
     */

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {

        if (dX < 0) { // Swiping left
            ((NotificationListAdapter.ViewHolder) viewHolder).viewBackground.setBackgroundResource(R.drawable.delete);
        }

        final View foregroundView = ((NotificationListAdapter.ViewHolder) viewHolder).viewForeground;
        getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
    }


}