package com.driver.repository;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.driver.model.Airport;
import com.driver.model.City;
import com.driver.model.Flight;
import com.driver.model.Passenger;

@Repository
public class AirportRepository {

    Map<String, Airport> airportMap=new HashMap<>(); 
    Map<Integer, Flight> flightMap=new HashMap<>();
     Map<Integer, Passenger> passengerMap=new HashMap<>();

     Map<Integer,Integer> passengerFlightMap=new HashMap<>();
     Map<Integer,List<Passenger>> flightPassengerMap=new HashMap<>();
     
    public void addAirport(Airport airport) {
        airportMap.put(airport.getAirportName(),airport);
    }
    public String getLargestAirportName() {
        int maxTerminals=0;
        String ansAirport="";
        Map<Integer,String> countMap=new HashMap<>();
        for(String airportName:airportMap.keySet()){
            Airport newAirport=airportMap.get(airportName);

            int noOfTerminals=newAirport.getNoOfTerminals();
            if(noOfTerminals> maxTerminals && !countMap.containsKey(noOfTerminals)){
                maxTerminals=noOfTerminals;
                ansAirport=newAirport.getAirportName();
                countMap.put(noOfTerminals,airportName);
            }
            if(countMap.containsKey(noOfTerminals)){

                for(int i=0;i<airportName.length();i++){
                    if(airportName.charAt(i)<ansAirport.charAt(i)){
                        return airportName;
                    }
                }
            }
           
             
        }
        return ansAirport;
    }
    public double getShortestDurationOfPossibleBetweenTwoCities(City fromCity, City toCity) {
         //Find the duration by finding the shortest flight that connects these 2 cities directly
        //If there is no direct flight between 2 cities return -1.

       double minTime=Integer.MAX_VALUE;
       for(Integer flightId:flightMap.keySet()){     
            Flight f=flightMap.get(flightId);
            if(f.getFromCity().equals(fromCity) && f.getToCity().equals(toCity)){
                minTime=Math.min(minTime,f.getDuration());
                
            }

       }
        return minTime==Integer.MAX_VALUE?-1:minTime;
    }
    public int getNumberOfPeopleOn(Date date, String airportName) {

         //Calculate the total number of people who have flights on that day on a particular airport
        //This includes both the people who have come for a flight and who have landed on an airport after their flight
        // for(Integer pId: passengerFlightMap.keySet()){
        //     Passenger newPassenger=passengerMap.get(pId);
        //     Flight newFlight=flightMap.get(passengerFlightMap.get(pId));
        //     if(newFlight.getFlightDate().equals(date)){
                
        //     }


        // }
        City cityCheck=null;
        int count=0;
        for(String names: airportMap.keySet()){
            Airport airport=airportMap.get(names);
           
            if(airportName.equals(names)){
                 cityCheck=airport.getCity();
            }
        }
        for(Integer flightId:flightMap.keySet()){
            Flight flight=flightMap.get(flightId);
            if(flight.getFlightDate().equals(date) && (flight.getFromCity().equals(cityCheck) || flight.getToCity().equals(cityCheck))){
                count++;
            }
        }
        return count;
    }
    public String addPassenger(Passenger passenger) {
        passengerMap.put(passenger.getPassengerId(),passenger);
        return "SUCCESS";
    }
    public String getAirportNameFromFlightId(Integer flightId) {
        City startCity=flightMap.get(flightId).getFromCity();
        for(String airportId: airportMap.keySet()){
            if(airportMap.get(airportId).getCity().equals(startCity)){
                return airportMap.get(airportId).getAirportName();
            }
        }
        return null;
    }
    public String addFlight(Flight flight) {
        flightMap.put(flight.getFlightId(),flight);
        return "SUCCESS";
    }
    public int calculateRevenueOfAFlight(Integer flightId) {
         //Calculate the total revenue that a flight could have
        //That is of all the passengers that have booked a flight till now and then calculate the revenue
        //Revenue will also decrease if some passenger cancels the flight

        List<Passenger> passengers=flightPassengerMap.get(flightId);
        return passengers.size();
    }
    public int countOfBookingsDoneByPassengerAllCombined(Integer passengerId) {
    
         //Tell the count of flight bookings done by a passenger: This will tell the total count of flight bookings done by a passenger :
        int count=0;
        for(Integer pId: passengerMap.keySet()){
            if(pId==passengerId){
                Integer fId=passengerFlightMap.get(pId);
                count++;
            }
        }
        return count;
    }
    public int calculateFlightFare(Integer flightId) {
        // //Calculation of flight prices is a function of number of people who have booked the flight already.
        //Price for any flight will be : 3000 + noOfPeopleWhoHaveAlreadyBooked*50
        //Suppose if 2 people have booked the flight already : the price of flight for the third person will be 3000 + 2*50 = 3100
        //This will not include the current person who is trying to book, he might also be just checking price
        int costOfFlight=0;
        for(Integer passengerId: passengerFlightMap.keySet()){
            if(passengerFlightMap.get(passengerId)==flightId){
                List<Passenger> passengers=flightPassengerMap.get(flightId);
                costOfFlight=3000+(passengers.size()-1)*50;
            }
        }
        
        return costOfFlight;
    }
    public String bookATicket(Integer flightId,Integer passengerId) {
         //If the numberOfPassengers who have booked the flight is greater than : maxCapacity, in that case :
        //return a String "FAILURE"
        //Also if the passenger has already booked a flight then also return "FAILURE".
        //else if you are able to book a ticket then return "SUCCESS"
        if(passengerFlightMap.containsKey(passengerId)){
            return "FAILURE";
        }
        else if(flightPassengerMap.containsKey(flightId)){
             List<Passenger> listOfPassengers=flightPassengerMap.get(flightId);
             if(listOfPassengers.size()>flightMap.get(flightId).getMaxCapacity()){
                return "FAILURE";
             }
        }
        
           passengerFlightMap.put(passengerId,flightId);
           List<Passenger> listOfPassengers=flightPassengerMap.get(flightId);
           Passenger p=passengerMap.get(passengerId);
           listOfPassengers.add(p);
           flightPassengerMap.put(flightId,listOfPassengers);
           return "SUCCESS";
        
        
    }
    public String cancelATicket(Integer flightId, Integer passengerId) {
        // //If the passenger has not booked a ticket for that flight or the flightId is invalid or in any other failure case
        // then return a "FAILURE" message
        // Otherwise return a "SUCCESS" message
        // and also cancel the ticket that passenger had booked earlier on the given flightId
        if(!passengerFlightMap.containsKey(passengerId)){
            return "FAILURE";
        }
        
        if(flightPassengerMap.containsKey(flightId)){
        List<Passenger> listOfPassengers=flightPassengerMap.get(flightId);
        for(Passenger p: listOfPassengers){
            if(p.getPassengerId()==passengerId){
                listOfPassengers.remove(p);
                flightPassengerMap.put(flightId,listOfPassengers);
                return "SUCCESS";
            }
        }
    }
    passengerFlightMap.remove(passengerId);
    return "FAILURE";

    }

    
}
