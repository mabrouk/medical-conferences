package com.mabrouk.medicalconferences.persistance.sqlite;

import android.content.ContentValues;
import android.database.Cursor;

import com.mabrouk.medicalconferences.model.Topic;
import com.mabrouk.medicalconferences.model.User;

/**
 * Created by User on 12/3/2016.
 */

public class TopicMapper {
    public static Topic topicFromCursor(Cursor cursor) {
        Topic result = new Topic(cursor.getInt(cursor.getColumnIndex(TopicTable.COLUMN_ID)),
                cursor.getString(cursor.getColumnIndex(TopicTable.COLUMN_DESCRIPTION)),
                cursor.getLong(cursor.getColumnIndex(TopicTable.COLUMN_CREATED_AT)),
                cursor.getInt(cursor.getColumnIndex(TopicTable.COLUMN_CREATOR_ID)),
                cursor.getInt(cursor.getColumnIndex(TopicTable.COLUMN_CONFERENCE_ID)));
        if(cursor.getColumnIndex(UserTable.COLUMN_FIRST_NAME) != -1) {
            result.setCreator(new User(result.getCreatorId(), "",
                    cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_FIRST_NAME)),
                    cursor.getString(cursor.getColumnIndex(UserTable.COLUMN_LAST_NAME)),
                    "", User.ROLE_DOCTOR, 0)
            );
        }
        return result;
    }

    public static ContentValues valuesForTopic(Topic topic) {
        ContentValues values = new ContentValues();
        values.put(TopicTable.COLUMN_CONFERENCE_ID, topic.getConferenceId());
        values.put(TopicTable.COLUMN_DESCRIPTION, topic.getDescription());
        values.put(TopicTable.COLUMN_CREATOR_ID, topic.getCreatorId());
        values.put(TopicTable.COLUMN_CREATED_AT, topic.getCreatedAtTimestamp());
        return values;
    }

    private TopicMapper() {
        throw new IllegalStateException("Shouldn't instantiate instances of a Mapper, rather use static methods");
    }
}
