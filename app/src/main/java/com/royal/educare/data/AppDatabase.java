package com.royal.educare.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {User.class, Enrollment.class, TopicProgress.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    
    public abstract UserDao userDao();
    public abstract EnrollmentDao enrollmentDao();
    public abstract TopicProgressDao topicProgressDao();

    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "educare_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
