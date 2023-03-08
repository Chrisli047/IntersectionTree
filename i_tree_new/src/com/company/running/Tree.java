package com.company.running;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Tree {

    public static void constructTree(Function[] intersections, Domain domain) {
        // create a table IntersectionTree in MySQL
        NodeRecord.createTable();

        // Construct the root node with the domain, store as the first record
        Function rootPartitionFunction = null;
        for (Function f:
             intersections) {
            if (Domain.ifPartitionsDomain(domain, f.coefficients)) {
                rootPartitionFunction = f;
                System.out.println("yes");
                break;
            } else {
                System.out.println("no");
                continue;
            }
        }

        NodeRecord nodeRecord = new NodeRecord(domain, rootPartitionFunction, -1, -1);
        nodeRecord.insertToMySql();

        // compute the intersections, Function intersection[]

//         for each intersection I, do the followings
        for (Function intersection : intersections) {
            // get record 0 from the TABLE
            NodeRecord record = NodeRecord.getRecordById(1);

            // put the record in a queue Q
            Queue<NodeRecord> Q = new LinkedList<NodeRecord>();
            Q.add(record);

            while (!Q.isEmpty()) {
                // fetch a record from the queue
                NodeRecord fetchedRecord = Q.poll();

                // construct a NodeRecord N with the record
//                NodeRecord N = new NodeRecord(fetchedRecord);

                // check if I partition N.domain
                if (fetchedRecord == null || !Domain.ifPartitionsDomain((Domain) fetchedRecord.d, intersection.coefficients)) {
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
                        int leftID = leftNode.insertToMySql();
                        int rightID = rightNode.insertToMySql();

                        System.out.println(leftID + " " + rightID);

                        // update N.leftID and N.rightID with the IDs
                        // update N to the table;
                        NodeRecord.updateRecord(fetchedRecord.ID, leftID, rightID);
                    }
                    // case 2: N is NOT a subdomain node
                    else {
                        // retrieve N.left and N.right from the table`
                        NodeRecord leftRecord = NodeRecord.getRecordById(fetchedRecord.leftID);
                        NodeRecord rightRecord = NodeRecord.getRecordById(fetchedRecord.rightID);

                        // insert the two records in Q
                        Q.add(leftRecord);
                        Q.add(rightRecord);
                    }
                }
            }
        }
    }

    public static void constructTreeSimplex(Function[] intersections, DomainSignChangingSimplex domain) {

        // create a table IntersectionTree in MySQL
        NodeRecord.createTable();

        // Construct the root node with the domain, store as the first record
        Function rootPartitionFunction = null;
        for (Function f:
                intersections) {
            if (DomainSignChangingSimplex.ifPartitionsDomain(new ArrayList<>(), new ArrayList<>(), f)) {
                rootPartitionFunction = f;
                System.out.println("yes");
                break;
            } else {
                System.out.println("no");
                continue;
            }
        }

        NodeRecord nodeRecord = new NodeRecord(domain, rootPartitionFunction, -1, -1);
        nodeRecord.insertToMySql();

        // compute the intersections, Function intersection[]

//         for each intersection I, do the followings
        for (Function intersection : intersections) {
            // get record 0 from the TABLE
            NodeRecord record = NodeRecord.getRecordById(1);

            // put the record in a queue Q
            Queue<NodeRecord> Q = new LinkedList<NodeRecord>();
            Q.add(record);

            // constraints defining subdomain
            ArrayList<double[]> allConstraintCoefficients = new ArrayList<>();
            ArrayList<Double> allConstraintConstants = new ArrayList<>();

            while (!Q.isEmpty()) {
                // fetch a record from the queue
                NodeRecord fetchedRecord = Q.poll();

                // add constraints to define subdomain
                allConstraintCoefficients.add(((DomainSignChangingSimplex) fetchedRecord.d).constraintCoefficients);
                allConstraintConstants.add(((DomainSignChangingSimplex) fetchedRecord.d).constraintConstant);

                // construct a NodeRecord N with the record
//                NodeRecord N = new NodeRecord(fetchedRecord);

                // check if I partition N.domain
                if (!DomainSignChangingSimplex.ifPartitionsDomain(allConstraintCoefficients, allConstraintConstants,
                        intersection)) {
                    // if no, exit
                    continue;
                } else {
                    // if yes, two scenarios:

                    // case 1: N is a subdomain node (i.e., leftID/rightID = -1)
                    if (fetchedRecord.leftID == -1 && fetchedRecord.rightID == -1) {
                        // store the intersection to N
                        fetchedRecord.f = intersection;

                        // partition the domain
                        DomainSignChangingSimplex leftDomain = new DomainSignChangingSimplex(intersection, true);
                        DomainSignChangingSimplex rightDomain = new DomainSignChangingSimplex(intersection, false);

                        // create two child nodes leftNode and rightNode
                        NodeRecord leftNode = new NodeRecord(leftDomain, intersection, -1, -1);
                        NodeRecord rightNode = new NodeRecord(rightDomain, intersection, -1, -1);
//
//                        // set the values for both nodes (e.g., domain)
//                        leftNode.domain = I.leftDomain(N.domain);
//                        rightNode.domain = I.rightDomain(N.domain);

                        // store the two child nodes to the table
                        // get the IDs for the two nodes
                        int leftID = leftNode.insertToMySql();
                        int rightID = rightNode.insertToMySql();

                        System.out.println(leftID + " " + rightID);

                        // update N.leftID and N.rightID with the IDs
                        // update N to the table;
                        NodeRecord.updateRecord(fetchedRecord.ID, leftID, rightID);
                    }
                    // case 2: N is NOT a subdomain node
                    else {
                        // retrieve N.left and N.right from the table`
                        NodeRecord leftRecord = NodeRecord.getRecordById(fetchedRecord.leftID);
                        NodeRecord rightRecord = NodeRecord.getRecordById(fetchedRecord.rightID);

                        // insert the two records in Q
                        Q.add(leftRecord);
                        Q.add(rightRecord);
                    }
                }
            }
        }
    }

}
