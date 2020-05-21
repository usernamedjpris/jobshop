package jobshop;

import java.io.PrintStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import jobshop.solvers.*;
import net.sourceforge.argparse4j.ArgumentParsers;
import net.sourceforge.argparse4j.inf.ArgumentParser;
import net.sourceforge.argparse4j.inf.ArgumentParserException;
import net.sourceforge.argparse4j.inf.Namespace;


public class Main {

    /** All solvers available in this program */
    private static HashMap<String, Solver> solvers;
    static {
        solvers = new HashMap<>();
        solvers.put("basic", new BasicSolver());
        solvers.put("random", new RandomSolver());
        solvers.put("greedy", new GreedySolver(GreedySolver.Priority.EST_LRPT));
        solvers.put("greedySPT", new GreedySolver(GreedySolver.Priority.SPT));
        solvers.put("greedyLPT", new GreedySolver(GreedySolver.Priority.LPT));
        solvers.put("greedySRPT", new GreedySolver(GreedySolver.Priority.SRPT));
        solvers.put("greedyLRPT", new GreedySolver(GreedySolver.Priority.LRPT));
        solvers.put("greedyEST_SPT", new GreedySolver(GreedySolver.Priority.EST_SPT));
        solvers.put("greedyEST_LPT", new GreedySolver(GreedySolver.Priority.EST_LPT));
        solvers.put("greedyEST_SRPT", new GreedySolver(GreedySolver.Priority.EST_SRPT));
        solvers.put("greedyEST_LRPT", new GreedySolver(GreedySolver.Priority.EST_LRPT));
        solvers.put("descent", new DescentSolver(GreedySolver.Priority.EST_LRPT));
        solvers.put("descentSPT", new DescentSolver(GreedySolver.Priority.SPT));
        solvers.put("descentLPT", new DescentSolver(GreedySolver.Priority.LPT));
        solvers.put("descentSRPT", new DescentSolver(GreedySolver.Priority.SRPT));
        solvers.put("descentLRPT", new DescentSolver(GreedySolver.Priority.LRPT));
        solvers.put("descentEST_SPT", new DescentSolver(GreedySolver.Priority.EST_SPT));
        solvers.put("descentEST_LPT", new DescentSolver(GreedySolver.Priority.EST_LPT));
        solvers.put("descentEST_SRPT", new DescentSolver(GreedySolver.Priority.EST_SRPT));
        solvers.put("descentEST_LRPT", new DescentSolver(GreedySolver.Priority.EST_LRPT));
        solvers.put("taboo", new TabooSolver(GreedySolver.Priority.EST_LRPT, 500, 10));
        solvers.put("tabooSPT", new TabooSolver(GreedySolver.Priority.SPT, 100, 10));
        solvers.put("tabooLPT", new TabooSolver(GreedySolver.Priority.LPT, 100, 10));
        solvers.put("tabooSRPT", new TabooSolver(GreedySolver.Priority.SRPT, 100, 10));
        solvers.put("tabooLRPT", new TabooSolver(GreedySolver.Priority.LRPT, 100, 10));
        solvers.put("tabooEST_SPT", new TabooSolver(GreedySolver.Priority.EST_SPT, 100, 10));
        solvers.put("tabooEST_LPT", new TabooSolver(GreedySolver.Priority.EST_LPT, 100, 10));
        solvers.put("tabooEST_SRPT", new TabooSolver(GreedySolver.Priority.EST_SRPT, 100, 10));
        solvers.put("tabooEST_LRPT", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 10));
        solvers.put("taboo_1", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 1));
        solvers.put("taboo_2", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 2));
        solvers.put("taboo_3", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 3));
        solvers.put("taboo_4", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 4));
        solvers.put("taboo_5", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 5));
        solvers.put("taboo_10", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 10));
        solvers.put("taboo_20", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 20));
        solvers.put("taboo_30", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 30));
        solvers.put("taboo_40", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 40));
        solvers.put("taboo_50", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 50));
        solvers.put("taboo_100", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 100));
        solvers.put("taboo_200", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 200));
        solvers.put("taboo_300", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 300));
        solvers.put("taboo_400", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 400));
        solvers.put("taboo_500", new TabooSolver(GreedySolver.Priority.EST_LRPT, 100, 500));

    }


    public static void main(String[] args) {
        ArgumentParser parser = ArgumentParsers.newFor("jsp-solver").build()
                .defaultHelp(true)
                .description("Solves jobshop problems.");

        parser.addArgument("-t", "--timeout")
                .setDefault(1L)
                .type(Long.class)
                .help("Solver timeout in seconds for each instance");
        parser.addArgument("--solver")
                .nargs("+")
                .required(true)
                .help("Solver(s) to use (space separated if more than one)");

        parser.addArgument("--instance")
                .nargs("+")
                .required(true)
                .help("Instance(s) to solve (space separated if more than one)");

        Namespace ns = null;
        try {
            ns = parser.parseArgs(args);
        } catch (ArgumentParserException e) {
            parser.handleError(e);
            System.exit(1);
        }

        PrintStream output = System.out;

        long solveTimeMs = ns.getLong("timeout") * 1000;

        List<String> solversToTest = ns.getList("solver");
        for(String solverName : solversToTest) {
            if(!solvers.containsKey(solverName)) {
                System.err.println("ERROR: Solver \"" + solverName + "\" is not avalaible.");
                System.err.println("       Available solvers: " + solvers.keySet().toString());
                System.err.println("       You can provide your own solvers by adding them to the `Main.solvers` HashMap.");
                System.exit(1);
            }
        }

        List<String> instancePrefixes = ns.getList("instance");
        List<String> instances = new ArrayList<>();
        for(String instancePrefix : instancePrefixes) {
            List<String> matches = BestKnownResult.instancesMatching(instancePrefix);
            if(matches.isEmpty()) {
                System.err.println("ERROR: instance prefix \"" + instancePrefix + "\" does not match any instance.");
                System.err.println("       available instances: " + Arrays.toString(BestKnownResult.instances));
                System.exit(1);
            }
            instances.addAll(matches);
        }

        float[] runtimes = new float[solversToTest.size()];
        float[] distances = new float[solversToTest.size()];

        try {
            output.print(  "                         ");
            for(String s : solversToTest)
                output.printf("%-30s", s);
            output.println();
            output.print("instance size  best      ");
            for(String s : solversToTest)
                output.print("runtime makespan ecart        ");
            output.println();


            for(String instanceName : instances) {
                int bestKnown = BestKnownResult.of(instanceName);

                Path path = Paths.get("instances/", instanceName);
                Instance instance = Instance.fromFile(path);

                output.printf("%-8s %-5s %4d      ",instanceName, instance.numJobs +"x"+instance.numTasks, bestKnown);

                for(int solverId = 0 ; solverId < solversToTest.size() ; solverId++) {
                    String solverName = solversToTest.get(solverId);
                    Solver solver = solvers.get(solverName);
                    long start = System.currentTimeMillis();
                    long deadline = System.currentTimeMillis() + solveTimeMs;
                    Result result = solver.solve(instance, deadline);
                    long runtime = System.currentTimeMillis() - start;

                    if(!result.schedule.isValid()) {
                        System.err.println("ERROR: solver returned an invalid schedule");
                        System.exit(1);
                    }

                    assert result.schedule.isValid();
                    int makespan = result.schedule.makespan();
                    float dist = 100f * (makespan - bestKnown) / (float) bestKnown;
                    runtimes[solverId] += (float) runtime / (float) instances.size();
                    distances[solverId] += dist / (float) instances.size();

                    output.printf("%7d %8s %5.1f        ", runtime, makespan, dist);
                    output.flush();
                }
                output.println();

            }

            output.printf("%-8s %-5s %4s      ", "AVG", "-", "-");
            for(int solverId = 0 ; solverId < solversToTest.size() ; solverId++) {
                output.printf("%7.1f %8s %5.1f        ", runtimes[solverId], "-", distances[solverId]);
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
