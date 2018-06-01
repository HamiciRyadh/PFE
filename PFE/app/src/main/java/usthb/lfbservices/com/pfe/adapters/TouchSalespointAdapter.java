package usthb.lfbservices.com.pfe.adapters;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import usthb.lfbservices.com.pfe.R;


public class TouchSalespointAdapter extends ItemTouchHelper.Callback {

    private final ITouchHelperAdapter mAdapter;

    public TouchSalespointAdapter(ProductSalesPointListAdapter adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        if (direction == ItemTouchHelper.END) { //DELETE
            mAdapter.onItemDismiss(viewHolder.getAdapterPosition(), ItemTouchHelper.END);
        }
        if ( direction == ItemTouchHelper.START){ //NOTIFY
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