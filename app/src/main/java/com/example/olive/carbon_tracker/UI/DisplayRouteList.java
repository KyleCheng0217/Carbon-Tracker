package com.example.olive.carbon_tracker.UI;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.olive.carbon_tracker.Model.DatabaseHelper;
import com.example.olive.carbon_tracker.Model.Journey;
import com.example.olive.carbon_tracker.Model.SuperUltraInfoDataBaseHelper;
import com.example.olive.carbon_tracker.Model.Vehicle;
import com.example.olive.carbon_tracker.R;
import com.example.olive.carbon_tracker.Model.Route;
import com.example.olive.carbon_tracker.Model.Singleton;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * displays the route list the user has entered
 */
public class DisplayRouteList extends AppCompatActivity {
    private List<Route> RouteList = new ArrayList<Route>();
    Singleton singleton  = Singleton.getInstance();
    String currentRouteName;
    Vehicle _vehicle = singleton.getVehicle();
    private long _EditedJourneyID = singleton.getEditPostion_Journey();
    private SQLiteDatabase myDataBase;
    private SQLiteDatabase RouteDB;
    private String _day =   checkDayIsSingleDIgit(singleton.getUserDay());
    private String _month =  ChangeMonthInInt(singleton.getUserMonth());
    private String _year =  singleton.getUserYear();
    private String _date = _year+"-"+_month+"-"+_day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        SuperUltraInfoDataBaseHelper RouteDBhelper = new SuperUltraInfoDataBaseHelper(this);
        RouteDB = RouteDBhelper.getWritableDatabase();

        setContentView(R.layout.activity_display_route_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.ChooseRoute);
        setSupportActionBar(toolbar);
        EditRoute();
        showAllRoute();
        UserChooseRoute();
        setToolBar();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        showAllRoute();
    }

    private void EditRoute() {
        ListView RouteInfo = (ListView) findViewById(R.id.ROUTES);
        RouteInfo.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Intent EditIntent = AddNewRoute.makeIntent(DisplayRouteList.this);
                long DB_id = RouteList.get(position).getRouteDBId();
                singleton.setEditPosition_Route(DB_id);
                singleton.userEditRoute();
                startActivityForResult(EditIntent,0);
                return true;
            }
        });
    }

    private void UserChooseRoute() {
        ListView CarInfo = (ListView) findViewById(R.id.ROUTES);
        CarInfo.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (singleton.isEditingJourney()){
                    Route RouteOnClicker = RouteList.get(position);
                    long idPassedBack = RouteOnClicker.getRouteDBId();
                    String name = RouteOnClicker.getName();
                    int cityDst = RouteOnClicker.getHighwayDistance();
                    int highWayDst = RouteOnClicker.getHighwayDistance();
                    int totalDst = RouteOnClicker.getTotalDistance();
                    ContentValues cv = new ContentValues();
                    String mode;
                    if (singleton.checkTransportationMode() == 1){
                        mode = "Walk/bike";
                        double co2 = 0;
                        editJoutneyDB(_date,0,mode,mode,"N/A","N/A",0,0,0,"N/A",idPassedBack,name,cityDst,
                                highWayDst,totalDst,_EditedJourneyID,co2,70);
                    }
                    else if (singleton.checkTransportationMode() == 2){
                        mode = "Bus";
                        double co2 = (cityDst + highWayDst)*0.089;
                        editJoutneyDB(_date,0,mode,mode,"N/A","N/A",0,0,0,"N/A",idPassedBack,name,cityDst,
                                highWayDst,totalDst,_EditedJourneyID,co2,50);
                    }
                    else if (singleton.checkTransportationMode() == 3){
                        mode = "Skytrain";
                        double co2 = (cityDst + highWayDst)*0.033;
                        editJoutneyDB(_date,0,mode,mode,"N/A","N/A",0,0,0,"N/A",idPassedBack,name,cityDst,
                                highWayDst,totalDst,_EditedJourneyID,co2,60);
                    }
                    else{
                        mode = "Car";
                        double fuelCost;
                        if (_vehicle.getFuelType().toLowerCase().matches("diesel")) {
                            fuelCost = 10.16;
                        } else if (_vehicle.getFuelType().toLowerCase().matches("electricity")) {
                            fuelCost = 0;
                        } else {
                            fuelCost = 8.89;
                        }
                        double cityGas = (cityDst * 0.621371192 / _vehicle.getCity08());
                        double hwyGas = highWayDst * 0.621371192 / _vehicle.getHighway08();
                        double totalGas = cityGas + hwyGas;
                        double co2 = fuelCost * totalGas;
                        editJoutneyDB(_date,_vehicle.getVehicleDBId(),_vehicle.getName(),mode,_vehicle.getMake()
                                ,_vehicle.getModel(),_vehicle.getYear(),_vehicle.getCity08(),
                                _vehicle.getHighway08(),_vehicle.getFuelType(),idPassedBack,name,cityDst,
                                highWayDst,totalDst,_EditedJourneyID,co2,_vehicle.getIndexID());
                    }


                    singleton.userFinishEditJourney();
                    Intent userEditJourney = DisplayJourneyList.makeIntent(DisplayRouteList.this);
                    startActivity(userEditJourney);
                    singleton.userFinishAdd();
                    finish();
                }
                else {
                    Route userPickRoute = RouteList.get(position);
                    currentRouteName = userPickRoute.getName();
                    calculateCO2(userPickRoute);
                }
            }


        });
    }

    private void editJoutneyDB(String date, long CarId,String CarName, String Mode, String CarMake,
                               String CarModel, int CarYear, double CarCity08, double CarHwy08,
                               String CarFuelType,
                               long RouteId, String RouteName, int RouteCityDst,
                               int RouteHwyDst, int TotalDst, long JourneyID,double totalCO2,int imageid) {
        ContentValues cv = new ContentValues();
        cv.put(SuperUltraInfoDataBaseHelper.Journey_Date,date);

        cv.put(SuperUltraInfoDataBaseHelper.Journey_CarId,CarId);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_CarName, CarName);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_CarMode, Mode);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_CarMake,CarMake);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_CarModel,CarModel);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_CarYear,CarYear);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_CarCity,CarCity08);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_CarHwy,CarHwy08);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_CarFuelType,CarFuelType);

        cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteId, RouteId);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteName, RouteName);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteCityDist, RouteCityDst);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteHwyDist, RouteHwyDst);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteTotalDist, TotalDst);

        cv.put(SuperUltraInfoDataBaseHelper.Journey_CO2Emitted, totalCO2);
        cv.put(SuperUltraInfoDataBaseHelper.Journey_Image, imageid);


        long idPassBack = RouteDB.update(SuperUltraInfoDataBaseHelper.Journey_Table,cv,"_id="+JourneyID, null);
        RouteDB.close();

    }


    private void showAllRoute() {
        List<Route> RouteListFromDB = new ArrayList<Route>();
        myDataBase = SQLiteDatabase.openOrCreateDatabase(DatabaseHelper.DB_PATH + DatabaseHelper.DB_NAME,null);
        Cursor cursor = myDataBase.rawQuery("select " +
                "RouteName," +
                "RouteCityDistance," +
                "RouteHwyDistance," +
                "RouteTotalDistance, " +
                "_id " +
                "from RouteInfoTable",null);
        cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            String RouteName = cursor.getString(0);
            int RouteCityDistance = cursor.getInt(1);
            int RouteHwyDistance = cursor.getInt(2);
            int RouteToatalDistance = cursor.getInt(3);
            long routeDBId = cursor.getLong(cursor.getColumnIndex("_id"));
            Route tempRoute = new Route(RouteName,RouteCityDistance,RouteHwyDistance,RouteToatalDistance,routeDBId);
            RouteListFromDB.add(tempRoute);
            cursor.moveToNext();
        }
        cursor.close();
        RouteList = RouteListFromDB;
        ArrayAdapter<Route> adapter = new myArrayAdapter();
        ListView RoutesShown = (ListView)findViewById(R.id.ROUTES);
        RoutesShown.setAdapter(adapter);
    }

    private class myArrayAdapter extends ArrayAdapter<Route> {
        public myArrayAdapter(){
            super(DisplayRouteList.this, R.layout.single_element_route_list, RouteList);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View itemView = convertView;
            if (itemView == null) {
                itemView = getLayoutInflater().inflate(R.layout.single_element_route_list, parent, false);
            }
            Route currentRoute = RouteList.get(position);
            ImageView imageView = (ImageView) itemView.findViewById(R.id.RouteImage);
            imageView.setImageResource(currentRoute.getIconId());
            TextView RouteName = (TextView)itemView.findViewById(R.id.StartingDate);
            RouteName.setText("Name: "+currentRoute.getName());
            TextView RouteCityDst = (TextView)itemView.findViewById(R.id.EndingDate);
            RouteCityDst.setText("Distance in City: " + currentRoute.getCityDistance()+" KM");
            TextView RouteHwayDst = (TextView)itemView.findViewById(R.id.IndElecUsage);
            RouteHwayDst.setText("Distance in HighWay: "+ currentRoute.getHighwayDistance()+" KM");
            TextView RouteTotalDst = (TextView)itemView.findViewById(R.id.totalDistanceWImage);
            RouteTotalDst.setText("Total Distance: " + currentRoute.getTotalDistance()+" KM");
            return itemView;
        }
    }

    public void calculateCO2(Route userInput){
        int cityDistance = userInput.getCityDistance();
        int HwyDistance = userInput.getHighwayDistance();
        double totalCO2 = 0;

        if (singleton.checkTransportationMode() == 1) { // Walk/Bike
            String TotalCO2 = String.format("%.2f", totalCO2);
            if(singleton.checkCO2Unit() == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.co2_production, TotalCO2),
                        Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(), R.string.zero_emissions, Toast.LENGTH_SHORT).show();
            }


            ContentValues cv = new ContentValues();
            cv.put(SuperUltraInfoDataBaseHelper.Journey_Date,_date);

            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarName,"Walk/Bike");
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarMode,"Walk/Bike");

            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteId, userInput.getRouteDBId());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteName, userInput.getName());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteCityDist, userInput.getCityDistance());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteHwyDist, userInput.getHighwayDistance());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteTotalDist, userInput.getTotalDistance());

            cv.put(SuperUltraInfoDataBaseHelper.Journey_CO2Emitted, totalCO2);
            cv.put(SuperUltraInfoDataBaseHelper.Journey_Image, 70);

            long idPassBack = RouteDB.insert(SuperUltraInfoDataBaseHelper.Journey_Table,null,cv);
            RouteDB.close();

            createNewJourney(cityDistance,HwyDistance,totalCO2, 1);
        }
        else if (singleton.checkTransportationMode() == 2){ //Bus
            totalCO2 = (cityDistance+HwyDistance)*0.089;
            String TotalCO2 = String.format("%.2f", totalCO2);
            String HumanCO2 = String.format("%.2f", totalCO2/2.06);
            if(singleton.checkCO2Unit() == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.co2_production, TotalCO2),
                        Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),
                        getString(R.string.DifferentUnitForCO2Emitted, HumanCO2),
                        Toast.LENGTH_SHORT).show();
            }



            ContentValues cv = new ContentValues();
            cv.put(SuperUltraInfoDataBaseHelper.Journey_Date,_date);

            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarName,"Bus");
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarMode,"Bus");

            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteId, userInput.getRouteDBId());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteName, userInput.getName());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteCityDist, userInput.getCityDistance());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteHwyDist, userInput.getHighwayDistance());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteTotalDist, userInput.getTotalDistance());

            cv.put(SuperUltraInfoDataBaseHelper.Journey_CO2Emitted, totalCO2);
            cv.put(SuperUltraInfoDataBaseHelper.Journey_Image, 50);

            long idPassBack = RouteDB.insert(SuperUltraInfoDataBaseHelper.Journey_Table,null,cv);
            RouteDB.close();

            createNewJourney(cityDistance,HwyDistance,totalCO2, 2);
        }
        else if (singleton.checkTransportationMode() == 3){ //Skytrain
            totalCO2 = (cityDistance+HwyDistance)*0.02348;
            String TotalCO2 = String.format("%.2f", totalCO2);
            String HumanCO2 = String.format("%.2f", totalCO2/2.06);
            if(singleton.checkCO2Unit() == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.co2_production, TotalCO2),
                        Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),
                        getString(R.string.DifferentUnitForCO2Emitted, HumanCO2),
                        Toast.LENGTH_SHORT).show();
            }


            ContentValues cv = new ContentValues();
            cv.put(SuperUltraInfoDataBaseHelper.Journey_Date,_date);

            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarName,"Skytrain");
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarMode,"Skytrain");

            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteId, userInput.getRouteDBId());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteName, userInput.getName());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteCityDist, userInput.getCityDistance());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteHwyDist, userInput.getHighwayDistance());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteTotalDist, userInput.getTotalDistance());

            cv.put(SuperUltraInfoDataBaseHelper.Journey_CO2Emitted, totalCO2);
            cv.put(SuperUltraInfoDataBaseHelper.Journey_Image, 60);

            long idPassBack = RouteDB.insert(SuperUltraInfoDataBaseHelper.Journey_Table,null,cv);
            RouteDB.close();

            createNewJourney(cityDistance,HwyDistance,totalCO2, 3);
        }
        else {
            double cityConsume = singleton.getVehicle().getCity08();
            double HwyConsume = singleton.getVehicle().getHighway08();
            String fuelType = singleton.getVehicle().getFuelType();
            double fuelCost;
            if (fuelType.toLowerCase().matches("diesel")) {
                fuelCost = 10.16;
            } else if (fuelType.toLowerCase().matches("electricity")) {
                fuelCost = 0;
            } else {
                fuelCost = 8.89;
            }
            double cityGas = (cityDistance * 0.621371192 / cityConsume);
            double hwyGas = HwyDistance * 0.621371192 / HwyConsume;
            double totalGas = cityGas + hwyGas;
            totalCO2 = fuelCost * totalGas;
            String TotalCO2 = String.format("%.2f", totalCO2);
            String HumanCO2 = String.format("%.2f", totalCO2/2.06);
            if(singleton.checkCO2Unit() == 0){
                Toast.makeText(getApplicationContext(), getString(R.string.co2_production, TotalCO2),
                        Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getApplicationContext(),
                        getString(R.string.DifferentUnitForCO2Emitted, HumanCO2),
                        Toast.LENGTH_SHORT).show();
            }

            Cursor cursor = RouteDB.rawQuery("select max(JourneyCO2Emitted) from JourneyInfoTable" +
                    " where JourneyMode = 'Car'",null);
            double maxCO2 = 0;
            cursor.moveToFirst();
            while (!cursor.isAfterLast()){
                maxCO2 = cursor.getDouble(0);
                cursor.moveToNext();
            }
            cursor.close();
            if (maxCO2 < totalCO2){
                singleton.setCarCO2Highest(true);
                singleton.setHighestCO2FromCar(totalCO2);
            }


            ContentValues cv = new ContentValues();
            cv.put(SuperUltraInfoDataBaseHelper.Journey_Date,_date);

            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarId,_vehicle.getVehicleDBId());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarName, _vehicle.getName());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarMode, "Car");
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarMake,_vehicle.getMake());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarModel,_vehicle.getModel());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarYear,_vehicle.getYear());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarCity,_vehicle.getCity08());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarHwy,_vehicle.getHighway08());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_CarFuelType,_vehicle.getFuelType());

            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteId, userInput.getRouteDBId());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteName, userInput.getName());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteCityDist, userInput.getCityDistance());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteHwyDist, userInput.getHighwayDistance());
            cv.put(SuperUltraInfoDataBaseHelper.Journey_RouteTotalDist, userInput.getTotalDistance());

            cv.put(SuperUltraInfoDataBaseHelper.Journey_CO2Emitted, totalCO2);
            cv.put(SuperUltraInfoDataBaseHelper.Journey_Image, _vehicle.getIndexID());

            long idPassBack = RouteDB.insert(SuperUltraInfoDataBaseHelper.Journey_Table,null,cv);
            RouteDB.close();


            createNewJourney(cityDistance, HwyDistance, totalCO2, 0);
        }
        if (singleton.isEditingJourney()) {
            singleton.userFinishEditJourney();
            Intent userEditJourney = DisplayJourneyList.makeIntent(DisplayRouteList.this);
            startActivity(userEditJourney);
        } else {
            Intent ConfirmCar = MainMenu.makeIntent(DisplayRouteList.this);
            startActivity(ConfirmCar);
        }
        Intent goBackToMainMenu = MainMenu.makeIntent(DisplayRouteList.this);
        startActivity(goBackToMainMenu);
    }

    public void onBackPressed() {
        Intent goBackToDisplayCar = DisplayCarList.makeIntent(DisplayRouteList.this);
        startActivity(goBackToDisplayCar);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, DisplayRouteList.class);
    }

    private void createNewJourney(int cityDistance,int hwyDistance,double co2, int TransMode){
        DateFormat df = new SimpleDateFormat("EEE, MMM d, ''yy");
        DisplayCalendar calendar = new DisplayCalendar();

        //Calendar calendar = new Calendar();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");

        String day =   singleton.getUserDay();
        String month =  singleton.getUserMonth();
        String year =  singleton.getUserYear();
        DecimalFormat Format = new DecimalFormat("#.##");
        double CO2 = Double.valueOf(Format.format(co2));
        String VehicleName = "";
        int imageid=0;
        switch(TransMode){
            case 0:
                VehicleName = _vehicle.getName();
                imageid = _vehicle.getIndexID();
                break;
            case 1:
                VehicleName = "Walk/Bike";
                imageid = 70;
                break;
            case 2:
                VehicleName = "Bus";
                imageid = 50;
                break;
            case 3:
                VehicleName = "Skytrain";
                imageid = 60;
                break;
        }

        long temp = 9999;
        Journey journey = new Journey(day+"/"+month+"/"+year,VehicleName,currentRouteName,
                (cityDistance+hwyDistance), VehicleName, CO2, temp,imageid);
        if (singleton.isEditingJourney()) {
            singleton.changeJourney(journey);
        }
    }
    private String checkDayIsSingleDIgit(String userDay) {
        if (userDay.length() == 1){
            return "0"+userDay;
        }else{
            return userDay;
        }
    }
    private String ChangeMonthInInt(String _month) {
        if (_month.matches("January")){
            return "01";
        }
        if (_month.matches("February")){
            return "02";
        }
        if (_month.matches("March")){
            return "03";
        }
        if (_month.matches("April")){
            return "04";
        }
        if (_month.matches("May")){
            return "05";
        }
        if (_month.matches("June")){
            return "06";
        }
        if (_month.matches("July")){
            return "07";
        }
        if (_month.matches("August")){
            return "08";
        }
        if (_month.matches("September")){
            return "09";
        }
        if (_month.matches("October")){
            return "10";
        }
        if (_month.matches("November")){
            return "11";
        }
        else{
            return "12";
        }
    }

    private void setToolBar(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LOW_PROFILE
                | View.SYSTEM_UI_FLAG_IMMERSIVE);
        Toolbar toolBar = (Toolbar) findViewById(R.id.toolbar_route);
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
        getMenuInflater().inflate(R.menu.toolbar_add_button, menu);
        return true;
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
            }
            saveCO2UnitStatus(singleton.checkCO2Unit());
            return true;
        }
        if(id == R.id.tool_about){
            startActivity(new Intent(DisplayRouteList.this, AboutActivity.class));
            return true;
        }
        if(id == R.id.tool_add){
            Intent AddIntent = AddNewRoute.makeIntent(DisplayRouteList.this);
            singleton.userAddRoute();
            startActivityForResult(AddIntent,1);
            finish();
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


