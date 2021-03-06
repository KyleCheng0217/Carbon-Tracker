package com.example.olive.carbon_tracker.Model;


import android.app.Notification;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * singleton restricts the instantiation of a class to one object
 */
public class Singleton {
    private static Singleton currInstance = new Singleton();
    //    private String currenMake;
    private List<Vehicle> VehiclesList = new ArrayList<>();
    private List<Route> RouteList = new ArrayList<>();
    private List<MonthlyUtilitiesData> BillList = new ArrayList<>();
    private VehicleData vehicleData = new VehicleData();
    private List<String> getVehicleMakeArray = new ArrayList<>();
    private List<String> vehicleModelArray = new ArrayList<>();

    private String userDay = null;
    private String userMonth = null;
    private String userYear = null;
    private boolean isDateChanged = false;

    private String startDay = null;
    private String startMonth = null;
    private String startYear = null;
    private boolean isStartDateChanged = false;

    private String endDay = null;
    private String endMonth = null;
    private String endYear = null;
    private boolean isEndDateChanged = false;


    SQLiteDatabase CarInfoDB;

    private Notification notification;
    private boolean addJourneyToday = false;
    private String currentDate;
    private String latestBill;
//-------------------------------tips
    private List<String> ShuffledTipsForCar;
    private List<String> shuffledTipsForGas;
    private List<String> shuffledTipsForEnegy;
    private List<String> unrelatedTips;


    private double highestCO2FromCar;
    private double highestCO2FromEnegy;
    private double highestCO2FromGas;

    public boolean isCarCO2Highest() {
        return CarCO2Highest;
    }

    public void setCarCO2Highest(boolean carCO2Highest) {
        CarCO2Highest = carCO2Highest;
    }

    public boolean isEnegyHighest() {
        return EnegyHighest;
    }

    public void setEnegyHighest(boolean enegyHighest) {
        EnegyHighest = enegyHighest;
    }

    public boolean isGasHighest() {
        return GasHighest;
    }

    public void setGasHighest(boolean gasHighest) {
        GasHighest = gasHighest;
    }

    private boolean CarCO2Highest = false;
    private boolean EnegyHighest = false;
    private boolean GasHighest = false;

//-------------------------------tips


    ///////WHAT I NEED FOR MY CARBON CLASS /////////////
    private List<Journey> journeyList = new ArrayList<>();


    private static int editRoute;
    private static int editVehicle;
    private static int editMonthlyUtilities;
    private static long editPosition_Route;
    private static long editPosition_Car;

    public long getEditPostion_Journey() {
        return editPostion_Journey;
    }

    public void setEditPostion_Journey(long editPostion_Journey) {
        Singleton.editPostion_Journey = editPostion_Journey;
    }

    private static long editPostion_Journey;
    private static long editPosition_bill;
    private boolean editJourney = false;
    private int editJourneyPosition;
    private static int addRoute;
    private static int addVehicle;
    private static int addMonthlyUtilities;
    private static int deleteRoute;
    private static int TransportationMode;
    private static int CO2Unit;
    private static Singleton instance = new Singleton();
    private static Vehicle userPickVehicle;

    public static Singleton getInstance() {
        return instance;
    }


    private Singleton() {

    }

    public boolean getIsDateChanged(){

       return isDateChanged;

    }

    public void setIsDateChanged(boolean change){

        isDateChanged = change;

    }

    public void setVehicleData(Context context) {
        vehicleData.ExtractVehicleData(context);
    }

    public VehicleData getVehicleData() {
        return vehicleData;
    }

    public void setVehicleMakeArray() {
        getVehicleMakeArray = vehicleData.getUniqueVehicleMakeArray();
    }

    public List<String> getVehicleMakeArray() {
        return getVehicleMakeArray;
    }

    public List<String> getVehicleModelArray()

    {
        return vehicleModelArray;
    }
//    public List<Integer> getVehicleYearArray(){
//        return vehicleData.uniqueVehicleYearArray();
//    }


    public void setEditPosition_car(long Position) {
        editPosition_Car = Position;
    }

    public long getEditPosition_car() {
        return editPosition_Car;
    }

    public int getAddPosition_car() {
        int position = VehiclesList.size() - 1;
        return position;
    }

    public void userEditRoute_car() { editVehicle = 1;  }

    public int checkEdit_car() {
        return editVehicle;
    }

    public void userFinishEdit_car() { editVehicle = 0;  }

    public void userAddVehicle() {
        addVehicle = 1;
    }

    public void userFinishAdd_car() {
        addVehicle = 0;
    }

    public int checkAdd_car() {
        return addVehicle;
    }

    public void setVehiclesList(List<Vehicle> newVehicle) {
        VehiclesList = newVehicle;
    }

    public List<Vehicle> getVehicleList() {
        return VehiclesList;
    }

    public List<String> updateModels(String vehicleMake) {
        List<String> vehicleModelArray = vehicleData.getModelsForAMake(vehicleMake);
        return vehicleModelArray;
    }

    public List<String> updateDispl(String model, int year) {
        List<String> vehicleDispl = vehicleData.getDisplForVehicle(model, year);

        return vehicleDispl;
    }

    public List<Integer> updateYears(String vehicleModel) {
        List<Integer> vehicleYearArray = vehicleData.getYearsForAModel(vehicleModel);

        return vehicleYearArray;

    }

    public int getCityData(int position) {

        return vehicleData.getGiveCity(position);

    }

    public int getHwayData(int position) {
        return vehicleData.getGiveHway(position);

    }

    public String getFuelType(int position) {
        return vehicleData.getGiveFuel(position);
    }

    public void setUserPickVehicleItem(Vehicle vehicle) {
        userPickVehicle = vehicle;
    }

    public Vehicle getVehicle() {
        return userPickVehicle;
    }

    public List<String> getMake(Context context) {

        vehicleData.ExtractVehicleData(context);
        List<String> make = vehicleData.getUniqueVehicleMakeArray();
        return make;
    }

   private void validateIndex(List list, int index) {
        if (index < 0 || index >= list.size()) {
            throw new IllegalArgumentException();
        }
    }

    //--------------------------------Vehicle Date----------------------------------------------//

    public String getUserDay() {
        return userDay;
    }

    public void setUserDay(String userDay) {
        this.userDay = userDay;
    }

    public String getUserMonth() {
        return userMonth;
    }

    public void setUserMonth(String userMonth) {
        this.userMonth = userMonth;
    }

    public String getUserYear() {
        return userYear;
    }

    public void setUserYear(String userYear) {
        this.userYear = userYear;
    }

    //-------------------------------Monthly Utilities Dates------------------------------------//

    public List<MonthlyUtilitiesData> getBillList() {
        return BillList;
    }


    public String getStartDay() { return startDay;  }

    public void setStartDay(String startDay) { this.startDay = startDay; }

    public String getStartMonth() { return startMonth;  }

    public void setStartMonth(String startMonth) { this.startMonth = startMonth; }

    public String getStartYear() { return startYear; }

    public void setStartYear(String startYear) { this.startYear = startYear; }

    public String getEndDay() { return endDay; }

    public void setEndDay(String endDay) { this.endDay = endDay; }

    public String getEndMonth() { return endMonth; }

    public void setEndMonth(String endMonth) { this.endMonth = endMonth; }

    public String getEndYear() { return endYear; }

    public void setEndYear(String endYear) { this.endYear = endYear; }


    public boolean isStartDateChanged() { return isStartDateChanged; }

    public void setStartDateChanged(boolean startDateChanged) { isStartDateChanged = startDateChanged; }

    public boolean isEndDateChanged() { return isEndDateChanged; }

    public void setEndDateChanged(boolean endDateChanged) { isEndDateChanged = endDateChanged; }


    public void userAddMonthlyUtilities() {
        addMonthlyUtilities = 1;
    }

    public void userFinishAdd_MonthlyUtilities() {
        addMonthlyUtilities = 0;
    }

    public int checkAdd_MonthlyUtilities() {
        return addMonthlyUtilities;
    }

    public void userEditMonthlyUtilities() { editMonthlyUtilities = 1;  }

    public int checkEditMonthlyUtilities() {
        return editMonthlyUtilities;
    }

    public void userFinishEditMonthlyUtilities() { editMonthlyUtilities = 0;  }


    public void setEditPosition_bill(long Position) {
        editPosition_bill = Position;
    }

    public long getEditPosition_bill() {
        return editPosition_bill;
    }

    public void setBillList(List<MonthlyUtilitiesData> newBill) {
        BillList = newBill;
    }

    //-----------------------------------Route's function-------------------------------------------

    public List<Route> getRouteList() {
        return RouteList;
    }

    public void setRouteList(List<Route> newRoute) {
        RouteList = newRoute;
    }

    public int checkEdit() {
        return editRoute;
    }

    public void setEditPosition_Route(long Position) {
        editPosition_Route = Position;
    }

    public int getAddPosition() {
        int position = RouteList.size() - 1;
        return position;
    }

    public long getEditPosition_Route() {
        return editPosition_Route;
    }

    public void userEditRoute() {
        editRoute = 1;
    }

    public void userFinishEdit() {
        editRoute = 0;
    }

    public void userAddRoute() {
        addRoute = 1;
    }

    public void userFinishAdd() {
        addRoute = 0;
    }

    public int checkAdd() {
        return addRoute;
    }

    public void UserEnterNewCarName(String newCarName,String oldName) {
        for (int i=0; i<journeyList.size();i++){
            if(journeyList.get(i).getVehicleName() ==  oldName){
                journeyList.get(i).setVehicleName(newCarName);
            }
        }

    }

    public void UserEnterNewRouteName(String newRouteName, String oldName) {
        for (int i=0; i<journeyList.size();i++){
            if(journeyList.get(i).getRouteName() ==  oldName){
                journeyList.get(i).setRouteName(newRouteName);
            }
        }
    }

   //------------------------------Transportation Mode functions------------------------------------

    public int checkTransportationMode() {
        return TransportationMode;
    }

    public void ModeCar() {
        TransportationMode = 0;
    }

    public void ModeWalkBike() {
        TransportationMode = 1;
    }

    public void ModeBus() {
        TransportationMode = 2;
    }

    public void ModeSkytrain() {
        TransportationMode = 3;
    }

    public void setCarInfoDb(SQLiteDatabase db) {
        CarInfoDB = db;
    }

    public SQLiteDatabase getCarInfoDb() {
        return CarInfoDB;
    }

    //----------------------------CO2 unit conversion functions-------------------------------------

    public int checkCO2Unit(){ return CO2Unit; }

    public void originalUnit(){ CO2Unit = 0; }

    public void humanRelatableUnit() { CO2Unit = 1; }


    //-----------------------------------Journey functions-------------------------------------------
    public List<Journey> getUsersJourneys() {
        return journeyList;
    }

    public void setJourneyList(List<Journey> JourneyList) {
        journeyList = JourneyList;
    }

//    public void addUserJourney(Journeys journey) {
//        journeyList.add(journey);
//    }

    public Journey getJourney(int position) {
        return journeyList.get(position);
    }

    public void setEditJourneyPosition(int position) {
        editJourneyPosition = position;
    }

    public boolean isEditingJourney() {
        return editJourney;
    }

    public void userEditJourney() {
        editJourney = true;
    }

    public void userFinishEditJourney() {
        editJourney = false;
    }

    public void changeJourney(Journey newJourney) {
        journeyList.remove(editJourneyPosition);
        journeyList.add(editJourneyPosition, newJourney);
    }
    public void setShuffledTipsForCar(List<String> tipList){
        this.ShuffledTipsForCar = tipList;
    }
    public List<String> getShuffledTipsForCar(){
        return ShuffledTipsForCar;
    }

    public void setShuffledTipsForGas(List<String> shuffledTipsForGas) {
        this.shuffledTipsForGas = shuffledTipsForGas;
    }

    public List<String> getShuffledTipsForEnegy() {
        return shuffledTipsForEnegy;
    }

    public List<String> getShuffledTipsForGas() {
        return shuffledTipsForGas;
    }

    public void setShuffledTipsForEnegy(List<String> shuffledTipsForEnegy) {
        this.shuffledTipsForEnegy = shuffledTipsForEnegy;
    }

    public List<String> getUnrelatedTips() {
        return unrelatedTips;
    }

    public void setUnrelatedTips(List<String> unrelatedTips) {
        this.unrelatedTips = unrelatedTips;
    }

    public double getHighestCO2FromCar() {
        return highestCO2FromCar;
    }

    public void setHighestCO2FromCar(double highestCO2FromCar) {
        this.highestCO2FromCar = highestCO2FromCar;
    }

    public double getHighestCO2FromEnegy() {
        return highestCO2FromEnegy;
    }

    public void setHighestCO2FromEnegy(double highestCO2FromEnegy) {
        this.highestCO2FromEnegy = highestCO2FromEnegy;
    }

    public double getHighestCO2FromGas() {
        return highestCO2FromGas;
    }

    public void setHighestCO2FromGas(double highestCO2FromGas) {
        this.highestCO2FromGas = highestCO2FromGas;
    }

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public boolean isAddJourneyToday() {
        return addJourneyToday;
    }

    public void addedJourneyToday() {
        addJourneyToday = true;
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public String getLatestBill() {
        return latestBill;
    }

    public void setLatestBill(String latestBill) {
        this.latestBill = latestBill;
    }
}
