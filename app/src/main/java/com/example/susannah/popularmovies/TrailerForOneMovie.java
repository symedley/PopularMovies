package com.example.susannah.popularmovies;

/**
 * Created by Susannah on 5/11/2016.
 */
public class TrailerForOneMovie {
/*
        public static final String COLUMN_TMDID = "TmdID";
        public static final String COLUMN_KEY = "Key";
        public static final String COLUMN_NAME = "Name";
        public static final String COLUMN_SITE = "Site";
        public static final String COLUMN_SIZE = "Size";
        public static final String COLUMN_TYPE = "Type";
*/

    int tmdId;
    String key;
    String name;
    String site;
    String size;
    String type;

    public TrailerForOneMovie(int _tmdId, String _key, String _name, String _site, String _size, String _type){
        tmdId = _tmdId;
        key = _key;
        name = _name;
        site = _site;
        size = _size;
        type = _type;
    }
}
