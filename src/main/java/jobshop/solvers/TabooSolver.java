package jobshop.solvers;

        import jobshop.*;
        import jobshop.encodings.ResourceOrder;
        import jobshop.encodings.Task;

        import java.util.ArrayList;
        import java.util.Arrays;
        import java.util.HashMap;
        import java.util.List;

public class TabooSolver implements Solver {

    GreedySolver.Priority priority;
    int maxIter;
    int peremption;

    public TabooSolver(GreedySolver.Priority priority, int maxIter, int per) {
        this.priority = priority;
        this.maxIter = maxIter;
        this.peremption = per;
    }

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

        /** copy */
        public Swap copy() {
            return new Swap(this.machine,this.t1,this.t2);
        }
    }


    @Override
    public Result solve(Instance instance, long deadline) {
        //int maxIter = 500;
        // initialisation
        // Générer une solution initiale réalisable
        GreedySolver greedy = new GreedySolver(this.priority);
        // Mémoriser la meilleure solution
        Result s_star = greedy.solve(instance, deadline);
        // Mémoriser la meilleure solution
        Result s = new Result(s_star.instance, s_star.schedule, s_star.cause); //s_star.copy();
        int best = s.schedule.makespan();
        // Mémoriser la solution taboue dans
        Staboo sTaboo = new Staboo(instance, this.peremption);


        int k = 0; // compteur itérations
        // exploration des voisins successifs
        while (k<this.maxIter && deadline - System.currentTimeMillis() > 1) {  // Exploration des voisinages successifs de s
            //Result s_prime = new Result(s_star.instance,s_star.schedule,s_star.cause);
            ResourceOrder resource_order_s = new ResourceOrder(s.schedule);
            ResourceOrder resource_order_star = new ResourceOrder(s_star.schedule);
            ResourceOrder resource_order_prime;
            List<Block> blocs = blocksOfCriticalPath(resource_order_star);

            Swap bestSwap = null;
            int best_makespan = -1;

            for (int i = 0; i < blocs.size(); i++) { // Choisir le meilleur voisin s prime :
                List<Swap> swap = neighbors(blocs.get(i));

                for (int j = 0; j < swap.size(); j++) {
                    if (sTaboo.isValid(swap.get(j).t1+instance.numJobs*swap.get(j).machine, swap.get(j).t2+instance.numJobs*swap.get(j).machine, k)){
                        resource_order_prime = resource_order_star.copy();
                        swap.get(j).applyOn(resource_order_prime);
                        int makespan;
                        try {
                             makespan = resource_order_prime.toSchedule().makespan();
                        } catch (Exception e) {
                            return s;
                        }
                        if (makespan < best_makespan || best_makespan == -1) {
                            bestSwap = swap.get(j);
                            best_makespan = makespan;
                            resource_order_star = resource_order_prime;
                            if (makespan < best) {
                                resource_order_s = resource_order_prime;
                                best = makespan;                                
                            }
                        }
                    }
                }
            }
            if (bestSwap != null) {
                sTaboo.add(bestSwap.t1+instance.numJobs*bestSwap.machine, bestSwap.t2+instance.numJobs*bestSwap.machine, k);
            }
            s_star = new Result(resource_order_star.instance, resource_order_star.toSchedule(), Result.ExitCause.Blocked);
            s = new Result(resource_order_s.instance, resource_order_s.toSchedule(), Result.ExitCause.Blocked);
            k++;
        }
        return s;
    }
    
    /** Returns a list of all blocks of the critical path. */
    List<Block> blocksOfCriticalPath(ResourceOrder order) {
        List<Task> criticalP = order.toSchedule().criticalPath();
        List<Block> listB = new ArrayList<>();

        int machineAv = order.instance.machine(criticalP.get(0));
        int argFirstTask = Arrays.asList(order.tasksByMachine[machineAv]).indexOf(criticalP.get(0));
        int argLastTask = argFirstTask;

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
            if (argFirstTask != argLastTask) {
                listB.add(new Block(machineAv, argFirstTask, argLastTask));
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
}