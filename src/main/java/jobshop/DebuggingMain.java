package jobshop;

import jobshop.encodings.JobNumbers;
import jobshop.encodings.ResourceOrder;

import java.io.IOException;
import java.nio.file.Paths;

public class DebuggingMain {

    public static void main(String[] args) {
        try {
            // load the aaa1 instance
            Instance instance = Instance.fromFile(Paths.get("instances/ft06"));

            // construit une solution dans la représentation par
            // numéro de jobs : [0 1 1 0 0 1]
            // Note : cette solution a aussi été vue dans les exercices (section 3.3)
            //        mais on commençait à compter à 1 ce qui donnait [1 2 2 1 1 2]
            /*JobNumbers enc = new JobNumbers(instance);
            enc.jobs[enc.nextToSet++] = 0;
            enc.jobs[enc.nextToSet++] = 1;
            enc.jobs[enc.nextToSet++] = 1;
            enc.jobs[enc.nextToSet++] = 0;
            enc.jobs[enc.nextToSet++] = 0;
            enc.jobs[enc.nextToSet++] = 1;

            System.out.println("\nENCODING: " + enc);

            Schedule sched = enc.toSchedule();
            System.out.println("SCHEDULE: " + sched.toString());
            System.out.println("VALID: " + sched.isValid());
            System.out.println("MAKESPAN: \n" + sched.makespan());*/

            /* // calcul de Dmax question 6.2
            int sum = 0;
            for(int i=0;i<instance.numJobs;i++){
                for(int j=0;j<instance.numTasks;j++) {
                    int duration = instance.duration(i,j);
                    sum += duration;
                }
            }
            System.out.println("somme = " + sum);
            */


        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }

    }
}
