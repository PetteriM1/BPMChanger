package me.petterim1.bpmchanger;

import com.google.gson.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.util.Scanner;

public class App {

    public static void main(String[] args) {
        System.out.println("BPMChanger version 1.0");
        System.out.println("Made by PetteriM1");
        System.out.println("---------------------");
        String path = null;
        if (args.length > 0) {
            path = args[0];
            System.out.println("Output location set to " + path);
        }
        if (path == null) {
            path = new File("").getAbsolutePath();
            System.out.println("Using default output location");
        }
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        boolean needsInput = true;
        String input = null;
        int oldBPM;
        int newBPM;
        while (running) {
            oldBPM = 0;
            newBPM = 0;
            if (needsInput) {
                System.out.println("Please enter the full path of the original <difficulty>.dat file");
                input = scanner.nextLine();
                while (!input.toLowerCase().contains(".dat")) {
                    System.out.println("Invalid path!");
                    input = scanner.nextLine();
                }
            }
            System.out.println("Please enter old BPM");
            while (oldBPM < 1) {
                try {
                    oldBPM = Integer.parseInt(scanner.nextLine());
                } catch (Exception ignored) {
                }
            }
            System.out.println("Please enter new BPM");
            while (newBPM < 1) {
                try {
                    newBPM = Integer.parseInt(scanner.nextLine());
                } catch (Exception ignored) {
                }
            }
            try {
                File in = new File(input);
                System.out.println("Processing file...");
                JsonObject json = JsonParser.parseReader(new InputStreamReader(new FileInputStream(in))).getAsJsonObject();
                JsonArray notes = json.get("_notes").getAsJsonArray();
                System.out.println(notes.size() + " notes found");
                JsonArray notesModified = new JsonArray();
                for (JsonElement element : notes) {
                    JsonObject note = element.getAsJsonObject();
                    double oldTime = note.get("_time").getAsDouble();
                    double newTime = (newBPM * oldTime) / oldBPM;
                    note.addProperty("_time", newTime);
                    notesModified.add(note);
                }
                if (notesModified.size() != notes.size()) {
                    throw new IllegalStateException("Notes dropped: " + notesModified.size() + " != " + notes.size());
                }
                json.add("_notes", notesModified);
                JsonArray obstacles = json.get("_obstacles").getAsJsonArray();
                System.out.println(obstacles.size() + " obstacles found");
                JsonArray obstaclesModified = new JsonArray();
                for (JsonElement element : obstacles) {
                    JsonObject note = element.getAsJsonObject();
                    double oldTime = note.get("_time").getAsDouble();
                    double newTime = (newBPM * oldTime) / oldBPM;
                    note.addProperty("_time", newTime);
                    obstaclesModified.add(note);
                }
                if (obstaclesModified.size() != obstacles.size()) {
                    throw new IllegalStateException("Obstacles dropped: " + obstaclesModified.size() + " != " + obstacles.size());
                }
                json.add("_obstacles", obstaclesModified);
                JsonArray events = json.get("_events").getAsJsonArray();
                System.out.println(events.size() + " events found");
                JsonArray eventsModified = new JsonArray();
                for (JsonElement element : events) {
                    JsonObject event = element.getAsJsonObject();
                    double oldTime = event.get("_time").getAsDouble();
                    double newTime = (newBPM * oldTime) / oldBPM;
                    event.addProperty("_time", newTime);
                    eventsModified.add(event);
                }
                if (eventsModified.size() != events.size()) {
                    throw new IllegalStateException("Events dropped: " + eventsModified.size() + " != " + events.size());
                }
                json.add("_events", eventsModified);
                System.out.println("Saving...");
                File out = new File(path + File.separatorChar + in.getName());
                FileWriter writer = new FileWriter(out);
                writer.write(json.toString());
                writer.close();
                System.out.println("Done! Modified file saved to " + path);
                System.out.println("Remember to move it before saving a new one");
            } catch (Exception ex) {
                System.out.println("Failed!");
                ex.printStackTrace();
            }
            System.out.println("Do you want to modify another file? (y/n)");
            try {
                input = scanner.nextLine();
                if (input.toLowerCase().startsWith("y")) {
                    needsInput = true;
                    continue;
                } else if (input.toLowerCase().contains(".dat")) {
                    needsInput = false;
                    continue;
                }
            } catch (Exception ignore) {
            }
            running = false;
        }
    }
}
