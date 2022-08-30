package me.weiking1021.fcu.assembler.compiler;

import me.weiking1021.fcu.assembler.compiler.exception.AssemblerInstructionException;

import java.util.*;

import static me.weiking1021.fcu.assembler.compiler.AssemblerFormat.*;

public class AssemblerCompiler {

    private enum CompilerCommand {

        START {

            @Override
            void preProcess(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                if (compiler.locationCounter >= 0)
                    throw new AssemblerInstructionException("Duplicate start compiler command");

                compiler.base = HexFormat.fromHexDigits(instruction.operand);
                compiler.locationCounter = compiler.base;
                compiler.symbolMap.put(instruction.label, compiler.base);
            }

            @Override
            void process(AssemblerCompiler compiler, AssemblerInstruction instruction, int address, AssemblerObjectCode objectCode) {

                objectCode.programName = instruction.label;
                objectCode.programByteLength = compiler.locationCounter - compiler.startLocation;
            }
        },
        END {

            @Override
            void preProcess(AssemblerCompiler compiler, AssemblerInstruction instruction) {

            }
            @Override
            void process(AssemblerCompiler compiler, AssemblerInstruction instruction, int address, AssemblerObjectCode objectCode) {

                objectCode.programByteLength = compiler.locationCounter - compiler.startLocation;
            }
        },
        BASE {

            @Override
            void preProcess(AssemblerCompiler compiler, AssemblerInstruction instruction) {

            }
            @Override
            void process(AssemblerCompiler compiler, AssemblerInstruction instruction, int address, AssemblerObjectCode objectCode) {

                try {

                    compiler.base = HexFormat.fromHexDigits(instruction.operand);
                }
                catch (Exception e) {

                    compiler.base = compiler.symbolMap.get(instruction.operand);
                }
            }
        },
        BYTE {

            @Override
            void preProcess(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                AssemblerOperand operand = AssemblerOperand.parse(instruction.operand);

                if (operand.value.startsWith("C'") && operand.value.endsWith("'"))
                    compiler.locationCounter += (operand.value.length() - 3) * 2;

                if (operand.value.startsWith("X'") && operand.value.endsWith("'"))
                    compiler.locationCounter += (operand.value.length() + 1) / 2;
            }

            @Override
            void process(AssemblerCompiler compiler, AssemblerInstruction instruction, int address, AssemblerObjectCode objectCode) {

                AssemblerOperand operand = AssemblerOperand.parse(instruction.operand);

                byte[] byteValue = null;

                if (operand.value.startsWith("C'") && operand.value.endsWith("'")) {

                    String value = operand.value.substring(2, operand.value.length() - 1);
                    int length = value.length();

                    byteValue = ByteUtil.fromAscii(value);
                }
                else if (operand.value.startsWith("X'") && operand.value.endsWith("'")) {

                    String value = operand.value.substring(2, operand.value.length() - 1);
                    int length = (value.length() + 1) / 2;

                    byteValue = ByteUtil.toByteArray(HexFormat.fromHexDigits(value), length);
                }

                objectCode.addTextRecord(address, byteValue);
            }
        },
        RESW {

            @Override
            void preProcess(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                compiler.locationCounter += Integer.parseInt(instruction.operand) * 3;
            }
            @Override
            void process(AssemblerCompiler compiler, AssemblerInstruction instruction, int address, AssemblerObjectCode objectCode) {

//                objectCode.addTextRecord(address, new byte[Integer.parseInt(instruction.operand) * 3]);
            }
        },
        RESB {

            @Override
            void preProcess(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                compiler.locationCounter += Integer.parseInt(instruction.operand);
            }
            @Override
            void process(AssemblerCompiler compiler, AssemblerInstruction instruction, int address, AssemblerObjectCode objectCode) {

//                objectCode.addTextRecord(address, new byte[Integer.parseInt(instruction.operand)]);
            }
        },
        ;

        abstract void preProcess(AssemblerCompiler compiler, AssemblerInstruction instruction);
        abstract void process(AssemblerCompiler compiler, AssemblerInstruction instruction, int address, AssemblerObjectCode objectCode);

        static CompilerCommand get(String command) {

            try { return valueOf(command); }
            catch (Exception e) { return null; }
        }
    }

    private List<AssemblerInstruction> loadedInstructions;
    private Map<String, Integer> symbolMap;
    private List<Map.Entry<AssemblerInstruction, Integer>> instructionAddressMap;

    private int locationCounter;
    private int startLocation;
    private int base;

    public AssemblerCompiler() {

        this.locationCounter = -1;
        this.loadedInstructions = new LinkedList<>();
        this.instructionAddressMap = new LinkedList<>();
        this.symbolMap = new HashMap<>();
        this.startLocation = -1;
    }

    public AssemblerInstruction writeSourceCode(String nextLine) {

        AssemblerInstruction instruction = AssemblerInstruction.parse(nextLine);

        if (instruction == null)
            return null;

        System.out.println(instruction);

        loadedInstructions.add(instruction);

        return instruction;
    }

    public AssemblerObjectCode compile() {

        System.out.println("---- Start compile (PreProcess) ----");

        for (AssemblerInstruction instruction : loadedInstructions) {

            instructionAddressMap.add(Map.entry(instruction, locationCounter));

            // put symbol label location and check containing
            if (instruction.label != null && symbolMap.put(instruction.label, locationCounter) != null)
                throw new AssemblerInstructionException("Duplicate instruction label '" + instruction.label + "'");

            CompilerCommand compilerCommand = CompilerCommand.get(instruction.opcode);

            // check first command
            if (locationCounter < 0 && compilerCommand != CompilerCommand.START)
                throw new AssemblerInstructionException("No header instruction");

            if (compilerCommand != null) {

                compilerCommand.preProcess(this, instruction);

                continue;
            }

            System.out.println("[" + Integer.toHexString(locationCounter) + "] " + instruction + " --> " + instruction.parseFormatType());

            locationCounter += instruction.parseFormatType().bytes;
        }

        System.out.println("---- Symbol Map ----");
        symbolMap.forEach((k, v) -> System.out.printf("%8s\t%s\r\n", k, Integer.toHexString(v)));

        System.out.println("---- Start compile (Process) ----");

        AssemblerObjectCode objectCode = new AssemblerObjectCode();

        for (Map.Entry<AssemblerInstruction, Integer> entry : instructionAddressMap) {

            AssemblerInstruction instruction = entry.getKey();
            int address = entry.getValue();

            CompilerCommand compilerCommand = CompilerCommand.get(instruction.opcode);

            if (compilerCommand != null) {

                compilerCommand.process(this, instruction, address, objectCode);

                continue;
            }

            AssemblerFormat format = instruction.parseFormatType();
            String opcode = format == FORMAT_4 ? instruction.opcode.substring(1) : instruction.opcode;
            AssemblerInstructionSchema schema = AssemblerInstructionSetTable.get(opcode);

            if (schema == null)
                throw new AssemblerInstructionException("Can't find opcode schema '" + instruction.opcode);

            int programCounter = address + format.bytes;

            byte[] resultBytes = null;

            if (format == FORMAT_1) {

                resultBytes = AssemblerInstruction.genFormat1Code(schema.opcode);
            }
            else if (format == FORMAT_2) {

                AssemblerOperand operand = AssemblerOperand.parse(instruction.operand);

                String[] r = operand.value.split(",");

                AssemblerRegister r1 = AssemblerRegister.valueOf(r[0]);
                AssemblerRegister r2 = r.length == 2 ? AssemblerRegister.valueOf(r[1]) : null;

                short adrR1 = r1.address;
                short adrR2 = (r2 == null ? 0 : r2.address);

                resultBytes = AssemblerInstruction.genFormat2Code(schema.opcode, adrR1, adrR2);
            }
            else if (format == FORMAT_3) {

                AssemblerOperand operand = AssemblerOperand.parse(instruction.operand);

                boolean n = operand.indirectAddressing;
                boolean i = operand.immediateAddressing;
                boolean x = operand.indexedAddressing;
                boolean b = false;
                boolean p = false;
                boolean e = false;
                int ta = 0;

                try {

                    ta = Integer.parseInt(operand.value);
                }
                catch (Exception ex) {

                    int symbolAddress = symbolMap.get(operand.value);

                    if (symbolAddress - programCounter > 2048) {

                        b = true;
                        ta = symbolAddress - base;
                    }
                    else {

                        p = true;
                        ta = symbolAddress - programCounter;
                    }
                }

                resultBytes = AssemblerInstruction.genFormat3Code(schema.opcode, n, i, x, b, p, e, ta);
            }
            else if (format == FORMAT_4) {

                AssemblerOperand operand = AssemblerOperand.parse(instruction.operand);

                boolean n = operand.indirectAddressing;
                boolean i = operand.immediateAddressing;
                boolean x = operand.indexedAddressing;
                boolean b = false;
                boolean p = false;
                boolean e = false;
                int ta = 0;

                try {

                    ta = Integer.parseInt(operand.value);
                }
                catch (Exception ex) {

                    ta = symbolMap.get(operand.value);
                }

                resultBytes = AssemblerInstruction.genFormat4Code(schema.opcode, n, i, x, b, p, e, ta);
            }

            System.out.println("Compile " + instruction + " To " + ByteUtil.toHexText(resultBytes));

            objectCode.addTextRecord(address, resultBytes);
        }

        return objectCode;
    }
}
