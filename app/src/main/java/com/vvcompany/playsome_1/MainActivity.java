package com.vvcompany.playsome_1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    TabLayout tabLayout;
    ViewPager viewPager;
    Uri audio_uri;
    SearchView searchView;
    String MY_SORT_PREF = "sortOrder";
    String searchChar;
    FrameLayout frameLayoutMini;
    public static ArrayList<AudioData> audioFiles;
    public static ArrayList<AudioData> albums = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        permisssion();
    }

    private void permisssion() {

        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            //request permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);

        }else{
            // permission granted
            initviewpager();
            audioFiles = getAllAudio(this);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                // permisssion granted
                Toast.makeText(MainActivity.this, "Please restart the app, If the list of songs does not appear.", Toast.LENGTH_LONG).show();
                initviewpager();
                audioFiles = getAllAudio(this);
            }else{
                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
            }
        }
    }

    private void initviewpager() {
        frameLayoutMini = findViewById(R.id.mini_PLayer_frame_layout);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());
        viewPageAdapter.add(new SongsFragment(), "Songs");
        viewPageAdapter.add(new AlbumFragment(), "Album");
        viewPageAdapter.add(new FavouriteFragment(), "Favourite");

        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        getIntentMethod();




    }


    private void getIntentMethod() {
        Intent intent = getIntent();
        String sender = intent.getStringExtra("sender");
        if (sender != null && sender.equals("gesture")){
            searchChar = intent.getStringExtra("searchChar");

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.searchBar);
        searchView = (SearchView) menuItem.getActionView();

        searchView.setOnQueryTextListener(searchListener);

        if (searchChar != null){
            searchView.setQuery(searchChar, false);
            Toast.makeText(getApplicationContext(), "Showing songs related to char : " + searchChar, Toast.LENGTH_SHORT).show();
        }

        return true; //super.onCreateOptionsMenu(menu)
    }

    SearchView.OnQueryTextListener searchListener = new SearchView.OnQueryTextListener() {
        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            String userInput = newText.toLowerCase();
            ArrayList<AudioData> myFiles = new ArrayList<>();

            for (AudioData song : audioFiles){
                char firstChar1 = song.getTitle().toLowerCase().charAt(0);


                if (userInput.length() == 1){
                    char firstChar2 = userInput.charAt(0);
                    if (firstChar1 == firstChar2){
                        myFiles.add(song);
                    }
                }
                else {
                    if (song.getTitle().toLowerCase().contains(userInput)){
                        myFiles.add(song);
                    }

                }

            }

//            for (int i = 0; i < audioFiles.size(); i++){
//                if ((audioFiles.get(i).getTitle().toLowerCase()).contains(newText.toLowerCase())){
//                    myFiles.add(audioFiles.get(i));
//                }
//            }

//            RecyclerViewAdapter.musicFiles = myFiles;
//            SongsFragment.recyclerViewAdapter.notifyDataSetChanged(); //ye b sahi hai

            SongsFragment.recyclerViewAdapter.updateList(myFiles);

            return true;
        }
    };

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        SharedPreferences.Editor editor = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE).edit();

        switch(item.getItemId()){
            case R.id.sortby_name:
                Toast.makeText(getApplicationContext(), "By Name", Toast.LENGTH_SHORT).show();
                editor.putString("orderType", "byName");
                editor.apply();
                this.recreate();
                return true;
            case R.id.sortby_date:
                Toast.makeText(getApplicationContext(), "By Date", Toast.LENGTH_SHORT).show();
                editor.putString("orderType", "byDate");
                editor.apply();
                this.recreate();
                return true;
            case R.id.sortby_size:
                Toast.makeText(getApplicationContext(), "By Size", Toast.LENGTH_SHORT).show();
                editor.putString("orderType", "bySize");
                editor.apply();
                this.recreate();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private ArrayList<AudioData> getAllAudio(Context context) {
        ArrayList<AudioData> tempAudioDataList = new ArrayList<>();
        ArrayList<String> dublicates = new ArrayList<>();
        SharedPreferences preferences = getSharedPreferences(MY_SORT_PREF, MODE_PRIVATE); // MY_SORT_PREF is kind of request code.
        String sortOrder = preferences.getString("orderType", "byName");
        albums.clear();


        audio_uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

//        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        // intent uri ki help se us place me jaaega jahan songs hai aur jaise hum phone gaane ki list
        // kholete hai file manager se usi tareh ye b khol dega ki aap gaane me touch kro aur gaana chalu jo jaae
//        startActivityForResult(intent, 100); // 100 request code hai mjhe laga activty me reult dega kutch..

        String[] projection = {
                MediaStore.Audio.Media.ALBUM,
                MediaStore.Audio.Media.TITLE,
                MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,
                MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media._ID
        };


        String order;



        switch(sortOrder){
//            case "byName":
//                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
//                break;
            case "byDate":
                order = MediaStore.MediaColumns.DATE_ADDED + " ASC";
                break;
            case "bySize":
                order = MediaStore.MediaColumns.SIZE + " DESC";
                break;
            default:
                order = MediaStore.MediaColumns.DISPLAY_NAME + " ASC";
            }

//        CursorLoader loader = new CursorLoader(MainActivity.this, audio_uri, projection,
//                null, null, order);
//        Cursor cursor = loader.loadInBackground();

        Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, order);


        if(cursor != null){
            while(cursor.moveToNext()){ // jab
                // pehli baar cursor.moveToNext() lagate hai tb cursor pehle element me jaata hai aur ye boolean value deta hai ki agla element hai ki nahi

                String album = cursor.getString(0);
                String title = cursor.getString(1);
                String duration = cursor.getString(2);
                String data = cursor.getString(3);
                String artist = cursor.getString(4);
                String id = cursor.getString(5);

                AudioData audioData = new AudioData(album, artist, id, data, duration, title);
                tempAudioDataList.add(audioData);

                if (!dublicates.contains(album)){
                    albums.add(audioData);
                    dublicates.add(album);
                }

            }cursor.close();
        }
        return tempAudioDataList;



    }

}