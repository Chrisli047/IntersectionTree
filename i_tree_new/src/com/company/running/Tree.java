package com.company.running;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class Tree {

    public static void constructTree(Function[] intersections, Domain domain, int dimension, String table_name) {
        // create a table IntersectionTree in MySQL
        NodeRecord.createTable(table_name);

        // Construct the root node with the domain, store as the first record
        Function rootPartitionFunction = null;
        for (Function f: intersections) {
            if (Domain.ifPartitionsDomain(domain, f.coefficients)) {
                rootPartitionFunction = f;
//                System.out.println("yes");
                break;
            } else {
//                System.out.println("no");
                continue;
            }
        }

        // none of the functions partition the domain
        if (rootPartitionFunction == null) {
            return;
        }

        NodeRecord nodeRecord = new NodeRecord(domain, rootPartitionFunction, -1, -1);
        nodeRecord.insertToMySql(dimension, table_name);

        // compute the intersections, Function intersection[]

//         for each intersection I, do the followings
        for (Function intersection : intersections) {
            // get record 0 from the TABLE
            NodeRecord record = NodeRecord.getRecordById(1, false, dimension, table_name);

            // put the record in a queue Q
            Queue<NodeRecord> Q = new LinkedList<NodeRecord>();
            Q.add(record);

            while (!Q.isEmpty()) {
                // fetch a record from the queue
                NodeRecord fetchedRecord = Q.poll();

                // construct a NodeRecord N with the record
//                NodeRecord N = new NodeRecord(fetchedRecord);

                // check if I partition N.domain
                if (fetchedRecord == null || !Domain.ifPartitionsDomain((Domain) fetchedRecord.d,
                        intersection.coefficients)) {
                    // if no, exit
                    continue;
                } else {
                    // if yes, two scenarios:

                    // case 1: N is a subdomain node (i.e., leftID/rightID = -1)
                    if (fetchedRecord.leftID == -1 && fetchedRecord.rightID == -1) {
                        // store the intersection to N
                        fetchedRecord.f = intersection;

                        // partition the domain
                        Domain[] partitionedDomain = Partition.partitionDomain((Domain) fetchedRecord.d, intersection);

                        // create two child nodes leftNode and rightNode
                        NodeRecord leftNode = new NodeRecord(partitionedDomain[0], intersection, -1, -1);
                        NodeRecord rightNode = new NodeRecord(partitionedDomain[1], intersection, -1, -1);
//
//                        // set the values for both nodes (e.g., domain)
//                        leftNode.domain = I.leftDomain(N.domain);
//                        rightNode.domain = I.rightDomain(N.domain);

                        // store the two child nodes to the table
                        // get the IDs for the two nodes
                        int leftID = leftNode.insertToMySql(dimension, table_name);
                        int rightID = rightNode.insertToMySql(dimension, table_name);

//                        System.out.println(leftID + " " + rightID);

                        // update N.leftID and N.rightID with the IDs
                        // update N to the table;
                        NodeRecord.updateRecord(fetchedRecord.ID, leftID, rightID, false, dimension, table_name);
                    }
                    // case 2: N is NOT a subdomain node
                    else {
                        // retrieve N.left and N.right from the table`
                        NodeRecord leftRecord = NodeRecord.getRecordById(fetchedRecord.leftID, false, dimension,
                                table_name);
                        NodeRecord rightRecord = NodeRecord.getRecordById(fetchedRecord.rightID, false,
                                dimension, table_name);

                        // insert the two records in Q
                        Q.add(leftRecord);
                        Q.add(rightRecord);
                    }
                }
            }
        }
    }

    // Build leftmost part of subtree from node
    private static void buildLeftFull(Function[] intersections, ArrayList<Integer> ancestorIDs,
                                      NodeRecord[] parentWrapper, AtomicInteger intersectionIndex,
                                      ArrayList<double[]> constraintCoefficients, ArrayList<Double> constraintConstants,
                                      SimplexType simplexType, int dimension, String tableName,
                                      AtomicInteger numNodes) {
        for (; intersectionIndex.get() < intersections.length; intersectionIndex.incrementAndGet()) {
            Function intersection = intersections[intersectionIndex.get()];
            if (DomainSimplex.ifPartitionsDomain(constraintCoefficients, constraintConstants, intersection, simplexType,
                    dimension)) {
                DomainSimplex leftDomain = new DomainSimplex(intersection, true);
                DomainSimplex rightDomain = new DomainSimplex(intersection, false);

                NodeRecord leftNode = new NodeRecord(leftDomain, intersection, -1, -1);
                NodeRecord rightNode = new NodeRecord(rightDomain, intersection, -1, -1);

                leftNode.ID = leftNode.insertToMySql(dimension, tableName);
                numNodes.incrementAndGet();
                rightNode.ID = rightNode.insertToMySql(dimension, tableName);
                numNodes.incrementAndGet();

                parentWrapper[0].leftID = leftNode.ID;
                parentWrapper[0].rightID = rightNode.ID;
                NodeRecord.updateRecord(parentWrapper[0].ID, leftNode.ID, rightNode.ID, true, dimension, tableName);

                parentWrapper[0] = leftNode;
                ancestorIDs.add(parentWrapper[0].ID);
                DomainSimplex parentDomain = (DomainSimplex) parentWrapper[0].d;
                constraintCoefficients.add(parentDomain.constraintCoefficients);
                constraintConstants.add(parentDomain.constraintConstant);
            }
        }
    }

    // Build right node
    private static void stepRight(Function[] intersections, ArrayList<Integer> ancestorIDs,
                                  NodeRecord[] parentWrapper, AtomicInteger intersectionIndex,
                                  ArrayList<double[]> constraintCoefficients,
                                  ArrayList<Double> constraintConstants, SimplexType simplexType, int dimension,
                                  String tableName, AtomicInteger numNodes) {
        Function intersection = intersections[intersectionIndex.get()];
        if (DomainSimplex.ifPartitionsDomain(constraintCoefficients, constraintConstants, intersection, simplexType,
                dimension)) {
            intersectionIndex.incrementAndGet();
            parentWrapper[0] = NodeRecord.getRecordById(parentWrapper[0].rightID, true, dimension, tableName);
            ancestorIDs.add(parentWrapper[0].ID);
            DomainSimplex parentDomain = (DomainSimplex) parentWrapper[0].d;
            constraintCoefficients.add(parentDomain.constraintCoefficients);
            constraintConstants.add(parentDomain.constraintConstant);
        }
    }

    // Step back to parent
    private static void stepBack(ArrayList<Integer> ancestorIDs, NodeRecord[] parentWrapper,
                                 AtomicInteger intersectionIndex, ArrayList<double[]> constraintCoefficients,
                                 ArrayList<Double> constraintConstants, int dimension, String tableName) {
        ancestorIDs.remove(ancestorIDs.size() - 1);
        parentWrapper[0] = NodeRecord.getRecordById(ancestorIDs.get(ancestorIDs.size() - 1), true, dimension,
                tableName);
        intersectionIndex.decrementAndGet();
        constraintCoefficients.remove(constraintCoefficients.size() - 1);
        constraintConstants.remove(constraintConstants.size() - 1);
    }

    // Pass constraintCoefficients and constraintConstants with initial domain
    public static int constructTreeSimplex(Function[] intersections,
                                           DomainSimplex domain,
                                           ArrayList<double[]> constraintCoefficients,
                                           ArrayList<Double> constraintConstants,
                                           SimplexType simplexType,
                                           int dimension, String tableName) {
        AtomicInteger numNodes = new AtomicInteger(0); // num nodes in tree for testing
        AtomicInteger intersectionIndex = new AtomicInteger(0); // identifies current intersection

        NodeRecord.createTable(tableName);
        Function rootPartitionFunction = null;
        for (; intersectionIndex.get() < intersections.length; intersectionIndex.incrementAndGet()) {
            Function function = intersections[intersectionIndex.get()];
            if (DomainSimplex.ifPartitionsDomain(constraintCoefficients, constraintConstants, function,
                    simplexType, dimension)) {
                rootPartitionFunction = function;
                intersectionIndex.get();
                break;
            }
        }

        if (rootPartitionFunction == null) {
            return numNodes.get();
        }

        NodeRecord root = new NodeRecord(domain, rootPartitionFunction, -1, -1);
        root.ID = root.insertToMySql(dimension, tableName);
        numNodes.incrementAndGet();

        ArrayList<Integer> ancestorIDs = new ArrayList<>();
        NodeRecord[] parentWrapper = new NodeRecord[]{root};
        ancestorIDs.add(parentWrapper[0].ID);
        NodeRecord lastNode = parentWrapper[0];

        while (parentWrapper[0] != root || parentWrapper[0].rightID != lastNode.ID) {
            // TODO: stepBack assumes 1 node = 1 intersection, it might be more for skipped ones
            //  don't change intersectionIndex when stepping back
            //  when all step backs completed, take intersection index using number in root
            //  but what if the number shows up multiple times?
            //  SOLUTION: store intersection number in nodeRecord in MySQL so we get whatever we need to use after being
            //    done with all stepping back
            buildLeftFull(intersections, ancestorIDs, parentWrapper, intersectionIndex, constraintCoefficients,
                    constraintConstants, simplexType, dimension, tableName, numNodes);
            lastNode = parentWrapper[0];
            stepBack(ancestorIDs, parentWrapper, intersectionIndex, constraintCoefficients, constraintConstants, dimension,
                    tableName);
            while (parentWrapper[0].rightID == lastNode.ID) {
                lastNode = parentWrapper[0];
                stepBack(ancestorIDs, parentWrapper, intersectionIndex, constraintCoefficients, constraintConstants, dimension,
                        tableName);
            }
            stepRight(intersections, ancestorIDs, parentWrapper, intersectionIndex, constraintCoefficients,
                    constraintConstants, simplexType, dimension, tableName, numNodes);
        }

        return numNodes.incrementAndGet();
    }

    // Constructs path down tree
    public static void constructTreeSegmentSimplex(Function[] intersections,
                                                   ArrayList<double[]> allConstraintCoefficients,
                                                   ArrayList<Double> allConstraintConstants,
                                                   SimplexType simplexType,
                                                   int dimension,
                                                   int boundary_length) {
        for (Function intersection : intersections) {
            if (DomainSimplex.ifPartitionsDomain(allConstraintCoefficients, allConstraintConstants, intersection,
                    simplexType, dimension)) {
                double[] coefficients = new double[intersection.coefficients.length - 1];
                System.arraycopy(intersection.coefficients, 0, coefficients, 0,
                        coefficients.length - 1);
                double constant = intersection.coefficients[intersection.coefficients.length - 1];

                // If inequality does not accept central point, flip it in order to maximize likelihood of future
                // partitions.
                double point_value = 0;
                for (double coefficient : coefficients) {
                    // point value = boundary_length/2
                    point_value += coefficient * boundary_length / 2;
                }
                if (point_value > constant) {
                    for (int i = 0; i < coefficients.length; i++) {
                        coefficients[i] *= -1;
                    }
                    constant *= -1;
                }

                // add constraints to define subdomain
                allConstraintCoefficients.add(coefficients);
                allConstraintConstants.add(constant);
            }
        }
    }

}
