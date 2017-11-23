package com.example.fud.spnew;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Helper_Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "MycoSearch.db";

    Context mContext;

    public Helper_Database(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS identified");
        onCreate(db);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(
                    "create table identified " +
                            "(id integer primary key autoincrement, date DATETIME DEFAULT CURRENT_TIMESTAMP, " +
                            "substrate text, " +
                            "top_picture blob, top_picture_scaled blob, " +
                            "top_species text, top_percentage text,  top_data text, " +
                            "underside_picture blob, underside_picture_scaled blob, " +
                            "underside_species text, underside_percentage text, underside_data text)"
            );

            db.execSQL("create table species " +
                    "(id integer primary key autoincrement, " +
                    "species text, colors text, texture text, substrate text, picture0 text, picture1 text, " +
                    "picture2 text)");

        } catch (SQLException e) {}

        initializeSpecies(db);
    }

    private void initializeSpecies(SQLiteDatabase db){
        ContentValues contentValues;
        String path = "picture-species/";

        contentValues = new ContentValues();
        contentValues.put("species", "Agaricaceae");
        contentValues.put("colors", "Gray, Brown, White, Yellow-brown, Black");
        contentValues.put("texture", "Smooth, Plicated, Crumbly, Scale-like, Web-like");
        contentValues.put("substrate", "Soil, Decaying wood, Dead wood, Animal manure");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "agaricaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Agaricales");
        contentValues.put("colors", "Yellow, Dark brown, Light brown, Orange");
        contentValues.put("texture", "Scale-like, Smooth");
        contentValues.put("substrate", "Soil, Decaying wood, Dead wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "agaricales" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Auriculariaceae");
        contentValues.put("colors", "Brown, Dark brown, Light brown");
        contentValues.put("texture", "Hairy underside, Smooth, Fleshy, Vein-like, Jelly-like");
        contentValues.put("substrate", "Decaying wood, Dead wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "auriculariaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Boletaceae");
        contentValues.put("colors", "Red, Pale green or pale blue underside stains");
        contentValues.put("texture", "Pored");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "boletaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Boletinellaceae");
        contentValues.put("colors", "Cream, Pink to light brown");
        contentValues.put("texture", "Pored, Smooth, Ridge-like");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "boletinellaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Cantharellaceae");
        contentValues.put("colors", "Yellow, White");
        contentValues.put("texture", "Smooth, Gills crowded, Leathery, Wrinkled");
        contentValues.put("substrate", "Soil, Dead branch");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "cantharellaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Ceratomyxa");
        contentValues.put("colors", "White");
        contentValues.put("texture", "Pored, Easily blown by the wind");
        contentValues.put("substrate", "Dead tree");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "ceratomyxa" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Clavariaceae");
        contentValues.put("colors", "Orange, Yellow, White");
        contentValues.put("texture", "Pasta-like, Thin, Spores are white, Coral-like");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "clavariaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Corticiaceae");
        contentValues.put("colors", "White, Yellow");
        contentValues.put("texture", "Canker, Disease-like to wood");
        contentValues.put("substrate", "Bark of tree");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "corticiaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Crepidotaceae");
        contentValues.put("colors", "Greyish white, Brown gills");
        contentValues.put("texture", "Smooth");
        contentValues.put("substrate", "Twig");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "crepidotaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Cudoniaceae");
        contentValues.put("colors", "Orange, Powdery white stripe");
        contentValues.put("texture", "Smooth");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "cudoniaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Fomitopsidaceae");
        contentValues.put("colors", "Orange to brown, White bottom and margin");
        contentValues.put("texture", "Tough, Woody, Leathery");
        contentValues.put("substrate", "Decaying wood, Dead wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "fomitopsidaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Ganodermataceae");
        contentValues.put("colors", "Pure black, White hymenium");
        contentValues.put("texture", "Tough, Woody");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "ganodermataceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Geastraceae");
        contentValues.put("colors", "Pale peach");
        contentValues.put("texture", "Warted, Rought, Flower-like");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "geastraceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Hydnaceae");
        contentValues.put("colors", "Light brown, Hymenium brown");
        contentValues.put("texture", "Toothed");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "hydnaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Hydnangiaceae");
        contentValues.put("colors", "Brown to white");
        contentValues.put("texture", "Convex and smooth, Gills subdistant");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "hydnangiaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Hygrophoraceae");
        contentValues.put("colors", "Red orange, Yellow orange, Bright colored, Light brown, Grayish brown, Flesh, White");
        contentValues.put("texture", "Convex and smooth, Conical when immature, Umbilicate");
        contentValues.put("substrate", "Soil, Wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "hygrophoraceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Hymenochaetaceae");
        contentValues.put("colors", "Yellow ochre, Yellow and brown bands, Brown, Chocolate brown");
        contentValues.put("texture", "Smooth edges, Central depression, Leathery, Tough");
        contentValues.put("substrate", "Twig, Wood, Dead wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "hymenochaetaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Hysteriaceae");
        contentValues.put("colors", "Black");
        contentValues.put("texture", "Boat-shaped, Growing in clusters");
        contentValues.put("substrate", "Bamboo");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "hysteriaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Lycogala");
        contentValues.put("colors", "Gray to white");
        contentValues.put("texture", "White spikes");
        contentValues.put("substrate", "Coconut bark, Wood debris");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "lycogala" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Marasmiaceae");
        contentValues.put("colors", "Brownish, White, Pinkish, Light orange, Yellow orange, Pale violet, Brownish-black, Reddish brown, Red");
        contentValues.put("texture", "Slimy, Smooth, Soft, Depressed, Crenate, Wavy, Convex");
        contentValues.put("substrate", "Twig, Leaf litter, Soil, Bark, Dead branch, Coconut husk, Decaying stump");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "marasmiaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Meruliaceae");
        contentValues.put("colors", "Light brown");
        contentValues.put("texture", "Rough, Irregular and flattened");
        contentValues.put("substrate", "Wood, Dead wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "meruliaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Mycenaceae");
        contentValues.put("colors", "White, Greyish brown");
        contentValues.put("texture", "Convex and smooth");
        contentValues.put("substrate", "Twig, Coconut rachis, Litter, Dead branch, Decaying wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "mycenaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Phaeolaceae");
        contentValues.put("colors", "Brown to dark orange");
        contentValues.put("texture", "Tough and woody");
        contentValues.put("substrate", "Wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "phaeolaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Pleurotaceae");
        contentValues.put("colors", "White, Cream white");
        contentValues.put("texture", "Smooth, Wavy, Crowded gills");
        contentValues.put("substrate", "Wood, Decaying wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "pleurotaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Pluteaceae");
        contentValues.put("colors", "Light brown to brown");
        contentValues.put("texture", "Smooth and convex, Gills compressed");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "pluteaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Podoscyphaceae");
        contentValues.put("colors", "Golden brown, Dark brown, White margin and hymenium");
        contentValues.put("texture", "Leathery, Cork-like");
        contentValues.put("substrate", "Dead trunk, Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "podoscyphaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Polyporaceae");
        contentValues.put("colors", "White to beige, White to yellow, Brown to white margin, Reddish brown to orange, Yellow, Cream white, Pinkish brown");
        contentValues.put("texture", "Pored, Tough and leathery, Fleshy, Wood-like, Smooth");
        contentValues.put("substrate", "Dead tree, Twig, Wood, Twig, Coconut roots, Decaying wood, Living tree, Tree stump");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "polyporaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Polyporales");
        contentValues.put("colors", "Yellow ochre, White, Pale grey to yellow");
        contentValues.put("texture", "Touch and bracket-like, Thin and not too tough");
        contentValues.put("substrate", "Decaying branch, Decaying wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "polyporales" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Psathyrellaceae");
        contentValues.put("colors", "Light brown, Brown, White to flesh");
        contentValues.put("texture", "Long and thin, Smooth and convex");
        contentValues.put("substrate", "Soil");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "psathyrellaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Sarcoscyphaceae");
        contentValues.put("colors", "Brown with white dotted pattern, Orange, Yellowish");
        contentValues.put("texture", "Hair-like appendages, Cup-like");
        contentValues.put("substrate", "Twig, Dead branch");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "sarcoscyphaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Schizophyllaceae");
        contentValues.put("colors", "White sporocarp, Flesh to brown hymenium");
        contentValues.put("texture", "Tough, Rough");
        contentValues.put("substrate", "Dead wood, Wood stump");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "schizophyllaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Stereaceae");
        contentValues.put("colors", "Yellow to white, Orange brown, Brown to grey, Brown to yellow bands, Brown and dark bands");
        contentValues.put("texture", "Thin and leathery, Short hairs");
        contentValues.put("substrate", "Dead wood, Decaying wood, Twig");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "stereaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Strophariaceae");
        contentValues.put("colors", "Khaki brown, Light brown, Gills yellowy white, Yellow ochre, Gray brown to orange, Pinkish to white, Yellow");
        contentValues.put("texture", "Convex and smooth, Wavy");
        contentValues.put("substrate", "Soil, Decaying wood, Coconut husk, Litter");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "strophariaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Theleporaceae");
        contentValues.put("colors", "Yellow orange to pale yellow, Peach to pale yellow");
        contentValues.put("texture", "Thin and leathery");
        contentValues.put("substrate", "Decaying wood");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "theleporaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Tremellaceae");
        contentValues.put("colors", "Translucent white");
        contentValues.put("texture", "Jelly-like");
        contentValues.put("substrate", "Dead wood, Log, Twig");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "tremellaceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);

        contentValues = new ContentValues();
        contentValues.put("species", "Tricholomataceae");
        contentValues.put("colors", "White, Off-white, Pale brown, Yellow-brown, Brown, ");
        contentValues.put("texture", "Gregarious, Convex and smooth, Depressed, Split margin");
        contentValues.put("substrate", "Soil, Litter, Coconut rachis, ");
        for(int i = 0; i < 3; i++){
            contentValues.put("picture" + Integer.toString(i), path + "tricholomataceae" + Integer.toString(i));
        }
        db.insert("species", null, contentValues);
    }

}
