package com.example.lab2;

import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoteCursorAdapter extends CursorAdapter {

    private final int LOW_IMPORTANCE_RATE = R.drawable.ic_icon_importance_low;
    private final int MEDIUM_IMPORTANCE_RATE = R.drawable.ic_icon_importance_medium;
    private final int HIGH_IMPORTANCE_RATE = R.drawable.ic_icon_importance_high;

    public NoteCursorAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.note_list_view, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView iconImageView = view.findViewById(R.id.noteIconImageView);
        TextView titleTextView = view.findViewById(R.id.noteTitleTextView);
        ImageView importanceImageView = view.findViewById(R.id.noteImportanceValue);
        TextView creationTimeTextView = view.findViewById(R.id.noteCreationTimeTextView);

        String iconSource = cursor.getString(getColumnIndexByName(DbHelper.Note.ICON_SRC, cursor));
        String titleText = cursor.getString(getColumnIndexByName(DbHelper.Note.NOTE_TITLE, cursor));
        int importanceValue = cursor.getInt(cursor.getColumnIndexOrThrow(DbHelper.Note.NOTE_IMPORTANCE));
        String creationDate = cursor.getString(getColumnIndexByName(DbHelper.Note.CREATED_AT, cursor));



        if (iconSource != null) {
            Uri imageIconUri = Uri.parse(iconSource);
            //context.getContentResolver().takePersistableUriPermission(imageIconUri, Intent.FLAG_GRANT_READ_URI_PERMISSION);
            iconImageView.setImageURI(imageIconUri);
        }
        if (titleText != null) {
            titleTextView.setText(titleText);
        }
        DbHelper.Note.ImportanceRate noteRate = DbHelper.Note.ImportanceRate.values()[++importanceValue];
        int imageRateIcon = -1;
        switch (noteRate) {
            case MEDIUM:
                imageRateIcon = MEDIUM_IMPORTANCE_RATE;
                break;
            case HIGH:
                imageRateIcon = HIGH_IMPORTANCE_RATE;
                break;
            default:
                imageRateIcon = LOW_IMPORTANCE_RATE;
        }
        importanceImageView.setImageResource(imageRateIcon);
        if (creationDate != null) {
            creationTimeTextView.setText(creationDate);
        }
    }

    private int getColumnIndexByName(String columnName, Cursor c) {
        int index = c.getColumnIndex(columnName);
        if (index == -1)
            throw new Resources.NotFoundException("Wrong column name");
        return index;
    }
}
