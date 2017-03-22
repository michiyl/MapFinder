package com.project.michiyl.mapfinder.dummy;

import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Master on 17.03.2017.
 */

public class MapContent {

    Set<MapItem> setOfMaps;

    public MapContent() {
        setOfMaps = new HashSet(200);
    }

    public void addMapItem(String ingameName, String consoleName) {
        setOfMaps.add(new MapItem(ingameName, consoleName));
    }



    // ==================================================================================================
    // here is a single map entry
    public static class MapItem implements Comparable<MapItem> {

        private String ingameName;
        private String consoleName;
        private String description;
        public List<String> listOfImagePaths;

        public MapItem(String ingameName, String consoleName) {
            this.ingameName = ingameName;
            this.consoleName = consoleName;
        }

        public MapItem(String ingameName, String consoleName, String description) {
            this.ingameName = ingameName;
            this.consoleName = consoleName;
            this.description = description;
        }

        public MapItem(String ingameName, String consoleName, List<String> listOfImagePaths) {
            this.ingameName = ingameName;
            this.consoleName = consoleName;
            this.listOfImagePaths = listOfImagePaths;
        }

        public MapItem(String ingameName, String consoleName, String description, List<String> listOfImagePaths) {
            this.ingameName = ingameName;
            this.consoleName = consoleName;
            this.listOfImagePaths = listOfImagePaths;
            this.description = description;
        }

        // === Getter + Setter ===

        public String getConsoleName() {
            return consoleName;
        }

        public void setConsoleName(String newConsoleName) {
            this.consoleName = newConsoleName;
        }

        public String getIngameName() {
            return ingameName;
        }

        public void setIngameName(String newIngameName) {
            this.ingameName = newIngameName;
        }


        public String getDescription() {
            return description;
        }

        public void setDescription(String newDescription) {
            this.description = newDescription;
        }



        /**
         *
         * @param position        Which directory index are we going to read from?
         * @param readConsoleName If set to "true" method will read the <b>console</b> name file. <br>
         *                        If set to "false" method will read the <b>ingame</b> name file.
         * @return
         */
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
                // read just the single line
                sb.append(br.readLine());
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Log.e("michiyl", "FileNotFoundException!", e);
            } catch (IOException e) {
                e.printStackTrace();
                Log.e("michiyl", "IOException!", e);
            }

            //Log.d("michiyl", "sb: " + sb.toString());
            return sb.toString();
        }




        // compareTo will only look for the console name since this simply cannot be there twice.
        @Override
        public int compareTo(@NonNull MapItem mapItem) {
            return this.consoleName.compareTo(mapItem.consoleName);
        }


        void loadFromFile(String directoryNameOrConsoleName) {

            File myMapFile = new File(myMapDirectory.getAbsolutePath() + "/" + directoryNameOrConsoleName);

            if(!myMapFile.exists()) {
                return; // do nothing
            }
            else {

            }
        }


        // where is our MapFinder CoD4MW directory?
        /**
         * myMapDirectory is: <br>
         *     <b>/storage/sdcard/MapFinder/CoD4MW/</b> on pre-Android 5.0 <br>
         *     <b>/storage/emulated/0/MapFinder/CoD4MW</b> on Android 5+
         */
        public static File myMapDirectory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/MapFinder/CoD4MW/");

        private void mapper() {
            // wenn unter Dateipfad vorhanden, dann auslesen und anlegen
            File dummyMapDir = new File(myMapDirectory + "/mp_dummymap");
            if(myMapDirectory.exists()) {
                doWithFile(dummyMapDir, "name_ingame.txt", "Dummy Map");
                doWithFile(dummyMapDir, "name_console.txt", "mp_dummymap");
                doWithFile(dummyMapDir, "description.txt", "This is a \ndescription");
            } /* else: nothing */
        }

        private String getConsoleNameFromFile(File myfile) {


            try {
                BufferedReader inputReader2 = new BufferedReader(new InputStreamReader(new FileInputStream(myfile)));

                String inputString2;

                StringBuffer stringBuffer2 = new StringBuffer();

                while ((inputString2 = inputReader2.readLine()) != null) {

                    stringBuffer2.append(inputString2);
                }

                return stringBuffer2.toString();
            }
            catch (Exception e) {

            }
            return null;

        }

        private void doWithFile(File directory, String filenameWithExtension, String content) {
            if(!directory.exists()) {
                directory.mkdir();
            }
            File outputFile_names = new File(directory, "/"+filenameWithExtension);
            try {
                FileOutputStream fos = new FileOutputStream(outputFile_names, false); // don't append -> overwrite!
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                bos.write(content.toString().getBytes());
                bos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

}




