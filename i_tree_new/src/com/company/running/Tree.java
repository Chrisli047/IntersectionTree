package com.company.running;

public class Tree {

    public static void constructTree(Function[] intersections, Domain domain) {
        // create a table IntersectionTree in MySQL
        NodeRecord.createTable();

        // Construct the root node with the domain, store as the first record


        // compute the intersections, Function intersection[]

        // for each intersection I, do the followings
//        for (Function intersection : intersections) {
//            // get record 0 from the TABLE
//            Record record = getRecordFromTable(0);
//
//            // put the record in a queue Q
//            Queue<Record> Q = new LinkedList<>();
//            Q.add(record);
//
//            while (!Q.isEmpty()) {
//                // fetch a record from the queue
//                Record fetchedRecord = Q.poll();
//
//                // construct a NodeRecord N with the record
//                NodeRecord N = new NodeRecord(fetchedRecord);
//
//                // check if I partition N.domain
//                if (!I.partition(N.domain)) {
//                    // if no, exit
//                    return;
//                } else {
//                    // if yes, two scenarios:
//
//                    // case 1: N is a subdomain node (i.e., leftID/rightID = -1)
//                    if (N.leftID == -1 && N.rightID == -1) {
//                        // store the intersection to N
//                        N.intersection = intersection;
//
//                        // create two child nodes leftNode and rightNode
//                        NodeRecord leftNode = new NodeRecord();
//                        NodeRecord rightNode = new NodeRecord();
//
//                        // set the values for both nodes (e.g., domain)
//                        leftNode.domain = I.leftDomain(N.domain);
//                        rightNode.domain = I.rightDomain(N.domain);
//
//                        // store the two child nodes to the table
//                        storeNodesToTable(leftNode, rightNode);
//
//                        // get the IDs for the two nodes
//                        int leftID = getID(leftNode);
//                        int rightID = getID(rightNode);
//
//                        // update N.leftID and N.rightID with the IDs
//                        N.leftID = leftID;
//                        N.rightID = rightID;
//
//                        // update N to the table
//                        updateRecord(N);
//                    }
//                    // case 2: N is NOT a subdomain node
//                    else {
//                        // retrieve N.left and N.right from the table`
//                        Record leftRecord = getRecord(N.leftID);
//                        Record rightRecord = getRecord(N.rightID);
//
//                        // insert the two records in Q
//                        Q.add(leftRecord);
//                        Q.add(rightRecord);
//                    }
//                }
//            }
//        }
    }

}
