package jobshop.solvers;

import jobshop.Instance;
import jobshop.Result;
import jobshop.Solver;
import jobshop.encodings.JobNumbers;

import jobshop.encodings.ResourceOrder;
import jobshop.encodings.Task;
import jobshop.Schedule;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GreedySolver implements Solver {
    public enum Priority {SPT, LPT, SRPT, LRPT, EST_SPT, EST_LPT, EST_SRPT, EST_LRPT}

    private Priority priority;
    private int random;

    // constructors
    public GreedySolver(Priority priority, int random) {
        this.priority = priority;
        this.random = random;
    }

    public GreedySolver(Priority priority) { //retrocompatible
        this.priority = priority;
        this.random = 0;
    }

    private int rand(Random generator, int plage){  
        int rand;
        if (plage==1){                                        // pas le choix
            rand = 0;       
        } else {                                              // on choisit aléatoirement
            rand = generator.nextInt(plage-1);                    
        }
        return rand;
    }

    @Override
    public Result solve(Instance instance, long deadline) {

        // INITIALISATION

        ResourceOrder sol = new ResourceOrder(instance);            // initialisation de la solution (representation RessourceOrder)

        ArrayList<Task> NextPossibleTasks = new ArrayList<>();
        for (int i = 0; i < instance.numJobs; i++)                  // initialisation de la liste des taches realisables
            NextPossibleTasks.add(new Task(i, 0));             //... avec première tache de chaque job

        int[] remainingTime = new int[instance.numJobs];            // initialisation de la liste des durees restante pour chaque job
        for (int i = 0; i < instance.numJobs; i++) {
            for (int j = 0; j < instance.numTasks; j++) {
                remainingTime[i] += instance.duration(i, j);        //... avec somme des durees des taches de chaque job
            }
        }

        int[][] tasksFinishAt = new int[instance.numJobs][instance.numTasks];  // instant de terminaison de chaque tache
        int[] machinesAvailableAt = new int[instance.numMachines];             // instant a partir duquel une machine/ressource est dispo

        Random generator = new Random();

        // BOUCLE        
        while (NextPossibleTasks.size() > 0) {                       // tant qu’il y a des tâches réalisables
            Task t;          
            if (this.random > 0 && generator.nextInt(this.random)==0){ 
                if(this.priority == Priority.SPT || this.priority == Priority.LPT) { 
                    t = NextPossibleTasks.remove(this.rand(generator, NextPossibleTasks.size()));    //rand c'est la tache
                } else if(this.priority == Priority.SRPT || this.priority == Priority.LRPT) {
                    int rand = 0; //rand c'est le job
                    int compteur = 0;
                    boolean trouve = false;
                    while (compteur<1000 && !trouve) {
                        int i = this.rand(generator, remainingTime.length); 
                        // maj de rand si le job i>=1 n'est pas deja fini et si (il est plus rapide a finir que rand ou que rand etait deja termine)
                        if (remainingTime[i] != 0 && remainingTime[rand] == 0){
                                rand = i;                                
                                trouve = true;
                            }
                        compteur++;
                    }
                    // maj temps restant de la tache choisie                    
                    int found = 0;
                    int i = 0;
                    while (found==0 && i < NextPossibleTasks.size()) {
                        if (NextPossibleTasks.get(i).job == rand) {  
                            remainingTime[rand] -= instance.duration(rand, NextPossibleTasks.get(i).task);
                            found = i;
                        }
                        i++;
                    }
                    t = NextPossibleTasks.remove(found);  
                } else if (this.priority == Priority.EST_SPT || this.priority == Priority.EST_LPT) {
                    // determination de la liste des taches qui peuvent commencer au plus tot
                    ArrayList<Task> earliestStartingTasks = new ArrayList<>();
                    Task task = NextPossibleTasks.get(0);
                    earliestStartingTasks.add(task);
                    int est;
                    if (task.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[task.job][task.task - 1];
                    }
                    int best = Math.max(machinesAvailableAt[instance.machine(task)], est);
                    for (int i = 1; i < NextPossibleTasks.size(); i++) {
                        task = NextPossibleTasks.get(i);
                        if (task.task == 0) {
                            est = 0;
                        } else {
                            est = tasksFinishAt[task.job][task.task - 1];
                        }
                        int start = Math.max(machinesAvailableAt[instance.machine(task)], est);
                        if (start < best) {
                            earliestStartingTasks.clear();
                            earliestStartingTasks.add(task);
                            best = start;
                        } else if (start == best)
                            earliestStartingTasks.add(task);
                    }

                    int rand = this.rand(generator, earliestStartingTasks.size());

                    // choix de la EST_LPT task
                    t = earliestStartingTasks.remove(rand);

                    if (t.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[t.job][t.task - 1];
                    }
                    tasksFinishAt[t.job][t.task] = Math.max(machinesAvailableAt[instance.machine(t)], est) + instance.duration(t);
                    machinesAvailableAt[instance.machine(t)] = tasksFinishAt[t.job][t.task];
                    NextPossibleTasks.remove(t);

                } else if (this.priority == Priority.EST_SRPT || this.priority == Priority.EST_LRPT) {
                    int[] earliestStartingTime = new int[remainingTime.length];
                    Task task = NextPossibleTasks.get(0);
                    int est;
                    if (task.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[task.job][task.task - 1];
                    }
                    int best = Math.max(machinesAvailableAt[instance.machine(task)], est);
                    earliestStartingTime[task.job] = remainingTime[task.job];
                    for (int i = 1; i < NextPossibleTasks.size(); i++) {
                        task = NextPossibleTasks.get(i);
                        if (task.task == 0) {
                            est = 0;
                        } else {
                            est = tasksFinishAt[task.job][task.task - 1];
                        }
                        int start = Math.max(machinesAvailableAt[instance.machine(task)], est);
                        if (start < best) {
                            earliestStartingTime = new int[remainingTime.length];
                            earliestStartingTime[NextPossibleTasks.get(i).job] = remainingTime[NextPossibleTasks.get(i).job];
                            best = start;
                        } else if (start == best)
                            earliestStartingTime[NextPossibleTasks.get(i).job] = remainingTime[NextPossibleTasks.get(i).job];
                    }

                    int rand = 0;
                    for (int i = 1; i < earliestStartingTime.length; i++) {
                        // maj de rand si le job i>=1 n'est pas deja fini et si (il est plus rapide a finir que rand ou que rand etait deja termine)
                        if (earliestStartingTime[i] != 0 &&  earliestStartingTime[rand] == 0)
                            rand = i;
                    }

                    boolean found = false;
                    int i = 0;
                    // maj temps restant de la tache choisie
                    while (!found && i < NextPossibleTasks.size()) {
                        if (NextPossibleTasks.get(i).job == rand) {
                            earliestStartingTime[rand] -= instance.duration(rand, NextPossibleTasks.get(i).task);
                            rand = i;
                            found = true;
                        }
                        i++;
                    }
                    // choix de la EST_SRPT task
                    t = NextPossibleTasks.remove(rand);

                    if (t.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[t.job][t.task - 1];
                    }
                    tasksFinishAt[t.job][t.task] = Math.max(machinesAvailableAt[instance.machine(t)], est) + instance.duration(t);
                    machinesAvailableAt[instance.machine(t)] = tasksFinishAt[t.job][t.task];
                    remainingTime[t.job] = earliestStartingTime[t.job];

                } else {
                    t = NextPossibleTasks.remove(0);
                    System.out.println("attention oulalallala");
                }
            

                
            } else {                                // sinon on choisit selon priorité
                // 1. Choisir une tache dans cet ensemble...
                /***********************************************************************************************************
                 ***                                                 SPT                                                 ***
                 ***********************************************************************************************************/
                if (priority == Priority.SPT) {
                    int shortestTask = 0;
                    int bestDuration = instance.duration(NextPossibleTasks.get(shortestTask));
                    for (int i = 1; i < NextPossibleTasks.size(); i++) {
                        if (bestDuration > instance.duration(NextPossibleTasks.get(i))) {
                            shortestTask = i;
                            bestDuration = instance.duration(NextPossibleTasks.get(shortestTask));
                        }
                    }
                    t = NextPossibleTasks.remove(shortestTask);


                /***********************************************************************************************************
                 ***                                                 LPT                                                 ***
                 ***********************************************************************************************************/
                } else if (priority == Priority.LPT) {
                    int longestTask = 0;
                    int longestDuration = instance.duration(NextPossibleTasks.get(longestTask));
                    for (int i = 1; i < NextPossibleTasks.size(); i++) {
                        if (longestDuration < instance.duration(NextPossibleTasks.get(i))) {
                            longestTask = i;
                            longestDuration = instance.duration(NextPossibleTasks.get(longestTask));
                        }
                    }
                    t = NextPossibleTasks.remove(longestTask);


                /***********************************************************************************************************
                 ***                                                 SRPT                                                ***
                 ***********************************************************************************************************/
                } else if (priority == Priority.SRPT) {
                    int fastestJob = 0;
                    for (int i = 1; i < remainingTime.length; i++) {
                        // maj de fastestJob si le job i>=1 n'est pas deja fini et si (il est plus rapide a finir que fastestJob ou que fastestJob etait deja termine)
                        if (remainingTime[i] != 0 && (remainingTime[fastestJob] > remainingTime[i] || remainingTime[fastestJob] == 0))
                            fastestJob = i;
                    }
                    boolean found = false;
                    int i = 0;
                    // maj temps restant de la tache choisie
                    while (!found && i < NextPossibleTasks.size()) {
                        if (NextPossibleTasks.get(i).job == fastestJob) {
                            remainingTime[fastestJob] -= instance.duration(fastestJob, NextPossibleTasks.get(i).task);
                            fastestJob = i;
                            found = true;
                        }
                        i++;
                    }
                    t = NextPossibleTasks.remove(fastestJob);


                /***********************************************************************************************************
                 ***                                                 LRPT                                                ***
                 ***********************************************************************************************************/
                } else if (priority == Priority.LRPT) {
                    int slowestJob = 0;
                    for (int i = 1; i < remainingTime.length; i++) {
                        // maj de slowestJob s'il n'est pas le plus lent a finir
                        if (remainingTime[i] > remainingTime[slowestJob])
                            slowestJob = i;
                    }
                    boolean found = false;
                    int i = 0;
                    // maj temps restant de la tache choisie
                    while (!found && i < NextPossibleTasks.size()) {
                        if (NextPossibleTasks.get(i).job == slowestJob) {
                            remainingTime[slowestJob] -= instance.duration(slowestJob, NextPossibleTasks.get(i).task);
                            slowestJob = i;
                            found = true;
                        }
                        i++;
                    }
                    t = NextPossibleTasks.remove(slowestJob);


                /***********************************************************************************************************
                 ***                                               EST_SPT                                              ***
                 ***********************************************************************************************************/
                } else if (priority == Priority.EST_SPT) {
                    // determination de la liste des taches qui peuvent commencer au plus tot
                    ArrayList<Task> earliestStartingTasks = new ArrayList<>();
                    Task task = NextPossibleTasks.get(0);
                    earliestStartingTasks.add(task);
                    int est;
                    if (task.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[task.job][task.task - 1];
                    }
                    int best = Math.max(machinesAvailableAt[instance.machine(task)], est);
                    for (int i = 1; i < NextPossibleTasks.size(); i++) {
                        task = NextPossibleTasks.get(i);
                        if (task.task == 0) {
                            est = 0;
                        } else {
                            est = tasksFinishAt[task.job][task.task - 1];
                        }
                        int start = Math.max(machinesAvailableAt[instance.machine(task)], est);
                        if (start < best) {
                            earliestStartingTasks.clear();
                            earliestStartingTasks.add(task);
                            best = start;
                        } else if (start == best)
                            earliestStartingTasks.add(task);
                    }
                    // algo SPT
                    int shortestTask = 0;
                    int bestDuration = instance.duration(earliestStartingTasks.get(shortestTask));
                    for (int i = 1; i < earliestStartingTasks.size(); i++) {
                        if (bestDuration > instance.duration(earliestStartingTasks.get(i))) {
                            shortestTask = i;
                            bestDuration = instance.duration(earliestStartingTasks.get(shortestTask));
                        }
                    }

                    // choix de la EST_SPT task
                    t = earliestStartingTasks.remove(shortestTask);

                    if (t.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[t.job][t.task - 1];
                    }
                    tasksFinishAt[t.job][t.task] = Math.max(machinesAvailableAt[instance.machine(t)], est) + instance.duration(t);
                    machinesAvailableAt[instance.machine(t)] = tasksFinishAt[t.job][t.task];
                    NextPossibleTasks.remove(t);


                /***********************************************************************************************************
                 ***                                               EST_LPT                                              ***
                 ***********************************************************************************************************/
                } else if (priority == Priority.EST_LPT) {
                    // determination de la liste des taches qui peuvent commencer au plus tot
                    ArrayList<Task> earliestStartingTasks = new ArrayList<>();
                    Task task = NextPossibleTasks.get(0);
                    earliestStartingTasks.add(task);
                    int est;
                    if (task.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[task.job][task.task - 1];
                    }
                    int best = Math.max(machinesAvailableAt[instance.machine(task)], est);
                    for (int i = 1; i < NextPossibleTasks.size(); i++) {
                        task = NextPossibleTasks.get(i);
                        if (task.task == 0) {
                            est = 0;
                        } else {
                            est = tasksFinishAt[task.job][task.task - 1];
                        }
                        int start = Math.max(machinesAvailableAt[instance.machine(task)], est);
                        if (start < best) {
                            earliestStartingTasks.clear();
                            earliestStartingTasks.add(task);
                            best = start;
                        } else if (start == best)
                            earliestStartingTasks.add(task);
                    }

                    // algo LPT sur earliestStartingTask
                    int longestTask = 0;
                    int longestDuration = instance.duration(earliestStartingTasks.get(longestTask));
                    for (int i = 1; i < earliestStartingTasks.size(); i++) {
                        if (longestDuration < instance.duration(earliestStartingTasks.get(i))) {
                            longestTask = i;
                            longestDuration = instance.duration(earliestStartingTasks.get(longestTask));
                        }
                    }

                    // choix de la EST_LPT task
                    t = earliestStartingTasks.remove(longestTask);

                    if (t.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[t.job][t.task - 1];
                    }
                    tasksFinishAt[t.job][t.task] = Math.max(machinesAvailableAt[instance.machine(t)], est) + instance.duration(t);
                    machinesAvailableAt[instance.machine(t)] = tasksFinishAt[t.job][t.task];
                    NextPossibleTasks.remove(t);


                /***********************************************************************************************************
                 ***                                               EST_SRPT                                              ***
                 ***********************************************************************************************************/
                } else if (priority == Priority.EST_SRPT) {
                    int[] earliestStartingTime = new int[remainingTime.length];
                    Task task = NextPossibleTasks.get(0);
                    int est;
                    if (task.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[task.job][task.task - 1];
                    }
                    int best = Math.max(machinesAvailableAt[instance.machine(task)], est);
                    earliestStartingTime[task.job] = remainingTime[task.job];
                    for (int i = 1; i < NextPossibleTasks.size(); i++) {
                        task = NextPossibleTasks.get(i);
                        if (task.task == 0) {
                            est = 0;
                        } else {
                            est = tasksFinishAt[task.job][task.task - 1];
                        }
                        int start = Math.max(machinesAvailableAt[instance.machine(task)], est);
                        if (start < best) {
                            earliestStartingTime = new int[remainingTime.length];
                            earliestStartingTime[NextPossibleTasks.get(i).job] = remainingTime[NextPossibleTasks.get(i).job];
                            best = start;
                        } else if (start == best)
                            earliestStartingTime[NextPossibleTasks.get(i).job] = remainingTime[NextPossibleTasks.get(i).job];
                    }

                    // algo SRPT sur earliestStartingTime en place de remainingTime
                    int fastestJob = 0;
                    for (int i = 1; i < earliestStartingTime.length; i++) {
                        // maj de fastestJob si le job i>=1 n'est pas deja fini et si (il est plus rapide a finir que fastestJob ou que fastestJob etait deja termine)
                        if (earliestStartingTime[i] != 0 && (earliestStartingTime[fastestJob] > earliestStartingTime[i] || earliestStartingTime[fastestJob] == 0))
                            fastestJob = i;
                    }
                    boolean found = false;
                    int i = 0;
                    // maj temps restant de la tache choisie
                    while (!found && i < NextPossibleTasks.size()) {
                        if (NextPossibleTasks.get(i).job == fastestJob) {
                            earliestStartingTime[fastestJob] -= instance.duration(fastestJob, NextPossibleTasks.get(i).task);
                            fastestJob = i;
                            found = true;
                        }
                        i++;
                    }

                    // choix de la EST_SRPT task
                    t = NextPossibleTasks.remove(fastestJob);

                    if (t.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[t.job][t.task - 1];
                    }
                    tasksFinishAt[t.job][t.task] = Math.max(machinesAvailableAt[instance.machine(t)], est) + instance.duration(t);
                    machinesAvailableAt[instance.machine(t)] = tasksFinishAt[t.job][t.task];
                    remainingTime[t.job] = earliestStartingTime[t.job];


                /***********************************************************************************************************
                 ***                                               EST_LRPT                                              ***
                 ***********************************************************************************************************/
                } else if (priority == Priority.EST_LRPT) {
                    int[] earliestStartingTime = new int[remainingTime.length];
                    Task task = NextPossibleTasks.get(0);
                    int est;
                    if (task.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[task.job][task.task - 1];
                    }
                    int best = Math.max(machinesAvailableAt[instance.machine(task)], est);
                    earliestStartingTime[task.job] = remainingTime[task.job];
                    for (int i = 1; i < NextPossibleTasks.size(); i++) {
                        task = NextPossibleTasks.get(i);
                        if (task.task == 0) {
                            est = 0;
                        } else {
                            est = tasksFinishAt[task.job][task.task - 1];
                        }
                        int start = Math.max(machinesAvailableAt[instance.machine(task)], est);
                        if (start < best) {
                            earliestStartingTime = new int[remainingTime.length];
                            earliestStartingTime[NextPossibleTasks.get(i).job] = remainingTime[NextPossibleTasks.get(i).job];
                            best = start;
                        } else if (start == best)
                            earliestStartingTime[NextPossibleTasks.get(i).job] = remainingTime[NextPossibleTasks.get(i).job];
                    }

                    // algo LRPT sur earliestStartingTime en place de remainingTime
                    int slowestJob = 0;
                    for (int i = 1; i < earliestStartingTime.length; i++) {
                        // maj de slowestJob s'il n'est pas le plus lent a finir
                        if (earliestStartingTime[i] > earliestStartingTime[slowestJob])
                            slowestJob = i;
                    }
                    boolean found = false;
                    int i = 0;
                    // maj temps restant de la tache choisie
                    while (!found && i < NextPossibleTasks.size()) {
                        if (NextPossibleTasks.get(i).job == slowestJob) {
                            earliestStartingTime[slowestJob] -= instance.duration(slowestJob, NextPossibleTasks.get(i).task);
                            slowestJob = i;
                            found = true;
                        }
                        i++;
                    }
                    // choix de la EST_SRPT task
                    t = NextPossibleTasks.remove(slowestJob);

                    if (t.task == 0) {
                        est = 0;
                    } else {
                        est = tasksFinishAt[t.job][t.task - 1];
                    }
                    tasksFinishAt[t.job][t.task] = Math.max(machinesAvailableAt[instance.machine(t)], est) + instance.duration(t);
                    machinesAvailableAt[instance.machine(t)] = tasksFinishAt[t.job][t.task];
                    remainingTime[t.job] = earliestStartingTime[t.job];


                } else {
                    System.out.println("priority: " + priority.toString() + " is unknown. Choose SPT, LPT, SRPT, LRPT, EST_SPT, EST_LPT, EST_SRPT or EST_LRPT instead");
                    t = NextPossibleTasks.remove(0);
                }    
            }
            


            // 1. ...et placer cette tâche sur la ressource qu’elle demande
            int machine = instance.machine(t);
            sol.tasksByMachine[machine][sol.nextFreeSlot[machine]++] = t;

            // 2. Mettre a jour l’ensemble des taches realisables
            if (t.task < instance.numTasks - 1)                           // si c'etait pas la derniere a placer
                NextPossibleTasks.add(new Task(t.job, t.task + 1));  // on ajoute la suivante dans les taches realisables
        }
        return new Result(instance, sol.toSchedule(), Result.ExitCause.Blocked);
    }
}