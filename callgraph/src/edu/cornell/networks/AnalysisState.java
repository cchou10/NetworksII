package edu.cornell.networks;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;

import java.util.Map;

public class AnalysisState {

    public Multimap<String, String> mm;
    private Map<String, Integer> indeg, outdeg;
    public AnalysisState() {
        mm = HashMultimap.create();
        indeg = Maps.newHashMap();
        outdeg = Maps.newHashMap();
    }

    public void addEdge(String me, String it) {
        mm.put(me, it);
    }

    public Map<String, Integer> getIn() {
        if (indeg.size() > 0) {
            return indeg;
        }

        // compute the map of indegrees
        Multimap<String, String> inv = HashMultimap.create();
        Multimaps.invertFrom(mm, inv);
        for (String key : inv.keys()) {
            indeg.put(key, inv.get(key).size());
        }
        return indeg;
    }

    public Map<String, Integer> getOut() {
        if (outdeg.size() > 0) {
            return outdeg;
        }

        // compute the map of outdegrees
        for (String key : mm.keys()) {
            outdeg.put(key, mm.get(key).size());
        }
        return outdeg;
    }

    public static Map<Integer, Integer> distribution(Map<String, Integer> degmap) {
        Map<Integer, Integer> map = Maps.newHashMap();
        for (Integer i : degmap.values()) {
            map.put(i,0);
        }
        for (String _ : degmap.keySet()) {
            int i = degmap.get(_);
            map.put(i, map.get(i)+1);
        }
        return map;
    }
}
