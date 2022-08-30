package me.weiking1021.fcu.assembler.compiler;

import me.weiking1021.fcu.assembler.compiler.exception.AssemblerInstructionException;
import me.weiking1021.fcu.assembler.compiler.exception.AssemblerSyntaxException;

import java.util.*;

public class AssemblerCompiler {

    private enum CompilerCommand {

        START {
            @Override
            void execute(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                if (compiler.locationCounter >= 0)
                    throw new AssemblerInstructionException("Duplicate start compiler command");

                compiler.base = (short) HexFormat.fromHexDigits(instruction.operand);
                compiler.locationCounter = compiler.base;
            }
        },
        END {
            @Override
            void execute(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                compiler.startLocation = compiler.symbolMap.get(instruction.operand);
            }
        },
        BASE {
            @Override
            void execute(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                compiler.base = (short) HexFormat.fromHexDigits(instruction.operand);
            }
        },
        BYTE {
            @Override
            void execute(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                if (instruction.label != null && compiler.symbolMap.put(instruction.label, compiler.locationCounter) != null)
                    throw new AssemblerInstructionException("Duplicate instruction label '" + instruction.label);

                AssemblerOperand operand = AssemblerOperand.parse(instruction.operand);

                short length = -1;

                if (operand.value.startsWith("C'") && operand.value.endsWith("'"))
                    length

                if (instruction.operand.startsWith("C")) {

                }

                compiler.locationCounter += length;
            }
        },
        RESW {
            @Override
            void execute(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                if (instruction.label != null && compiler.symbolMap.put(instruction.label, compiler.locationCounter) != null)
                    throw new AssemblerInstructionException("Duplicate instruction label '" + instruction.label);

                short length = (short) (Short.parseShort(instruction.operand) * 3);

                compiler.locationCounter += length;
            }
        },
        RESB {
            @Override
            void execute(AssemblerCompiler compiler, AssemblerInstruction instruction) {

                if (instruction.label != null && compiler.symbolMap.put(instruction.label, compiler.locationCounter) != null)
                    throw new AssemblerInstructionException("Duplicate instruction label '" + instruction.label);

                short length = Short.parseShort(instruction.operand);

                compiler.locationCounter += length;
            }
        },
        ;

        abstract void execute(AssemblerCompiler compiler, AssemblerInstruction instruction);

        static CompilerCommand get(String command) {

            try { return valueOf(command); }
            catch (Exception e) { return null; }
        }
    }

    private List<AssemblerInstruction> loadedInstructions;
    private Map<String, Short> symbolMap;

    private short locationCounter;
    private short startLocation;
    private short base;

    public AssemblerCompiler() {

        this.locationCounter = -1;
        this.loadedInstructions = new LinkedList<>();
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

    public void compile() {

        System.out.println("---- Start compile ----");

        for (AssemblerInstruction instruction : loadedInstructions) {

            CompilerCommand compilerCommand = CompilerCommand.get(instruction.opcode);

            // check first command
            if (locationCounter < 0 && compilerCommand != CompilerCommand.START)
                throw new AssemblerInstructionException("No header instruction");

            if (compilerCommand != null) {

                compilerCommand.execute(this, instruction);
            }
            else {

                System.out.println("[" + Integer.toHexString(locationCounter) + "] " + instruction + " --> " + instruction.parseFormatType());

                // put symbol label location and check containing
                if (instruction.label != null && symbolMap.put(instruction.label, locationCounter) != null)
                    throw new AssemblerInstructionException("Duplicate instruction label '" + instruction.label + "'");

                locationCounter += instruction.parseFormatType().bytes;

//                System.out.println(locationCounter);
            }
        }
    }
}
