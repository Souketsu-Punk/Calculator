import java.util.*;
import java.io.*;

public class Calculator_Kiriti{

    private static String input;
    private static int pos = -1, ch;

    private static final LinkedList<String> history = new LinkedList<>();
    private static final String HISTORY_FILE = "calculations.txt";

    public static void main(String[] args) {
        printUsage();

        Scanner sc = new Scanner(System.in);
        System.out.println("Enter expression (or type 'exit' to quit):");
        while (true) {
            System.out.print("> ");
            input = sc.nextLine().trim();

            if (input.equalsIgnoreCase("exit") || input.equalsIgnoreCase("quit")) {
                System.out.println("Goodbye!");
                break;
            }

            switch (input.toLowerCase()) {
                case "history":
                    showHistory();
                    continue;
                case "clear history":
                    clearHistory();
                    continue;
                case "load":
                    loadFromFile();
                    continue;
            }

            try {
                pos = -1;
                ch = -1;
                nextChar();
                double result = parseExpression();
                if (pos < input.length()) {
                    throw new RuntimeException("Unexpected: " + (char) ch);
                }
                System.out.println("Result = " + result);
                String record = input + " = " + result;
                addToHistory(record);
                saveToFile(record);
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        sc.close();
    }

    private static void printUsage() {
        System.out.println("Scientific Calculator");
        System.out.println("Supported operations:");
        System.out.println("  +  -  *  /  ^  ( )");
        System.out.println("Supported functions:");
        System.out.println("  sin(x)   cos(x)   tan(x)");
        System.out.println("  log(x) - base 10 logarithm");
        System.out.println("  ln(x)  - natural logarithm");
        System.out.println("  sqrt(x)  abs(x)");
        System.out.println("  pow(base, exponent)");
        System.out.println();
        System.out.println("Examples:");
        System.out.println("  2+3*4");
        System.out.println("  sin(0.5)");
        System.out.println("  pow(2,3)");
        System.out.println("  (1+2)*3");
        System.out.println("  sqrt(16)");
        System.out.println();
        System.out.println("Special commands:");
        System.out.println("  history         - Show last 5 calculations");
        System.out.println("  clear history   - Clear calculation history");
        System.out.println("  load            - Load previous calculations from file");
        System.out.println("  exit / quit     - Exit the program");
        System.out.println();
    }

    private static void addToHistory(String record) {
        if (history.size() == 5) {
            history.removeFirst();
        }
        history.add(record);
    }

    private static void showHistory() {
        if (history.isEmpty()) {
            System.out.println("History is empty.");
        } else {
            System.out.println("Last 5 calculations:");
            for (String entry : history) {
                System.out.println(entry);
            }
        }
    }

    private static void clearHistory() {
        history.clear();
        System.out.println("History cleared.");
    }

    private static void saveToFile(String record) {
        try (FileWriter fw = new FileWriter(HISTORY_FILE, true)) {
            fw.write(record + "\n");
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    private static void loadFromFile() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) {
            System.out.println("No saved calculations found.");
            return;
        }

        System.out.println("Saved Calculations:");
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error reading from file: " + e.getMessage());
        }
    }

    // Expression Parsing Logic
    private static void nextChar() {
        pos++;
        ch = (pos < input.length()) ? input.charAt(pos) : -1;
    }

    private static boolean eat(int charToEat) {
        while (ch == ' ') nextChar();
        if (ch == charToEat) {
            nextChar();
            return true;
        }
        return false;
    }

    private static double parseExpression() {
        double x = parseTerm();
        while (true) {
            if (eat('+')) x += parseTerm();
            else if (eat('-')) x -= parseTerm();
            else return x;
        }
    }

    private static double parseTerm() {
        double x = parseFactor();
        while (true) {
            if (eat('*')) x *= parseFactor();
            else if (eat('/')) x /= parseFactor();
            else return x;
        }
    }

    private static double parseFactor() {
        if (eat('+')) return parseFactor();
        if (eat('-')) return -parseFactor();

        double x;
        int startPos = pos;

        if (eat('(')) {
            x = parseExpression();
            if (!eat(')')) throw new RuntimeException("Missing closing parenthesis");
        } else if ((ch >= '0' && ch <= '9') || ch == '.') {
            while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
            x = Double.parseDouble(input.substring(startPos, pos));
        } else if (ch >= 'a' && ch <= 'z') {
            while (ch >= 'a' && ch <= 'z') nextChar();
            String func = input.substring(startPos, pos);
            x = parseFactor();
            x = applyFunc(func, x);
        } else {
            throw new RuntimeException("Unexpected: " + (char) ch);
        }

        if (eat('^')) {
            x = Math.pow(x, parseFactor());
        }

        return x;
    }

    private static double applyFunc(String func, double x) {
        switch (func) {
            case "sqrt": return Math.sqrt(x);
            case "sin": return Math.sin(x);
            case "cos": return Math.cos(x);
            case "tan": return Math.tan(x);
            case "log": return Math.log10(x);
            case "ln": return Math.log(x);
            case "abs": return Math.abs(x);
            case "pow": {
                if (!eat('(')) throw new RuntimeException("Expected '(' after pow");
                double y = parseExpression();
                if (!eat(')')) throw new RuntimeException("Expected ')' after pow args");
                return Math.pow(x, y);
            }
            default: throw new RuntimeException("Unknown function: " + func);
        }
    }
}
