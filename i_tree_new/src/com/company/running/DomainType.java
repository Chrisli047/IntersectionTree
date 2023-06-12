package com.company.running;

// TODO: domain is not a good name; parametric equation stores domain, simplex stores intersection ID and maybe min/max sets
public interface DomainType {
    public byte[] toByte(int dimension, boolean storePoints);

    public static DomainType toDomain(byte[] bytes, int dimension, boolean storedPoints) {
        return null;
    }

    public void printDomain();
}
