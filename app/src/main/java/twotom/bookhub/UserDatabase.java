package twotom.bookhub;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

// TODO: Add wishList and myBooks tables
public final class UserDatabase {
    private UserDatabaseHelper databaseHelper;

    public UserDatabase(Context context) {
        databaseHelper = new UserDatabaseHelper(context);
    }

    private static class UserEntry {
        public static final String _ID                       = "_id";
        public static final String TABLE_NAME                = "users";
        public static final String COLUMN_NAME_USERNAME      = "username";
        public static final String COLUMN_NAME_PASSWORD      = "password";
        public static final String COLUMN_NAME_EMAIL         = "email";
        public static final String COLUMN_NAME_SELLER_RATING = "seller_rating";
        public static final String COLUMN_NAME_BUYER_RATING  = "buyer_rating";

        private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
            UserEntry._ID + " INTEGER PRIMARY KEY," +
            UserEntry.COLUMN_NAME_USERNAME + " TEXT," +
            UserEntry.COLUMN_NAME_PASSWORD + " TEXT," +
            UserEntry.COLUMN_NAME_EMAIL + " TEXT," +
            UserEntry.COLUMN_NAME_SELLER_RATING + " SMALLINT," +
            UserEntry.COLUMN_NAME_BUYER_RATING + " SMALLINT);";

        private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME;
    }

    private static class UserDatabaseHelper extends SQLiteOpenHelper {
        public static final int DATABASE_VERSION = 1;
        public static final String DATABASE_NAME = "UserContract.db";

        public UserDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase database) {
            database.execSQL(UserEntry.SQL_CREATE_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase database,
                              int oldVersion,
                              int newVersion)
        {
            database.execSQL(UserEntry.SQL_DELETE_ENTRIES);
            onCreate(database);
        }
    }

    public long insertUser(User user) {
        ContentValues values = new ContentValues();
        values.put(UserEntry.COLUMN_NAME_USERNAME,      user.getUsername());
        values.put(UserEntry.COLUMN_NAME_PASSWORD,      user.getPassword());
        values.put(UserEntry.COLUMN_NAME_EMAIL,         user.getEmail());
        values.put(UserEntry.COLUMN_NAME_SELLER_RATING, user.getSellerRating());
        values.put(UserEntry.COLUMN_NAME_BUYER_RATING,  user.getBuyerRating());
        return databaseHelper.getWritableDatabase()
                             .insert(UserEntry.TABLE_NAME, null, values);
    }

    public boolean userExists(String username) {
        String[] projection = {UserEntry.COLUMN_NAME_USERNAME};
        String[] selectionArgs = {username};
        Cursor cursor = databaseHelper.getReadableDatabase().query(
            UserEntry.TABLE_NAME,
            projection,
            UserEntry.COLUMN_NAME_USERNAME + " = ?",
            selectionArgs,
            null,
            null,
            null
        );
        int count = cursor.getCount();
        cursor.close();
        return count > 0;
    }

    public User readUser(String username) {
        String[] projection = {
            UserEntry.COLUMN_NAME_USERNAME,
            UserEntry.COLUMN_NAME_PASSWORD,
            UserEntry.COLUMN_NAME_EMAIL,
            UserEntry.COLUMN_NAME_SELLER_RATING,
            UserEntry.COLUMN_NAME_BUYER_RATING,
        };
        String selection = UserEntry.COLUMN_NAME_USERNAME + " = ?";
        String[] selectionArgs = {username};
        Cursor cursor = databaseHelper.getReadableDatabase().query(
            UserEntry.TABLE_NAME,
            projection,
            selection,
            selectionArgs,
            null,
            null,
            null
        );

        int count = cursor.getCount();
        if (count < 1) {
            cursor.close();
            return null;
        }

        cursor.moveToNext();
        User user = new User();
        user.setUsername(cursor.getString(
            cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_USERNAME)
        ));
        user.setPassword(cursor.getString(
            cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_PASSWORD)
        ));
        user.setEmail(cursor.getString(
            cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_EMAIL)
        ));
        user.setSellerRating(cursor.getInt(
            cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_SELLER_RATING)
        ));
        user.setBuyerRating(cursor.getInt(
            cursor.getColumnIndexOrThrow(UserEntry.COLUMN_NAME_BUYER_RATING)
        ));
        cursor.close();
        return user;
    }
}
