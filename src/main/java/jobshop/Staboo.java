package jobshop;

import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.HashMap;

public class Staboo {
    public HashMap<Task, HashMap<Task, Integer>> sTaboo;
    private int dureeTaboo;

    public Staboo(Instance instance, Result s) {
        this.dureeTaboo = 2;
        this.sTaboo = new HashMap<Task, HashMap<Task, Integer>>();
        for (int iTaboo=0 ; iTaboo<instance.numMachines ; iTaboo++){
            for (int jTaboo=0 ; jTaboo<instance.numTasks ; jTaboo++){
                for (int idedans=0 ; idedans<instance.numMachines ; idedans++){
                    for (int jdedans=0 ; jdedans<instance.numTasks ; jdedans++) {
                        HashMap<Task, Integer> dedans = new HashMap<Task, Integer>();
                        dedans.put(new ResourceOrder(s.schedule).tasksByMachine[idedans][jdedans], 0);
                        sTaboo.put(new ResourceOrder(s.schedule).tasksByMachine[iTaboo][jTaboo], dedans);
                    }
                }
            }
        }
    }

    public void add(Task t1, Task t2, int k){
        HashMap<Task, Integer> intermediaire = new HashMap<Task, Integer>();
        intermediaire.put(t1,k+this.dureeTaboo);
        this.sTaboo.put(t2,intermediaire);
    }

    public boolean isStaboo(Task t1, Task t2, int k){
        return k>sTaboo.get(t1).get(t2);
    }
}
