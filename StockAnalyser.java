import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/*
 * Class: StockAnalyser
 * Description: This class is used to analyze the stock data.
 *              It uses a Red-Black Tree to store the stock data.
 * Author: Demetrius Chatterjee
 * Github ID: DemetriusChatterjee
 * Version: 1.0
 */
public class StockAnalyser {

    private Node root;
    private static final boolean RED = true;
    private static final boolean BLACK = false;

    /*
     * Class: Node
     * Description: This class is used to store the stock data in the Red-Black Tree.
     * Author: Demetrius Chatterjee
     * Github ID: DemetriusChatterjee
     * Version: 1.0
     */
    private class Node {
        Stock stock;
        Node left, right;
        boolean color;
        
        Node(Stock stock) {
            this.stock = stock;
            this.color = RED;
        }
    }

    /*
     * Class: Stock
     * Description: This class is used to store the stock data.
     * Author: Demetrius Chatterjee
     * Github ID: DemetriusChatterjee
     * Version: 1.0
     */
    public class Stock {
        String name;
        double price;
        double open;
        double high;
        double low; 
        double close;
        double adjClose;
        int volume;
        Date date;

        public Stock(String name, double price, Date date, double open, double high, double low, double close, double adjClose, int volume) {
            this.name = name;
            this.price = price;
            this.date = date;
            this.open = open;
            this.high = high;
            this.low = low;
            this.close = close;
            this.adjClose = adjClose;
            this.volume = volume;
        }
    }

    /**
     * Comparator for sorting stocks by volume.
     * Author: Demetrius Chatterjee
     * Github ID: DemetriusChatterjee
     * Version: 1.0
     */
    public static class VolumeComparator implements Comparator<Stock> {
        @Override
        public int compare(Stock s1, Stock s2) {
            return Integer.compare(s2.volume, s1.volume); // Descending order
        }
    }

    /**
     * Comparator for sorting stocks by opening price.
     * Author: Demetrius Chatterjee
     * Github ID: DemetriusChatterjee
     * Version: 1.0 
     */
    public static class OpenPriceComparator implements Comparator<Stock> {
        @Override
        public int compare(Stock s1, Stock s2) {
            return Double.compare(s2.open, s1.open); // Descending order
        }
    }

    /**
     * Checks if a node is red.
     * @param x The node to check
     * @return true if the node is red, false otherwise
     */
    private boolean isRed(Node x) {
        if (x == null) return false;
        return x.color == RED;
    }

    /**
     * Performs a left rotation on the given node.
     * @param h The node to rotate
     * @return The new root node after rotation
     */
    private Node rotateLeft(Node h) {
        Node x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    /**
     * Performs a right rotation on the given node.
     * @param h The node to rotate
     * @return The new root node after rotation
     */
    private Node rotateRight(Node h) {
        Node x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        return x;
    }

    /**
     * Flips the colors of a node and its two children.
     * @param h The parent node
     */
    private void flipColors(Node h) {
        h.color = RED;
        h.left.color = BLACK;
        h.right.color = BLACK;
    }

    /**
     * Inserts a new stock into the Red-Black tree.
     * @param stock The stock to insert
     */
    public void insert(Stock stock) {
        root = insert(root, stock);
        root.color = BLACK;
    }

    /*
     * Class: StockKey
     * Description: This class is used to store the stock data in the Red-Black Tree.
     * Author: Demetrius Chatterjee
     * Github ID: DemetriusChatterjee
     * Version: 1.0
     */
    private class StockKey implements Comparable<StockKey> {
        String symbol;
        Date date;
        
        public StockKey(String symbol, Date date) {
            this.symbol = symbol;
            this.date = date;
        }
        
        /**
         * Compares two StockKey objects.
         * @param other The StockKey to compare to
         * @return The comparison result
         */
        @Override
        public int compareTo(StockKey other) {
            int symbolCompare = this.symbol.compareTo(other.symbol);
            if (symbolCompare != 0) return symbolCompare;
            return this.date.compareTo(other.date);
        }
    }

    /**
     * Inserts a new stock into the Red-Black tree.
     * @param h The root node of the tree
     * @param stock The stock to insert
     * @return The new root node after insertion
     */
    private Node insert(Node h, Stock stock) {
        if (h == null) return new Node(stock);

        StockKey newKey = new StockKey(stock.name, stock.date);
        StockKey existingKey = new StockKey(h.stock.name, h.stock.date);
        
        int cmp = newKey.compareTo(existingKey);
        if (cmp < 0) h.left = insert(h.left, stock);
        else if (cmp > 0) h.right = insert(h.right, stock);

        if (isRed(h.right) && !isRed(h.left)) h = rotateLeft(h);
        if (isRed(h.left) && isRed(h.left.left)) h = rotateRight(h);
        if (isRed(h.left) && isRed(h.right)) flipColors(h);

        return h;
    }

    /**
     * Searches for a stock by symbol and date.
     * @param symbol The stock symbol to search for
     * @param date The date to search for
     * @return The found Stock object, or null if not found
     */
    public Stock search(String symbol, Date date) {
        return search(root, symbol, date);
    }

    /**
     * Searches for a stock by symbol and date.
     * @param x The root node of the tree
     * @param symbol The stock symbol to search for
     * @param date The date to search for
     * @return The found Stock object, or null if not found
     */
    private Stock search(Node x, String symbol, Date date) {
        while (x != null) {
            int cmp = symbol.compareTo(x.stock.name);
            if (cmp < 0) x = x.left;
            else if (cmp > 0) x = x.right;
            else {
                if (x.stock.date.equals(date)) return x.stock;
                x = x.right;
            }
        }
        return null;
    }

    /**
     * Reads stock data from a CSV file and populates the tree.
     * @param filename The path to the CSV file
     * @throws RuntimeException if there's an error reading the file
     */
    public void readCSV(String filename) {
        try {
            java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(filename));
            reader.readLine();
            String line;
            
            while ((line = reader.readLine()) != null) {
                try {
                    String[] data = line.split(",");
                    Date date = new SimpleDateFormat("yyyy-MM-dd").parse(data[0]);
                    double open = Double.parseDouble(data[1]);
                    double high = Double.parseDouble(data[2]);
                    double low = Double.parseDouble(data[3]);
                    double close = Double.parseDouble(data[4]);
                    int volume = Integer.parseInt(data[5]);
                    String name = data[6];
                    
                    Stock stock = new Stock(name, close, date, open, high, low, close, close, volume);
                    insert(stock);
                } catch (NumberFormatException e) {
                    continue;
                }
            }
            reader.close();
        } catch (Exception e) {
            throw new RuntimeException("Failed to read CSV: " + e.getMessage());
        }
    }

    /**
     * Returns a list of all stocks in the tree.
     * @return List of all stocks
     */
    public List<Stock> getAllStocks() {
        List<Stock> stocks = new ArrayList<>();
        inorderTraversal(root, stocks);
        return stocks;
    }

    /**
     * Performs an inorder traversal of the tree and adds the stocks to the list.
     * @param x The root node of the tree
     * @param stocks The list to add the stocks to
     */
    private void inorderTraversal(Node x, List<Stock> stocks) {
        if (x == null) return;
        inorderTraversal(x.left, stocks);
        stocks.add(x.stock);
        inorderTraversal(x.right, stocks);
    }

    /**
     * Performs bubble sort on an array of stocks.
     * @param arr The array to sort
     * @param comparator The comparator to use for sorting
     */
    public void bubbleSort(Stock[] arr, Comparator<Stock> comparator) {
        long startTime = System.nanoTime();
        int n = arr.length;
        for (int i = 0; i < n-1; i++)
            for (int j = 0; j < n-i-1; j++)
                if (comparator.compare(arr[j], arr[j+1]) < 0) {
                    Stock temp = arr[j];
                    arr[j] = arr[j+1];
                    arr[j+1] = temp;
                }
        long endTime = System.nanoTime();
        System.out.printf("Bubble Sort took %.3f milliseconds%n", (endTime - startTime) / 1_000_000.0);
    }

    /**
     * Performs selection sort on an array of stocks.
     * @param arr The array to sort
     * @param comparator The comparator to use for sorting
     */
    public void selectionSort(Stock[] arr, Comparator<Stock> comparator) {
        long startTime = System.nanoTime();
        int n = arr.length;
        for (int i = 0; i < n-1; i++) {
            int maxIdx = i;
            for (int j = i+1; j < n; j++)
                if (comparator.compare(arr[j], arr[maxIdx]) > 0)
                    maxIdx = j;
            Stock temp = arr[maxIdx];
            arr[maxIdx] = arr[i];
            arr[i] = temp;
        }
        long endTime = System.nanoTime();
        System.out.printf("Selection Sort took %.3f milliseconds%n", (endTime - startTime) / 1_000_000.0);
    }

    /**
     * Performs merge sort on an array of stocks.
     * @param arr The array to sort
     * @param comparator The comparator to use for sorting
     */
    public void mergeSort(Stock[] arr, Comparator<Stock> comparator) {
        long startTime = System.nanoTime();
        mergeSort(arr, 0, arr.length - 1, comparator);
        long endTime = System.nanoTime();
        System.out.printf("Merge Sort took %.3f milliseconds%n", (endTime - startTime) / 1_000_000.0);
    }

    /**
     * Performs the merge sort algorithm on an array of stocks.
     * @param arr The array to sort
     * @param left The left index of the array
     * @param right The right index of the array
     * @param comparator The comparator to use for sorting
     */
    private void mergeSort(Stock[] arr, int left, int right, Comparator<Stock> comparator) {
        if (left < right) {
            int mid = (left + right) / 2;
            mergeSort(arr, left, mid, comparator);
            mergeSort(arr, mid + 1, right, comparator);
            merge(arr, left, mid, right, comparator);
        }
    }

    /**
     * Merges two halves of an array of stocks.
     * @param arr The array to merge
     * @param left The left index of the array
     * @param mid The middle index of the array
     * @param right The right index of the array
     * @param comparator The comparator to use for sorting
     */
    private void merge(Stock[] arr, int left, int mid, int right, Comparator<Stock> comparator) {
        Stock[] temp = new Stock[right - left + 1];
        int i = left, j = mid + 1, k = 0;
        
        while (i <= mid && j <= right) {
            if (comparator.compare(arr[i], arr[j]) >= 0)
                temp[k++] = arr[i++];
            else
                temp[k++] = arr[j++];
        }
        
        while (i <= mid) temp[k++] = arr[i++];
        while (j <= right) temp[k++] = arr[j++]; 
        
        for (i = 0; i < temp.length; i++)
            arr[left + i] = temp[i];
    }

    /**
     * Performs quick sort on an array of stocks.
     * @param arr The array to sort
     * @param comparator The comparator to use for sorting
     */
    public void quickSort(Stock[] arr, Comparator<Stock> comparator) {
        long startTime = System.nanoTime();
        quickSort(arr, 0, arr.length - 1, comparator);
        long endTime = System.nanoTime();
        System.out.printf("Quick Sort took %.3f milliseconds%n", (endTime - startTime) / 1_000_000.0);
    }

    /**
     * Performs the quick sort algorithm on an array of stocks.
     * @param arr The array to sort
     * @param low The left index of the array
     * @param high The right index of the array
     * @param comparator The comparator to use for sorting
     */
    private void quickSort(Stock[] arr, int low, int high, Comparator<Stock> comparator) {
        if (low < high) {
            int pi = partition(arr, low, high, comparator);
            quickSort(arr, low, pi - 1, comparator);
            quickSort(arr, pi + 1, high, comparator);
        }
    }

    /**
     * Partitions an array of stocks.
     * @param arr The array to partition
     * @param low The left index of the array
     * @param high The right index of the array
     * @param comparator The comparator to use for sorting
     * @return The pivot index
     */
    private int partition(Stock[] arr, int low, int high, Comparator<Stock> comparator) {
        Stock pivot = arr[high];
        int i = (low - 1);
        for (int j = low; j < high; j++) {
            if (comparator.compare(arr[j], pivot) >= 0) {
                i++;
                Stock temp = arr[i];
                arr[i] = arr[j];
                arr[j] = temp;
            }
        }
        Stock temp = arr[i + 1];
        arr[i + 1] = arr[high];
        arr[high] = temp;
        return i + 1;
    }

    /**
     * Gets all stocks for a specific date.
     * @param date The date to search for
     * @return List of stocks on the given date
     */
    public List<Stock> getStocksForDate(Date date) {
        List<Stock> allStocks = getAllStocks();
        List<Stock> stocksForDate = new ArrayList<>();
        
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String targetDateStr = sdf.format(date);
        
        System.out.println("Target date: " + targetDateStr);
        
        for (Stock stock : allStocks) {
            String stockDateStr = sdf.format(stock.date);
            if (stockDateStr.equals(targetDateStr)) {
                stocksForDate.add(stock);
            }
        }
        
        return stocksForDate;
    }

    /**
     * Calculates the Simple Moving Average (SMA) for a stock.
     * @param symbol The stock symbol
     * @param date The end date for calculation
     * @param period The number of days to calculate SMA for
     * @return The calculated SMA value
     */
    public double calculateSMA(String symbol, Date date, int period) {
        List<Stock> stocks = getAllStocks();
        List<Stock> symbolStocks = new ArrayList<>();
        for (Stock stock : stocks) {
            if (stock.name.equals(symbol) && !stock.date.after(date)) {
                symbolStocks.add(stock);
            }
        }
        
        if (symbolStocks.size() < period) return 0;
        
        double sum = 0;
        for (int i = symbolStocks.size() - 1; i >= symbolStocks.size() - period; i--) {
            sum += symbolStocks.get(i).close;
        }
        return sum / period;
    }

    /**
     * Gets the price trend for a stock.
     * @param symbol The stock symbol
     * @param date The date to search for
     * @return The price trend
     */
    public String getPriceTrend(String symbol, Date date) {
        double shortSMA = calculateSMA(symbol, date, 5);
        double longSMA = calculateSMA(symbol, date, 20);
        
        if (shortSMA == 0 || longSMA == 0) return "Insufficient data";
        return shortSMA > longSMA ? "Upward Trend" : "Downward Trend";
    }

    /**
     * Gets the average volume for a stock over a date range.
     * @param symbol The stock symbol
     * @param startDate The start date
     * @param endDate The end date
     * @return The average volume
     */
    public double getAverageVolume(String symbol, Date startDate, Date endDate) {
        List<Stock> stocks = getAllStocks();
        long totalVolume = 0;
        int count = 0;
        
        for (Stock stock : stocks) {
            if (stock.name.equals(symbol) && 
                !stock.date.before(startDate) && 
                !stock.date.after(endDate)) {
                totalVolume += stock.volume;
                count++;
            }
        }
        
        return count > 0 ? totalVolume / (double)count : 0;
    }

    /**
     * Displays the statistics for a stock symbol.
     * @param symbol The stock symbol to display statistics for
     */
    public void displayStockStats(String symbol) {
        List<Stock> stocks = getAllStocks();
        double minPrice = Double.MAX_VALUE;
        double maxPrice = Double.MIN_VALUE;
        long totalVolume = 0;
        int count = 0;

        for (Stock stock : stocks) {
            if (stock.name.equals(symbol)) {
                minPrice = Math.min(minPrice, stock.open);
                maxPrice = Math.max(maxPrice, stock.high);
                totalVolume += stock.volume;
                count++;
            }
        }

        if (count > 0) {
            System.out.printf("Statistics for %s: Minimum Price: $%.2f, Maximum Price: $%.2f, Average Volume: %d%n",
                    symbol, minPrice, maxPrice, totalVolume/count);
        } else {
            System.out.println("No data found for symbol: " + symbol);
        }
    }

    /**
     * StockAnalyser class that implements a Red-Black Tree to store and analyze stock data.
     * This class provides functionality for storing, searching, and analyzing stock market data
     * using various sorting algorithms and analysis methods.
     * @param args Command line arguments (not used)
     */
    public static void main(String[] args) {
        StockAnalyser analyser = new StockAnalyser();
        Scanner scanner = new Scanner(System.in);
        
        try {
            //analyser.readCSV("stocks.csv");
            analyser.readCSV(args[0]);
            
            while (true) {
                System.out.println("\nMenu:");
                System.out.println("1. Search stock by date and symbol");
                System.out.println("2. Display top 5 high volume stocks (Bubble Sort)");
                System.out.println("3. Display top 5 high volume stocks (Selection Sort)");
                System.out.println("4. Display statistics for a stock symbol");
                System.out.println("5. Display top 5 high volume stocks (Quick Sort)");
                System.out.println("6. Display simple moving average for a stock");
                System.out.println("7. Display price trend for a stock");
                System.out.println("8. Display average volume over date range");
                System.out.println("9. Display top 5 high volume stocks (Merge Sort)");
                System.out.println("10. Display top 5 highest volume stocks for a specific date");
                System.out.println("11. Display top 5 highest opening price stocks for a specific date");
                System.out.println("12. Exit");
                System.out.print("Enter choice: ");
                
                int choice;
                try {
                    choice = scanner.nextInt();
                    scanner.nextLine();
                } catch (InputMismatchException e) {
                    System.out.println("Error: Please enter a valid number");
                    scanner.nextLine();
                    continue;
                }
                
                switch (choice) {
                    case 1:
                        System.out.print("Enter symbol (e.g., AAPL): ");
                        String symbol = scanner.nextLine();
                        System.out.print("Enter date (YYYY-MM-DD): ");
                        String dateStr = scanner.nextLine();
                        
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                            Stock stock = analyser.search(symbol, date);
                            if (stock != null) {
                                System.out.println("Stock found:");
                                System.out.printf("Name: %s, Date: %s, Open: %.2f, High: %.2f, Low: %.2f, Close: %.2f, Volume: %d%n",
                                        stock.name, dateStr, stock.open, stock.high, stock.low, stock.close, stock.volume);
                            } else {
                                System.out.println("Stock not found");
                            }
                        } catch (ParseException e) {
                            System.out.println("Error: Invalid date format. Please use YYYY-MM-DD");
                        }
                        break;
                        
                    case 2:
                        List<Stock> stocks = analyser.getAllStocks();
                        Stock[] stockArray = stocks.toArray(new Stock[0]);
                        analyser.bubbleSort(stockArray, new VolumeComparator());
                        System.out.println("Top 5 high volume stocks (Bubble Sort):");
                        for (int i = 0; i < Math.min(5, stockArray.length); i++) {
                            System.out.printf("%s: %d%n", stockArray[i].name, stockArray[i].volume);
                        }
                        break;
                        
                    case 3:
                        stocks = analyser.getAllStocks();
                        stockArray = stocks.toArray(new Stock[0]);
                        analyser.selectionSort(stockArray, new VolumeComparator());
                        System.out.println("Top 5 high volume stocks (Selection Sort):");
                        for (int i = 0; i < Math.min(5, stockArray.length); i++) {
                            System.out.printf("%s: %d%n", stockArray[i].name, stockArray[i].volume);
                        }
                        break;
                        
                    case 4:
                        System.out.print("Enter stock symbol: ");
                        symbol = scanner.nextLine();
                        analyser.displayStockStats(symbol);
                        break;

                    case 5:
                        stocks = analyser.getAllStocks();
                        stockArray = stocks.toArray(new Stock[0]);
                        analyser.quickSort(stockArray, new VolumeComparator());
                        System.out.println("Top 5 high volume stocks (Quick Sort):");
                        for (int i = 0; i < Math.min(5, stockArray.length); i++) {
                            System.out.printf("%s: %d%n", stockArray[i].name, stockArray[i].volume);
                        }
                        break;

                    case 6:
                        System.out.print("Enter symbol: ");
                        symbol = scanner.nextLine();
                        System.out.print("Enter date (YYYY-MM-DD): ");
                        dateStr = scanner.nextLine();
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                            double sma5 = analyser.calculateSMA(symbol, date, 5);
                            double sma20 = analyser.calculateSMA(symbol, date, 20);
                            System.out.printf("5-day SMA: %.2f%n20-day SMA: %.2f%n", sma5, sma20);
                        } catch (ParseException e) {
                            System.out.println("Error: Invalid date format");
                        }
                        break;

                    case 7:
                        System.out.print("Enter symbol: ");
                        symbol = scanner.nextLine();
                        System.out.print("Enter date (YYYY-MM-DD): ");
                        dateStr = scanner.nextLine();
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                            System.out.println("Price Trend: " + analyser.getPriceTrend(symbol, date));
                        } catch (ParseException e) {
                            System.out.println("Error: Invalid date format");
                        }
                        break;

                    case 8:
                        System.out.print("Enter symbol: ");
                        symbol = scanner.nextLine();
                        System.out.print("Enter start date (YYYY-MM-DD): ");
                        String startDateStr = scanner.nextLine();
                        System.out.print("Enter end date (YYYY-MM-DD): ");
                        String endDateStr = scanner.nextLine();
                        try {
                            Date startDate = new SimpleDateFormat("yyyy-MM-dd").parse(startDateStr);
                            Date endDate = new SimpleDateFormat("yyyy-MM-dd").parse(endDateStr);
                            double avgVolume = analyser.getAverageVolume(symbol, startDate, endDate);
                            System.out.printf("Average Volume: %.2f%n", avgVolume);
                        } catch (ParseException e) {
                            System.out.println("Error: Invalid date format");
                        }
                        break;

                    case 9:
                        stocks = analyser.getAllStocks();
                        stockArray = stocks.toArray(new Stock[0]);
                        analyser.mergeSort(stockArray, new VolumeComparator());
                        System.out.println("Top 5 high volume stocks (Merge Sort):");
                        for (int i = 0; i < Math.min(5, stockArray.length); i++) {
                            System.out.printf("%s: %d%n", stockArray[i].name, stockArray[i].volume);
                        }
                        break;

                    case 10:
                        System.out.print("Enter date (YYYY-MM-DD): ");
                        dateStr = scanner.nextLine();
                        try {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                            sdf.setLenient(false);
                            Date date = sdf.parse(dateStr);
                            
                            List<Stock> dateStocks = analyser.getStocksForDate(date);
                            
                            if (dateStocks.isEmpty()) {
                                System.out.println("No stocks found for date: " + dateStr);
                                break;
                            }
                            
                            // Sort by volume in descending order
                            dateStocks.sort((s1, s2) -> Long.compare(s2.volume, s1.volume));
                            
                            System.out.println("\nTop 5 highest volume stocks for " + dateStr + ":");
                            for (int i = 0; i < Math.min(5, dateStocks.size()); i++) {
                                Stock stock = dateStocks.get(i);
                                System.out.printf("%s: %,d shares%n", stock.name, stock.volume);
                            }
                        } catch (ParseException e) {
                            System.out.println("Error: Please enter date in YYYY-MM-DD format");
                        }
                        break;

                    case 11:
                        System.out.print("Enter date (YYYY-MM-DD): ");
                        dateStr = scanner.nextLine();
                        try {
                            Date date = new SimpleDateFormat("yyyy-MM-dd").parse(dateStr);
                            List<Stock> dateStocks = analyser.getStocksForDate(date);
                            Stock[] dateStockArray = dateStocks.toArray(new Stock[0]);
                            analyser.quickSort(dateStockArray, new OpenPriceComparator());
                            System.out.println("Top 5 highest opening price stocks for " + dateStr + ":");
                            for (int i = 0; i < Math.min(5, dateStockArray.length); i++) {
                                System.out.printf("%s: $%.2f%n", dateStockArray[i].name, dateStockArray[i].open);
                            }
                        } catch (ParseException e) {
                            System.out.println("Error: Invalid date format");
                        }
                        break;
                        
                    case 12:
                        System.out.println("Goodbye!");
                        scanner.close();
                        return;
                        
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 12");
                }
            }
        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
