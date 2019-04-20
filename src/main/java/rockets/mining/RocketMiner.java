package rockets.mining;

import com.google.common.collect.Iterables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rockets.dataaccess.DAO;
import rockets.model.Launch;
import rockets.model.LaunchServiceProvider;
import rockets.model.Rocket;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.*;
import static java.util.Map.Entry.*;


public class RocketMiner {
    private static Logger logger = LoggerFactory.getLogger(RocketMiner.class);

    private DAO dao;

    public RocketMiner(DAO dao) {
        this.dao = dao;
    }


    public List<Rocket> mostLaunchedRockets(int k) {
        Collection<Launch> launchesa = dao.loadAll(Launch.class);
        ArrayList<Rocket> rockets = new ArrayList<>();
        for(int i = 0;i<launchesa.size();i++)
        {
            rockets.add(Iterables.get(launchesa,i).getLaunchVehicle());
        }
        Map<Rocket,Integer> rocketnum = new HashMap<>();
        for(int i=0;i<rockets.size();i++)
        {
            if(rocketnum.containsKey(rockets.get(i)))
            {
                int num = rocketnum.get(rockets.get(i));
                rocketnum.put(rockets.get(i),num+1);
            }
            else
            {
                rocketnum.put(rockets.get(i),1);
            }
        }
        Map<Rocket, Integer> sorted = rocketnum
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        ArrayList<Rocket> returnval = new ArrayList<>();
        for(Rocket i:sorted.keySet())
        {
            returnval.add(i);
        }
        return returnval.subList(0,k);
    }

    /**
     * TODO: to be implemented & tested!
     * <p>
     * Returns the top-k most reliable launch service providers as measured
     * by percentage of successful launches.
     *
     * @param k the number of launch service providers to be returned.
     * @return the list of k most reliable ones.
     */
    public List<LaunchServiceProvider> mostReliableLaunchServiceProviders(int k) {
        return null;
    }

    /**
     * <p>
     * Returns the top-k most recent launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most recent launches.
     */
    public List<Launch> mostRecentLaunches(int k) {
        //logger.info("find most recent " + k + " launches");
        Collection<Launch> launches = dao.loadAll(Launch.class);
        Comparator<Launch> launchDateComparator = (a, b) -> -a.getLaunchDate().compareTo(b.getLaunchDate());
        return launches.stream().sorted(launchDateComparator).limit(k).collect(Collectors.toList());
    }

    /**
     * TODO: to be implemented & tested!
     * <p>
     * Returns the dominant country who has the most launched rockets in an orbit.
     *
     * @param orbit the orbit
     * @return the country who sends the most payload to the orbit
     */
    public String dominantCountry(String orbit) {
        Collection<Launch> launches = dao.loadAll(Launch.class);
        ArrayList<LaunchServiceProvider> lsp = new ArrayList<>();
        Map<String,Integer> countrymap = new HashMap<>();
        //logger.info(orbit);
        for(int i=0;i<launches.size();i++)
        {
            //logger.info(Boolean.toString(Iterables.get(launches,i).getOrbit().equals(orbit)));
            if(Iterables.get(launches,i).getOrbit().equals(orbit))
            {
                //logger.info(Iterables.get(launches,i).getLaunchVehicle().getManufacturer().getCountry());
                if (countrymap.containsKey(Iterables.get(launches,i).getLaunchVehicle().getManufacturer().getCountry()))
                {
                    int val = countrymap.get(Iterables.get(launches,i).getLaunchVehicle().getManufacturer().getCountry());
                    countrymap.put(Iterables.get(launches,i).getLaunchVehicle().getManufacturer().getCountry(),val+1);
                }
                else
                {
                    countrymap.put(Iterables.get(launches,i).getLaunchVehicle().getManufacturer().getCountry(),1);
                }
            }
        }
        String returnval = countrymap.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
        return returnval;
    }

    /**
     * TODO: to be implemented & tested!
     * <p>
     * Returns the top-k most expensive launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most expensive launches.
     */
    public List<Launch> mostExpensiveLaunches(int k) {
        return null;
    }

    /**
     * TODO: to be implemented & tested!
     * <p>
     * Returns a list of launch service provider that has the top-k highest
     * sales revenue in a year.
     *
     * @param k the number of launch service provider.
     * @param year the year in request
     * @return the list of k launch service providers who has the highest sales revenue.
     */
    public List<LaunchServiceProvider> highestRevenueLaunchServiceProviders(int k, int year) {
        return null;
    }
}
