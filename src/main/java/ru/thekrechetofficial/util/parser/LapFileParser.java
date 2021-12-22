/*
 * Copyright (C) 2021 theValidator <the.validator@yandex.ru>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package ru.thekrechetofficial.util.parser;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import ru.thekrechetofficial.entity.Crew;
import ru.thekrechetofficial.entity.Lap;
import ru.thekrechetofficial.entity.Penalty;
import ru.thekrechetofficial.entity.Stage;
import ru.thekrechetofficial.util.Data;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
public class LapFileParser {

    public static final String MY_LAPS_SS = "\\d+\\t(?<crewStartNum>\\d+)\\t(?<laptime>[0-9:.,]+)\\t.+";
    public static final String MY_LAPS_PRA_CREW = "(\\d+) - [a-zA-Zа-яА-Я ]+";
    //from atron \\d{1,2}:\d{2}:\d{2}.\d{3}\t\d+\t(?<laptime>\d{1,2}:\d{2}.\d{3})
    //from champ \\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\t\\d+\\t\\d+\\t(?<laptime>\\d{1,2}:\\d{2}.\\d{3})
    public static final String MY_LAPS_PRA_LAP = "\\d{1,2}:\\d{2}:\\d{2}.\\d{3}\\t\\d+\\t(?<laptime>\\d{1,2}:\\d{2}.\\d{3})";
    public static final String MY_TEST = "(?<crewStartNum>\\d{1,4})\\t(?<laptime>[0-9:.,]+)";
    private static final String RACE_RUSSIA = "(?<crewStartNum>^\\d{1,3})"
                                            + "(?<lapData>(\\t\\d{1}:\\d{2},\\d{3})*)";
    
    
    public static void parseSSLapFileMyLapsPattern() {
        
    }
    
    public static void parsePracticeLapFileMyLapsPattern() {
        
    }
    
    

    public static int parseLapCustomTemplate(BufferedReader reader, List<Lap> laps, Stage stage,
            Map<Integer, Crew> crewMap, Map<Long, Integer> lapNumbers) throws IOException {

        String rootPath = Data.RESOURCES;
        String template = rootPath + Data.PARSE_PROPERTIES;
        
        System.out.println("template: " + template);  //debug
        
        Properties parseProperty = new Properties();      
        parseProperty.load(new FileInputStream(template));
        
        String crewProperty = parseProperty.getProperty("data.crew");     //properties 
        String lapProperty = parseProperty.getProperty("data.lap");       //properties 

        String regex = String.format("(?<crewStartNum>%s)(?<lapData>%s)", crewProperty, lapProperty);
        
        System.out.println("regex: " + regex);   //debug

        Pattern p = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS);

        String line;
        int result = 0;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = p.matcher(line);
            while (matcher.find()) {
                int crewStartNum = Integer.parseInt(matcher.group("crewStartNum"));

                if (crewMap.containsKey(crewStartNum)) {
                    //if (true) {
                    String lapData = matcher.group("lapData").trim();
                    String[] lapArray = lapData.split("\t");
                    for (int i = 0; i < lapArray.length; i++) {

                        Crew crew = crewMap.get(crewStartNum);
                        Lap lap = new Lap();
                        int minutes = Integer.parseInt(lapArray[i].substring(0, 1));
                        int seconds = Integer.parseInt(lapArray[i].substring(2, 4));
                        int millsec = Integer.parseInt(lapArray[i].substring(5));

                        long lapTime = Duration.ofMinutes(minutes).plusSeconds(seconds).plusMillis(millsec).toMillis();
                        lap.setStage(stage);
                        lap.setCrew(crew);
                        int lapNumber = lapNumbers.get(crew.getId()) + 1;
                        lap.setLapNumber(lapNumber);
                        lapNumbers.put(crew.getId(), lapNumber);
                        lap.setLapTime(lapTime);
                        lap.setPenalty(new Penalty());
                        laps.add(lap);
                    }
                } else {
                    result = 1;
                    System.out.println("crewNum:" + crewStartNum);
                }

            }

        }

        return result; // 0 if all lines parsed, 1 if something missed

    }

    public static void parseLapsRaceRussia(BufferedReader reader, List<Lap> laps, Stage stage,
            Map<Integer, Crew> crewMap, Map<Long, Integer> lapNumbers) throws IOException {

        Pattern p = Pattern.compile(RACE_RUSSIA, Pattern.UNICODE_CHARACTER_CLASS);

        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = p.matcher(line);
            while (matcher.find()) {
                int crewStartNum = Integer.parseInt(matcher.group("crewStartNum"));

                if (crewMap.containsKey(crewStartNum)) {
                    //if (true) {
                    String lapData = matcher.group("lapData").trim();
                    String[] lapArray = lapData.split("\t");
                    for (int i = 0; i < lapArray.length; i++) {

                        Crew crew = crewMap.get(crewStartNum);
                        Lap lap = new Lap();
                        int minutes = Integer.parseInt(lapArray[i].substring(0, 1));
                        int seconds = Integer.parseInt(lapArray[i].substring(2, 4));
                        int millsec = Integer.parseInt(lapArray[i].substring(5));

                        long lapTime = Duration.ofMinutes(minutes).plusSeconds(seconds).plusMillis(millsec).toMillis();
                        lap.setStage(stage);
                        lap.setCrew(crew);
                        int lapNumber = lapNumbers.get(crew.getId()) + 1;
                        lap.setLapNumber(lapNumber);
                        lapNumbers.put(crew.getId(), lapNumber);
                        lap.setLapTime(lapTime);
                        lap.setPenalty(new Penalty());
                        laps.add(lap);
                    }
                }

            }

        }

    }

    public static void parseLapsMyTest(BufferedReader reader, List<Lap> laps, Stage stage,
            Map<Integer, Crew> crewMap, Map<Long, Integer> lapNumbers) throws IOException {

        Pattern p = Pattern.compile(MY_TEST);
        String line;
        while ((line = reader.readLine()) != null) {
            Matcher matcher = p.matcher(line);

            while (matcher.find()) {
                int crewStartNum = Integer.parseInt(matcher.group("crewStartNum"));
                int minutes = Integer.parseInt(matcher.group("min"));
                int seconds = Integer.parseInt(matcher.group("sec"));
                int millsec = Integer.parseInt(matcher.group("ms"));

                if (crewMap.containsKey(crewStartNum)) {
                    Crew crew = crewMap.get(crewStartNum);
                    Lap lap = new Lap();
                    long lapTime = Duration.ofMinutes(minutes).plusSeconds(seconds).plusMillis(millsec).toMillis();
                    lap.setStage(stage);
                    lap.setCrew(crew);
                    int lapNumber = lapNumbers.get(crew.getId()) + 1;
                    lap.setLapNumber(lapNumber);
                    lapNumbers.put(crew.getId(), lapNumber);
                    lap.setLapTime(lapTime);
                    lap.setPenalty(new Penalty());
                    laps.add(lap);
                }
            }

        }

    }

}
