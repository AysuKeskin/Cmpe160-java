import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
/**
 * Program finds the shortest distance and the path between two given cities on the map.
 * @author Aysu Keskin, Student ID: 2023400042
 * @since Date: 25.03.2024
 */
public class main {
    public static void main(String[] args) throws FileNotFoundException {
        int count = getLineCount("city_coordinates.txt"); // finds the line count of the file
        File file = new File("city_coordinates.txt"); // opens the file
        Scanner fileText = new Scanner(file); // scans the file content
        City[] cities = new City[count]; // opens a City array that is going to contain all the cities
        int count2 = -1; // initializes the count variable
        while (fileText.hasNextLine()) { // while loop for filling the cities array
            count2++;
            String st = fileText.nextLine();
            String[] array = st.split(", ");
            City city = new City(array[0], Integer.parseInt(array[1]), Integer.parseInt(array[2])); // creates the city variable
            cities[count2] = city;
        }
        fileText.close();

        int count3 = getLineCount("city_connections.txt"); // finds the line count of the file
        File file2 = new File("city_connections.txt"); // opens the file
        Scanner fileText2 = new Scanner(file2); // scans the file content
        City[][] connectedCities = new City[count3][2]; // opens a two-dimensional City array that is going to contain city connections
        int count4 = -1; // initializes the count variable
        while (fileText2.hasNextLine()) { // while loop for filling the connectedCities array
            City city1 = null;
            City city2 = null;
            count4++;
            String st = fileText2.nextLine();
            String[] array = st.split(",");
            for (City city : cities) { // This for loop finds out which city in cities array equals the city that has this connection.
                if (Objects.equals(array[0], city.cityName)) {
                    city1 = city;
                } else if (Objects.equals(city.cityName, array[1])) {
                    city2 = city;
                }
            }
            connectedCities[count4][0] = city1; // One of the connected cities
            connectedCities[count4][1] = city2; // other connected city
        }
        fileText2.close();

        Scanner input = new Scanner(System.in);
        int startingCity = -1;
        int destinationCity = -1;
        while (true) { // Will work until city inputs are taken correctly
            System.out.print("Enter starting city: ");
            String startingCityName = input.nextLine(); // Takes the city input
            for (int i = 0; i < cities.length; i++) { // looks for the City in cities array with the same name as input
                if (Objects.equals(cities[i].cityName.toUpperCase(), startingCityName.toUpperCase()))
                    startingCity = i;
            }
            if (startingCity == -1) { // Checks if that city exists
                System.out.printf("City named '%s' not found. Please enter a valid city name.\n",startingCityName);
                continue;
            }
            while (true) { // Will work until city input is taken correctly
                System.out.print("Enter destination city: ");
                String destinationCityName = input.nextLine();
                for (int i = 0; i < cities.length; i++) { // looks for the City in cities array with the same name as input
                    if (Objects.equals(cities[i].cityName.toUpperCase(), destinationCityName.toUpperCase()))
                        destinationCity = i;
                }
                if (destinationCity == -1) { // Checks if that city exists
                    System.out.printf("City named '%s' not found. Please enter a valid city name.\n", destinationCityName);
                    continue;
                }
                break;
            }
            break;
        }
        findShortestPath(startingCity, destinationCity, cities, connectedCities); // Finds the shortest path and distance, prints those
    }

    public static int getLineCount(String fileName) throws FileNotFoundException { // Counts the line number of the specified file
        File file = new File(fileName);
        Scanner fileText = new Scanner(file);
        int count = 0;
        while (fileText.hasNextLine()) {
            fileText.nextLine();
            count++;
        }
        fileText.close();
        return count;
    }

    public static void findShortestPath(int startingCity, int destinationCity, City[] cities, City[][] cityConnections) { // Finds the shortest path and distance, prints those
        ArrayList<ArrayList<City>> path = new ArrayList<>(); // array list of all the paths for all the cities
        for (int i = 0; i<cities.length; i++){ // adds path as much as null as the city number.
            path.add(null);
        }
        ArrayList<City> arrayForStarting = new ArrayList<>();
        arrayForStarting.add(cities[startingCity]);
        path.set(startingCity,arrayForStarting); // sets the path of the startingCity

        boolean[] visited = new boolean[cities.length]; // for cities that are visited
        double[] distance = new double[cities.length]; // for the distances of all the cities from the source
        distance[startingCity] = 0;

        for (int i = 0; i < cities.length; i++) { // initializes the distances to infinity
            if (i == startingCity) continue;
            distance[i] = Integer.MAX_VALUE;
        }

        //finds the city that is most close to current city
        for (int i = 0; i < cities.length; i++) {
            int minDistCity = findMinDistCity(distance, visited); // finds the minimum distance city
            visited[minDistCity] = true; //marks the city that is most close to current city as visited
            //checks the neighbors of each city and updating distance array with new distance
            for (int j = 0; j < cities.length; j++) {
                ArrayList<City> pathOfJ;
                boolean ifNeighbor = false;
                for (int k = 0; k < cityConnections.length; k++) { // Checks if cities are neighbor
                    if (cityConnections[k][0].cityName == cities[j].cityName || cityConnections[k][1].cityName == cities[j].cityName) {
                        if (cityConnections[k][0].cityName == cities[minDistCity].cityName || cityConnections[k][1].cityName == cities[minDistCity].cityName) {
                            ifNeighbor = true;
                            break;
                        }
                    }
                }

                double dx = cities[minDistCity].x - cities[j].x;
                double dy = cities[minDistCity].y - cities[j].y;
                double d = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)); // finds the distance between two cities

                if ((d != 0) && (visited[j] == false) && (distance[minDistCity] != Integer.MAX_VALUE) && (ifNeighbor == true)) { // Checks if the necessary parameters are obtained
                    double newDist = distance[minDistCity] + d; // finds the new distance from source to the city
                    if (newDist < distance[j]) { // Checks if the new distance is smaller that the distance that is obtained before
                        distance[j] = newDist;
                        pathOfJ = (ArrayList<City>) path.get(minDistCity).clone(); // copies the path of minimum distance city
                        pathOfJ.add(cities[j]); // adds the last city
                        path.set(j,pathOfJ); // adds obtained path to the list of paths
                    }
                }
            }
        }

        if (path.get(destinationCity) == null){ // if there is no path
            System.out.print("No path could be found.");
            return;
        }

        System.out.printf("Total Distance: %.2f. Path:", distance[destinationCity]); // prints the shortest distance of the destination city from the source

        for (City i:path.get(destinationCity)) { // prints the path
            if (i.cityName == cities[destinationCity].cityName)
                System.out.print(" " + i.cityName);
            else
                System.out.print(" " + i.cityName + " ->");
        }

        StdDraw.enableDoubleBuffering(); // for better performance
        drawMap(cities, "map.png",cityConnections); // draws the map with cities and roads
        drawShortestPath(path.get(destinationCity)); // Draws the obtained shortest path
        StdDraw.show();
    }
    public static int findMinDistCity(double[] distance, boolean[] visited) { // method to find the minimum distant city and return it
        int minCity = -1;
        //travels through the distance array and finds the least distance city which visited is also false
        for (int i = 0; i < distance.length; i++) {
            if (visited[i] == false && (minCity == -1 || distance[i] < distance[minCity])) {
                minCity = i;
            }
        }
        return minCity;
    }
    public static void drawMap(City[] cities,String fileName,City[][] connections){ // method for drawing the map
        StdDraw.setCanvasSize(2377/2,1055/2);
        StdDraw.setXscale(0.0,2377.0);
        StdDraw.setYscale(0.0,1055.0);

        StdDraw.picture(2377/2.0,1055/2.0,fileName,2377,1055); // Draws the given picture
        StdDraw.setPenColor(StdDraw.DARK_GRAY);
        StdDraw.setFont(new Font("Serif",Font.PLAIN,13));

        for (City city:cities){ // draws every city in the cities array
            StdDraw.filledCircle(city.x,city.y,5.0);
            StdDraw.text(city.x,city.y+17.0, city.cityName);
        }

        for (City[] cityArray: connections){ // draws every road
            StdDraw.line(cityArray[0].x,cityArray[0].y,cityArray[1].x,cityArray[1].y);
        }
    }
    public static void drawShortestPath(ArrayList<City> path){ // method to draw the shortest path
       StdDraw.setPenRadius(0.007);
       StdDraw.setPenColor(StdDraw.BOOK_LIGHT_BLUE);
       for (int i= 0; i< path.size()-1; i++){ // draws the lines
           StdDraw.line(path.get(i).x,path.get(i).y,path.get(i+1).x,path.get(i+1).y);
       }
       for (City city:path){ // changes the text colors of the cities in the path
           StdDraw.filledCircle(city.x,city.y,5.0);
           StdDraw.text(city.x,city.y+17.0,city.cityName);
       }
    }
}