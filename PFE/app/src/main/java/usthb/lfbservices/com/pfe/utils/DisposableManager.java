package usthb.lfbservices.com.pfe.utils;

import android.util.Log;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

/**
 * Created by root on 06/03/18.
 * A static class that is used to dispose of Disposable objects that are used with RxAndroid
 * to avoid memory leaks
 */

public class DisposableManager {

    private static final String TAG = DisposableManager.class.getName();

    /**
     * A CompositeDisposable that is used to contain all the Disposables objects to Dispose
     * later on
     */
    private static CompositeDisposable compositeDisposable;


    /**
     * Adds the Disposable passed in parameter to the CompositeDisposable
     * @param disposable A Disposable object to add to the CompositeDisposable
     */
    public static void add(Disposable disposable) {
        getCompositeDisposable().add(disposable);
    }

    /**
     * This methods disposes of all the Disposables objects
     */
    public static void dispose() {
        getCompositeDisposable().dispose();
    }

    /**
     * Returns a CompositeDisposable
     * @return The current CompositeDisposable if not null or not disposed or a new
     * CompositeDisposable otherwise
     */
    private static CompositeDisposable getCompositeDisposable() {
        if (compositeDisposable == null || compositeDisposable.isDisposed()) {
            compositeDisposable = new CompositeDisposable();
        }
        return compositeDisposable;
    }

    /**
     * A constructor for the DisposableManager
     */
    private DisposableManager() {}
}