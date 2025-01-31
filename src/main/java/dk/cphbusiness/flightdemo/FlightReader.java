package dk.cphbusiness.flightdemo;

import com.fasterxml.jackson.databind.ObjectMapper;
import dk.cphbusiness.flightdemo.dtos.FlightDTO;
import dk.cphbusiness.flightdemo.dtos.FlightInfoDTO;
import dk.cphbusiness.utils.Utils;

import java.io.IOException;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Locale.filter;

/**
 * Purpose:
 *
 * @author: Thomas Hartmann
 */
public class FlightReader
{

    public static void main(String[] args)
    {
        try
        {
            List<FlightDTO> flightList = getFlightsFromFile("flights.json");
            List<FlightInfoDTO> flightInfoDTOList = getFlightInfoDetails(flightList);
            //flightInfoDTOList.forEach(System.out::println);

            System.out.println("\t"+"\t"+"\t"+"\t"+"------------------");
            System.out.println("The total flight time for airline is: "+ totalFlightTimeForAirline("Lufthansa")+" minutes");

            System.out.println("\t"+"\t"+"\t"+"\t"+"------------------");
            System.out.println("The average flight time for airline is: "+ averageFlightTimeForAirline("Lufthansa")+" minutes");

            System.out.println("\t"+"\t"+"\t"+"\t"+"------------------");
            getTotalFlightTimePerAirline(flightInfoDTOList);

            System.out.println("\t"+"\t"+"\t"+"\t"+"------------------");
            getFlightsBetweenSpecificAirports("Fukuoka", "Haneda");


        } catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static List<FlightDTO> getFlightsFromFile(String filename) throws IOException
    {

        ObjectMapper objectMapper = Utils.getObjectMapper();

        // Deserialize JSON from a file into FlightDTO[]
        FlightDTO[] flightsArray = objectMapper.readValue(Paths.get("flights.json").toFile(), FlightDTO[].class);

        // Convert to a list
        List<FlightDTO> flightsList = List.of(flightsArray);
        return flightsList;
    }

    public static List<FlightInfoDTO> getFlightInfoDetails(List<FlightDTO> flightList)
    {
        List<FlightInfoDTO> flightInfoList = flightList.stream()
                .map(flight ->
                {
                    LocalDateTime departure = flight.getDeparture().getScheduled();
                    LocalDateTime arrival = flight.getArrival().getScheduled();
                    Duration duration = Duration.between(departure, arrival);
                    FlightInfoDTO flightInfo =
                            FlightInfoDTO.builder()
                                    .name(flight.getFlight().getNumber())
                                    .iata(flight.getFlight().getIata())
                                    .airline(flight.getAirline().getName())
                                    .duration(duration)
                                    .departure(departure)
                                    .arrival(arrival)
                                    .origin(flight.getDeparture().getAirport())
                                    .destination(flight.getArrival().getAirport())
                                    .build();

                    return flightInfo;
                })
                .toList();
        return flightInfoList;
    }

    public static long totalFlightTimeForAirline(String airline) throws IOException {
        List<FlightDTO> flightList = FlightReader.getFlightsFromFile("flights json");
        List<FlightInfoDTO> flightInfoDTOList = FlightReader.getFlightInfoDetails(flightList);

        long result = flightInfoDTOList.stream()
                .filter(flight -> flight.getAirline() != null)
                .filter(flight -> flight.getAirline().equals(airline))
                .mapToLong(flight -> flight.getDuration().toMinutes())
                .sum();
        System.out.println(result/60 +" hours.");
        return result;
    }

    public static double averageFlightTimeForAirline(String airline) throws IOException
    {
        List<FlightDTO> flightList = FlightReader.getFlightsFromFile("flights json");
        List<FlightInfoDTO> flightInfoDTOList = FlightReader.getFlightInfoDetails(flightList);

        double averageDuration = flightInfoDTOList.stream()
                .filter(flight -> flight.getAirline() != null)
                .filter(flight -> flight.getAirline().equals(airline))
                .mapToDouble(flight -> flight.getDuration().toMinutes())
                .average()
                .getAsDouble(); //Runtime error hvis det er null ellers skal man bruge optionel og evt. isPresent() eller og orElse()

        return averageDuration;
    }

    public static void getTotalFlightTimePerAirline(List<FlightInfoDTO> flightInfoDTOList)
    {
        //Copied from teachers program
        flightInfoDTOList.stream()
                .filter(flight -> flight.getAirline() != null)
                .collect(Collectors.groupingBy(FlightInfoDTO::getAirline, Collectors.summingLong(flight -> flight.getDuration().toMinutes())))
                .forEach((airline, total)-> System.out.println(airline + " has a total flight time of " + total + " minutes" +System.lineSeparator()));
    }

    public static List<FlightInfoDTO> getFlightsBetweenSpecificAirports(String airport1, String airport2) throws IOException
    {
        List<FlightDTO> flightList = FlightReader.getFlightsFromFile("flights json");
        List<FlightInfoDTO> flightInfoDTOList = FlightReader.getFlightInfoDetails(flightList);

        List<FlightInfoDTO> result = flightInfoDTOList.stream()
                .filter(flight -> flight.getOrigin() != null && flight.getDestination() != null)
                .filter(flight -> (flight.getOrigin().contains(airport1) && flight.getDestination().contains(airport2)) || (flight.getOrigin().contains(airport2) && flight.getDestination().contains(airport1)))
                .collect(Collectors.toList());

        result.forEach(flight -> {
            System.out.println("Origin: " + flight.getOrigin());
            System.out.println("Destination: " + flight.getDestination());
            System.out.println("Flight Number: " + flight.getIata());
            System.out.println("Departure Time: " + flight.getDeparture());
            System.out.println("-------------------------------");
        });

        return result;
    }

}