package edu.vuum.mocca;

import java.lang.ref.WeakReference;
import java.util.concurrent.CountDownLatch;

import android.app.Activity;
import android.widget.TextView;
import android.util.Log;

/**
 * @class AndroidPlatformStrategy
 * 
 * @brief Provides methods that define a platform-independent API for
 *        output data to Android UI thread and synchronizing on thread
 *        completion in the ping/pong game.  It plays the role of the
 *        "Concrete Strategy" in the Strategy pattern.
 */
public class AndroidPlatformStrategy extends PlatformStrategy
{	
    /** TextViewVariable. */
    private TextView mTextViewOutput;
	
    /** Activity variable finds gui widgets by view. */
    private WeakReference<Activity> mActivity;

    public AndroidPlatformStrategy(Object output, final Object activityParam)
    {
        /**
         * A textview output which displays calculations and
         * expression trees.
         */
        mTextViewOutput = (TextView) output;

        /** The current activity window (succinct or verbose). */
        mActivity = new WeakReference<Activity>((Activity) activityParam);
    }

    private static CountDownLatch mLatch = null;
    private Options mOptions;

    public void begin()
    {
        mOptions = Options.instance();
        mLatch = new CountDownLatch(mOptions.maxIterations());
    }

    public void print(final String outputString)
    {
        printUI(outputString);
    }

    public void done(){
        mLatch.countDown();
        printUI("Done!");
    }

    /** Barrier that waits for all the game threads to finish. */
    public void awaitDone()
    {
        try {
            mLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /** 
     * Error log formats the message and displays it for the
     * debugging purposes.
     */
    public void errorLog(String javaFile, String errorMessage) 
    {
       Log.e(javaFile, errorMessage);
    }

    private void printUI(final String string){
        if(mActivity!=null&&mActivity.get()!=null){
            mActivity.get().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTextViewOutput.setText(mTextViewOutput.getText() + "\n" + string);
                }
            });
        }
    }
}
