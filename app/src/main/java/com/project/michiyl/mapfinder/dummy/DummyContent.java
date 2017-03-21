package com.project.michiyl.mapfinder.dummy;

import android.graphics.Path;
import android.provider.MediaStore;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class DummyContent {

    /**
     * An array of sample (dummy) items.
     */
    public static final List<DummyItem> ITEM_LIST = new ArrayList<DummyItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static final Map<String, DummyItem> ITEM_HASHMAP = new HashMap<String, DummyItem>();

    private static final int COUNT;// = 2; //only make this many entries

    static {
        // Add some sample items.

        int countOfDirectories = MapContent.MapItem.myMapDirectory.listFiles().length;

        File f = MapContent.MapItem.myMapDirectory;
        String[] names = f.list();
        for (String name : names) {
            Log.d("michiyl", "directories inside MapFinder/CoD4MW/ : " + name);
        }

        COUNT = countOfDirectories;

        for (int i = 1; i <= COUNT; i++) {
            addItem(createDummyItem(i));
        }
    }

    private static void addItem(DummyItem item) {
        ITEM_LIST.add(item);
        ITEM_HASHMAP.put(item.id, item);
    }

    private static DummyItem createDummyItem(int position) {
        // erzeuge ein neues DummyItem mit id, content, details
        //return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
        return new DummyItem(String.valueOf(position), "Item " + position, makeDetails(position));
    }

    private static String makeDetails(int position) {
        /*
        StringBuilder builder = new StringBuilder();
        builder.append("Details about Item: ").append(position);
        for (int i = 0; i < position; i++) {
            builder.append("\nMore details information here.");
        }
        return builder.toString();
        */

        int countOfDirectories = MapContent.MapItem.myMapDirectory.listFiles().length;
        if(countOfDirectories <= 0) {
            Log.e("michiyl", "ERROR --- NO DIRECTORY!");
            return "";
        }

        File theDirectory = MapContent.MapItem.myMapDirectory;
        String[] filenames = theDirectory.list();
        String sb = "";
        File myFile = null;

        // check which one we want to read from this time
        // FULL path necessary!
        myFile = new File(theDirectory.getAbsolutePath() + "/" + filenames[position-1], "/description.txt");

        Log.d("michiyl", "myFile Description: " + myFile.toString());
        Log.d("michiyl", "exists myFile Description: " + myFile.exists());
        Log.d("michiyl", "canRead myFile Description: " + myFile.canRead());

        try (Scanner br = new Scanner(new FileReader(myFile))) {
            // read just the lines
            if(br.hasNext())
                while( br.hasNext()) {
                    sb += br.nextLine() + "\n"; // it won't read \n without adding it
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.e("michiyl", "FileNotFoundException!", e);
        } catch (IOException e) {
            e.printStackTrace();
            Log.e("michiyl", "IOException!", e);
        }

        Log.d("michiyl", "sb: " + sb);
        return sb.toString();

    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class DummyItem {
        public final String id;
        public final String content;
        public final String details;

        public DummyItem(String id, String content, String details) {
            this.id = id;
            this.content = content;
            this.details = details;
        }

        @Override
        public String toString() {
            return content;
        }
    }
}
