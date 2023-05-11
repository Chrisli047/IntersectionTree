package com.company.running;

public interface DomainType {
    public byte[] toByte(int dimension, boolean storePoints);

    public static DomainType toDomain(byte[] bytes, int dimension, boolean storedPoints) {
        return null;
    }

    public void printDomain();
}
