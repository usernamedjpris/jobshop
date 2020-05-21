package jobshop;

import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.HashMap;

public class Staboo {
    final int stab[][];
    final int dureeTabou;

    public Staboo (Instance instance, int dureeTabou) {
        this.stab = new int[instance.numJobs*instance.numMachines][instance.numJobs*instance.numMachines];
        this.dureeTabou = dureeTabou;
    }

    public void add(int t1, int t2, int k) {
        stab[t1][t2] = k + dureeTabou;
    }

    public boolean isValid(int t1, int t2, int k){
        return k>stab[t1][t2];
    }
}
