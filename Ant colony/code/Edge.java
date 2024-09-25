public class Edge {
    public House house_i; // starting point of the edge
    public House house_j; // ending point of the edge
    public double pheromone;

    public Edge(House house1, House house2, double pheromone) {
        this.house_i = house1;
        this.house_j = house2;
        this.pheromone = pheromone;
        this.distance = Math.sqrt(Math.pow((house_i.x - house_j.x), 2) + Math.pow((house_i.y - house_j.y), 2));
    }

    public double distance; // length of the edge
}

