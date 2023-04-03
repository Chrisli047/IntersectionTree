package com.company.running;

public interface DomainType {
    public byte[] toByte(int dimension);

    public static DomainType toDomain(byte[] bytes, int dimension) {
        return null;
    }

    public void printDomain();
}
