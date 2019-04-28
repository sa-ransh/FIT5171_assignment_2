package rockets.mining;

import com.google.common.collect.Iterables;
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


public class RocketMiner {

    private DAO dao;

    public RocketMiner(DAO dao) {
        this.dao = dao;
    }

    /**
     * Returns the top-k most active rockets, as measured by number of completed launches.
     *
     * @param k the number of rockets to be returned.
     * @return the list of k most active rockets.
     */

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
     * <p>
     * Returns the top-k most reliable launch service providers as measured
     * by percentage of successful launches.
     *
     * @param k the number of launch service providers to be returned.
     * @return the list of k most reliable ones.
     */
    public List<LaunchServiceProvider> mostReliableLaunchServiceProviders(int k) {
        Collection<Launch> launch = dao.loadAll(Launch.class);
        Map<LaunchServiceProvider,Integer> successLaunches = new HashMap<>();
        Map<LaunchServiceProvider,Integer> totalLaunches = new HashMap<>();
        for(int i = 0;i<launch.size();i++)
        {
            Launch temp = Iterables.get(launch,i);
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
        ArrayList<LaunchServiceProvider> returnval = new ArrayList<>();

        for(LaunchServiceProvider i:sorted.keySet())
        {
            returnval.add(i);
        }
        return returnval.subList(0,k);
    }

    /**
     * <p>
     * Returns the top-k most recent launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most recent launches.
     */
    public List<Launch> mostRecentLaunches(int k) {
        Collection<Launch> launches = dao.loadAll(Launch.class);
        Comparator<Launch> launchDateComparator = Comparator.comparing(Launch::getLaunchDate).reversed();
        return launches.stream().sorted(launchDateComparator).limit(k).collect(Collectors.toList());
    }

    /**
     * <p>
     * Returns the dominant country who has the most launched rockets in an orbit.
     *
             * @param orbit the orbit
     * @return the country who sends the most payload to the orbit
     */

    public String dominantCountry(String orbit) {
        Collection<Launch> launches = dao.loadAll(Launch.class);
        Map<String,Integer> countrymap = new HashMap<>();
        for(int i=0;i<launches.size();i++)
        {
            if(Iterables.get(launches,i).getOrbit().equals(orbit))
            {
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
        Optional<Map.Entry<String,Integer>> tempvalue = countrymap.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1);
        String returnval = "";
        if(tempvalue.isPresent())
        {
            returnval = tempvalue.get().getKey();
        }
        return returnval;
    }

    /**
     * <p>
     * Returns the top-k most expensive launches.
     *
     * @param k the number of launches to be returned.
     * @return the list of k most expensive launches.
     */

    public List<Launch> mostExpensiveLaunches(int k) {
        Collection<Launch> launches = dao.loadAll(Launch.class);
        Comparator<Launch> priceComparator = Comparator.comparing(Launch::getPrice).reversed();
        return launches.stream().sorted(priceComparator).limit(k).collect(Collectors.toList());
    }

    /**
     * <p>
     * Returns a list of launch service provider that has the top-k highest
     * sales revenue in a year.
     *
     * @param k the number of launch service provider.
     * @param year the year in request
     * @return the list of k launch service providers who has the highest sales revenue.
     */
    public List<LaunchServiceProvider> highestRevenueLaunchServiceProviders(int k, int year) {
        Collection<Launch> launch = dao.loadAll(Launch.class);
        Map<LaunchServiceProvider, BigDecimal> revenueLsp= new HashMap<>();
        for(int i = 0;i<launch.size();i++) {
            Launch temp = Iterables.get(launch, i);
            int tempDate = temp.getLaunchDate().getYear();
            if(tempDate == year)
            {
                if(revenueLsp.containsKey(temp.getLaunchServiceProvider()))
                {
                    BigDecimal temp1 = (revenueLsp.get(temp.getLaunchServiceProvider())).add(temp.getPrice());
                    revenueLsp.put(temp.getLaunchServiceProvider(),temp1);
                }
                else
                {
                    revenueLsp.put(temp.getLaunchServiceProvider(),temp.getPrice());
                }
            }

        }

        Map<LaunchServiceProvider, BigDecimal> sorted = revenueLsp
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        ArrayList<LaunchServiceProvider> returnval = new ArrayList<>();

        for(LaunchServiceProvider i:sorted.keySet())
        {
            returnval.add(i);
        }
        return returnval.subList(0,k);
    }

    /**
     * <p>
     * Returns the top-k most unreliable launch service providers as measured
     * by percentage of unsuccessful launches.
     *
     * @param k the number of launch service providers to be returned.
     * @return the list of k most unreliable ones.
     */
    public List<LaunchServiceProvider> mostUnreliableLaunchServiceProviders(int k) {
        Collection<Launch> launch = dao.loadAll(Launch.class);
        Map<LaunchServiceProvider,Integer> failedLaunches = new HashMap<>();
        Map<LaunchServiceProvider,Integer> totalLaunches = new HashMap<>();
        for(int i = 0;i<launch.size();i++)
        {
            Launch temp = Iterables.get(launch,i);
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
        ArrayList<LaunchServiceProvider> returnval = new ArrayList<>();

        for(LaunchServiceProvider i:sorted.keySet())
        {
            returnval.add(i);
        }
        return returnval.subList(0,k);
    }
}
