package edu.cornell.networks;

import com.google.common.collect.*;

import java.util.Collections;
import java.util.List;
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

    public static Map<Integer, Integer> cumulativeDistribution(Map<Integer, Integer> dist) {
        List<Integer> arr = Lists.newArrayList(dist.keySet());
        Collections.sort(arr);
        Map<Integer, Integer> map = Maps.newHashMap();
        Integer acc = 0, tot = 0;
        for (int k : arr) tot += dist.get(k);
        for (int k : arr) {
            acc += dist.get(k);
            if (tot - acc == 0) break;
            map.put(k,tot - acc);
        }
        return map;
    }

    public static double[] coefs(Map<Integer, Integer> dist) {
        // let A be n by 2 = [1 & log k(1); 1 & log k(2); ...] then we want to solve the system
        // A*[c; g] = log[y(1); y(2); ...]
        // we know that the least squares solution to this is just (A'A)^-1 * A' * log(y)
        // A'A = [1 sum(log(k)); sum(log(k)) norm(log(k),2)^2] (source: math)
        // since we're working under the transform Log, round off is gonna be a bitch...
        double sk = 0, sy = 0, kk = 0, ky = 0, n = dist.size();
        for (int k : dist.keySet()) {
            double lk = Math.log(k), ly = Math.log(dist.get(k));
            sk += lk;    sy += ly;
            kk += lk*lk; ky += lk*ly;
        }
        double a = (n*kk - sk*sk);

        return new double[]{Math.exp((kk*sy - sk*ky)/a), (n*ky - sk*sy)/a};
    }

    public static String plotInOctave(Map<Integer, Integer> distribution, double c, double g) {
        StringBuilder s = new StringBuilder();
        /**
         * c = [c]; g = [g];
         * k = 1:[maxk];
         * y = c*k.^g;
         * loglog([x],[y],'o',k,y)
         *
         *
         A = @(x)[ones(length(x),1),log(x')]
         solve = @(x,y)(A(x)'*A(x))\(A(x)'*log(y'))
         ss = @(x,y,n)solve(x(1:n),y(1:n))
         */
        s.append("c = ").append(c).append("; g = ").append(g).append(";\nk = 1:");
        int max = 0;
        List<Integer> keys = Lists.newArrayList(distribution.keySet());
        Collections.sort(keys);
        for (int i :keys) {
            if (i > max) {
                max = i;
            }
        }
        s.append(max).append(";\ny = c*k.^g;\nloglog(");
        StringBuilder x = new StringBuilder(), y = new StringBuilder();
        for (int i : keys) {
            x.append(i).append(",");
            y.append(distribution.get(i)).append(",");
        }
        s.append("[").append(x).append("0],[").append(y).append("0],'o',k,y)\n");
        s.append("x = [").append(x).append("0]; \ny = [").append(y).append("0]");
        return s.toString();
    }
}
