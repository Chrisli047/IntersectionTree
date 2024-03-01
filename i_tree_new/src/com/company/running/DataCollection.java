package com.company.running;

import java.io.*;
import java.sql.SQLException;
import java.util.ArrayList;

public class DataCollection {

    // ***************
    // DATA COLLECTION
    // ***************

    // Does not include boundary inequalities
    final static int num_inequality_default = 50;
    final static int num_dimension_default = 5;
    final static int domain_boundary_length_default = 1;

    // Number of runs per variable
    final static int unique_runs = 10;
    final static int repeat_runs = 10;

    public static void collectData() throws IOException, SQLException {
        int[] table_counter = new int[]{0};
        collect_data_individual_feasibility_checks();
        collect_data_tree_path(table_counter);
        collect_data_tree_construction(table_counter);
    }

    public static void collect_data_individual_feasibility_checks() throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("individual_feasibility_checks.txt"));

        writer.write("Variable #Inequalities\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_inequality = 3; num_inequality <= 100; num_inequality++) {
            writer.write(num_inequality + ",");
            time_individual_feasibility_checks(num_dimension_default, num_inequality, domain_boundary_length_default,
                    writer);
        }

        writer.write("\nVariable #Dimensions\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_dimension = 2; num_dimension <= 10; num_dimension++) {
            writer.write(num_dimension + ",");
            time_individual_feasibility_checks(num_dimension, num_inequality_default, domain_boundary_length_default,
                    writer);
        }

        writer.write("\nVariable Domain Boundary Length\n");
        writer.write("Domain Boundary Length,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int domain_boundary_length = 1; domain_boundary_length <= 10; domain_boundary_length++) {
            writer.write(domain_boundary_length + ",");
            time_individual_feasibility_checks(num_dimension_default, num_inequality_default, domain_boundary_length,
                    writer);
        }

        writer.close();
    }

    public static void collect_data_tree_path(int[] table_counter) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("tree_path.txt"));

        writer.write("Variable #Inequalities\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_inequality = 1; num_inequality <= 100; num_inequality++) {
            writer.write(num_inequality + ",");
            time_tree_path(num_dimension_default, num_inequality, domain_boundary_length_default, writer, table_counter);
        }

        writer.write("\nVariable #Dimensions\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_dimension = 2; num_dimension <= 10; num_dimension++) {
            writer.write(num_dimension + ",");
            time_tree_path(num_dimension, num_inequality_default, domain_boundary_length_default, writer, table_counter);
        }

        writer.write("\nVariable Domain Boundary Length\n");
        writer.write("Domain Boundary Length,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int domain_boundary_length = 1; domain_boundary_length <= 10; domain_boundary_length++) {
            writer.write(domain_boundary_length + ",");
            time_tree_path(num_dimension_default, num_inequality_default, domain_boundary_length, writer, table_counter);
        }

        writer.close();
    }

    public static void collect_data_tree_construction(int[] table_counter)
            throws IOException, SQLException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("tree_construction.txt"));

        writer.write("Variable #Inequalities\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_inequality = 1; num_inequality <= 100; num_inequality++) {
            writer.write(num_inequality + ",");
            time_tree_construction(num_dimension_default, num_inequality, domain_boundary_length_default, writer,
                    table_counter);
        }

        writer.write("\nVariable #Dimensions\n");
        writer.write("#Inequalities,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int num_dimension = 2; num_dimension <= 10; num_dimension++) {
            writer.write(num_dimension + ",");
            time_tree_construction(num_dimension, num_inequality_default, domain_boundary_length_default, writer,
                    table_counter);
        }

        writer.write("\nVariable Domain Boundary Length\n");
        writer.write("Domain Boundary Length,Simplex,Sign-Changing Simplex,Parametric Equation\n");
        for (int domain_boundary_length = 1; domain_boundary_length <= 10; domain_boundary_length++) {
            writer.write(domain_boundary_length + ",");
            time_tree_construction(num_dimension_default, num_inequality_default, domain_boundary_length, writer,
                    table_counter);
        }

        writer.close();
    }

    private static void time_individual_feasibility_checks(int num_dimension, int num_inequality,
                                                           int domain_boundary_length, BufferedWriter writer)
            throws IOException {
        long average_time_simplex = 0;
        long average_time_sign_changing_simplex = 0;
        long average_time_parametric_equation = 0;
        long average_time_repeat, start_time, stop_time;

        for (int i = 0; i < unique_runs; i++) {
            // Equations defining subdomain
            ArrayList<double[]> inequalities = Test.generate_inequalities(num_inequality, num_dimension, domain_boundary_length);
            // Equation of line for feasibility checking. Hijacks generate_inequalities().
            Function function = new Function(Test.generate_equation(num_dimension));

            // Uncomment if slack variables required
//            // Modifications for Simplex:
//            // Introduce slack variables
//            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
//            // Separate constraint constants
//            ArrayList<Double> constraintConstants = new ArrayList<>();
//            for (double[] inequality : inequalities) {
//                // ignore constant at the end
//                double[] slackenedEquation = new double[inequality.length * 2 - 2];
//                for (int j = 0; j < inequality.length - 1; j++) {
//                    slackenedEquation[j * 2] = inequality[j];
//                    slackenedEquation[j * 2 + 1] = -inequality[j];
//                }
//                constraintConstants.add(inequality[inequality.length - 1]);
//                constraintCoefficients.add(slackenedEquation);
//            }

            // Modifications for Simplex:
            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
            // Separate constraint constants
            ArrayList<Double> constraintConstants = new ArrayList<>();
            for (double[] inequality : inequalities) {
                // ignore constant at the end
                double[] slackenedEquation = new double[inequality.length - 1];
                System.arraycopy(inequality, 0, slackenedEquation, 0, inequality.length - 1);
                constraintConstants.add(inequality[inequality.length - 1]);
                constraintCoefficients.add(slackenedEquation);
            }

            // Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                NodeData.ifPartitionsDomain(new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), function,
                        SimplexType.SIMPLEX, num_dimension, null, null, false, false);
                stop_time = System.nanoTime();
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_simplex += average_time_repeat / unique_runs;

            // Sign-Changing Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                NodeData.ifPartitionsDomain(new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), function,
                        SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension, null, null, false, false);
                stop_time = System.nanoTime();
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_sign_changing_simplex += average_time_repeat / unique_runs;
        }

        writer.write(average_time_simplex + ",");
        writer.write(average_time_sign_changing_simplex + ",");
        writer.write(average_time_parametric_equation + "\n");
    }

    private static void time_tree_path(int num_dimension, int num_inequality, int domain_boundary_length,
                                       BufferedWriter writer, int[] table_counter)
            throws IOException {
        long average_time_simplex = 0;
        long average_time_sign_changing_simplex = 0;
        long average_time_parametric_equation = 0;
        long average_time_repeat, start_time, stop_time;

        for (int i = 0; i < unique_runs; i++) {
            // Equations defining subdomain
            ArrayList<double[]> inequalities = Test.generate_inequalities(0, num_dimension,
                    domain_boundary_length);
            Function[] functions = new Function[num_inequality];
            for (int j = 0; j < functions.length; j++) {
                functions[j] = new Function(Test.generate_equation(num_dimension));
            }

            // Uncomment if slack variables required
//            // Modifications for Simplex:
//            // Introduce slack variables
//            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
//            // Separate constraint constants
//            ArrayList<Double> constraintConstants = new ArrayList<>();
//            for (double[] inequality : inequalities) {
//                // ignore constant at the end
//                double[] slackenedEquation = new double[inequality.length * 2 - 2];
//                for (int j = 0; j < inequality.length - 1; j++) {
//                    slackenedEquation[j * 2] = inequality[j];
//                    slackenedEquation[j * 2 + 1] = -inequality[j];
//                }
//                constraintConstants.add(inequality[inequality.length - 1]);
//                constraintCoefficients.add(slackenedEquation);
//            }

            // Modifications for Simplex:
            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
            // Separate constraint constants
            ArrayList<Double> constraintConstants = new ArrayList<>();
            for (double[] inequality : inequalities) {
                // ignore constant at the end
                double[] slackenedEquation = new double[inequality.length - 1];
                System.arraycopy(inequality, 0, slackenedEquation, 0, inequality.length - 1);
                constraintConstants.add(inequality[inequality.length - 1]);
                constraintCoefficients.add(slackenedEquation);
            }

            // Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                Tree.constructTreeSegmentSimplex(functions, new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), SimplexType.SIMPLEX, num_dimension,
                        domain_boundary_length);
                stop_time = System.nanoTime();
                table_counter[0]++;
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_simplex += average_time_repeat / unique_runs;

            // Sign-Changing Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                Tree.constructTreeSegmentSimplex(functions, new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension,
                        domain_boundary_length);
                stop_time = System.nanoTime();
                table_counter[0]++;
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_sign_changing_simplex += average_time_repeat / unique_runs;
        }

        writer.write(average_time_simplex + ",");
        writer.write(average_time_sign_changing_simplex + ",");
        writer.write(average_time_parametric_equation + "\n");
    }

    private static void time_tree_construction(int num_dimension, int num_inequality, int domain_boundary_length,
                                               BufferedWriter writer, int[] table_counter)
            throws IOException, SQLException {
        long average_time_simplex = 0;
        long average_time_sign_changing_simplex = 0;
        long average_time_parametric_equation = 0;
        long average_time_repeat, start_time, stop_time;

        for (int i = 0; i < unique_runs; i++) {
            // Equations defining subdomain
            ArrayList<double[]> inequalities = Test.generate_inequalities(0, num_dimension,
                    domain_boundary_length);
            Function[] functions = new Function[num_inequality];
            for (int j = 0; j < functions.length; j++) {
                functions[j] = new Function(Test.generate_equation(num_dimension));
            }


            // Uncomment if slack variables required
//            // Modifications for Simplex:
//            // Introduce slack variables
//            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
//            // Separate constraint constants
//            ArrayList<Double> constraintConstants = new ArrayList<>();
//            for (double[] inequality : inequalities) {
//                // ignore constant at the end
//                double[] slackenedEquation = new double[inequality.length * 2 - 2];
//                for (int j = 0; j < inequality.length - 1; j++) {
//                    slackenedEquation[j * 2] = inequality[j];
//                    slackenedEquation[j * 2 + 1] = -inequality[j];
//                }
//                constraintConstants.add(inequality[inequality.length - 1]);
//                constraintCoefficients.add(slackenedEquation);
//            }

            // Modifications for Simplex:
            NodeData d = new NodeData(-1, null, null, null, num_dimension);
            ArrayList<double[]> constraintCoefficients = new ArrayList<>();
            // Separate constraint constants
            ArrayList<Double> constraintConstants = new ArrayList<>();
            for (double[] inequality : inequalities) {
                // ignore constant at the end
                double[] slackenedEquation = new double[inequality.length - 1];
                System.arraycopy(inequality, 0, slackenedEquation, 0, inequality.length - 1);
                constraintConstants.add(inequality[inequality.length - 1]);
                constraintCoefficients.add(slackenedEquation);
            }

            // Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                Tree.constructTreeSimplex(functions, d, new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), SimplexType.SIMPLEX, num_dimension,
                        "IntersectionTree" + table_counter[0]);
                stop_time = System.nanoTime();
                table_counter[0]++;
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_simplex += average_time_repeat / unique_runs;

            // Sign-Changing Simplex
            average_time_repeat = 0;
            for (int j = 0; j < repeat_runs; j++) {
                start_time = System.nanoTime();
                Tree.constructTreeSimplex(functions, d, new ArrayList<>(constraintCoefficients),
                        new ArrayList<>(constraintConstants), SimplexType.SIGN_CHANGING_SIMPLEX, num_dimension,
                        "IntersectionTree" + table_counter[0]);
                stop_time = System.nanoTime();
                table_counter[0]++;
                average_time_repeat += (stop_time - start_time) / repeat_runs;
            }
            average_time_sign_changing_simplex += average_time_repeat / unique_runs;
        }

        writer.write(average_time_simplex + ",");
        writer.write(average_time_sign_changing_simplex + ",");
        writer.write(average_time_parametric_equation + "\n");
    }

    public static void parseDataFiles() {
        try {
            File individual_inequality_naive = new File("Data_Individual_Partition_Variable_Inequalities_Simplex.txt");
            individual_inequality_naive.delete();
            File individual_inequality_sign_changing = new File("Data_Individual_Partition_Variable_Inequalities_Sign_Changing_Simplex.txt");
            individual_inequality_sign_changing.createNewFile();
            File individual_dimension_naive = new File("Data_Individual_Partition_Variable_Dimensions_Simplex.txt");
            individual_inequality_naive.createNewFile();
            File individual_dimension_sign_changing = new File("Data_Individual_Partition_Variable_Dimensions_Sign_Changing_Simplex.txt");
            individual_inequality_sign_changing.createNewFile();
            File individual_domain_naive = new File("Data_Individual_Partition_Variable_Domain_Simplex.txt");
            individual_inequality_naive.createNewFile();
            File individual_domain_sign_changing = new File("Data_Individual_Partition_Variable_Domain_Sign_Changing_Simplex.txt");
            individual_inequality_sign_changing.createNewFile();

            File inputFile = new File("individual_feasibility_checks.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            int lineCount = 1;

            while (line != null && lineCount < 3) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 100) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(individual_inequality_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(individual_inequality_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 104) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 112) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(individual_dimension_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(individual_dimension_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 116) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 125) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(individual_domain_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(individual_domain_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File path_inequality_naive = new File("Data_Tree_Path_Variable_Inequalities_Simplex.txt");
            path_inequality_naive.createNewFile();
            File path_inequality_sign_changing = new File("Data_Tree_Path_Variable_Inequalities_Sign_Changing_Simplex.txt");
            path_inequality_sign_changing.createNewFile();
            File path_dimension_naive = new File("Data_Tree_Path_Variable_Dimensions_Simplex.txt");
            path_inequality_naive.createNewFile();
            File path_dimension_sign_changing = new File("Data_Tree_Path_Variable_Dimensions_Sign_Changing_Simplex.txt");
            path_inequality_sign_changing.createNewFile();
            File path_domain_naive = new File("Data_Tree_Path_Variable_Domain_Simplex.txt");
            path_inequality_naive.createNewFile();
            File path_domain_sign_changing = new File("Data_Tree_Path_Variable_Domain_Sign_Changing_Simplex.txt");
            path_inequality_sign_changing.createNewFile();

            File inputFile = new File("tree_path.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            int lineCount = 1;

            while (line != null && lineCount < 3) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 102) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(path_inequality_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(path_inequality_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 106) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 114) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(path_dimension_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(path_dimension_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 118) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 127) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(path_domain_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(path_domain_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            File full_inequality_naive = new File("Data_Full_Tree_Variable_Inequalities_Simplex.txt");
            full_inequality_naive.createNewFile();
            File full_inequality_sign_changing = new File("Data_Full_Tree_Variable_Inequalities_Sign_Changing_Simplex.txt");
            full_inequality_sign_changing.createNewFile();
            File full_dimension_naive = new File("Data_Full_Tree_Variable_Dimensions_Simplex.txt");
            full_inequality_naive.createNewFile();
            File full_dimension_sign_changing = new File("Data_Full_Tree_Variable_Dimensions_Sign_Changing_Simplex.txt");
            full_inequality_sign_changing.createNewFile();
            File full_domain_naive = new File("Data_Full_Tree_Variable_Domain_Simplex.txt");
            full_inequality_naive.createNewFile();
            File full_domain_sign_changing = new File("Data_Full_Tree_Variable_Domain_Sign_Changing_Simplex.txt");
            full_inequality_sign_changing.createNewFile();

            File inputFile = new File("tree_construction.txt");
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));
            String line = reader.readLine();
            int lineCount = 1;

            while (line != null && lineCount < 3) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 102) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(full_inequality_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(full_inequality_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 106) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 114) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(full_dimension_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(full_dimension_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount < 118) {
                line = reader.readLine();
                lineCount++;
            }

            while (line != null && lineCount <= 127) {
                String[] parts = line.split(",");
                int num1 = Integer.parseInt(parts[0]);
                double num2 = Long.parseLong(parts[1]) / 1000000000.0;
                double num3 = Long.parseLong(parts[2]) / 1000000000.0;

                FileWriter writer1 = new FileWriter(full_domain_naive, true);
                writer1.write(num1 + " " + num2 + "\n");
                writer1.close();

                FileWriter writer2 = new FileWriter(full_domain_sign_changing, true);
                writer2.write(num1 + " " + num3 + "\n");
                writer2.close();

                line = reader.readLine();
                lineCount++;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
