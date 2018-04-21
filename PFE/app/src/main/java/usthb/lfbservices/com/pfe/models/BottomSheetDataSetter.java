package usthb.lfbservices.com.pfe.models;

import android.support.design.widget.BottomSheetBehavior;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 */

public interface BottomSheetDataSetter {

    void setBottomSheetData(SalesPoint salesPoint, ProductSalesPoint productSalesPoint);

    void setBottomSheetDataDetails(SalesPoint salesPoint);

    void setBottomSheetState(int state);
}
