package com.androprogramming.taskmanger.Activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androprogramming.taskmanger.Adapters.ListsAdapter;
import com.androprogramming.taskmanger.Databases.MainListsDatabase;
import com.androprogramming.taskmanger.Models.ListsModel;
import com.androprogramming.taskmanger.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class MainActivity extends AppCompatActivity implements ListsAdapter.onListsClick {

    MainListsDatabase mainListsDatabase;

    RoundedImageView userImage;
    TextView textViewUserName;

    List<ListsModel> modelList;
    RecyclerView recyclerLists;
    ListsAdapter adapterLists;

    FloatingActionButton actionButtonAdd;
    EditText editTextListName;

    ImageButton imageButtonImportant;

    ConstraintLayout constraintLayout;


    InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainListsDatabase = new MainListsDatabase(MainActivity.this);

        constraintLayout = findViewById(R.id.constraintMainActivity);

        imageButtonImportant = findViewById(R.id.imageButtonImportant);

        userImage = findViewById(R.id.roundedImageViewUserImage);
        textViewUserName = findViewById(R.id.textViewUserName);

        recyclerLists = findViewById(R.id.recyclerMainLists);

        actionButtonAdd = findViewById(R.id.fabAdd);
        editTextListName = findViewById(R.id.editTextListName);

        setListener();
        setUserInfo();
        addMainLists();
        setUpRecyclerLists();
        setUpAddList();
        setRecyclerActions();

        ad();
        setmInterstitialAd();

    }

    private void setListener(){
        imageButtonImportant.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(),Important.class));
        });
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        if (encodedImage != null) {
            byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        } else {
            return null;
        }
    }

    private void setUserInfo(){
        SharedPreferences sharedPreferences = getSharedPreferences("main",MODE_PRIVATE);
        textViewUserName.setText(sharedPreferences.getString("name",""));
        userImage.setImageBitmap(getBitmapFromEncodedString(sharedPreferences.getString("image","")));
    }

    private void addMainLists() {
        if (mainListsDatabase.getAll().isEmpty()) {
            mainListsDatabase.addOne(new ListsModel(0, "To-Do"));
        }
    }

    private void setUpAddList() {
        actionButtonAdd.setOnClickListener(view -> {
            if (editTextListName.getText().toString().isEmpty()) {
                editTextListName.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(editTextListName, InputMethodManager.SHOW_IMPLICIT);
            } else {

                if (mInterstitialAd != null) {
                    mInterstitialAd.show(MainActivity.this);
                    setmInterstitialAd();
                } else {
                    Log.d("TAG", "The interstitial ad wasn't ready yet.");
                }


                mainListsDatabase.addOne(new ListsModel(0, editTextListName.getText().toString()));
                setUpRecyclerLists();
                adapterLists.notifyDataSetChanged();
                hidemKeyboard(MainActivity.this);


                editTextListName.setText("");
            }
        });
    }

    private void setUpRecyclerLists() {
        modelList = mainListsDatabase.getAll();
        adapterLists = new ListsAdapter(this, modelList, this);


        recyclerLists.setAdapter(adapterLists);
        recyclerLists.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerLists.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    public void onListClick(int position) {
        Intent intent = new Intent(MainActivity.this, Tasks.class);
        intent.putExtra("listId", modelList.get(position).getId());
        startActivity(intent);
    }

    public static void hidemKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private Snackbar showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(this, constraintLayout, message, Snackbar.LENGTH_SHORT);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
        snackbar.show();
        return snackbar;
    }

    private void setRecyclerActions() {
        ItemTouchHelper.SimpleCallback callbackCompleted = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                ListsModel model = modelList.get(position);

                modelList.remove(position);
                adapterLists.notifyItemRemoved(position);
                mainListsDatabase.deleteOne(model);

                showSnackbar("Task deleted").setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        modelList.add(position, model);
                        adapterLists.notifyItemInserted(position);
                        mainListsDatabase.addOne(model);

                        mainListsDatabase = new MainListsDatabase(MainActivity.this);
                    }
                });
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                        .addBackgroundColor(Color.RED)
                        .addActionIcon(R.drawable.ic_baseline_delete_24)
                        .setActionIconTint(Color.WHITE)
                        .addCornerRadius(1, 10)
                        .create()
                        .decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelperCompleted = new ItemTouchHelper(callbackCompleted);
        itemTouchHelperCompleted.attachToRecyclerView(recyclerLists);
    }

    private void ad() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void setmInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-2477864030793539/4333003815", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        // The mInterstitialAd reference will be null until
                        // an ad is loaded.
                        mInterstitialAd = interstitialAd;

                        Log.i("TAG", "onAdLoaded");
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        // Handle the error
                        Log.d("TAG", loadAdError.toString());
                        mInterstitialAd = null;
                    }
                });


    }

}