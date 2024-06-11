//Armin Torkamandi ---- Arshia Raufpanah
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GeneticAlgorithm {
    // تعریف ثابت‌های مربوط به الگوریتم ژنتیک
    private static final int POPULATION_SIZE = 100;
    private static final double MUTATION_RATE = 0.1;
    private static final int MAX_GENERATIONS = 100;

    // تعریف توپولوژی شبکه و گره‌های منبع و مقصد
    private static final Map<String, List<String>> networkTopology = new HashMap<>();
    private static final String sourceNode = "A";
    private static final String destinationNode = "E";

    public static void main(String[] args) {
        // ایجاد توپولوژی شبکه
        initializeNetworkTopology();

        // اجرای الگوریتم ژنتیک و چاپ مسیر بهینه
        List<String> bestRoute = geneticAlgorithm();
        System.out.println("Best route from " + sourceNode + " to " + destinationNode + ": " + String.join(" -> ", bestRoute));
    }

    // تابعی برای ایجاد توپولوژی شبکه
    private static void initializeNetworkTopology() {
        networkTopology.put("A", List.of("B", "C"));
        networkTopology.put("B", List.of("A", "C", "D"));
        networkTopology.put("C", List.of("A", "B", "D", "E"));
        networkTopology.put("D", List.of("B", "C", "E"));
        networkTopology.put("E", List.of("C", "D"));
    }

    // تابعی برای تولید جمعیت اولیه از مسیرهای تصادفی
    private static List<String> generateInitialPopulation() {
        List<String> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            String current = sourceNode;
            StringBuilder route = new StringBuilder(current);
            while (!current.equals(destinationNode)) {
                List<String> neighbors = networkTopology.get(current);
                String next = neighbors.get(new Random().nextInt(neighbors.size()));
                route.append(" -> ").append(next);
                current = next;
            }
            population.add(route.toString());
        }
        return population;
    }

    // تابعی برای انتخاب والدین با استفاده از روش چرخ رولت
    private static List<String> selectParents(List<String> population, List<Double> fitnessValues) {
        double totalFitness = fitnessValues.stream().mapToDouble(Double::doubleValue).sum();
        List<Double> probabilities = new ArrayList<>();
        for (Double fitness : fitnessValues) {
            probabilities.add(fitness / totalFitness);
        }
        int index1 = getRandomIndex(probabilities);
        int index2 = getRandomIndex(probabilities);
        return List.of(population.get(index1), population.get(index2));
    }

    // تابعی برای انتخاب تصادفی یک اندیس بر اساس احتمالات داده شده
    private static int getRandomIndex(List<Double> probabilities) {
        double random = Math.random();
        double cumulativeProbability = 0;
        for (int i = 0; i < probabilities.size(); i++) {
            cumulativeProbability += probabilities.get(i);
            if (random <= cumulativeProbability) {
                return i;
            }
        }
        return probabilities.size() - 1;
    }

    // تابعی برای تولید فرزندان جدید با استفاده از عملگر ترکیب و جهش
    private static List<String> crossover(List<String> parent1, List<String> parent2) {
        int crossoverPoint = new Random().nextInt(parent1.size() - 1) + 1;
        List<String> child1 = new ArrayList<>(parent1.subList(0, crossoverPoint));
        List<String> child2 = new ArrayList<>(parent2.subList(0, crossoverPoint));
        child1.addAll(parent2.subList(crossoverPoint, parent2.size()));
        child2.addAll(parent1.subList(crossoverPoint, parent1.size()));
        return List.of(String.join(" -> ", child1), String.join(" -> ", child2));
    }

    // تابعی برای جهش تصادفی در مسیرهای فرزندان
    private static List<String> mutate(List<String> route) {
        if (Math.random() < MUTATION_RATE) {
            int mutationPoint = new Random().nextInt(route.size() - 2) + 1;
            String current = route.get(mutationPoint);
            List<String> neighbors = networkTopology.get(current);
            String newNode = neighbors.get(new Random().nextInt(neighbors.size()));
            route.set(mutationPoint, newNode);
        }
        return route;
    }

    // تابع ارزیابی کیفیت هر مسیر بر اساس فاصله
    private static double fitnessFunction(String route) {
        int totalDistance = route.split(" -> ").length - 1;
        return 1.0 / totalDistance;
    }

    // تابع اجرای الگوریتم ژنتیک برای محاسبه مسیر بهینه
    private static List<String> geneticAlgorithm() {

        List<String> population = generateInitialPopulation();

        for (int generation = 0; generation < MAX_GENERATIONS ; generation++) {
            List<Double> fitnessValues = new ArrayList<>();
            for (String route : population) {
                double fitness = fitnessFunction(route);    // 1 / Distance
                fitnessValues.add(fitness);
            }

            List<String> newPopulation = new ArrayList<>();
            for (int i = 0; i < POPULATION_SIZE / 2; i++) {
                List<String> parents = selectParents(population, fitnessValues);
                List<String> children = crossover(List.of(parents.get(0), parents.get(1)));
                children.set(0, String.join(" -> ", mutate(children.get(0).split(" -> "))));
                children.set(1, String.join(" -> ", mutate(children.get(1).split(" -> "))));
                newPopulation.addAll(children);
            }
            population = newPopulation;
        }
        return population;
    }
}