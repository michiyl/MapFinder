package com.project.michiyl.mapfinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.project.michiyl.mapfinder.dummy.DummyContent;
import com.project.michiyl.mapfinder.dummy.MapContent;

import java.io.File;
import java.util.Arrays;


import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private DummyContent.DummyItem mItem;
    private MapContent.MapItem myMapItem;
    static Drawable myItemPreviewDrawable;  // image used in title bar of detail fragment
    static String[] imageIDs;   // the paths to the images inside "images" folder


    // here we look inside a specific directory position and count the amount of images,
    // then we initialize an array of Strings with this amount
    // and fill the array with the full path names
    public static void findPreviewImage(int position) {
        String slashImagesSlash = "/images/";
        File theDirectory = MapContent.MapItem.myMapDirectory;
        String[] filenames = theDirectory.list();
        File myimages = new File(theDirectory + "/" + filenames[position], slashImagesSlash);
        String[] filenames2 = myimages.list();
        if(filenames2.length == 0) {
            filenames2[0] = "";
        }

        if(myimages.list().length > 0) {
            imageIDs = new String[myimages.list().length];

            for (int i = 0; i < myimages.list().length; i++) {
                imageIDs[i] = theDirectory + "/" + filenames[position] + slashImagesSlash + filenames2[i];
            }
        }
        else {
            imageIDs = new String[0];
        }
    }

    // TODO: delete and consolidate unnecessary files
    // TODO: MOVE static stuff to a single class!
    // this method is just for the preview images
    public static String findPreviewImage(int position, int imageIndexInDirectory) {
        String slashImagesSlash = "/images/";
        File theDirectory = MapContent.MapItem.myMapDirectory;
        String[] filenames = theDirectory.list();
        File myimages = new File(theDirectory + "/" + filenames[position], slashImagesSlash);
        String[] filenames2 = myimages.list();
		Arrays.sort(filenames2);	// sort it so that image starting with "_" comes first
		String theSingleImagePath = theDirectory + "/" + filenames[position] + slashImagesSlash + filenames2[imageIndexInDirectory];

        if(myimages.list().length > 0) {
            return theSingleImagePath;
        }
        else {
			// if there is not a single image there, just take one from our asset list
			// TODO: check whether we still need to copy an image file
            Uri path = Uri.parse("android.resource://com.project.michiyl.mapfinder/" + R.drawable.default_loadingscreen);
            return path.toString();
        }
    }


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {

            int itemPosition = Integer.valueOf(String.valueOf(DummyContent.ITEM_HASHMAP.get(getArguments().getString(ARG_ITEM_ID))));
            Log.d("michiyl", "onCreate: " + DummyContent.ITEM_HASHMAP.get(getArguments().getString(ARG_ITEM_ID)));
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_HASHMAP.get(getArguments().getString(ARG_ITEM_ID));
            myMapItem = new MapContent.MapItem("Test Map", "mp_testmap", "This is the test map description. \nAnd a new line.");
            myMapItem.setIngameName(myMapItem.readNameFromFile(itemPosition-1,false));

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                //appBarLayout.setTitle(mItem.content);
                appBarLayout.setTitle(myMapItem.getIngameName());
                myItemPreviewDrawable = BitmapDrawable.createFromPath(ItemDetailFragment.findPreviewImage(itemPosition-1, 0));
                appBarLayout.setBackground(myItemPreviewDrawable);
            }
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail_mic, container, false);

        // Show the (dummy) content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_detailText)).setText(mItem.details);
            //((TextView) rootView.findViewById(R.id.item_detailText)).setText(myMapItem.getDescription());
        }

        CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) getActivity().findViewById(R.id.toolbar_layout);
        if (appBarLayout != null) {
            // when switching between Portrait and Landscape, re-assign the title bar image
            appBarLayout.setBackground(myItemPreviewDrawable);
			// the appBarLayout item detail also needs to have the correct title text!
			appBarLayout.setTitle(myMapItem.getIngameName());
		}


        // == Grid view stuff ==
        GridView gridView = (GridView) rootView.findViewById(R.id.detail_gridview);
        gridView.setAdapter(new ImageAdapter(getActivity()));
        // GridView onClickListener still necessary?
        /*
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "pic " + position + " selected", Toast.LENGTH_SHORT).show();
            }
        });
        */
        return rootView;
    }


    // ATTENTION: it gets messy in here - you've been warned!
    // TODO: clean up the messiness!
    public class ImageAdapter extends BaseAdapter
    {
        private Context context;

        public ImageAdapter(Context c)
        {
            context = c;

            int itemPosition = Integer.valueOf(String.valueOf(DummyContent.ITEM_HASHMAP.get(getArguments().getString(ARG_ITEM_ID))));
            Log.d("michiyl", "itemPosition: " + itemPosition);
            findPreviewImage(itemPosition-1);  // itemPosition starts with 1, we need 0-based
        }

        //---returns the number of images---
        public int getCount() {
            return imageIDs.length;
        }

        //---returns the ID of an item---
        public Object getItem(int position) {
            return position;
        }

        public long getItemId(int position) {
            return position;
        }

        //---returns an ImageView view---
        public View getView(final int position, View convertView, ViewGroup parent)
        {
            final ViewGroup parentViewGroup = parent;

            final SquareImageView imageView;
            if (convertView == null) {
                imageView = new SquareImageView(context);
                //imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setLayoutParams(new GridView.LayoutParams(MATCH_PARENT,
                                                                    MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);

                // OnClickListener "chaining":
                // the first listener "opens" the image:
                //   - it fills the invisible "big image" view with the image that was clicked on,
                //   - after that it turns it visible
                // this "big image" view also has a onClickListener which "closes" the image:
                //   - it turns the image invisible after clicking on it
                // TODO: increase performance while in "big image mode" if necessary
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // create WindowManager parameters with which we fill a
                        // WindowManager later on. Settings are:
                        // - MATCH_PARENT (fill the screen) for height and width
                        // - FLAG_LAYOUT_IN_SCREEN = show the (ImageView) view on the display,
                        //   wherever the user has scrolled to
                        // - translucent background pixels of this view
                        WindowManager.LayoutParams mWindowParams = new WindowManager.LayoutParams();
                        mWindowParams.height = WindowManager.LayoutParams.MATCH_PARENT;
                        mWindowParams.width = WindowManager.LayoutParams.MATCH_PARENT;
                        mWindowParams.flags = WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                        mWindowParams.format = PixelFormat.TRANSLUCENT;

                        Context mContext = getActivity();
                        final ImageView myBigImageView = new ImageView(mContext);   // our new ImageView
                        Bitmap bigImage = BitmapFactory.decodeFile(imageIDs[position]); // which image?
                        myBigImageView.setImageBitmap(bigImage);
                        myBigImageView.setVisibility(View.VISIBLE); // show it to the people
                        final WindowManager mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
                        myBigImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                myBigImageView.setVisibility(View.INVISIBLE);   // don't show it
                                mWindowManager.removeView(myBigImageView);  // fixes leakage
                                // TODO: leakage error when switching from portrait to landscape mode
                            }
                        });
                        mWindowManager.addView(myBigImageView, mWindowParams);  // add it and apply params

                        /*
                        // TODOdone: don't use existing ImageView, create fragment or dialog
                        final ImageView bigImageView = (ImageView) getActivity().findViewById(R.id.bigDetailImage);
                        Bitmap bigImage = BitmapFactory.decodeFile(imageIDs[position]);
                        bigImageView.setImageBitmap(bigImage);
                        bigImageView.setVisibility(View.VISIBLE);

                        bigImageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                bigImageView.setVisibility(View.INVISIBLE);
                            }
                        });
                        */
                    }
                });

            } else {
                imageView = (SquareImageView) convertView;
            }

            // just don't use images in the size of megabytes!
            Bitmap bmImg = BitmapFactory.decodeFile(imageIDs[position]);
            Bitmap scaled = Bitmap.createScaledBitmap(bmImg, 256, 256, false);
            // TODO: check the filesize and display a warning above a certain limit
            imageView.setImageBitmap(scaled);

            return imageView;
        }
    }



    public class SquareImageView extends android.support.v7.widget.AppCompatImageView {
        public SquareImageView(Context context) {
            super(context);
        }

        public SquareImageView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
            super(context, attrs, defStyle);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            setMeasuredDimension(getMeasuredWidth(), getMeasuredWidth()); //Snap to width
        }
    }
}
