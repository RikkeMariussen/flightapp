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

            System.out.println("The total flight time for airline is: "+ totalFlightTimeForAirline("Lufthansa")+" minutes");
            System.out.println("The average flight time for airline is: "+ averageFlightTimeForAirline("Lufthansa")+" minutes");


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
                .getAsDouble();

        return averageDuration;
    }
}