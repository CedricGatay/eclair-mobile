package fr.acinq.eclair.wallet.services;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.msopentech.thali.android.installer.AndroidTorInstaller;
import com.msopentech.thali.android.toronionproxy.AndroidOnionProxyManager;
import com.msopentech.thali.android.toronionproxy.AndroidTorConfig;
import com.msopentech.thali.toronionproxy.OnionProxyManager;
import com.msopentech.thali.toronionproxy.TorConfig;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeoutException;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class TorBackgroundService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "fr.acinq.poc.tor.android.action.FOO";

    public TorBackgroundService() {
        super("TorBackgroundService");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, TorBackgroundService.class);
        intent.setAction(ACTION_FOO);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                handleActionFoo();
            }
        }
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo() {
        try {
            startTor();
        } catch (IOException | InterruptedException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void startTor() throws IOException, InterruptedException, TimeoutException {
        File outputDir = getCacheDir();
        String fileStorageLocation = outputDir.getAbsolutePath();

        OnionProxyManager onionProxyManager = getOnionProxyManager(fileStorageLocation);
        int totalSecondsPerTorStartup = 4 * 60;
        int totalTriesPerTorStartup = 5;
        onionProxyManager.setup();
        onionProxyManager.getTorInstaller().updateTorConfigCustom("ControlPort auto" +
                "\nControlPortWriteToFile " + onionProxyManager.getContext().getConfig().getControlPortFile() +
                "\nCookieAuthFile " + onionProxyManager.getContext().getConfig().getCookieAuthFile() +
                "\nCookieAuthentication 1" +
                "\nSocksPort 10462");
        // Start the Tor Onion Proxy
        if (!onionProxyManager.startWithRepeat(totalSecondsPerTorStartup, totalTriesPerTorStartup, true)) {
            Log.e("TorTest", "Couldn't start Tor!");
            return;
        }
        Log.i("TorTest", "Started tor successfully !");
    }

    private OnionProxyManager getOnionProxyManager(String workingSubDirectoryName) {
        File installDir = new File(workingSubDirectoryName);
        TorConfig torConfig = AndroidTorConfig.createConfig(installDir, installDir, this);
        return new AndroidOnionProxyManager(this, torConfig, new TestTorInstaller(this, installDir),
                null, null, null);
    }
}

class TestTorInstaller extends AndroidTorInstaller {
    public TestTorInstaller(Context context, File configDir) {
        super(context, configDir);
    }

    @Override
    public InputStream openBridgesStream() throws IOException {
        return context.getResources().openRawResource(com.msopentech.thali.toronionproxy.android.R.raw.bridges);
    }
}
