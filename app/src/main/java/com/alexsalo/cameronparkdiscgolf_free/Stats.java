package com.alexsalo.cameronparkdiscgolf_free;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by salo on 5/7/2015.
 */
public class Stats {
    public static Map<Integer, Integer> getStatsDistr(ArrayList<ArrayList<Integer>> x, int hole){
        Map<Integer, Integer> distribution = new HashMap<Integer, Integer>();
        for (ArrayList<Integer> list : x){
            int result = list.get(hole);
            int count = distribution.containsKey(result) ? distribution.get(result) : 0;
            distribution.put(result, count + 1);
        }
        return distribution;
    }

    public static ArrayList<Integer> getScoreArray(ArrayList<ArrayList<Integer>> x, int hole){
        ArrayList<Integer> scores = new ArrayList<Integer>();
        for (ArrayList<Integer> list : x){
            int sum = 0;
            boolean doCount = true;
            for (int i = 0; i < hole; i++) {
                if (list.get(hole) == game_screen.NOGAME)
                    doCount = false;

                sum += list.get(i);
            }
            if (doCount)
                scores.add(sum);
        }
        return scores;
    }

    public static int getBest(ArrayList<ArrayList<Integer>> x, int hole){
        int best = 99;
        for (int sum : getScoreArray(x, hole)){
            if (sum < best)
                best = sum;
        }
        return best == 99 ? 0 : best;
    }

    public static double getAvg(ArrayList<ArrayList<Integer>> x, int hole){
        int totalSum = 0;
        ArrayList<Integer> scores = getScoreArray(x, hole);
        for (int sum : scores){
            totalSum += sum;
        }
        return totalSum * 1.0 / scores.size();
    }

    public static double getRecentAvg(ArrayList<ArrayList<Integer>> x, int hole){
        int totalSum = 0;
        ArrayList<Integer> scores = getScoreArray(x, hole);
        for (int i = 0; i < scores.size(); i++){
            totalSum += scores.get(scores.size() - 1 - i);
            if (i >= 2){
                break;
            }
        }
        return totalSum * 1.0 / scores.size();
    }

    public static int getBestScore(ArrayList<ArrayList<Integer>> x, int hole){
        int best = 99;
        for (ArrayList<Integer> list : x){
            if (list.get(hole) != game_screen.NOGAME && list.get(hole) < best){
                best = list.get(hole);
            }
        }
        return best == 99 ? 0 : best;
    }

    public static double getAvgScore(ArrayList<ArrayList<Integer>> x, int hole){
        int avg = 0;
        int size = 0;
        for (ArrayList<Integer> list : x){
            if (list.get(hole) != game_screen.NOGAME) {
                avg += list.get(hole);
                size++;
            }
        }
        return avg * 1.0 / size;
    }

    public static double getRecentAvgScore(ArrayList<ArrayList<Integer>> x, int hole){
        int avg = 0;
        int size = 0;
        for (int i = 0; i < x.size(); i++){
            if (x.get(i).get(hole) != game_screen.NOGAME) {
                avg += x.get(i).get(hole);
                size++;
            }
            if (size >= 2){
                break;
            }
        }
        return avg * 1.0 / size;
    }
}
