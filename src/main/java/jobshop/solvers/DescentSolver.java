package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Schedule;
import jobshop.Solver;
import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DescentSolver implements Solver {

    /** A block represents a subsequence of the critical path such that all tasks in it execute on the same machine.
     * This class identifies a block in a ResourceOrder representation.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The block with : machine = 1, firstTask = 0 and lastTask = 1
     * Represent the task sequence : [(0,2) (2,1)]
     *
     * */
    static class Block {
        /** machine on which the block is identified */
        final int machine;
        /** index of the first task of the block */
        final int firstTask;
        /** index of the last task of the block */
        final int lastTask;

        Block(int machine, int firstTask, int lastTask) {
            this.machine = machine;
            this.firstTask = firstTask;
            this.lastTask = lastTask;
        }
    }

    /**
     * Represents a swap of two tasks on the same machine in a ResourceOrder encoding.
     *
     * Consider the solution in ResourceOrder representation
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (0,2) (2,1) (1,1)
     * machine 2 : ...
     *
     * The swap with : machine = 1, t1 = 0 and t2 = 1
     * Represent inversion of the two tasks : (0,2) and (2,1)
     * Applying this swap on the above resource order should result in the following one :
     * machine 0 : (0,1) (1,2) (2,2)
     * machine 1 : (2,1) (0,2) (1,1)
     * machine 2 : ...
     */
    static class Swap {
        // machine on which to perform the swap
        final int machine;
        // index of one task to be swapped
        final int t1;
        // index of the other task to be swapped
        final int t2;

        Swap(int machine, int t1, int t2) {
            this.machine = machine;
            this.t1 = t1;
            this.t2 = t2;
        }

        /** Apply this swap on the given resource order, transforming it into a new solution. */
        public void applyOn(ResourceOrder order) {
            Task accoudoir = order.tasksByMachine[this.machine][t1];
            order.tasksByMachine[this.machine][t1] = order.tasksByMachine[this.machine][t2];
            order.tasksByMachine[this.machine][t2] = accoudoir;
        }
    }


    @Override
    public Result solve(Instance instance, long deadline) {
        //initialisation
        GreedySolver greedy = new GreedySolver(GreedySolver.Priority.EST_LRPT);
        Result s_star = greedy.solve(instance, deadline);
        // exploration des voisins successifs
        Boolean ameliorationPossible = true;
        while (ameliorationPossible){
            //Result s_prime = new Result(s_star.instance,s_star.schedule,s_star.cause);
            ResourceOrder resource_order_star = new ResourceOrder(s_star.schedule);
            ResourceOrder resource_order_prime = new ResourceOrder(s_star.schedule);
            List<Block> blocs = blocksOfCriticalPath(resource_order_prime);
            for (int i=0 ; i<blocs.size() ; i++){
                List<Swap> swap = neighbors(blocs.get(i));
                for (int j=0 ; j<swap.size() ; j++){
                    swap.get(j).applyOn(resource_order_prime);
                    // si on minimise l'objectif
                    if (resource_order_prime.toSchedule().makespan() < resource_order_star.toSchedule().makespan()){
                        s_star = new Result(instance, resource_order_prime.toSchedule(), s_star.cause);
                    } else {
                        ameliorationPossible = false;
                    }
                }
            }
        }
        return s_star;
    }

    /** Returns a list of all blocks of the critical path. */
   List<Block> blocksOfCriticalPath(ResourceOrder order) {
        List<Task> criticalP = order.toSchedule().criticalPath();
        List<Block> listB = new ArrayList<>();

        int machineAv = order.instance.machine(criticalP.get(0));
        int argFirstTask = Arrays.asList(order.tasksByMachine[machineAv]).indexOf(criticalP.get(0));
        int argLastTask = argFirstTask;

        Task t = criticalP.get(0);

        for(int i=1; i<criticalP.size(); i++){
            int machineAp = order.instance.machine(criticalP.get(i));
            if (machineAv == machineAp){
                argLastTask++;
            } else {
                if (argFirstTask != argLastTask) {
                    listB.add(new Block(machineAv, argFirstTask, argLastTask));
                }
                machineAv = machineAp; //on change la machine de comparaison
                argFirstTask = Arrays.asList(order.tasksByMachine[machineAv]).indexOf(criticalP.get(i));
                argLastTask = argFirstTask;
            }
        }
        return listB;
    }

    /** For a given block, return the possible swaps for the Nowicki and Smutnicki neighborhood */
    List<Swap> neighbors(Block block) {
        List<Swap> swapy = new ArrayList<Swap>();
        //permutation des deux premieres taches
        swapy.add(new Swap(block.machine, block.firstTask, block.firstTask + 1));
        //...et les deux taches en fin
        swapy.add(new Swap(block.machine, block.lastTask - 1, block.lastTask));
        return swapy;
    }
    /* List<Swap> neighbors(Block block) {                      //trouvait toutes les permutations possibles (avec boucle triangulaire)
        List<Swap> swapy = new ArrayList<Swap>();              //mais ce sont pas tous les swaps qu'on recherche pour Nowicki
        for(int i=block.firstTask; i<=block.lastTask ; i++){
            for(int j=i+1; j<=block.lastTask ; j++){
                swapy.add(new Swap(block.machine, i, j));
            }
        }
        return swapy;
    }*/
}