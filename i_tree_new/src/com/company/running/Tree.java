package com.company.running;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

public class Tree {

    public static void constructTree(Function[] intersections, Domain domain, int dimension, String table_name) throws SQLException {
        // create a table IntersectionTree in MySQL
        TreeNode.createTable(table_name);

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

        TreeNode treeNode = new TreeNode(domain, rootPartitionFunction);
        treeNode.insertToMySql(dimension, table_name, false);

        // compute the intersections, Function intersection[]

//         for each intersection I, do the followings
        for (Function intersection : intersections) {
            // get record 0 from the TABLE
            TreeNode record = TreeNode.getRecordByID(1, false, dimension, table_name, false);

            // put the record in a queue Q
            Queue<TreeNode> Q = new LinkedList<TreeNode>();
            Q.add(record);

            while (!Q.isEmpty()) {
                // fetch a record from the queue
                TreeNode fetchedRecord = Q.poll();

                // construct a NodeRecord N with the record
//                NodeRecord N = new NodeRecord(fetchedRecord);

                // check if I partition N.domain
                if (fetchedRecord == null || !Domain.ifPartitionsDomain((Domain) fetchedRecord.domainType,
                        intersection.coefficients)) {
                    // if no, exit
                    continue;
                } else {
                    // if yes, two scenarios:

                    // case 1: N is a subdomain node (i.e., leftID/rightID = -1)
                    if (fetchedRecord.leftID == -1 && fetchedRecord.rightID == -1) {
                        // store the intersection to N
                        fetchedRecord.function = intersection;

                        // partition the domain
                        Domain[] partitionedDomain = Partition.partitionDomain((Domain) fetchedRecord.domainType, intersection);

                        // create two child nodes leftNode and rightNode
                        TreeNode leftNode = new TreeNode(partitionedDomain[0], intersection);
                        TreeNode rightNode = new TreeNode(partitionedDomain[1], intersection);
//
//                        // set the values for both nodes (e.g., domain)
//                        leftNode.domain = I.leftDomain(N.domain);
//                        rightNode.domain = I.rightDomain(N.domain);

                        // store the two child nodes to the table
                        // get the IDs for the two nodes
                        int leftID = leftNode.insertToMySql(dimension, table_name, false);
                        int rightID = rightNode.insertToMySql(dimension, table_name, false);

//                        System.out.println(leftID + " " + rightID);

                        // update N.leftID and N.rightID with the IDs
                        // update N to the table;
                        TreeNode.updateRecord(fetchedRecord.ID, leftID, rightID, fetchedRecord.domainType,
                                dimension, table_name,
                                false);
                    }
                    // case 2: N is NOT a subdomain node
                    else {
                        // retrieve N.left and N.right from the table`
                        TreeNode leftRecord = TreeNode.getRecordByID(fetchedRecord.leftID, false, dimension,
                                table_name, false);
                        TreeNode rightRecord = TreeNode.getRecordByID(fetchedRecord.rightID, false,
                                dimension, table_name, false);

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
                                      TreeNode[] parentWrapper, AtomicInteger intersectionIndex,
                                      ArrayList<double[]> constraintCoefficients, ArrayList<Double> constraintConstants,
                                      SimplexType simplexType, int dimension, String tableName,
                                      AtomicInteger numNodes) throws SQLException {
        for (; intersectionIndex.get() < intersections.length; intersectionIndex.incrementAndGet()) {
            Function intersection = intersections[intersectionIndex.get()];

            DomainSimplex parentDomain = ((DomainSimplex) parentWrapper[0].domainType);
            HashSet<double[]> maxSet = parentDomain.maxSet;
            DomainSimplex leftDomain = new DomainSimplex(parentWrapper[0].function, true, maxSet, null,
                    null);

            constraintCoefficients.add(leftDomain.constraintCoefficients);
            constraintConstants.add(leftDomain.constraintConstant);

            if (constructTreePartitionDomain(simplexType, parentWrapper[0], constraintCoefficients, constraintConstants,
                    intersection, dimension, tableName)) {
                TreeNode leftNode = new TreeNode(parentWrapper[0].ID, leftDomain, intersection,
                        intersectionIndex.get() + 1);

                leftNode.ID = leftNode.insertToMySql(dimension, tableName, false);
                numNodes.incrementAndGet();

                parentWrapper[0].leftID = leftNode.ID;
                TreeNode.updateRecord(parentWrapper[0].ID, leftNode.ID, parentWrapper[0].rightID, parentWrapper[0].domainType,
                        dimension, tableName, false);

                parentWrapper[0] = leftNode;
                ancestorIDs.add(parentWrapper[0].ID);
            } else {
                constraintCoefficients.remove(constraintCoefficients.size() - 1);
                constraintConstants.remove(constraintConstants.size() - 1);
            }
        }
    }

    // Build right node
    private static void buildRightStep(Function[] intersections, ArrayList<Integer> ancestorIDs, TreeNode[] parentWrapper,
                                       AtomicInteger intersectionIndex, ArrayList<double[]> constraintCoefficients,
                                       ArrayList<Double> constraintConstants, int dimension, String tableName, SimplexType simplexType,
                                       AtomicInteger numNodes) throws SQLException {
        for (; intersectionIndex.get() < intersections.length; intersectionIndex.incrementAndGet()) {
            Function intersection = intersections[intersectionIndex.get()];

            DomainSimplex parentDomain = ((DomainSimplex) parentWrapper[0].domainType);
            HashSet<double[]> minSet = parentDomain.minSet;
            DomainSimplex rightDomain = new DomainSimplex(parentWrapper[0].function, false, minSet, null,
                    null);

            constraintCoefficients.add(rightDomain.constraintCoefficients);
            constraintConstants.add(rightDomain.constraintConstant);

            if (constructTreePartitionDomain(simplexType, parentWrapper[0], constraintCoefficients, constraintConstants,
                    intersection, dimension, tableName)) {
                TreeNode rightNode = new TreeNode(parentWrapper[0].ID, rightDomain, intersection,
                        intersectionIndex.get() + 1);

                rightNode.ID = rightNode.insertToMySql(dimension, tableName, false);
                numNodes.incrementAndGet();

                parentWrapper[0].rightID = rightNode.ID;
                TreeNode.updateRecord(parentWrapper[0].ID, parentWrapper[0].leftID, rightNode.ID, parentWrapper[0].domainType,
                        dimension, tableName, false);

                parentWrapper[0] = rightNode;
                ancestorIDs.add(parentWrapper[0].ID);

                return;
            } else {
                constraintCoefficients.remove(constraintCoefficients.size() - 1);
                constraintConstants.remove(constraintConstants.size() - 1);
            }
        }
    }

    // Step back to parent
    private static void stepBack(ArrayList<Integer> ancestorIDs, TreeNode[] parentWrapper,
                                 AtomicInteger intersectionIndex, ArrayList<double[]> constraintCoefficients,
                                 ArrayList<Double> constraintConstants, int dimension, String tableName) throws SQLException {
        ancestorIDs.remove(ancestorIDs.size() - 1);
        parentWrapper[0] = TreeNode.getRecordByID(ancestorIDs.get(ancestorIDs.size() - 1), true, dimension,
                tableName, false);
        intersectionIndex.set(parentWrapper[0].intersectionIndex);
        constraintCoefficients.remove(constraintCoefficients.size() - 1);
        constraintConstants.remove(constraintConstants.size() - 1);
    }

    // Returns true if node inequality accepts point
    // Not aware of ancestor node inequalities
    private static boolean nodeAcceptsPoint(TreeNode node, double[] point) {
        DomainSimplex domain = (DomainSimplex) node.domainType;

        double pointValue = 0;
        for (int i = 0; i < domain.constraintCoefficients.length; i++) {
            pointValue += point[i] * domain.constraintCoefficients[i];
        }

        return pointValue < domain.constraintConstant;
    }

    // Trickle points down from last node toward first node
    private static void checkPoints(ArrayList<TreeNode> nodes, HashSet<double[]> maxSet, HashSet<double[]> minSet,
                                    int dimension, String tableName) throws SQLException {
        TreeNode firstNode = nodes.get(0);
        TreeNode lastNode = nodes.get(nodes.size() - 1);

        // At index i store right child points for ith ancestor
        // Not updated immediately to reduce number of database calls
        ArrayList<double[]>[] rightChildPoints = new ArrayList[nodes.size()];

        for (double[] point : ((DomainSimplex) lastNode.domainType).unknownSet) {
            ((DomainSimplex) lastNode.domainType).unknownSet.remove(point);

            // Trickle point down toward leaf node
            boolean trickledToBottom = true;
            for (int i = nodes.size() - 1; i > 0; i--) {
                TreeNode node = nodes.get(i);
                TreeNode childNode = nodes.get(i - 1);
                boolean rightChild = node.rightID == childNode.ID;

                // Halt trickle down if wrong direction
                if (!(nodeAcceptsPoint(childNode, point) && rightChild)) {
                    // Don't save left points (tree constructed left to right)
                    if (!rightChild) {
                        if (rightChildPoints[i] == null) {
                            rightChildPoints[i] = new ArrayList<>();
                        }

                        rightChildPoints[i].add(point);
                    }

                    trickledToBottom = false;
                    break;
                }
            }

            if (trickledToBottom) {
                if (nodeAcceptsPoint(firstNode, point)) {
                    minSet.add(point);
                } else {
                    maxSet.add(point);
                }

                // partition points found
                if (!maxSet.isEmpty() && !minSet.isEmpty()) {
                    // do not return because we need to update saved points
                    break;
                }
            }
        }

        // lastNode.d.unknownSet has likely been shrunk
        TreeNode.updateRecord(lastNode.ID, lastNode.leftID, lastNode.rightID, lastNode.domainType, dimension,
                tableName, true);

        // add all remembered points to the right children of the respective nodes
        for (int i = 0; i < rightChildPoints.length; i++) {
            ArrayList<double[]> pointSet = rightChildPoints[i];
            if (pointSet != null) {
                int parentNodeID = nodes.get(i).ID;
                TreeNode rightChild = TreeNode.getRecordByID(parentNodeID, true, dimension, tableName, true);
                ((DomainSimplex) rightChild.domainType).unknownSet.addAll(pointSet);
                TreeNode.updateRecord(rightChild.ID, rightChild.leftID, rightChild.rightID, rightChild.domainType, dimension,
                        tableName, true);
            }
        }
    }

    private static boolean constructTreePartitionDomain(SimplexType simplexType,
                                                        TreeNode node,
                                                        ArrayList<double[]> constraintCoefficients,
                                                        ArrayList<Double> constraintConstants,
                                                        Function function,
                                                        int dimension,
                                                        String tableName) throws SQLException {
        HashSet<double[]> maxSet = new HashSet<>();
        HashSet<double[]> minSet = new HashSet<>();
        boolean maxFound = false;
        boolean minFound = false;

        if (simplexType == SimplexType.POINT_REMEMBERING_PERMANENT_SIGN_CHANGING_SIMPLEX) {
            ArrayList<TreeNode> nodes = new ArrayList<>();
            nodes.add(node);

            while (true) {
                checkPoints(nodes, maxSet, minSet, dimension, tableName);
                maxFound = !maxSet.isEmpty();
                minFound = !minSet.isEmpty();
                if (maxFound && minFound) {
                    return true;
                }

                if (node.parentID == -1) {
                    break;
                }

                node = TreeNode.getRecordByID(node.parentID, true, dimension, tableName, true);
                nodes.add(node);
            }
        } else if (simplexType == SimplexType.POINT_REMEMBERING_LOCAL_SIGN_CHANGING_SIMPLEX) {
            // TODO: same as permanent memorization, but all points stored in array (for get and for put)
        }
        return DomainSimplex.ifPartitionsDomain(constraintCoefficients, constraintConstants, function,
                // TODO: do maxSet and minSet persist after return?
                simplexType, dimension, maxSet, minSet, maxFound, minFound);
    }

    // Pass constraintCoefficients and constraintConstants with initial domain
    public static int constructTreeSimplex(Function[] intersections,
                                           DomainSimplex domain,
                                           ArrayList<double[]> constraintCoefficients,
                                           ArrayList<Double> constraintConstants,
                                           SimplexType simplexType,
                                           int dimension, String tableName) throws SQLException {
        AtomicInteger numNodes = new AtomicInteger(0); // num nodes in tree for testing
        AtomicInteger intersectionIndex = new AtomicInteger(0); // identifies current intersection
        boolean storePoints = false;

        if (simplexType == SimplexType.POINT_REMEMBERING_PERMANENT_SIGN_CHANGING_SIMPLEX
                || simplexType == SimplexType.POINT_REMEMBERING_LOCAL_SIGN_CHANGING_SIMPLEX) {
            storePoints = true;
            domain.maxSet = new HashSet<>();
            domain.minSet = new HashSet<>();
        }

        TreeNode.createTable(tableName);
        Function rootPartitionFunction = null;
        for (; intersectionIndex.get() < intersections.length; intersectionIndex.incrementAndGet()) {
            Function function = intersections[intersectionIndex.get()];

            if (DomainSimplex.ifPartitionsDomain(constraintCoefficients, constraintConstants, function, simplexType,
                    dimension, domain.maxSet, domain.minSet, false, false)) {
                rootPartitionFunction = function;
                intersectionIndex.incrementAndGet();
                break;
            }
        }

        if (rootPartitionFunction == null) {
            return numNodes.get();
        }

        TreeNode root = new TreeNode(-1, domain, rootPartitionFunction, intersectionIndex.get());
        root.ID = root.insertToMySql(dimension, tableName, storePoints);
        numNodes.incrementAndGet();

        ArrayList<Integer> ancestorIDs = new ArrayList<>();
        TreeNode[] parentWrapper = new TreeNode[]{root};
        ancestorIDs.add(parentWrapper[0].ID);

        while (true) {
            buildLeftFull(intersections, ancestorIDs, parentWrapper, intersectionIndex, constraintCoefficients,
                    constraintConstants, simplexType, dimension, tableName, numNodes);
            intersectionIndex.set(parentWrapper[0].intersectionIndex);

            TreeNode lastNode = parentWrapper[0];
            buildRightStep(intersections, ancestorIDs, parentWrapper, intersectionIndex, constraintCoefficients,
                    constraintConstants, dimension, tableName, simplexType, numNodes);
            TreeNode currentNode = parentWrapper[0];

            while (lastNode.ID == currentNode.ID) {
                if (parentWrapper[0].ID == root.ID) {
                    return numNodes.get();
                }

                stepBack(ancestorIDs, parentWrapper, intersectionIndex, constraintCoefficients, constraintConstants, dimension,
                        tableName);
                while (parentWrapper[0].rightID == currentNode.ID) {
                    if (parentWrapper[0].ID == root.ID) {
                        return numNodes.get();
                    }

                    currentNode = parentWrapper[0];
                    stepBack(ancestorIDs, parentWrapper, intersectionIndex, constraintCoefficients, constraintConstants, dimension,
                            tableName);
                }

                lastNode = parentWrapper[0];
                buildRightStep(intersections, ancestorIDs, parentWrapper, intersectionIndex, constraintCoefficients,
                        constraintConstants, dimension, tableName, simplexType, numNodes);
                currentNode = parentWrapper[0];
            }
        }
    }

    // Constructs path down tree
    public static int constructTreeSegmentSimplex(Function[] intersections,
                                                   ArrayList<double[]> allConstraintCoefficients,
                                                   ArrayList<Double> allConstraintConstants,
                                                   SimplexType simplexType,
                                                   int dimension,
                                                   int boundary_length) {
        // TODO: local point memorization: same as full tree but cascade forgets
        int numPartitions = 0;
        for (Function intersection : intersections) {
            if (DomainSimplex.ifPartitionsDomain(allConstraintCoefficients, allConstraintConstants, intersection,
                    simplexType, dimension, null, null, false, false)) {
                numPartitions++;
                double[] coefficients = new double[intersection.coefficients.length - 1];
                System.arraycopy(intersection.coefficients, 0, coefficients, 0, coefficients.length);
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
        return numPartitions;
    }

}
