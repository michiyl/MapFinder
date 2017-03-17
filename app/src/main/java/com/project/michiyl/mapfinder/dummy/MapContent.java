package com.project.michiyl.mapfinder.dummy;

import android.support.annotation.NonNull;

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


        // compareTo will only look for the console name since this simply cannot be there twice.
        @Override
        public int compareTo(@NonNull MapItem mapItem) {
            return this.consoleName.compareTo(mapItem.consoleName);
        }
    }

}




