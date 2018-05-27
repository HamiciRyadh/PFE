package usthb.lfbservices.com.pfe.fragments;

import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import usthb.lfbservices.com.pfe.R;
import usthb.lfbservices.com.pfe.utils.CameraSource;
import usthb.lfbservices.com.pfe.utils.Utils;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentBarcodeScanner.BarcodeScannerActions} interface
 * to handle interaction events.
 * Use the {@link FragmentBarcodeScanner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentBarcodeScanner extends Fragment {

    private static final String TAG = "BarcodeScanner";

    private BarcodeScannerActions implementation;
    private FragmentActivity fragmentBelongActivity;
    private View rootView;

    private SurfaceView surfaceView;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;

    public FragmentBarcodeScanner() {
        // Required empty public constructor
    }

    public static FragmentBarcodeScanner newInstance() {
        FragmentBarcodeScanner fragment = new FragmentBarcodeScanner();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView =  inflater.inflate(R.layout.fragment_fragment_barcode_scanner, container, false);
        if (rootView != null) {
            surfaceView = rootView.findViewById(R.id.surface_view);
            fragmentBelongActivity = this.getActivity();
            implementation.setToolbarTitleForFragmentBarcodeScanner();
        }
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof BarcodeScannerActions) {
            implementation = (BarcodeScannerActions)context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement BarcodeScannerActions");
        }
    }

    @Override
    public void onDetach() {
        Log.e(TAG, "OnDetach");
        super.onDetach();
        implementation = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        cameraSource.release();
    }

    @Override
    public void onResume() {
        super.onResume();
        initialiseDetectorsAndSources();
    }

    private void initialiseDetectorsAndSources() {
        barcodeDetector = new BarcodeDetector.Builder(fragmentBelongActivity)
                .setBarcodeFormats(Barcode.ALL_FORMATS)
                .build();

        cameraSource = new CameraSource.Builder(fragmentBelongActivity, barcodeDetector)
                .setRequestedPreviewSize(1920, 1080)
                .setFlashMode(Camera.Parameters.FLASH_MODE_ON)
                .setFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)
                .build();

        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (Utils.checkCameraPermission(fragmentBelongActivity)) {
                        cameraSource.start(surfaceView.getHolder());
                    } else {
                        Utils.requestCameraPermission(fragmentBelongActivity);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });


        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {
            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                if (barcodes != null && barcodes.size() != 0) {
                    implementation.onBarcodeScanFinished(barcodes);
                }
            }
        });
    }

    public interface BarcodeScannerActions {
        void onBarcodeScanFinished(final SparseArray<Barcode> barcodes);
        void setToolbarTitleForFragmentBarcodeScanner();
    }
}