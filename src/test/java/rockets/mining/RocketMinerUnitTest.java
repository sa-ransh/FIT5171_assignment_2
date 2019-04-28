package rockets.mining;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.CsvSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.dataaccess.neo4j.Neo4jDAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;
import scala.Array;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toMap;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;

public class RocketMinerUnitTest {
    Logger logger = LoggerFactory.getLogger(RocketMinerUnitTest.class);

    private DAO dao;
    private RocketMiner miner;
    private List<Rocket> rockets;
    private List<LaunchServiceProvider> lsps;
    private List<Launch> launches;

    @BeforeEach
    public void setUp() {
        dao = mock(Neo4jDAO.class);
        miner = new RocketMiner(dao);
        rockets = Lists.newArrayList();

        lsps = Arrays.asList(
                new LaunchServiceProvider("ULA", 1990, "USA"),
                new LaunchServiceProvider("ULA", 1990, "USA"),
                new LaunchServiceProvider("ULA", 1990, "USA"),
                new LaunchServiceProvider("ULA", 1990, "USA"),
                new LaunchServiceProvider("SpaceX", 2002, "USA"),
                new LaunchServiceProvider("SpaceX", 2002, "USA"),
                new LaunchServiceProvider("SpaceX", 2002, "USA"),
                new LaunchServiceProvider("SpaceX", 2002, "USA"),
                new LaunchServiceProvider("ESA", 1975, "Europe "),
                new LaunchServiceProvider("ESA", 1975, "Europe "),
                new LaunchServiceProvider("ESA", 1975, "Europe "),
                new LaunchServiceProvider("ESA", 1975, "Europe "),
                new LaunchServiceProvider("ESA", 1975, "Europe ")
);

        // index of lsp of each rocket
        int[] lspIndex = new int[]{0, 0, 0, 1, 1};
        // 5 rockets
        for (int i = 0; i < 5; i++) {
            rockets.add(new Rocket("rocket_" + i, "USA", lsps.get(lspIndex[i])));
        }
        for(int i = 5;i<9;i++)
        {
            rockets.add(new Rocket("rocket_" + i, "Europe", lsps.get(2)));
        }
        // month of each launch
        int[] months = new int[]{1, 6, 4, 3, 4, 11, 6, 5, 12, 5, 6, 10, 4};

        // year of each launch service provider
        int[] years = new int[]{1, 6, 4, 3, 4, 11, 6, 5, 12, 5};

        // index of rocket of each launch
        int[] rocketIndex = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3, 5, 5, 5};

        BigDecimal[] price = new BigDecimal[]{BigDecimal.valueOf(100), BigDecimal.valueOf(300), BigDecimal.valueOf(400), BigDecimal.valueOf(500), BigDecimal.valueOf(100), BigDecimal.valueOf(600), BigDecimal.valueOf(700), BigDecimal.valueOf(900), BigDecimal.valueOf(1000), BigDecimal.valueOf(1100), BigDecimal.valueOf(1200), BigDecimal.valueOf(100), BigDecimal.valueOf(1100)};
        Launch.LaunchOutcome[] launchOutcomes = new Launch.LaunchOutcome[]{Launch.LaunchOutcome.SUCCESSFUL,Launch.LaunchOutcome.SUCCESSFUL, Launch.LaunchOutcome.SUCCESSFUL,Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.SUCCESSFUL,Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.SUCCESSFUL,Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.SUCCESSFUL,Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.FAILED,Launch.LaunchOutcome.FAILED, Launch.LaunchOutcome.SUCCESSFUL};
        // 10 launches
        launches = IntStream.range(0, 13).mapToObj(i -> {
            Launch l = new Launch();
            l.setLaunchDate(LocalDate.of(2017, months[i], 1));
            l.setLaunchVehicle(rockets.get(rocketIndex[i]));
            l.setLaunchServiceProvider(lsps.get(i));
            l.setLaunchOutcome(launchOutcomes[i]);
            l.setLaunchSite("VAFB");
            l.setOrbit("LEO");
            l.setPrice(price[i]);
            spy(l);
            return l;
        }).collect(Collectors.toList());

        lsps = IntStream.range(0, 10).mapToObj(i -> {
            logger.info("create " + i + " launch service provider in year: " + years[i]);
            LaunchServiceProvider lsp = new LaunchServiceProvider("ULA", 1990, "USA");
            lsp.setRevenue("20");
            spy(lsp);
            return lsp;
        }).collect(Collectors.toList());
    }


    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void shouldReturnTopMostRecentLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        sortedLaunches.sort((a, b) -> -a.getLaunchDate().compareTo(b.getLaunchDate()));
        List<Launch> loadedLaunches = miner.mostRecentLaunches(k);
        assertEquals(k, loadedLaunches.size());
        assertEquals(sortedLaunches.subList(0, k), loadedLaunches);
        logger.info(loadedLaunches.get(0).getLaunchVehicle().getName());
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3, 4, 5})
    public void shouldReturnMostLaunchedRockets(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        List<Rocket> loadedRockets = miner.mostLaunchedRockets(k);
        Map<Rocket,Integer> rocketmap = new HashMap<>();
        for(int i=0;i<sortedLaunches.size();i++)
        {
            if(rocketmap.containsKey(sortedLaunches.get(i).getLaunchVehicle()))
            {
                int val = rocketmap.get(sortedLaunches.get(i).getLaunchVehicle());
                rocketmap.put(sortedLaunches.get(i).getLaunchVehicle(),val+1);
            }
            else
            {
                rocketmap.put(sortedLaunches.get(i).getLaunchVehicle(),1);
            }
        }
        Map<Rocket, Integer> sorted = rocketmap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        ArrayList<Rocket> sortedRockets = new ArrayList<>();
        for(Rocket i:sorted.keySet())
        {
            sortedRockets.add(i);
        }
        assertEquals(k, loadedRockets.size());
        assertEquals(sortedRockets.subList(0, k), loadedRockets);
    }

    @ParameterizedTest
    @ValueSource(strings = {"LEO"})
    public void shouldReturnDominantCountry(String k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        Map<String,Integer> countrymap = new HashMap<>();
        for(int i=0;i<sortedLaunches.size();i++)
        {
            if(sortedLaunches.get(i).getOrbit().equals(k))
            {
                if(countrymap.containsKey(sortedLaunches.get(i).getLaunchVehicle().getManufacturer().getCountry()))
                {
                    int val = countrymap.get(sortedLaunches.get(i).getLaunchVehicle().getManufacturer().getCountry());
                    countrymap.put(sortedLaunches.get(i).getLaunchVehicle().getManufacturer().getCountry(),val+1);
                }
                else
                {
                    countrymap.put(sortedLaunches.get(i).getLaunchVehicle().getManufacturer().getCountry(),1);
                }
            }
        }
        String domcountry = countrymap.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
        String country = miner.dominantCountry(k);
        assertEquals(domcountry,country);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void shouldReturnMostExpensiveLaunches(int k) {
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        sortedLaunches.sort((a, b) -> -a.getPrice().compareTo(b.getPrice()));
        List<Launch> loadedLaunches = miner.mostExpensiveLaunches(k);
        for(int i =0;i<loadedLaunches.size();i++) {
            logger.info(String.valueOf(loadedLaunches.get(i).getPrice()));
        }
        assertEquals(k, loadedLaunches.size());
        assertEquals(sortedLaunches.subList(0, k), loadedLaunches);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void shouldReturnMostReliableLaunchServiceProviders(int k){
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        List<LaunchServiceProvider> launchServiceProviders = miner.mostReliableLaunchServiceProviders(k);
        Map<LaunchServiceProvider,Integer> successLaunches= new HashMap<>();
        Map<LaunchServiceProvider,Integer> totalLaunches= new HashMap<>();
        for(int i=0;i<sortedLaunches.size();i++)
        {
            Launch temp = sortedLaunches.get(i);
            if(temp.getLaunchOutcome() == Launch.LaunchOutcome.SUCCESSFUL)
            {
                if(successLaunches.containsKey(temp.getLaunchServiceProvider()))
                {
                    int temp1 = successLaunches.get(temp.getLaunchServiceProvider());
                    successLaunches.put(temp.getLaunchServiceProvider(),temp1+1);
                }
                else
                {
                    successLaunches.put(temp.getLaunchServiceProvider(),1);
                }

            }
            if(totalLaunches.containsKey(temp.getLaunchServiceProvider()))
            {
                int temp1 = totalLaunches.get(temp.getLaunchServiceProvider());
                totalLaunches.put(temp.getLaunchServiceProvider(),temp1+1);
            }
            else
            {
                totalLaunches.put(temp.getLaunchServiceProvider(),1);
            }

        }
        Map<LaunchServiceProvider,Float> percentLaunches = new HashMap<>();
        for(LaunchServiceProvider ls:successLaunches.keySet())
        {
            Float percent = ((float)successLaunches.get(ls)/(float)totalLaunches.get(ls)) * 100;
            percentLaunches.put(ls,percent);
        }
        Map<LaunchServiceProvider, Float> sorted = percentLaunches
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        ArrayList<LaunchServiceProvider> reliablelsps = new ArrayList<>();
        for(LaunchServiceProvider i:sorted.keySet())
        {
            reliablelsps.add(i);
        }
        /*for(int i=0;i<launchServiceProviders.size();i++)
        {
            logger.info(launchServiceProviders.get(i).getName());
        }*/
        assertEquals(k,launchServiceProviders.size());
        assertEquals(reliablelsps.subList(0,k),launchServiceProviders);
    }

    @ParameterizedTest
    @CsvSource({"1,2002", "2,2002","3,2002"})
    public void shouldReturnHighestRevenueLaunchServiceProviders(int k, int year) {
        when(dao.loadAll(LaunchServiceProvider.class)).thenReturn(lsps);
        List<LaunchServiceProvider> sortedLsps = new ArrayList<>(lsps);
        sortedLsps.sort((a, b) -> -a.getRevenue().compareTo(b.getRevenue()));
        List<LaunchServiceProvider> loadedLaunchServiceProviders = miner.highestRevenueLaunchServiceProviders(k, year);
        assertEquals(k, loadedLaunchServiceProviders.size());
        assertEquals(sortedLsps.subList(0, k), loadedLaunchServiceProviders);
    }

    @ParameterizedTest
    @ValueSource(ints = {1, 2, 3})
    public void shouldReturnMostUnreliableLaunchServiceProviders(int k){
        when(dao.loadAll(Launch.class)).thenReturn(launches);
        List<Launch> sortedLaunches = new ArrayList<>(launches);
        List<LaunchServiceProvider> launchServiceProviders = miner.mostUnreliableLaunchServiceProviders(k);
        Map<LaunchServiceProvider,Integer> failedLaunches= new HashMap<>();
        Map<LaunchServiceProvider,Integer> totalLaunches= new HashMap<>();
        for(int i=0;i<sortedLaunches.size();i++)
        {
            Launch temp = sortedLaunches.get(i);
            if(temp.getLaunchOutcome() == Launch.LaunchOutcome.FAILED)
            {
                if(failedLaunches.containsKey(temp.getLaunchServiceProvider()))
                {
                    int temp1 = failedLaunches.get(temp.getLaunchServiceProvider());
                    failedLaunches.put(temp.getLaunchServiceProvider(),temp1+1);
                }
                else
                {
                    failedLaunches.put(temp.getLaunchServiceProvider(),1);
                }

            }
            if(totalLaunches.containsKey(temp.getLaunchServiceProvider()))
            {
                int temp1 = totalLaunches.get(temp.getLaunchServiceProvider());
                totalLaunches.put(temp.getLaunchServiceProvider(),temp1+1);
            }
            else
            {
                totalLaunches.put(temp.getLaunchServiceProvider(),1);
            }

        }
        Map<LaunchServiceProvider,Float> percentLaunches = new HashMap<>();
        for(LaunchServiceProvider ls:failedLaunches.keySet())
        {
            Float percent = ((float)failedLaunches.get(ls)/(float)totalLaunches.get(ls)) * 100;
            percentLaunches.put(ls,percent);
        }
        Map<LaunchServiceProvider, Float> sorted = percentLaunches
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        ArrayList<LaunchServiceProvider> unreliablelsps = new ArrayList<>();
        for(LaunchServiceProvider i:sorted.keySet())
        {
            unreliablelsps.add(i);
        }
        /*for(int i=0;i<launchServiceProviders.size();i++)
        {
            logger.info(launchServiceProviders.get(i).getName());
        }*/
        assertEquals(k,launchServiceProviders.size());
        assertEquals(unreliablelsps.subList(0,k),launchServiceProviders);
    }

/*   @Test
    public void shouldThrowExceptionWhenGivenInvalidKRockets() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> miner.mostLaunchedRockets(4));
        assertEquals("value exceeds total number of rockets", exception.getMessage());
    }

    @Test
    public void shouldThrowExceptionWhenGivenInvalidKServiceProviders() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> miner.mostReliableLaunchServiceProviders(3));
        assertEquals("value exceeds total number of launch service providers", exception.getMessage());
    }
*/

}