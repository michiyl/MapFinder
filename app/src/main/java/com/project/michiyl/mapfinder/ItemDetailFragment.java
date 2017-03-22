package com.project.michiyl.mapfinder;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.CollapsingToolbarLayout;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androidquery.AQuery;
import com.project.michiyl.mapfinder.dummy.DummyContent;
import com.project.michiyl.mapfinder.dummy.MapContent;

import java.io.File;
import java.io.FileOutputStream;

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

    static String[] imageIDs;

    static {
        File theDirectory = MapContent.MapItem.myMapDirectory;
        String[] filenames = theDirectory.list();
        File myimages = new File(theDirectory + "/" + filenames[0], "/images/");
        Log.d("michiyl", "static: " + myimages.getAbsolutePath() + " " + myimages.list().length);
        String[] filenames2 = myimages.list();

        if(myimages.list().length > 0) {
            imageIDs = new String[myimages.list().length];

            for (int i = 0; i < myimages.list().length; i++) {
                imageIDs[i] = theDirectory + "/" + filenames[0] + "/images/" + filenames2[i];
            }
        }
        else {
            imageIDs = new String[0];

        }
        //imageIDs = filenames[0]+"/images/"
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
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = DummyContent.ITEM_HASHMAP.get(getArguments().getString(ARG_ITEM_ID));
            myMapItem = new MapContent.MapItem("Test Map", "mp_testmap", "This is the test map description. \nAnd a new line.");

            Activity activity = this.getActivity();
            CollapsingToolbarLayout appBarLayout = (CollapsingToolbarLayout) activity.findViewById(R.id.toolbar_layout);
            if (appBarLayout != null) {
                appBarLayout.setTitle(mItem.content);
                appBarLayout.setTitle(myMapItem.getIngameName());
            }
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.item_detail_mic, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_detailText)).setText(mItem.details);
            //((TextView) rootView.findViewById(R.id.item_detailText)).setText(myMapItem.getDescription());
        }

        // == Grid view stuff ==
        GridView gridView = (GridView) rootView.findViewById(R.id.detail_gridview);
        gridView.setAdapter(new ImageAdapter(getActivity()));


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Toast.makeText(getActivity(), "pic " + position + " selected", Toast.LENGTH_SHORT).show();

            }
        });
        return rootView;
    }



    public class ImageAdapter extends BaseAdapter
    {
        private Context context;

        public ImageAdapter(Context c)
        {
            context = c;
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
            final SquareImageView imageView;
            if (convertView == null) {
                imageView = new SquareImageView(context);
                //imageView.setLayoutParams(new GridView.LayoutParams(200, 200));
                imageView.setLayoutParams(new GridView.LayoutParams(GridView.LayoutParams.MATCH_PARENT,
                                                                    GridView.LayoutParams.MATCH_PARENT));
                imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                imageView.setPadding(5, 5, 5, 5);

                // OnClickListener chaining:
                // the first listener "opens" the image:
                //   - it fills the invisible "big image" view with the image that was clicked on,
                //   - after that it turns it visible
                // this "big image" view also has a onClickListener which "closes" the image:
                //   - it turns the image invisible after clicking on it
                // TODO: increase performance
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
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
