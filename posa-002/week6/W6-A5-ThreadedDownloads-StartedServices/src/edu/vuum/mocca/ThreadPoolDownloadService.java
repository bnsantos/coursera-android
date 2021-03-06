package edu.vuum.mocca;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Messenger;

/**
 * @class ThreadPoolDownloadService
 *
 * @brief This Service handles downloading several files concurrently
 *        within a pool of Threads.  When it is created, it creates a
 *        ThreadPoolExecutor using the newFixedThreadPool() method of
 *        the Executors class.
 * 
 *        When this Service is started, it should be supplied with a
 *        URI for download and a Messenger.  It downloads the URI
 *        supplied, stores it on the Android file system, then returns
 *        the pathname of the downloaded file using the supplied
 *        Messenger.
 * 
 *        This class implements the Synchronous Service layer of the
 *        Half-Sync/Half-Async pattern.  It also implements a variant
 *        of the Factory Method pattern.
 */
public class ThreadPoolDownloadService extends Service {
    /**
     * A class constant that determines the maximum number of threads
     * used to service download requests.
     */
    static final int MAX_THREADS = 4;
	
    /**
     * The ExecutorService that references a ThreadPool.
     */
    ExecutorService mExecutor;

    /**
     * Hook method called when the Service is created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        mExecutor = Executors.newFixedThreadPool(MAX_THREADS);
    }

    /**
     * Make an intent that will start this service if supplied to
     * startService() as a parameter.
     * 
     * @param context		The context of the calling component.
     * @param handler		The handler that the service should
     *                          use to respond with a result  
     * @param uri               The web URL of a file to download
     * 
     * This method utilizes the Factory Method makeMessengerIntent()
     * from the DownloadUtils class.  The returned intent is a Command
     * in the Command Processor Pattern. The intent contains a
     * messenger, which plays the role of Proxy in the Active Object
     * Pattern.
     */
    public static Intent makeIntent(Context context,
                                    Handler handler,
                                    String uri) {
        // Create the Intent that's associated to the DownloadService
        // class.
        Intent intent = new Intent(context, ThreadPoolDownloadService.class);

        // Set the URI as data in the Intent.
        intent.setData(Uri.parse(uri));

        // Create and pass a Messenger as an "extra" so the
        // DownloadService can send back the pathname.
        intent.putExtra(DownloadUtils.MESSENGER_KEY, new Messenger(handler));
        return intent;
    }

    /**
     * Hook method called when a component calls startService() with
     * the proper Intent.
     */
    @Override
    public int onStartCommand(final Intent intent,
                              int flags,
                              int startId) {
        Runnable downloadRunnable = new Runnable() {
            @Override
            public void run() {
                DownloadUtils.downloadAndRespond(getApplicationContext(), intent.getData(), (Messenger) intent.getExtras().get(DownloadUtils.MESSENGER_KEY));
            }
        };

        mExecutor.execute(downloadRunnable);
      
        // Tell the Android framework how to behave if this service is
        // interrupted.  In our case, we want to restart the service
        // then re-deliver the intent so that all files are eventually
        // downloaded.
        return START_REDELIVER_INTENT;
    }

    /**
     * Called when the service is destroyed, which is the last call
     * the Service receives informing it to clean up any resources it
     * holds.
     */
    @Override
	public void onDestroy() {
    	// Ensure that the threads used by the ThreadPoolExecutor
    	// complete and are reclaimed by the system.

        mExecutor.shutdown();
    }

    /**
     * Return null since this class does not implement a Bound
     * Service.
     */
    @Override
	public IBinder onBind (Intent intent) {
        return null;
    }
}
