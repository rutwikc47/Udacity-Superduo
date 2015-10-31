package it.jaschke.alexandria;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import it.jaschke.alexandria.Fragments.About;
import it.jaschke.alexandria.Fragments.AddBook;
import it.jaschke.alexandria.Fragments.BookDetail;
import it.jaschke.alexandria.Fragments.ListOfBooks;
import it.jaschke.alexandria.Fragments.NavigationDrawerFragment;
import it.jaschke.alexandria.api.Callback;
import it.jaschke.alexandria.com.google.zxing.integration.android.IntentIntegrator;
import it.jaschke.alexandria.com.google.zxing.integration.android.IntentResult;


public class MainActivity extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, Callback {

    /**
      Proper Backstack navigation was not implemented - Fixed
     **/
    private NavigationDrawerFragment navigationDrawerFragment;


    private CharSequence title;
    public static boolean IS_TABLET = false;
    private BroadcastReceiver messageReciever;
    public DrawerLayout drawelayout;

    public static final String MESSAGE_EVENT = "MESSAGE_EVENT";
    public static final String MESSAGE_KEY = "MESSAGE_EXTRA";

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    EditText eantxt;
    private String mScanContents = "Contents:";


    ListOfBooks ls;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IS_TABLET = isTablet();
        if(IS_TABLET){
            setContentView(R.layout.activity_main_tablet);
        }else {
            setContentView(R.layout.activity_main);
        }

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);


        messageReciever = new MessageReciever();
        IntentFilter filter = new IntentFilter(MESSAGE_EVENT);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReciever,filter);

        navigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        title = getTitle();

        // Set up the drawer.
        navigationDrawerFragment.setUp(R.id.navigation_drawer,
                    (DrawerLayout) findViewById(R.id.drawer_layout));
        drawelayout=(DrawerLayout)findViewById(R.id.drawer_layout);

    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment nextFragment;

        switch (position){
            default:
            case 0:
                nextFragment = new ListOfBooks();
                break;
            case 1:
                nextFragment = new AddBook();
                break;
            case 2:
                nextFragment = new About();
                break;

        }

        fragmentManager.beginTransaction()
                .replace(R.id.container, nextFragment)
                .addToBackStack((String) title)
                .commit();
    }

    public void setTitle(int titleId) {
        title = getString(titleId);
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(title);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        eantxt=(EditText)findViewById(R.id.ean);
        IntentResult scanresult= IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (scanresult!=null){
            mScanContents=scanresult.getContents();
            eantxt.setText(mScanContents);
        }else {
            Toast.makeText(this,"No Scan Results",Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!navigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;




        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReciever);
        super.onDestroy();
    }

    @Override
    public void onItemSelected(String ean) {
        if(getSupportActionBar()!=null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Bundle args = new Bundle();
        args.putString(BookDetail.EAN_KEY, ean);

        BookDetail fragment = new BookDetail();
        fragment.setArguments(args);

        int id = R.id.container;
        if(findViewById(R.id.right_container) != null){
            id = R.id.right_container;
        }

        getSupportFragmentManager().beginTransaction()
                .replace(id, fragment)
                .addToBackStack("Book Detail")
                .commit();

    }

    private class MessageReciever extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getStringExtra(MESSAGE_KEY)!=null){
                Toast.makeText(MainActivity.this, intent.getStringExtra(MESSAGE_KEY), Toast.LENGTH_LONG).show();
            }
        }
    }

    private boolean isTablet() {
        return (getApplicationContext().getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    @Override
    public void onBackPressed() {
        Fragment currFrag=this.getSupportFragmentManager().findFragmentById(R.id.container);
        if (currFrag instanceof BookDetail){
            if(!((BookDetail) currFrag).onBackPressed()) {
                super.onBackPressed();
            }
        }
        if (currFrag instanceof AddBook){
            drawelayout.openDrawer(Gravity.LEFT);
        }
        if (currFrag instanceof About){
            drawelayout.openDrawer(Gravity.LEFT);
        }
        if (currFrag instanceof ListOfBooks){
            finish();
        }
        if (drawelayout.isDrawerOpen(Gravity.LEFT)){
            if (currFrag instanceof BookDetail){
                drawelayout.closeDrawer(Gravity.LEFT);
            }else {
                if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
                {
                    finish();
                    return;
                } else {
                    if (currFrag instanceof ListOfBooks){
                    }else {
                        Toast.makeText(getBaseContext(), "Press again to exit", Toast.LENGTH_SHORT).show();
                    }

                }

                mBackPressed = System.currentTimeMillis();

            }

        }
    }
}