package com.project.michiyl.mapfinder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.project.michiyl.mapfinder.dummy.DummyContent;
import com.project.michiyl.mapfinder.dummy.MapContent;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

/**
 * An activity representing a list of Items. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link ItemDetailActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class ItemListActivity extends AppCompatActivity {


    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean twoWayDisplay;
    private static final int PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE = 456;   // irgendeine Zahl, MUSS aber eindeutig sein
    private static final int PERMISSION_REQUEST_READ_EXTERNAL_STORAGE = 789;   // irgendeine Zahl, MUSS aber eindeutig sein

    // where is our MapFinder CoD4MW directory?
    /**
     * myMapDirectory is: <br>
     *     <b>/storage/sdcard/MapFinder/CoD4MW/</b> on pre-Android 5.0 <br>
     *     <b>/storage/emulated/0/MapFinder/CoD4MW</b> on Android 5+
     */
    File myMapDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MapFinder/CoD4MW/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_list);

        // let's ask for permissions in Android post-4.4
        if(Build.VERSION.SDK_INT >= 23) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                /* & checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED*/)
            {
                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE);
                //requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_EXTERNAL_STORAGE);
            }
            else
            {
                doCheckEnvironmentExternalStorage();
            }
        }
        else
        {
            doCheckEnvironmentExternalStorage();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle(getTitle());
        
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // create a File object for the parent directory
                //File myMapDirectory = new File("/sdcard/MapFinder/CoD4MW/");    // don't forget to end with /
                Log.d("ItemListActivity", "onClick: " + Environment.getExternalStorageDirectory().getAbsolutePath());

                // have the object build the directory structure, if needed.
                if(myMapDirectory.exists() == false) {
                    myMapDirectory.mkdirs();
                }
                Log.d("ItemListActivity", "after onClick: " + myMapDirectory.getAbsolutePath());
                // create a File object for the output file
                //File outputFile = new File(myMapDirectory, "/mapname.txt");
                // now attach the OutputStream to the file object, instead of a String representation
                /*
                try {
                    FileOutputStream fos = new FileOutputStream(outputFile, false); // don't append -> overwrite!
                    BufferedOutputStream bos = new BufferedOutputStream(fos);
                    bos.write("Test Map\nmp_testmap\nThis is a description.".toString().getBytes());
                    bos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                */


                mapper();

                /*
                Snackbar.make(view, "Output file created: " + outputFile.exists(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();

                Log.d("ItemListActivity", "after save: " + outputFile.getAbsolutePath());

                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                */
            }
        });

        View recyclerView = findViewById(R.id.item_list);
        assert recyclerView != null;
        setupRecyclerView((RecyclerView) recyclerView);

        if (findViewById(R.id.item_detail_container) != null) {
            // The detail container view will be present only in the
            // large-screen layouts (res/values-w900dp).
            // If this view is present, then the
            // activity should be in two-pane mode.
            twoWayDisplay = true;
        }
    }

    // needed for post-4.4-permission check
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if( (requestCode == PERMISSION_REQUEST_WRITE_EXTERNAL_STORAGE) &&
                (grantResults.length > 0) &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) )
        {
            doCheckEnvironmentExternalStorage();
        }


        // if we ever need permission to read, here's the code for it
        /*
        if( (requestCode == PERMISSION_REQUEST_READ_EXTERNAL_STORAGE) &&
                (grantResults.length > 0) &&
                (grantResults[0] == PackageManager.PERMISSION_GRANTED) ) {
            // nothing
        }
        */

    }

    private void doCheckEnvironmentExternalStorage() {
        // Ausgabe von Text: wenn TRUE, dann "", wenn FALSE, dann " nicht"
        //Log.d("michiyl", "Medium kann entfernt werden: " + Environment.isExternalStorageRemovable());

        // Statusabfrage
        String state = Environment.getExternalStorageState();
        boolean canRead, canWrite;
        switch (state) {
            case Environment.MEDIA_MOUNTED:
                canRead = canWrite = true;
                break;
            case Environment.MEDIA_MOUNTED_READ_ONLY:
                canRead = true;
                canWrite = false;
                break;
            default:
                canRead = canWrite = true;
        }

        if(canWrite && canWrite) /* == true */ {
            // NOW we can do stuff on the file system!
            mapper();
        }

        //Log.d("michiyl", "canRead: " +canRead+ ", canWrite: " +canWrite);
    }



    private void mapper() {

        // wenn unter Dateipfad vorhanden, dann auslesen und anlegen
        File dummyMapDir = new File(myMapDirectory + "/mp_dummymap");

        if(myMapDirectory.exists()) {
            createDummyFiles(dummyMapDir, "name_ingame.txt", "Dummy Map");
            createDummyFiles(dummyMapDir, "name_console.txt", "mp_dummymap");
            createDummyFiles(dummyMapDir, "description.txt", "Enter your description here!");
            File imagesDirectory = new File(dummyMapDir, "/images/");
            Log.d("michiyl", "imagesDirectory: " + imagesDirectory.getAbsolutePath());
            if(! imagesDirectory.exists()) {
                imagesDirectory.mkdirs();
            }
            else {
                 createFirstImage(imagesDirectory);
            }
        }
        else {
            // if not created - create it and run through again
            dummyMapDir.mkdirs();
            mapper();
        }
    }



    private void createFirstImage(File filepath) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.default_loadingscreen);
        File saveTo = filepath;
        String fileName = "_placeholder.png";
        File dest = new File(saveTo, fileName);
        try (FileOutputStream fos = new FileOutputStream(dest)) {
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
	
		MediaScannerHelper mediaScannerHelper = new MediaScannerHelper();
		mediaScannerHelper.addFile(dest.getAbsolutePath().toString());
        
    }



    private void createDummyFiles(File directory, String filenameWithExtension, String content) {
        if(!directory.exists()) {
            directory.mkdir();
        }
		
        File outputFile = new File(directory, "/"+filenameWithExtension);
		//TODO: create boolean to check wether we can overwrite or not!
		if(outputFile.exists()) {
			return;
		}
		else {
            // TODO: try-with-resources
			try {
				FileOutputStream fos = new FileOutputStream(outputFile, false); // don't append -> overwrite!
				BufferedOutputStream bos = new BufferedOutputStream(fos);
				bos.write(content.toString().getBytes());
				bos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

            MediaScannerHelper mediaScannerHelper = new MediaScannerHelper();
            mediaScannerHelper.addFile(outputFile.getAbsolutePath().toString());
		}
    }


    private void setupRecyclerView(@NonNull RecyclerView recyclerView) {
        recyclerView.setAdapter(new SimpleItemRecyclerViewAdapter(DummyContent.ITEM_LIST));
		// some optimisations for the scrollable recyclerView
		recyclerView.setDrawingCacheEnabled(true);
		recyclerView.setItemViewCacheSize(20);
		recyclerView.setHasFixedSize(true);
    }

    public class SimpleItemRecyclerViewAdapter
            extends RecyclerView.Adapter<SimpleItemRecyclerViewAdapter.ViewHolder> {

        private final List<DummyContent.DummyItem> mValues;

        public SimpleItemRecyclerViewAdapter(List<DummyContent.DummyItem> items) {
            mValues = items;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_list_content, parent, false);
            return new ViewHolder(view);
        }


        /**
         * @param position        Which directory index are we going to read from?
         * @param readConsoleName If set to "true" method will read the <b>console</b> name file. <br>
         *                        If set to "false" method will read the <b>ingame</b> name file.
         * @return A string formed from the single line inside the text file
         */
        // TODO: generalize it more -> away with the boolean (and check the name instead?)
        public String readNameFromFile(int position, boolean readConsoleName) {
            int countOfDirectories = MapContent.MapItem.myMapDirectory.listFiles().length;
            if(countOfDirectories <= 0) {
                Log.e("michiyl", "ERROR --- NO DIRECTORY!");
                return "";
            }

            File theDirectory = MapContent.MapItem.myMapDirectory;
            String[] filenames = theDirectory.list();
            StringBuilder sb = new StringBuilder("");
            File myFile = null;

            // check which one we want to read from this time
            if(readConsoleName) {
                // FULL path necessary!
                myFile = new File(theDirectory.getAbsolutePath() + "/" + filenames[position], "/name_console.txt");
            }
            if(! readConsoleName) {
                // FULL path necessary!
                myFile = new File(theDirectory.getAbsolutePath() + "/" + filenames[position], "/name_ingame.txt");
            }
            Log.d("michiyl", "myFile: " + myFile.toString());
            Log.d("michiyl", "exists myFile: " + myFile.exists());
            Log.d("michiyl", "canRead myFile: " + myFile.canRead());

            try (BufferedReader br = new BufferedReader(new FileReader(myFile))) {
                // read just the single line - no need for line break
                sb.append(br.readLine());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("michiyl", "FileNotFoundException!", e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("michiyl", "IOException!", e);
            }
            myFile = null;
			filenames = null;
			theDirectory = null;

            //Log.d("michiyl", "sb: " + sb.toString());
            return sb.toString();
        }


        @Override
        public void onBindViewHolder(final ViewHolder holder, int position) {
			
			Log.i("michiyl", "onBindViewHolder: " + holder + ", position: " + position);
			final int myPositionForRunnable = position;
			holder.mItem = mValues.get(position);
	
			// another thread can handle inserting the item informations:
			new Handler().post(new Runnable()
			{
				@Override
				public void run()
				{
					holder.mIdView.setText(readNameFromFile(myPositionForRunnable, true));    // here we set the text
					holder.mContentView.setText(readNameFromFile(myPositionForRunnable, false)); // here, too!
					
					setViewHolderBitmap(holder, myPositionForRunnable);
				}
			});
            
			
			/*
			int[][] states = new int[][] {
					new int[] { android.R.attr.state_enabled}, // enabled
					new int[] {-android.R.attr.state_enabled}, // disabled
					new int[] {-android.R.attr.state_checked}, // unchecked
					new int[] { android.R.attr.state_pressed}  // pressed
			};
	
			int[] colors = new int[] {
					Color.RED,
					Color.RED,
					Color.GREEN,
					Color.BLUE
			};
			ColorStateList myList = new ColorStateList(states, colors);
			holder.mIdView.setBackgroundTintList(myList);
			*/

            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (twoWayDisplay) {
                        Bundle arguments = new Bundle();
                        arguments.putString(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);
                        ItemDetailFragment fragment = new ItemDetailFragment();
                        fragment.setArguments(arguments);
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.item_detail_container, fragment)
                                .commit();
                    } else {
                        Context context = v.getContext();
                        Intent intent = new Intent(context, ItemDetailActivity.class);
                        intent.putExtra(ItemDetailFragment.ARG_ITEM_ID, holder.mItem.id);

                        context.startActivity(intent);
                    }
                }
            });
        }
	
	
		/*
		TODO: Increase performance for list item generation
		Quickly scrolling down the list produces noticeable drops in
		performance - this is barely acceptable!
		- serialize items on startup, then load them everytime instead?
		 */
		private void setViewHolderBitmap(ViewHolder holder, int position) {
			Bitmap myItemPreview = BitmapFactory.decodeFile(ItemDetailFragment.findPreviewImage(position, 0));
			Bitmap scaledItemPreview = Bitmap.createScaledBitmap(myItemPreview, 512, 512, false);
			holder.mImageView.setImageBitmap(scaledItemPreview);
		}
	
		@Override
        public int getItemCount() {
            return mValues.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public final View mView;
            public final TextView mIdView;
            public final TextView mContentView;
            public DummyContent.DummyItem mItem;
            public final ImageView mImageView;

            public ViewHolder(View view) {
                super(view);
                mView = view;
                mIdView = (TextView) view.findViewById(R.id.id);
                mContentView = (TextView) view.findViewById(R.id.content);
                mImageView = (ImageView) view.findViewById(R.id.listItem_previewPic);
            }

            @Override
            public String toString() {
                return super.toString() + " '" + mContentView.getText() + "'";
            }
        }
    }



    // Helper class to make our files visible without device reboot
    // taken from:
    // https://skotagiri.wordpress.com/2012/08/23/make-files-created-by-android-applications-visible-in-windows-without-a-reboot/
    public class MediaScannerHelper implements MediaScannerConnection.MediaScannerConnectionClient {

        public void addFile(String filename)
        {
            String [] paths = new String[1];
            paths[0] = filename;
            MediaScannerConnection.scanFile(getApplicationContext(), paths, null, this);
        }

        public void onMediaScannerConnected() {
        }

        public void onScanCompleted(String path, Uri uri) {
            Log.i("ScannerHelper","Scan done - path:" + path + " uri:" + uri);
        }
    }

}
