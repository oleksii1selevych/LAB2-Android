package com.example.lab2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.appcompat.widget.SearchView;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TITLE_SEARCH_KEY = "title";
    private static final String IMPRT_FILTER_KEY = "impRate";

    private String ukLanguageCode = "uk";

    private String[] spinnerItemText;
    private Integer[] spinnerItemImages = {
            R.drawable.ic_icon_importance_low,
            R.drawable.ic_icon_importance_low,
            R.drawable.ic_icon_importance_medium,
            R.drawable.ic_icon_importance_high};

    private static final int MEMBER_LOADER = 123;

    FloatingActionButton addButton;
    ListView notesList;
    NoteCursorAdapter noteCursorAdapter;

    private String searchText = "";
    private int importanceLevel = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            searchText = savedInstanceState.getString(TITLE_SEARCH_KEY);
            importanceLevel = savedInstanceState.getInt(IMPRT_FILTER_KEY);
        }

        spinnerItemText = new String[]{getString(R.string.status_imp_all), getString(R.string.status_imp_low), getString(R.string.status_imp_mid), getString(R.string.status_imp_high)};

        Log.d("locale", LocalizationManager.getLanguage(this));

        addButton = findViewById(R.id.addButtonId);
        notesList = findViewById(R.id.notesListId);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                startActivity(intent);
            }
        });

        noteCursorAdapter = new NoteCursorAdapter(this, null, false);
        notesList.setAdapter(noteCursorAdapter);

        registerForContextMenu(notesList);

        LoaderManager.getInstance(this).initLoader(MEMBER_LOADER, null, this);

        notesList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                Uri currentMemberUri = ContentUris.withAppendedId(DbHelper.Note.CONTENT_URI, id);
                intent.setData(currentMemberUri);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(TITLE_SEARCH_KEY, searchText);
        outState.putInt(IMPRT_FILTER_KEY, importanceLevel);

        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_activity_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView =
                (SearchView) searchItem.getActionView();


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchText = query;
                search();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchText = newText;
                search();
                return true;
            }
        });

        MenuItem spinnerItem = menu.findItem(R.id.filterSpinner);
        AppCompatSpinner spinnerView = (AppCompatSpinner) spinnerItem.getActionView();

        SpinnerAdapter spinnerAdapter = new SpinnerAdapter(this, R.layout.custom_spinner,
                spinnerItemText, spinnerItemImages);
        spinnerView.setAdapter(spinnerAdapter);
        spinnerView.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                importanceLevel = position;
                search();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                importanceLevel = 0;
                search();
            }
        });

        MenuItem changeLanguageItem = menu.findItem(R.id.changeLanguageBtn);
        changeLanguageItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
                builder1.setTitle(getString(R.string.change_language_label));
                String[] langs = new String[]{getString(R.string.ukrainian_lanuage), getString(R.string.enlish_lanuage)};
                builder1.setItems(langs, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == 0) {
                            Toast.makeText(MainActivity.this, "Ukrainian language", Toast.LENGTH_SHORT).show();
                            LocalizationManager.setLocale(MainActivity.this, ukLanguageCode);
                            recreate();

                        } else if (which == 1) {
                            Toast.makeText(MainActivity.this, "English language", Toast.LENGTH_SHORT).show();
                            LocalizationManager.setLocale(MainActivity.this, "en");
                            recreate();
                        }
                    }
                });
                AlertDialog alertDialog = builder1.create();
                alertDialog.show();

                return true;
            }
        });

        if (searchText != null) {
            searchView.setQuery(searchText, true);
        }
        if (importanceLevel > 0) {
            spinnerView.setSelection(importanceLevel);
        }
        return super.onCreateOptionsMenu(menu);

    }


    private void search() {
        Bundle bundle = new Bundle();
        if (!searchText.equals("") && searchText != null) {
            bundle.putString(TITLE_SEARCH_KEY, searchText);
        }
        if (importanceLevel > 0) {
            bundle.putInt(IMPRT_FILTER_KEY, importanceLevel - 1);
        }

        LoaderManager.getInstance(this).restartLoader(MEMBER_LOADER, bundle, this);

    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.context_menu, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
        switch (item.getItemId()) {
            case R.id.deleteItem:
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage(getString(R.string.delete_message));
                builder.setPositiveButton(getString(R.string.delete_btn_txt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Uri deleteNoteUri = ContentUris.withAppendedId(DbHelper.Note.CONTENT_URI, info.id);
                        int rowsDeleted = getContentResolver().delete(deleteNoteUri, null, null);

                        if (rowsDeleted == 0) {
                            Toast.makeText(MainActivity.this, "Deleting of data in the table failed", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(MainActivity.this, "Member is deleted", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                builder.setNegativeButton(getString(R.string.cancel_btn_txt), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (dialogInterface != null) {
                            dialogInterface.dismiss();
                        }
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
                return true;

            default:
                return super.onContextItemSelected(item);
        }
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selectionTitle = DbHelper.Note.NOTE_TITLE + " LIKE '%' || ? || '%' AND ";
        String selectionImportance = DbHelper.Note.NOTE_IMPORTANCE + " = ? AND ";

        String finalSelectionString = "";
        ArrayList<String> selectionArgs = new ArrayList<>();
        String[] selectionArgsArr = null;

        if (args != null) {
            if (args.containsKey(TITLE_SEARCH_KEY)) {
                finalSelectionString = finalSelectionString.concat(selectionTitle);
                selectionArgs.add(args.getString(TITLE_SEARCH_KEY));
            }

            if (args.containsKey(IMPRT_FILTER_KEY)) {
                finalSelectionString = finalSelectionString.concat(selectionImportance);
                selectionArgs.add(String.valueOf(args.getInt(IMPRT_FILTER_KEY)));
            }
        }
        if (!finalSelectionString.equals("")) {
            finalSelectionString = finalSelectionString.substring(0, finalSelectionString.length() - 5);
            selectionArgsArr = selectionArgs.toArray(new String[selectionArgs.size()]);
        } else {
            finalSelectionString = null;
            selectionArgs = null;
        }


        String[] projection = {
                DbHelper.Note.KEY_ID,
                DbHelper.Note.NOTE_TITLE,
                DbHelper.Note.ICON_SRC,
                DbHelper.Note.NOTE_IMPORTANCE,
                DbHelper.Note.CREATED_AT
        };

        CursorLoader cursorLoader = new CursorLoader(this,
                DbHelper.Note.CONTENT_URI,
                projection,
                finalSelectionString,
                selectionArgsArr,
                null);
        return cursorLoader;

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        noteCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        noteCursorAdapter.swapCursor(null);
    }
}
