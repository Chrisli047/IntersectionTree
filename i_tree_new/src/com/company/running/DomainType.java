package com.company.running;

public interface DomainType {
    public byte[] toByte();

    public static DomainType toDomain(byte[] bytes) {
        return null;
    }

    public void printDomain();
}
