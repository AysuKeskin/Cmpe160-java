import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
/**
 * Program finds the quickest delivery route of a Migros delivery car using brute-force and ant colony methods.
 * @author Aysu Keskin, Student ID: 2023400042
 * @since Date: 11.05.2024
 */
//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class main {
    public static void main(String[] args) throws FileNotFoundException {
        int chosenMethod = 2; // Set variable to 1 for the brute force method, 2 for the ant colony method
        int whichToDraw = 1; // Set variable to 1 for drawing the shortest route graphic, 2 for drawing the pheromone intensity graphic
        // Parameters of the ant colony optimization approach:
        int iterationCount = 70;
        int antCount = 50;
        double degradation = 0.5;
        double alpha = 0.9;
        double beta = 1.8;
        double initialPheromone = 0.1;
        double Q = 0.0001;

        double start = System.currentTimeMillis();
        ArrayList<House> houses = new ArrayList<>(); // Arraylist for storing all the houses

        File file = new File("input.txt");
        Scanner scanner = new Scanner(file);
        int number = 1; // House number of the Migros
        while (scanner.hasNextLine()) { // creates houses and adds them to the arraylist
            String line = scanner.nextLine();
            String[] parts = line.split(",");
            House house = new House();
            house.x = Double.parseDouble(parts[0]);
            house.y = Double.parseDouble(parts[1]);
            house.number = number++;
            houses.add(house);
        }
        scanner.close();

        if (chosenMethod == 1){ // brute-force method
            int[] initialRoute = new int[houses.size() - 1];
            int index = 0;
            for (int i = 1; i < houses.size(); i++) { // creates initial route to permute
                initialRoute[index++] = i;
            }

            permute(initialRoute,0,houses); // calls permutation function

            int[] bestWithMigros = new int[House.bestRoute.length + 2]; // route that Migros is added as first and last element
            bestWithMigros[0] = 1;
            bestWithMigros[House.bestRoute.length + 1] = 1; // sets first and last elements to Migros
            int count = 1;
            for (int i : House.bestRoute) { // sets other elements
                bestWithMigros[count] = i+1;
                count++;
            }

            System.out.println("Method: Brute-Force Method");
            System.out.printf("Shortest Distance: %.5f\n",House.min);
            System.out.println("Shortest Path: " + Arrays.toString(bestWithMigros));

            StdDraw.enableDoubleBuffering();
            for (int i=0;i<bestWithMigros.length-1;i++) { // draws the route
                StdDraw.line(houses.get(bestWithMigros[i]-1).x,houses.get(bestWithMigros[i]-1).y,houses.get(bestWithMigros[i+1]-1).x,houses.get(bestWithMigros[i+1]-1).y);
            }
            StdDraw.setFont(new Font("Arabic",Font.BOLD,11));
            for (House house:houses) { // draws the houses
                if (house.number == 1)
                    StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                else
                    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                StdDraw.filledCircle(house.x, house.y, 0.025);
                StdDraw.setPenColor(StdDraw.BLACK);
                StdDraw.text(house.x,house.y, String.valueOf(house.number));
            }
            StdDraw.show();
        }
        else if (chosenMethod == 2){ // ant colony method
            Random random = new Random();
            ArrayList<Edge> edges= new ArrayList<>();
            for (House house1:houses){ // creates arraylist with all the edges
                for (House house2: houses){
                    if (Objects.equals(house1,house2))
                        continue;
                    Edge edge = new Edge(house1,house2,initialPheromone);
                    edges.add(edge);
                }
            }
            Ant.edges = edges;
            Ant.houses = houses; // sets static variables
            for(int k = 0; k< iterationCount; k++ ) { // executes code as many times as iteration count
                double[] deltas = new double[antCount];
                Edge[][] edgesOfAnts = new Edge[antCount][];
                for (int i = 1; i <= antCount; i++) { // produces as many ants as ant count
                    Ant ant = new Ant();
                    int n = random.nextInt(1, houses.size() + 1); // chooses a random first element
                    int prev_n = n;
                    ant.currentTour.add(n); // sets the first element
                    ant.visited.add(n); // sets first element as visited
                    int m = 0;
                    while (m < houses.size() - 1) { // moves the ant until it finishes the cycle
                        n = ant.move(n, alpha, beta);
                        m++;
                    }
                    ant.currentTour.add(prev_n); // sets the last element
                    for (Edge edge:edges) { // adds the last edge of the tour to the list of edges
                        if ((edge.house_j.number == prev_n) && edge.house_i.number == (ant.currentTour.get(ant.currentTour.size()-2)))
                            ant.edgeOfTour.add(edge);
                    }
                    double totalCycleDistance = calculateRouteDistance(ant.currentTour,houses);
                    double delta = Q / totalCycleDistance;
                    deltas[i-1] = delta; // adds delta of the ant to the deltas array

                    Edge[] edgesOfAnt = new Edge[ant.edgeOfTour.size()]; // array with edges of the ant
                    int idx = 0;
                    for (Edge edge : ant.edgeOfTour) {
                        edgesOfAnt[idx] = edge;
                        idx++;
                    }
                    edgesOfAnts[i-1] = edgesOfAnt; // adds edges of the ant to the edges of the ants array

                    if (totalCycleDistance < House.min){ // checks if distance of the cycle is shorter than the current
                        House.min = totalCycleDistance; // updates the minimum distance
                        House.bestEdgeRoute = ant.edgeOfTour;
                        int count_ = 0;
                        House.bestRoute = new int[ant.currentTour.size()];
                        for (int index: ant.currentTour){ // updates the shortest route
                            House.bestRoute[count_] = index;
                            count_++;
                        }
                    }
                }
                for (int i= 0; i < antCount; i++){
                    for (Edge edge: edgesOfAnts[i]){
                        edge.pheromone += deltas[i]; // updates the pheromones of the edges that are on the cycles
                    }
                }
                for (Edge edge: edges){
                    edge.pheromone *= degradation; // decreases pheromone levels using degradation constant
                }
            }
            int length = House.bestRoute.length;
            int[] bestRouteSorted = new int[length]; // creates array for storing the sorted route
            int index = 0;
            for (int i=0;i<length;i++){ // finds the index that has Migros
                if (House.bestRoute[i] == 1) {
                    index = i;
                    break;
                }
            }
            System.arraycopy(House.bestRoute,index,bestRouteSorted,0,length - index); // copies part of the route after Migros to the sorted route
            int j = 1;
            while ( j < index){ // adds rest of the elements to the sorted route
                bestRouteSorted[length - index + j - 1] = House.bestRoute[j];
                j++;
            }
            bestRouteSorted[length-1] = 1; // sets the last element

            System.out.println("Method: Ant Colony Optimization");
            System.out.printf("Shortest Distance: %.5f\n",House.min);
            System.out.println("Shortest Path: " + Arrays.toString(bestRouteSorted));

            StdDraw.enableDoubleBuffering();
            if (whichToDraw == 2) { // draws the pheromone levels graph
                for (Edge edge : edges) {
                    StdDraw.setPenRadius(8*edge.pheromone); // sets pen radius proportional to the pheromone level
                    StdDraw.line(edge.house_i.x, edge.house_i.y, edge.house_j.x, edge.house_j.y); // draws the pheromone level line
                }
                StdDraw.setFont(new Font("Arabic",Font.BOLD,11));
                for (House house:houses) { // draws all the houses
                    StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                    StdDraw.filledCircle(house.x, house.y, 0.025);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.text(house.x,house.y, String.valueOf(house.number));
                }
            }

            else { // draws the shortest route graph
                for (Edge edge: House.bestEdgeRoute){ // draws edges of the route
                    StdDraw.line(edge.house_i.x,edge.house_i.y,edge.house_j.x,edge.house_j.y);
                }
                StdDraw.setFont(new Font("Arabic",Font.BOLD,11));
                for (House house:houses) { // draws all the houses
                    if (house.number == 1)
                        StdDraw.setPenColor(StdDraw.PRINCETON_ORANGE);
                    else
                        StdDraw.setPenColor(StdDraw.LIGHT_GRAY);
                    StdDraw.filledCircle(house.x, house.y, 0.025);
                    StdDraw.setPenColor(StdDraw.BLACK);
                    StdDraw.text(house.x,house.y, String.valueOf(house.number));
                }
            }
            StdDraw.show();
        }

        double end = System.currentTimeMillis();
        double time = end - start; // time it takes to execute the program in milliseconds
        System.out.printf("Time it takes to find the shortest path: %.2f",time/1000.0);
    }
    public static void permute(int[] initialRoute, int currentIndex, ArrayList<House> houses){ // finds all permutations and sets the minimum distance and the shortest route
        if (currentIndex<initialRoute.length){
            for (int i=currentIndex; i<initialRoute.length; i++) {
                int[] route1 = initialRoute.clone();
                route1[currentIndex] = initialRoute[i];
                route1[i] = initialRoute[currentIndex]; // changes places of two variables
                permute(route1, currentIndex + 1,houses); // calls itself with the new placement it has done
            }
        }else {
            double distance = calculateRouteDistance(initialRoute.clone(),houses,0);
            if (distance<House.min){ // checks if the permutation is shorter than the current route
                House.min = distance;
                House.bestRoute = initialRoute.clone();
            }
        }
    }
    private static double calculateRouteDistance(int[] route, ArrayList<House> houses, int migrosIdx) { // calculates route distance in the brute-force method and returns it
        double distance = 0;
        int prevIdx = migrosIdx;
        for (int locationIdx : route) {
            distance += Math.sqrt(Math.pow(houses.get(locationIdx).x - houses.get(prevIdx).x, 2) +
                    Math.pow(houses.get(locationIdx).y - houses.get(prevIdx).y, 2)); // calculates distance using  Pythagorean theorem and adds it to the total distance
            prevIdx = locationIdx; // updates previous index
        }
        distance += Math.sqrt(Math.pow(houses.get(migrosIdx).x - houses.get(prevIdx).x, 2) +
                Math.pow(houses.get(migrosIdx).y - houses.get(prevIdx).y, 2)); // adds the distance of the last road to the total distance
        return distance;
    }
    private static double calculateRouteDistance(ArrayList<Integer> route, ArrayList<House> houses) { // calculates route distance in the ant colony method and returns it
        double distance = 0;
        int prevIdx = -1;
        for (int locationIdx : route) {
            if (prevIdx == -1) { // finds the index of the first element in the road
                prevIdx = locationIdx-1;
                continue;
            }
            distance += Math.sqrt(Math.pow(houses.get(locationIdx-1).x - houses.get(prevIdx).x, 2) +
                    Math.pow(houses.get(locationIdx-1).y - houses.get(prevIdx).y, 2)); // calculates distance using  Pythagorean theorem and adds it to the total distance
            prevIdx = locationIdx-1;
        }
        return distance;
    }
}