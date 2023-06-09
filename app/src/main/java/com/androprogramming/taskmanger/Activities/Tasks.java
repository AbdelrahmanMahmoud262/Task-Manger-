package com.androprogramming.taskmanger.Activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androprogramming.taskmanger.Adapters.TasksAdapter;
import com.androprogramming.taskmanger.Databases.MainListsDatabase;
import com.androprogramming.taskmanger.Databases.TasksDatabase;
import com.androprogramming.taskmanger.Models.ListsModel;
import com.androprogramming.taskmanger.Models.TasksModel;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

public class Tasks extends AppCompatActivity {

    MainListsDatabase mainListsDatabase;
    TasksDatabase tasksDatabase;

    ConstraintLayout constraintLayout;

    ImageButton imageButtonBack;
    AppCompatImageButton imageButtonMore;
    TextView textViewListName;

    RecyclerView recyclerInProgress, recyclerCompleted;

    List<TasksModel> listInProgressTasks, listCompletedTasks;
    TasksAdapter adapterInProgress, adapterCompleted;

    ListsModel listModel;
    List<TasksModel> allTasks;

    FloatingActionButton actionButtonAddTask;
    EditText editTextTaskName;
    CheckBox checkBoxAddIsImportant;
    TextView textViewAddDueDate;

    PopupMenu dropDownMenu;
    Menu menu;

    int listId;

    InterstitialAd mInterstitialAd;


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);

        mainListsDatabase = new MainListsDatabase(this);
        tasksDatabase = new TasksDatabase(this);

        listId = getIntent().getIntExtra("listId", 0);
        dropDownMenu = new PopupMenu(this, imageButtonMore);
        menu = dropDownMenu.getMenu();


        allTasks = tasksDatabase.getAll();

        constraintLayout = findViewById(R.id.constraintTasks);

        imageButtonBack = findViewById(R.id.imageButtonBack);
        imageButtonMore = findViewById(R.id.imageButtonMoreMenu);
        textViewListName = findViewById(R.id.textViewTaskListName);

        actionButtonAddTask = findViewById(R.id.floatingActionButtonTaskAdd);

        recyclerCompleted = findViewById(R.id.recyclerCompletedTasks);
        recyclerInProgress = findViewById(R.id.recyclerInProgressTasks);

        editTextTaskName = findViewById(R.id.editTextAddTaskName);
        checkBoxAddIsImportant = findViewById(R.id.checkBoxAddIsImportant);
        textViewAddDueDate = findViewById(R.id.textViewAddDueDate);


        setUpMenu();
        setListeners();
        setRecyclerInProgress();
        setRecyclerCompleted();
        setListAll();
        setRecyclerActionsInProgress();
        setRecyclerActionsCompleted();

        ad();
        setmInterstitialAd();
    }

    private void setUpMenu() {
        dropDownMenu.getMenuInflater().inflate(R.menu.task_menu, menu);


        dropDownMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.add_home_screen:
                        Toast.makeText(Tasks.this, "home screen", Toast.LENGTH_SHORT).show();
                        return true;

                }
                return false;
            }
        });

    }

    private void setListeners() {
        imageButtonBack.setOnClickListener(view -> onBackPressed());

//        imageButtonMore.setOnClickListener(view -> dropDownMenu.show());

        actionButtonAddTask.setOnClickListener(view -> addTask());

        textViewAddDueDate.setOnClickListener(view -> startDatePicker());
    }

    private void startDatePicker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Calendar mCalendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    SimpleDateFormat simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    textViewAddDueDate.setText(simpledateformat.format(newDate.getTime()));
                }
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.setCancelable(true);
            datePickerDialog.show();
        }
    }

    private void addTask() {
        if (editTextTaskName.getText().toString().isEmpty()) {

            editTextTaskName.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextTaskName, InputMethodManager.SHOW_IMPLICIT);

        } else if (textViewAddDueDate.getText().toString().equals("Add due date")) {

            editTextTaskName.setError("Due Date required");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textViewAddDueDate.performClick();
                }
            }, 1000);
            return;
        }

        if (!editTextTaskName.getText().toString().isEmpty() && !textViewAddDueDate.getText().toString().equals("Add due date")) {


            if (mInterstitialAd != null) {
                mInterstitialAd.show(Tasks.this);
                setmInterstitialAd();
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

            tasksDatabase.addOne(new TasksModel(0, editTextTaskName.getText().toString(), textViewAddDueDate.getText().toString(), "", false, checkBoxAddIsImportant.isChecked(), listId));
            setRecyclerInProgress();

            editTextTaskName.setText("");
            editTextTaskName.clearFocus();
            textViewAddDueDate.setText("Add due date");
            checkBoxAddIsImportant.setChecked(false);

            hideKeyboard(Tasks.this);
        }

    }

    private void setListAll() {
        listModel = mainListsDatabase.searchDataById(listId);

        textViewListName.setText(listModel.getListName());
    }

    private void setRecyclerInProgress() {
        listInProgressTasks = tasksDatabase.getInProgress(listId);
        adapterInProgress = new TasksAdapter(this, listInProgressTasks, new TasksAdapter.onTaskClick() {
            @Override
            public void onImportantClick(int position, boolean checked) {
                tasksDatabase.setImportant(listInProgressTasks.get(position), checked);
            }

            @Override
            public void onCheckBoxClick(int position, boolean checked) {
                tasksDatabase.setChecked(listInProgressTasks.get(position), checked);
            }

            @Override
            public void onTaskItemClick(int position) {
                Intent intent = new Intent(Tasks.this, TaskDetails.class);
                intent.putExtra("taskId", listInProgressTasks.get(position).getTaskId());
                startActivity(intent);
            }

            @Override
            public boolean onTaskItemLongClick(int position) {
                Toast.makeText(Tasks.this, "Long Click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        recyclerInProgress.setAdapter(adapterInProgress);
        recyclerInProgress.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerInProgress.setItemAnimator(new DefaultItemAnimator());
    }

    private void setRecyclerCompleted() {
        listCompletedTasks = tasksDatabase.getCompleted(listId);
        adapterCompleted = new TasksAdapter(this, listCompletedTasks, new TasksAdapter.onTaskClick() {
            @Override
            public void onImportantClick(int position, boolean checked) {
                tasksDatabase.setImportant(listCompletedTasks.get(position), checked);
            }

            @Override
            public void onCheckBoxClick(int position, boolean checked) {
                tasksDatabase.setChecked(listCompletedTasks.get(position), checked);
            }

            @Override
            public void onTaskItemClick(int position) {
                Intent intent = new Intent(Tasks.this, TaskDetails.class);
                intent.putExtra("taskId", listCompletedTasks.get(position).getTaskId());
                startActivity(intent);
            }

            @Override
            public boolean onTaskItemLongClick(int position) {

                Toast.makeText(Tasks.this, "Long Click", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        recyclerCompleted.setAdapter(adapterCompleted);
        recyclerCompleted.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerCompleted.setItemAnimator(new DefaultItemAnimator());
    }

    public static void hideKeyboard(Activity activity) {
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

    private void setRecyclerActionsInProgress() {
        ItemTouchHelper.SimpleCallback callbackInProgress = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                TasksModel tasksModel = listInProgressTasks.get(position);

                listInProgressTasks.remove(position);
                adapterInProgress.notifyItemRemoved(position);
                tasksDatabase.deleteOne(tasksModel);

                showSnackbar("Task deleted").setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listInProgressTasks.add(position, tasksModel);
                        adapterInProgress.notifyItemInserted(position);
                        tasksDatabase.addOne(tasksModel);
                        tasksDatabase = new TasksDatabase(Tasks.this);
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

        ItemTouchHelper itemTouchHelperInProgress = new ItemTouchHelper(callbackInProgress);
        itemTouchHelperInProgress.attachToRecyclerView(recyclerInProgress);
    }

    private void setRecyclerActionsCompleted() {
        ItemTouchHelper.SimpleCallback callbackCompleted = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                TasksModel tasksModel = listCompletedTasks.get(position);

                listCompletedTasks.remove(position);
                adapterCompleted.notifyItemRemoved(position);
                tasksDatabase.deleteOne(tasksModel);

                showSnackbar("Task deleted").setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listCompletedTasks.add(position, tasksModel);
                        adapterCompleted.notifyItemInserted(position);
                        tasksDatabase.addOne(tasksModel);

                        tasksDatabase = new TasksDatabase(Tasks.this);
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
        itemTouchHelperCompleted.attachToRecyclerView(recyclerCompleted);
    }

    private void ad() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = findViewById(R.id.adViewTasks);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void setmInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-2477864030793539/9932814854", adRequest,
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