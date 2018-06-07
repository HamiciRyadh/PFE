package usthb.lfbservices.com.pfe.adapters;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;

import usthb.lfbservices.com.pfe.R;


public class TouchSalespointAdapter extends ItemTouchHelper.Callback {

    private final ITouchHelperAdapter mAdapter;

    /**
     * constructor
     * @param adapter  {@link ProductSalesPointListAdapter} .
     */
    public TouchSalespointAdapter(ProductSalesPointListAdapter adapter) {
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
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
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
        if (direction == ItemTouchHelper.START) { //DELETE
            Log.e("ffff", "END");
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition(), ItemTouchHelper.END);
        }
        if ( direction == ItemTouchHelper.END){ //NOTIFY
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition(), ItemTouchHelper.START);
        }
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView,
                            RecyclerView.ViewHolder viewHolder, float dX, float dY,
                            int actionState, boolean isCurrentlyActive) {
        final View foregroundView = ((ProductSalesPointListAdapter.ViewHolder) viewHolder).viewForeground;

        if (dX > 0) { // swiping right
            ((ProductSalesPointListAdapter.ViewHolder) viewHolder).viewBackground.setBackgroundResource(R.drawable.notification);
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX / 4, dY, actionState, isCurrentlyActive);
        } else {
            ((ProductSalesPointListAdapter.ViewHolder) viewHolder).viewBackground.setBackgroundResource(R.drawable.delete);
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }
}