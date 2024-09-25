import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

public class Ant {
    public ArrayList<Integer> currentTour = new ArrayList<>();
    public static ArrayList<House> houses; // arraylist of all houses
    public static ArrayList<Edge> edges; // arraylist of all edges
    public ArrayList<Edge> edgeOfTour = new ArrayList<>();
    private final Random r = new Random();
    public ArrayList<Integer> visited = new ArrayList<>(); // visited houses
    private double calculateEdgeValue(Edge edge, double alpha, double beta){ // returns the edge value of an edge
        double edgeValue;
        edgeValue = Math.pow(edge.pheromone, alpha) / Math.pow(edge.distance,beta);
        return edgeValue;
    }
    public int move(int i,double alpha, double beta){ // moves the ant from one node to another
        double totalEdgeValue = 0;
        for (Edge edge: edges){
            if ((Objects.equals(edge.house_i,houses.get(i-1)) ) && ( ! visited.contains(edge.house_j.number))) // checks if starting point of the edge is true, ending point of the edge is not visited
                totalEdgeValue += calculateEdgeValue(edge,alpha, beta);
        }
        for (Edge edge:edges) {
            double pro;
            if ((visited.contains(edge.house_j.number)) || (!(Objects.equals(edge.house_i,houses.get(i-1)))))
                continue;
            double edgeValue = calculateEdgeValue(edge,alpha,beta);
            pro =  edgeValue/ totalEdgeValue; // probability of going in that edge
            if (r.nextDouble(0.0,1.0) < pro) {
                currentTour.add(edge.house_j.number);
                edgeOfTour.add(edge);
                visited.add(edge.house_j.number); // marks the house number as visited
                break;
            }
            else
                totalEdgeValue -= edgeValue;
        }
        return currentTour.getLast(); // returns the last element of the tour
    }
}