package usthb.lfbservices.com.pfe.utils;

import android.util.Log;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by root on 06/03/18.
 */

public class DisposableManager {

    private static final String TAG = DisposableManager.class.getName();

    private static CompositeDisposable compositeDisposable;

    public static void add(Disposable disposable) {
        getCompositeDisposable().add(disposable);
    }

    public static void dispose() {
        Log.e(TAG, "isDisposed "+compositeDisposable.isDisposed());
        getCompositeDisposable().dispose();
        Log.e(TAG,"isDisposed "+ compositeDisposable.isDisposed());
    }

    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

    private DisposableManager() {}
}