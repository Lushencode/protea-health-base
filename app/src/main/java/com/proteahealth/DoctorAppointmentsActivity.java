package com.proteahealth;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.proteahealth.adapter.DoctorAppointmentAdapter;
import com.proteahealth.model.DoctorAppointment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;

public class DoctorAppointmentsActivity extends AppCompatActivity {

    // =========================================================
    // UI COMPONENTS
    // =========================================================

    private CalendarView doctorCalendar;
    private TextView tvSelectedDate;
    private RecyclerView rvDoctorAppointments;
    private View btnManageAvailability;


    // =========================================================
    // DATA
    // =========================================================

    private String selectedDate = "";

    private final HashSet<String> unavailableDays =
            new HashSet<>();

    private final ArrayList<DoctorAppointment> appointmentList =
            new ArrayList<>();

    private DoctorAppointmentAdapter adapter;


    // =========================================================
    // ON CREATE
    // =========================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);

        setContentView(
                R.layout.activity_doctor_appointment
        );

        ImageButton navDoctorProfile =
                findViewById(R.id.navDoctorProfile);

        ImageButton navDoctorPatients =
                findViewById(R.id.navDoctorPatients);

        ImageButton navDoctorHome =
                findViewById(R.id.navDoctorHome);

        ImageButton navDoctorAppointments =
                findViewById(R.id.navDoctorAppointments);

        ImageButton navDoctorQuestions =
                findViewById(R.id.navDoctorQuestions);


// PROFILE
        navDoctorProfile.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DoctorAppointmentsActivity.this,
                    ProfileActivity.class
            );

            startActivity(intent);
        });

        // home
        navDoctorHome.setOnClickListener(v -> {

            Intent intent = new Intent(
                    DoctorAppointmentsActivity.this,
                    DoctorDashboardActivity.class
            );

            startActivity(intent);
        });


// PATIENTS
        navDoctorPatients.setOnClickListener(v -> {

            Toast.makeText(
                    DoctorAppointmentsActivity.this,
                    "Coming soon",
                    Toast.LENGTH_SHORT
            ).show();
        });


        navDoctorAppointments.setBackgroundResource(
                R.drawable.nav_icon_glow
        );


// APPOINTMENTS


// QUESTIONS
        navDoctorQuestions.setOnClickListener(v -> {

            Toast.makeText(
                    DoctorAppointmentsActivity.this,
                    "Coming soon",
                    Toast.LENGTH_SHORT
            ).show();
        });


        // -----------------------------------------------------
        // EDGE TO EDGE
        // -----------------------------------------------------

        View mainView =
                findViewById(
                        R.id.main
                );

        if (mainView != null) {

            ViewCompat.setOnApplyWindowInsetsListener(
                    mainView,
                    (view, insets) -> {

                        Insets systemBars =
                                insets.getInsets(
                                        WindowInsetsCompat.Type.systemBars()
                                );

                        view.setPadding(
                                systemBars.left,
                                systemBars.top,
                                systemBars.right,
                                0
                        );

                        return insets;
                    }
            );
        }


        // -----------------------------------------------------
        // FIND VIEWS
        // -----------------------------------------------------

        doctorCalendar =
                findViewById(
                        R.id.doctorCalendar
                );

        tvSelectedDate =
                findViewById(
                        R.id.tvSelectedDate
                );

        rvDoctorAppointments =
                findViewById(
                        R.id.rvDoctorAppointments
                );

        btnManageAvailability =
                findViewById(
                        R.id.btnManageAvailability
                );


        // -----------------------------------------------------
        // RECYCLER VIEW
        // -----------------------------------------------------

        adapter =
                new DoctorAppointmentAdapter(
                        this,
                        appointmentList
                );

        rvDoctorAppointments.setLayoutManager(
                new LinearLayoutManager(this)
        );

        rvDoctorAppointments.setAdapter(
                adapter
        );


        // -----------------------------------------------------
        // TODAY
        // -----------------------------------------------------

        Calendar calendar =
                Calendar.getInstance();

        selectedDate =
                formatDate(
                        calendar.getTime()
                );

        tvSelectedDate.setText(
                "Today's Timetable • "
                        + selectedDate
        );

        loadAppointments(
                selectedDate
        );


        // -----------------------------------------------------
        // CALENDAR DATE SELECTION
        // -----------------------------------------------------

        doctorCalendar.setOnDateChangeListener(
                (view, year, month, dayOfMonth) -> {

                    Calendar selectedCalendar =
                            Calendar.getInstance();

                    selectedCalendar.set(
                            year,
                            month,
                            dayOfMonth
                    );

                    selectedDate =
                            formatDate(
                                    selectedCalendar.getTime()
                            );

                    tvSelectedDate.setText(
                            "Schedule • "
                                    + selectedDate
                    );

                    loadAppointments(
                            selectedDate
                    );
                }
        );


        // -----------------------------------------------------
        // MANAGE AVAILABILITY
        // -----------------------------------------------------

        btnManageAvailability.setOnClickListener(
                view ->
                        showManageAvailabilityDialog()
        );
    }


    // =========================================================
    // FORMAT DATE
    // =========================================================

    private String formatDate(Date date) {

        SimpleDateFormat dateFormat =
                new SimpleDateFormat(
                        "dd MMM yyyy",
                        Locale.ENGLISH
                );

        return dateFormat.format(date);
    }


    // =========================================================
    // LOAD APPOINTMENTS
    // =========================================================

    private void loadAppointments(
            String date
    ) {

        appointmentList.clear();


        // -----------------------------------------------------
        // UNAVAILABLE DAY
        // -----------------------------------------------------

        if (unavailableDays.contains(date)) {

            tvSelectedDate.setText(
                    "Schedule • "
                            + date
                            + " (Unavailable Day)"
            );

            appointmentList.add(
                    new DoctorAppointment(
                            "Full Day",
                            "No Patients Scheduled",
                            "Doctor Unavailable / Out of Office",
                            "UNAVAILABLE"
                    )
            );

            adapter.notifyDataSetChanged();

            return;
        }


        // -----------------------------------------------------
        // TEMPORARY APPOINTMENT DATA
        // -----------------------------------------------------

        appointmentList.add(
                new DoctorAppointment(
                        "08:30 AM",
                        "Thabo Mokoena",
                        "Diabetes Follow-up & Lab Review",
                        "CONFIRMED"
                )
        );


        appointmentList.add(
                new DoctorAppointment(
                        "09:15 AM",
                        "Sarah Jenkins",
                        "Hypertension Blood Pressure Check",
                        "PENDING"
                )
        );


        appointmentList.add(
                new DoctorAppointment(
                        "10:00 AM",
                        "Available Slot",
                        "Open for patient booking",
                        "AVAILABLE"
                )
        );


        appointmentList.add(
                new DoctorAppointment(
                        "11:00 AM",
                        "Lerato Khumalo",
                        "Routine Wellness Check & Prescription Refill",
                        "CONFIRMED"
                )
        );


        appointmentList.add(
                new DoctorAppointment(
                        "12:00 PM",
                        "Lunch Break / Blocked",
                        "Doctor Unavailable",
                        "UNAVAILABLE"
                )
        );


        appointmentList.add(
                new DoctorAppointment(
                        "02:00 PM",
                        "Sipho Dlamini",
                        "Asthma Symptom Review",
                        "PENDING"
                )
        );


        adapter.notifyDataSetChanged();
    }


    // =========================================================
    // MANAGE AVAILABILITY
    // =========================================================

    private void showManageAvailabilityDialog() {

        boolean isCurrentUnavailable =
                unavailableDays.contains(
                        selectedDate
                );


        String availabilityOption;

        if (isCurrentUnavailable) {

            availabilityOption =
                    "Mark Selected Day ("
                            + selectedDate
                            + ") as Available";

        } else {

            availabilityOption =
                    "Mark Selected Day ("
                            + selectedDate
                            + ") as Unavailable";
        }


        String[] options = {
                "Set Working Hours",
                "Block Time Slot",
                availabilityOption,
                "View Full Month Summary"
        };


        new AlertDialog.Builder(this)

                .setTitle(
                        "Manage Availability • "
                                + selectedDate
                )

                .setItems(
                        options,
                        (dialog, which) -> {

                            switch (which) {

                                // ---------------------------------
                                // WORKING HOURS
                                // ---------------------------------

                                case 0:

                                    showWorkingHoursDialog();

                                    break;


                                // ---------------------------------
                                // BLOCK TIME SLOT
                                // ---------------------------------

                                case 1:

                                    showBlockTimeDialog();

                                    break;


                                // ---------------------------------
                                // AVAILABLE / UNAVAILABLE DAY
                                // ---------------------------------

                                case 2:

                                    if (isCurrentUnavailable) {

                                        unavailableDays.remove(
                                                selectedDate
                                        );

                                        Toast.makeText(
                                                this,
                                                "Day marked as Available!",
                                                Toast.LENGTH_SHORT
                                        ).show();

                                    } else {

                                        unavailableDays.add(
                                                selectedDate
                                        );

                                        Toast.makeText(
                                                this,
                                                "Day marked as Unavailable",
                                                Toast.LENGTH_SHORT
                                        ).show();
                                    }


                                    loadAppointments(
                                            selectedDate
                                    );

                                    break;


                                // ---------------------------------
                                // MONTH SUMMARY
                                // ---------------------------------

                                case 3:

                                    Toast.makeText(
                                            this,
                                            "Month summary: "
                                                    + unavailableDays.size()
                                                    + " day(s) marked unavailable",
                                            Toast.LENGTH_LONG
                                    ).show();

                                    break;
                            }
                        }
                )

                .show();
    }


    // =========================================================
    // WORKING HOURS
    // =========================================================

    private void showWorkingHoursDialog() {

        String[] hours = {
                "08:00 AM - 04:00 PM",
                "08:30 AM - 04:30 PM",
                "09:00 AM - 05:00 PM",
                "10:00 AM - 06:00 PM"
        };


        new AlertDialog.Builder(this)

                .setTitle(
                        "Working Hours"
                )

                .setItems(
                        hours,
                        (dialog, which) -> {

                            Toast.makeText(
                                    this,
                                    "Working hours set to: "
                                            + hours[which],
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )

                .show();
    }


    // =========================================================
    // BLOCK TIME SLOT
    // =========================================================

    private void showBlockTimeDialog() {

        String[] times = {
                "09:00 AM - 09:30 AM",
                "10:00 AM - 10:30 AM",
                "11:00 AM - 11:30 AM",
                "12:00 PM - 01:00 PM",
                "02:00 PM - 02:30 PM"
        };


        new AlertDialog.Builder(this)

                .setTitle(
                        "Block Time Slot"
                )

                .setItems(
                        times,
                        (dialog, which) -> {

                            String startTime =
                                    times[which]
                                            .split(" - ")[0];


                            appointmentList.add(
                                    0,
                                    new DoctorAppointment(
                                            startTime,
                                            "Blocked Slot",
                                            "Doctor Blocked Time",
                                            "UNAVAILABLE"
                                    )
                            );


                            adapter.notifyDataSetChanged();


                            Toast.makeText(
                                    this,
                                    times[which]
                                            + " blocked",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }
                )

                .show();
    }
}