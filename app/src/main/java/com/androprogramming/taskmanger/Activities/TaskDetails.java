package com.androprogramming.taskmanger.Activities;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.androprogramming.taskmanger.Adapters.TasksAdapter;
import com.androprogramming.taskmanger.Databases.SubTasksDatabase;
import com.androprogramming.taskmanger.Databases.TasksDatabase;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;


public class TaskDetails extends AppCompatActivity implements TasksAdapter.onTaskClick {


    SubTasksDatabase subTasksDatabase;
    TasksDatabase tasksDatabase;

    TextView textViewTaskName, textViewTaskStatus, textViewTaskType;
    ImageButton imageButtonBack;
    ToggleButton toggleButtonIsImportant;

    RecyclerView recyclerTasks;
    TasksAdapter tasksAdapter;
    List<TasksModel> listTasks;

    EditText editTextNotes;

    EditText editTextSubTaskName;
    FloatingActionButton actionButtonAddTask;
    CheckBox checkBoxSubTaskIsImportant;
    TextView textViewAddDueDate;
    ConstraintLayout constraintLayoutAdd;

    int taskId;
    boolean isSubTask;

    Intent starterIntent;

    TasksModel tasksModel;

    InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_details);

        subTasksDatabase = new SubTasksDatabase(this);
        tasksDatabase = new TasksDatabase(this);


        textViewTaskName = findViewById(R.id.textViewTaskName);
        imageButtonBack = findViewById(R.id.imageButtonSubBack);
        toggleButtonIsImportant = findViewById(R.id.toggleButtonSubTaskImportant);
        textViewTaskStatus = findViewById(R.id.textViewTaskStatus);
        textViewTaskType = findViewById(R.id.textViewTaskType);

        recyclerTasks = findViewById(R.id.recyclerSubTasks);

        editTextNotes = findViewById(R.id.editTextAddNote);

        editTextSubTaskName = findViewById(R.id.editTextAddSubTaskName);
        actionButtonAddTask = findViewById(R.id.floatingActionButtonSubTaskAdd);
        checkBoxSubTaskIsImportant = findViewById(R.id.checkBoxAddSubIsImportant);
        textViewAddDueDate = findViewById(R.id.textViewAddSubDueDate);
        constraintLayoutAdd = findViewById(R.id.constraintLayoutAddSubTask);

        taskId = getIntent().getIntExtra("taskId", 0);
        isSubTask = getIntent().getBooleanExtra("isSubTask", false);


        starterIntent = getIntent();

        setListeners();
        setRecyclerTasks();
        setLists();
        setToggleButtonIsImportant();
        setTaskStatus();
        setRecyclerActions();

        ad();
        setmInterstitialAd();
    }

    private void setTaskStatus() {
        if (tasksModel.isChecked()) {
            textViewTaskStatus.setText("Completed");
            textViewTaskStatus.setTextColor(Color.GREEN);
        } else {
            textViewTaskStatus.setText("In Progress");
            textViewTaskStatus.setTextColor(Color.RED);
        }

        if (isSubTask) {
            textViewTaskType.setText("Sub Task");
        } else {
            textViewTaskType.setText("Task");
        }

        editTextNotes.setText(tasksModel.getTaskNotes());
    }

    private void setToggleButtonIsImportant() {
        toggleButtonIsImportant.setChecked(tasksModel.isImportant());

        toggleButtonIsImportant.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (!isSubTask) {
                    tasksDatabase.setImportant(tasksModel, b);
                } else {
                    subTasksDatabase.setImportant(tasksModel, b);
                }
            }
        });
    }

    private void setLists() {

        if (!isSubTask) {
            tasksModel = tasksDatabase.searchDataById(taskId);
        } else {
            tasksModel = subTasksDatabase.searchDataById(taskId);
        }

        textViewTaskName.setText(tasksModel.getTaskName());

    }

    private void setListeners() {
        imageButtonBack.setOnClickListener(view -> onBackPressed());

        textViewAddDueDate.setOnClickListener(view -> startDatePicker());

        actionButtonAddTask.setOnClickListener(view -> addSubTask());

        editTextNotes.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    constraintLayoutAdd.setVisibility(View.INVISIBLE);
                } else {
                    constraintLayoutAdd.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setRecyclerTasks() {
        listTasks = subTasksDatabase.getAll(taskId);
        tasksAdapter = new TasksAdapter(this, listTasks, this);

        recyclerTasks.setAdapter(tasksAdapter);
        recyclerTasks.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerTasks.setItemAnimator(new DefaultItemAnimator());
    }

    private void addSubTask() {
        if (editTextSubTaskName.getText().toString().isEmpty()) {
            editTextSubTaskName.requestFocus();
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(editTextSubTaskName, InputMethodManager.SHOW_IMPLICIT);
            return;
        }
        if (textViewAddDueDate.getText().toString().equals("Add due date")) {

            editTextSubTaskName.setError("Due Date required");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    textViewAddDueDate.performClick();
                }
            }, 1000);
            return;
        }

        if (!editTextSubTaskName.getText().toString().isEmpty() && !textViewAddDueDate.getText().toString().equals("Add due date")) {


            if (mInterstitialAd != null) {
                mInterstitialAd.show(TaskDetails.this);
                setmInterstitialAd();
            } else {
                Log.d("TAG", "The interstitial ad wasn't ready yet.");
            }

            subTasksDatabase.addOne(new TasksModel(0, editTextSubTaskName.getText().toString(), textViewAddDueDate.getText().toString(), "", false, checkBoxSubTaskIsImportant.isChecked(), taskId));
            setRecyclerTasks();

            editTextSubTaskName.setText("");
            editTextSubTaskName.clearFocus();
            textViewAddDueDate.setText("Add due date");
            checkBoxSubTaskIsImportant.setChecked(false);

            hideKeyboard(TaskDetails.this);

        } else Toast.makeText(this, "Error Adding the Task", Toast.LENGTH_SHORT).show();

    }

    private void startDatePicker() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            Calendar mCalendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(this, R.style.datepicker, new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    SimpleDateFormat simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
                    Calendar newDate = Calendar.getInstance();
                    newDate.set(year, month, dayOfMonth);
                    textViewAddDueDate.setText(simpledateformat.format(newDate.getTime()));
                }
            }, mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH));

            try {
                if (System.currentTimeMillis() < new SimpleDateFormat("dd-MM-yyyy").parse(tasksModel.getTaskDueDate()).getTime()) {

                    datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

                    datePickerDialog.getDatePicker().setMaxDate(new SimpleDateFormat("dd-MM-yyyy").parse(tasksModel.getTaskDueDate()).getTime());
                }else{
                    datePickerDialog.dismiss();
                    showSnackbar("Task is completed");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            datePickerDialog.setCancelable(true);
            datePickerDialog.show();
        }
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

    @Override
    public void onImportantClick(int position, boolean checked) {
        subTasksDatabase.setImportant(listTasks.get(position), checked);
    }

    @Override
    public void onCheckBoxClick(int position, boolean checked) {
        subTasksDatabase.setChecked(listTasks.get(position), checked);
    }

    @Override
    public void onTaskItemClick(int position) {
        starterIntent.putExtra("taskId", listTasks.get(position).getTaskId());
        starterIntent.putExtra("isSubTask", true);
        starterIntent.putExtra("isImportant", listTasks.get(position).isImportant());
        finish();
        startActivity(starterIntent);
    }

    @Override
    public boolean onTaskItemLongClick(int position) {
        Toast.makeText(this, "Long Click", Toast.LENGTH_SHORT).show();
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!isSubTask) {
            tasksDatabase.setNotes(tasksModel, editTextNotes.getText().toString());
        } else {
            subTasksDatabase.setNotes(tasksModel, editTextNotes.getText().toString());
        }
    }

    private Snackbar showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(this, constraintLayoutAdd, message, Snackbar.LENGTH_SHORT);
        snackbar.setAnimationMode(Snackbar.ANIMATION_MODE_SLIDE);
        snackbar.show();
        return snackbar;
    }

    private void setRecyclerActions() {

        ItemTouchHelper.SimpleCallback callback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();

                TasksModel tasksModel = listTasks.get(position);

                listTasks.remove(position);
                tasksAdapter.notifyItemRemoved(position);

                if (!isSubTask) {
                    tasksDatabase.deleteOne(tasksModel);
                } else {
                    subTasksDatabase.deleteOne(tasksModel);
                }

                showSnackbar("Task deleted").setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        listTasks.add(position, tasksModel);
                        tasksAdapter.notifyItemInserted(position);
                        if (isSubTask) {
                            subTasksDatabase.addOne(tasksModel);
                        } else tasksDatabase.addOne(tasksModel);
                    }
                });
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                new RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive).addBackgroundColor(Color.RED).addActionIcon(R.drawable.ic_baseline_delete_24).setActionIconTint(Color.WHITE).addCornerRadius(1, 10).create().decorate();
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(callback);
        itemTouchHelper.attachToRecyclerView(recyclerTasks);
    }

    private void ad() {
        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });

        AdView adView = findViewById(R.id.adViewTaskDetails);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void setmInterstitialAd() {
        AdRequest adRequest = new AdRequest.Builder().build();

        InterstitialAd.load(this, "ca-app-pub-2477864030793539/3474653062", adRequest, new InterstitialAdLoadCallback() {
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