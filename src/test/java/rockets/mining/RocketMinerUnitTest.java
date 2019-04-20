package rockets.mining;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.dataaccess.neo4j.Neo4jDAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;
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
                new LaunchServiceProvider("SpaceX", 2002, "USA"),
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

        // index of rocket of each launch
        int[] rocketIndex = new int[]{0, 0, 0, 0, 1, 1, 1, 2, 2, 3, 5, 5, 5};

        // 10 launches
        launches = IntStream.range(0, 13).mapToObj(i -> {
            //logger.info("create " + i + " launch in month: " + months[i]);
            Launch l = new Launch();
            l.setLaunchDate(LocalDate.of(2017, months[i], 1));
            l.setLaunchVehicle(rockets.get(rocketIndex[i]));
            l.setLaunchSite("VAFB");
            l.setOrbit("LEO");
            spy(l);
            return l;
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
}