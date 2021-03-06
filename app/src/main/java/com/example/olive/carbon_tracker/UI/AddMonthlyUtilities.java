package com.example.olive.carbon_tracker.UI;

import android.app.Activity;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olive.carbon_tracker.Model.MonthlyUtilitiesData;
import com.example.olive.carbon_tracker.Model.Singleton;
import com.example.olive.carbon_tracker.Model.SuperUltraInfoDataBaseHelper;
import com.example.olive.carbon_tracker.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;
import static java.lang.Long.parseLong;

/**
 * adds and edits the monthly utility bill
 */
public class AddMonthlyUtilities extends AppCompatActivity {

    Singleton singleton = Singleton.getInstance();

    private List<MonthlyUtilitiesData> MonthlyUtilitiesList = new ArrayList<>();
    private SQLiteDatabase UtilityDB;
    private long position;
    MonthlyUtilitiesData _billToBeEdited;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_monthly_utilities);
        SuperUltraInfoDataBaseHelper UtilityDBhelper = new SuperUltraInfoDataBaseHelper(this);
        UtilityDB = UtilityDBhelper.getWritableDatabase();


        MonthlyUtilitiesList = singleton.getBillList();
        setupCalendarButton(R.id.ID_startDate_button);
        setupCalendarButton(R.id.ID_endDate_button);

        uesrWantToEditBill();
        viewCurrentDate();

        setToolBar();
    }

    private void uesrWantToEditBill() {
        if(singleton.checkEditMonthlyUtilities() == 1){
            position = singleton.getEditPosition_bill();
            String startDate = new String();
            String endDate = new String();
            double eleUsage = 0;
            double gasUsage = 0;
            long totalDay = 0;
            long peopleSharing = 0;
            double AverageCO2 = 0;
            long _id = 0;
            Cursor cursor = UtilityDB.rawQuery("select * from UtilityInfoTable" +
                            " where _id = " + position,
                    null);
            cursor.moveToFirst();
            while(!cursor.isAfterLast()){
                startDate = cursor.getString(1);
                endDate = cursor.getString(2);
                eleUsage = cursor.getDouble(3);
                gasUsage = cursor.getLong(4);
                totalDay = cursor.getLong(5);
                peopleSharing = cursor.getLong(6);
                AverageCO2 = cursor.getDouble(7);
                _id = cursor.getLong(0);
                cursor.moveToNext();
            }
            cursor.close();
            MonthlyUtilitiesData billToBeEdit = new MonthlyUtilitiesData(startDate,endDate,totalDay,
                    eleUsage/peopleSharing/totalDay,
                    gasUsage/peopleSharing/totalDay,
                    peopleSharing,AverageCO2,_id);
            _billToBeEdited = billToBeEdit;


            TextView StartDate = (TextView) findViewById(R.id.displayStartDate);
            TextView EndDate = (TextView) findViewById(R.id.DisplayEndDate);
            StartDate.setText(startDate);
            EndDate.setText(endDate);
            EditText elec = (EditText) findViewById(R.id.editElecUsage);
            EditText gas = (EditText) findViewById(R.id.editNaturalGasUsage);
            EditText people = (EditText) findViewById(R.id.editNum_people);
            elec.setText(""+eleUsage+"");
            gas.setText(""+gasUsage+"");
            people.setText(""+_billToBeEdited.getNumOfPeople()+"");
        }
    }

    private void setupCalendarButton(final int buttonID){
        Button btn = (Button) findViewById(buttonID);
        btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent setDate = new Intent(AddMonthlyUtilities.this, DisplayCalendar.class);
                if(buttonID == R.id.ID_startDate_button)
                    setDate.putExtra("AddMonthlyUtilities", 10);
                if(buttonID == R.id.ID_endDate_button)
                    setDate.putExtra("AddMonthlyUtilities", 20);
                startActivity(setDate);
            }
        });
    }

    private void viewCurrentDate(){
        boolean isStartDateChanged = singleton.isStartDateChanged();
        boolean isEndDateChanged = singleton.isEndDateChanged();
        TextView currentStartDate = (TextView) findViewById(R.id.displayStartDate);
        TextView currentEndDate = (TextView) findViewById(R.id.DisplayEndDate);
        if(!isStartDateChanged) {
            Date StartDate = new Date();
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");

            simpleDateFormat = new SimpleDateFormat("dd");
            String StartDay = simpleDateFormat.format(StartDate);

            simpleDateFormat = new SimpleDateFormat("MM");
            String StartMonth = simpleDateFormat.format(StartDate);

            simpleDateFormat = new SimpleDateFormat("yyyy");
            String StartYear = simpleDateFormat.format(StartDate);
            singleton.setStartDay(StartDay);
            singleton.setStartMonth(StartMonth);
            singleton.setStartYear(StartYear);
            currentStartDate.setText(StartDay + "/" + StartMonth + "/" + StartYear);
        }
        if(isStartDateChanged){
            super.onRestart();
            String startDay = singleton.getStartDay();
            String startMonth = singleton.getStartMonth();
            String startYear = singleton.getStartYear();
            currentStartDate.setText(startDay + "/" + startMonth + "/" + startYear);
            singleton.setStartDateChanged(false);
        }

        if(!isEndDateChanged){
            Date EndDate = new Date();
            SimpleDateFormat simpleEndDateFormat = new SimpleDateFormat("EEEE");

            simpleEndDateFormat = new SimpleDateFormat("dd");
            String EndDay = simpleEndDateFormat.format(EndDate);

            simpleEndDateFormat = new SimpleDateFormat("MM");
            String EndMonth = simpleEndDateFormat.format(EndDate);

            simpleEndDateFormat = new SimpleDateFormat("yyyy");
            String EndYear = simpleEndDateFormat.format(EndDate);
            singleton.setEndDay(EndDay);
            singleton.setEndMonth(EndMonth);
            singleton.setEndYear(EndYear);
            currentEndDate.setText(EndDay + "/" + EndMonth + "/" + EndYear);
        }
        if(isEndDateChanged){
            super.onRestart();
            String EndDay = singleton.getEndDay();
            String EndMonth = singleton.getEndMonth();
            String EndYear = singleton.getEndYear();
            currentEndDate.setText(EndDay + "/" + EndMonth + "/" + EndYear);
            singleton.setEndDateChanged(false);

        }
    }

    public void onRestart() {
        super.onRestart();
        TextView currentStartDate = (TextView) findViewById(R.id.displayStartDate);
        String startDay = singleton.getStartDay();
        String startMonth = singleton.getStartMonth();
        String startYear = singleton.getStartYear();
        currentStartDate.setText(startDay + "/" + startMonth + "/" + startYear);
        singleton.setStartDateChanged(true);

        TextView currentEndDate = (TextView) findViewById(R.id.DisplayEndDate);
        String EndDay = singleton.getEndDay();
        String EndMonth = singleton.getEndMonth();
        String EndYear = singleton.getEndYear();
        currentEndDate.setText(EndDay + "/" + EndMonth + "/" + EndYear);
        singleton.setEndDateChanged(true);
    }


    private void setupAdd() {
        String startDay = singleton.getStartDay();
        String startMonth = singleton.getStartMonth();
        String startYear = singleton.getStartYear();

        String EndDay = singleton.getEndDay();
        String EndMonth = singleton.getEndMonth();
        String EndYear = singleton.getEndYear();

        EditText ETelectricUsage = (EditText) findViewById(R.id.editElecUsage);
        EditText ETnaturalGasUsage = (EditText) findViewById(R.id.editNaturalGasUsage);
        EditText ETnumOfPeople = (EditText) findViewById(R.id.editNum_people);
        String electricUsage = ETelectricUsage.getText().toString();
        String naturalGasUsage = ETnaturalGasUsage.getText().toString();
        String numOfPeople = ETnumOfPeople.getText().toString();


        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            try {
                Date start = sdf.parse(startDay+"/"+startMonth+"/"+startYear);
                Date end = sdf.parse(EndDay+"/"+EndMonth+"/"+EndYear);
                long dateDifference = end.getTime() - start.getTime(); //convert date into msec
                dateDifference = dateDifference / 86400000;
                //convert time in msec back to days (1000*60*60*24 = 86400000)

                if(dateDifference > 0 && (!electricUsage.matches("") || !naturalGasUsage.matches(""))
                        && !numOfPeople.matches("") && parseInt(numOfPeople)!= 0){

                        if(!electricUsage.matches("") && naturalGasUsage.matches("")) {
                            //when no natural gas data given
                            double indElecUsage = parseDouble(electricUsage) / parseDouble(numOfPeople);
                            double indGasUsage = 0;
                            double indCO2 = indElecUsage*0.009 + indGasUsage*56.1;
                                //0.009kg CO2 per kwh of elec, 56.1kg CO2 per GJ of natural gas
                            String startDate = startYear+"-"+addZeroToDay(startMonth)+"-"+addZeroToDay(startDay);
                            String endDate = EndYear+"-"+addZeroToDay(EndMonth)+"-"+addZeroToDay(EndDay);
                            double CO2PerDayPerPerson = indCO2/dateDifference;

                            if(singleton.checkEditMonthlyUtilities() == 1){ //editing
                                Cursor cursor = UtilityDB.rawQuery("select max(UtilityAverageCO2) from UtilityInfoTable",null);
                                double maxCO2 = 0;
                                cursor.moveToFirst();
                                while (!cursor.isAfterLast()){
                                    maxCO2 = cursor.getDouble(0);
                                    cursor.moveToNext();
                                }
                                cursor.close();
                                if (maxCO2 < CO2PerDayPerPerson){
                                    singleton.setEnegyHighest(true);
                                    singleton.setHighestCO2FromEnegy(CO2PerDayPerPerson);
                                }
                                UpdateUtilityToDB(startDate,endDate , parseDouble(electricUsage),
                                        0, dateDifference,
                                        parseLong(numOfPeople), CO2PerDayPerPerson);
                                singleton.userFinishEditMonthlyUtilities();
                            }
                            else {
                                Cursor cursor = UtilityDB.rawQuery("select max(UtilityAverageCO2) from UtilityInfoTable",null);
                                double maxCO2 = 0;
                                cursor.moveToFirst();
                                while (!cursor.isAfterLast()){
                                    maxCO2 = cursor.getDouble(0);
                                    cursor.moveToNext();
                                }
                                cursor.close();
                                if (maxCO2 < CO2PerDayPerPerson){
                                    singleton.setEnegyHighest(true);
                                    singleton.setHighestCO2FromEnegy(CO2PerDayPerPerson);
                                }
                                addNewUtilityToDB(startDate,endDate , parseDouble(electricUsage),
                                        0, dateDifference,
                                        parseLong(numOfPeople), CO2PerDayPerPerson);
                                singleton.userFinishAdd_MonthlyUtilities();
                            }
                        }

                        else if(!naturalGasUsage.matches("") && electricUsage.matches("")) {
                            //when no electricity data given
                            double indElecUsage = 0;
                            double indGasUsage = parseDouble(naturalGasUsage) / parseDouble(numOfPeople);
                            double indCO2 = indElecUsage*0.009 + indGasUsage*56.1;
                                //0.009kg CO2 per kwh of elec, 56.1kg CO2 per GJ of natural gas
                            String startDate = startYear+"-"+addZeroToDay(startMonth)+"-"+addZeroToDay(startDay);
                            String endDate = EndYear+"-"+addZeroToDay(EndMonth)+"-"+addZeroToDay(EndDay);
                            double CO2PerDayPerPerson = indCO2/dateDifference;

                            if(singleton.checkEditMonthlyUtilities() == 1){ //editing
                                Cursor cursor = UtilityDB.rawQuery("select max(UtilityAverageCO2) from UtilityInfoTable",null);
                                double maxCO2 = 0;
                                cursor.moveToFirst();
                                while (!cursor.isAfterLast()){
                                    maxCO2 = cursor.getDouble(0);
                                    cursor.moveToNext();
                                }
                                cursor.close();
                                if (maxCO2 < CO2PerDayPerPerson){
                                    singleton.setEnegyHighest(true);
                                    singleton.setHighestCO2FromEnegy(CO2PerDayPerPerson);
                                }
                                UpdateUtilityToDB(startDate,endDate , 0,
                                        parseDouble(naturalGasUsage), dateDifference,
                                        parseLong(numOfPeople), CO2PerDayPerPerson);
                                singleton.userFinishEditMonthlyUtilities();
                            }
                            else {
                                Cursor cursor = UtilityDB.rawQuery("select max(UtilityAverageCO2) from UtilityInfoTable",null);
                                double maxCO2 = 0;
                                cursor.moveToFirst();
                                while (!cursor.isAfterLast()){
                                    maxCO2 = cursor.getDouble(0);
                                    cursor.moveToNext();
                                }
                                cursor.close();
                                if (maxCO2 < CO2PerDayPerPerson){
                                    singleton.setEnegyHighest(true);
                                    singleton.setHighestCO2FromEnegy(CO2PerDayPerPerson);
                                }
                                addNewUtilityToDB(startDate,endDate , 0,
                                        parseDouble(naturalGasUsage), dateDifference,
                                        parseLong(numOfPeople), CO2PerDayPerPerson);
                                singleton.userFinishAdd_MonthlyUtilities();
                            }
                        }

                        else{   //when both electricity and natural gas data are given
                            double indElecUsage = parseDouble(electricUsage) / parseDouble(numOfPeople);
                            double indGasUsage = parseDouble(naturalGasUsage) / parseDouble(numOfPeople);
                            double indCO2 = indElecUsage*0.009 + indGasUsage*56.1;
                                //0.009kg CO2 per kwh of elec, 56.1kg CO2 per GJ of natural gas
                            String startDate = startYear+"-"+addZeroToDay(startMonth)+"-"+addZeroToDay(startDay);
                            String endDate = EndYear+"-"+addZeroToDay(EndMonth)+"-"+addZeroToDay(EndDay);
                            double CO2PerDayPerPerson = indCO2/dateDifference;

                            if(singleton.checkEditMonthlyUtilities() == 1){ //editing
                                Cursor cursor = UtilityDB.rawQuery("select max(UtilityAverageCO2) from UtilityInfoTable",null);
                                double maxCO2 = 0;
                                cursor.moveToFirst();
                                while (!cursor.isAfterLast()){
                                    maxCO2 = cursor.getDouble(0);
                                    cursor.moveToNext();
                                }
                                cursor.close();
                                if (maxCO2 < CO2PerDayPerPerson){
                                    singleton.setEnegyHighest(true);
                                    singleton.setHighestCO2FromEnegy(CO2PerDayPerPerson);
                                }
                                UpdateUtilityToDB(startDate,endDate , parseDouble(electricUsage),
                                        parseDouble(naturalGasUsage), dateDifference,
                                        parseLong(numOfPeople), CO2PerDayPerPerson);
                                singleton.userFinishEditMonthlyUtilities();
                            }
                            else {
                                Cursor cursor = UtilityDB.rawQuery("select max(UtilityAverageCO2) from UtilityInfoTable",null);
                                double maxCO2 = 0;
                                cursor.moveToFirst();
                                while (!cursor.isAfterLast()){
                                    maxCO2 = cursor.getDouble(0);
                                    cursor.moveToNext();
                                }
                                cursor.close();
                                if (maxCO2 < CO2PerDayPerPerson){
                                    singleton.setEnegyHighest(true);
                                    singleton.setHighestCO2FromEnegy(CO2PerDayPerPerson);
                                }
                                addNewUtilityToDB(startDate,endDate,parseDouble(electricUsage),
                                        parseDouble(naturalGasUsage), dateDifference,
                                        parseLong(numOfPeople),CO2PerDayPerPerson);
                                singleton.userFinishAdd_MonthlyUtilities();
                            }
                        }
                    setLatestBill();
                    startActivity(new Intent(AddMonthlyUtilities.this, DisplayMonthlyUtilities.class));
                    finish();
                }
                else if(dateDifference <= 0){
                    Toast.makeText(AddMonthlyUtilities.this, R.string.EndDateGreaterThanStartDate,
                            Toast.LENGTH_LONG).show();
                }
                else if(electricUsage.matches("") && naturalGasUsage.matches("")){
                    Toast.makeText(AddMonthlyUtilities.this, R.string.FillAtLeast1UsageData,Toast.LENGTH_LONG).show();
                }
                else if(numOfPeople.matches("") || parseInt(numOfPeople)== 0){
                    Toast.makeText(AddMonthlyUtilities.this, R.string.ValueCannotBe0,
                            Toast.LENGTH_LONG).show();
                }


            }
            catch(Exception  e){
                Toast.makeText(AddMonthlyUtilities.this, R.string.PickADate, Toast.LENGTH_LONG).show();
            }
    }

    private String addZeroToDay(String startDay) {
        if(startDay.equals("1")){
            return "01";
        }        if(startDay.equals("2")){
            return "02";
        }        if(startDay.equals("3")){
            return "03";
        }        if(startDay.equals("4")){
            return "04";
        }        if(startDay.equals("5")){
            return "05";
        }        if(startDay.equals("6")){
            return "06";
        }        if(startDay.equals("7")){
            return "07";
        }        if(startDay.equals("8")){
            return "08";
        }        if(startDay.equals("9")){
            return "09";
        }else{
            return startDay;
        }
    }

    private long UpdateUtilityToDB(String startDate, String endDate, double electricUsage, double gasUsage,
                                   long dateDifference, long numOfPeople, double CO2PerDayPerPerson) {
        ContentValues cv = new ContentValues();
        cv.put(SuperUltraInfoDataBaseHelper.Utility_StartDate,startDate);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_EndDate,endDate);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_Electricy,electricUsage);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_Gas,gasUsage);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_TotalDay,dateDifference);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_NumberOfSharing,numOfPeople);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_AverageCO2,CO2PerDayPerPerson);
        long idPassBack = UtilityDB.update(SuperUltraInfoDataBaseHelper.Utility_Table,cv,
                "_id="+position, null);
        UtilityDB.close();
        return idPassBack;
    }

    private long addNewUtilityToDB(String startDate, String endDate, double electricUsage, double gasUsage,
                                   long dateDifference, long numOfPeople, double CO2PerDayPerPerson) {

        ContentValues cv = new ContentValues();
        cv.put(SuperUltraInfoDataBaseHelper.Utility_StartDate,startDate);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_EndDate,endDate);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_Electricy,electricUsage);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_Gas,gasUsage);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_TotalDay,dateDifference);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_NumberOfSharing,numOfPeople);
        cv.put(SuperUltraInfoDataBaseHelper.Utility_AverageCO2,CO2PerDayPerPerson);
        setLatestBill();
        cv.put(SuperUltraInfoDataBaseHelper.Utility_CreationDate, singleton.getLatestBill());
        long idPassBack = UtilityDB.insert(SuperUltraInfoDataBaseHelper.Utility_Table,null,cv);
        UtilityDB.close();
        return  idPassBack;
    }


    private void setupDelete(final long position){
        new AlertDialog.Builder(AddMonthlyUtilities.this)
                .setTitle("Delete Bill")
                .setMessage(R.string.BillWarning)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent del_intent = new Intent();
                        //TODO delete bill from database
                        UtilityDB.delete(SuperUltraInfoDataBaseHelper.Utility_Table,
                                "_id"+"="+position,null);
                        UtilityDB.close();
                        singleton.userFinishEditMonthlyUtilities();
                        setResult(Activity.RESULT_OK, del_intent);
                        Toast.makeText(AddMonthlyUtilities.this, R.string.BillHasBeenDeleted,
                                Toast.LENGTH_LONG).show();
                        startActivity(new Intent(AddMonthlyUtilities.this, DisplayMonthlyUtilities.class));
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                        singleton.userFinishEditMonthlyUtilities();
                        startActivity(new Intent(AddMonthlyUtilities.this, DisplayMonthlyUtilities.class));
                        finish();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert).show();
    }

    public void onBackPressed(){
        singleton.userFinishEditMonthlyUtilities();
        singleton.userFinishAdd_MonthlyUtilities();
        startActivity(new Intent(AddMonthlyUtilities.this, DisplayMonthlyUtilities.class));
        finish();
    }

    private void setLatestBill() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        singleton.setLatestBill(sdf.format(calendar.getTime()));
    }

    private void setToolBar(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_add_bill);
        setSupportActionBar(toolBar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(singleton.checkAdd_MonthlyUtilities() == 1){
            getMenuInflater().inflate(R.menu.toolbar_add_button, menu);
            return true;
        }
        else {
            getMenuInflater().inflate(R.menu.toolbar_check_delete_buttons, menu);
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.tool_change_unit){
            if(singleton.checkCO2Unit() == 0) {
                singleton.humanRelatableUnit();
                Toast.makeText(getApplicationContext(), R.string.UnitChangedToGarbageUnit, Toast.LENGTH_SHORT).show();
            }
            else {
                singleton.originalUnit();
                Toast.makeText(getApplicationContext(), R.string.UnitChangedToKG, Toast.LENGTH_SHORT).show();
            }saveCO2UnitStatus(singleton.checkCO2Unit());
            return true;
        }
        if(id == R.id.tool_about){
            startActivity(new Intent(AddMonthlyUtilities.this, AboutActivity.class));
            return true;
        }
        if(id == R.id.tool_add || id == R.id.tool_check){
            setupAdd();
            return true;
        }
        if(id == R.id.tool_delete){
            setupDelete(position);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void saveCO2UnitStatus(int status) {
        SharedPreferences prefs = this.getSharedPreferences("CO2Status", MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt("CO2 status", status);
        editor.apply();
    }
}
