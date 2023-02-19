package com.company.running;

public class Node {
    Domain domain;
    Function f;
    Node right;
    Node left;

    public Node (Domain domain, Function f) {
        this.domain = domain;
        this.f = f;
        this.right = null;
        this.left = null;
    }

}