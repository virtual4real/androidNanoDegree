//adapted from https://github.com/googlesamples/android-vision
package it.jaschke.alexandria.barcode;



        //import com.google.android.gms.samples.vision.barcodereader.ui.camera.GraphicOverlay;
        import com.google.android.gms.vision.MultiProcessor;
        import com.google.android.gms.vision.Tracker;
        import com.google.android.gms.vision.barcode.Barcode;

        import it.jaschke.alexandria.ui.camera.GraphicOverlay;

/**
 * Factory for creating a tracker and associated graphic to be associated with a new barcode.  The
 * multi-processor uses this factory to create barcode trackers as needed -- one for each barcode.
 */
public class BarcodeTrackerFactory implements MultiProcessor.Factory<Barcode> {
    private GraphicOverlay<BarcodeGraphic> mGraphicOverlay;

    public BarcodeTrackerFactory(GraphicOverlay<BarcodeGraphic> barcodeGraphicOverlay) {
        mGraphicOverlay = barcodeGraphicOverlay;
    }

    @Override
    public Tracker<Barcode> create(Barcode barcode) {
        BarcodeGraphic graphic = new BarcodeGraphic(mGraphicOverlay);
        return new BarcodeGraphicTracker(mGraphicOverlay, graphic);
    }

}

