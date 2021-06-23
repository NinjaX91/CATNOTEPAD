package ninja.cfg.catnotepad;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.birbit.android.jobqueue.JobManager;
import com.birbit.android.jobqueue.config.Configuration;
import com.birbit.android.jobqueue.log.CustomLogger;
import com.facebook.stetho.Stetho;
import com.google.gson.JsonObject;
import com.microsoft.identity.client.IPublicClientApplication;
import com.microsoft.identity.client.PublicClientApplication;
import com.raizlabs.android.dbflow.config.FlowManager;
//MSGraph Import.
import com.microsoft.graph.authentication.IAuthenticationProvider;
import com.microsoft.graph.concurrency.ICallback;
import com.microsoft.graph.core.ClientException;
import com.microsoft.graph.http.IHttpRequest;
import com.microsoft.graph.models.extensions.Drive;
import com.microsoft.graph.models.extensions.IGraphServiceClient;
import com.microsoft.graph.requests.extensions.GraphServiceClient;
import com.microsoft.identity.client.AuthenticationCallback;
import com.microsoft.identity.client.IAccount;
import com.microsoft.identity.client.IAuthenticationResult;
import com.microsoft.identity.client.ISingleAccountPublicClientApplication;
import com.microsoft.identity.client.SilentAuthenticationCallback;
import com.microsoft.identity.client.exception.MsalException;
import com.microsoft.intune.mam.client.app.MAMComponents;
import com.microsoft.intune.mam.policy.MAMEnrollmentManager;
import ninja.cfg.catnotepad.activities.home.HomeActivity;

/**
 * Created by MohMah on 8/17/2016.
 */
public class CatNotepad extends Application {
    private static final String TAG = "CatNotePad";
    public static volatile Context CONTEXT;
    public static JobManager JOB_MANAGER;

    private MAMEnrollmentManager mEnrollmentManager;


    @Override
    public void onCreate()
    {
        super.onCreate();
        CONTEXT = getApplicationContext();
        FlowManager.init(this);
        Stetho.initializeWithDefaults(this);
        configureJobManager();
        mEnrollmentManager = MAMComponents.get(MAMEnrollmentManager.class);

    }

    private void configureJobManager()
    {
        Configuration.Builder builder = new Configuration.Builder(this)
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";

                    @Override
                    public boolean isDebugEnabled()
                    {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args)
                    {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args)
                    {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args)
                    {
                        Log.e(TAG, String.format(text, args));
                    }

                    @Override
                    public void v(String text, Object... args)
                    {

                    }
                })
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120);//wait 2 minute
        //if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP){
        //	builder.scheduler(FrameworkJobSchedulerService.createSchedulerFor(this,
        //			MyJobService.class), true);
        //}else{
        //	int enableGcm = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this);
        //	if (enableGcm == ConnectionResult.SUCCESS){
        //		builder.scheduler(GcmJobSchedulerService.createSchedulerFor(this,
        //				MyGcmJobService.class), true);
        //	}
        //}
        JOB_MANAGER = new JobManager(builder.build());
    }

}
