package me.weiking1021.fcu.assembler.compiler;

import java.util.ArrayList;
import java.util.List;

public class AssemblerObjectCode {

    record TextRecord(int address, byte[] objects) { }

    String programName;
    int startAddress;
    int programByteLength;

    final List<TextRecord> textRecords;

    public AssemblerObjectCode() {

        this.textRecords = new ArrayList<>();
    }

    public void addTextRecord(int address, byte[] objects) {

        textRecords.add(new TextRecord(address, objects));
    }

    @Override
    public String toString() {

        return "AssemblerObjectCode{" +
            "programName='" + programName + '\'' +
            ", startAddress=" + startAddress +
            ", programByteLength=" + programByteLength +
            ", objects=" + textRecords +
            '}';
    }
}
