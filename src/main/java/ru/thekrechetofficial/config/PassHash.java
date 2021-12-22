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

package ru.thekrechetofficial.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import ru.thekrechetofficial.dto.LapDtoForVariance;
import ru.thekrechetofficial.entity.Lap;
import ru.thekrechetofficial.entity.StageType;

/**
 * @author theValidator <the.validator@yandex.ru>
 */
public class PassHash {
    
    public static void main(String[] args) {
        //String password = new Scanner(System.in).nextLine();
        //System.out.println(new BCryptPasswordEncoder().encode(password));
//        List<Long> laps = Arrays.asList(121873L, 123614L, 124041L, 124332L, 124712L, 124754L);
//        List<Long> laps3 = Arrays.asList(106553L, 107850L, 108229L, 109491L, 110718L, 111722L);
//        List<Long> laps2 = Arrays.asList(2L, 4L, 6L, 8L, 10L);
//        
//        getCoefficientOfVariation(laps);
//        getCoefficientOfVariation(laps2);
//        getCoefficientOfVariation(laps3);

        //System.out.println("==> " + getCoefficientOfVariationTest(2));
        System.out.println(StageType.SS.name());
        
        
        
    }
    
    private static double getCoefficientOfVariation(List<Long> times) {
        double average;
        long sum = 0;
        for (Long t: times) {
            sum += t;
        }
        average = sum / times.size();
        System.out.println("ave: " + average);
        
        double lineDeviance = 0;
        for (Long t: times) {
            lineDeviance += Math.pow((t - average), 2);
        }
        double variance = lineDeviance / times.size();
        System.out.println("var: " + variance);
        double standartDeviance = Math.sqrt(variance);
        System.out.println("stan: " + standartDeviance);
        double coefficientOfVariation = standartDeviance / average * 100;
        System.out.println("ddd: " + standartDeviance * 100);
        
        System.out.println("== " + coefficientOfVariation);
        
        
        return coefficientOfVariation;
    }
    
    private static double getCoefficientOfVariation1(List<Lap> laps, int confAmount) {
        
        long[] sum = new long[confAmount];
        int[] number = new int[confAmount];
        double[] average = new double[confAmount];
        
        for (Lap l: laps) {
            int index = l.getConfiguration()-1;
            sum[index] += l.getLapNumber() + l.getPenalty().getPenaltyTime();
            number[index] += 1;
        }
        for (int i = 0; i < confAmount; i++) {
            average[i] = sum[i] / number[i];
        }
        
        double lineDeviance = 0;
        for (Lap l: laps) {
            int index = l.getConfiguration()-1;
            long time = l.getLapNumber() + l.getPenalty().getPenaltyTime();
            lineDeviance += Math.pow((time - average[index]), 2);
        }
        double variance = lineDeviance / laps.size();
        double averageTime = 0;
        for (int i = 0; i < confAmount; i++) {
            averageTime += average[i];
        }
        averageTime /= confAmount; 
        double standartDeviance = Math.sqrt(variance);
        double coefficientOfVariation = standartDeviance / averageTime * 100;
  
        
        
        return coefficientOfVariation;
    }
    
    private static double getCoefficientOfVariationTest(int confAmount) {
        
        List<LapDtoForVariance> laps = new ArrayList<>();
        
        LapDtoForVariance l1 = new LapDtoForVariance();
        LapDtoForVariance l2 = new LapDtoForVariance();
        LapDtoForVariance l3 = new LapDtoForVariance();
        LapDtoForVariance l4 = new LapDtoForVariance();
        LapDtoForVariance l5 = new LapDtoForVariance();
        LapDtoForVariance l6 = new LapDtoForVariance();
        
        l1.setLapTime(166494L);
        l1.setConfiguration(1);
        l2.setLapTime(164620L);
        l2.setConfiguration(1);
        l3.setLapTime(167100L);
        l3.setConfiguration(1);
        l4.setLapTime(164777L);
        l4.setConfiguration(1);
        l5.setLapTime(187314L);
        l5.setConfiguration(2);
        l6.setLapTime(171204L);
        l6.setConfiguration(2);
        
        laps.add(l1);
        laps.add(l2);
        laps.add(l3);
        laps.add(l4);
        laps.add(l5);
        laps.add(l6);
        
        
        
        
        
        long[] sum = new long[confAmount];
        int[] number = new int[confAmount];
        double[] average = new double[confAmount];
        
        for (LapDtoForVariance l: laps) {
            int index = l.getConfiguration()-1;
            sum[index] += l.getLapTime();
            number[index] += 1;
        }
        for (int i = 0; i < confAmount; i++) {
            average[i] = sum[i] / number[i];
        }
        
        double lineDeviance = 0;
        for (LapDtoForVariance l: laps) {
            int index = l.getConfiguration()-1;
            long time = l.getLapTime();
            lineDeviance += Math.pow((time - average[index]), 2);
        }
        double variance = lineDeviance / laps.size();
        double averageTime = 0;
        for (int i = 0; i < confAmount; i++) {
            averageTime += average[i];
        }
        averageTime /= confAmount; 
        double standartDeviance = Math.sqrt(variance);
        double coefficientOfVariation = standartDeviance / averageTime * 100;
  
       
        
        return coefficientOfVariation;
    }

}
